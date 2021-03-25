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
import kotlinx.android.synthetic.main.activity_simple_demo.*

/**
 * Created by sungjun on 3/23/21.
 */
class SimpleListActivity : AppCompatActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.your_layout)

        pxlPhotosView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                try {
                    if (pxlPhotosView == null)
                        return

                    initiateList((pxlPhotosView.measuredHeight / 2).toInt())

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
                widgetTypeForAnalytics = "your_widget_type",
                viewType = PXLPhotosView.ViewType.List(),
                cellHeightInPixel = cellHeightInPixel,
                params = PXLKtxBaseAlbum.Params(
                        // album images
                        searchId = PXLKtxBaseAlbum.SearchId.Album("your album number"),
                        filterOptions = PXLAlbumFilterOptions(),
                        sortOptions = PXLAlbumSortOptions().apply {
                            sortType = PXLAlbumSortType.RECENCY
                            descending = false
                        }
                ),
                configuration = PXLPhotoView.Configuration().apply {
                    pxlPhotoSize = PXLPhotoSize.MEDIUM
                    imageScaleType = ImageScaleType.CENTER_CROP
                },
                onPhotoClickedListener = { view, photoWithImageScaleType ->
                    // TODO: you can add your business logic here
                    ViewerActivity.launch(this, photoWithImageScaleType)
                    Toast.makeText(this, "onItemClickedListener", Toast.LENGTH_SHORT).show()
                }
        )
    }
}
