package com.dev175.privatescreenshots.ui.screenshots

import android.content.Intent
import com.dev175.privatescreenshots.R
import com.dev175.privatescreenshots.databinding.ActivityScreenshotsBinding
import com.dev175.privatescreenshots.model.Screenshot
import com.dev175.privatescreenshots.ui.base.BaseActivity
import com.dev175.privatescreenshots.ui.image.ImageActivity
import com.dev175.privatescreenshots.utils.Constants
import com.dev175.privatescreenshots.utils.ImageUtils.getAllImages
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ScreenshotsActivity  : BaseActivity<ActivityScreenshotsBinding>(R.layout.activity_screenshots) {

    @Inject
    lateinit var adapter: ScreenshotsAdapter

    override fun initUi() {
        super.initUi()

        bindings.ivBack.setOnClickListener {
            onBackPressed()
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

    private fun setRecyclerView(images: List<Screenshot>) {
        adapter.items = images.toMutableList()

        bindings.rvGallery.adapter = adapter

        adapter.listener = {_, item, _ ->
            val intent = Intent(this,ImageActivity::class.java)
            intent.putExtra(Constants.IMAGE,item)
            startActivity(intent)
        }
    }
}