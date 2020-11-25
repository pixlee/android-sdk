package com.pixlee.pixleeandroidsdk.ui.gallery

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.radiobutton.MaterialRadioButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.pixlee.pixleeandroidsdk.BuildConfig
import com.pixlee.pixleeandroidsdk.EventObserver
import com.pixlee.pixleeandroidsdk.R
import com.pixlee.pixleeandroidsdk.ui.BaseFragment
import com.pixlee.pixleeandroidsdk.ui.BaseViewModel
import com.pixlee.pixleeandroidsdk.ui.gallery.live.LiveAdapter
import com.pixlee.pixleeandroidsdk.ui.widgets.PXLPhotoViewFragment
import com.pixlee.pixleeandroidsdk.ui.widgets.ViewerActivity
import com.pixlee.pixleesdk.client.PXLKtxAlbum
import com.pixlee.pixleesdk.client.PXLKtxBaseAlbum
import com.pixlee.pixleesdk.data.PXLAlbumFilterOptions
import com.pixlee.pixleesdk.data.PXLAlbumSortOptions
import com.pixlee.pixleesdk.enums.PXLAlbumSortType
import com.pixlee.pixleesdk.enums.PXLContentSource
import com.pixlee.pixleesdk.enums.PXLContentType
import com.pixlee.pixleesdk.enums.PXLPhotoSize
import com.pixlee.pixleesdk.ui.adapter.ProductAdapter
import com.pixlee.pixleesdk.ui.viewholder.PhotoWithImageScaleType
import com.pixlee.pixleesdk.ui.widgets.*
import com.pixlee.pixleesdk.ui.widgets.list.BaseRecyclerView
import com.pixlee.pixleesdk.ui.widgets.list.ListHeader
import com.pixlee.pixleesdk.ui.widgets.list.Space
import com.pixlee.pixleesdk.util.px
import kotlinx.android.synthetic.main.fragment_ktx_gallery_grid.*
import kotlinx.android.synthetic.main.fragment_ktx_gallery_grid.drawerLayout
import kotlinx.android.synthetic.main.fragment_ktx_gallery_grid.fabFilter
import kotlinx.android.synthetic.main.fragment_ktx_gallery_grid.lottieView
import kotlinx.android.synthetic.main.fragment_ktx_gallery_grid.v_body
import kotlinx.android.synthetic.main.fragment_ktx_gallery_list.switchSound

/**
 * This shows how you can load photos of Pixlee using PXLAlbum.java
 */
class KtxGalleryGridFragment : BaseKtxGalleryFragment(), LifecycleObserver {
    override fun getTitleResource(): Int {
        return R.string.title_ktx_album_grid
    }

    override val viewModel: KtxGalleryViewModel by lazy {
        // get PXLKtxAlbum
        KtxGalleryViewModel(PXLKtxAlbum(context!!))
    }
    override val _liveList: RecyclerView?
        get() = liveList
    override val _v_body: View
        get() = v_body
    override val _switchSound: SwitchMaterial?
        get() = switchSound
    override val _pxlPhotoRecyclerView: BaseRecyclerView
        get() = pxlPhotoRecyclerViewInGrid
    override val _fabFilter: FloatingActionButton
        get() = fabFilter
    override val _drawerLayout: DrawerLayout
        get() = drawerLayout
    override val _lottieView: PXLLoading
        get() = lottieView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_ktx_gallery_grid, container, false)
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
                text = "Spring\nColors"
                size = 30.px
                sizeUnit = TypedValue.COMPLEX_UNIT_PX
                typeface = null
                textPadding = TextPadding(bottom = 30.px.toInt())
            }
            // Customize Sub TextView
            subTextViewStyle = null // you can hide this view by giving it null
            // Customize Button
            buttonStyle = PXLPhotoView.ButtonStyle().apply {
                text = "VER AHORA"
                size = 12.px
                sizeUnit = TypedValue.COMPLEX_UNIT_PX
                typeface = null
                buttonIcon = com.pixlee.pixleesdk.R.drawable.baseline_play_arrow_white_24
                stroke = PXLPhotoView.Stroke().apply {
                    width = 1.px.toInt()
                    color = Color.WHITE
                    radiusInPixel = 25.px
                    padding = PXLPhotoView.Padding().apply {
                        left = 10.px.toInt()
                        centerRight = 20.px.toInt()
                        topBottom = 10.px.toInt()
                    }
                }
            }
        }
        // you can customize color, size if you need
        pxlPhotoRecyclerViewInGrid.initiate(gridSpan = 2, // the number of cells in a row in the grid list
                lineSpace = Space().apply {
                    lineWidthInPixel = 4.px.toInt() // space in pixel between cells
                    includingEdge = false           // true: if you want to have the space out side of the list, false: no space out side of the list
                },
                listHeader = getTitleGif(), // you can custom your spannable either using getTitleSpannable() or getTitleGif(), examples of how you can implement your spannable
                showingDebugView = false,
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

    fun getTitleSpannable(): ListHeader{
        val top = "PXLEE\nSHOPPERS"
        val tv = "\nTV"
        val total = top + tv
        val spannable = SpannableString(total)

        spannable.setSpan(AbsoluteSizeSpan(40.px.toInt()), 0, top.length, 0); // set size
        spannable.setSpan(ForegroundColorSpan(Color.BLACK), 0, top.length, 0);// set color

        total.indexOf(tv).let { tvLocatedAt ->
            spannable.setSpan(AbsoluteSizeSpan(20.px.toInt()), tvLocatedAt, tvLocatedAt + tv.length, 0); // set size
            spannable.setSpan(ForegroundColorSpan(Color.BLACK), tvLocatedAt, tvLocatedAt + tv.length, 0);// set color
        }

        val padding = 20.px.toInt()
        return ListHeader.SpannableText(spannable = spannable,
                padding = TextPadding(left = padding, top = padding, right = padding, bottom = padding))
    }

    fun getTitleGif(): ListHeader{
        return ListHeader.Gif(url = "https://media.giphy.com/media/dzaUX7CAG0Ihi/giphy.gif", heightInPixel = 200.px.toInt(), imageScaleType = ImageScaleType.CENTER_CROP)
    }
}
