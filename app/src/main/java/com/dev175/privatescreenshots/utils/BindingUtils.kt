package com.dev175.privatescreenshots.utils

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.databinding.BindingAdapter

object BindingUtils {

    @JvmStatic
    @BindingAdapter("bind:imageBitmap")
    fun loadImage(iv: ImageView, bitmap: Bitmap?) {
        iv.setImageBitmap(bitmap)
    }
}