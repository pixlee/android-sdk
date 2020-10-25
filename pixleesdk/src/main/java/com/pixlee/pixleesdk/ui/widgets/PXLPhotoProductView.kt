package com.pixlee.pixleesdk.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.LinearLayoutManager
import com.pixlee.pixleesdk.R
import com.pixlee.pixleesdk.data.PXLProduct
import com.pixlee.pixleesdk.ui.adapter.ProductAdapter
import com.pixlee.pixleesdk.ui.viewholder.PhotoWithVideoInfo
import com.pixlee.pixleesdk.ui.viewholder.ProductViewHolder
import kotlinx.android.synthetic.main.widget_viewer.view.*
import java.util.*

/**
 * this view is supposed to be used in a fullscreen.
 * This view take PXLPhoto to show its content(photo/video), a product list with product's bookmark
 */
class PXLPhotoProductView : FrameLayout, LifecycleObserver {
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
                   configuration: ProductViewHolder.Configuration = ProductViewHolder.Configuration(),
                   bookmarkMap: HashMap<String, Boolean>? = null,
                   onBookmarkClicked: ((productId: String, isBookmarkChecked: Boolean) -> Unit)? = null,
                   onProductClicked: ((pxlProduct: PXLProduct) -> Unit)? = null) {
        this.photoInfo = photoInfo
        this.bookmarkMap = bookmarkMap
        this.onBookmarkClicked = onBookmarkClicked
        this.onProductClicked = onProductClicked

        loadProducts(configuration)

        pxlPhotoView.setConfiguration(configuration = PXLPhotoView.Configuration())
        pxlPhotoView.setContent(photoInfo.pxlPhoto, photoInfo.imageScaleType)
        pxlPhotoView.setLooping(photoInfo.isLoopingVideo)
        pxlPhotoView.changeVolume(if (photoInfo.soundMuted) 0f else 1f)
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

    /**
     * mute the sound
     */
    fun mute() {
        pxlPhotoView.changeVolume(0f)
    }

    /**
     * unmute the sound
     */
    fun unmute() {
        pxlPhotoView.changeVolume(1f)
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
