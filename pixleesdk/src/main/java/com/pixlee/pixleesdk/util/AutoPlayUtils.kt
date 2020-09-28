package com.pixlee.pixleesdk.util

import android.graphics.Rect
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cn.jzvd.Jzvd
import com.pixlee.pixleesdk.ui.widgets.JzvdVolumeControl
import com.pixlee.pixleesdk.ui.widgets.PXLPhotoView

/**
 * 列表自动播放工具类
 *
 * @author Liberations
 */
object AutoPlayUtils {
    var positionInList = -1 //记录当前播放列表位置

    /**
     * @param firstVisiblePosition 首个可见item位置
     * @param lastVisiblePosition  最后一个可见item位置
     */
    fun onScrollPlayVideo(recyclerView: RecyclerView, jzvdId: Int, firstVisiblePosition: Int, lastVisiblePosition: Int) {
        Log.d("AuthPlayUtils", "position first: $firstVisiblePosition, lastVisiblePosition: $lastVisiblePosition")
        for (i in 0..lastVisiblePosition - firstVisiblePosition) {
            val child = recyclerView.getChildAt(i)
            val pxlPhotoView = child.findViewById<PXLPhotoView>(jzvdId)
            if (getViewVisiblePercent(pxlPhotoView) == 100) {
                if (positionInList != i + firstVisiblePosition) {
                    Log.e("AuthPlayUtils", "-- detected player performClick() position: " + (firstVisiblePosition + i))
                    pxlPhotoView.setVolume(0f)
                            .setLooping(true)
                            .playVideo()
                }
                return
            }
        }
    }

    /**
     * @param firstVisiblePosition 首个可见item位置
     * @param lastVisiblePosition  最后一个可见item位置
     * @param percent              当item被遮挡percent/1时释放,percent取值0-1
     */
    fun onScrollReleaseAllVideos(firstVisiblePosition: Int, lastVisiblePosition: Int, percent: Int) {
        if (Jzvd.CURRENT_JZVD == null) return
        if (positionInList >= 0) {
            if (positionInList <= firstVisiblePosition || positionInList >= lastVisiblePosition - 1) {
                if (getViewVisiblePercent(Jzvd.CURRENT_JZVD) < percent) {
                    Jzvd.releaseAllVideos()
                }
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