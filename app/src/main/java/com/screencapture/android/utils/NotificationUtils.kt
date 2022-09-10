package com.screencapture.android.utils

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.util.Pair
import com.screencapture.android.R
import com.screencapture.android.ScreenshotApplication.Companion.CHANNEL_ID
import com.screencapture.android.receiver.NotificationReceiver
import com.screencapture.android.ui.screenshots.ScreenshotsActivity
import com.screencapture.android.utils.Constants.ACTION_GALLERY
import com.screencapture.android.utils.Constants.ACTION_START_STOP
import com.screencapture.android.utils.Constants.ACTION_STOP


object NotificationUtils {
    private val NOTIFICATION_ID = 103
    private var notification: Notification ?= null
    private var remoteViews: RemoteViews ?= null

    fun showNotification(context: Context) : Pair<Int, Notification>{
        val  notificationManager: NotificationManagerCompat = NotificationManagerCompat.from(context)

        remoteViews = RemoteViews(
            context.packageName,
            R.layout.notification_record
        )

        //Start Stop Intent
        val startStopIntent = Intent(context, NotificationReceiver::class.java)
        startStopIntent.action = ACTION_START_STOP
        val pendingStartStopIntent = PendingIntent.getBroadcast(context, 0,startStopIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        remoteViews?.setOnClickPendingIntent(R.id.start_stop_iv, pendingStartStopIntent)

        //Stop Service
        val stopIntent = Intent(context,NotificationReceiver::class.java)
        stopIntent.action = ACTION_STOP
        val pendingStopIntent = PendingIntent.getBroadcast(context, 0,stopIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        remoteViews?.setOnClickPendingIntent(R.id.close_iv, pendingStopIntent)

        //Open Screenshots
        val galleryIntent = Intent(context, ScreenshotsActivity::class.java)
        galleryIntent.action = ACTION_GALLERY
        val pendingGalleryIntent = PendingIntent.getActivity(context, 0,galleryIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        remoteViews?.setOnClickPendingIntent(R.id.gallery_iv, pendingGalleryIntent)

        notification= NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setCustomContentView(remoteViews)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .build()
        notification?.let {
            notificationManager.notify(NOTIFICATION_ID, it)
        }
        return Pair(NOTIFICATION_ID, notification)
    }


    // use this method to update the Notification's Start Stop Icon
     fun updateNotification(context:Context?,drawable:Int) {
        val  notificationManager = NotificationManagerCompat.from(context!!)

        // update the icon
        remoteViews?.setImageViewResource(R.id.start_stop_iv, drawable)
/*        // update the title
        customView.setTextViewText(R.id.notif_title, getResources().getString(R.string.new_title))
        // update the content
        customView.setTextViewText(
            R.id.notif_content,
            getResources().getString(R.string.new_content_text)
        )*/

        notification?.let {
            notificationManager.notify(NOTIFICATION_ID, it)
        }

    }

    // use this method to update the Notification's Start Stop Icon
    fun updateNotificationCount(context:Context?,count:Int) {
        context?.let {
            val  notificationManager = NotificationManagerCompat.from(it)
            remoteViews?.setTextViewText(R.id.count, "$count ${it.resources.getString(R.string.screen_capture)}")

            notification?.let {n->
                notificationManager.notify(NOTIFICATION_ID, n)
            }
        }


    }



}
