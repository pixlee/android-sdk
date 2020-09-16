package com.pixlee.pixleesdk.ui.widgets

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.annotation.ColorInt
import androidx.core.view.ViewCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.pixlee.pixleesdk.PXLPhoto
import com.pixlee.pixleesdk.PXLPhotoSize
import com.bumptech.glide.request.target.Target
import com.volokh.danylo.video_player_manager.ui.MediaPlayerWrapper
import com.volokh.danylo.video_player_manager.ui.ScalableTextureView
import com.volokh.danylo.video_player_manager.ui.VideoPlayerView
import jp.wasabeef.glide.transformations.BlurTransformation


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
class PXLPhotoView : RelativeLayout {
    val defaultScaleType = ImageScaleType.FIT_CENTER
    var imageScaleType: ImageScaleType = defaultScaleType
    private var pxlPhoto: PXLPhoto? = null

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context) : super(context, null) {
        initView()
    }

    val bgImageView: ImageView by lazy {
        ImageView(context).apply {
            id = ViewCompat.generateViewId()
        }
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
//    val lottieView: PXLLoading by lazy {
//        PXLLoading(context).apply {
//            id = ViewCompat.generateViewId()
//        }
//    }

    private fun initView() {
        LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT).apply {
            addRule(ALIGN_TOP, imageView.id)
            addRule(ALIGN_LEFT, imageView.id)
            addRule(ALIGN_RIGHT, imageView.id)
            addRule(ALIGN_BOTTOM, imageView.id)
            bgImageView.layoutParams = this
            bgImageView.scaleType = ImageView.ScaleType.FIT_XY
            addView(bgImageView)
        }

        LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT).apply {
            imageView.layoutParams = this
            imageView.adjustViewBounds = true
            addView(imageView)
        }

        LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT).apply {
            addRule(ALIGN_TOP, imageView.id)
            addRule(ALIGN_LEFT, imageView.id)
            addRule(ALIGN_RIGHT, imageView.id)
            addRule(ALIGN_BOTTOM, imageView.id)
            videoView.layoutParams = this

            addView(videoView)
        }

//        LayoutParams(80.px, 80.px).apply {
//            addRule(CENTER_IN_PARENT, TRUE)
//            lottieView.layoutParams = this
//            addView(lottieView)
//        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
//        bgImageView.layoutParams.let {
//            imageView.layoutParams = it
//        }
    }

    var parentWidth = 0
    var parentHeight = 0
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val parentWidth = MeasureSpec.getSize(widthMeasureSpec)
        val parentHeight = MeasureSpec.getSize(heightMeasureSpec)
        if (this.parentWidth != parentWidth || this.parentHeight != parentHeight) {
            this.parentWidth = parentWidth
            this.parentHeight = parentHeight
            imageView.layoutParams.let {
                it.width = parentWidth
                it.height = parentHeight
                imageView.layoutParams = it
            }
        }

        //this.setMeasuredDimension(parentWidth/2, parentHeight);
        //this.setLayoutParams(new *ParentLayoutType*.LayoutParams(parentWidth/2,parentHeight));
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


    }

    /**
     * Start the UI
     * @param pxlPhoto
     * @param ImageViewParam
     */
    fun setPhoto(pxlPhoto: PXLPhoto, imageScaleType: ImageScaleType = defaultScaleType) {
        this.pxlPhoto = pxlPhoto
        this.imageScaleType = imageScaleType
        startBlurBG()
        startPhoto()
        if (pxlPhoto.isVideo) {
            startVideo()
        }
    }

    private fun startBlurBG() {
        // load a main image into an ImageView
        pxlPhoto?.also {
            Glide.with(this)
                    .load(it.getUrlForSize(PXLPhotoSize.THUMBNAIL).toString())
                    .centerCrop()
                    .apply(RequestOptions.bitmapTransform(BlurTransformation(70, 3)))
                    .into(bgImageView)
        }
    }

    private fun startPhoto() {
        imageView.visibility = VISIBLE
        imageView.scaleType = imageScaleType.type
        pxlPhoto?.also {
            val imageUrl = it.getUrlForSize(PXLPhotoSize.BIG).toString()
            Log.d("pxlphoto", "pxlphoto.url: $imageUrl")
            // load a main image into an ImageView


            var builder = Glide.with(this).asBitmap().load(imageUrl)
            builder = builder.signature(ObjectKey(imageUrl + imageScaleType.type))
//            builder = builder.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            builder = builder.skipMemoryCache(true)
            builder = when (imageScaleType) {
                ImageScaleType.FIT_CENTER -> builder.fitCenter()
                ImageScaleType.CENTER_CROP -> builder.centerCrop()
            }

            builder/*.listener(object : RequestListener<Bitmap?> {
                override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Bitmap?>, isFirstResource: Boolean): Boolean {
                    imageView.scaleType = ImageView.ScaleType.CENTER
                    return false
                }

                override fun onResourceReady(resource: Bitmap?, model: Any, target: Target<Bitmap?>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                    //lottieView.visibility = GONE
                    imageView.scaleType = imageScaleType.type

                    if (imageScaleType == ImageScaleType.FIT_CENTER) {
                        //imageView.scaleType = imageScaleType.type
                        val viewWidth = imageView.measuredWidth
                        val targetHeight: Int = viewWidth * (resource?.height
                                ?: 1) / (resource?.width ?: 1)
                        Log.e("PXLPhotoView", "scale.type: ${imageScaleType.type.name}, targetHeight: $targetHeight")
                        if (imageView.layoutParams.height != targetHeight) {
                            imageView.layoutParams.height = targetHeight

                        }

                    }
                    imageView.requestLayout()
                    return false
                }
            })*/.into(imageView)
        }
    }

    fun startVideo() {
        Log.d("pxlphoto", "pxlphoto.videoUrl: ${pxlPhoto?.videoUrl}")
        when (imageScaleType) {
            ImageScaleType.FIT_CENTER -> videoView.setScaleType(ScalableTextureView.ScaleType.FIT_CENTER)
            ImageScaleType.CENTER_CROP -> videoView.setScaleType(ScalableTextureView.ScaleType.CENTER_CROP)
        }


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

    /**
     * @param size: in pixel
     */
    fun setTitleSize(size:Int) {

    }

    /**
     * @param text: default is PXLPhoto title
     */
    fun setTitleText(text:String) {

    }

    fun setTitleColor(@ColorInt color: Int) {

    }


}
