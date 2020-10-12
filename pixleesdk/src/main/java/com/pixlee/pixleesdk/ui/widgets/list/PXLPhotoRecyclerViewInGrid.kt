package com.pixlee.pixleesdk.ui.widgets.list

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.pixlee.pixleesdk.ui.viewholder.PhotoWithImageScaleType
import com.pixlee.pixleesdk.ui.widgets.PXLPhotoView

/**
 * Created by sungjun on 9/17/20.
 */

open class PXLPhotoRecyclerViewInGrid : BaseRecyclerView {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun initiate(gridSpan: Int = 2,
                 showingDebugView: Boolean = false,   // false: for production, true: development only when you want to see the debug info
                 configuration: PXLPhotoView.Configuration? = null,
                 onButtonClickedListener: ((view: View, photoWithImageScaleType: PhotoWithImageScaleType) -> Unit)? = null,
                 onPhotoClickedListener: ((view: View, photoWithImageScaleType: PhotoWithImageScaleType) -> Unit)? = null) {
        layoutManager = StaggeredGridLayoutManager(gridSpan, StaggeredGridLayoutManager.VERTICAL)
        setHasFixedSize(true)

        adapter = pxlPhotoAdapter
        pxlPhotoAdapter.infiniteScroll = false
        pxlPhotoAdapter.showingDebugView = showingDebugView
        pxlPhotoAdapter.photoViewConfiguration = configuration
        pxlPhotoAdapter.onButtonClickedListener = onButtonClickedListener
        pxlPhotoAdapter.onPhotoClickedListener = onPhotoClickedListener
        pxlPhotoAdapter.notifyDataSetChanged()
    }
}