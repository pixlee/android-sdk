package com.pixlee.pixleeandroidsdk

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.pixlee.pixleesdk.client.PXLKtxBaseAlbum
import com.pixlee.pixleesdk.data.PXLAlbumFilterOptions
import com.pixlee.pixleesdk.data.PXLAlbumSortOptions
import com.pixlee.pixleesdk.enums.PXLAlbumSortType
import com.pixlee.pixleesdk.enums.PXLPhotoSize
import com.pixlee.pixleesdk.ui.widgets.ImageScaleType
import com.pixlee.pixleesdk.ui.widgets.PXLPhotoView
import com.pixlee.pixleesdk.ui.widgets.list.ListHeader
import com.pixlee.pixleesdk.ui.widgets.list.Space
import com.pixlee.pixleesdk.ui.widgets.list.v2.PXLPhotosView
import com.pixlee.pixleesdk.util.px
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by sungjun on 3/23/21.
 */
class MainActivity : AppCompatActivity() {
    val listHeightRatio = 0.5f

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        toolbar.navigationIcon?.setColorFilter(
                ContextCompat.getColor(this, R.color.grey_60),
                PorterDuff.Mode.SRC_ATOP
        )

        pxlListView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                try {
                    if (pxlListView == null)
                        return

                    initiateList((pxlListView.measuredHeight * listHeightRatio).toInt())

                    pxlListView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        })
    }

    fun initiateList(cellHeightInPixel: Int) {
        // you can customize color, size if you need
        pxlListView.initiate(
                widgetTypeForAnalytics = "your_widget_type", // this will be used when this view automatically fires openedWidget, widgetVisible analytics
                /** Alternative viewType option: List
                    viewType = PXLPhotosView.ViewType.List(
                        infiniteScroll = false,     // or false
                        autoPlayVideo = false,
                        alphaForStoppedVideos = 1f
                    ),
                 */
                viewType = PXLPhotosView.ViewType.Grid(
                        gridSpan = 2,
                        lineSpace = Space(lineWidthInPixel = 5.px.toInt()),
                        listHeader = ListHeader.Gif(url = "https://media.giphy.com/media/dzaUX7CAG0Ihi/giphy.gif", heightInPixel = 200.px.toInt(), imageScaleType = ImageScaleType.CENTER_CROP)
                ),
                cellHeightInPixel = cellHeightInPixel,
                params = getSearchParams(),
                configuration = getConfiguration(),
                onButtonClickedListener = { view, photoWithImageScaleType ->
                    // TODO: you can add your business logic here
                    Toast.makeText(this, "onButtonClickedListener", Toast.LENGTH_SHORT).show()
                },
                onPhotoClickedListener = { view, photoWithImageScaleType ->
                    // TODO: you can add your business logic here
                    ViewerActivity.launch(this, photoWithImageScaleType)
                    Toast.makeText(this, "onItemClickedListener", Toast.LENGTH_SHORT).show()
                }
        )
    }

    fun getSearchParams(): PXLKtxBaseAlbum.Params {
        return PXLKtxBaseAlbum.Params(
                searchId = PXLKtxBaseAlbum.SearchId.Album(BuildConfig.PIXLEE_ALBUM_ID), // album images
                //searchId = PXLKtxBaseAlbum.SearchId.Product(BuildConfig.PIXLEE_SKU), // product images
                filterOptions = PXLAlbumFilterOptions().apply {
                    hasProduct = true
                    // options
                    // - hasPermission = true
                    // - inStockOnly = true
                    // - .. there are more. Please check README or PXLAlbumFilterOptions class for more filter options
                },
                sortOptions = PXLAlbumSortOptions().apply {
                    sortType = PXLAlbumSortType.RECENCY
                    descending = true
                }
        )
    }

    fun getConfiguration(): PXLPhotoView.Configuration {
        return PXLPhotoView.Configuration().apply {
            // TODO: change variables values to customize the look if needed
            pxlPhotoSize = PXLPhotoSize.MEDIUM
            imageScaleType = ImageScaleType.CENTER_CROP
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
