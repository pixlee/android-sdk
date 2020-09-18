package com.pixlee.pixleeandroidsdk.ui.widgets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pixlee.pixleeandroidsdk.R
import com.pixlee.pixleeandroidsdk.ui.BaseFragment
import com.pixlee.pixleesdk.data.PXLPhoto
import com.pixlee.pixleesdk.ui.widgets.ImageScaleType
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
        pxlPhoto?.also{
            pxlPhotoViewCrop.setPhoto(it, ImageScaleType.CENTER_CROP)
            pxlPhotoViewFit.setPhoto(it)
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
