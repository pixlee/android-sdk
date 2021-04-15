package com.pixlee.pixleeandroidsdk

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.pixlee.pixleesdk.client.PXLKtxBaseAlbum
import com.pixlee.pixleesdk.data.PXLAlbumFilterOptions
import com.pixlee.pixleesdk.data.PXLAlbumSortOptions
import com.pixlee.pixleesdk.enums.PXLAlbumSortType
import com.pixlee.pixleesdk.enums.PXLPhotoSize
import com.pixlee.pixleesdk.ui.widgets.ImageScaleType
import com.pixlee.pixleesdk.ui.widgets.PXLPhotoView
import com.pixlee.pixleesdk.ui.widgets.TextPadding
import com.pixlee.pixleesdk.ui.widgets.TextViewStyle
import com.pixlee.pixleesdk.ui.widgets.list.v2.PXLPhotosView
import com.pixlee.pixleesdk.util.px
import kotlinx.android.synthetic.main.activity_simple_demo.*

/**
 * Created by sungjun on 3/23/21.
 */
class SimpleGridActivity : AppCompatActivity() {
    val listHeightRatio = 0.5f

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple_demo)
        setSupportActionBar(toolbar)
        toolbar.navigationIcon?.setColorFilter(
                ContextCompat.getColor(this, R.color.grey_60),
                PorterDuff.Mode.SRC_ATOP
        )

        pxlPhotosView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                try {
                    if (pxlPhotosView == null)
                        return

                    initiateList((pxlPhotosView.measuredHeight * listHeightRatio).toInt())

                    pxlPhotosView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        })
    }

    private fun initiateList(cellHeightInPixel: Int) {
        // you can customize color, size if you need
        pxlPhotosView.initiate(
                widgetTypeForAnalytics = "your_widget_type", // this will be used when this view automatically fires openedWidget, widgetVisible analytics
                viewType = PXLPhotosView.ViewType.Grid(),
                cellHeightInPixel = cellHeightInPixel,
                apiParameters = PXLKtxBaseAlbum.Params(
                        // album images
                        searchId = PXLKtxBaseAlbum.SearchId.Album(BuildConfig.PIXLEE_ALBUM_ID), // product images: searchId = PXLKtxBaseAlbum.SearchId.Product(BuildConfig.PIXLEE_SKU),
                        filterOptions = PXLAlbumFilterOptions().apply {
                            // hasProduct and hasPermission are often used together for displaying photos with tagged products and gotten the permission from their creators
                            // if you don't see any photos after the loading is done, go to https://app.pixlee.com/app#albums/{your album id} and make sure your photos have the same filter conditions as your filterOptions.
                            hasProduct = true
                            hasPermission = true

                            // more filter options
                            // - hasPermission = true
                            // - inStockOnly = true
                            // - .. there are more. Please check README or PXLAlbumFilterOptions class for more filter options
                        },
                        sortOptions = PXLAlbumSortOptions().apply {
                            sortType = PXLAlbumSortType.RECENCY
                            descending = false
                        }
                ),
                configuration = PXLPhotoView.Configuration().apply {
                    pxlPhotoSize = PXLPhotoSize.MEDIUM
                    imageScaleType = ImageScaleType.CENTER_CROP
                },
                loadMoreTextViewStyle = TextViewStyle().apply {
                    text = "Load More"
                    textPadding = TextPadding(0, 22.px.toInt(), 0, 22.px.toInt())
                    size = 24.px
                    color = Color.BLACK
                },
                onPhotoClickedListener = { view, photoWithImageScaleType ->
                    // TODO: open lightbox
                    ViewerActivity.launch(this, photoWithImageScaleType)
                }
        )
    }
}
