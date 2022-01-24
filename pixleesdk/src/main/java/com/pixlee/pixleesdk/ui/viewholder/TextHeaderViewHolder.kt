package com.pixlee.pixleesdk.ui.viewholder

import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pixlee.pixleesdk.databinding.ItemHeaderBinding
import com.pixlee.pixleesdk.ui.widgets.ImageScaleType
import com.pixlee.pixleesdk.ui.widgets.list.ListHeader

/**
 * This is to display Header in the grid list.
 * You can set the header using either ListHeader.Gif(gif) or ListHeader.SpannableText(text)
 */
class TextHeaderViewHolder(val binding: ItemHeaderBinding) : RecyclerView.ViewHolder(binding.root) {

    fun setData(listHeader: ListHeader) {
        when(listHeader){
            is ListHeader.Gif -> {
                binding.ivHeader.layoutParams.height = listHeader.heightInPixel

                var builder = Glide.with(binding.ivHeader)
                        .load(listHeader.url)
                builder = when (listHeader.imageScaleType) {
                    ImageScaleType.FIT_CENTER -> builder.fitCenter()
                    ImageScaleType.CENTER_CROP -> builder.centerCrop()
                }
                binding.vHeader.setPadding(0, 0, 0, 0)
                builder.into(binding.ivHeader)
            }
            is ListHeader.SpannableText -> {
                binding.tvHeader.text = listHeader.spannable
                listHeader.padding.apply {
                    binding.vHeader.setPadding(left, top, right, bottom)
                }
            }
        }

        binding.tvHeader.visibility = if(listHeader is ListHeader.SpannableText) VISIBLE else GONE
        binding.ivHeader.visibility = if(listHeader is ListHeader.Gif) VISIBLE else GONE
    }

    companion object {
        fun create(parent: ViewGroup): TextHeaderViewHolder {
            return TextHeaderViewHolder(ItemHeaderBinding.bind(parent))
        }
    }
}