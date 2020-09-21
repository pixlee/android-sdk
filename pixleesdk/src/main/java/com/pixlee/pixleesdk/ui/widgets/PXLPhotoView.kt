package com.pixlee.pixleesdk.ui.widgets

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.view.ViewCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.pixlee.pixleesdk.data.PXLPhoto
import com.pixlee.pixleesdk.enums.PXLPhotoSize
import com.pixlee.pixleesdk.R
import com.pixlee.pixleesdk.util.px
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager
import com.volokh.danylo.video_player_manager.meta.MetaData
import com.volokh.danylo.video_player_manager.ui.MediaPlayerWrapper
import com.volokh.danylo.video_player_manager.ui.ScalableTextureView
import com.volokh.danylo.video_player_manager.ui.VideoPlayerView
import jp.wasabeef.glide.transformations.BlurTransformation

/**
 * This view is to show a photo of PXLPhoto inside a RecyclerView or a ViewGroup
 */
class PXLPhotoView : RelativeLayout {
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

    class Configuration(var mainTextViewStyle: TextViewStyle = TextViewStyle().apply {
        text = "Text 1"
        size = 30.px
    }, var subTextViewStyle: TextViewStyle = TextViewStyle().apply {
        text = "Text 2"
        size = 18.px
    }, var buttonStyle: ButtonStyle = ButtonStyle().apply {
        text = "Button"
        size = 20.px
    })

    class ButtonStyle(
            var isButtonVisible: Boolean = true,
            var buttonIcon: Int? = R.drawable.baseline_play_arrow_white_24,
            var stroke: Stroke = Stroke(),
            var padding: Padding = Padding()) : TextViewStyle()

    class Stroke(var width: Int = 2.px.toInt(),
                 var color: Int = Color.WHITE,
                 var radiusInPixel: Float = 25.px)

    // in pixel
    class Padding(
            var left: Int = 20.px.toInt(),
            var centerRight: Int = 40.px.toInt(),
            var topBottom: Int = 10.px.toInt())


    val defaultScaleType = ImageScaleType.FIT_CENTER
    var imageScaleType: ImageScaleType = defaultScaleType
    var pxlPhoto: PXLPhoto? = null


    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }

    val imageViewBg: ImageView by lazy {
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
            id = ViewCompat.generateViewId()
        }
    }

    val mainTextView: TextView by lazy {
        TextView(context).apply {
            text = "Main"
            setTextColor(Color.WHITE)
            setTextSize(TypedValue.COMPLEX_UNIT_PX, 30.px)
            id = ViewCompat.generateViewId()
        }
    }

    val subTextView: TextView by lazy {
        TextView(context).apply {
            text = "Sub"
            setTextColor(Color.WHITE)
            setTextSize(TypedValue.COMPLEX_UNIT_PX, 18.px)
            id = ViewCompat.generateViewId()
        }
    }

    val buttonBg: GradientDrawable by lazy {
        GradientDrawable()
    }

    val button: TextView by lazy {
        TextView(context).apply {
            setTextColor(Color.WHITE)
            text = ""
            setTextSize(TypedValue.COMPLEX_UNIT_PX, 20.px)
            id = ViewCompat.generateViewId()
        }
    }

    fun setConfiguration(configuration: Configuration) {
        configuration.buttonStyle.let { style ->
            button.visibility = if (style.isButtonVisible) VISIBLE else GONE
            button.setTextViewStyle(style)
            button.apply {
                // padding of the parent view
                style.padding.let {
                    val left = it.left
                    val centerRight = it.centerRight
                    val topBottom = it.topBottom
                    setPadding(left, topBottom, centerRight, topBottom)
                    compoundDrawablePadding = (centerRight * 0.6f).toInt()
                }

                // padding left between image and text
                setCompoundDrawablesWithIntrinsicBounds(style.buttonIcon ?: 0, 0, 0, 0)

                // stroke design
                style.stroke.let {
                    background = buttonBg.apply {
                        // radius size
                        cornerRadius = it.radiusInPixel

                        // stroke size and color
                        setStroke(it.width, it.color)
                    }
                }
            }
        }

        configuration.mainTextViewStyle.let { style ->
            mainTextView.setTextViewStyle(style)
        }

        configuration.subTextViewStyle.let { style ->
            subTextView.setTextViewStyle(style)
        }
    }

    fun setButtonClickListener(buttonClickListener: OnClickListener? = null) {
        button.setOnClickListener(buttonClickListener)
    }

    private fun initView() {
        LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT).apply {
            addRule(ALIGN_TOP, imageView.id)
            addRule(ALIGN_LEFT, imageView.id)
            addRule(ALIGN_RIGHT, imageView.id)
            addRule(ALIGN_BOTTOM, imageView.id)
            imageViewBg.layoutParams = this
            imageViewBg.scaleType = ImageView.ScaleType.FIT_XY
            addView(imageViewBg)
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

        val linearLayout = LinearLayout(context)
        LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            addRule(CENTER_IN_PARENT, TRUE)
            linearLayout.layoutParams = this
            linearLayout.orientation = LinearLayout.VERTICAL
            linearLayout.gravity = Gravity.CENTER_HORIZONTAL
            addView(linearLayout)

            LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                bottomMargin = 14.px.toInt()
                subTextView.layoutParams = this
                linearLayout.addView(subTextView)
            }

            LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                bottomMargin = 14.px.toInt()
                mainTextView.layoutParams = this
                linearLayout.addView(mainTextView)
            }

            LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                button.layoutParams = this
                linearLayout.addView(button)
            }
        }

        setConfiguration(Configuration())
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

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * Start the UI
     * @param pxlPhoto
     * @param imageScaleType
     */
    fun setPhoto(pxlPhoto: PXLPhoto, imageScaleType: ImageScaleType = defaultScaleType) {
        this.pxlPhoto = pxlPhoto
        this.imageScaleType = imageScaleType
        startBlurBG()
        startPhoto()
        if (pxlPhoto.isVideo) {
            initVideoPlayer()
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

            builder.into(imageView)
        }
    }

    fun initVideoPlayer() {
        Log.d("pxlphoto", "pxlphoto.videoUrl: ${pxlPhoto?.videoUrl}")
        when (imageScaleType) {
            ImageScaleType.FIT_CENTER -> videoView.setScaleType(ScalableTextureView.ScaleType.FIT_CENTER)
            ImageScaleType.CENTER_CROP -> videoView.setScaleType(ScalableTextureView.ScaleType.CENTER_CROP)
        }

        addMediaPlayerListener(object : MediaPlayerWrapper.MainThreadMediaPlayerListener {
            override fun onVideoSizeChangedMainThread(width: Int, height: Int) {}
            override fun onVideoPreparedMainThread() {
                // When video is prepared it's about to start playback. So we hide the cover
                //imageView.visibility = View.INVISIBLE
                //videoView.alpha = 1f
            }

            override fun onVideoCompletionMainThread() {}
            override fun onErrorMainThread(what: Int, extra: Int) {}
            override fun onBufferingUpdateMainThread(percent: Int) {}
            override fun onVideoStoppedMainThread() {
                // Show the cover when video stopped
                //imageView.visibility = View.VISIBLE
                //videoView.alpha = 0f
            }
        })
    }

    fun addMediaPlayerListener(listener: MediaPlayerWrapper.MainThreadMediaPlayerListener) {
        videoView.addMediaPlayerListener(listener)
    }

    /**
     * @param unit : example TypedValue.COMPLEX_UNIT_PX
     * @param size for main title TextView
     */
    fun setMainTitleSize(unit: Int, size: Float) {
        mainTextView.setTextSize(unit, size)
    }

    /**
     * @param text for main title TextView: default is PXLPhoto title
     */
    fun setMainTitleText(text: String?) {
        mainTextView.text = text
    }

    /**
     * color for main title TextView: color should be in @ColorInt
     */
    fun setMainTitleColor(@ColorInt color: Int) {
        mainTextView.setTextColor(color)
    }

    /**
     * Typeface for main title TextView
     */
    fun setMainTitleTypeface(typeface: Typeface) {
        mainTextView.typeface = typeface
    }

    /**
     * @param unit : example TypedValue.COMPLEX_UNIT_PX
     * @param size for sub title TextView
     */
    fun setSubTitleSize(unit: Int, size: Float) {
        subTextView.setTextSize(unit, size)
    }

    /**
     * @param text for sub title TextView: default is PXLPhoto title
     */
    fun setSubTitleText(text: String?) {
        subTextView.text = text
    }

    /**
     * color for sub title TextView: color should be in @ColorInt
     */
    fun setSubTitleColor(@ColorInt color: Int) {
        subTextView.setTextColor(color)
    }

    /**
     * Typeface for sub title TextView
     */
    fun setSubTitleTypeface(typeface: Typeface) {
        subTextView.typeface = typeface
    }
}

fun PXLPhotoView.playVideo(videoPlayerManger: VideoPlayerManager<MetaData>, isLooping:Boolean = false, muted:Boolean = false){
    if(pxlPhoto?.isVideo ?: false){
        videoView.setLooping(isLooping)
        if(muted) videoView.muteVideo()
        else videoView.unMuteVideo()
        videoPlayerManger.playNewVideo(null, this.videoView, pxlPhoto?.videoUrl)
    }
}