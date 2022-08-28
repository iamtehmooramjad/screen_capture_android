package com.screencapture.android.utils

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.net.toUri
import com.screencapture.android.model.Screenshot
import java.io.File


object ImageUtils {

     fun getAllImages(context: Context): List<Screenshot> {

         if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q) {

             val imageProjection = arrayOf(
                 MediaStore.Images.Media.DISPLAY_NAME,
                 MediaStore.Images.Media.SIZE,
                 MediaStore.Images.Media.DATE_TAKEN,
                 MediaStore.Images.Media._ID
             )

             val imageSortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"

/*        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            imageProjection,
            null,
            null,
            imageSortOrder
        )*/

             val cursor = context.contentResolver.query( MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                 imageProjection,
                 MediaStore.Images.Media.DATA + " like ? ",
                 arrayOf("%/${Constants.SCREENSHOTS_FOLDER_NAME}/%"),
                 imageSortOrder);

             cursor.use {
                 it?.let {
                     val screenshots = mutableListOf<Screenshot>()

                     val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                     val nameColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                     val sizeColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
                     val dateColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)

                     while (it.moveToNext()) {
                         val id: Long = it.getLong(idColumn)
                         val name: String = it.getString(nameColumn)
                         val size: String = it.getString(sizeColumn)
                         val date: String? = it.getString(dateColumn)

                         val contentUri: Uri = ContentUris.withAppendedId(
                             MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                             id
                         )
                         // add the URI to the list
                         // generate the thumbnail
//                    val thumbnail: Bitmap = context.contentResolver.loadThumbnail(contentUri, Size(480, 480), null)

                         val screenshot = Screenshot(id,name,size,date?:"",contentUri)
                         screenshots.add(screenshot)
                     }
                     return screenshots
                 } ?: kotlin.run {
                     Log.e("TAG", "Cursor is null!")
                 }
             }
         }
         else{
             val folder = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/${Constants.SCREENSHOTS_FOLDER_NAME}/")
             if (folder.exists()){
                 val files: Array<out File>? =folder.listFiles()
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