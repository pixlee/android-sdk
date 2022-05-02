package com.pixlee.pixleesdk.ui.viewholder

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pixlee.pixleesdk.databinding.ItemLoadMoreBinding
import com.pixlee.pixleesdk.ui.adapter.PXLPhotoAdapter
import com.pixlee.pixleesdk.ui.widgets.setTextViewStyle

/**
 * This is to display Header in the grid list.
 * You can set the header using either ListHeader.Gif(gif) or ListHeader.SpannableText(text)
 */
class LoadMoreViewHolder(val binding: ItemLoadMoreBinding) :
        RecyclerView.ViewHolder(binding.root) {

    fun setData(loadMore: PXLPhotoAdapter.Item.LoadMore) {
        when (loadMore.itemType) {
            is PXLPhotoAdapter.ItemType.Horizontal -> {
                binding.root.layoutParams.width = loadMore.width ?: ViewGroup.LayoutParams.WRAP_CONTENT
                binding.root.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            }
            else -> {
                binding.root.layoutParams.width = loadMore.width ?: ViewGroup.LayoutParams.MATCH_PARENT
                binding.root.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            }
        }

        binding.tvLoadMore.setTextViewStyle(loadMore.loadMoreTextViewStyle)
        when(loadMore.loading){
            true -> {
                binding.tvLoadMore.visibility = View.INVISIBLE
                binding.tvLoadMore.isEnabled = false
                binding.lottieView.visibility = View.VISIBLE
            }
            false -> {
                binding.tvLoadMore.visibility = View.VISIBLE
                binding.tvLoadMore.isEnabled = true
                binding.lottieView.visibility = View.INVISIBLE
            }
        }
    }

    companion object {
        fun create(parent: ViewGroup): LoadMoreViewHolder {
            return LoadMoreViewHolder(ItemLoadMoreBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }
}