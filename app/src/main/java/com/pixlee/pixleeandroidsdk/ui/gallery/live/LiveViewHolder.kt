package com.pixlee.pixleeandroidsdk.ui.gallery.live

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pixlee.pixleeandroidsdk.R
import com.pixlee.pixleesdk.data.PXLPhoto
import com.pixlee.pixleesdk.enums.PXLPhotoSize
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_live.*

class LiveViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView),
        LayoutContainer {

    fun bind(pxlPhoto: PXLPhoto) {
        var builder = Glide.with(iv)
                .load(pxlPhoto.getUrlForSize(PXLPhotoSize.THUMBNAIL))
                .centerCrop()
                .into(iv)
    }

    companion object {
        fun create(parent: ViewGroup): LiveViewHolder {
            return LiveViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_live, parent, false))
        }
    }
}