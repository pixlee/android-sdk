package com.pixlee.pixleesdk.ui.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pixlee.pixleesdk.R
import com.pixlee.pixleesdk.ui.adapter.PXLPhotoAdapter
import com.pixlee.pixleesdk.ui.widgets.setTextViewStyle
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_load_more.*

/**
 * This is to display Header in the grid list.
 * You can set the header using either ListHeader.Gif(gif) or ListHeader.SpannableText(text)
 */
class LoadMoreViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView),
        LayoutContainer {

    fun bind(loadMore: PXLPhotoAdapter.Item.LoadMore) {
        tvLoadMore.setTextViewStyle(loadMore.loadMoreTextViewStyle)
        when(loadMore.loading){
            true -> {
                tvLoadMore.visibility = View.INVISIBLE
                tvLoadMore.isEnabled = false
                lottieView.visibility = View.VISIBLE
            }
            false -> {
                tvLoadMore.visibility = View.VISIBLE
                tvLoadMore.isEnabled = true
                lottieView.visibility = View.GONE
            }
        }
    }

    companion object {
        fun create(parent: ViewGroup): LoadMoreViewHolder {
            return LoadMoreViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_load_more, parent, false))
        }
    }
}