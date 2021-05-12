package com.pixlee.pixleesdk.ui.widgets

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.view.ViewCompat
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.resource.SimpleResource
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.pixlee.pixleesdk.R
import com.pixlee.pixleesdk.client.PXLAnalytics
import com.pixlee.pixleesdk.client.PXLClient
import com.pixlee.pixleesdk.data.PXLProduct
import com.pixlee.pixleesdk.enums.PXLPhotoSize
import com.pixlee.pixleesdk.ui.adapter.ProductAdapter
import com.pixlee.pixleesdk.ui.viewholder.PhotoWithVideoInfo
import com.pixlee.pixleesdk.ui.viewholder.ProductViewHolder
import com.pixlee.pixleesdk.util.HotspotsReader
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
            @DrawableRes var icon: Int = R.drawable.round_close_black_18,
            @ColorInt var iconColor: Int = Color.BLACK,
            @ColorInt var backgroundColor: Int = Color.WHITE,
            var padding: Int = 10.px.toInt(),
            var onClickListener: (() -> Unit)? = null
    )

    class MuteCheckBox(
            @DrawableRes var mutedIcon: Int = R.drawable.outline_volume_off_black_18,
            @DrawableRes var unmutedIcon: Int = R.drawable.outline_volume_up_black_18,
            @ColorInt var iconColor: Int = Color.BLACK,
            @ColorInt var backgroundColor: Int = Color.WHITE,
            var padding: Int = 10.px.toInt(),
            var onCheckedListener: ((isChecked: Boolean) -> Unit)? = null
    )

    protected val scope = CoroutineScope(Job() + Dispatchers.Main)

    private var adapter: ProductAdapter? = null
    private var photoInfo: PhotoWithVideoInfo? = null
    var bookmarkMap: HashMap<String, Boolean>? = null
    var onBookmarkClicked: ((productId: String, isBookmarkChecked: Boolean) -> Unit)? = null
    var onProductClicked: ((pxlProduct: PXLProduct) -> Unit)? = null
    var isMutted: Boolean = false
    var useHotspots: Boolean = false

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        hotspotsJob?.cancel()
        scope.cancel()
    }

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

        val lifecycleOwner = context as? LifecycleOwner
                ?: throw Exception("androidx.lifecycle.LifecycleOwner is required. Please make sure your Activity or Fragment provides androidx.lifecycle.LifecycleOwner")
        lifecycleOwner.lifecycle.addObserver(this)
    }

    // HashMap <productId: the position of product list>
    val hotspotMap = HashMap<String, Int>()

    /**
     * Start the UI
     * @param photoInfo: PhotoWithVideoInfo
     * @param bookmarkMap: user's current bookmarks < Product id: String, is bookmarked: Boolean >
     *                      if null, hide bookmark toggle
     *                      if not null, show bookmark toggle
     * @param onBookmarkClicked {productId: String, isBookmarkChecked: Boolean -> ... }
     */
    fun setContent(photoInfo: PhotoWithVideoInfo,
                   showHotspots: Boolean = true,
                   headerConfiguration: Configuration = Configuration(),
                   configuration: ProductViewHolder.Configuration = ProductViewHolder.Configuration(),
                   bookmarkMap: HashMap<String, Boolean>? = null,
                   onBookmarkClicked: ((productId: String, isBookmarkChecked: Boolean) -> Unit)? = null,
                   onProductClicked: ((pxlProduct: PXLProduct) -> Unit)? = null): PXLPhotoProductView {
        this.photoInfo = photoInfo
        this.useHotspots = showHotspots
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

        addHotspots()

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

    private fun loadProducts(configuration: ProductViewHolder.Configuration) {
        // initiate the product list view
        photoInfo?.pxlPhoto?.also {
            it.products?.also { products ->
                adapter = ProductAdapter(
                        configuration = configuration,
                        list = products,
                        bookmarkMap = bookmarkMap,
                        onBookmarkChanged = { productId, isBookmarkChecked ->
                            onBookmarkClicked?.let { it -> it(productId, isBookmarkChecked) }
                        },
                        onItemClicked = { product ->
                            onProductClicked?.let { it -> it(product) }
                        }
                )



                recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                recyclerView.adapter = adapter
            }
        }
    }

    private var hiddenHotspots = false
    private var hotspotsJob: Job? = null
    private fun addHotspots() {
        // video does not have hotspots.
        if (!useHotspots || photoInfo?.pxlPhoto?.isVideo == true) return

        hotspotsJob?.cancel()
        hotspotsJob = scope.launch {
            withContext(Dispatchers.IO) {
                // delay below for other elements like products, and the main content to be loaded with the maximum resources
                delay(1000)
            }

            // since this is run after the delay run on a background thread, v_hotspots could be null. so null check is essential.
            if (v_hotspots == null) return@launch

            if (v_hotspots.childCount > 0) {
                // remove all child views if this is not the first trial
                v_hotspots.removeAllViews()
            }

            v_hotspots.setOnClickListener {
                hiddenHotspots = !hiddenHotspots
                val visibility = if (hiddenHotspots) GONE else VISIBLE
                val childCount = v_hotspots.childCount
                if (childCount > 0) {
                    for (i in 0 until childCount) {
                        v_hotspots.getChildAt(i).visibility = visibility
                    }
                }
            }

            photoInfo?.pxlPhoto?.products?.forEachIndexed { index, pxlProduct ->
                hotspotMap[pxlProduct.id] = index
            }
            photoInfo?.pxlPhoto?.boundingBoxProducts?.let { boundingBoxProducts ->
                context?.let { context ->

                    /*
                       Always read original url to get content's width and height
                       for calculating the positions of hotspots on the screen
                       because bounding_box_products's x, y, with, height are generated
                       ased on the original content's width height from Control Panel.
                     */
                    val originalImageUrl = photoInfo?.pxlPhoto?.getUrlForSize(PXLPhotoSize.ORIGINAL).toString()
                    Glide.with(getContext().applicationContext)
                            .asBitmap()
                            .load(originalImageUrl)
                            .into(object : SimpleTarget<Bitmap>() {
                                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                    if (v_hotspots == null) return

                                    photoInfo?.configuration?.imageScaleType?.let { imageScaleType ->
                                        val reader = HotspotsReader(imageScaleType,
                                                pxlPhotoView.measuredWidth, pxlPhotoView.measuredHeight,
                                                resource.width, resource.height
                                        )

                                        // draw all hotspots
                                        boundingBoxProducts.forEach { boundingBoxProduct ->
                                            val imageView = ImageView(context).apply {
                                                layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
                                                setImageResource(R.drawable.outline_local_offer_black_24)
                                                background = GradientDrawable().apply {
                                                    shape = android.graphics.drawable.GradientDrawable.OVAL
                                                    setColor(Color.WHITE)
                                                }
                                                val padding = 10.px.toInt()
                                                setPadding(padding, padding, padding, padding)
                                                ViewCompat.setElevation(this, 20f)
                                                val position = reader.getHotspotsPosition(boundingBoxProduct)
                                                apply {
                                                    doOnPreDraw {
                                                        x = position.x - (width.toFloat() / 2f)
                                                        y = position.y - (height.toFloat() / 2f)
                                                    }
                                                }
                                            }

                                            v_hotspots.addView(imageView)

                                            // on hotspot clicked
                                            imageView.setOnClickListener {
                                                if (recyclerView == null) return@setOnClickListener
                                                hotspotMap[boundingBoxProduct.productId]?.let { position ->
                                                    recyclerView.smoothScrollToPosition(position)
                                                }
                                            }
                                        }

                                    }
                                }
                            })
                    Log.e("PXLPPV", "start loading image")
                }

            }

        }
    }

    class Size(val width: Int, val height: Int)
    class OptionsSizeResourceTranscoder : ResourceTranscoder<BitmapFactory.Options, Size> {
        val id: String
            get() = javaClass.name

        override fun transcode(toTranscode: Resource<BitmapFactory.Options>, options: Options): Resource<Size>? {
            val options: BitmapFactory.Options = toTranscode.get()
            val size = Size(options.outWidth, options.outHeight)
            return SimpleResource<Size>(size)
        }
    }

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

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun playVideoOnStart() {
        playVideoOnResume()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun playVideoOnResume() {
        fireAnalyticsOpenLightbox()
        pxlPhotoView.playVideo()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun stopVideoOnPause() {
        pxlPhotoView.pauseVideo()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stopVideoOnStop() {
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
}
