package com.pixlee.pixleeandroidsdk.ui.widgets

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.pixlee.pixleeandroidsdk.R
import com.pixlee.pixleeandroidsdk.databinding.FragmentPxlphotoviewInRecyclerviewBinding
import com.pixlee.pixleeandroidsdk.ui.BaseFragment
import com.pixlee.pixleesdk.data.PXLPhoto
import com.pixlee.pixleesdk.enums.PXLPhotoSize
import com.pixlee.pixleesdk.ui.viewholder.PhotoWithImageScaleType
import com.pixlee.pixleesdk.ui.widgets.ImageScaleType
import com.pixlee.pixleesdk.ui.widgets.PXLPhotoView
import com.pixlee.pixleesdk.ui.widgets.TextViewStyle
import com.pixlee.pixleesdk.util.px

/**
 * This is to display a photo with texts of PXLPhoto in RecyclerView
 */
class PXLPhotoViewInRecyclerViewFragment : BaseFragment() {
    override fun getTitleResource(): Int {
        return R.string.title_pxlphotoview_in_recyclerview
    }

    private var _binding: FragmentPxlphotoviewInRecyclerviewBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentPxlphotoviewInRecyclerviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.pxlPhotoRecyclerView.initiate(infiniteScroll = false,
                onButtonClickedListener = { view, pxlPhoto ->
                    context?.also { ctx ->
                        Toast.makeText(ctx, "onButtonClickedListener", Toast.LENGTH_SHORT).show()
                        ViewerActivity.launch(ctx, pxlPhoto)
                    }
                }, onPhotoClickedListener = { view, pxlPhoto ->
            context?.also { ctx ->
                Toast.makeText(ctx, "onItemClickedListener", Toast.LENGTH_SHORT).show()
            }
        })

        binding.pxlPhotoRecyclerView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                try {
                    if (binding.pxlPhotoRecyclerView == null)
                        return

                    val cellSize = binding.pxlPhotoRecyclerView.measuredHeight / 2
                    startList(cellSize)
                    binding.pxlPhotoRecyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        })
    }

    fun generateConfiguration(scaleType: ImageScaleType):PXLPhotoView.Configuration{
        return PXLPhotoView.Configuration().apply {
            // Customize image size, not a video
            pxlPhotoSize = PXLPhotoSize.ORIGINAL
            // Customize image scale type
            imageScaleType = scaleType
            // Customize Main TextView
            mainTextViewStyle = TextViewStyle().apply {
                text = "Main Text"
                size = 30.px
                sizeUnit = TypedValue.COMPLEX_UNIT_PX
                typeface = null
            }
            // Customize Sub TextView
            subTextViewStyle = TextViewStyle().apply {
                text = "Sub Text"
                size = 18.px
                sizeUnit = TypedValue.COMPLEX_UNIT_PX
                typeface = null
            }
            // Customize Button
            buttonStyle = PXLPhotoView.ButtonStyle().apply {
                text = "Action Button"
                size = 20.px
                sizeUnit = TypedValue.COMPLEX_UNIT_PX
                typeface = null
                buttonIcon = com.pixlee.pixleesdk.R.drawable.baseline_play_arrow_white_24
                stroke = PXLPhotoView.Stroke().apply {
                    width = 2.px.toInt()
                    color = Color.WHITE
                    radiusInPixel = 25.px
                    padding = PXLPhotoView.Padding().apply {
                        left = 20.px.toInt()
                        centerRight = 40.px.toInt()
                        topBottom = 10.px.toInt()
                    }
                }
            }
        }
    }

    fun startList(cellSize: Int) {
        val pxlPhoto: PXLPhoto? = arguments?.getParcelable("pxlPhoto")
        if (pxlPhoto != null) {
            val list = ArrayList<PhotoWithImageScaleType>()
            for (i in 1..5) {
                list.add(PhotoWithImageScaleType(pxlPhoto, generateConfiguration(ImageScaleType.CENTER_CROP), cellSize))
            }

            list.add(PhotoWithImageScaleType(pxlPhoto, generateConfiguration(ImageScaleType.CENTER_CROP), cellSize))

            for (i in 6..10) {
                //list.add("$i")
                list.add(PhotoWithImageScaleType(pxlPhoto, generateConfiguration(ImageScaleType.FIT_CENTER), cellSize))
            }

            list.add(PhotoWithImageScaleType(pxlPhoto, generateConfiguration(ImageScaleType.FIT_CENTER), 600.px.toInt()))

            for (i in 11..20) {
                //list.add("$i")
                list.add(PhotoWithImageScaleType(pxlPhoto, generateConfiguration(ImageScaleType.FIT_CENTER), 600.px.toInt()))
            }

            binding.pxlPhotoRecyclerView.replaceList(list)
        } else {
            val pxlPhotos = arguments?.getParcelableArrayList<PXLPhoto>("pxlPhotos")
            pxlPhotos?.also {
                // two items in the viewport
                //pxlPhotoRecyclerView.replaceList(it.toList(), ImageScaleType.FIT_CENTER, cellSize)

                val list = ArrayList<PhotoWithImageScaleType>()
                it.forEach {
                    // crop mode
                    list.add(PhotoWithImageScaleType(it, generateConfiguration(ImageScaleType.CENTER_CROP), cellSize))
                }

                binding.pxlPhotoRecyclerView.replaceList(list)

                // customize the cell height
                // pxlPhotoRecyclerView.replaceList(it.toList(), ImageScaleType.FIT_CENTER, 600.px.toInt())
            }

        }
    }

    override fun onResume() {
        super.onResume()
        binding.pxlPhotoRecyclerView.playVideoOnResume()
    }

    override fun onStop() {
        super.onStop()
        binding.pxlPhotoRecyclerView.stopVideoOnPause()
    }

    companion object {
        fun getInstance(pxlPhoto: PXLPhoto): Fragment {
            val f = PXLPhotoViewInRecyclerViewFragment()
            val bundle = Bundle()
            bundle.putParcelable("pxlPhoto", pxlPhoto)
            f.arguments = bundle
            return f
        }

        fun getInstance(pxlPhotos: kotlin.collections.ArrayList<PXLPhoto>): Fragment {
            val f = PXLPhotoViewInRecyclerViewFragment()
            val bundle = Bundle()
            bundle.putParcelableArrayList("pxlPhotos", pxlPhotos)
            f.arguments = bundle
            return f
        }
    }
}
