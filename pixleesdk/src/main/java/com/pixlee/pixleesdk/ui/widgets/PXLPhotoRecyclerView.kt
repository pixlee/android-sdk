package com.pixlee.pixleesdk.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.widget.AbsListView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pixlee.pixleesdk.data.PXLPhoto
import com.pixlee.pixleesdk.ui.viewholder.PXLPhotoAdapter
import com.pixlee.pixleesdk.ui.viewholder.PhotoWithImageScaleType
import com.pixlee.pixleesdk.util.px
import com.volokh.danylo.video_player_manager.manager.SingleVideoPlayerManager
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager
import com.volokh.danylo.video_player_manager.meta.MetaData
import com.volokh.danylo.visibility_utils.calculator.DefaultSingleItemCalculatorCallback
import com.volokh.danylo.visibility_utils.calculator.ListItemsVisibilityCalculator
import com.volokh.danylo.visibility_utils.calculator.SingleListViewItemActiveCalculator
import com.volokh.danylo.visibility_utils.scroll_utils.ItemsPositionGetter
import com.volokh.danylo.visibility_utils.scroll_utils.RecyclerViewItemPositionGetter

/**
 * Created by sungjun on 9/17/20.
 */

class PXLPhotoRecyclerView : RecyclerView {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }

    val pxlPhotoAdapter: PXLPhotoAdapter by lazy {
        PXLPhotoAdapter { pxlPhoto ->
            itemClickListener?.also {
                it(pxlPhoto)
            }
        }
    }

    private var mScrollState = AbsListView.OnScrollListener.SCROLL_STATE_IDLE
    var mItemsPositionGetter: ItemsPositionGetter? = null
    var mVideoVisibilityCalculator: ListItemsVisibilityCalculator? = null

    val singleVideoPlayerManager: VideoPlayerManager<MetaData> = SingleVideoPlayerManager {

    }

    val linearLayoutManager: LinearLayoutManager by lazy {
        LinearLayoutManager(context)
    }


    fun initView() {
        layoutManager = linearLayoutManager
        this.adapter = pxlPhotoAdapter

        mVideoVisibilityCalculator = SingleListViewItemActiveCalculator(DefaultSingleItemCalculatorCallback(), pxlPhotoAdapter.list)
        mItemsPositionGetter = RecyclerViewItemPositionGetter(linearLayoutManager, this)

        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, scrollState: Int) {
                mScrollState = scrollState
                if (scrollState == RecyclerView.SCROLL_STATE_IDLE && linearLayoutManager != null && mItemsPositionGetter != null && mScrollState != null && pxlPhotoAdapter != null && pxlPhotoAdapter.list.isNotEmpty()) {
                    mVideoVisibilityCalculator?.onScrollStateIdle(
                            mItemsPositionGetter,
                            linearLayoutManager.findFirstVisibleItemPosition(),
                            linearLayoutManager.findLastVisibleItemPosition())
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (linearLayoutManager != null && mItemsPositionGetter != null && mScrollState != null && pxlPhotoAdapter != null && pxlPhotoAdapter.list.isNotEmpty()) {
                    mVideoVisibilityCalculator?.onScroll(
                            mItemsPositionGetter,
                            linearLayoutManager.findFirstVisibleItemPosition(),
                            linearLayoutManager.findLastVisibleItemPosition() - linearLayoutManager.findFirstVisibleItemPosition() + 1,
                            mScrollState)
                }
            }
        })
    }

    var itemClickListener: ((pxlPhoto: PXLPhoto) -> Unit)? = null
    fun setOnItemClicked(listener: (pxlPhoto: PXLPhoto) -> Unit) {
        itemClickListener = listener
    }

    fun replaceList(list: List<PhotoWithImageScaleType>) {
        clearOldList()
        if (list.isNotEmpty()) {
            list.forEach {
                pxlPhotoAdapter.list.add(it.apply {
                    videoPlayerManager = singleVideoPlayerManager
                })
            }
            pxlPhotoAdapter.notifyItemRangeInserted(0, pxlPhotoAdapter.list.size)
        }
    }

    fun replaceList(list: List<PXLPhoto>, imageScaleType: ImageScaleType, heightInPixel: Int = 400.px.toInt()) {
        clearOldList()
        if (list.isNotEmpty()) {
            list.forEach {
                pxlPhotoAdapter.list.add(PhotoWithImageScaleType(it, imageScaleType, heightInPixel).apply {
                    videoPlayerManager = singleVideoPlayerManager
                })
            }
            pxlPhotoAdapter.notifyItemRangeInserted(0, pxlPhotoAdapter.list.size)
        }
    }

    private fun clearOldList() {
        if (pxlPhotoAdapter.list.isNotEmpty()) {
            val count = pxlPhotoAdapter.list.size
            pxlPhotoAdapter.list.clear()
            pxlPhotoAdapter.notifyItemRangeRemoved(0, count)
        }
    }


    fun onResume() {
        postDelayed(Runnable {
            if (pxlPhotoAdapter.list.isNotEmpty()) {
                mVideoVisibilityCalculator?.onScrollStateIdle(
                        mItemsPositionGetter,
                        linearLayoutManager.findFirstVisibleItemPosition(),
                        linearLayoutManager.findLastVisibleItemPosition())
            }
        }, 500)
    }

    fun onStop() {
        // we have to stop any playback in onStop
        singleVideoPlayerManager.resetMediaPlayer()
    }
}