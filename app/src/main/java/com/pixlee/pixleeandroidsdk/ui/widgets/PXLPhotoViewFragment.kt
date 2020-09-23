package com.pixlee.pixleeandroidsdk.ui.widgets

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pixlee.pixleeandroidsdk.R
import com.pixlee.pixleeandroidsdk.ui.BaseFragment
import com.pixlee.pixleesdk.data.PXLPhoto
import com.pixlee.pixleesdk.enums.PXLPhotoSize
import com.pixlee.pixleesdk.ui.widgets.PXLPhotoView
import com.pixlee.pixleesdk.ui.widgets.TextViewStyle
import com.pixlee.pixleesdk.ui.widgets.playVideo
import com.pixlee.pixleesdk.util.px
import com.volokh.danylo.video_player_manager.manager.SingleVideoPlayerManager
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager
import com.volokh.danylo.video_player_manager.meta.MetaData
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

    private val mVideoPlayerManager: VideoPlayerManager<MetaData> = SingleVideoPlayerManager { }

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
            pxlPhotoViewFitWrapLandscape.setConfiguration(configuration)
            pxlPhotoViewFitPortrait.setConfiguration(configuration)
            pxlPhotoViewCrop.setConfiguration(configuration)

            pxlPhotoViewFitWrapLandscape.setPhoto(it, PXLPhotoView.ImageScaleType.FIT_CENTER)
            pxlPhotoViewFitPortrait.setPhoto(it, PXLPhotoView.ImageScaleType.FIT_CENTER)
            pxlPhotoViewCrop.setPhoto(it, PXLPhotoView.ImageScaleType.CENTER_CROP)

            pxlPhotoViewFitPortrait.playVideo(videoPlayerManger = mVideoPlayerManager, isLooping = true, muted = true)
            pxlPhotoViewFitWrapLandscape.playVideo(videoPlayerManger = mVideoPlayerManager, isLooping = true, muted = true)

        }
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
