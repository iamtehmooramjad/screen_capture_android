package com.screencapture.android

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.analytics.FirebaseAnalytics
import com.screencapture.android.utils.Constants
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ScreenshotApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        setUpPreferences()
        initializeAds()
        createNotificationChannel()


        if (isGooglePlayServicesAvailable()) {
            FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(true)
        } else {
            //show user proper error
        }
    }

    private fun setUpPreferences() {
        val prefs: SharedPreferences = getSharedPreferences("${packageName}_preferences", Context.MODE_PRIVATE)
        val defaultMaxImages: String? = prefs.getString(Constants.MAX_IMAGES,"")
        if (defaultMaxImages.isNullOrEmpty()) {
            val editor: SharedPreferences.Editor = prefs.edit()
            editor.putString(Constants.MAX_IMAGES, Constants.MAX_IMAGES_DEFAULT)
            editor.apply()
        }

    }

    private fun initializeAds() {
        MobileAds.initialize(this) {}
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Private Screenshots",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(channel)
        }
    }

    private fun isGooglePlayServicesAvailable(): Boolean {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val status = googleApiAvailability.isGooglePlayServicesAvailable(this)
        if (status != ConnectionResult.SUCCESS) {
            return false
        }
        return true
    }

    companion object {
        const val CHANNEL_ID = "PrivateScreenshots"
    }
}