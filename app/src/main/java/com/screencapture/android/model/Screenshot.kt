package com.screencapture.android.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Screenshot(
    val id : Long?=null,
    val name : String = "",
    val size : String = "",
    val date : String = "",
    val uri : Uri ?= null,
//    val thumbnail : Bitmap,
    var checked:Boolean = false
):Parcelable
