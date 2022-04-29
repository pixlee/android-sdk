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

class SpannedGridSpacingItemDecoration(val layoutManager: SpannedGridLayoutManager, var spanCount: Int, var spacingPx: Int, var includingEdge: Boolean) : ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        val rect = layoutManager.getItemIndex(position)

        val isTopEnd = rect.top == 0
        val isLeftEnd = rect.left == 0
        val isRightEnd = rect.right == spanCount
        val centerPX = spacingPx / 2
        val edgePX = if(includingEdge) spacingPx else 0

        outRect.left = if(isLeftEnd) edgePX else centerPX
        outRect.top = if(isTopEnd) edgePX else centerPX
        outRect.right = if(isRightEnd) edgePX else centerPX
        outRect.bottom = centerPX
    }
}