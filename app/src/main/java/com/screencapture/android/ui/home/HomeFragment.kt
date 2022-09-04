package com.screencapture.android.ui.home


import android.app.Activity
import android.content.Context.MEDIA_PROJECTION_SERVICE
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.screencapture.android.R
import com.screencapture.android.databinding.FragmentHomeBinding
import com.screencapture.android.service.ScreenShotService
import com.screencapture.android.ui.base.BaseFragment
import com.screencapture.android.ui.navigation.HomeActivity
import com.screencapture.android.utils.showShortToast


class HomeFragment  : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {


    override fun initUi() {
        setUpAdView()
        bindings.menu.setOnClickListener {
            (activity as HomeActivity).openAndCloseDrawer()
        }
        bindings.createBtn.setOnClickListener {
            checkForPermissions()
        }
    /*    bindings.stopBtn.setOnClickListener {
            stopProjection()
        }*/
    }

    private fun setUpAdView() {
        val adRequest = AdRequest.Builder().build()
        bindings.adView.loadAd(adRequest)
        bindings.adView.adListener = object: AdListener() {
            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }

            override fun onAdFailedToLoad(adError : LoadAdError) {
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


    private var requestPermissions = registerForActivityResult(ActivityResultContracts.RequestPermission()){isGranted->
        isGranted?.let {
            if (it){
                startProjection()
            }
            else{
                context?.showShortToast("Permission is required")
            }
        }

    }
    private fun checkForPermissions() {

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            requestPermissions.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    private fun stopProjection() {
        context?.startService(ScreenShotService.getStopIntent(context))
    }

    private var resultLauncherForProjection = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            context?.startService(ScreenShotService.getStartIntent(requireContext(),result.resultCode,data))
        }
    }

    private fun startProjection() {
        val mProjectionManager = context?.getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        resultLauncherForProjection.launch(mProjectionManager.createScreenCaptureIntent())
    }
}