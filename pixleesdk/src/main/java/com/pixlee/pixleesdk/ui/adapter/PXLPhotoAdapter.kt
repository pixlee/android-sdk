package com.pixlee.pixleesdk.ui.adapter

import android.text.Spannable
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pixlee.pixleesdk.ui.viewholder.PXLPhotoViewHolder
import com.pixlee.pixleesdk.ui.viewholder.PhotoWithImageScaleType
import com.pixlee.pixleesdk.ui.viewholder.TextHeaderViewHolder
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
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    sealed class Item {
        class Header(val text: Spannable): Item()
        class Content(val data: PhotoWithImageScaleType): Item()
    }

    val list: ArrayList<Item> = ArrayList()

    val TYPE_HEADER = 1
    val TYPE_ITEM = 2
    override fun getItemViewType(position: Int): Int {
        return when(list[getRealPosition(position)]){
            is Item.Header -> TYPE_HEADER
            is Item.Content -> TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            TYPE_HEADER -> TextHeaderViewHolder.create(parent)
            else -> PXLPhotoViewHolder.create(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[getRealPosition(position)]
        when(item){
            is Item.Header -> {
                val vh = (holder as TextHeaderViewHolder)
                vh.bind(item.text)
                vh.itemView.setOnClickListener(null)
            }
            is Item.Content -> {
                val vh = (holder as PXLPhotoViewHolder)
                vh.bind(item.data, photoViewConfiguration, showingDebugView)
                vh.itemView.setOnClickListener {
                    onPhotoClickedListener?.also {
                        it(vh.itemView, item.data)
                    }
                }

                if (onButtonClickedListener == null) {
                    vh.pxlPhotoView.setButtonClickListener(null)
                } else {
                    vh.pxlPhotoView.setButtonClickListener(View.OnClickListener {
                        onButtonClickedListener?.also {
                            it(vh.itemView, item.data)
                        }
                    })
                }
            }
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