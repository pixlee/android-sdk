package com.pixlee.pixleesdk.ui.widgets.list.v2

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.pixlee.pixleesdk.R
import com.pixlee.pixleesdk.client.PXLKtxAlbum
import com.pixlee.pixleesdk.client.PXLKtxBaseAlbum
import com.pixlee.pixleesdk.ui.adapter.PXLPhotoAdapter
import com.pixlee.pixleesdk.ui.viewholder.PhotoWithImageScaleType
import com.pixlee.pixleesdk.ui.widgets.PXLPhotoView
import com.pixlee.pixleesdk.ui.widgets.list.BaseRecyclerView
import com.pixlee.pixleesdk.ui.widgets.list.ListHeader
import com.pixlee.pixleesdk.ui.widgets.list.ListViewModel
import com.pixlee.pixleesdk.ui.widgets.list.Space
import com.pixlee.pixleesdk.util.AutoPlayUtils
import com.pixlee.pixleesdk.util.EventObserver
import com.pixlee.pixleesdk.util.GridSpacingItemDecoration
import com.pixlee.pixleesdk.util.px
import kotlinx.coroutines.*

/**
 * Created by sungjun on 9/17/20.
 */

class PXLPhotosView : BaseRecyclerView, LifecycleObserver {
    sealed class ViewType {
        class List(val infiniteScroll: Boolean = false,     // or false
                   val autoPlayVideo: Boolean = false,
                   val alphaForStoppedVideos: Float = 1f): ViewType()
        class Grid(val gridSpan: Int = 2,
                   val lineSpace: Space = Space(),
                   val listHeader: ListHeader? = null): ViewType()
    }

    protected val scope = CoroutineScope(Job() + Dispatchers.Main)
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }
    var viewModel = ListViewModel(PXLKtxAlbum(context))

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        scope.cancel()
    }

    var linearLayoutManager: LinearLayoutManager? = null

    fun initView() {
        this.adapter = pxlPhotoAdapter
        addViewModelListeners()
    }

    //    lateinit var masterExoPlayerHelper: MasterExoPlayerHelper
    var viewType: ViewType = ViewType.List()
    fun initiate(widgetTypeForAnalytics: String,
                 viewType: ViewType,
                 showingDebugView: Boolean = false,   // false: for production, true: development only when you want to see the debug info
                 cellHeightInPixel: Int = 200.px.toInt(),
                 params: PXLKtxBaseAlbum.Params,
                 configuration: PXLPhotoView.Configuration = PXLPhotoView.Configuration(),
                 onButtonClickedListener: ((view: View, photoWithImageScaleType: PhotoWithImageScaleType) -> Unit)? = null, // called when a button is clicked
                 onPhotoClickedListener: ((view: View, photoWithImageScaleType: PhotoWithImageScaleType) -> Unit)? = null  // called when a whole view is clicked
    ) {

        albumForAutoAnalytics = AlbumForAutoAnalytics(viewModel.pxlKtxAlbum, widgetTypeForAnalytics)
        this.viewType = viewType
        when (viewType) {
            is ViewType.List -> {
                linearLayoutManager = LinearLayoutManager(context)
                layoutManager = linearLayoutManager
                pxlPhotoAdapter.infiniteScroll = viewType.infiniteScroll
            }
            is ViewType.Grid -> {
                linearLayoutManager = null
                layoutManager = StaggeredGridLayoutManager(viewType.gridSpan, StaggeredGridLayoutManager.VERTICAL)
                addItemDecoration(GridSpacingItemDecoration(viewType.gridSpan, viewType.lineSpace.lineWidthInPixel, viewType.lineSpace.includingEdge, viewType.listHeader != null))
                pxlPhotoAdapter.infiniteScroll = false
            }
        }
        setHasFixedSize(true)

        viewModel.init(params)
        viewModel.customizedConfiguration = configuration
        viewModel.cellHeightInPixel = cellHeightInPixel

        pxlPhotoAdapter.onButtonClickedListener = onButtonClickedListener
        pxlPhotoAdapter.onPhotoClickedListener = onPhotoClickedListener

        addOnChildAttachStateChangeListener(object : RecyclerView.OnChildAttachStateChangeListener {
            override fun onChildViewAttachedToWindow(view: View) {
            }

            override fun onChildViewDetachedFromWindow(view: View) {
                when (viewType){
                    is ViewType.List -> {
                        if (viewType.autoPlayVideo) {
                            val pxlPhotoView = view.findViewById<PXLPhotoView>(R.id.pxlPhotoView)
                            if (pxlPhotoView.hasPlayer()) {
                                view.alpha = viewType.alphaForStoppedVideos
                                pxlPhotoView.pauseVideo()
                            }
                        }
                    }
                }


            }
        })

        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (viewType){
                    is ViewType.List -> {
                        if (viewType.autoPlayVideo && newState == RecyclerView.SCROLL_STATE_IDLE) {
                            playVideoIfneeded(recyclerView)
                        }
                    }
                }

            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                linearLayoutManager?.let {
                    when (viewType){
                        is ViewType.List -> {
                            if (viewType.autoPlayVideo && dy != 0) {
                                AutoPlayUtils.onScrollReleaseAllVideos(recyclerView, R.id.pxlPhotoView, it.findFirstVisibleItemPosition(), it.findLastVisibleItemPosition(), 20, viewType.alphaForStoppedVideos)
                            }
                        }
                    }

                }

            }
        })

        pxlPhotoAdapter.showingDebugView = showingDebugView
        pxlPhotoAdapter.notifyDataSetChanged()

        loadAlbum()
    }

    fun loadAlbum() {
        // retrieve the first page
        scope.launch {
            viewModel.getFirstPage()
        }
    }

    fun addViewModelListeners() {
        val lifecycleOwner = context as? LifecycleOwner ?: throw Exception("androidx.lifecycle.LifecycleOwner is required. Please make sure your Activity or Fragment provides androidx.lifecycle.LifecycleOwner")
        lifecycleOwner.lifecycle.addObserver(this)
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

    override fun setList(type: ListAddType, list: List<PhotoWithImageScaleType>) {
        when (val viewType = this.viewType) {
            is ViewType.List -> {
                var needToMoveScroll = false
                if (list.isNotEmpty()) {
                    needToMoveScroll = pxlPhotoAdapter.list.isEmpty()
                }
                super.setList(type, list)
                moveScrollToInitialPosition(needToMoveScroll)
                fireOpenAndVisible()
            }
            is ViewType.Grid -> {
                clearOldList(type)
                if (pxlPhotoAdapter.list.isEmpty() && viewType.listHeader != null) {
                    pxlPhotoAdapter.list.add(PXLPhotoAdapter.Item.Header(listHeader = viewType.listHeader))
                }
                if (list.isNotEmpty()) {
                    list.forEach {
                        pxlPhotoAdapter.list.add(PXLPhotoAdapter.Item.Content(it))
                    }
                    pxlPhotoAdapter.notifyDataSetChanged()
                }
                fireOpenAndVisible()
            }
        }
    }

    private fun moveScrollToInitialPosition(needToMoveScroll: Boolean) {
        if (needToMoveScroll && pxlPhotoAdapter.infiniteScroll) {
            scrollToPosition(Integer.MAX_VALUE / 2)
        }
    }

    internal fun playVideoIfneeded(recyclerView: RecyclerView) {
        linearLayoutManager?.let {
            when (val viewType = viewType){
                is ViewType.List -> {
                    if (viewType.autoPlayVideo && pxlPhotoAdapter != null && pxlPhotoAdapter.list.isNotEmpty()) {
                        var muted = false
                        if(pxlPhotoAdapter.list.lastOrNull() is PXLPhotoAdapter.Item.Content){
                            (pxlPhotoAdapter.list.lastOrNull() as PXLPhotoAdapter.Item.Content).let {
                                muted = it.data.soundMuted
                            }
                        }
                        AutoPlayUtils.onScrollPlayVideo(recyclerView,
                                R.id.pxlPhotoView,
                                it.findFirstVisibleItemPosition(),
                                it.findLastVisibleItemPosition(),
                                viewType.alphaForStoppedVideos,
                                muted)
                    }
                }
            }
        }

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
        linearLayoutManager?.let {
            when (val viewType = viewType){
                is ViewType.List -> {
                    AutoPlayUtils.releaseAllVideos(this, R.id.pxlPhotoView, it.findFirstVisibleItemPosition(), it.findLastVisibleItemPosition(), viewType.alphaForStoppedVideos)
                }
            }
        }

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
        linearLayoutManager?.let {
            when (val viewType = viewType) {
                is ViewType.List -> {
                    AutoPlayUtils.applyVolume(this, R.id.pxlPhotoView, it.findFirstVisibleItemPosition(), it.findLastVisibleItemPosition(), muted, viewType.alphaForStoppedVideos)
                }
            }
        }
    }
}