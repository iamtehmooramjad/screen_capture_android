package com.dev175.privatescreenshots.ui.settings


import android.view.View
import com.dev175.privatescreenshots.R
import com.dev175.privatescreenshots.databinding.ActivitySettingsBinding
import com.dev175.privatescreenshots.ui.base.BaseActivity


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