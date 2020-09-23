package com.pixlee.pixleeandroidsdk.ui.widgets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.pixlee.pixleeandroidsdk.R
import com.pixlee.pixleeandroidsdk.ui.BaseFragment
import com.pixlee.pixleesdk.data.PXLPhoto
import com.pixlee.pixleesdk.ui.viewholder.PhotoWithImageScaleType
import com.pixlee.pixleesdk.ui.widgets.PXLPhotoView
import com.pixlee.pixleesdk.util.px
import kotlinx.android.synthetic.main.fragment_pxlphotoview_in_recyclerview.*

/**
 * This is to display a photo with texts of PXLPhoto in RecyclerView
 */
class PXLPhotoViewInRecyclerViewFragment : BaseFragment() {
    override fun getTitleResource(): Int {
        return R.string.title_pxlphotoview_in_recyclerview
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_pxlphotoview_in_recyclerview, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        pxlPhotoRecyclerView.initiate(infiniteScroll = false,
                configuration = PXLPhotoView.Configuration(),
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

        pxlPhotoRecyclerView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                try {
                    if (pxlPhotoRecyclerView == null)
                        return

                    val cellSize = pxlPhotoRecyclerView.measuredHeight / 2
                    startList(cellSize)
                    pxlPhotoRecyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        })
    }


    fun startList(cellSize: Int) {
        val pxlPhoto: PXLPhoto? = arguments?.getParcelable("pxlPhoto")
        if (pxlPhoto != null) {
            PhotoWithImageScaleType(pxlPhoto = pxlPhoto,
                    imageScaleType = PXLPhotoView.ImageScaleType.CENTER_CROP,
                    heightInPixel = cellSize,
                    isLoopingVideo = true,
                    soundMuted = true)
            val list = ArrayList<PhotoWithImageScaleType>()
            for (i in 1..5) {
                list.add(PhotoWithImageScaleType(pxlPhoto, PXLPhotoView.ImageScaleType.CENTER_CROP, cellSize))
            }

            list.add(PhotoWithImageScaleType(pxlPhoto, PXLPhotoView.ImageScaleType.CENTER_CROP, cellSize))

            for (i in 6..10) {
                //list.add("$i")
                list.add(PhotoWithImageScaleType(pxlPhoto, PXLPhotoView.ImageScaleType.FIT_CENTER, cellSize))
            }

            list.add(PhotoWithImageScaleType(pxlPhoto, PXLPhotoView.ImageScaleType.FIT_CENTER, 600.px.toInt()))

            for (i in 11..20) {
                //list.add("$i")
                list.add(PhotoWithImageScaleType(pxlPhoto, PXLPhotoView.ImageScaleType.FIT_CENTER, 600.px.toInt()))
            }

            pxlPhotoRecyclerView.replaceList(list)
        } else {
            val pxlPhotos = arguments?.getParcelableArrayList<PXLPhoto>("pxlPhotos")
            pxlPhotos?.also {
                // two items in the viewport
                //pxlPhotoRecyclerView.replaceList(it.toList(), PXLPhotoView.ImageScaleType.FIT_CENTER, cellSize)

                // crop mode
                pxlPhotoRecyclerView.replaceList(it.toList(), PXLPhotoView.ImageScaleType.CENTER_CROP, cellSize)

                // customize the cell height
                // pxlPhotoRecyclerView.replaceList(it.toList(), PXLPhotoView.ImageScaleType.FIT_CENTER, 600.px.toInt())
            }

        }
    }

    override fun onResume() {
        super.onResume()
        pxlPhotoRecyclerView.onResume()
    }

    override fun onStop() {
        super.onStop()
        pxlPhotoRecyclerView.onStop()
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
