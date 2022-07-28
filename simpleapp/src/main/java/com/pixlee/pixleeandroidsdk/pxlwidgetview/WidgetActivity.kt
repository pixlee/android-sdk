package com.pixlee.pixleeandroidsdk.pxlwidgetview

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.pixlee.pixleeandroidsdk.R
import com.pixlee.pixleeandroidsdk.ViewerActivity
import com.pixlee.pixleeandroidsdk.databinding.ActivitySimpleDemoBinding
import com.pixlee.pixleesdk.client.PXLKtxAlbum
import com.pixlee.pixleesdk.client.PXLKtxBaseAlbum
import com.pixlee.pixleesdk.data.PXLAlbumFilterOptions
import com.pixlee.pixleesdk.data.PXLAlbumSortOptions
import com.pixlee.pixleesdk.data.WidgetResult
import com.pixlee.pixleesdk.enums.PXLAlbumSortType
import com.pixlee.pixleesdk.enums.PXLPhotoSize
import com.pixlee.pixleesdk.ui.widgets.ImageScaleType
import com.pixlee.pixleesdk.ui.widgets.PXLPhotoView
import com.pixlee.pixleesdk.ui.widgets.TextPadding
import com.pixlee.pixleesdk.ui.widgets.TextViewStyle
import com.pixlee.pixleesdk.ui.widgets.list.PXLWidgetView
import com.pixlee.pixleesdk.ui.widgets.list.Space
import com.pixlee.pixleesdk.util.px
import kotlinx.coroutines.*

/**
 * Created by sungjun on 3/23/21.
 */
class WidgetActivity : AppCompatActivity() {
    private var _binding: ActivitySimpleDemoBinding? = null
    private val binding get() = _binding!!

    override fun onDestroy() {
        super.onDestroy()
        displayJob?.cancel()
        scope.cancel()
        _binding = null
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySimpleDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.navigationIcon?.setColorFilter(
            ContextCompat.getColor(this, R.color.grey_60),
            PorterDuff.Mode.SRC_ATOP
        )

        binding.widget.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                try {
                    if (binding.widget == null)
                        return

                    loadDisplayOptions()

                    binding.widget.viewTreeObserver.removeOnGlobalLayoutListener(this)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        })
    }

    val widgetId = 7363177L
    protected val scope = CoroutineScope(Job() + Dispatchers.Main)
    private var displayJob: Job? = null
    private fun loadDisplayOptions() {
        val album = PXLKtxAlbum(this)
        displayJob?.cancel()
        displayJob = scope.launch {
            binding.pxlLoading.visibility = View.VISIBLE
            try {
                val widgetResult = album.getDisplayOption(filter_id = widgetId)
                Log.e("test display option", "photoResult.id: ${widgetResult.id}")
                Log.e("test display option", "photoResult.regionId: ${widgetResult.regionId}")
                Log.e("test display option", "photoResult.albumId: ${widgetResult.albumId}")
                Log.e("test display option", "photoResult.widgetType: ${widgetResult.widgetType}")
                Log.e("test display option", "photoResult.displayOptions: ${widgetResult.displayOptions}")
                Log.e("test display option", "photoResult.displayOptions?.options: ${widgetResult.displayOptions?.options}")
                Log.e("test display option", "photoResult.displayOptions?.options?.hotspots: ${widgetResult.displayOptions?.options?.hotspots}")
                Log.e("test display option", "photoResult.displayOptions?.options?.carouselOptions?.layout: ${widgetResult.displayOptions?.options?.carouselOptions?.layout}")
                initiateList(widgetResult)
            } catch (e: Exception) {
            } finally {
                binding.pxlLoading.visibility = View.GONE
            }
        }
    }


    private fun initiateList(widgetResult: WidgetResult) {
        // you can customize color, size if you need
        val halfOfScreen = (binding.widget.measuredHeight * 0.5f).toInt()
        val squareSizeInPixel = (binding.widget.measuredWidth * 0.4f).toInt()

        val gridViewType = PXLWidgetView.ViewType.Grid(gridSpan = 4, cellHeightInPixel = (binding.widget.measuredWidth * 0.25f).toInt())
        val viewType = when (widgetResult.widgetType) {
            "photowall" -> gridViewType
            "mosaic", "mosaic_v2" -> PXLWidgetView.ViewType.Mosaic(gridSpan = 4, lineSpace = Space(lineWidthInPixel = 4.px.toInt(), includingEdge = false))
            "vertical" -> PXLWidgetView.ViewType.List(cellHeightInPixel = halfOfScreen)
            "horizontal" -> PXLWidgetView.ViewType.Horizontal(squareSizeInPixel = squareSizeInPixel, lineWidthInPixel = 4.px.toInt())
            "coverflow" -> PXLWidgetView.ViewType.Horizontal(squareSizeInPixel = squareSizeInPixel, lineWidthInPixel = 4.px.toInt())
            "carousel" -> {
                val row = widgetResult.displayOptions?.options?.carouselOptions?.rows ?: 2
                val photowall = PXLWidgetView.ViewType.Grid(cellHeightInPixel = halfOfScreen, gridSpan = row)
                when (widgetResult.displayOptions?.options?.carouselOptions?.layout ?: "photowall") {
                    "photowall" -> photowall
                    "mosaic" -> PXLWidgetView.ViewType.Mosaic(gridSpan = row, lineSpace = Space(lineWidthInPixel = 4.px.toInt(), includingEdge = false))
                    else -> photowall
                }
            }
            else -> gridViewType
        }

        binding.widget.initiate(
            widgetTypeForAnalytics = widgetResult.widgetType, // this will be used when this view automatically fires openedWidget, widgetVisible analytics
            viewType = viewType,
            apiParameters = PXLKtxBaseAlbum.Params(
                // album images
                searchId = PXLKtxBaseAlbum.SearchId.Widget(id = widgetId), // product images: searchId = PXLKtxBaseAlbum.SearchId.Product(BuildConfig.PIXLEE_SKU),
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
                    descending = true
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
                Log.e("widgetAcitivity", "widgetActivity hotspots:${widgetResult.displayOptions?.options?.hotspots ?: false} ")
                photoWithImageScaleType.isHotspots = widgetResult.displayOptions?.options?.hotspots ?: false
                Log.e("widgetAcitivity", "widgetActivity photoWithImageScaleType.isHotspots:${photoWithImageScaleType.isHotspots} ")
                ViewerActivity.launch(this, photoWithImageScaleType)
            }
        )
    }
}
