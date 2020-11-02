package com.pixlee.pixleesdk.ui.widgets

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.view.ViewCompat
import cn.jzvd.Jzvd
import cn.jzvd.Jzvd.STATE_NORMAL
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.pixlee.pixleesdk.R
import com.pixlee.pixleesdk.data.PXLPhoto
import com.pixlee.pixleesdk.enums.PXLPhotoSize
import com.pixlee.pixleesdk.util.px
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.parcel.Parcelize

/**
 * This view is to show a photo of PXLPhoto inside a RecyclerView or a ViewGroup
 */
class PXLPhotoView : RelativeLayout {
    companion object {
        /**
         * this is to release video player
         */
        fun releaseAllVideos() {
            Jzvd.releaseAllVideos()
        }
    }

    @Parcelize
    data class Configuration(
            var pxlPhotoSize: PXLPhotoSize = PXLPhotoSize.ORIGINAL, // PXLPhotoSize [THUMBNAIL, MEDIUM, BIG, ORIGINAL]
            var imageScaleType: ImageScaleType = ImageScaleType.FIT_CENTER,
            var mainTextViewStyle: TextViewStyle? = null, // if null, the view is gone
            var subTextViewStyle: TextViewStyle? = null, // if null, the view is gone
            var buttonStyle: ButtonStyle? = null  // if null, the view is gone
    ) : Parcelable

    @Parcelize
    class ButtonStyle(
            var buttonIcon: Int? = R.drawable.baseline_play_arrow_white_24,
            var stroke: Stroke = Stroke(),
            var padding: Padding = Padding()) : TextViewStyle(textPadding = TextPadding()), Parcelable

    @Parcelize
    class Stroke(var width: Int = 2.px.toInt(),
                 var color: Int = Color.WHITE,
                 var radiusInPixel: Float = 25.px) : Parcelable

    // in pixel
    @Parcelize
    class Padding(
            var left: Int = 20.px.toInt(),
            var centerRight: Int = 40.px.toInt(),
            var topBottom: Int = 10.px.toInt()) : Parcelable


    val defaultScaleType = ImageScaleType.FIT_CENTER
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

    val videoView: VideoWidget by lazy {
        VideoWidget(context).apply {
            id = ViewCompat.generateViewId()
        }
    }

    val mainTextView: TextView by lazy {
        TextView(context).apply {
            id = ViewCompat.generateViewId()
        }
    }

    val subTextView: TextView by lazy {
        TextView(context).apply {
            id = ViewCompat.generateViewId()
        }
    }

    val buttonBg: GradientDrawable by lazy {
        GradientDrawable()
    }

    val button: TextView by lazy {
        TextView(context).apply {
            id = ViewCompat.generateViewId()
        }
    }

    var currentConfiguration: Configuration = Configuration()
    fun setConfiguration(configuration: Configuration) {
        currentConfiguration = configuration
        button.visibility = if (configuration.buttonStyle != null) VISIBLE else GONE
        configuration.buttonStyle?.let { style ->

            button.setTextViewStyle(style)
            button.apply {
                gravity = Gravity.CENTER
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

        mainTextView.visibility = if (configuration.mainTextViewStyle != null) VISIBLE else GONE
        configuration.mainTextViewStyle?.let { style ->
            mainTextView.setTextViewStyle(style)
        }

        subTextView.visibility = if (configuration.subTextViewStyle != null) VISIBLE else GONE
        configuration.subTextViewStyle?.let { style ->
            subTextView.setTextViewStyle(style)
        }
    }

    var volume = 1f

    /**
     * @param 0f: mute the sound, ~ 1f: unmute the sound, 1f is the max volume
     * @return PXLPhotoView, builder pattern
     */
    fun changeVolume(volume: Float): PXLPhotoView {
        this.volume = volume
        videoView.changeVolume(volume)
        return this
    }

    fun mute() {
        changeVolume(0f)
    }

    fun unmute() {
        changeVolume(1f)
    }

    var looping = false

    /**
     * @param looping false: play the video only once, true: loop the video
     * @return PXLPhotoView, builder pattern
     */
    fun setLooping(looping: Boolean): PXLPhotoView {
        this.looping = looping
        videoView.setLooping(looping)
        return this
    }


    fun isPlaying(): Boolean {
        return videoView.isPlaying
    }

    /**
     * if the content is video, this plays the
     */
    fun playVideo() {
        if (pxlPhoto?.isVideo ?: false) {
            when (currentConfiguration.imageScaleType) {
                ImageScaleType.FIT_CENTER -> Jzvd.setVideoImageDisplayType(Jzvd.VIDEO_IMAGE_DISPLAY_TYPE_ADAPTER)
                ImageScaleType.CENTER_CROP -> Jzvd.setVideoImageDisplayType(Jzvd.VIDEO_IMAGE_DISPLAY_TYPE_FILL_SCROP)
            }
            videoView.play()
            //videoView.resumeVideo()
        }
    }

    fun resumeVideo() {
        if (pxlPhoto?.isVideo ?: false) {
            when (currentConfiguration.imageScaleType) {
                ImageScaleType.FIT_CENTER -> Jzvd.setVideoImageDisplayType(Jzvd.VIDEO_IMAGE_DISPLAY_TYPE_ADAPTER)
                ImageScaleType.CENTER_CROP -> Jzvd.setVideoImageDisplayType(Jzvd.VIDEO_IMAGE_DISPLAY_TYPE_FILL_SCROP)
            }
            videoView.resumeVideo()
        }
    }

    fun pauseVideo() {
        if (pxlPhoto?.isVideo ?: false) {
            Log.e("PXLPhotoView", "===mediaInterface.pause(), isPlaying: ${videoView.isPlaying}, mediaInterface: ${videoView.mediaInterface}")
            videoView.pauseVideo()
//            PXLPhotoView.releaseAllVideos()
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
                subTextView.layoutParams = this
                linearLayout.addView(subTextView)
            }

            LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
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
    fun setContent(pxlPhoto: PXLPhoto, imageScaleType: ImageScaleType = defaultScaleType) {
        this.pxlPhoto = pxlPhoto
        currentConfiguration.imageScaleType = imageScaleType
        startBlurBG()
        startPhoto()
        if (pxlPhoto.isVideo) {
            initVideoPlayer()
        }
    }

    private fun startBlurBG() {
        if (currentConfiguration.imageScaleType == ImageScaleType.CENTER_CROP) {
            // if center crop, don't load blur to preserve the compute power because this are will not be visible on the screen.
            return
        }

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
        imageView.scaleType = currentConfiguration.imageScaleType.type
        pxlPhoto?.also {
            // this replaces video's original size with big size because video's original returns a video url while other sizes return photo urls.
            // to reduce a loading time, we use a photo here
            val pxlPhotoSize: PXLPhotoSize = if (pxlPhoto?.isVideo ?: false && currentConfiguration.pxlPhotoSize == PXLPhotoSize.ORIGINAL) {
                PXLPhotoSize.BIG
            } else {
                currentConfiguration.pxlPhotoSize
            }

            val imageUrl = it.getUrlForSize(pxlPhotoSize).toString()

            // load a main image into an ImageView
            var builder = Glide.with(this).asBitmap().load(imageUrl)
            builder = builder.signature(ObjectKey(imageUrl + currentConfiguration.imageScaleType.type))
//            builder = builder.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            builder = builder.skipMemoryCache(true)
            builder = when (currentConfiguration.imageScaleType) {
                ImageScaleType.FIT_CENTER -> builder.fitCenter()
                ImageScaleType.CENTER_CROP -> builder.centerCrop()
            }

            builder.into(imageView)
        }
    }

    private fun initVideoPlayer() {
        Log.d("pxlphoto", "pxlphoto.videoUrl: ${pxlPhoto?.videoUrl}")
        videoView.setUp(pxlPhoto?.videoUrl, null, Jzvd.SCREEN_NORMAL)
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