package com.dev175.privatescreenshots.ui.screenshots

import com.dev175.privatescreenshots.R
import com.dev175.privatescreenshots.databinding.FragmentScreenshotsBinding
import com.dev175.privatescreenshots.model.Screenshot
import com.dev175.privatescreenshots.ui.base.BaseFragment
import com.dev175.privatescreenshots.utils.ImageUtils.getAllImages
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ScreenshotsFragment  : BaseFragment<FragmentScreenshotsBinding>(R.layout.fragment_screenshots) {

    @Inject
    lateinit var adapter: ScreenshotsAdapter

    override fun initUi() {
        super.initUi()

        CoroutineScope(Dispatchers.IO).launch {
            val images = getAllImages(requireContext())

            CoroutineScope(Dispatchers.Main).launch {
                setRecyclerView(images)
            }
        }
    }

    private fun setRecyclerView(images: List<Screenshot>) {
        adapter.items = images.toMutableList()

        bindings.rvGallery.adapter = adapter
    }
}