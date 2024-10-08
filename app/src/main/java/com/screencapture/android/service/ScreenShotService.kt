package com.screencapture.android.service

import android.app.Activity
import android.app.Service
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.util.Log
import android.view.Display
import android.view.OrientationEventListener
import android.view.WindowManager
import androidx.core.util.component1
import androidx.core.util.component2
import com.screencapture.android.receiver.NotificationReceiver
import com.screencapture.android.utils.Constants
import com.screencapture.android.utils.Constants.ACTION
import com.screencapture.android.utils.Constants.DATA
import com.screencapture.android.utils.Constants.RESULT_CODE
import com.screencapture.android.utils.Constants.SCREENCAP_NAME
import com.screencapture.android.utils.Constants.START
import com.screencapture.android.utils.Constants.START_PROJECTION
import com.screencapture.android.utils.Constants.STOP
import com.screencapture.android.utils.Constants.STOP_PROJECTION
import com.screencapture.android.utils.Constants.TIME_IN_MILLIS_FOR_SCREENSHOT_DELAY
import com.screencapture.android.utils.NotificationUtils
import com.screencapture.android.utils.getFileName
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.util.*


class ScreenShotService : Service() {

    private val TAG = "ScreenShotService"
    private var mImageReader: ImageReader? = null
    private var mHandler: Handler? = null
    private var mDisplay: Display? = null
    private var mVirtualDisplay: VirtualDisplay? = null
    private var mDensity = 0
    private var mWidth = 0
    private var mHeight = 0
    private var mRotation = 0
    private var mOrientationChangeCallback: OrientationChangeCallback? = null
    private var resultCode: Int? = null
    private var data: Intent? = null

    private var prefs: SharedPreferences? = null
    private var defaultMaxImages: Int = 150

    companion object {

        private var mMediaProjection: MediaProjection? = null

        var IMAGES_PRODUCED = 0
        var isMediaProjectionRunning = false


        //To Start Service,and show notification and initialize media projection only
        fun getStartIntent(context: Context?, resultCode: Int, data: Intent?): Intent {
            val intent = Intent(context, ScreenShotService::class.java)
            intent.putExtra(RESULT_CODE, resultCode)
            intent.putExtra(ACTION, START)
            intent.putExtra(DATA, data)
            return intent
        }


        //To Stop Service
        fun getStopIntent(context: Context?): Intent {
            val intent = Intent(context, ScreenShotService::class.java)
            intent.putExtra(ACTION, STOP)
            return intent
        }

        //To Start Media Projection
        fun getStartProjection(context: Context?): Intent {
            val intent = Intent(context, ScreenShotService::class.java)
            intent.putExtra(ACTION, START_PROJECTION)
            return intent
        }

        //To Stop Media Projection
        fun getStopProjection(context: Context?): Intent {
            val intent = Intent(context, ScreenShotService::class.java)
            intent.putExtra(ACTION, STOP_PROJECTION)
            return intent
        }

        private fun isStartProjection(intent: Intent): Boolean {
            return intent.hasExtra(ACTION) && Objects.equals(
                intent.getStringExtra(ACTION),
                START_PROJECTION
            )
        }


        private fun isStopProjection(intent: Intent): Boolean {
            return intent.hasExtra(ACTION) && Objects.equals(
                intent.getStringExtra(ACTION),
                STOP_PROJECTION
            )
        }

        private fun isStartCommand(intent: Intent): Boolean {
            return (intent.hasExtra(RESULT_CODE) && intent.hasExtra(DATA)
                    && intent.hasExtra(ACTION) && intent.getStringExtra(ACTION) == START)
        }

        private fun isStopCommand(intent: Intent): Boolean {
            return intent.hasExtra(ACTION) && Objects.equals(intent.getStringExtra(ACTION), STOP)
        }

        private fun getVirtualDisplayFlags(): Int {
            return DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY or DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC
        }

    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    inner class OrientationChangeCallback(context: Context?) : OrientationEventListener(context) {
        override fun onOrientationChanged(p0: Int) {
            val rotation = mDisplay!!.rotation
            if (rotation != mRotation) {
                mRotation = rotation
                try {
                    // clean up
                    mVirtualDisplay?.release()
                    mImageReader?.setOnImageAvailableListener(null, null)

                    // re-create virtual display depending on device width / height
                    createVirtualDisplay()
                } catch (e: Exception) {
                    Log.e(TAG, "onOrientationChanged: ", e)
                }
            }
        }

    }

    inner class ImageAvailableListener : ImageReader.OnImageAvailableListener {

        override fun onImageAvailable(reader: ImageReader?) {
            if (IMAGES_PRODUCED < defaultMaxImages) {
                Thread.sleep(TIME_IN_MILLIS_FOR_SCREENSHOT_DELAY)
                var bitmap: Bitmap? = null
                try {
                    mImageReader?.acquireLatestImage().use { image ->
                        if (image != null) {
                            val planes: Array<Image.Plane> = image.planes
                            val buffer: ByteBuffer = planes[0].buffer
                            val pixelStride: Int = planes[0].pixelStride
                            val rowStride: Int = planes[0].rowStride
                            val rowPadding = rowStride - pixelStride * mWidth

                            // create bitmap
                            bitmap = Bitmap.createBitmap(
                                mWidth + rowPadding / pixelStride,
                                mHeight,
                                Bitmap.Config.ARGB_8888
                            )
                            bitmap?.copyPixelsFromBuffer(buffer)

                            // write bitmap to a file
                            saveImageToStorage(bitmap)
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "onImageAvailable: ", e)
                } finally {
                    bitmap?.recycle()
                }
            } else {
                /** Max Images Captured */
                //Start maxLimitIntent Broadcast
                val maxLimitIntent =
                    Intent(this@ScreenShotService, NotificationReceiver::class.java)
                maxLimitIntent.action = Constants.ACTION_MAX_LIMIT
                sendBroadcast(maxLimitIntent)
            }

        }

    }

    inner class MediaProjectionStopCallback : MediaProjection.Callback() {
        override fun onStop() {
            Log.e(TAG, "stopping projection.")
            mHandler?.post {
                mVirtualDisplay?.release()
                mImageReader?.setOnImageAvailableListener(null, null)
                mOrientationChangeCallback?.disable()
                mMediaProjection?.unregisterCallback(this@MediaProjectionStopCallback)
                IMAGES_PRODUCED = 0

                mMediaProjection = null
                isMediaProjectionRunning = false

            }
        }

    }

    private fun createVirtualDisplay() {
        // get width and height
        mWidth = Resources.getSystem().displayMetrics.widthPixels
        mHeight = Resources.getSystem().displayMetrics.heightPixels

        // start capture reader
        mImageReader = ImageReader.newInstance(mWidth, mHeight, PixelFormat.RGBA_8888, 2)
        mVirtualDisplay = mMediaProjection?.createVirtualDisplay(
            SCREENCAP_NAME, mWidth, mHeight,
            mDensity, getVirtualDisplayFlags(), mImageReader?.surface, null, mHandler
        )
        mImageReader?.setOnImageAvailableListener(ImageAvailableListener(), mHandler)
    }

    override fun onCreate() {
        super.onCreate()

        prefs = getSharedPreferences("${packageName}_preferences", Context.MODE_PRIVATE)
        val maxImages = prefs?.getString(Constants.MAX_IMAGES, Constants.MAX_IMAGES_DEFAULT)
        maxImages?.let {
            defaultMaxImages = it.toInt()
        }

        // create store dir

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

        } else {

            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    .toString() + "/" + Constants.SCREENSHOTS_FOLDER_NAME
            val storeDirectory = File(imagesDir)
            if (!storeDirectory.exists()) {
                val success: Boolean = storeDirectory.mkdirs()
                if (!success) {
                    Log.e(TAG, "failed to create file storage directory.")
                    stopForeground(true)
                    stopSelf()
                }
            }

        }


        // start capture handling thread
        object : Thread() {
            override fun run() {
                Looper.prepare()
                mHandler = Handler(Looper.getMainLooper())
                Looper.loop()
            }
        }.start()
    }

    private fun startProjection(resultCode: Int, data: Intent) {
        val mpManager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        if (mMediaProjection == null) {
            this.resultCode = resultCode
            this.data = data
            mMediaProjection = mpManager.getMediaProjection(resultCode, data)
//            startMediaProjection()
        }
    }

    private fun startMediaProjection() {
        val mpManager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        if (mMediaProjection == null && resultCode != null && data != null) {
            mMediaProjection = mpManager.getMediaProjection(resultCode!!, data!!)
        }
        if (mMediaProjection != null) {
            // display metrics
            mDensity = Resources.getSystem().displayMetrics.densityDpi
            val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
            mDisplay = windowManager.defaultDisplay

            // create virtual display depending on device width / height
            createVirtualDisplay()

            // register orientation change callback
            mOrientationChangeCallback = OrientationChangeCallback(this)
            mOrientationChangeCallback?.let {
                if (it.canDetectOrientation()) {
                    it.enable()
                }
            }

            // register media projection stop callback
            mMediaProjection?.registerCallback(MediaProjectionStopCallback(), mHandler)

            isMediaProjectionRunning = true
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (isStartCommand(intent)) {
            // create notification
            val (first, second) = NotificationUtils.showNotification(this)
            startForeground(first, second)
            // start projection
            val resultCode = intent.getIntExtra(RESULT_CODE, Activity.RESULT_CANCELED)
            val data = intent.getParcelableExtra<Intent>(DATA)
            startProjection(resultCode, data!!)
        } else if (isStartProjection(intent)) {
            startMediaProjection()
        } else if (isStopProjection(intent)) {
            stopProjection()
        } else if (isStopCommand(intent)) {
            stopProjection()
            stopForeground(true)
            stopSelf()
        } else {
            stopForeground(true)
            stopSelf()
        }
        return START_REDELIVER_INTENT
    }

    private fun saveImageToStorage(bitmap: Bitmap?) {
        var imageOutStream: OutputStream? = null
        val name = getFileName()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues()
            values.put(MediaStore.Images.Media.DISPLAY_NAME, name)
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            values.put(
                MediaStore.Images.Media.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES + "/" + Constants.SCREENSHOTS_FOLDER_NAME
            )
            val uri: Uri? =
                contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            uri?.let {
                imageOutStream = contentResolver.openOutputStream(it)
            }
            Log.d(TAG, "saveImageToStorage: if $uri")
        } else {
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    .toString() + "/" + Constants.SCREENSHOTS_FOLDER_NAME

            Log.d(TAG, "saveImageToStorage: else $imagesDir")
            val image = File(imagesDir, name)
            imageOutStream = FileOutputStream(image)
        }
        try {
            val isCompressed: Boolean? = bitmap?.compress(
                Bitmap.CompressFormat.JPEG,
                100,
                imageOutStream
            )
            isCompressed?.let {
                if (it) {
                    IMAGES_PRODUCED += 1
                    updateNotificationScreenshotCount()
                    Log.d(TAG, "saveImageToStorage: $IMAGES_PRODUCED")
                }
            }
        } finally {
            imageOutStream?.close()

        }
    }

    private fun updateNotificationScreenshotCount() {
        val intent = Intent(this, NotificationReceiver::class.java)
        intent.action = Constants.ACTION_COUNT
        intent.putExtra(Constants.COUNT, IMAGES_PRODUCED)
        sendBroadcast(intent)
    }

    private fun stopProjection() {
        mHandler?.let {
            it.post {
                if (mMediaProjection != null) {
                    mMediaProjection?.stop()
                }
            }
        }
    }


}
