package com.pixlee.pixleeandroidsdk.ui.widgets

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pixlee.pixleeandroidsdk.R
import com.pixlee.pixleesdk.PXLPhoto
import com.pixlee.pixleesdk.ui.widgets.ImageScaleType
import com.pixlee.pixleesdk.util.px
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_pxlphoto.*

/**
 * Created by sungjun on 9/14/20.
 */
class PXLPhotoViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView),
        LayoutContainer {

    fun bind(data: PhotoWithImageScaleType) {
        when(data.imageScaleType){
            ImageScaleType.CENTER_CROP->{
                pxlPhotoView.layoutParams.height = 200.px
            }
            ImageScaleType.FIT_CENTER->{
                pxlPhotoView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            }
        }
        pxlPhotoView.setPhoto(data.pxlPhoto, data.imageScaleType)
        tv.text = "ScaleType: ${data.imageScaleType.name}\nwidth: ${pxlPhotoView.layoutParams.width}, height: ${pxlPhotoView.layoutParams.height}"
    }

    companion object {
        fun create(parent: ViewGroup): PXLPhotoViewHolder {
            return PXLPhotoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_pxlphoto, parent, false))
        }
    }
}

class PhotoWithImageScaleType(val pxlPhoto:PXLPhoto, val imageScaleType: ImageScaleType)