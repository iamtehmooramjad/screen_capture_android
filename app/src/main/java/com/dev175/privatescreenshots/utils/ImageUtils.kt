package com.dev175.privatescreenshots.utils

import android.os.Build
import android.os.Environment

object ImageUtils {

    fun getImagesFromFolder(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

        } else {
            val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                .toString()+"/"+Constants.SCREENSHOTS_FOLDER_NAME

        }
    }


}