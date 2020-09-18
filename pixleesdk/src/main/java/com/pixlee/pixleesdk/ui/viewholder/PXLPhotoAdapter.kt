package com.pixlee.pixleesdk.ui.viewholder

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pixlee.pixleesdk.data.PXLPhoto

/**
 * Created by sungjun on 9/11/20.
 */
class PXLPhotoAdapter(val listener: (pxlPhoto: PXLPhoto) -> Unit) : RecyclerView.Adapter<PXLPhotoViewHolder>() {
    val list: ArrayList<PhotoWithImageScaleType> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PXLPhotoViewHolder {
        return PXLPhotoViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: PXLPhotoViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            listener(item.pxlPhoto)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}