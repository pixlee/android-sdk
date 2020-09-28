package com.pixlee.pixleesdk.ui.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pixlee.pixleesdk.R
import com.pixlee.pixleesdk.data.PXLPhoto
import com.pixlee.pixleesdk.ui.widgets.PXLPhotoView
import com.pixlee.pixleesdk.util.px
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_pxlphoto.*

/**
 * This is to display PhotoWithImageScaleType having PXLPhoto as a RecyclerView.ViewHolder.
 * This shows its content(photo/video). Via PXLPhotoView.Configuration, you can custom TextViews and a Button.
 */
class PXLPhotoViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView),
        LayoutContainer {

    fun bind(data: PhotoWithImageScaleType, configuration: PXLPhotoView.Configuration? = null, showingDebugView: Boolean = false) {
        pxlPhotoView.layoutParams.height = data.heightInPixel
        pxlPhotoView.setConfiguration(configuration = configuration ?: PXLPhotoView.Configuration())
        pxlPhotoView.setPhoto(data.pxlPhoto, data.imageScaleType)
        pxlPhotoView.setLooping(data.isLoopingVideo)
        pxlPhotoView.setVolume(if(data.soundMuted) 0f else 1f)

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

/**
 * This class contains PXLPhoto and UI information so that
 * Via bind() method, PXLPhotoViewHolder receives this class as an argument and change the UI and manipulate VideoPlayerView.
 * You can pass PhotoWithImageScaleType when declaring PXLPhotoAdapter
 */
class PhotoWithImageScaleType(val pxlPhoto: PXLPhoto,
                              val imageScaleType: PXLPhotoView.ImageScaleType,
                              val heightInPixel: Int = 400.px.toInt(),
                              val isLoopingVideo: Boolean = true,
                              val soundMuted: Boolean = false)