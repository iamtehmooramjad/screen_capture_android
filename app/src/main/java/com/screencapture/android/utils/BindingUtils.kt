package com.screencapture.android.utils

import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy


object BindingUtils {


    @JvmStatic
    @BindingAdapter("bind:imageUri")
    fun loadImage(iv: ImageView, uri: Uri?) {
        Glide
            .with(iv.context)
            .load(uri)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(iv)
    }
}