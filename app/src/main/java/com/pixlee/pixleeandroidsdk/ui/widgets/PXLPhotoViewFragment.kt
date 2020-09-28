package com.pixlee.pixleeandroidsdk.ui.widgets

import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import com.pixlee.pixleeandroidsdk.R
import com.pixlee.pixleeandroidsdk.ui.BaseFragment
import com.pixlee.pixleesdk.data.PXLPhoto
import com.pixlee.pixleesdk.enums.PXLPhotoSize
import com.pixlee.pixleesdk.ui.widgets.PXLPhotoView
import com.pixlee.pixleesdk.ui.widgets.TextViewStyle
import com.pixlee.pixleesdk.util.px
import kotlinx.android.synthetic.main.fragment_pxlphoto_view.*

/**
 * This is to display a photo with texts of PXLPhoto
 */
class PXLPhotoViewFragment : BaseFragment() {
    override fun getTitleResource(): Int {
        return R.string.title_pxlphotoview
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_pxlphoto_view, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val pxlPhoto: PXLPhoto? = arguments?.getParcelable("pxlPhoto")
        pxlPhoto?.also {
            val configuration = PXLPhotoView.Configuration().apply {
                // Customize image size
                pxlPhotoSize = PXLPhotoSize.ORIGINAL
                // Customize Main TextView
                mainTextViewStyle = TextViewStyle().apply {
                    text = "Welcome"
                    size = 30.px
                    sizeUnit = TypedValue.COMPLEX_UNIT_PX
                    typeface = null
                }
                // Customize Sub TextView
                subTextViewStyle = TextViewStyle().apply {
                    text = "This is Pixlee SDK"
                    size = 18.px
                    sizeUnit = TypedValue.COMPLEX_UNIT_PX
                    typeface = null
                }
                // Customize Button
                buttonStyle = PXLPhotoView.ButtonStyle().apply {
                    isButtonVisible = true
                    text = "Click here"
                    size = 20.px
                    sizeUnit = TypedValue.COMPLEX_UNIT_PX
                    typeface = null
                    buttonIcon = com.pixlee.pixleesdk.R.drawable.baseline_play_arrow_white_24
                    stroke = PXLPhotoView.Stroke().apply {
                        width = 2.px.toInt()
                        color = Color.WHITE
                        radiusInPixel = 25.px
                        stroke = PXLPhotoView.Stroke().apply {
                            width = 2.px.toInt()
                            color = Color.WHITE
                            radiusInPixel = 25.px
                        }
                        padding = PXLPhotoView.Padding().apply {
                            left = 20.px.toInt()
                            centerRight = 40.px.toInt()
                            topBottom = 10.px.toInt()
                        }
                    }
                }

            }
            pxlPhotoViewFitWrapLandscape.setConfiguration(configuration.copy())
            pxlPhotoViewFitPortrait.setConfiguration(configuration.copy())
            pxlPhotoViewCrop.setConfiguration(configuration.copy())

            pxlPhotoViewFitWrapLandscape.setPhoto(it, PXLPhotoView.ImageScaleType.FIT_CENTER)
            pxlPhotoViewFitPortrait.setPhoto(it, PXLPhotoView.ImageScaleType.FIT_CENTER)
            pxlPhotoViewCrop.setPhoto(it, PXLPhotoView.ImageScaleType.CENTER_CROP)



            scrollView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    try {
                        if (scrollView == null)
                            return

                        startScrollListener()
                        scrollView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            })
        }
    }

    fun startScrollListener() {
        pxlPhotoViewFitWrapLandscape
                .setVolume(1f)
                .setLooping(true)
                .playVideo()

        val scrollBounds = Rect()
        scrollView.getHitRect(scrollBounds)
        scrollView.setOnScrollChangeListener { v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            playRelevantVideo(scrollBounds)
        }
    }

    var currentView: Int = 0
    fun playRelevantVideo(scrollBounds: Rect) {
        if (pxlPhotoViewFitWrapLandscape.getLocalVisibleRect(scrollBounds)) {
            currentView = 1
            pxlPhotoViewFitWrapLandscape
                    .setVolume(1f)
                    .setLooping(true)
                    .playVideo()
            return
        }

        if (pxlPhotoViewCrop.getLocalVisibleRect(scrollBounds)) {
            currentView = 2
            pxlPhotoViewCrop
                    .setVolume(1f)
                    .setLooping(true)
                    .playVideo()
            return
        }

        if (pxlPhotoViewFitPortrait.getLocalVisibleRect(scrollBounds)) {
            currentView = 3
            pxlPhotoViewFitPortrait
                    .setVolume(1f)
                    .setLooping(true)
                    .playVideo()
            return
        }
    }

    override fun onResume() {
        super.onResume()
        if (currentView > 0) {
            val scrollBounds = Rect()
            scrollView.getHitRect(scrollBounds)
            playRelevantVideo(scrollBounds)
        }
    }

    override fun onPause() {
        super.onPause()
        PXLPhotoView.releaseAllVideos()
    }

    companion object {
        fun getInstance(pxlPhoto: PXLPhoto): Fragment {
            val f = PXLPhotoViewFragment()
            val bundle = Bundle()
            bundle.putParcelable("pxlPhoto", pxlPhoto)
            f.arguments = bundle
            return f
        }
    }
}
