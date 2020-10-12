package com.pixlee.pixleesdk.ui.viewholder

import android.text.Spannable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pixlee.pixleesdk.R
import com.pixlee.pixleesdk.ui.widgets.TextPadding
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_header.tvHeader
/**
 * This is to display Header Title in the grid list.
 */
class TextHeaderViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView),
        LayoutContainer {

    fun bind(span: Spannable, padding: TextPadding) {
        tvHeader.text = span
        tvHeader.setPadding(padding.left, padding.top, padding.right, padding.bottom)
    }

    companion object {
        fun create(parent: ViewGroup): TextHeaderViewHolder {
            return TextHeaderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_header, parent, false))
        }
    }
}