package com.screencapture.android.ui.image

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.screencapture.android.BuildConfig
import com.screencapture.android.R
import com.screencapture.android.databinding.ActivityImageBinding
import com.screencapture.android.model.Screenshot
import com.screencapture.android.ui.base.BaseActivity
import com.screencapture.android.utils.Constants
import java.io.File


class ImageActivity : BaseActivity<ActivityImageBinding>(R.layout.activity_image),
    View.OnClickListener {

    lateinit var screenshot: Screenshot

    override fun initUi(savedInstanceState: Bundle?) {
        super.initUi(savedInstanceState)
        setUpAdView()
        getImage()
        setOnClickListeners()
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

    private fun getImage() {
        intent?.extras?.let {
            screenshot = it.get(Constants.IMAGE) as Screenshot
            bindings.image.setImageURI(screenshot.uri)
            Glide.with(this)
                .load(screenshot.uri)
                .into(bindings.image)
        }
    }

    private fun setOnClickListeners() {
        bindings.shareBtn.setOnClickListener(this)
        bindings.deleteBtn.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            bindings.shareBtn.id -> {
                if (this::screenshot.isInitialized) {
                    shareImage()
                }
            }
            bindings.deleteBtn.id -> {
                if (this::screenshot.isInitialized) {
                    deleteImage()
                }
            }
        }
    }

    private fun deleteImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val isDeleted = contentResolver.delete(screenshot.uri!!, null, null)
            if (isDeleted == 1) {
                finish()
            }
        } else {
            val isDeleted = screenshot.uri?.toFile()?.delete()
            isDeleted?.let {
                if (it) {
                    finish()
                }
            }
        }

    }

    private fun shareImage() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        intent.type = "image/*"

        var uri = screenshot.uri
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            uri?.path?.let {
                val file = File(it)
                uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID, file)
            }
        } else {
            uri = screenshot.uri
        }
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(Intent.createChooser(intent, "Share Image"))
    }


}