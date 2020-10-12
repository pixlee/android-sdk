package com.pixlee.pixleesdk.ui.widgets.list

import android.content.Context
import android.text.Spannable
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.pixlee.pixleesdk.data.PXLPhoto
import com.pixlee.pixleesdk.ui.adapter.PXLPhotoAdapter
import com.pixlee.pixleesdk.ui.viewholder.PhotoWithImageScaleType
import com.pixlee.pixleesdk.ui.widgets.PXLPhotoView
import com.pixlee.pixleesdk.ui.widgets.TextPadding
import com.pixlee.pixleesdk.util.GridSpacingItemDecoration
import com.pixlee.pixleesdk.util.px

/**
 * Created by sungjun on 9/17/20.
 */

open class PXLPhotoRecyclerViewInGrid : BaseRecyclerView {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    var title: Spannable? = null

    fun initiate(gridSpan: Int = 2,
                 lineSpace: Space = Space(),
                 title: Spannable? = null,
                 showingDebugView: Boolean = false,   // false: for production, true: development only when you want to see the debug info
                 configuration: PXLPhotoView.Configuration? = null,
                 onButtonClickedListener: ((view: View, photoWithImageScaleType: PhotoWithImageScaleType) -> Unit)? = null,
                 onPhotoClickedListener: ((view: View, photoWithImageScaleType: PhotoWithImageScaleType) -> Unit)? = null) {
        layoutManager = StaggeredGridLayoutManager(gridSpan, StaggeredGridLayoutManager.VERTICAL)
        addItemDecoration(GridSpacingItemDecoration(gridSpan, lineSpace.lineWidthInPixel, lineSpace.includingEdge, title != null))
        setHasFixedSize(true)
        this.title = title
        adapter = pxlPhotoAdapter
        pxlPhotoAdapter.infiniteScroll = false
        pxlPhotoAdapter.showingDebugView = showingDebugView
        pxlPhotoAdapter.photoViewConfiguration = configuration
        pxlPhotoAdapter.onButtonClickedListener = onButtonClickedListener
        pxlPhotoAdapter.onPhotoClickedListener = onPhotoClickedListener
        pxlPhotoAdapter.notifyDataSetChanged()
    }

    internal override fun setList(type: ListAddType, list: List<PXLPhoto>, imageScaleType: PXLPhotoView.ImageScaleType, heightInPixel: Int) {
        clearOldList(type)
        if (pxlPhotoAdapter.list.isEmpty() && title != null) {
            pxlPhotoAdapter.list.add(PXLPhotoAdapter.Item.Header(text = title!!))
        }
        if (list.isNotEmpty()) {
            list.forEach {
                pxlPhotoAdapter.list.add(PXLPhotoAdapter.Item.Content(PhotoWithImageScaleType(it, imageScaleType, heightInPixel)))
            }
            pxlPhotoAdapter.notifyDataSetChanged()
        }
    }

    internal override fun setList(type: ListAddType, list: List<PhotoWithImageScaleType>) {
        clearOldList(type)
        if (pxlPhotoAdapter.list.isEmpty() && title != null) {
            val padding = 20.px.toInt()
            pxlPhotoAdapter.list.add(PXLPhotoAdapter.Item.Header(text = title!!, padding = TextPadding(padding, padding, padding, padding)))
        }
        if (list.isNotEmpty()) {
            list.forEach {
                pxlPhotoAdapter.list.add(PXLPhotoAdapter.Item.Content(it))
            }
            pxlPhotoAdapter.notifyDataSetChanged()
        }
    }
}

class Space(var lineWidthInPixel: Int = 4.px.toInt(), var includingEdge: Boolean = true)