package com.pixlee.pixleeandroidsdk.ui.gallery

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.Observer
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.radiobutton.MaterialRadioButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.pixlee.pixleeandroidsdk.BuildConfig
import com.pixlee.pixleeandroidsdk.EventObserver
import com.pixlee.pixleeandroidsdk.R
import com.pixlee.pixleeandroidsdk.ui.BaseFragment
import com.pixlee.pixleeandroidsdk.ui.BaseViewModel
import com.pixlee.pixleeandroidsdk.ui.widgets.PXLPhotoViewFragment
import com.pixlee.pixleeandroidsdk.ui.widgets.ViewerActivity
import com.pixlee.pixleesdk.client.PXLKtxAlbum
import com.pixlee.pixleesdk.client.PXLKtxBaseAlbum
import com.pixlee.pixleesdk.data.PXLAlbumFilterOptions
import com.pixlee.pixleesdk.data.PXLAlbumSortOptions
import com.pixlee.pixleesdk.data.PXLRegion
import com.pixlee.pixleesdk.enums.PXLAlbumSortType
import com.pixlee.pixleesdk.enums.PXLContentSource
import com.pixlee.pixleesdk.enums.PXLContentType
import com.pixlee.pixleesdk.enums.PXLPhotoSize
import com.pixlee.pixleesdk.ui.viewholder.PhotoWithImageScaleType
import com.pixlee.pixleesdk.ui.widgets.ImageScaleType
import com.pixlee.pixleesdk.ui.widgets.PXLLoading
import com.pixlee.pixleesdk.ui.widgets.PXLPhotoView
import com.pixlee.pixleesdk.ui.widgets.TextViewStyle
import com.pixlee.pixleesdk.ui.widgets.list.BaseRecyclerView
import com.pixlee.pixleesdk.util.px
import kotlinx.android.synthetic.main.fragment_ktx_gallery_list.*
import kotlinx.android.synthetic.main.module_search.*

/**
 * This shows how you can load photos of Pixlee using PXLAlbum.java
 */
class KtxGalleryListFragment : BaseKtxGalleryFragment(), LifecycleObserver {
    override fun getTitleResource(): Int {
        return R.string.title_ktx_album_list
    }

    override val viewModel: KtxGalleryViewModel by lazy {
        // get PXLKtxAlbum
        KtxGalleryViewModel(PXLKtxAlbum(context!!))
    }
    override val _v_body: View
        get() = v_body
    override val _switchSound: SwitchMaterial?
        get() = null
    override val _pxlPhotoRecyclerView: BaseRecyclerView
        get() = pxlPhotoRecyclerView
    override val _fabFilter: FloatingActionButton
        get() = fabFilter
    override val _drawerLayout: DrawerLayout
        get() = drawerLayout
    override val _lottieView: PXLLoading
        get() = lottieView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_ktx_gallery_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }


    override fun initRecyclerView() {
        viewModel.customizedConfiguration = PXLPhotoView.Configuration().apply {
            // Customize image size, not a video
            pxlPhotoSize = PXLPhotoSize.ORIGINAL
            // Customize image scale type
            imageScaleType = ImageScaleType.CENTER_CROP
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

        // this will play the video on onResume and stop the video on onPause
        pxlPhotoRecyclerView.useLifecycleObserver(lifecycle)

        // you can customize color, size if you need
        pxlPhotoRecyclerView.initiate(infiniteScroll = true,
                showingDebugView = false,
                alphaForStoppedVideos = 0.5f,
                onButtonClickedListener = { view, photoWithImageScaleType ->
                    context?.also { ctx ->
                        // you can add your business logic here
                        Toast.makeText(ctx, "onButtonClickedListener", Toast.LENGTH_SHORT).show()
                        moveToViewer(photoWithImageScaleType)
                    }
                }, onPhotoClickedListener = { view, photoWithImageScaleType ->
            context?.also { ctx ->
                // you can add your business logic here
                Toast.makeText(ctx, "onItemClickedListener", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
