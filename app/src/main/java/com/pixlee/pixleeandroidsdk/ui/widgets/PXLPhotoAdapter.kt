package com.pixlee.pixleeandroidsdk.ui.widgets

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pixlee.pixleesdk.PXLPhoto

/**
 * Created by sungjun on 9/11/20.
 */
class PXLPhotoAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val list: ArrayList <Any> = ArrayList()
    private val TYPE_PXLPHOTO = 0
    private val TYPE_TEXT = 1

    override fun getItemViewType(position: Int): Int {
        return when(list[position]){
            is PhotoWithImageScaleType -> TYPE_PXLPHOTO
            else -> TYPE_TEXT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_PXLPHOTO -> PXLPhotoViewHolder.create(parent)
            else -> TextViewHolder.create(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        when(holder){
            is PXLPhotoViewHolder -> holder.bind(item as PhotoWithImageScaleType)
            is TextViewHolder -> holder.bind(item as String)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}