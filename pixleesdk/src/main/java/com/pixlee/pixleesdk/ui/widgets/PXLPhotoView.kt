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
import android.view.ViewGroup
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
import com.pixlee.pixleesdk.PXLPhoto
import com.pixlee.pixleesdk.PXLPhotoSize
import com.pixlee.pixleesdk.R
import com.pixlee.pixleesdk.ui.adapter.ProductAdapter
import com.pixlee.pixleesdk.util.PXLViewUtil
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.widget_photo.view.*
import java.util.HashMap

/**
 * This class is to let PXLPhotoView support a limited number of ImageView.ScaleType
 */
enum class ImageScaleType(val type: ImageView.ScaleType) {
    /**
     * ImageScaleType.FIT_CENTER: we keep the ratio of the video, so there must be empty areas. To cover it, Pixlee’s SDK will show a full-screen-size blurry image background. All parts of the video will be visible on the screen.
     */
    FIT_CENTER(ImageView.ScaleType.FIT_CENTER),

    /**
     * ImageScaleType.CENTER_CROP: there is no empty area. Some parts of the video going outside of the screen will not be visible on the screen
     */
    CENTER_CROP(ImageView.ScaleType.CENTER_CROP);
}

class ImageViewParam(val width: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
                     val height: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
                     val imageScaleType: ImageScaleType = ImageScaleType.FIT_CENTER)

/**
 * This view is to show a photo of PXLPhoto inside a RecyclerView or a ViewGroup
 */
class PXLPhotoView : FrameLayout {
    var imageViewParam: ImageViewParam = ImageViewParam()
    private var pxlPhoto: PXLPhoto? = null

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context)
    }

    constructor(context: Context) : super(context, null) {
        initView(context)
    }

    private fun initView(context: Context) {
        val li = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = li.inflate(R.layout.widget_photo, this, false)
        addView(view)
    }

    /**
     * Start the UI
     * @param pxlPhoto
     * @param ImageViewParam
     */
    fun setPhoto(pxlPhoto: PXLPhoto, imageViewParam: ImageViewParam = ImageViewParam()) {
        this.pxlPhoto = pxlPhoto
        this.imageViewParam = imageViewParam
        startPhoto()
        if (pxlPhoto.isVideo) {
            startVideo()
        }
    }

    private fun startPhoto() {
        imageView.scaleType = imageViewParam.imageScaleType.type
        imageView.layoutParams.width = imageViewParam.width
        imageView.layoutParams.height = imageViewParam.height

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
