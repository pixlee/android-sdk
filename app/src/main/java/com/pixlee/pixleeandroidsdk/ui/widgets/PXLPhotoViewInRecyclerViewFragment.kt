package com.pixlee.pixleeandroidsdk.ui.widgets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.pixlee.pixleeandroidsdk.R
import com.pixlee.pixleeandroidsdk.ui.BaseFragment
import com.pixlee.pixleesdk.PXLPhoto
import com.pixlee.pixleesdk.ui.widgets.ImageScaleType
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

    val adapter: PXLPhotoAdapter by lazy {
        PXLPhotoAdapter()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        val pxlPhoto: PXLPhoto? = arguments?.getParcelable("pxlPhoto")
        pxlPhoto?.also {
//            pxlPhotoViewCrop.setPhoto(it, ImageScaleType.CENTER_CROP)
//            pxlPhotoViewFit.setPhoto(it)
            for (i in 1..5) {
                adapter.list.add("$i")
            }

            adapter.list.add(PhotoWithImageScaleType(it, ImageScaleType.CENTER_CROP))

            for (i in 6..10) {
                adapter.list.add("$i")
            }

            adapter.list.add(PhotoWithImageScaleType(it, ImageScaleType.FIT_CENTER))

            for (i in 11..20) {
                adapter.list.add("$i")
            }
        }
    }

    companion object {
        fun getInstance(pxlPhoto: PXLPhoto): Fragment {
            val f = PXLPhotoViewInRecyclerViewFragment()
            val bundle = Bundle()
            bundle.putParcelable("pxlPhoto", pxlPhoto)
            f.arguments = bundle
            return f
        }
    }
}
