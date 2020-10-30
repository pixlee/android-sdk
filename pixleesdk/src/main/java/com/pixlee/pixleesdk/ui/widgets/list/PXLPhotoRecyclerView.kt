package com.pixlee.pixleesdk.ui.widgets.list

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.jzvd.Jzvd
import com.pixlee.pixleesdk.R
import com.pixlee.pixleesdk.data.PXLPhoto
import com.pixlee.pixleesdk.ui.adapter.PXLPhotoAdapter
import com.pixlee.pixleesdk.ui.viewholder.PhotoWithImageScaleType
import com.pixlee.pixleesdk.ui.widgets.ImageScaleType
import com.pixlee.pixleesdk.ui.widgets.PXLPhotoView
import com.pixlee.pixleesdk.util.AutoPlayUtils
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * Created by sungjun on 9/17/20.
 */

class PXLPhotoRecyclerView : BaseRecyclerView, LifecycleObserver, CoroutineScope {
    lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        job = Job()
        initView()
    }

    val linearLayoutManager: LinearLayoutManager by lazy {
        LinearLayoutManager(context)
    }

    fun initView() {
        layoutManager = linearLayoutManager
        this.adapter = pxlPhotoAdapter
    }

    internal var alphaForStoppedVideos: Float = 1f
    fun initiate(infiniteScroll: Boolean = false,     // or false
                 showingDebugView: Boolean = false,   // false: for production, true: development only when you want to see the debug info
                 alphaForStoppedVideos: Float = 1f,    // this is the alpha(opacity) of visible items in recyclerview except the first fully visible view(always 1f)
                 onButtonClickedListener: ((view: View, photoWithImageScaleType: PhotoWithImageScaleType) -> Unit)? = null, // called when a button is clicked
                 onPhotoClickedListener: ((view: View, photoWithImageScaleType: PhotoWithImageScaleType) -> Unit)? = null  // called when a whole view is clicked
    ) {
        setHasFixedSize(true)
        pxlPhotoAdapter.infiniteScroll = infiniteScroll
        pxlPhotoAdapter.showingDebugView = showingDebugView
        this.alphaForStoppedVideos = alphaForStoppedVideos
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
                        view.alpha = alphaForStoppedVideos
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
                    AutoPlayUtils.onScrollReleaseAllVideos(recyclerView, linearLayoutManager.findFirstVisibleItemPosition(), linearLayoutManager.findLastVisibleItemPosition(), 20, alphaForStoppedVideos)
                }
            }
        })

        pxlPhotoAdapter.notifyDataSetChanged()
    }

    override fun setList(type: ListAddType, list: List<PhotoWithImageScaleType>) {
        var needToMoveScroll = false
        if (list.isNotEmpty()) {
            needToMoveScroll = pxlPhotoAdapter.list.isEmpty()
        }
        super.setList(type, list)
        moveScrollToInitialPosition(needToMoveScroll)
    }

    private fun moveScrollToInitialPosition(needToMoveScroll: Boolean) {
        if (needToMoveScroll && pxlPhotoAdapter.infiniteScroll) {
            scrollToPosition(Integer.MAX_VALUE / 2)
        }
    }

    internal fun playVideoIfneeded(recyclerView: RecyclerView) {
        if (linearLayoutManager != null && pxlPhotoAdapter != null && pxlPhotoAdapter.list.isNotEmpty()) {
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
    fun useLifecycleObserver(lifecycle: Lifecycle) {
        lifecycle.addObserver(this)
    }

    private var playingVideo = false

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun playVideo() {
        post {
            playingVideo = true
            playVideoIfneeded(this)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun stopVideo() {
        playingVideo = false
        PXLPhotoView.releaseAllVideos()
    }

    /**
     * mute the sound
     */
    fun mute() {
        changeSound(true)
    }

    /**
     * unmute the sound
     */
    fun unmute() {
        changeSound(false)
    }

    var changingSoundJob:Job? = null
    private fun changeSound(muted: Boolean) {
        changingSoundJob?.cancel()
        changingSoundJob = launch {
            withContext(Dispatchers.IO){
                if (pxlPhotoAdapter.list.isNotEmpty()) {
                    pxlPhotoAdapter.list.forEach {
                        when (it) {
                            is PXLPhotoAdapter.Item.Content -> {
                                it.data.soundMuted = muted
                            }
                        }
                    }

                }
            }

            pxlPhotoAdapter.notifyDataSetChanged()
            if (playingVideo) {
                playVideo()
            }
        }
    }
}