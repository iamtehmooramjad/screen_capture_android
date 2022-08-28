package com.screencapture.android.ui.image

import android.content.Intent
import android.view.View
import com.bumptech.glide.Glide
import com.screencapture.android.R
import com.screencapture.android.databinding.ActivityImageBinding
import com.screencapture.android.model.Screenshot
import com.screencapture.android.ui.base.BaseActivity
import com.screencapture.android.utils.Constants


class ImageActivity : BaseActivity<ActivityImageBinding>(R.layout.activity_image), View.OnClickListener {

    lateinit var screenshot : Screenshot

    override fun initUi() {
        super.initUi()
        getImage()
        setOnClickListeners()
    }

    private fun getImage() {
        intent?.extras?.let {
            screenshot = it.get(Constants.IMAGE) as Screenshot
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
        when(view?.id){
            bindings.shareBtn.id->{
                if(this::screenshot.isInitialized){
                    shareImage()
                }
            }
            bindings.deleteBtn.id->{
                if(this::screenshot.isInitialized){
                    deleteImage()
                }
            }
        }
    }

    private fun deleteImage() {
        val isDeleted = contentResolver.delete(screenshot.uri,null,null)
        if (isDeleted==1){
            finish()
        }
    }

    private fun shareImage() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_STREAM,screenshot.uri)
        startActivity(Intent.createChooser(intent, "Share Image"))
    }


}