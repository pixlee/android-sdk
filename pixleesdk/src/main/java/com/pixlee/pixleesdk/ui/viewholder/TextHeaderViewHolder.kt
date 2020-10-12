package com.pixlee.pixleesdk.ui.viewholder

import android.text.Spannable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pixlee.pixleesdk.R
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_text.*

/**
 * This is to display Header Title in the grid list.
 */
class TextHeaderViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView),
        LayoutContainer {

    fun bind(span: Spannable) {
        tvTitle.text = span
    }

    companion object {
        fun create(parent: ViewGroup): PXLPhotoViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_text, parent, false)
            val holder = PXLPhotoViewHolder(view)
            view.setTag(holder)
            return holder
        }
    }
}