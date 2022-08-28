package com.screencapture.android.ui.screenshots

import com.screencapture.android.R
import com.screencapture.android.databinding.ItemImageBinding
import com.screencapture.android.model.Screenshot
import com.screencapture.android.ui.base.BaseRecyclerAdapter
import javax.inject.Inject


class ScreenshotsAdapter @Inject constructor(): BaseRecyclerAdapter<Screenshot, ItemImageBinding>() {

    override val layout: Int  = R.layout.item_image

    override fun onBindViewHolder(
        holder: Companion.BaseViewHolder<ItemImageBinding>,
        position: Int
    ) {
        holder.binding.data = items[position]
        holder.binding.executePendingBindings()

        holder.binding.container.setOnClickListener {
            listener?.invoke(holder.binding.root, items[position], position)
        }

        holder.binding.container.setOnLongClickListener{
            return@setOnLongClickListener true
        }
    }

}