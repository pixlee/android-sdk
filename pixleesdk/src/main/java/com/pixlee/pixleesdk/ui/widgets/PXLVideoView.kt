package com.pixlee.pixleesdk.ui.widgets

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.graphics.Matrix
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.Surface
import android.view.TextureView
import android.view.TextureView.SurfaceTextureListener
import java.io.IOException


/**
 * Created by sungjun on 9/14/20.
 */
class PXLVideoView : TextureView, SurfaceTextureListener {
    private var mMediaPlayer: MediaPlayer? = null
    private var mVideoHeight = 0f
    private var mVideoWidth = 0f
    private var mIsDataSourceSet = false
    private var mIsViewAvailable = false
    private var mIsVideoPrepared = false
    private var mIsPlayCalled = false
    private var mScaleType: ScaleType? = null
    private var mState: State? = null

    enum class ScaleType {
        CENTER_CROP, TOP, BOTTOM
    }

    enum class State {
        UNINITIALIZED, PLAY, STOP, PAUSE, END
    }

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        initView()
    }

    private fun initView() {
        initPlayer()
        setScaleType(ScaleType.CENTER_CROP)
        surfaceTextureListener = this
    }

    fun setScaleType(scaleType: ScaleType?) {
        mScaleType = scaleType
    }

    private fun updateTextureViewSize() {
        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()
        var scaleX = 1.0f
        var scaleY = 1.0f
        if (mVideoWidth > viewWidth && mVideoHeight > viewHeight) {
            scaleX = mVideoWidth / viewWidth
            scaleY = mVideoHeight / viewHeight
        } else if (mVideoWidth < viewWidth && mVideoHeight < viewHeight) {
            scaleY = viewWidth / mVideoWidth
            scaleX = viewHeight / mVideoHeight
        } else if (viewWidth > mVideoWidth) {
            scaleY = viewWidth / mVideoWidth / (viewHeight / mVideoHeight)
        } else if (viewHeight > mVideoHeight) {
            scaleX = viewHeight / mVideoHeight / (viewWidth / mVideoWidth)
        }

        // Calculate pivot points, in our case crop from center
        val pivotPointX: Float
        val pivotPointY: Float
        when (mScaleType) {
            ScaleType.TOP -> {
                pivotPointX = 0f
                pivotPointY = 0f
            }
            ScaleType.BOTTOM -> {
                pivotPointX = viewWidth.toFloat()
                pivotPointY = viewHeight.toFloat()
            }
            ScaleType.CENTER_CROP -> {
                pivotPointX = (viewWidth / 2f)
                pivotPointY = (viewHeight / 2f)
            }
            else -> {
                pivotPointX = (viewWidth / 2f)
                pivotPointY = (viewHeight / 2f)
            }
        }
        val matrix = Matrix()
        matrix.setScale(scaleX, scaleY, pivotPointX, pivotPointY)
        setTransform(matrix)
    }

    private fun initPlayer() {
        if (mMediaPlayer == null) {
            mMediaPlayer = MediaPlayer()
        } else {
            mMediaPlayer!!.reset()
        }
        mIsVideoPrepared = false
        mIsPlayCalled = false
        mState = State.UNINITIALIZED
    }

    /**
     * @see android.media.MediaPlayer.setDataSource
     */
    fun setDataSource(path: String?) {
        initPlayer()
        try {
            mMediaPlayer!!.setDataSource(path)
            mIsDataSourceSet = true
            prepare()
        } catch (e: IOException) {
            Log.d(TAG, e.message)
        }
    }

    /**
     * @see android.media.MediaPlayer.setDataSource
     */
    fun setDataSource(context: Context?, uri: Uri?) {
        initPlayer()
        try {
            mMediaPlayer!!.setDataSource(context, uri)
            mIsDataSourceSet = true
            prepare()
        } catch (e: IOException) {
            Log.d(TAG, e.message)
        }
    }

    /**
     * @see android.media.MediaPlayer.setDataSource
     */
    fun setDataSource(afd: AssetFileDescriptor) {
        initPlayer()
        try {
            val startOffset = afd.startOffset
            val length = afd.length
            mMediaPlayer!!.setDataSource(afd.fileDescriptor, startOffset, length)
            mIsDataSourceSet = true
            prepare()
        } catch (e: IOException) {
            Log.d(TAG, e.message)
        }
    }

    private fun prepare() {
        try {
            mMediaPlayer!!.setOnVideoSizeChangedListener { mp, width, height ->
                mVideoWidth = width.toFloat()
                mVideoHeight = height.toFloat()
                updateTextureViewSize()
            }
            mMediaPlayer!!.setOnCompletionListener {
                mState = State.END
                log("Video has ended.")
                if (mListener != null) {
                    mListener!!.onVideoEnd()
                }
            }

            // don't forget to call MediaPlayer.prepareAsync() method when you use constructor for
            // creating MediaPlayer
            mMediaPlayer!!.prepareAsync()

            // Play video when the media source is ready for playback.
            mMediaPlayer!!.setOnPreparedListener {
                mIsVideoPrepared = true
                if (mIsPlayCalled && mIsViewAvailable) {
                    log("Player is prepared and play() was called.")
                    play()
                }
                if (mListener != null) {
                    mListener!!.onVideoPrepared()
                }
            }
        } catch (e: IllegalArgumentException) {
            Log.d(TAG, e.message)
        } catch (e: SecurityException) {
            Log.d(TAG, e.message)
        } catch (e: IllegalStateException) {
            Log.d(TAG, e.toString())
        }
    }

    fun isPlaying(): Boolean {
        return mState!=null && mState == State.PLAY
    }

    /**
     * Play or resume video. Video will be played as soon as view is available and media player is
     * prepared.
     *
     * If video is stopped or ended and play() method was called, video will start over.
     */
    fun play() {
        if (!mIsDataSourceSet) {
            log("play() was called but data source was not set.")
            return
        }
        mIsPlayCalled = true
        if (!mIsVideoPrepared) {
            log("play() was called but video is not prepared yet, waiting.")
            return
        }
        if (!mIsViewAvailable) {
            log("play() was called but view is not available yet, waiting.")
            return
        }
        if (mState == State.PLAY) {
            log("play() was called but video is already playing.")
            return
        }
        if (mState == State.PAUSE) {
            log("play() was called but video is paused, resuming.")
            mState = State.PLAY
            mMediaPlayer!!.start()
            return
        }
        if (mState == State.END || mState == State.STOP) {
            log("play() was called but video already ended, starting over.")
            mState = State.PLAY
            mMediaPlayer!!.seekTo(0)
            mMediaPlayer!!.start()
            return
        }
        mState = State.PLAY
        mMediaPlayer!!.start()
    }

    /**
     * Pause video. If video is already paused, stopped or ended nothing will happen.
     */
    fun pause() {
        if (mState == State.PAUSE) {
            log("pause() was called but video already paused.")
            return
        }
        if (mState == State.STOP) {
            log("pause() was called but video already stopped.")
            return
        }
        if (mState == State.END) {
            log("pause() was called but video already ended.")
            return
        }
        mState = State.PAUSE
        if (mMediaPlayer!!.isPlaying) {
            mMediaPlayer!!.pause()
        }
    }

    /**
     * Stop video (pause and seek to beginning). If video is already stopped or ended nothing will
     * happen.
     */
    fun stop() {
        if (mState == State.STOP) {
            log("stop() was called but video already stopped.")
            return
        }
        if (mState == State.END) {
            log("stop() was called but video already ended.")
            return
        }
        mState = State.STOP
        if (mMediaPlayer!!.isPlaying) {
            mMediaPlayer!!.pause()
            mMediaPlayer!!.seekTo(0)
        }
    }

    /**
     * @see android.media.MediaPlayer.setLooping
     */
    fun setLooping(looping: Boolean) {
        mMediaPlayer!!.isLooping = looping
    }

    /**
     * @see android.media.MediaPlayer.seekTo
     */
    fun seekTo(milliseconds: Int) {
        mMediaPlayer!!.seekTo(milliseconds)
    }

    fun getCurrentPosition():Int{
        return mMediaPlayer?.currentPosition ?: 0
    }

    /**
     * @see android.media.MediaPlayer.getDuration
     */
    fun getDuration(): Int {
        return mMediaPlayer!!.duration
    }

    private var mListener: MediaPlayerListener? = null

    /**
     * Listener trigger 'onVideoPrepared' and `onVideoEnd` events
     */
    fun setListener(listener: MediaPlayerListener?) {
        mListener = listener
    }

    interface MediaPlayerListener {
        fun onVideoPrepared()
        fun onVideoEnd()
    }

    override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture?, width: Int, height: Int) {
        val surface = Surface(surfaceTexture)
        mMediaPlayer!!.setSurface(surface)
        mIsViewAvailable = true
        if (mIsDataSourceSet && mIsPlayCalled && mIsVideoPrepared) {
            log("View is available and play() was called.")
            play()
        }
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {}
    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
        return false
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {}

    companion object {
        // Indicate if logging is on
        const val LOG_ON = true

        // Log tag
        private val TAG = PXLVideoView::class.java.name
        fun log(message: String?) {
            if (LOG_ON) {
                Log.d(TAG, message)
            }
        }
    }
}