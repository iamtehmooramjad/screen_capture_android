package com.screencapture.android.ui.privacypolicy

import android.os.Bundle
import android.view.View
import com.screencapture.android.R
import com.screencapture.android.databinding.ActivityPrivacyPolicyBinding
import com.screencapture.android.ui.base.BaseActivity

class PrivacyPolicyActivity :
    BaseActivity<ActivityPrivacyPolicyBinding>(R.layout.activity_privacy_policy),
    View.OnClickListener {

    override fun initUi(savedInstanceState: Bundle?) {
        super.initUi(savedInstanceState)
        setOnClickListener()
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
}