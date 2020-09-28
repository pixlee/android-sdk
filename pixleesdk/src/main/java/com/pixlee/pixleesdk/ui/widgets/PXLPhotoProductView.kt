package com.pixlee.pixleesdk.ui.widgets

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.pixlee.pixleesdk.data.PXLPhoto
import com.pixlee.pixleesdk.enums.PXLPhotoSize
import com.pixlee.pixleesdk.R
import com.pixlee.pixleesdk.data.PXLProduct
import com.pixlee.pixleesdk.ui.adapter.ProductAdapter
import com.pixlee.pixleesdk.ui.viewholder.ProductViewHolder
import com.pixlee.pixleesdk.util.PXLViewUtil
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.widget_viewer.view.*
import java.util.*

/**
 * this view is supposed to be used in a fullscreen.
 * This view take PXLPhoto to show its content(photo/video), a product list with product's bookmark
 */
class PXLPhotoProductView : FrameLayout {
    private var pxlPhoto: PXLPhoto? = null
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
     * @param pxlPhoto
     * @param bookmarkMap: user's current bookmarks < Product id: String, is bookmarked: Boolean >
     *                      if null, hide bookmark toggle
     *                      if not null, show bookmark toggle
     * @param onBookmarkClicked {productId: String, isBookmarkChecked: Boolean -> ... }
     */
    fun setPhoto(pxlPhoto: PXLPhoto,
                 configuration: ProductViewHolder.Configuration = ProductViewHolder.Configuration(),
                 bookmarkMap: HashMap<String, Boolean>? = null,
                 onBookmarkClicked: ((productId: String, isBookmarkChecked: Boolean) -> Unit)? = null,
                 onProductClicked: ((pxlProduct: PXLProduct) -> Unit)? = null) {
        this.pxlPhoto = pxlPhoto
        this.bookmarkMap = bookmarkMap
        this.onBookmarkClicked = onBookmarkClicked
        this.onProductClicked = onProductClicked
        startBlurBG()
        loadProducts(configuration)
        if (pxlPhoto.isVideo) {
            startVideo()
        } else {
            startPhoto()
        }
    }

    private fun startBlurBG() {
        // load a main image into an ImageView
        pxlPhoto?.also {
            Glide.with(this)
                    .load(it.getUrlForSize(PXLPhotoSize.THUMBNAIL).toString())
                    .centerCrop()
                    .apply(RequestOptions.bitmapTransform(BlurTransformation(70, 3)))
                    .into(imageViewBg)
        }
    }

    private fun loadProducts(configuration: ProductViewHolder.Configuration) {
        // initiate the product list view
        pxlPhoto?.also {
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

    private fun startPhoto() {
        imageView.visibility = VISIBLE
        pxlPhoto?.also {
            val imageUrl = it.getUrlForSize(PXLPhotoSize.BIG).toString()
            // load a main image into an ImageView
            Glide.with(this)
                    .load(imageUrl)
                    .fitCenter()
                    .listener(object : RequestListener<Drawable?> {
                        override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable?>, isFirstResource: Boolean): Boolean {
                            imageView.scaleType = ImageView.ScaleType.CENTER
                            return false
                        }

                        override fun onResourceReady(resource: Drawable?, model: Any, target: Target<Drawable?>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                            lottieView.visibility = GONE
                            imageView.scaleType = ImageView.ScaleType.FIT_CENTER
                            return false
                        }
                    }).into(imageView)
        }
    }

    private fun startVideo() {
        // start a pixlee loading view
        val json = PXLViewUtil.getLottieLoadingJson(context)
        lottieView.setAnimationFromJson(json, json)
        lottieView.playAnimation()

        // play the video
        playVideo()
    }

    private fun playVideo() {
        pxlPhoto?.also {
            setVideoViewer(it.getUrlForSize(PXLPhotoSize.ORIGINAL).toString())
        }
    }

    private var adapter: ProductAdapter? = null
    private fun setVideoViewer(videoUrl: String?) {
        videoView.visibility = VISIBLE
        videoView.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.setOnInfoListener { mp, what, extra ->
                if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                    videoView.alpha = 1f
                }
                true
            }
            mediaPlayer.isLooping = true
            lottieView.visibility = GONE
            if (context is ComponentActivity) {
                (context as ComponentActivity).lifecycle.addObserver(LifecycleEventObserver { source, event ->
                    Log.d("PPV", "Lifecycle.Event : " + event.name)
                    if (event == Lifecycle.Event.ON_DESTROY) {
                        localHandler.removeCallbacks(runnableTimer)
                    } else {
                        localHandler.postDelayed(runnableTimer, 0)
                    }
                    if (event == Lifecycle.Event.ON_RESUME) {
                        onResume()
                    } else if (event == Lifecycle.Event.ON_PAUSE) {
                        onPause()
                    }
                })
            }
        }
        videoView.setVideoURI(Uri.parse(videoUrl))
        videoView.setVideoPath(videoUrl)
        videoView.start()
    }

    private val localHandler = Handler()
    private var runnableTimer: Runnable = object : Runnable {
        override fun run() {
            var started: Boolean = videoView.isPlaying
            if (!started || videoView.isPlaying) {
                if (!started) {
                    started = videoView.isPlaying
                }
                tvTime.text = showMMSS(videoView.duration, videoView.currentPosition)
                localHandler.postDelayed(this, 1000)
            } else {
                tvTime.text = showMMSS(videoView.duration, videoView.duration)
            }
        }
    }

    private fun showMMSS(duration: Int, timeInMilli: Int): String {
        val gap = duration - timeInMilli
        val sec = gap / 1000
        val min = sec / 60
        val secOfMin = sec % 60
        return String.format("$min:%02d", secOfMin)
    }

    private var stopPosition = 0
    fun onResume() {
        pxlPhoto?.also {
            if (it.isVideo && !videoView.isPlaying) {
                videoView.seekTo(stopPosition)
                videoView.start()
            }
        }

    }

    fun onPause() {
        pxlPhoto?.also {
            if (it.isVideo && videoView.isPlaying) {
                stopPosition = videoView.currentPosition
                videoView.pause()
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        // Stop media player
    }
}
