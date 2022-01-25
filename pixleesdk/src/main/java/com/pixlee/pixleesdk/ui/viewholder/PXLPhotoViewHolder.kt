package com.pixlee.pixleesdk.ui.viewholder

import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pixlee.pixleesdk.data.PXLPhoto
import com.pixlee.pixleesdk.databinding.ItemLoadMoreBinding
import com.pixlee.pixleesdk.databinding.ItemPxlphotoBinding
import com.pixlee.pixleesdk.ui.widgets.PXLPhotoView
import com.pixlee.pixleesdk.util.px
import kotlinx.android.parcel.Parcelize

/**
 * This is to display PhotoWithImageScaleType having PXLPhoto as a RecyclerView.ViewHolder.
 * This shows its content(photo/video). Via PXLPhotoView.Configuration, you can custom TextViews and a Button.
 */
class PXLPhotoViewHolder(val binding: ItemPxlphotoBinding) :
        RecyclerView.ViewHolder(binding.root) {

    fun setData(data: PhotoWithImageScaleType, showingDebugView: Boolean = false) {
        binding.pxlPhotoView.layoutParams.height = data.heightInPixel
        binding.pxlPhotoView.setConfiguration(configuration = data.configuration)
        binding.pxlPhotoView.setContent(data.pxlPhoto, data.configuration.imageScaleType)
        binding.pxlPhotoView.setLooping(data.isLoopingVideo)
        binding.pxlPhotoView.changeVolume(if(data.soundMuted) 0f else 1f)

        binding.tv.visibility = if (showingDebugView) View.VISIBLE else View.GONE
        binding.tvPercent.visibility = if (showingDebugView) View.VISIBLE else View.GONE
        binding.tv.text = "ScaleType: ${data.configuration.imageScaleType.name}\nwidth: ${binding.pxlPhotoView.layoutParams.width}, height: ${binding.pxlPhotoView.layoutParams.height}\nid: ${data.pxlPhoto.id}"
    }

    companion object {
        fun create(parent: ViewGroup): PXLPhotoViewHolder {
            val binding = ItemPxlphotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            val holder = PXLPhotoViewHolder(binding)
            //binding.root.setTag(holder)
            return holder
        }
    }
}

/**
 * This class contains PXLPhoto and UI information so that
 * Via bind() method, PXLPhotoViewHolder receives this class as an argument and change the UI and manipulate VideoPlayerView.
 * You can pass PhotoWithImageScaleType when declaring PXLPhotoAdapter
 */
@Parcelize
class PhotoWithImageScaleType(override val pxlPhoto: PXLPhoto,
                              override val configuration: PXLPhotoView.Configuration,
                              var heightInPixel: Int = 400.px.toInt(),
                              override val isLoopingVideo: Boolean = true,
                              override var soundMuted: Boolean = false):PhotoWithVideoInfo(pxlPhoto, configuration, isLoopingVideo, soundMuted), Parcelable

@Parcelize
open class PhotoWithVideoInfo(open val pxlPhoto: PXLPhoto,
                              open val configuration: PXLPhotoView.Configuration,
                              open val isLoopingVideo: Boolean = true,
                              open val soundMuted: Boolean = false): Parcelable