package com.pixlee.pixleesdk.ui.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pixlee.pixleesdk.R
import com.pixlee.pixleesdk.ui.widgets.ImageScaleType
import com.pixlee.pixleesdk.ui.widgets.list.ListHeader
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_header.*

/**
 * This is to display Header in the grid list.
 * You can set the header using either ListHeader.Gif(gif) or ListHeader.SpannableText(text)
 */
class TextHeaderViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView),
        LayoutContainer {

    fun bind(listHeader: ListHeader) {
        when(listHeader){
            is ListHeader.Gif -> {
                ivHeader.layoutParams.height = listHeader.heightInPixel

                var builder = Glide.with(ivHeader)
                        .load(listHeader.url)
                builder = when(listHeader.imageScaleType){
                    ImageScaleType.FIT_CENTER -> builder.fitCenter()
                    ImageScaleType.CENTER_CROP -> builder.centerCrop()
                }
                builder.into(ivHeader)
            }
            is ListHeader.SpannableText -> {
                tvHeader.text = listHeader.spannable
                listHeader.padding.apply {
                    vHeader.setPadding(left, top, right, bottom)
                }
            }
        }

        tvHeader.visibility = if(listHeader is ListHeader.SpannableText) VISIBLE else GONE
        ivHeader.visibility = if(listHeader is ListHeader.Gif) VISIBLE else GONE
    }

    companion object {
        fun create(parent: ViewGroup): TextHeaderViewHolder {
            return TextHeaderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_header, parent, false))
        }
    }
}