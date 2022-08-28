package com.screencapture.android.ui.settings


import android.view.View
import com.screencapture.android.R
import com.screencapture.android.databinding.ActivitySettingsBinding
import com.screencapture.android.ui.base.BaseActivity


class SettingsActivity  : BaseActivity<ActivitySettingsBinding>(R.layout.activity_settings),View.OnClickListener {

    override fun initUi() {
        super.initUi()
        setOnClickListener()
    }

    private fun setOnClickListener(){
        bindings.ivBack.setOnClickListener(this)
    }
    override fun onClick(view: View?) {
        when(view?.id){
            bindings.ivBack.id->{
                onBackPressed()
            }
        }
    }
}