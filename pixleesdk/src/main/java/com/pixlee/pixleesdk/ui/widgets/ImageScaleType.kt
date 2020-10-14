package com.pixlee.pixleesdk.ui.widgets

import android.widget.ImageView

/**
 * This class is to let PXLPhotoView support ImageView.ScaleType
 */
enum class ImageScaleType(val type: ImageView.ScaleType) {
    /**
     * ImageScaleType.FIT_CENTER: we keep the ratio of the video, so there must be empty areas.
     * To cover it, Pixlee’s SDK will show a full-screen-size blurry image background.
     * All parts of the video will be visible on the screen.
     */
    FIT_CENTER(ImageView.ScaleType.FIT_CENTER),

    /**
     * ImageScaleType.CENTER_CROP: there is no empty area.
     * Some parts of the video going outside of the screen will not be visible on the screen.
     */
    CENTER_CROP(ImageView.ScaleType.CENTER_CROP);
}