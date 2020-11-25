package com.pixlee.pixleeandroidsdk.ui.gallery.live

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pixlee.pixleesdk.data.PXLPhoto

/**
 * This is to be used in PXLPhotoRecyclerView which extends RecyclerView
 */
class LiveAdapter(
        val list: List<PXLPhoto>,
        val onItemClicked: (pxlPhoto: PXLPhoto) -> Unit) : RecyclerView.Adapter<LiveViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LiveViewHolder {
        return LiveViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: LiveViewHolder, position: Int) {
        holder.bind(list[position])
        holder.itemView.setOnClickListener {
            onItemClicked(list[holder.adapterPosition])
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}