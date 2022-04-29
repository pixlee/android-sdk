package com.pixlee.pixleesdk.ui.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class GridSpacingItemDecoration(var spanCount: Int, var spacingPx: Int, var includingEdge: Boolean, var includingTitle: Boolean) : ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view) // item position
        val offset = if(includingTitle) 1 else 0

        if(includingTitle && position==0){
            outRect.left = 0
            outRect.right = 0
            outRect.top = 0
            outRect.bottom = 0 // item bottom
            return
        }

        val realPosition = position - offset
        val column = realPosition % spanCount // item column
        if (includingEdge) {
            outRect.left = spacingPx - column * spacingPx / spanCount
            outRect.right = (column + 1) * spacingPx / spanCount
            if (realPosition < spanCount) { // top edge
                outRect.top = spacingPx
            }
            outRect.bottom = spacingPx // item bottom
        } else {
            outRect.left = column * spacingPx / spanCount
            outRect.right = spacingPx - (column + 1) * spacingPx / spanCount
            if (realPosition >= spanCount) {
                outRect.top = spacingPx // item top
            }
        }
    }
}