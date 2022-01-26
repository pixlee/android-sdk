package com.pixlee.pixleeandroidsdk.ui.widgets

import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.pixlee.pixleeandroidsdk.R
import com.pixlee.pixleeandroidsdk.databinding.FragmentKtxGalleryListBinding
import com.pixlee.pixleeandroidsdk.databinding.FragmentPxlphotoViewBinding
import com.pixlee.pixleeandroidsdk.databinding.ModuleSearchBinding
import com.pixlee.pixleeandroidsdk.ui.BaseFragment
import com.pixlee.pixleesdk.enums.PXLPhotoSize
import com.pixlee.pixleesdk.ui.viewholder.PhotoWithImageScaleType
import com.pixlee.pixleesdk.ui.widgets.ImageScaleType
import com.pixlee.pixleesdk.ui.widgets.PXLPhotoView
import com.pixlee.pixleesdk.ui.widgets.TextViewStyle
import com.pixlee.pixleesdk.util.px

/**
 * This is to display a photo with texts of PXLPhoto
 */
class PXLPhotoViewFragment : BaseFragment(), LifecycleObserver {
    override fun getTitleResource(): Int {
        return R.string.title_pxlphotoview
    }

    private var _binding: FragmentPxlphotoViewBinding? = null
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentPxlphotoViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.switchSound.setOnClickListener {
            binding.pxlPhotoViewFitWrapLandscape.apply {
                if(binding.switchSound.isChecked) unmute() else mute()
            }

            binding.pxlPhotoViewFitPortrait.apply {
                if(binding.switchSound.isChecked) unmute() else mute()
            }

            binding.pxlPhotoViewCrop.apply {
                if(binding.switchSound.isChecked) unmute() else mute()
            }
        }

        lifecycle.addObserver(this)
        val item: PhotoWithImageScaleType? = arguments?.getParcelable("photoWithImageScaleType")
        item?.also {
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

            initPXLPhotoView(binding.pxlPhotoViewFitWrapLandscape, configuration.copy(), it, ImageScaleType.FIT_CENTER)
            initPXLPhotoView(binding.pxlPhotoViewFitPortrait, configuration.copy(), it, ImageScaleType.FIT_CENTER)
            initPXLPhotoView(binding.pxlPhotoViewCrop, configuration.copy(), it, ImageScaleType.CENTER_CROP)

            binding.scrollView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    try {
                        if (binding.scrollView == null)
                            return

                        startScrollListener()
                        binding.scrollView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            })
        }
    }

    fun initPXLPhotoView(pxlPhotoView: PXLPhotoView, configuration: PXLPhotoView.Configuration, data:PhotoWithImageScaleType, imageScaleType: ImageScaleType){
        pxlPhotoView.setConfiguration(configuration = configuration)
        pxlPhotoView.setContent(data.pxlPhoto, imageScaleType)
        pxlPhotoView.setLooping(data.isLoopingVideo)
        pxlPhotoView.changeVolume(if(data.soundMuted) 0f else 1f)
    }

    fun startScrollListener() {
        binding.pxlPhotoViewFitWrapLandscape.playVideo()

        val scrollBounds = Rect()
        binding.scrollView.getHitRect(scrollBounds)
        binding.scrollView.setOnScrollChangeListener { v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            playRelevantVideo(scrollBounds)
        }
    }

    var currentView: Int = 0
    private fun playRelevantVideo(scrollBounds: Rect) {
        fun stopExistingAndPlayNew(pxlPhotoView: PXLPhotoView, position:Int){
            if(currentView!=position){
                stopVideo()
                currentView = position
                pxlPhotoView.playVideo()
            }
        }
        
        if (binding.pxlPhotoViewFitWrapLandscape.getLocalVisibleRect(scrollBounds)) {
            stopExistingAndPlayNew(binding.pxlPhotoViewFitWrapLandscape, 1)

        } else if (binding.pxlPhotoViewCrop.getLocalVisibleRect(scrollBounds)) {
            stopExistingAndPlayNew(binding.pxlPhotoViewCrop, 2)

        }else if (binding.pxlPhotoViewFitPortrait.getLocalVisibleRect(scrollBounds)) {
            stopExistingAndPlayNew(binding.pxlPhotoViewFitPortrait, 3)

        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun playVideoOnStart() {
        playVideo()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun playVideoOnResume() {
        playVideo()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun stopVideoOnPause() {
        stopVideo()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stopVideoOnStop() {
        stopVideo()
    }

    fun playVideo(){
        val scrollBounds = Rect()
        binding.scrollView.getHitRect(scrollBounds)
        playRelevantVideo(scrollBounds)
    }

    fun stopVideo() {
        binding.pxlPhotoViewFitWrapLandscape.pauseVideo()
        binding.pxlPhotoViewCrop.pauseVideo()
        binding.pxlPhotoViewFitPortrait.pauseVideo()
    }

    override fun onStop() {
        super.onStop()

    }


    companion object {
        fun getInstance(pxlPhoto: PhotoWithImageScaleType): Fragment {
            val f = PXLPhotoViewFragment()
            val bundle = Bundle()
            bundle.putParcelable("photoWithImageScaleType", pxlPhoto)
            f.arguments = bundle
            return f
        }
    }
}
