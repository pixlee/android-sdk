package com.pixlee.pixleesdk.ui.widgets.list

import android.content.Context
import android.text.Spannable
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.pixlee.pixleesdk.data.PXLPhoto
import com.pixlee.pixleesdk.ui.adapter.PXLPhotoAdapter
import com.pixlee.pixleesdk.ui.viewholder.PhotoWithImageScaleType
import com.pixlee.pixleesdk.ui.widgets.ImageScaleType
import com.pixlee.pixleesdk.ui.widgets.PXLPhotoView
import com.pixlee.pixleesdk.ui.widgets.TextPadding
import com.pixlee.pixleesdk.util.GridSpacingItemDecoration
import com.pixlee.pixleesdk.util.px

sealed class ListHeader {
    class SpannableText(val spannable: Spannable, val padding:TextPadding = TextPadding()) : ListHeader()
    class Gif(val url: String, val heightInPixel: Int, val imageScaleType: ImageScaleType) : ListHeader()
}

/**
 * Created by sungjun on 9/17/20.
 */

open class PXLPhotoRecyclerViewInGrid : BaseRecyclerView {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    var listHeader: ListHeader? = null

    fun initiate(gridSpan: Int = 2,
                 lineSpace: Space = Space(),
                 listHeader: ListHeader? = null,
                 showingDebugView: Boolean = false,   // false: for production, true: development only when you want to see the debug info
                 onButtonClickedListener: ((view: View, photoWithImageScaleType: PhotoWithImageScaleType) -> Unit)? = null,
                 onPhotoClickedListener: ((view: View, photoWithImageScaleType: PhotoWithImageScaleType) -> Unit)? = null) {
        layoutManager = StaggeredGridLayoutManager(gridSpan, StaggeredGridLayoutManager.VERTICAL)
        addItemDecoration(GridSpacingItemDecoration(gridSpan, lineSpace.lineWidthInPixel, lineSpace.includingEdge, listHeader != null))
        setHasFixedSize(true)
        this.listHeader = listHeader
        adapter = pxlPhotoAdapter
        pxlPhotoAdapter.infiniteScroll = false
        pxlPhotoAdapter.showingDebugView = showingDebugView
        pxlPhotoAdapter.onButtonClickedListener = onButtonClickedListener
        pxlPhotoAdapter.onPhotoClickedListener = onPhotoClickedListener
        pxlPhotoAdapter.notifyDataSetChanged()

        /*listHeader?.let{
            pxlPhotoAdapter.list.add(PXLPhotoAdapter.Item.Header(listHeader = it))
            pxlPhotoAdapter.notifyDataSetChanged()
        }*/
    }

    internal override fun setList(type: ListAddType, list: List<PhotoWithImageScaleType>) {
        clearOldList(type)
        if (pxlPhotoAdapter.list.isEmpty() && listHeader != null) {
            pxlPhotoAdapter.list.add(PXLPhotoAdapter.Item.Header(listHeader = listHeader!!))
        }
        if (list.isNotEmpty()) {
            list.forEach {
                pxlPhotoAdapter.list.add(PXLPhotoAdapter.Item.Content(it))
            }
            pxlPhotoAdapter.notifyDataSetChanged()
        }
        fireAnalytics()
    }
}

class Space(var lineWidthInPixel: Int = 4.px.toInt(), var includingEdge: Boolean = true)