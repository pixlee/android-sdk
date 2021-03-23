package com.pixlee.pixleeandroidsdk

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.pixlee.pixleesdk.client.PXLKtxBaseAlbum
import com.pixlee.pixleesdk.data.PXLAlbumFilterOptions
import com.pixlee.pixleesdk.data.PXLAlbumSortOptions
import com.pixlee.pixleesdk.enums.PXLAlbumSortType
import com.pixlee.pixleesdk.enums.PXLPhotoSize
import com.pixlee.pixleesdk.ui.widgets.ImageScaleType
import com.pixlee.pixleesdk.ui.widgets.PXLPhotoView
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by sungjun on 3/23/21.
 */
class MainActivity : BaseActivity() {
    val listHeightRatio = 0.5f

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        toolbar.navigationIcon?.setColorFilter(
                ContextCompat.getColor(this, R.color.grey_60),
                PorterDuff.Mode.SRC_ATOP
        )

        // this will play the video on onResume and stop the video on onPause
        pxlListView.useLifecycleObserver(lifecycle)

        pxlListView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                try {
                    if (pxlListView == null)
                        return

                    initalizeList((pxlListView.measuredHeight * listHeightRatio).toInt())

                    pxlListView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        })
    }

    fun initalizeList(cellHeightInPixel: Int) {
        // you can customize color, size if you need
        pxlListView.initiate(
                params = getSearchParams(),
                cellHeightInPixel = cellHeightInPixel,
                configuration = getConfiguration(),
                onButtonClickedListener = { view, photoWithImageScaleType ->
                    // TODO: you can add your business logic here
                    Toast.makeText(this, "onButtonClickedListener", Toast.LENGTH_SHORT).show()
                }, onPhotoClickedListener = { view, photoWithImageScaleType ->
            // TODO: you can add your business logic here
            ViewerActivity.launch(this, photoWithImageScaleType)
            Toast.makeText(this, "onItemClickedListener", Toast.LENGTH_SHORT).show()
        })
    }

    fun getSearchParams(): PXLKtxBaseAlbum.Params {
        return PXLKtxBaseAlbum.Params(
                searchId = PXLKtxBaseAlbum.SearchId.Album(BuildConfig.PIXLEE_ALBUM_ID), // album images
                //searchId = PXLKtxBaseAlbum.SearchId.Product(BuildConfig.PIXLEE_SKU), // product images
                perPage = 30,
                filterOptions = PXLAlbumFilterOptions().apply {
                    //hasPermission = true
                    //hasProduct = true
                    //inStockOnly = true
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
