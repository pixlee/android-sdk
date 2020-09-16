package com.pixlee.pixleeandroidsdk.ui.widgets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pixlee.pixleeandroidsdk.R
import com.pixlee.pixleeandroidsdk.ui.BaseFragment
import com.pixlee.pixleesdk.PXLPhoto
import com.pixlee.pixleesdk.ui.widgets.ImageScaleType
import com.volokh.danylo.video_player_manager.manager.SingleVideoPlayerManager
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager
import com.volokh.danylo.video_player_manager.meta.MetaData
import com.volokh.danylo.visibility_utils.calculator.DefaultSingleItemCalculatorCallback
import com.volokh.danylo.visibility_utils.calculator.ListItemsVisibilityCalculator
import com.volokh.danylo.visibility_utils.calculator.SingleListViewItemActiveCalculator
import com.volokh.danylo.visibility_utils.scroll_utils.ItemsPositionGetter
import com.volokh.danylo.visibility_utils.scroll_utils.RecyclerViewItemPositionGetter
import kotlinx.android.synthetic.main.fragment_pxlphotoview_in_recyclerview.*

/**
 * This is to display a photo with texts of PXLPhoto in RecyclerView
 */
class PXLPhotoViewInRecyclerViewFragment : BaseFragment() {
    override fun getTitleResource(): Int {
        return R.string.title_pxlphotoview_in_recyclerview
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_pxlphotoview_in_recyclerview, container, false)
    }

    val adapter: PXLPhotoAdapter by lazy {
        PXLPhotoAdapter(videoPlayerManager)
    }

    var mVideoVisibilityCalculator: ListItemsVisibilityCalculator? = null

    val videoPlayerManager: VideoPlayerManager<MetaData> = SingleVideoPlayerManager {

    }

    val layoutManager: LinearLayoutManager by lazy {
        LinearLayoutManager(activity)
    }
    private var mScrollState = AbsListView.OnScrollListener.SCROLL_STATE_IDLE

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        //recyclerView.setHasFixedSize(true)

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter



        mVideoVisibilityCalculator = SingleListViewItemActiveCalculator(DefaultSingleItemCalculatorCallback(), adapter.list)
        mItemsPositionGetter = RecyclerViewItemPositionGetter(layoutManager, recyclerView)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, scrollState: Int) {
                mScrollState = scrollState
                if (scrollState == RecyclerView.SCROLL_STATE_IDLE && layoutManager != null && mItemsPositionGetter != null && mScrollState != null && adapter != null && adapter.list.isNotEmpty()) {
                    mVideoVisibilityCalculator?.onScrollStateIdle(
                            mItemsPositionGetter,
                            layoutManager.findFirstVisibleItemPosition(),
                            layoutManager.findLastVisibleItemPosition())
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (layoutManager != null && mItemsPositionGetter != null && mScrollState != null && adapter != null && adapter.list.isNotEmpty()) {
                    mVideoVisibilityCalculator?.onScroll(
                            mItemsPositionGetter,
                            layoutManager.findFirstVisibleItemPosition(),
                            layoutManager.findLastVisibleItemPosition() - layoutManager.findFirstVisibleItemPosition() + 1,
                            mScrollState)
                }
            }
        })

        val pxlPhoto: PXLPhoto? = arguments?.getParcelable("pxlPhoto")
        pxlPhoto?.also {
//            pxlPhotoViewCrop.setPhoto(it, ImageScaleType.CENTER_CROP)
//            pxlPhotoViewFit.setPhoto(it)

//            for (i in 1..5) {
//                //adapter.list.add("$i")
//                adapter.list.add(PhotoWithImageScaleType(it, ImageScaleType.CENTER_CROP, videoPlayerManager))
//            }
//
//            adapter.list.add(PhotoWithImageScaleType(it, ImageScaleType.CENTER_CROP, videoPlayerManager))

            for (i in 6..10) {
                //adapter.list.add("$i")
                adapter.list.add(PhotoWithImageScaleType(it, ImageScaleType.FIT_CENTER, videoPlayerManager))
            }

            adapter.list.add(PhotoWithImageScaleType(it, ImageScaleType.FIT_CENTER, videoPlayerManager))

            for (i in 11..20) {
                //adapter.list.add("$i")
                adapter.list.add(PhotoWithImageScaleType(it, ImageScaleType.FIT_CENTER, videoPlayerManager))
            }
            adapter.notifyItemRangeInserted(0, adapter.itemCount)
        }
    }

    /**
     * ItemsPositionGetter is used by [ListItemsVisibilityCalculator] for getting information about
     * items position in the RecyclerView and LayoutManager
     */
    private var mItemsPositionGetter: ItemsPositionGetter? = null

    override fun onResume() {
        super.onResume()
        if (adapter.list.isNotEmpty()) {
            // need to call this method from list view handler in order to have filled list
            recyclerView.postDelayed(Runnable {
                mVideoVisibilityCalculator?.onScrollStateIdle(
                        mItemsPositionGetter,
                        layoutManager.findFirstVisibleItemPosition(),
                        layoutManager.findLastVisibleItemPosition())
            }, 2000)
        }
    }

    override fun onStop() {
        super.onStop()
        // we have to stop any playback in onStop
        videoPlayerManager.resetMediaPlayer()
    }

    companion object {
        fun getInstance(pxlPhoto: PXLPhoto): Fragment {
            val f = PXLPhotoViewInRecyclerViewFragment()
            val bundle = Bundle()
            bundle.putParcelable("pxlPhoto", pxlPhoto)
            f.arguments = bundle
            return f
        }
    }
}
