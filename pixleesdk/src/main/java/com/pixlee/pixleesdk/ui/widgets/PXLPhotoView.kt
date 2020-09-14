package com.pixlee.pixleesdk.ui.widgets

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.VideoView
import androidx.activity.ComponentActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID
import androidx.core.view.ViewCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.pixlee.pixleesdk.PXLPhoto
import com.pixlee.pixleesdk.PXLPhotoSize
import com.pixlee.pixleesdk.util.PXLViewUtil
import com.pixlee.pixleesdk.util.dp
import com.pixlee.pixleesdk.util.px

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

/**
 * This view is to show a photo of PXLPhoto inside a RecyclerView or a ViewGroup
 */
class PXLPhotoView : FrameLayout {
    val defaultScaleType = ImageScaleType.FIT_CENTER
    var imageScaleType: ImageScaleType = defaultScaleType
    private var pxlPhoto: PXLPhoto? = null

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context)
    }

    constructor(context: Context) : super(context, null) {
        initView(context)
    }

    val imageView: ImageView by lazy {
        ImageView(context).apply {
            id = ViewCompat.generateViewId()
        }
    }
    val videoView: VideoView by lazy {
        VideoView(context).apply {
            alpha = 0f
            id = ViewCompat.generateViewId()
        }
    }
    val lottieView: PXLLoading by lazy {
        PXLLoading(context).apply {
            id = ViewCompat.generateViewId()
        }
    }

    private fun initView(context: Context) {
        ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT).apply {
            leftToLeft = PARENT_ID
            topToTop = PARENT_ID
            rightToRight = PARENT_ID
            bottomToBottom = PARENT_ID
            imageView.layoutParams = this
            imageView.setBackgroundColor(Color.RED)
            addView(imageView)
        }

        ConstraintLayout.LayoutParams(0, 0).apply {
            leftToLeft = imageView.id
            topToTop = imageView.id
            rightToRight = imageView.id
            bottomToBottom = imageView.id
            videoView.layoutParams = this
            addView(videoView)
        }

        ConstraintLayout.LayoutParams(80.px, 80.px).apply {
            leftToLeft = PARENT_ID
            topToTop = PARENT_ID
            rightToRight = PARENT_ID
            bottomToBottom = PARENT_ID
            lottieView.layoutParams = this
            addView(lottieView)
        }
    }

    /**
     * Start the UI
     * @param pxlPhoto
     * @param ImageViewParam
     */
    fun setPhoto(pxlPhoto: PXLPhoto, imageScaleType: ImageScaleType = defaultScaleType) {
        this.pxlPhoto = pxlPhoto
        this.imageScaleType = imageScaleType
        startPhoto()
        if (pxlPhoto.isVideo) {
            startVideo()
        }
    }

    private fun startPhoto() {
        imageView.visibility = VISIBLE
        pxlPhoto?.also {
            val imageUrl = it.getUrlForSize(PXLPhotoSize.BIG).toString()
            Log.d("pxlphoto", "pxlphoto.url: $imageUrl")
            // load a main image into an ImageView
            var builder = Glide.with(this).load(imageUrl)
            builder.listener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable?>, isFirstResource: Boolean): Boolean {
                    imageView.scaleType = ImageView.ScaleType.CENTER
                    return false
                }

                override fun onResourceReady(resource: Drawable?, model: Any, target: Target<Drawable?>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                    lottieView.visibility = GONE
                    imageView.scaleType = imageScaleType.type
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
            if (it.isVideo) {
                setVideoViewer(it.getUrlForSize(PXLPhotoSize.ORIGINAL).toString())
            }
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
                    /*if (event == Lifecycle.Event.ON_DESTROY) {
                        localHandler.removeCallbacks(runnableTimer)
                    } else {
                        localHandler.postDelayed(runnableTimer, 0)
                    }*/
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
