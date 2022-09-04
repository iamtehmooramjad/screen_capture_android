package com.screencapture.android.ui.settings


import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.screencapture.android.R
import com.screencapture.android.databinding.ActivitySettingsBinding
import com.screencapture.android.ui.base.BaseActivity


class SettingsActivity : BaseActivity<ActivitySettingsBinding>(R.layout.activity_settings),
    View.OnClickListener {

    override fun initUi(savedInstanceState: Bundle?) {
        super.initUi(savedInstanceState)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.content_preference, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setUpAdView()
        setOnClickListener()
    }

    private fun setUpAdView() {
        val adRequest = AdRequest.Builder().build()
        bindings.adView.loadAd(adRequest)
        bindings.adView.adListener = object : AdListener() {
            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                // Code to be executed when an ad request fails.
                bindings.adView.loadAd(adRequest)
            }

            override fun onAdImpression() {
                // Code to be executed when an impression is recorded
                // for an ad.
            }

            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }
        }
    }


    private fun setOnClickListener() {
        bindings.ivBack.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            bindings.ivBack.id -> {
                onBackPressed()
            }
        }
    }


    class SettingsFragment : PreferenceFragmentCompat(),
        SharedPreferences.OnSharedPreferenceChangeListener {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.settings, rootKey)
        }


        override fun onResume() {
            super.onResume()
            preferenceManager.sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
        }

        override fun onPause() {
            preferenceManager.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
            super.onPause()
        }

        override fun onSharedPreferenceChanged(
            sharedPreferences: SharedPreferences?,
            key: String?
        ) {
            Log.d("TAG", "${sharedPreferences?.getString(key, "none")}")
        }


    }
}