package com.pixlee.pixleesdk.ui.widgets

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.LinearLayoutManager
import com.pixlee.pixleesdk.R
import com.pixlee.pixleesdk.data.PXLProduct
import com.pixlee.pixleesdk.ui.adapter.ProductAdapter
import com.pixlee.pixleesdk.ui.viewholder.PhotoWithVideoInfo
import com.pixlee.pixleesdk.ui.viewholder.ProductViewHolder
import com.pixlee.pixleesdk.util.px
import com.pixlee.pixleesdk.util.setCompatIconWithColor
import kotlinx.android.synthetic.main.widget_viewer.view.*
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
                   onProductClicked: ((pxlProduct: PXLProduct) -> Unit)? = null) {
        this.photoInfo = photoInfo
        this.bookmarkMap = bookmarkMap
        this.onBookmarkClicked = onBookmarkClicked
        this.onProductClicked = onProductClicked
        isMutted = photoInfo.soundMuted
        initHeader(headerConfiguration)
        loadProducts(configuration)

        pxlPhotoView.setConfiguration(configuration = PXLPhotoView.Configuration())
        pxlPhotoView.setContent(photoInfo.pxlPhoto, photoInfo.configuration.imageScaleType)
        pxlPhotoView.setLooping(photoInfo.isLoopingVideo)
        pxlPhotoView.changeVolume(if (photoInfo.soundMuted) 0f else 1f)
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

        vMute.visibility = if (headerConfiguration.muteCheckBox != null && photoInfo?.pxlPhoto?.isVideo?:false) View.VISIBLE else View.GONE
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

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun playVideo() {
        post {
            pxlPhotoView.playVideo()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun stopVideo() {
        PXLPhotoView.releaseAllVideos()
    }

    private var adapter: ProductAdapter? = null

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        // Stop media player
    }
}
