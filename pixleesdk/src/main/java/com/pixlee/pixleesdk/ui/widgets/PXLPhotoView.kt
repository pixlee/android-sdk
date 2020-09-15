package com.pixlee.pixleesdk.ui.widgets

import android.R.attr
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID
import androidx.core.view.ViewCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.signature.ObjectKey
import com.pixlee.pixleesdk.PXLPhoto
import com.pixlee.pixleesdk.PXLPhotoSize
import com.pixlee.pixleesdk.util.px
import com.volokh.danylo.video_player_manager.ui.MediaPlayerWrapper
import com.volokh.danylo.video_player_manager.ui.ScalableTextureView
import com.volokh.danylo.video_player_manager.ui.VideoPlayerView


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
        initView()
    }

    constructor(context: Context) : super(context, null) {
        initView()
    }

    val imageView: ImageView by lazy {
        ImageView(context).apply {
            id = ViewCompat.generateViewId()
        }
    }
    val videoView: VideoPlayerView by lazy {
        VideoPlayerView(context).apply {
            //alpha = 0f
            id = ViewCompat.generateViewId()
        }
    }
    val lottieView: PXLLoading by lazy {
        PXLLoading(context).apply {
            id = ViewCompat.generateViewId()
        }
    }

    private fun initView() {
        ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT).apply {
            leftToLeft = PARENT_ID
            topToTop = PARENT_ID
            rightToRight = PARENT_ID
            bottomToBottom = PARENT_ID
            imageView.layoutParams = this
            addView(imageView)
        }

        ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT).apply {
            leftToLeft = imageView.id
            topToTop = imageView.id
            rightToRight = imageView.id
            bottomToBottom = imageView.id
            horizontalWeight = 1f
            verticalWeight = 1f
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


            var builder = Glide.with(this).asBitmap().load(imageUrl)
            builder = builder.signature(ObjectKey(imageUrl + imageScaleType.type))
//            builder = builder.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            builder = builder.skipMemoryCache(true)
//            builder = when(imageScaleType){
//                ImageScaleType.FIT_CENTER -> builder.fitCenter()
//                ImageScaleType.CENTER_CROP -> builder.centerCrop()
//            }

            builder.listener(object : RequestListener<Bitmap?> {
                override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Bitmap?>, isFirstResource: Boolean): Boolean {
                    imageView.scaleType = ImageView.ScaleType.CENTER
                    return false
                }

                override fun onResourceReady(resource: Bitmap?, model: Any, target: Target<Bitmap?>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                    lottieView.visibility = GONE
                    imageView.scaleType = imageScaleType.type
                    val viewWidth = imageView.measuredWidth
                    val targetHeight: Int = viewWidth * height / (resource?.width ?: 0)
                    Log.e("PXLPhotoView", "remote.w: ${(resource?.width ?: 0)}, remote.h: ${(resource?.height ?:0)} , iv.w: $viewWidth, targetHeight: $targetHeight")
                    if (imageView.layoutParams.height != targetHeight) {
                        imageView.layoutParams.height = targetHeight
                        imageView.requestLayout()
                    }
                    return false
                }
            }).into(imageView)
        }
    }

    fun startVideo(){
//        when(imageScaleType){
//            ImageScaleType.FIT_CENTER -> videoView.setScaleType(ScalableTextureView.ScaleType.TOP)
//            ImageScaleType.CENTER_CROP -> videoView.setScaleType(ScalableTextureView.ScaleType.CENTER_CROP)
//        }
        //videoView.setScaleType(ScalableTextureView.ScaleType.FILL)

        videoView.addMediaPlayerListener(object : MediaPlayerWrapper.MainThreadMediaPlayerListener {
            override fun onVideoSizeChangedMainThread(width: Int, height: Int) {}
            override fun onVideoPreparedMainThread() {
                // When video is prepared it's about to start playback. So we hide the cover
                //imageView.visibility = View.INVISIBLE
            }

            override fun onVideoCompletionMainThread() {}
            override fun onErrorMainThread(what: Int, extra: Int) {}
            override fun onBufferingUpdateMainThread(percent: Int) {}
            override fun onVideoStoppedMainThread() {
                // Show the cover when video stopped
                //imageView.visibility = View.VISIBLE
            }
        })
    }



    private fun showMMSS(duration: Int, timeInMilli: Int): String {
        val gap = duration - timeInMilli
        val sec = gap / 1000
        val min = sec / 60
        val secOfMin = sec % 60
        return String.format("$min:%02d", secOfMin)
    }

    private var stopPosition = 0

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        // Stop media player
    }
}
