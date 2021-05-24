package com.pixlee.pixleesdk.util

import com.pixlee.pixleesdk.data.PXLBoundingBoxProduct
import com.pixlee.pixleesdk.ui.widgets.ImageScaleType

/**
 * Created by sungjun on 5/11/21.
 */
class HotspotsReader(val imageScaleType: ImageScaleType, val screenWidth: Int, val screenHeight: Int, val contentWidth: Int, val contentHeight: Int) {
    val topPadding: Int
    val leftPadding: Int
    val contentScreenWidth: Int
    val contentScreenHeight: Int

    init {
        val screenRatio = screenHeight.toFloat() / screenWidth.toFloat()
        val viewRatio = contentHeight.toFloat() / contentWidth.toFloat()

        // calculate content's with and height based on ImageSaleType and the ratios of the screen and content
        if ((imageScaleType == ImageScaleType.FIT_CENTER && viewRatio < screenRatio) ||
                (imageScaleType == ImageScaleType.CENTER_CROP && viewRatio > screenRatio)) {
            // content's ratio is shorter than screen's ratio.
            contentScreenWidth = screenWidth
            contentScreenHeight = (screenWidth.toFloat() * viewRatio).toInt()
        } else {
            // content's ratio is taller than screen's ratio.
            contentScreenWidth = (screenHeight.toFloat() * (contentWidth.toFloat() / contentHeight.toFloat())).toInt()
            contentScreenHeight = screenHeight
        }

        // calculate paddings
        topPadding = (screenHeight - contentScreenHeight) / 2
        leftPadding = (screenWidth - contentScreenWidth) / 2
    }

    fun getHotspotsPosition(pxlBoundingBoxProduct: PXLBoundingBoxProduct): HotspotPosition {
        // Pixlee photos draw Hotspot at 1/3 position of the bounding_box's with and height
        val leftThird = pxlBoundingBoxProduct.width / 3
        val topThird = pxlBoundingBoxProduct.height / 3

        // convert the [x, y] to be displayed inside the content size of the screen
        val x = contentScreenWidth * (pxlBoundingBoxProduct.x + (leftThird)) / contentWidth
        val y = contentScreenHeight * (pxlBoundingBoxProduct.y + (topThird)) / contentHeight

        // return it with paddings
        return HotspotPosition(
                x.toFloat() + leftPadding,
                y.toFloat() + topPadding
        )
    }
}

class HotspotPosition(val x: Float, val y: Float)