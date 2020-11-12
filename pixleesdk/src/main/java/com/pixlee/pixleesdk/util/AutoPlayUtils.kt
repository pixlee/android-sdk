package com.pixlee.pixleesdk.util

import android.graphics.Rect
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.pixlee.pixleesdk.ui.widgets.PXLPhotoView

/**
 * 列表自动播放工具类
 *
 * @author Liberations
 */
object AutoPlayUtils {
    /**
     * @param recyclerView
     * @param firstVisiblePosition
     * @param lastVisiblePosition
     * @param alphaForStoppedVideos alpha for view.alpha when not playing the video
     */
    fun onScrollPlayVideo(recyclerView: RecyclerView, pxlPhotoViewId: Int, firstVisiblePosition: Int, lastVisiblePosition: Int, alphaForStoppedVideos: Float) {
        Log.d("AuthPlayUtils", "position first: $firstVisiblePosition, lastVisiblePosition: $lastVisiblePosition")
        var playingIdx = -1
        var positionInList = -1
        for (i in 0..lastVisiblePosition - firstVisiblePosition) {
            val child = recyclerView.getChildAt(i)
            if (child != null) {
                val pxlPhotoView = child.findViewById<PXLPhotoView>(pxlPhotoViewId)
                if (playingIdx == -1 && getViewVisiblePercent(pxlPhotoView) == 100) {
                    if (positionInList != i + firstVisiblePosition) {
                        playingIdx = i
                        pxlPhotoView.playVideo()
                        positionInList = playingIdx
                    }
                }
                child.alpha = if (playingIdx == i) 1f else alphaForStoppedVideos
            }
        }

        if(positionInList==-1)
            return

        for (i in 0..lastVisiblePosition - firstVisiblePosition) {
            if (positionInList != i) {
                val child = recyclerView.getChildAt(i)
                if (child != null) {
                    val pxlPhotoView = child.findViewById<PXLPhotoView>(pxlPhotoViewId)
                    pxlPhotoView.pauseVideo()
                }
            }
        }
    }

    /**
     * @param recyclerView
     * @param firstVisiblePosition
     * @param lastVisiblePosition
     * @param percent
     * @param alphaForStoppedVideos alpha for view.alpha when not playing the video
     */
    fun onScrollReleaseAllVideos(recyclerView: RecyclerView, pxlPhotoViewId: Int, firstVisiblePosition: Int, lastVisiblePosition: Int, percent: Int, alphaForStoppedVideos: Float) {
        for (i in 0..lastVisiblePosition - firstVisiblePosition) {
            recyclerView.getChildAt(i)?.let { child ->
                val pxlPhotoView = child.findViewById<PXLPhotoView>(pxlPhotoViewId)
                if (pxlPhotoView!=null && pxlPhotoView.havePlayer() && getViewVisiblePercent(pxlPhotoView) < percent) {
                    pxlPhotoView.pauseVideo()
                }

                if(pxlPhotoView==null || !pxlPhotoView.havePlayer()){
                    child.alpha = alphaForStoppedVideos
                }
            }
        }
    }

    fun releaseAllVideos(recyclerView: RecyclerView, pxlPhotoViewId: Int, firstVisiblePosition: Int, lastVisiblePosition: Int, alphaForStoppedVideos: Float) {
        for (i in 0..lastVisiblePosition - firstVisiblePosition) {
            recyclerView.getChildAt(i)?.let { child ->
                val pxlPhotoView = child.findViewById<PXLPhotoView>(pxlPhotoViewId)
                if (pxlPhotoView!=null && pxlPhotoView.havePlayer()) {
                    pxlPhotoView.pauseVideo()
                }
                child.alpha = alphaForStoppedVideos
            }
        }
    }

    /**
     * @param view
     * @return 当前视图可见比列
     */
    fun getViewVisiblePercent(view: View?): Int {
        if (view == null) {
            Log.d("AuthPlayUtils", "-- getViewVisiblePercent () view is null")
            return 0
        }

        var percents = 100
        val rect = Rect()
        val result = !view.getLocalVisibleRect(rect)
        if (result) {
            Log.d("AuthPlayUtils", "-- getViewVisiblePercent () getLocalVisibleRect false")
            return 0
        }
        val height = view.height

        val isViewPartiallyHiddenTop = rect.top > 0
        if (isViewPartiallyHiddenTop) {
            // view is partially hidden behind the top edge
            percents = (height - rect.top) * 100 / height
        } else if (rect.bottom in 1 until height) {
            percents = rect.bottom * 100 / height
        }
        Log.d("AuthPlayUtils", "-- getViewVisiblePercent () percents: " + percents)
        return percents
    }
}