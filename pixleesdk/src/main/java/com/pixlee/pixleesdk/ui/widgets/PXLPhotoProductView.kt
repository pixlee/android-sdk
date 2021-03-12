package com.pixlee.pixleesdk.ui.widgets

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pixlee.pixleesdk.R
import com.pixlee.pixleesdk.client.PXLAnalytics
import com.pixlee.pixleesdk.client.PXLClient
import com.pixlee.pixleesdk.data.PXLProduct
import com.pixlee.pixleesdk.data.PXLTimeBasedProduct
import com.pixlee.pixleesdk.ui.adapter.ProductAdapter
import com.pixlee.pixleesdk.ui.viewholder.PhotoWithVideoInfo
import com.pixlee.pixleesdk.ui.viewholder.ProductViewHolder
import com.pixlee.pixleesdk.util.px
import com.pixlee.pixleesdk.util.setCompatIconWithColor
import kotlinx.android.synthetic.main.widget_viewer.view.*
import kotlinx.coroutines.*
import java.util.*

/**
 * this view is supposed to be used in a fullscreen.
 * This view take PXLPhoto to show its content(photo/video), a product list with product's bookmark
 */
class PXLPhotoProductView : FrameLayout, LifecycleObserver {
    class Configuration(
            var backButton: CircleButton? = null,         // back button
            var muteCheckBox: MuteCheckBox? = null      // mute/numute check box
    )

    class CircleButton(
            @DrawableRes var icon: Int = R.drawable.round_close_black_24,
            @ColorInt var iconColor: Int = Color.WHITE,
            @ColorInt var backgroundColor: Int = Color.YELLOW,
            var padding: Int = 20.px.toInt(),
            var onClickListener: (() -> Unit)? = null
    )

    class MuteCheckBox(
            @DrawableRes var mutedIcon: Int = R.drawable.outline_volume_off_black_24,
            @DrawableRes var unmutedIcon: Int = R.drawable.outline_volume_up_black_24,
            @ColorInt var iconColor: Int = Color.WHITE,
            @ColorInt var backgroundColor: Int = Color.YELLOW,
            var padding: Int = 20.px.toInt(),
            var onCheckedListener: ((isChecked: Boolean) -> Unit)? = null
    )

    private var adapter: ProductAdapter? = null
    private var photoInfo: PhotoWithVideoInfo? = null
    var bookmarkMap: HashMap<String, Boolean>? = null
    var onBookmarkClicked: ((productId: String, isBookmarkChecked: Boolean) -> Unit)? = null
    var onProductClicked: ((pxlProduct: PXLProduct) -> Unit)? = null

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context)
    }

    constructor(context: Context) : super(context, null) {
        initView(context)
    }

    private fun initView(context: Context) {
        val li = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = li.inflate(R.layout.widget_viewer, this, false)
        addView(view)
        addScrollListener()
    }

    var scrollState = RecyclerView.SCROLL_STATE_IDLE
    fun addScrollListener() {
        list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                scrollState = RecyclerView.SCROLL_STATE_IDLE
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

            }
        })
    }

    /**
     * Start the UI
     * @param photoInfo: PhotoWithVideoInfo
     * @param bookmarkMap: user's current bookmarks < Product id: String, is bookmarked: Boolean >
     *                      if null, hide bookmark toggle
     *                      if not null, show bookmark toggle
     * @param onBookmarkClicked {productId: String, isBookmarkChecked: Boolean -> ... }
     */
    fun setContent(photoInfo: PhotoWithVideoInfo,
                   headerConfiguration: Configuration = Configuration(),
                   configuration: ProductViewHolder.Configuration = ProductViewHolder.Configuration(),
                   bookmarkMap: HashMap<String, Boolean>? = null,
                   onBookmarkClicked: ((productId: String, isBookmarkChecked: Boolean) -> Unit)? = null,
                   onProductClicked: ((pxlProduct: PXLProduct) -> Unit)? = null): PXLPhotoProductView {
        this.photoInfo = photoInfo
        this.bookmarkMap = bookmarkMap
        this.onBookmarkClicked = onBookmarkClicked
        this.onProductClicked = onProductClicked
        isMutted = photoInfo.soundMuted
        initHeader(headerConfiguration)
        loadProducts(configuration)

        pxlPhotoView.setConfiguration(configuration = PXLPhotoView.Configuration(pxlPhotoSize = photoInfo.configuration.pxlPhotoSize))
        pxlPhotoView.setContent(photoInfo.pxlPhoto, photoInfo.configuration.imageScaleType)
        pxlPhotoView.setLooping(photoInfo.isLoopingVideo)
        pxlPhotoView.changeVolume(if (photoInfo.soundMuted) 0f else 1f)

        fireAnalyticsOpenLightbox()
        return this
    }

    private var headerConfiguration: Configuration? = null
    private fun initHeader(headerConfiguration: Configuration) {
        this.headerConfiguration = headerConfiguration
        vBack.visibility = if (headerConfiguration.backButton != null) View.VISIBLE else View.GONE
        headerConfiguration.backButton?.apply {
            vBack.setOnClickListener { onClickListener?.let { it() } }
            vBack.background = GradientDrawable().apply {
                shape = android.graphics.drawable.GradientDrawable.OVAL
                setColor(backgroundColor)
            }
            vBack.setPadding(padding, padding, padding, padding)
            ivBack.setCompatIconWithColor(iconColor, icon)
        }

        vMute.visibility = if (headerConfiguration.muteCheckBox != null && photoInfo?.pxlPhoto?.isVideo ?: false) View.VISIBLE else View.GONE
        headerConfiguration.muteCheckBox?.apply {
            vMute.setOnClickListener {
                if (isMutted) {
                    unmute()
                } else {
                    mute()
                }
                onCheckedListener?.let { it1 -> it1(isMutted) }
            }
            vMute.background = GradientDrawable().apply {
                shape = android.graphics.drawable.GradientDrawable.OVAL
                setColor(backgroundColor)
            }
            vMute.setPadding(padding, padding, padding, padding)
            setMuteIcon(isMutted)
        }
    }

    /**
     * this adds padding to top header.
     * The content will not be affected by this padding because the header area and the content are not related in a ViewGroup.
     */
    fun addPaddingToHeader(left: Int, top: Int, right: Int, bottom: Int) {
        headerView.setPadding(left, top, right, bottom)
    }

    var productIndexMap = HashMap<String, Int>()
    private fun loadProducts(configuration: ProductViewHolder.Configuration) {
        // initiate the product list view
        photoInfo?.pxlPhoto?.also { photo ->
            photo.products?.also { products ->
                val videoTimestampMap = HashMap<String, PXLTimeBasedProduct>() // map <productId: Long, PXLVideoTime>
                // register video timestamps in a map
                photo.time_based_products?.forEach {
                    videoTimestampMap[it.productId] = it
                }

                // register product ids' positions in a map
                photo.products.forEachIndexed { index, pxlProduct ->
                    productIndexMap[pxlProduct.id] = index
                }

                adapter = ProductAdapter(
                        configuration = configuration,
                        list = products,
                        timeBasedProductMap = videoTimestampMap,
                        bookmarkMap = bookmarkMap,
                        onBookmarkChanged = { productId, isBookmarkChecked ->
                            onBookmarkClicked?.let { it -> it(productId, isBookmarkChecked) }
                        },
                        onTimestampClicked = { videoTimestamp ->
                            pxlPhotoView.seekTo(videoTimestamp.timestamp * 1000)
                        },
                        onItemClicked = { product ->
                            onProductClicked?.let { it -> it(product) }
                        }
                )



                list.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                list.adapter = adapter
            }
        }
    }

    var isMutted: Boolean = false

    /**
     * mute the sound
     */
    fun mute() {
        changeMute(true)
    }

    /**
     * unmute the sound
     */
    fun unmute() {
        changeMute(false)
    }

    private fun changeMute(muted: Boolean) {
        isMutted = muted
        pxlPhotoView.changeVolume(if (muted) 0f else 1f)
        setMuteIcon(muted)
    }

    private fun setMuteIcon(muted: Boolean) {
        headerConfiguration?.muteCheckBox?.apply {
            ivMute.setCompatIconWithColor(iconColor, if (muted) mutedIcon else unmutedIcon)
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

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun playVideoOnStart() {
        playVideoOnResume()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun playVideoOnResume() {
        fireAnalyticsOpenLightbox()
        pxlPhotoView.playVideo()
        GlobalScope.launch {
            async {
                videoTimer?.stop()
                videoTimer = VideoTimer()
                videoTimer?.start {
                    val timestamp = pxlPhotoView.getTimestamp()
                    val time = String.format(Locale.US, "%02d:%02d", timestamp / 60, timestamp % 60)
                    launch(Dispatchers.Main) {
                        tvDebugTimerTextViewer.text = time
                    }
                    photoInfo?.pxlPhoto?.time_based_products?.forEach {
                        if (it.timestamp == timestamp) {
                            val productPosition = productIndexMap[it.productId] ?: -1
                            if (productPosition > -1) {
                                launch(Dispatchers.Main) {
                                    if (scrollState == RecyclerView.SCROLL_STATE_IDLE) {
                                        list.smoothScrollToPosition(productPosition)
                                        Log.e("videoTimer", "### PLAY (${it.timestamp})###")
                                    } else {
                                        Log.e("videoTimer", "### PLAY ignored because you're scrolling the list (${it.timestamp})###")
                                    }
                                }
                                return@forEach
                            }
                        }
                    }
                }
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun stopVideoOnPause() {
        pxlPhotoView.pauseVideo()
        videoTimer?.stop()
        videoTimer = null
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stopVideoOnStop() {
        stopVideoOnPause()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopVideoOnPause()
    }

    private var isAnalyticsOpenLightboxFired = false

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun fireAnalyticsOpenLightbox() {
        if (PXLClient.autoAnalyticsEnabled && !isAnalyticsOpenLightboxFired) {
            if (photoInfo?.pxlPhoto == null) {
                Log.e(PXLAnalytics.TAG, "can't fire OpenLightbox analytics event because photoInfo is null")
                return
            }

            isAnalyticsOpenLightboxFired = true
            GlobalScope.launch {
                photoInfo?.pxlPhoto?.also { pxlPhoto ->
                    try {
                        PXLClient.getInstance(context).ktxAnalyticsDataSource.openedLightbox(pxlPhoto.albumId, pxlPhoto.albumPhotoId)
                    } catch (e: Exception) {
                        isAnalyticsOpenLightboxFired = false
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    var videoTimer: VideoTimer? = null
}

class VideoTimer {
    var canWork = true
    suspend fun start(listener: () -> Unit) {
        while (canWork) {
            listener()
            delay(1000)
        }
    }

    fun stop() {
        canWork = false
    }
}
