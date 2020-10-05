package com.pixlee.pixleesdk.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pixlee.pixleesdk.data.PXLPhoto
import com.pixlee.pixleesdk.ui.viewholder.PXLPhotoViewHolder
import com.pixlee.pixleesdk.ui.viewholder.PhotoWithImageScaleType
import com.pixlee.pixleesdk.ui.widgets.PXLPhotoView
import kotlinx.android.synthetic.main.item_pxlphoto.*

/**
 * This is to display PhotoWithImageScaleType having PXLPhoto in a RecyclerView.
 */
class PXLPhotoAdapter(
        var onButtonClickedListener: ((view: View, photoWithImageScaleType: PhotoWithImageScaleType) -> Unit)? = null,
        var onPhotoClickedListener: ((view: View, photoWithImageScaleType: PhotoWithImageScaleType) -> Unit)? = null,
        var photoViewConfiguration: PXLPhotoView.Configuration? = null,
        var infiniteScroll: Boolean = false,
        var showingDebugView: Boolean = false
) : RecyclerView.Adapter<PXLPhotoViewHolder>() {
    val list: ArrayList<PhotoWithImageScaleType> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PXLPhotoViewHolder {
        return PXLPhotoViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: PXLPhotoViewHolder, position: Int) {
        val item = list[getRealPosition(position)]
        holder.bind(item, photoViewConfiguration, showingDebugView)
        holder.itemView.setOnClickListener {
            onPhotoClickedListener?.also {
                it(holder.itemView, item)
            }
        }

        if (onButtonClickedListener == null) {
            holder.pxlPhotoView.setButtonClickListener(null)
        } else {
            holder.pxlPhotoView.setButtonClickListener(View.OnClickListener {
                onButtonClickedListener?.also {
                    it(holder.itemView, item)
                }
            })
        }
    }

    fun getRealPosition(position: Int): Int {
        return if (infiniteScroll) {
            position % list.size
        } else {
            position
        }
    }

    override fun getItemCount(): Int {
        return if (infiniteScroll) {
            if (list.size > 0) Integer.MAX_VALUE
            else 0
        } else {
            list.size
        }
    }
}