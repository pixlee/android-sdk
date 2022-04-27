package com.pixlee.pixleesdk.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.pixlee.pixleesdk.ui.viewholder.LoadMoreViewHolder
import com.pixlee.pixleesdk.ui.viewholder.PXLPhotoViewHolder
import com.pixlee.pixleesdk.ui.viewholder.PhotoWithImageScaleType
import com.pixlee.pixleesdk.ui.viewholder.TextHeaderViewHolder
import com.pixlee.pixleesdk.ui.widgets.TextViewStyle
import com.pixlee.pixleesdk.ui.widgets.list.ListHeader

/**
 * This is to display PhotoWithImageScaleType having PXLPhoto in a RecyclerView.
 */
class PXLPhotoAdapter(
        var onButtonClickedListener: ((view: View, photoWithImageScaleType: PhotoWithImageScaleType) -> Unit)? = null,
        var onPhotoClickedListener: ((view: View, photoWithImageScaleType: PhotoWithImageScaleType) -> Unit)? = null,
        var onLoadMoreClickedListener: (() -> Unit)? = null,
        var infiniteScroll: Boolean = false,
        var showingDebugView: Boolean = false
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    sealed class Item {
        class Header(val listHeader: ListHeader) : Item()
        class Content(val data: PhotoWithImageScaleType, var itemType: ItemType) : Item()
        class LoadMore(var loading:Boolean, val loadMoreTextViewStyle: TextViewStyle) : Item()
    }

    sealed class ItemType {
        object List : ItemType()
        object Grid : ItemType()
        class Mosaic(var isLarge: Boolean = false) : ItemType()
    }

    val list: ArrayList<Item> = ArrayList()

    val TYPE_HEADER = 1
    val TYPE_ITEM = 2
    val TYPE_LOAD_MORE = 3
    override fun getItemViewType(position: Int): Int {
        return when (list[getRealPosition(position)]) {
            is Item.Header -> TYPE_HEADER
            is Item.Content -> TYPE_ITEM
            is Item.LoadMore -> TYPE_LOAD_MORE
            else -> TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> TextHeaderViewHolder.create(parent)
            TYPE_ITEM -> PXLPhotoViewHolder.create(parent)
            else -> LoadMoreViewHolder.create(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[getRealPosition(position)]
        when (holder) {
            is TextHeaderViewHolder -> {
                if(holder.itemView.layoutParams is StaggeredGridLayoutManager.LayoutParams){
                    val layoutParams = holder.itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams
                    layoutParams.isFullSpan = true
                }

                val data = item as Item.Header
                holder.setData(data.listHeader)
                holder.itemView.setOnClickListener(null)
            }
            is PXLPhotoViewHolder -> {
                val data = item as Item.Content

                holder.setData(data.data, data.itemType, showingDebugView)
                holder.itemView.setOnClickListener {
                    onPhotoClickedListener?.also {
                        it(holder.itemView, data.data)
                    }
                }

                if (onButtonClickedListener == null) {
                    holder.binding.pxlPhotoView.setButtonClickListener(null)
                } else {
                    holder.binding.pxlPhotoView.setButtonClickListener(View.OnClickListener {
                        onButtonClickedListener?.also {
                            it(holder.itemView, data.data)
                        }
                    })
                }
            }
            is LoadMoreViewHolder -> {
                val data = item as Item.LoadMore
                holder.setData(data)
                holder.binding.tvLoadMore.setOnClickListener {
                    onLoadMoreClickedListener?.also {
                        it()
                    }
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