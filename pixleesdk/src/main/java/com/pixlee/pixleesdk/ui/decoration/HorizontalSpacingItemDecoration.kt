package com.pixlee.pixleesdk.ui.decoration

import android.graphics.Rect
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.pixlee.pixleesdk.ui.widgets.mosaic.InvalidSpanSizeException
import com.pixlee.pixleesdk.ui.widgets.mosaic.RectsHelper
import com.pixlee.pixleesdk.ui.widgets.mosaic.SpanSize
import com.pixlee.pixleesdk.ui.widgets.mosaic.SpannedGridLayoutManager

class HorizontalSpacingItemDecoration(var spacingPx: Int) : ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)

        val isTopEnd = position == 0
        val centerPX = spacingPx / 2
        val edgePX = 0

        outRect.left = if(isTopEnd) edgePX else centerPX
        outRect.top = 0
        outRect.right = centerPX
        outRect.bottom = 0
    }
}