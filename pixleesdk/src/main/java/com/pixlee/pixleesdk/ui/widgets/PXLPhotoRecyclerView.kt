package com.pixlee.pixleesdk.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.jzvd.Jzvd
import com.pixlee.pixleesdk.R
import com.pixlee.pixleesdk.data.PXLPhoto
import com.pixlee.pixleesdk.ui.adapter.PXLPhotoAdapter
import com.pixlee.pixleesdk.ui.viewholder.PhotoWithImageScaleType
import com.pixlee.pixleesdk.util.AutoPlayUtils
import com.pixlee.pixleesdk.util.px

/**
 * Created by sungjun on 9/17/20.
 */

class PXLPhotoRecyclerView : RecyclerView, LifecycleObserver {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }

    val pxlPhotoAdapter: PXLPhotoAdapter by lazy {
        PXLPhotoAdapter()
    }

    val linearLayoutManager: LinearLayoutManager by lazy {
        LinearLayoutManager(context)
    }


    fun initView() {
        layoutManager = linearLayoutManager
        this.adapter = pxlPhotoAdapter
    }

    var alphaForStoppedVideos:Float = 1f
    fun initiate(infiniteScroll: Boolean = false,
                 showingDebugView: Boolean = false,
                 alphaForStoppedVideos:Float = 1f,
                 configuration: PXLPhotoView.Configuration? = null,
                 onButtonClickedListener: ((view: View, photoWithImageScaleType: PhotoWithImageScaleType) -> Unit)? = null,
                 onPhotoClickedListener: ((view: View, photoWithImageScaleType: PhotoWithImageScaleType) -> Unit)? = null) {
        pxlPhotoAdapter.infiniteScroll = infiniteScroll
        pxlPhotoAdapter.showingDebugView = showingDebugView
        this.alphaForStoppedVideos = alphaForStoppedVideos
        pxlPhotoAdapter.photoViewConfiguration = configuration
        pxlPhotoAdapter.onButtonClickedListener = onButtonClickedListener
        pxlPhotoAdapter.onPhotoClickedListener = onPhotoClickedListener

        addOnChildAttachStateChangeListener(object : RecyclerView.OnChildAttachStateChangeListener {
            override fun onChildViewAttachedToWindow(view: View) {
            }

            override fun onChildViewDetachedFromWindow(view: View) {
                val pxlPhotoView = view.findViewById<PXLPhotoView>(R.id.pxlPhotoView)
                val jzvd: Jzvd = pxlPhotoView.videoView
                if (jzvd != null && Jzvd.CURRENT_JZVD != null &&
                        jzvd.jzDataSource.containsTheUrl(Jzvd.CURRENT_JZVD.jzDataSource.currentUrl)) {
                    if (Jzvd.CURRENT_JZVD != null && Jzvd.CURRENT_JZVD.screen != Jzvd.SCREEN_FULLSCREEN) {
                        PXLPhotoView.releaseAllVideos()
                    }
                }
            }
        })

        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    playVideoIfneeded(recyclerView)
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy != 0) {
                    AutoPlayUtils.onScrollReleaseAllVideos(linearLayoutManager.findFirstVisibleItemPosition(), linearLayoutManager.findLastVisibleItemPosition(), 20)
                }
            }
        })

        pxlPhotoAdapter.notifyDataSetChanged()
    }

    /**
     * Add a list: List<PhotoWithImageScaleType> to an existing list
     */
    fun addList(list: List<PhotoWithImageScaleType>) {
        setList(ListAddType.ADD, list)
    }

    /**
     * Add a list: List<PhotoWithImageScaleType> to an existing list
     */
    fun addList(list: List<PXLPhoto>, imageScaleType: PXLPhotoView.ImageScaleType, heightInPixel: Int = 400.px.toInt()) {
        setList(ListAddType.ADD, list, imageScaleType, heightInPixel)
    }

    /**
     * Replace a list: List<PhotoWithImageScaleType> with an existing list
     */
    fun replaceList(list: List<PhotoWithImageScaleType>) {
        setList(ListAddType.REPLACE, list)
    }

    /**
     * Replace a list: List<PXLPhoto> with an existing list
     */
    fun replaceList(list: List<PXLPhoto>, imageScaleType: PXLPhotoView.ImageScaleType, heightInPixel: Int = 400.px.toInt()) {
        setList(ListAddType.REPLACE, list, imageScaleType, heightInPixel)
    }

    private fun setList(type: ListAddType, list: List<PXLPhoto>, imageScaleType: PXLPhotoView.ImageScaleType, heightInPixel: Int = 400.px.toInt()) {
        when (type) {
            ListAddType.REPLACE -> clearOldList()
            ListAddType.ADD -> { /* do nothing */
            }
        }

        if (list.isNotEmpty()) {
            val needToMoveScroll = type == ListAddType.ADD && pxlPhotoAdapter.list.isEmpty()
            list.forEach {
                pxlPhotoAdapter.list.add(PhotoWithImageScaleType(it, imageScaleType, heightInPixel))
            }
            pxlPhotoAdapter.notifyDataSetChanged()
            moveScrollToInitialPosition(needToMoveScroll)
        }
    }

    private fun setList(type: ListAddType, list: List<PhotoWithImageScaleType>) {
        when (type) {
            ListAddType.REPLACE -> clearOldList()
            ListAddType.ADD -> { /* do nothing */
            }
        }

        if (list.isNotEmpty()) {
            val needToMoveScroll = type == ListAddType.ADD && pxlPhotoAdapter.list.isEmpty()
            list.forEach {
                pxlPhotoAdapter.list.add(it)
            }
            pxlPhotoAdapter.notifyDataSetChanged()
            moveScrollToInitialPosition(needToMoveScroll)
        }
    }

    private fun moveScrollToInitialPosition(needToMoveScroll: Boolean) {
        if (needToMoveScroll && pxlPhotoAdapter.infiniteScroll) {
            scrollToPosition(Integer.MAX_VALUE / 2)
        }
    }

    private fun clearOldList() {
        if (pxlPhotoAdapter.list.isNotEmpty()) {
            pxlPhotoAdapter.list.clear()
            pxlPhotoAdapter.notifyDataSetChanged()
        }
    }

    internal fun playVideoIfneeded(recyclerView: RecyclerView){
        if(linearLayoutManager!=null && pxlPhotoAdapter!=null && pxlPhotoAdapter.list.isNotEmpty()){
            AutoPlayUtils.onScrollPlayVideo(recyclerView,
                    R.id.pxlPhotoView,
                    linearLayoutManager.findFirstVisibleItemPosition(),
                    linearLayoutManager.findLastVisibleItemPosition(),
                    alphaForStoppedVideos)
        }
    }

    /**
     * This will play the video on onResume and stop the video on onPause.
     *   - when ON_RESUME, this will call playVideo()
     *   - when ON_PAUSE, this will call stopVideo()
     * If you want to manually play and stop the video, don't use this and do use playVideo() and stopVideo() when you want
     */
    fun useLifecycleObserver(lifecycle:Lifecycle){
        lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun playVideo() {
        post {
            playVideoIfneeded(this)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun stopVideo() {
        PXLPhotoView.releaseAllVideos()
    }


    internal enum class ListAddType {
        ADD, REPLACE
    }
}