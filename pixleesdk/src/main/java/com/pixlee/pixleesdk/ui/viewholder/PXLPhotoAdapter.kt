package com.pixlee.pixleesdk.ui.viewholder

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by sungjun on 9/11/20.
 */
class PXLPhotoAdapter : RecyclerView.Adapter<PXLPhotoViewHolder>() {
    val list: ArrayList <PhotoWithImageScaleType> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PXLPhotoViewHolder {
        return PXLPhotoViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: PXLPhotoViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}