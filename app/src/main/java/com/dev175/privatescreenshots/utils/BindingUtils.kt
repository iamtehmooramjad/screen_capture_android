package com.dev175.privatescreenshots.utils

import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide


object BindingUtils {

    @JvmStatic
    @BindingAdapter("bind:imageBitmap")
    fun loadImage(iv: ImageView, bitmap: Bitmap?) {
        Glide.with(iv.context).load(bitmap).into(iv)
    }

    @JvmStatic
    @BindingAdapter("bind:imageUri")
    fun loadImage(iv: ImageView, uri: Uri?) {
        Glide.with(iv.context).load(uri).into(iv)
    }
}