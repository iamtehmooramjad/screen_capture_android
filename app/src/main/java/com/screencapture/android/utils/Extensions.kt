package com.screencapture.android.utils

import android.content.Context
import android.transition.Fade
import android.transition.Transition
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.DialogFragmentNavigator
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController

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


fun Fragment.safeNavigate(directions: NavDirections, bundle: Bundle? = null) {
    val navController = findNavController()
    when (val destination = navController.currentDestination) {
        is FragmentNavigator.Destination -> {
            if (javaClass.name == destination.className) {
                navController.navigate(directions.actionId, bundle)
            }
        }
        is DialogFragmentNavigator.Destination -> {
            if (javaClass.name == destination.className) {
                navController.navigate(directions.actionId, bundle)
            }
        }
    }
}