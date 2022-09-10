package com.screencapture.android.ui.screenshots

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.screencapture.android.R
import com.screencapture.android.databinding.ActivityScreenshotsBinding
import com.screencapture.android.model.Screenshot
import com.screencapture.android.ui.base.BaseActivity
import com.screencapture.android.ui.image.ImageActivity
import com.screencapture.android.utils.Constants
import com.screencapture.android.utils.ImageUtils.getAllImages
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ScreenshotsActivity  : BaseActivity<ActivityScreenshotsBinding>(R.layout.activity_screenshots) {

    @Inject
    lateinit var adapter: ScreenshotsAdapter

    private var mInterstitialAd: InterstitialAd? = null

    private val TAG = "ScreenshotsActivity"

    override fun initUi(savedInstanceState: Bundle?) {
        super.initUi(savedInstanceState)
        setUpAdView()
        setUpInterstitialAd()
        bindings.ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setUpInterstitialAd() {
        var adRequest = AdRequest.Builder().build()

        InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d("TAG", adError.toString())
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d("TAG", "Ad was loaded.")
                mInterstitialAd = interstitialAd
                mInterstitialAd?.let {
                    mInterstitialAd?.show(this@ScreenshotsActivity)
                }

            }
        })


        mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
            override fun onAdClicked() {
                // Called when a click is recorded for an ad.
                Log.d(TAG, "Ad was clicked.")
            }

            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                Log.d(TAG, "Ad dismissed fullscreen content.")
                mInterstitialAd = null
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                // Called when ad fails to show.
                Log.e(TAG, "Ad failed to show fullscreen content.")
                mInterstitialAd = null
            }

            override fun onAdImpression() {
                // Called when an impression is recorded for an ad.
                Log.d(TAG, "Ad recorded an impression.")
            }

            override fun onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.d(TAG, "Ad showed fullscreen content.")
            }
        }


    }

    override fun onResume() {
        super.onResume()
        CoroutineScope(Dispatchers.IO).launch {
            val images = getAllImages(context)

            CoroutineScope(Dispatchers.Main).launch {
                setRecyclerView(images)
            }
        }
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

    private fun setRecyclerView(images: List<Screenshot>) {
        if(images.isEmpty()){
            bindings.noScreenshotsTv.visibility = View.VISIBLE
        }
        else{
            bindings.noScreenshotsTv.visibility = View.GONE
        }
        Log.d(TAG, "setRecyclerView: $images")
        Log.d(TAG, "setRecyclerView: ${images.size}")
        adapter.items = images.toMutableList()

        bindings.rvGallery.adapter = adapter

        adapter.listener = {_, item, _ ->
            val intent = Intent(this,ImageActivity::class.java)
            intent.putExtra(Constants.IMAGE,item)
            startActivity(intent)
        }
    }
}