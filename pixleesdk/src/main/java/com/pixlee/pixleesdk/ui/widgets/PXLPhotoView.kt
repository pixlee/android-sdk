package com.pixlee.pixleesdk.ui.widgets

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.util.Pair
import android.view.Gravity
import android.view.View
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
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer.DecoderInitializationException
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil.DecoderQueryException
//import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
//import com.google.android.exoplayer2.source.MediaSourceFactory
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector.ParametersBuilder
import com.google.android.exoplayer2.trackselection.TrackSelection
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.util.ErrorMessageProvider
import com.google.android.exoplayer2.util.EventLogger
import com.pixlee.pixleesdk.R
import com.pixlee.pixleesdk.data.PXLPhoto
import com.pixlee.pixleesdk.enums.PXLPhotoSize
import com.pixlee.pixleesdk.util.px
import com.pixlee.pixleesdk.video.ExoPlayerUtil
import com.pixlee.pixleesdk.video.TransparentStyledPlayerView
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.parcel.Parcelize

/**
 * This view is to show a photo of PXLPhoto inside a RecyclerView or a ViewGroup
 */
class PXLPhotoView : RelativeLayout {
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

    // #####################################
    // ######### Start: ExoPlayer ##########
    // #####################################
    private var startWindow = 0
    private var startPosition: Long = 0
    private var trackSelector: DefaultTrackSelector? = null
    private var trackSelectorParameters: DefaultTrackSelector.Parameters? = null
    private var startAutoPlay = true
    // Fields used only for ad playback.
    private var dataSourceFactory: DataSource.Factory? = null
    protected var player: SimpleExoPlayer? = null
    // ###################################
    // ######### End: ExoPlayer ##########
    // ###################################

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

    val videoView: TransparentStyledPlayerView by lazy {
        TransparentStyledPlayerView(context).apply {
            id = ViewCompat.generateViewId()
            useController = false
            setErrorMessageProvider(PlayerErrorMessageProvider())
            dataSourceFactory = ExoPlayerUtil.buildDataSourceFactory(context, true)

            trackSelectorParameters = ParametersBuilder().build()

            requestFocus()
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
        Log.e("", "changeVolume: $volume")
        this.volume = volume
        player?.volume = volume
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
        player?.repeatMode = if (looping) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
        return this
    }

    fun hasPlayer(): Boolean {
        return player!=null
    }

    /**
     * if the content is video, this plays the
     */
    fun playVideo() {
        Log.e("PXLPhotoViewVP", "======> playVideo()")
        if (pxlPhoto?.isVideo ?: false) {
            videoView.visibility = View.VISIBLE
            val isNotPlaying = !hasPlayer()
            if(isNotPlaying) initVideoPlayer()
            if(isNotPlaying) {
                videoView.setShutterBackgroundColor(Color.TRANSPARENT)
            }
            if(isNotPlaying){
                videoView.resizeMode = when (currentConfiguration.imageScaleType) {
                    ImageScaleType.FIT_CENTER -> AspectRatioFrameLayout.RESIZE_MODE_FIT
                    ImageScaleType.CENTER_CROP -> AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                }
            }
            // change volume
            player?.volume = volume
            player?.repeatMode = if (looping) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
            //if(isNotPlaying) videoView.onResume()
        }
    }

    fun pauseVideo() {
        Log.e("PXLPhotoViewVP", "## pauseVideo()")
        if (pxlPhoto?.isVideo ?: false) {
            videoView.visibility = View.GONE
            //videoView.onPause()
            releasePlayer()
        }
    }

    protected fun releasePlayer() {
        Log.e("PXLPhotoViewVP", "## releasePlayer() player: $player")
        if (player != null) {
            updateTrackSelectorParameters()
            updateStartPosition()
            player?.stop()
            player?.release()
            player = null
            trackSelector = null
        }
    }

    private fun updateTrackSelectorParameters() {
        if (trackSelector != null) {
            trackSelectorParameters = trackSelector!!.parameters
        }
    }

    private fun updateStartPosition() {
        if (player != null) {
            startAutoPlay = player!!.playWhenReady
            startWindow = player!!.currentWindowIndex
            startPosition = Math.max(0, player!!.contentPosition)
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
            videoView.visibility = View.GONE
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
            var builder = Glide.with(this).load(imageUrl)
            builder = builder.signature(ObjectKey(imageUrl + currentConfiguration.imageScaleType.type))
            builder = when (currentConfiguration.imageScaleType) {
                ImageScaleType.FIT_CENTER -> builder.fitCenter()
                ImageScaleType.CENTER_CROP -> builder.centerCrop()
            }

            builder.into(imageView)
        }
    }


    private fun initVideoPlayer() {
        Log.e("PXLPhotoViewVP", "## initVideoPlayer() start player: $player")
        val mediaSource = createMediaSource()
        if (player == null) {

            if (mediaSource==null) {
                return
            }
            val preferExtensionDecoders = false
            val renderersFactory: RenderersFactory = ExoPlayerUtil.buildRenderersFactory( /* context= */context, preferExtensionDecoders)

            var trackSelectionFactory: TrackSelection.Factory = AdaptiveTrackSelection.Factory(ExoPlayerUtil.BANDWIDTH_METER)
            trackSelector = DefaultTrackSelector(trackSelectionFactory)
            trackSelector!!.parameters = trackSelectorParameters!!

            player = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector)
            player?.addAnalyticsListener(EventLogger(trackSelector))

            player!!.playWhenReady = startAutoPlay
            videoView.setPlayer(player)
            videoView.setPlaybackPreparer {
                initVideoPlayer()
            }
        }
        val haveStartPosition = startWindow != C.INDEX_UNSET
        if (haveStartPosition) {
            player?.seekTo(startWindow, startPosition)
        }
        player?.prepare(mediaSource, !haveStartPosition, false)
        Log.e("PXLPhotoViewVP", "## initVideoPlayer() done player: $player")
        return
    }

    private fun createMediaSource(): MediaSource? {
        if(pxlPhoto?.videoUrl==null)
            return null

        return if(pxlPhoto?.videoUrl!!.indexOf(".m3u8")>-1){
            Log.e("PXLPhotoViewVP", "========= createMediaSource()  ->  HlsMediaSource")
            HlsMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(pxlPhoto?.videoUrl))
        }else{
            Log.e("PXLPhotoViewVP", "========= createMediaSource()  ->  ExtractorMediaSource")
            Log.e("PXLPhotoViewVP", "## initVideoPlayer() done player: $player")
            ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(pxlPhoto?.videoUrl))
        }
    }

    private class PlayerErrorMessageProvider : ErrorMessageProvider<ExoPlaybackException> {
        override fun getErrorMessage(e: ExoPlaybackException): Pair<Int, String>? {
            var errorString: String = "error_generic"
            if (e.type == ExoPlaybackException.TYPE_RENDERER) {
                val cause = e.rendererException
                if (cause is DecoderInitializationException) {
                    // Special case for decoder initialization failures.
                    val decoderInitializationException = cause
                    if (decoderInitializationException.decoderName == null) {
                        if (decoderInitializationException.cause is DecoderQueryException) {
                            errorString = "error_querying_decoders"
                        } else if (decoderInitializationException.secureDecoderRequired) {
                            errorString = "error_no_secure_decoder " + decoderInitializationException.mimeType
                        } else {
                            errorString = "error_no_decoder " + decoderInitializationException.mimeType
                        }
                    } else {
                        errorString = "error_instantiating_decoder " + decoderInitializationException.decoderName
                    }
                }
            }
            return Pair.create(0, errorString)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        releasePlayer()
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