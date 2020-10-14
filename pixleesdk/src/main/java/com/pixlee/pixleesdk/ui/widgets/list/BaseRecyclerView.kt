package com.pixlee.pixleesdk.ui.widgets.list

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import com.pixlee.pixleesdk.data.PXLPhoto
import com.pixlee.pixleesdk.ui.adapter.PXLPhotoAdapter
import com.pixlee.pixleesdk.ui.viewholder.PhotoWithImageScaleType
import com.pixlee.pixleesdk.ui.widgets.ImageScaleType
import com.pixlee.pixleesdk.ui.widgets.PXLPhotoView
import com.pixlee.pixleesdk.util.px

/**
 * Created by sungjun on 9/17/20.
 */

open class BaseRecyclerView : RecyclerView {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    val pxlPhotoAdapter: PXLPhotoAdapter by lazy {
        PXLPhotoAdapter()
    }

    /**
     * Add a list: List<PhotoWithImageScaleType> to an existing list
     */
    open fun addList(list: List<PhotoWithImageScaleType>) {
        setList(ListAddType.ADD, list)
    }

    /**
     * Add a list: List<PhotoWithImageScaleType> to an existing list
     */
    fun addList(list: List<PXLPhoto>, imageScaleType: ImageScaleType, heightInPixel: Int = 400.px.toInt()) {
        setList(ListAddType.ADD, list, imageScaleType, heightInPixel)
    }

    /**
     * Replace a list: List<PhotoWithImageScaleType> with an existing list
     */
    fun replaceList(list: List<PhotoWithImageScaleType>) {
        setList(ListAddType.REPLACE, list)
    }

    /**
     * Replace a list: List<PXLPhoto> with an existing list
     */
    fun replaceList(list: List<PXLPhoto>, imageScaleType: ImageScaleType, heightInPixel: Int = 400.px.toInt()) {
        setList(ListAddType.REPLACE, list, imageScaleType, heightInPixel)
    }

    internal open fun setList(type: ListAddType, list: List<PXLPhoto>, imageScaleType: ImageScaleType, heightInPixel: Int = 400.px.toInt()) {
        clearOldList(type)
        if (list.isNotEmpty()) {
            list.forEach {
                pxlPhotoAdapter.list.add(PXLPhotoAdapter.Item.Content(PhotoWithImageScaleType(it, imageScaleType, heightInPixel)))
            }
            pxlPhotoAdapter.notifyDataSetChanged()
        }
    }

    internal open fun setList(type: ListAddType, list: List<PhotoWithImageScaleType>) {
        clearOldList(type)

        if (list.isNotEmpty()) {
            list.forEach {
                pxlPhotoAdapter.list.add(PXLPhotoAdapter.Item.Content(it))
            }
            pxlPhotoAdapter.notifyDataSetChanged()
        }
    }

    internal fun clearOldList(type: ListAddType) {
        when (type) {
            ListAddType.REPLACE -> clearOldList()
            ListAddType.ADD -> { /* do nothing */
            }
        }
    }

    internal fun clearOldList() {
        if (pxlPhotoAdapter.list.isNotEmpty()) {
            pxlPhotoAdapter.list.clear()
            pxlPhotoAdapter.notifyDataSetChanged()
        }
    }

    internal enum class ListAddType {
        ADD, REPLACE
    }
}