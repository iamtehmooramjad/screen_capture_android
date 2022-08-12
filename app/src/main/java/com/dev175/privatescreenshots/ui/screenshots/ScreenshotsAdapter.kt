package com.dev175.privatescreenshots.ui.screenshots

import com.dev175.privatescreenshots.R
import com.dev175.privatescreenshots.databinding.ItemImageBinding
import com.dev175.privatescreenshots.model.Screenshot
import com.dev175.privatescreenshots.ui.base.BaseRecyclerAdapter
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