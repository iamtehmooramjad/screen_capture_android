package com.dev175.privatescreenshots.ui.home

import android.app.Activity
import android.content.Context.MEDIA_PROJECTION_SERVICE
import android.content.Intent
import android.media.projection.MediaProjectionManager
import androidx.activity.result.contract.ActivityResultContracts
import com.dev175.privatescreenshots.R
import com.dev175.privatescreenshots.databinding.FragmentHomeBinding
import com.dev175.privatescreenshots.service.ScreenShotService
import com.dev175.privatescreenshots.ui.base.BaseFragment
import com.dev175.privatescreenshots.ui.navigation.HomeActivity


class HomeFragment  : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {

    override fun initUi() {

        bindings.menu.setOnClickListener {
            (activity as HomeActivity).openAndCloseDrawer()
        }
        bindings.createBtn.setOnClickListener {
            startProjection()
        }
        bindings.stopBtn.setOnClickListener {
            stopProjection()
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