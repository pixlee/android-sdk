package com.pixlee.pixleesdk.util

import android.util.Log
import com.pixlee.pixleesdk.data.PXLBoundingBoxProduct
import com.pixlee.pixleesdk.ui.widgets.ImageScaleType

/**
 * Created by sungjun on 5/11/21.
 */
class HotspotsReader(val imageScaleType: ImageScaleType, val screenWidth: Int, val screenHeight: Int, val contentWidth: Int, val contentHeight: Int) {
    val top: Int
    val left: Int
    val contentScreenWidth: Int
    val contentScreenHeight: Int

    init {
        when (imageScaleType) {
            ImageScaleType.FIT_CENTER -> {
                val screenRatio = screenHeight.toFloat() / screenWidth.toFloat()
                val viewRatio = contentHeight.toFloat() / contentWidth.toFloat()

                if (viewRatio < screenRatio){
                    // content is shorter than screen. the blurry areas are at the top and bottom
                    contentScreenWidth = screenWidth
                    contentScreenHeight = (screenWidth.toFloat() * viewRatio).toInt()
                } else {
                    // content is taller than screen. the blurry areas are at the left and right
                    contentScreenWidth = (screenHeight.toFloat() * (contentWidth.toFloat() / contentHeight.toFloat())).toInt()
                    contentScreenHeight = screenHeight
                }
            }
            ImageScaleType.CENTER_CROP -> {
                contentScreenWidth = screenWidth
                contentScreenHeight = screenHeight
            }
        }
        val leftPadding = if(contentScreenWidth < contentScreenHeight) (contentScreenHeight - contentScreenWidth) / 2 * -1 else 0
        val topPadding = if(contentScreenWidth > contentScreenHeight) (contentScreenWidth - contentScreenHeight) / 2 * -1 else 0

        top = (screenHeight - contentScreenHeight) / 2 + topPadding
        left = (screenWidth - contentScreenWidth) / 2 + leftPadding



        Log.e("PXLPPV", "hotspots HotspotsReader contentWidth: ${contentScreenWidth}, contentScreenHeight: ${contentScreenHeight}")
        Log.e("PXLPPV", "hotspots HotspotsReader contentWidth: ${contentWidth}, contentHeight: ${contentHeight}")

        Log.e("PXLPPV", "hotspots HotspotsReader screenWidth: ${screenWidth}, contentScreenWidth: ${contentScreenWidth}")
        Log.e("PXLPPV", "hotspots HotspotsReader screenHeight: ${screenHeight}, contentScreenHeight: ${contentScreenHeight}")

        Log.e("PXLPPV", "hotspots HotspotsReader topPadding: ${topPadding}, leftPadding: ${leftPadding}")
        Log.e("PXLPPV", "hotspots HotspotsReader top: ${top}, left: ${left}")
    }

    val logicalWidth = 1080
    val logicalHeight = 1080

    fun getHotspotsPosition(pxlBoundingBoxProduct: PXLBoundingBoxProduct): HotspotPosition {
        Log.e("PXLPPV", "hotspots getHotspotsPosition x: ${pxlBoundingBoxProduct.x}, y: ${pxlBoundingBoxProduct.y}")

        val x = contentScreenWidth * pxlBoundingBoxProduct.x / 1080
        val leftPadding = contentScreenWidth * (pxlBoundingBoxProduct.width / 3) / 1080

        val y = contentScreenHeight * pxlBoundingBoxProduct.y / 1080
        val topPadding = contentScreenHeight * (pxlBoundingBoxProduct.height / 3) / 1080

        return HotspotPosition(
                (x + leftPadding).toFloat() + left,
                (y + topPadding).toFloat() + top
        ).apply {
            Log.e("PXLPPV", "hotspots getHotspotsPosition return x: ${x}, y: ${y}")
        }
    }
}

class HotspotPosition(val x: Float, val y: Float)