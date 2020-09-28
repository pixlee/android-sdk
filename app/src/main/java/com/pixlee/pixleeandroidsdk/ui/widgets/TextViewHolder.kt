package com.pixlee.pixleeandroidsdk.ui.widgets

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pixlee.pixleeandroidsdk.R
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_text.*

/**
 * Created by sungjun on 9/14/20.
 */
class TextViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView),
        LayoutContainer {

    fun bind(text: String) {
        tv.text = text
    }

    companion object {
        fun create(parent: ViewGroup): TextViewHolder {
            return TextViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_text, parent, false))
        }
    }
}