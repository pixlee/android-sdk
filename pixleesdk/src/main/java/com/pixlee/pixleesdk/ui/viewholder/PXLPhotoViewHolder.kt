package com.pixlee.pixleesdk.ui.viewholder

import android.graphics.Rect
import android.opengl.Visibility
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pixlee.pixleesdk.data.PXLPhoto
import com.pixlee.pixleesdk.R
import com.pixlee.pixleesdk.ui.widgets.PXLPhotoView
import com.pixlee.pixleesdk.ui.widgets.playVideo
import com.pixlee.pixleesdk.util.px
import com.volokh.danylo.video_player_manager.manager.VideoItem
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager
import com.volokh.danylo.video_player_manager.meta.CurrentItemMetaData
import com.volokh.danylo.video_player_manager.meta.MetaData
import com.volokh.danylo.video_player_manager.ui.ScalableTextureView
import com.volokh.danylo.video_player_manager.ui.VideoPlayerView
import com.volokh.danylo.visibility_utils.items.ListItem
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_pxlphoto.*

/**
 * Created by sungjun on 9/14/20.
 */
class PXLPhotoViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView),
        LayoutContainer {

    fun bind(data: PhotoWithImageScaleType, configuration: PXLPhotoView.Configuration? = null, showingDebugView: Boolean = false) {
        pxlPhotoView.layoutParams.height = data.heightInPixel
        if (configuration != null) {
            pxlPhotoView.setConfiguration(configuration)
        }
        pxlPhotoView.setPhoto(data.pxlPhoto, data.imageScaleType)
        tv.visibility = if (showingDebugView) View.VISIBLE else View.GONE
        tvPercent.visibility = if (showingDebugView) View.VISIBLE else View.GONE
        tv.text = "ScaleType: ${data.imageScaleType.name}\nwidth: ${pxlPhotoView.layoutParams.width}, height: ${pxlPhotoView.layoutParams.height}\nid: ${data.pxlPhoto.id}"
    }

    companion object {
        fun create(parent: ViewGroup): PXLPhotoViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pxlphoto, parent, false)
            val holder = PXLPhotoViewHolder(view)
            view.setTag(holder)
            return holder
        }
    }
}

class PhotoWithImageScaleType(val pxlPhoto: PXLPhoto,
                              val imageScaleType: PXLPhotoView.ImageScaleType,
                              val heightInPixel: Int = 400.px.toInt(),
                              val isLoopingVideo: Boolean = true,
                              val soundMuted: Boolean = false) : ListItem, VideoItem {
    lateinit var videoPlayerManager: VideoPlayerManager<MetaData>
    private val mCurrentViewRect = Rect()

    // a part of ListItem
    override fun getVisibilityPercents(currentView: View?): Int {
        var percents = 100
        if (pxlPhoto.isVideo) {
            if (currentView != null) {
                currentView.getLocalVisibleRect(mCurrentViewRect)

                val height: Int = currentView.getHeight()

                if (viewIsPartiallyHiddenTop()) {
                    // view is partially hidden behind the top edge
                    percents = (height - mCurrentViewRect.top) * 100 / height
                } else if (viewIsPartiallyHiddenBottom(height)) {
                    percents = mCurrentViewRect.bottom * 100 / height
                }

                setVisibilityPercentsText(currentView, percents)
            }
        }
        return percents
    }

    private fun setVisibilityPercentsText(currentView: View, percents: Int) {
        if (pxlPhoto.isVideo) {
            currentView.tag?.also {
                val videoViewHolder: PXLPhotoViewHolder = it as PXLPhotoViewHolder
                val percentsText = "Visibility percents: $percents"
                videoViewHolder.tvPercent.text = percentsText
            }
        }
    }

    private fun viewIsPartiallyHiddenBottom(height: Int): Boolean {
        return mCurrentViewRect.bottom in 1 until height
    }

    private fun viewIsPartiallyHiddenTop(): Boolean {
        return mCurrentViewRect.top > 0
    }

    // a part of ListItem
    override fun setActive(newActiveView: View?, newActiveViewPosition: Int) {
        if (pxlPhoto.isVideo) {
            newActiveView?.tag?.also {
                val viewHolder: PXLPhotoViewHolder = it as PXLPhotoViewHolder
                playNewVideo(CurrentItemMetaData(newActiveViewPosition, newActiveView), viewHolder.pxlPhotoView.videoView, videoPlayerManager)
            }
        }
    }

    // a part of ListItem
    override fun deactivate(currentView: View?, position: Int) {
        if (pxlPhoto.isVideo) {
            stopPlayback(videoPlayerManager)
        }
    }

    // a part of VideoItem
    override fun playNewVideo(currentItemMetaData: MetaData, player: VideoPlayerView, videoPlayerManager: VideoPlayerManager<MetaData>) {
        if (pxlPhoto.isVideo) {
            player.setLooping(isLoopingVideo)
            if(soundMuted) player.muteVideo() else player.unMuteVideo()
            videoPlayerManager.playNewVideo(currentItemMetaData, player, pxlPhoto.videoUrl)
        }
    }

    // a part of VideoItem
    override fun stopPlayback(videoPlayerManager: VideoPlayerManager<MetaData>) {
        if (pxlPhoto.isVideo) {
            videoPlayerManager.stopAnyPlayback()
        }
    }
}