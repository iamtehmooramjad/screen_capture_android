package com.screencapture.android.utils

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.net.toUri
import com.screencapture.android.model.Screenshot
import java.io.File


object ImageUtils {

    private fun checkImageUri(cr: ContentResolver, image: Uri): Boolean {
        val res: Boolean

        val projection = arrayOf(MediaStore.MediaColumns.DATA)
        val cur: Cursor? = cr.query(image, projection, null, null, null)
        if (cur != null) {
            res = if (cur.moveToFirst()) {
                val filePath: String = cur.getString(0)
                // true= if it exists
                // false= File was not found
                File(filePath).exists()
            } else {
                // Uri was ok but no entry found.
                false
            }
            cur.close()
        } else {
            // content Uri was invalid or some other error occurred
            res = false
        }
        return res
    }

    fun getAllImages(context: Context): List<Screenshot> {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            val imageProjection = arrayOf(
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
            )

            val imageSortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"


            val cursor = context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                imageProjection,
                MediaStore.Images.Media.DATA + " like ? ",
                arrayOf("%/${Constants.SCREENSHOTS_FOLDER_NAME}/%"),
                imageSortOrder
            )

            cursor.use {
                it?.let {
                    val screenshots = mutableListOf<Screenshot>()

                    val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                    val nameColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                    val sizeColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
                    val dateColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
                    val dataColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

                    while (it.moveToNext()) {
                        val id: Long = it.getLong(idColumn)
                        val name: String = it.getString(nameColumn)
                        val size: String = it.getString(sizeColumn)
                        val date: String? = it.getString(dateColumn)
                        val data: String = it.getString(dataColumn) ?: ""
                        Log.d("ImageUtils", "getAllImages: $it")

                        val contentUri: Uri = ContentUris.withAppendedId(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            id
                        )


                        // add the URI to the list
                        // generate the thumbnail
//                    val thumbnail: Bitmap = context.contentResolver.loadThumbnail(contentUri, Size(480, 480), null)


                        val file = File(data)
                        if (file.exists()) {
                            val screenshot = Screenshot(id, name, size, date ?: "", contentUri)
                            screenshots.add(screenshot)
                        }


                    }
                    return screenshots
                } ?: kotlin.run {
                    Log.e("TAG", "Cursor is null!")
                }
            }
        } else {
            val folder = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    .toString() + "/${Constants.SCREENSHOTS_FOLDER_NAME}/"
            )
            if (folder.exists()) {
                val files: Array<out File>? = folder.listFiles()
                val screenshots = mutableListOf<Screenshot>()
                files?.forEach {
                    val screenshot = Screenshot(uri = it.toUri(), name = it.name)
                    screenshots.add(screenshot)
                }
                return screenshots
            }
        }

        return emptyList<Screenshot>()
    }

}