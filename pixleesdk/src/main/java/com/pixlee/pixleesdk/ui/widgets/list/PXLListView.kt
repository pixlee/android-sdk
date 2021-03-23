package com.pixlee.pixleesdk.ui.widgets.list

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pixlee.pixleesdk.R
import com.pixlee.pixleesdk.client.PXLKtxAlbum
import com.pixlee.pixleesdk.client.PXLKtxBaseAlbum
import com.pixlee.pixleesdk.data.PXLAlbumSortOptions
import com.pixlee.pixleesdk.enums.PXLAlbumSortType
import com.pixlee.pixleesdk.ui.adapter.PXLPhotoAdapter
import com.pixlee.pixleesdk.ui.viewholder.PhotoWithImageScaleType
import com.pixlee.pixleesdk.ui.widgets.PXLPhotoView
import com.pixlee.pixleesdk.util.AutoPlayUtils
import com.pixlee.pixleesdk.util.EventObserver
import com.pixlee.pixleesdk.util.px
import kotlinx.coroutines.*

/**
 * Created by sungjun on 9/17/20.
 */

class PXLListView : BaseRecyclerView, LifecycleObserver {
    protected val scope = CoroutineScope(Job() + Dispatchers.Main)
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }
    var viewModel: ListViewModel? = null

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        scope.cancel()
    }

    val linearLayoutManager: LinearLayoutManager by lazy {
        LinearLayoutManager(context)
    }

    fun initView() {
        layoutManager = linearLayoutManager
        this.adapter = pxlPhotoAdapter

    }

    //    lateinit var masterExoPlayerHelper: MasterExoPlayerHelper
    internal var alphaForStoppedVideos: Float = 1f
    var autoPlayVideo = false
    fun initiate(infiniteScroll: Boolean = false,     // or false
                 autoPlayVideo: Boolean = false,
                 showingDebugView: Boolean = false,   // false: for production, true: development only when you want to see the debug info
                 alphaForStoppedVideos: Float = 1f,    // this is the alpha(opacity) of visible items in recyclerview except the first fully visible view(always 1f)
                 cellHeightInPixel: Int = 200.px.toInt(),
                 params: PXLKtxBaseAlbum.Params,
                 configuration: PXLPhotoView.Configuration = PXLPhotoView.Configuration(),
                 onButtonClickedListener: ((view: View, photoWithImageScaleType: PhotoWithImageScaleType) -> Unit)? = null, // called when a button is clicked
                 onPhotoClickedListener: ((view: View, photoWithImageScaleType: PhotoWithImageScaleType) -> Unit)? = null  // called when a whole view is clicked
    ) {
        setHasFixedSize(true)
        pxlPhotoAdapter.infiniteScroll = infiniteScroll
        this.autoPlayVideo = autoPlayVideo
        pxlPhotoAdapter.showingDebugView = showingDebugView
        this.alphaForStoppedVideos = alphaForStoppedVideos

        viewModel = ListViewModel(PXLKtxAlbum(context))
        viewModel?.init(params)
        viewModel?.customizedConfiguration = configuration
        viewModel?.cellHeightInPixel = cellHeightInPixel
        addViewModelListeners()

        pxlPhotoAdapter.onButtonClickedListener = onButtonClickedListener
        pxlPhotoAdapter.onPhotoClickedListener = onPhotoClickedListener

        addOnChildAttachStateChangeListener(object : RecyclerView.OnChildAttachStateChangeListener {
            override fun onChildViewAttachedToWindow(view: View) {
            }

            override fun onChildViewDetachedFromWindow(view: View) {
                if(autoPlayVideo){
                    val pxlPhotoView = view.findViewById<PXLPhotoView>(R.id.pxlPhotoView)
                    if (pxlPhotoView.hasPlayer()) {
                        view.alpha = alphaForStoppedVideos
                        pxlPhotoView.pauseVideo()
                    }
                }

            }
        })

        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (autoPlayVideo && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    playVideoIfneeded(recyclerView)
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (autoPlayVideo && dy != 0) {
                    AutoPlayUtils.onScrollReleaseAllVideos(recyclerView, R.id.pxlPhotoView, linearLayoutManager.findFirstVisibleItemPosition(), linearLayoutManager.findLastVisibleItemPosition(), 20, alphaForStoppedVideos)
                }
            }
        })

        pxlPhotoAdapter.notifyDataSetChanged()

        loadAlbum()
    }

    fun loadAlbum() {
        // retrieve the first page
        scope.launch {
            viewModel?.getFirstPage()
        }
    }

    fun addViewModelListeners() {
        viewModel?.also {viewModel ->
            val lifecycleOwner = context as? LifecycleOwner ?: throw Exception("androidx.lifecycle.LifecycleOwner is required. Please make sure your Activity or Fragment provides androidx.lifecycle.LifecycleOwner")

            viewModel.loading.observe(lifecycleOwner, Observer {
                //lottieView.visibility = if (it) View.VISIBLE else View.GONE
            })

            viewModel.searchResultEvent.observe(lifecycleOwner, EventObserver {
                when (it) {
                    is ListViewModel.Command.Data -> {
                        if (it.isFirstPage) {
                            replaceList(it.list)
                            playVideoOnResume()

                            // if no result in the first page, open search panel so that the SDK developers will try out different filters
                            if (it.list.isEmpty()) {
                                Toast.makeText(context, "success!! but you got an empty list.\nwhat about trying different searching options here?", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            addList(it.list)
                        }

                    }
                }
            })
        }
    }

    override fun setList(type: ListAddType, list: List<PhotoWithImageScaleType>) {
        var needToMoveScroll = false
        if (list.isNotEmpty()) {
            needToMoveScroll = pxlPhotoAdapter.list.isEmpty()
        }
        super.setList(type, list)
        moveScrollToInitialPosition(needToMoveScroll)
        fireOpenAndVisible()
    }

    private fun moveScrollToInitialPosition(needToMoveScroll: Boolean) {
        if (needToMoveScroll && pxlPhotoAdapter.infiniteScroll) {
            scrollToPosition(Integer.MAX_VALUE / 2)
        }
    }

    internal fun playVideoIfneeded(recyclerView: RecyclerView) {
        if (autoPlayVideo && linearLayoutManager != null && pxlPhotoAdapter != null && pxlPhotoAdapter.list.isNotEmpty()) {
            var muted = false
            if(pxlPhotoAdapter.list.lastOrNull() is PXLPhotoAdapter.Item.Content){
                (pxlPhotoAdapter.list.lastOrNull() as PXLPhotoAdapter.Item.Content).let {
                    muted = it.data.soundMuted
                }
            }
            AutoPlayUtils.onScrollPlayVideo(recyclerView,
                    R.id.pxlPhotoView,
                    linearLayoutManager.findFirstVisibleItemPosition(),
                    linearLayoutManager.findLastVisibleItemPosition(),
                    alphaForStoppedVideos,
                    muted)
        }
    }

    /**
     * This will play the video on onResume and stop the video on onPause.
     *   - when ON_RESUME, this will call playVideo()
     *   - when ON_PAUSE, this will call stopVideo()
     * If you want to manually play and stop the video, don't use this and do use playVideo() and stopVideo() when you want
     */
    fun useLifecycleObserver(lifecycle: Lifecycle) {
//        masterExoPlayerHelper.makeLifeCycleAware(lifecycle)
        lifecycle.addObserver(this)
    }

    private var playingVideo = false

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private fun playVideoOnStart() {
        playVideoOnResume()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun playVideoOnResume() {
        // The reason why we need post {} is to give list time to get the item loaded completely.
        // linearLayoutManager.findFirstVisibleItemPosition() and linearLayoutManager.findLastVisibleItemPosition() return -1 without post {}.
        post {
            changingSoundJob?.cancel()
            playingVideo = true
            playVideoIfneeded(this)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun stopVideoOnPause() {
        changingSoundJob?.cancel()
        playingVideo = false

        AutoPlayUtils.releaseAllVideos(this, R.id.pxlPhotoView, linearLayoutManager.findFirstVisibleItemPosition(), linearLayoutManager.findLastVisibleItemPosition(), alphaForStoppedVideos)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private fun stopVideoOnStop() {
        stopVideoOnPause()
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

    var changingSoundJob: Job? = null
    private fun changeSound(muted: Boolean) {
        changingSoundJob?.cancel()
        changingSoundJob = scope.launch {
            withContext(Dispatchers.IO) {
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

            changeVolume(muted)
        }
    }

    private fun changeVolume(muted: Boolean) {
        Log.e("aa", "changeVolume- applyVolume: ${linearLayoutManager.findFirstVisibleItemPosition()}")
        AutoPlayUtils.applyVolume(this, R.id.pxlPhotoView, linearLayoutManager.findFirstVisibleItemPosition(), linearLayoutManager.findLastVisibleItemPosition(), muted, alphaForStoppedVideos)
    }
}