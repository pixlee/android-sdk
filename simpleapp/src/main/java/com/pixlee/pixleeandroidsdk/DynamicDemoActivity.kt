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
import com.pixlee.pixleeandroidsdk.databinding.ActivityDynamicPhotosBinding
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
import com.pixlee.pixleesdk.ui.widgets.list.PXLWidgetView
import com.pixlee.pixleesdk.ui.widgets.list.Space
import com.pixlee.pixleesdk.util.px

/**
 * Created by sungjun on 3/23/21.
 */
class DynamicDemoActivity : AppCompatActivity() {
    private var _binding: ActivityDynamicPhotosBinding? = null
    private val binding get() = _binding!!

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    var cellHeightInPixel = 200.px.toInt()
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDynamicPhotosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setFilters()
        binding.widget.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                try {
                    if (binding.widget == null)
                        return

                    cellHeightInPixel = binding.widget.measuredHeight / 2
                    initiateList()
                    binding.widget.viewTreeObserver.removeOnGlobalLayoutListener(this)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        })
    }

    private fun initiateList() {
        // you can customize color, size if you need
        binding.widget.initiate(
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
        return if (binding.leftLayout.radioGrid.isChecked) {
            PXLWidgetView.ViewType.Grid(
                    gridSpan = getGridSpan(),
                    lineSpace = Space(lineWidthInPixel = getLineSpace().px.toInt()),
                    listHeader = getHeader()
            )
        } else {
            PXLWidgetView.ViewType.List(
                    infiniteScroll = binding.leftLayout.radioInfiniteScrollOn.isChecked,
                    autoPlayVideo = binding.leftLayout.radioAutoPlayVideoOn.isChecked,
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
        binding.drawerLayout.setScrimColor(Color.TRANSPARENT)
        binding.drawerLayout.drawerElevation = 0f
        binding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

            }

            override fun onDrawerOpened(drawerView: View) {

            }

            override fun onDrawerClosed(drawerView: View) {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.leftLayout.tvLineSpace.windowToken, 0)
            }

            override fun onDrawerStateChanged(newState: Int) {

            }
        })

        binding.fabFilter.setOnClickListener { binding.drawerLayout.openDrawer(GravityCompat.END) }

        binding.leftLayout.btnApply.setOnClickListener { binding.drawerLayout.closeDrawer(GravityCompat.END) }



        binding.leftLayout.radioGridCount.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                binding.leftLayout.radioGridCount2.id -> {
                    changeSpan(2)
                }
                binding.leftLayout.radioGridCount3.id -> {
                    changeSpan(3)
                }
                binding.leftLayout.radioGridCount4.id -> {
                    changeSpan(4)
                }
                binding.leftLayout.radioGridCount5.id -> {
                    changeSpan(5)
                }
            }
        }

        binding.leftLayout.radioViewType.setOnCheckedChangeListener { group, checkedId ->
            binding.leftLayout.vGrid.visibility = if (R.id.radioGrid == checkedId) View.VISIBLE else View.GONE
            binding.leftLayout.vList.visibility = if (R.id.radioList == checkedId) View.VISIBLE else View.GONE
            refreshViewType()
            changeSpan(getGridSpan())
        }

        binding.leftLayout.radioInfiniteScroll.setOnCheckedChangeListener { group, checkedId ->
            changeSpan(getGridSpan())
            refreshViewType()
            if (binding.leftLayout.radioInfiniteScrollOn.isChecked) {
                binding.widget.scrollToPosition(Integer.MAX_VALUE / 2)
            }
        }

        binding.leftLayout.radioAutoPlayVideo.setOnCheckedChangeListener { group, checkedId ->
            changeSpan(getGridSpan())
            refreshViewType()
        }

        binding.leftLayout.radioHeader.setOnCheckedChangeListener { group, checkedId ->
            refreshViewType()
        }

        binding.leftLayout.tvLineSpace.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                Log.e("text change", "changed text: ${binding.leftLayout.tvLineSpace.text}")
                binding.leftLayout.tvLineSpace.text
                refreshViewType()
                changeSpan(getGridSpan())
            }
        })
    }

    fun changeSpan(span: Int) {
        val viewType = binding.widget.currentViewType
        if (viewType is PXLWidgetView.ViewType.Grid) {
            val allLineSpace = getLineSpace().px.toInt() * (span - 1)
            cellHeightInPixel = (binding.widget.measuredWidth - allLineSpace) / span

            binding.widget.currentViewType = viewType.copy().apply {
                gridSpan = span
            }

        } else {
            cellHeightInPixel = binding.widget.measuredHeight / 2
        }

        binding.widget.viewModel.cellHeightInPixel = cellHeightInPixel
        binding.widget.viewModel.customizedConfiguration.pxlPhotoSize = if (binding.leftLayout.radioList.isChecked) PXLPhotoSize.BIG else PXLPhotoSize.MEDIUM
        binding.widget.pxlPhotoAdapter.list.forEach {
            when (it) {
                is PXLPhotoAdapter.Item.Content -> {
                    it.data.heightInPixel = binding.widget.viewModel.cellHeightInPixel
                    it.data.configuration.pxlPhotoSize = binding.widget.viewModel.customizedConfiguration.pxlPhotoSize
                }
            }
        }

        binding.widget.pxlPhotoAdapter.notifyDataSetChanged()

        if (binding.leftLayout.radioInfiniteScrollOn.isChecked) {
            var lastItem = binding.widget.pxlPhotoAdapter.list.lastOrNull()
            if (lastItem != null && lastItem is PXLPhotoAdapter.Item.LoadMore) {
                val position = binding.widget.pxlPhotoAdapter.list.count() - 1
                binding.widget.pxlPhotoAdapter.list.removeAt(position)
                binding.widget.pxlPhotoAdapter.notifyItemRemoved(position)
            }
        }
    }

    fun refreshViewType() {
        binding.widget.currentViewType = getViewType()
    }

    fun getGridSpan(): Int {
        return if (binding.leftLayout.radioGridCount2.isChecked) 2
        else if (binding.leftLayout.radioGridCount3.isChecked) 3
        else if (binding.leftLayout.radioGridCount4.isChecked) 4
        else if (binding.leftLayout.radioGridCount5.isChecked) 5
        else 2
    }

    fun getLineSpace(): Int {
        val text = binding.leftLayout.tvLineSpace.text.toString()
        return if (text.isEmpty()) 0 else text.toInt()
    }

    fun getHeader(): ListHeader? {
        return if (binding.leftLayout.radioHeaderImage.isChecked)
            ListHeader.Gif(url = "https://media.giphy.com/media/dzaUX7CAG0Ihi/giphy.gif", heightInPixel = 200.px.toInt(), imageScaleType = ImageScaleType.CENTER_CROP)
        else if (binding.leftLayout.radioHeaderText.isChecked)
            getTitleSpannable()
        else
            null
    }

    fun getTitleSpannable(): ListHeader {
        val top = "PIXLEE\nSHOPPERS"
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
