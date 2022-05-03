package com.pixlee.pixleesdk.ui.widgets.list

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Toast
import androidx.lifecycle.*
import androidx.recyclerview.widget.*
import com.pixlee.pixleesdk.R
import com.pixlee.pixleesdk.client.PXLKtxAlbum
import com.pixlee.pixleesdk.client.PXLKtxBaseAlbum
import com.pixlee.pixleesdk.ui.adapter.PXLPhotoAdapter
import com.pixlee.pixleesdk.ui.decoration.GridSpacingItemDecoration
import com.pixlee.pixleesdk.ui.decoration.HorizontalSpacingItemDecoration
import com.pixlee.pixleesdk.ui.decoration.SpannedGridSpacingItemDecoration
import com.pixlee.pixleesdk.ui.viewholder.PhotoWithImageScaleType
import com.pixlee.pixleesdk.ui.widgets.PXLPhotoView
import com.pixlee.pixleesdk.ui.widgets.TextViewStyle
import com.pixlee.pixleesdk.ui.widgets.mosaic.SpanSize
import com.pixlee.pixleesdk.ui.widgets.mosaic.SpannedGridLayoutManager
import com.pixlee.pixleesdk.util.*
import kotlinx.coroutines.*


/**
 * Created by sungjun on 9/17/20.
 */

class PXLWidgetView : BaseRecyclerView, LifecycleObserver {
    sealed class ViewType {
        data class List(val cellHeightInPixel: Int = 200.px.toInt(),
                        val infiniteScroll: Boolean = false,     // or false
                        val autoPlayVideo: Boolean = false,
                        val alphaForStoppedVideos: Float = 1f) : ViewType()

        data class Grid(val cellHeightInPixel: Int = 200.px.toInt(),
                        var gridSpan: Int = 2,
                        var lineSpace: Space = Space(),
                        var listHeader: ListHeader? = null) : ViewType()

        data class Mosaic(
            var gridSpan: Int = 4,
            var lineSpace: Space = Space()) : ViewType()

        data class Horizontal(var squareSizeInPixel: Int = 100.px.toInt(),
                              var lineWidthInPixel: Int = 4.px.toInt()) : ViewType()
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
    var spannedGridLayoutManager: SpannedGridLayoutManager? = null
    var gridLayoutManager: GridLayoutManager? = null
    var gridSpacingDecoration: GridSpacingItemDecoration? = null
    var horizontalSpacingItemDecoration: HorizontalSpacingItemDecoration? = null
    var spannedGridSpacingDecoration: SpannedGridSpacingItemDecoration? = null
    fun initView() {
        this.adapter = pxlPhotoAdapter
        addViewModelListeners()
    }

    var currentViewType: ViewType = ViewType.List()
        set(value) {
            field = value

            updateCellHeightInViewModel(value)
            updateViewHeight(value)


            // clear unused variables
            if (value !is ViewType.List || value !is ViewType.Horizontal) {
                linearLayoutManager = null
            }

            if (value !is ViewType.Horizontal) {
                horizontalSpacingItemDecoration?.apply { removeItemDecoration(this) }
                horizontalSpacingItemDecoration = null
            }

            if (value !is ViewType.Mosaic) {
                spannedGridLayoutManager = null
                spannedGridSpacingDecoration?.apply { removeItemDecoration(this) }
                spannedGridSpacingDecoration = null
            }

            if (value !is ViewType.Grid) {
                gridLayoutManager = null
                gridSpacingDecoration?.apply { removeItemDecoration(this) }
                gridSpacingDecoration = null
            }

            // remove header
            when(value){
                is ViewType.Mosaic, is ViewType.List, is ViewType.Horizontal -> {
                    if (pxlPhotoAdapter.list.isNotEmpty() && pxlPhotoAdapter.list[0] is PXLPhotoAdapter.Item.Header) {
                        pxlPhotoAdapter.list.removeAt(0)
                        pxlPhotoAdapter.notifyItemRemoved(0)
                    }
                }
            }

            // initiate PXLWidgetView or update the UI with PXLWidgetView's configs
            when (value) {
                is ViewType.List -> {
                    linearLayoutManager = LinearLayoutManager(context).apply {
                        layoutManager = this
                    }
                    pxlPhotoAdapter.infiniteScroll = value.infiniteScroll
                }
                is ViewType.Horizontal -> {
                    if (linearLayoutManager == null) {
                        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false).apply {
                            layoutManager = this
                        }
                    }

                    if (horizontalSpacingItemDecoration == null) {
                        horizontalSpacingItemDecoration = HorizontalSpacingItemDecoration(value.lineWidthInPixel).apply {
                            addItemDecoration(this)
                        }
                    } else {
                        horizontalSpacingItemDecoration?.spacingPx = value.lineWidthInPixel
                    }

                    pxlPhotoAdapter.infiniteScroll = false
                }
                is ViewType.Grid -> {
                    if (pxlPhotoAdapter.list.isNotEmpty() && value.listHeader != null && pxlPhotoAdapter.list[0] is PXLPhotoAdapter.Item.Header) {
                        val headerOld = (pxlPhotoAdapter.list[0] as PXLPhotoAdapter.Item.Header).listHeader
                        val headerNew = value.listHeader!!
                        if ((headerOld is ListHeader.SpannableText && headerNew is ListHeader.Gif)
                                || (headerOld is ListHeader.Gif && headerNew is ListHeader.SpannableText)) {
                            pxlPhotoAdapter.list.removeAt(0)
                            pxlPhotoAdapter.notifyItemRemoved(0)
                            pxlPhotoAdapter.list.add(0, PXLPhotoAdapter.Item.Header(listHeader = value.listHeader!!))
                            pxlPhotoAdapter.notifyItemInserted(0)
                        }

                    } else if (pxlPhotoAdapter.list.isNotEmpty() && value.listHeader != null && pxlPhotoAdapter.list[0] !is PXLPhotoAdapter.Item.Header) {
                        pxlPhotoAdapter.list.add(0, PXLPhotoAdapter.Item.Header(listHeader = value.listHeader!!))
                        pxlPhotoAdapter.notifyItemInserted(0)
                    } else if (pxlPhotoAdapter.list.isNotEmpty() && value.listHeader == null && pxlPhotoAdapter.list[0] is PXLPhotoAdapter.Item.Header) {
                        pxlPhotoAdapter.list.removeAt(0)
                        pxlPhotoAdapter.notifyItemRemoved(0)
                    }

                    if (gridLayoutManager == null) {
                        gridLayoutManager = GridLayoutManager(context, value.gridSpan).apply {
                            layoutManager = this
                        }
                        gridLayoutManager?.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                            override fun getSpanSize(position: Int): Int {
                                return if (pxlPhotoAdapter.list[position] is PXLPhotoAdapter.Item.Header || pxlPhotoAdapter.list[position] is PXLPhotoAdapter.Item.LoadMore) {
                                    val viewType = currentViewType
                                    if (viewType is ViewType.Grid) {
                                        viewType.gridSpan
                                    } else {
                                        1
                                    }

                                } else {
                                    1
                                }
                            }
                        }
                    } else {
                        gridLayoutManager?.spanCount = value.gridSpan
                    }

                    if (gridSpacingDecoration == null) {
                        gridSpacingDecoration = GridSpacingItemDecoration(value.gridSpan, value.lineSpace.lineWidthInPixel, value.lineSpace.includingEdge, value.listHeader != null).apply {
                            addItemDecoration(this)
                        }
                    } else {
                        gridSpacingDecoration?.spanCount = value.gridSpan
                        gridSpacingDecoration?.spacingPx = value.lineSpace.lineWidthInPixel
                        gridSpacingDecoration?.includingEdge = value.lineSpace.includingEdge

                        gridSpacingDecoration?.includingTitle = value.listHeader != null
                    }

                    pxlPhotoAdapter.infiniteScroll = false
                }
                is ViewType.Mosaic -> {
                    if (spannedGridLayoutManager == null) {
                        spannedGridLayoutManager = SpannedGridLayoutManager(orientation = SpannedGridLayoutManager.Orientation.VERTICAL, value.gridSpan).apply {
                            layoutManager = this
                        }
                    }

                    if (spannedGridSpacingDecoration == null) {
                        spannedGridSpacingDecoration = SpannedGridSpacingItemDecoration(spannedGridLayoutManager!!, value.gridSpan, value.lineSpace.lineWidthInPixel, value.lineSpace.includingEdge).apply {
                            addItemDecoration(this)
                        }
                    } else {
                        spannedGridSpacingDecoration?.spanCount = value.gridSpan
                        spannedGridSpacingDecoration?.spacingPx = value.lineSpace.lineWidthInPixel
                        spannedGridSpacingDecoration?.includingEdge = value.lineSpace.includingEdge
                    }

                    spannedGridLayoutManager?.spanSizeLookup = SpannedGridLayoutManager.SpanSizeLookup { position ->
                        val item = pxlPhotoAdapter.list[position]
                        when (item){
                            is PXLPhotoAdapter.Item.Content -> {
                                val itemType = item.itemType
                                if (itemType is PXLPhotoAdapter.ItemType.Mosaic && itemType.isLarge){
                                    SpanSize(2, 2)
                                } else {
                                    SpanSize(1, 1)
                                }
                            }
                            else -> {
                                SpanSize(value.gridSpan, 1)
                            }
                        }

                    }

                    pxlPhotoAdapter.infiniteScroll = false
                }
            }
            pxlPhotoAdapter.notifyDataSetChanged()
            post {
                updateItemTypeOnListData(value)
                pxlPhotoAdapter.notifyDataSetChanged()
            }
        }

    fun initiate(widgetTypeForAnalytics: String,
                 viewType: ViewType,
                 sourceIconColor: Int? = null,
                 apiParameters: PXLKtxBaseAlbum.Params,
                 configuration: PXLPhotoView.Configuration = PXLPhotoView.Configuration(),
                 loadMoreTextViewStyle: TextViewStyle? = null, // if null, the view is gone
                 onButtonClickedListener: ((view: View, photoWithImageScaleType: PhotoWithImageScaleType) -> Unit)? = null, // called when a button is clicked
                 onPhotoClickedListener: ((view: View, photoWithImageScaleType: PhotoWithImageScaleType) -> Unit)? = null  // called when a whole view is clicked
    ) {

        albumForAutoAnalytics = AlbumForAutoAnalytics(viewModel.pxlKtxAlbum, widgetTypeForAnalytics)
        this.currentViewType = viewType

        setHasFixedSize(true)

        viewModel.init(apiParameters)
        viewModel.customizedConfiguration = configuration
        updateCellHeightInViewModel(viewType)

        viewModel.loadMoreTextViewStyle = loadMoreTextViewStyle

        pxlPhotoAdapter.onButtonClickedListener = onButtonClickedListener
        pxlPhotoAdapter.onPhotoClickedListener = onPhotoClickedListener
        pxlPhotoAdapter.onLoadMoreClickedListener = {
            scope.launch {
                viewModel?.getNextPage()
            }
        }

        addOnChildAttachStateChangeListener(object : RecyclerView.OnChildAttachStateChangeListener {
            override fun onChildViewAttachedToWindow(view: View) {
            }

            override fun onChildViewDetachedFromWindow(view: View) {
                val viewType = currentViewType
                when (viewType) {
                    is ViewType.List -> {
                        if (viewType.autoPlayVideo) {
                            val pxlPhotoView = view.findViewById<PXLPhotoView>(R.id.pxlPhotoView)
                            if (pxlPhotoView!=null && pxlPhotoView.hasPlayer()) {
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
                val viewType = currentViewType
                when (viewType) {
                    is ViewType.List -> {
                        if (viewType.autoPlayVideo && newState == RecyclerView.SCROLL_STATE_IDLE) {
                            playVideoIfneeded(recyclerView)
                        }
                    }
                }

            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                linearLayoutManager?.let {
                    val viewType = currentViewType
                    when (viewType) {
                        is ViewType.List -> {
                            if (viewType.autoPlayVideo && dy != 0) {
                                AutoPlayUtils.onScrollReleaseAllVideos(recyclerView, R.id.pxlPhotoView, it.findFirstVisibleItemPosition(), it.findLastVisibleItemPosition(), 20, viewType.alphaForStoppedVideos)
                            }
                        }
                    }

                }

            }
        })

        pxlPhotoAdapter.sourceIconColor = sourceIconColor
        pxlPhotoAdapter.notifyDataSetChanged()

        loadAlbum()
    }

    fun updateViewHeight(viewType: ViewType){
        when(viewType){
            is ViewType.List -> this.layoutParams.height = LayoutParams.MATCH_PARENT
            is ViewType.Grid -> this.layoutParams.height = LayoutParams.MATCH_PARENT
            is ViewType.Mosaic -> this.layoutParams.height = LayoutParams.MATCH_PARENT
            is ViewType.Horizontal -> this.layoutParams.height = viewType.squareSizeInPixel
        }
    }

    fun updateCellHeightInViewModel(viewType: ViewType){
        viewModel.cellHeightInPixel = when(viewType){
            is ViewType.List -> viewType.cellHeightInPixel
            is ViewType.Grid -> viewType.cellHeightInPixel
            is ViewType.Horizontal -> viewType.squareSizeInPixel
            else -> 0
        }
    }

    fun updateItemTypeOnListData(viewType: ViewType) {
        pxlPhotoAdapter.list.forEach { item ->
            when(item) {
                is PXLPhotoAdapter.Item.LoadMore -> {
                    item.width = if(viewType is ViewType.Horizontal) viewType.squareSizeInPixel else null
                    item.itemType = getItemType(viewType)
                }
                is PXLPhotoAdapter.Item.Content -> {
                    item.itemType = when(viewType) {
                        is ViewType.List -> PXLPhotoAdapter.ItemType.List
                        is ViewType.Grid -> PXLPhotoAdapter.ItemType.Grid
                        is ViewType.Mosaic -> generateRandomMosaicCellSize()
                        is ViewType.Horizontal -> PXLPhotoAdapter.ItemType.Horizontal
                    }

                    when(viewType) {
                        is ViewType.List -> item.data.heightInPixel = viewType.cellHeightInPixel
                        is ViewType.Grid -> item.data.heightInPixel = viewType.cellHeightInPixel
                        is ViewType.Horizontal -> item.data.heightInPixel = viewType.squareSizeInPixel
                    }
                }
            }
        }

    }

    fun getItemType(viewType: ViewType? = currentViewType): PXLPhotoAdapter.ItemType {
        return when(viewType){
            is ViewType.List -> PXLPhotoAdapter.ItemType.List
            is ViewType.Grid -> PXLPhotoAdapter.ItemType.Grid
            is ViewType.Mosaic -> generateRandomMosaicCellSize()
            is ViewType.Horizontal -> PXLPhotoAdapter.ItemType.Horizontal
            else -> PXLPhotoAdapter.ItemType.List
        }
    }

    fun generateRandomMosaicCellSize(): PXLPhotoAdapter.ItemType.Mosaic {
        return PXLPhotoAdapter.ItemType.Mosaic(isLarge = (0..1).random() == 0)
    }

    fun loadAlbum() {
        // retrieve the first page
        scope.launch {
            viewModel.getFirstPage()
        }
    }

    fun addViewModelListeners() {
        val lifecycleOwner = context as? LifecycleOwner
                ?: throw Exception("androidx.lifecycle.LifecycleOwner is required. Please make sure your Activity or Fragment provides androidx.lifecycle.LifecycleOwner")
        lifecycleOwner.lifecycle.addObserver(this)

        viewModel.loading.observe(lifecycleOwner, Observer {
            val loadingWidth = when (val viewType = currentViewType) {
                is ViewType.Horizontal -> viewType.squareSizeInPixel
                else -> null
            }

            when (it) {
                is ListViewModel.LoadState.Hide -> {
                    val lastItem = pxlPhotoAdapter.list.lastOrNull()
                    if (lastItem != null && lastItem is PXLPhotoAdapter.Item.LoadMore) {
                        val index = pxlPhotoAdapter.list.count() - 1
                        pxlPhotoAdapter.list.removeAt(index)
                        pxlPhotoAdapter.notifyItemRemoved(index)
                    }
                }

                is ListViewModel.LoadState.LoadMoreButton -> {
                    val lastPosition = pxlPhotoAdapter.list.count() - 1
                    val lastItem = pxlPhotoAdapter.list.lastOrNull()
                    if (lastItem == null || lastItem !is PXLPhotoAdapter.Item.LoadMore) {
                        pxlPhotoAdapter.list.add(PXLPhotoAdapter.Item.LoadMore(loading = false, loadMoreTextViewStyle = it.loadMoreTextViewStyle, itemType = getItemType(), width = loadingWidth))
                        pxlPhotoAdapter.notifyItemInserted(pxlPhotoAdapter.list.size - 1)
                    } else {
                        if(lastItem.loading){
                            lastItem.loading = false
                            pxlPhotoAdapter.notifyItemChanged(lastPosition)
                        }
                    }
                }
                is ListViewModel.LoadState.Loading -> {
                    val lastPosition = pxlPhotoAdapter.list.count() - 1
                    val lastItem = pxlPhotoAdapter.list.lastOrNull()
                    if (lastItem == null || lastItem !is PXLPhotoAdapter.Item.LoadMore) {
                        pxlPhotoAdapter.list.add(PXLPhotoAdapter.Item.LoadMore(loading = true, loadMoreTextViewStyle = it.loadMoreTextViewStyle, itemType = getItemType(), width = loadingWidth))
                        pxlPhotoAdapter.notifyItemInserted(pxlPhotoAdapter.list.size - 1)
                    } else {
                        if(!lastItem.loading){
                            lastItem.loading = true
                            pxlPhotoAdapter.notifyItemChanged(lastPosition)
                        }
                    }
                }
            }
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
        when (val viewType = this.currentViewType) {
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
                var lastItem = pxlPhotoAdapter.list.lastOrNull()
                if(lastItem!=null && lastItem is PXLPhotoAdapter.Item.LoadMore){
                    val position = pxlPhotoAdapter.list.count() - 1
                    pxlPhotoAdapter.list.removeAt(position)
                    pxlPhotoAdapter.notifyItemRemoved(position)
                } else {
                    lastItem = null
                }

                if (pxlPhotoAdapter.list.isEmpty() && viewType.listHeader != null) {
                    val position = pxlPhotoAdapter.list.count()
                    pxlPhotoAdapter.list.add(PXLPhotoAdapter.Item.Header(listHeader = viewType.listHeader!!))
                    pxlPhotoAdapter.notifyItemInserted(position)
                }
                if (list.isNotEmpty()) {
                    val position = pxlPhotoAdapter.list.count()
                    list.forEach {
                        pxlPhotoAdapter.list.add(PXLPhotoAdapter.Item.Content(it, PXLPhotoAdapter.ItemType.Grid))
                    }
                    lastItem?.also {
                        pxlPhotoAdapter.list.add(it)
                    }
                    pxlPhotoAdapter.notifyItemRangeInserted(position, pxlPhotoAdapter.list.count() - position)
                }
                fireOpenAndVisible()
            }
            is ViewType.Mosaic, is ViewType.Horizontal -> {
                clearOldList(type)
                var lastItem = pxlPhotoAdapter.list.lastOrNull()
                if(lastItem!=null && lastItem is PXLPhotoAdapter.Item.LoadMore){
                    val position = pxlPhotoAdapter.list.count() - 1
                    pxlPhotoAdapter.list.removeAt(position)
                    pxlPhotoAdapter.notifyItemRemoved(position)
                } else {
                    lastItem = null
                }

                if (list.isNotEmpty()) {
                    val position = pxlPhotoAdapter.list.count()
                    list.forEach {
                        val itemType = if (viewType is ViewType.Mosaic) generateRandomMosaicCellSize() else PXLPhotoAdapter.ItemType.Horizontal
                        pxlPhotoAdapter.list.add(PXLPhotoAdapter.Item.Content(it, itemType))
                    }
                    lastItem?.also {
                        pxlPhotoAdapter.list.add(it)
                    }
                    pxlPhotoAdapter.notifyItemRangeInserted(position, pxlPhotoAdapter.list.count() - position)
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
            when (val viewType = currentViewType) {
                is ViewType.List -> {
                    if (viewType.autoPlayVideo && pxlPhotoAdapter != null && pxlPhotoAdapter.list.isNotEmpty()) {
                        var muted = false
                        if (pxlPhotoAdapter.list.lastOrNull() is PXLPhotoAdapter.Item.Content) {
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
            when (val viewType = currentViewType) {
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
            when (val viewType = currentViewType) {
                is ViewType.List -> {
                    AutoPlayUtils.applyVolume(this, R.id.pxlPhotoView, it.findFirstVisibleItemPosition(), it.findLastVisibleItemPosition(), muted, viewType.alphaForStoppedVideos)
                }
            }
        }
    }
}