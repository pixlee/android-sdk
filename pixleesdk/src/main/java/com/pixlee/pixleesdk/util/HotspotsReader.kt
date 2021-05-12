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
        val screenRatio = screenHeight.toFloat() / screenWidth.toFloat()
        val viewRatio = contentHeight.toFloat() / contentWidth.toFloat()
        when (imageScaleType) {
            ImageScaleType.FIT_CENTER -> {
                if (viewRatio < screenRatio) {
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
                if (viewRatio < screenRatio) {
                    // content is shorter than screen.
                    contentScreenWidth = (screenHeight.toFloat() * (contentWidth.toFloat() / contentHeight.toFloat())).toInt()
                    contentScreenHeight = screenHeight
                } else {
                    // content is taller than screen.
                    contentScreenWidth = screenWidth
                    contentScreenHeight = (screenWidth.toFloat() * viewRatio).toInt()
                }
            }
        }
//        val leftPadding = if (contentScreenWidth < contentScreenHeight) (contentScreenHeight - contentScreenWidth) / 2 else 0
//        val topPadding = if (contentScreenWidth > contentScreenHeight) (contentScreenWidth - contentScreenHeight) / 2 else 0

        top = (screenHeight - contentScreenHeight) / 2 /*- topPadding*/
        left = (screenWidth - contentScreenWidth) / 2 /*- leftPadding*/



        Log.e("PXLPPV", "hotspots HotspotsReader contentWidth: ${contentWidth}, contentHeight: ${contentHeight}")

        Log.e("PXLPPV", "hotspots HotspotsReader screenWidth: ${screenWidth}, contentScreenWidth: ${contentScreenWidth}")
        Log.e("PXLPPV", "hotspots HotspotsReader screenHeight: ${screenHeight}, contentScreenHeight: ${contentScreenHeight}")

        //Log.e("PXLPPV", "hotspots HotspotsReader topPadding: ${topPadding}, leftPadding: ${leftPadding}")

        //Log.e("PXLPPV", "hotspots HotspotsReader logical.width: ${contentScreenWidth + leftPadding + leftPadding}, logical.height: ${contentScreenHeight + topPadding + topPadding}")
        Log.e("PXLPPV", "hotspots HotspotsReader contentScreenWidth: ${contentScreenWidth}, contentScreenHeight: ${contentScreenHeight}")
        Log.e("PXLPPV", "hotspots HotspotsReader top: ${top}, left: ${left}")
    }


    fun getHotspotsPosition(pxlBoundingBoxProduct: PXLBoundingBoxProduct): HotspotPosition {
        Log.e("PXLPPV", "hotspots getHotspotsPosition x: ${pxlBoundingBoxProduct.x}, y: ${pxlBoundingBoxProduct.y}, w: ${pxlBoundingBoxProduct.width}, h: ${pxlBoundingBoxProduct.height}")

        val leftThird = pxlBoundingBoxProduct.width / 3
        val topThird = pxlBoundingBoxProduct.height / 3
        val x = contentScreenWidth * (pxlBoundingBoxProduct.x + (leftThird)) / contentWidth
        val y = contentScreenHeight * (pxlBoundingBoxProduct.y + (topThird)) / contentHeight

        return HotspotPosition(
                x.toFloat() + left,
                y.toFloat() + top
//        return HotspotPosition(
//                0.toFloat(), top.toFloat()
        ).apply {
            Log.e("PXLPPV", "hotspots getHotspotsPosition --------> x: ${x}, y: ${y}")
        }
    }
}

class HotspotPosition(val x: Float, val y: Float)