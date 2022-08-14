package com.dev175.privatescreenshots.utils

import android.content.Context
import android.transition.Fade
import android.transition.Transition
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

fun View.fadeVisibility(visibility: Int, duration: Long = 1000) {
    val transition: Transition = Fade()
    transition.duration = duration
    transition.addTarget(this)
    TransitionManager.beginDelayedTransition(this.parent as ViewGroup, transition)
    this.visibility = visibility
}

fun getFileName(): String {
    val timeStamp: String = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(Date())
    return "Screenshot_" + timeStamp + "_.jpg"
}

fun Context.showShortToast(message:String){
    Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
}