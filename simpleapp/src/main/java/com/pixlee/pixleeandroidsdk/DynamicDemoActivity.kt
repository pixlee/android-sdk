package com.pixlee.pixleeandroidsdk

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.pixlee.pixleesdk.client.PXLKtxBaseAlbum
import com.pixlee.pixleesdk.data.PXLAlbumFilterOptions
import com.pixlee.pixleesdk.data.PXLAlbumSortOptions
import com.pixlee.pixleesdk.enums.PXLAlbumSortType
import com.pixlee.pixleesdk.enums.PXLPhotoSize
import com.pixlee.pixleesdk.ui.adapter.PXLPhotoAdapter
import com.pixlee.pixleesdk.ui.widgets.ImageScaleType
import com.pixlee.pixleesdk.ui.widgets.PXLPhotoView
import com.pixlee.pixleesdk.ui.widgets.TextPadding
import com.pixlee.pixleesdk.ui.widgets.TextViewStyle
import com.pixlee.pixleesdk.ui.widgets.list.ListHeader
import com.pixlee.pixleesdk.ui.widgets.list.Space
import com.pixlee.pixleesdk.ui.widgets.list.PXLWidgetView
import com.pixlee.pixleesdk.util.px
import kotlinx.android.synthetic.main.activity_dynamic_photos.*
import kotlinx.android.synthetic.main.module_search.*

/**
 * Created by sungjun on 3/23/21.
 */
class DynamicDemoActivity : AppCompatActivity() {
    var cellHeightInPixel = 200.px.toInt()
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dynamic_photos)
        setFilters()
        pxlPhotosView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                try {
                    if (pxlPhotosView == null)
                        return

                    cellHeightInPixel = pxlPhotosView.measuredHeight / 2
                    initiateList()
                    pxlPhotosView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        })
    }

    private fun initiateList() {
        // you can customize color, size if you need
        pxlPhotosView.initiate(
                widgetTypeForAnalytics = "your_widget_type", // this will be used when this view automatically fires openedWidget, widgetVisible analytics
                viewType = getViewType(),
                cellHeightInPixel = cellHeightInPixel,
                apiParameters = getAPIParametersToGetPhotos(),
                configuration = getConfiguration(),
                loadMoreTextViewStyle = TextViewStyle().apply {
                    text = "Load More"
                    textPadding = TextPadding(0, 22.px.toInt(), 0, 22.px.toInt())
                    size = 18.px
                    color = Color.BLACK
                },
                onButtonClickedListener = { view, photoWithImageScaleType ->
                    // TODO: you can add your business logic here

                },
                onPhotoClickedListener = { view, photoWithImageScaleType ->
                    // TODO: you can add your business logic here
                    ViewerActivity.launch(this, photoWithImageScaleType)
                }
        )
    }

    private fun getConfiguration(): PXLPhotoView.Configuration {
        return PXLPhotoView.Configuration().apply {
            // TODO: change variables values to customize the look if needed
            pxlPhotoSize = PXLPhotoSize.MEDIUM
            imageScaleType = ImageScaleType.CENTER_CROP
        }
    }

    private fun getViewType(): PXLWidgetView.ViewType {
        return if (radioGrid.isChecked) {
            PXLWidgetView.ViewType.Grid(
                    gridSpan = getGridSpan(),
                    lineSpace = Space(lineWidthInPixel = getLineSpace().px.toInt()),
                    listHeader = getHeader()
            )
        } else {
            PXLWidgetView.ViewType.List(
                    infiniteScroll = radio_infiniteScroll_on.isChecked,
                    autoPlayVideo = radio_autoPlayVideo_on.isChecked,
                    alphaForStoppedVideos = 1f
            )
        }
    }

    private fun getAPIParametersToGetPhotos(): PXLKtxBaseAlbum.Params {
        return PXLKtxBaseAlbum.Params(
                // album images
                perPage = 15,
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
        )
    }

    private fun setFilters() {
        drawerLayout.setScrimColor(Color.TRANSPARENT)
        drawerLayout.drawerElevation = 0f
        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

            }

            override fun onDrawerOpened(drawerView: View) {

            }

            override fun onDrawerClosed(drawerView: View) {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(tvLineSpace.windowToken, 0)
            }

            override fun onDrawerStateChanged(newState: Int) {

            }
        })

        fabFilter.setOnClickListener { drawerLayout.openDrawer(GravityCompat.END) }

        btnApply.setOnClickListener { drawerLayout.closeDrawer(GravityCompat.END) }

        radio_grid.setOnCheckedChangeListener { group, checkedId ->
            val viewType = pxlPhotosView.currentViewType
            when (checkedId) {
                R.id.radio_grid_2 -> {
                    changeSpan(2)
                }
                R.id.radio_grid_3 -> {
                    changeSpan(3)
                }
                R.id.radio_grid_4 -> {
                    changeSpan(4)
                }
                R.id.radio_grid_5 -> {
                    changeSpan(5)
                }
            }
        }

        radioViewType.setOnCheckedChangeListener { group, checkedId ->
            v_grid.visibility = if (R.id.radioGrid == checkedId) View.VISIBLE else View.GONE
            v_list.visibility = if (R.id.radioList == checkedId) View.VISIBLE else View.GONE
            refreshViewType()
            changeSpan(getGridSpan())
        }

        radio_infiniteScroll.setOnCheckedChangeListener { group, checkedId ->
            changeSpan(getGridSpan())
            refreshViewType()
            if (radio_infiniteScroll_on.isChecked) {
                pxlPhotosView.scrollToPosition(Integer.MAX_VALUE / 2)
            }
        }

        radio_autoPlayVideo.setOnCheckedChangeListener { group, checkedId ->
            changeSpan(getGridSpan())
            refreshViewType()
        }

        radio_header.setOnCheckedChangeListener { group, checkedId ->
            refreshViewType()
        }

        tvLineSpace.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                Log.e("text change", "changed text: ${tvLineSpace.text}")
                tvLineSpace.text
                refreshViewType()
                changeSpan(getGridSpan())
            }
        })
    }

    fun changeSpan(span: Int) {
        val viewType = pxlPhotosView.currentViewType
        if (viewType is PXLWidgetView.ViewType.Grid) {
            val allLineSpace = getLineSpace().px.toInt() * (span - 1)
            cellHeightInPixel = (pxlPhotosView.measuredWidth - allLineSpace) / span

            pxlPhotosView.currentViewType = viewType.copy().apply {
                gridSpan = span
            }

        } else {
            cellHeightInPixel = pxlPhotosView.measuredHeight / 2
        }

        pxlPhotosView.viewModel.cellHeightInPixel = cellHeightInPixel
        pxlPhotosView.viewModel.customizedConfiguration.pxlPhotoSize = if (radioList.isChecked) PXLPhotoSize.BIG else PXLPhotoSize.MEDIUM
        pxlPhotosView.pxlPhotoAdapter.list.forEach {
            when (it) {
                is PXLPhotoAdapter.Item.Content -> {
                    it.data.heightInPixel = pxlPhotosView.viewModel.cellHeightInPixel
                    it.data.configuration.pxlPhotoSize = pxlPhotosView.viewModel.customizedConfiguration.pxlPhotoSize
                }
            }
        }

        pxlPhotosView.pxlPhotoAdapter.notifyDataSetChanged()

        if (radio_infiniteScroll_on.isChecked) {
            var lastItem = pxlPhotosView.pxlPhotoAdapter.list.lastOrNull()
            if (lastItem != null && lastItem is PXLPhotoAdapter.Item.LoadMore) {
                val position = pxlPhotosView.pxlPhotoAdapter.list.count() - 1
                pxlPhotosView.pxlPhotoAdapter.list.removeAt(position)
                pxlPhotosView.pxlPhotoAdapter.notifyItemRemoved(position)
            }
        }
    }

    fun refreshViewType() {
        pxlPhotosView.currentViewType = getViewType()
    }

    fun getGridSpan(): Int {
        return if (radio_grid_2.isChecked) 2
        else if (radio_grid_3.isChecked) 3
        else if (radio_grid_4.isChecked) 4
        else if (radio_grid_5.isChecked) 5
        else 2
    }

    fun getLineSpace(): Int {
        val text = tvLineSpace.text.toString()
        return if (text.isEmpty()) 0 else text.toInt()
    }

    fun getHeader(): ListHeader? {
        return if (radio_header_image.isChecked)
            ListHeader.Gif(url = "https://media.giphy.com/media/dzaUX7CAG0Ihi/giphy.gif", heightInPixel = 200.px.toInt(), imageScaleType = ImageScaleType.CENTER_CROP)
        else if (radio_header_text.isChecked)
            getTitleSpannable()
        else
            null
    }

    fun getTitleSpannable(): ListHeader {
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

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
