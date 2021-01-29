package com.pixlee.pixleeandroidsdk.ui.analytics

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.core.view.GravityCompat
import androidx.core.widget.NestedScrollView
import com.google.android.material.radiobutton.MaterialRadioButton
import com.pixlee.pixleeandroidsdk.BuildConfig
import com.pixlee.pixleesdk.util.EventObserver
import com.pixlee.pixleeandroidsdk.R
import com.pixlee.pixleeandroidsdk.ui.BaseFragment
import com.pixlee.pixleeandroidsdk.ui.BaseViewModel
import com.pixlee.pixleesdk.client.PXLClient
import com.pixlee.pixleesdk.client.PXLKtxAlbum
import com.pixlee.pixleesdk.client.PXLKtxBaseAlbum
import com.pixlee.pixleesdk.data.PXLAlbumFilterOptions
import com.pixlee.pixleesdk.data.PXLAlbumSortOptions
import com.pixlee.pixleesdk.enums.PXLAlbumSortType
import com.pixlee.pixleesdk.enums.PXLContentSource
import com.pixlee.pixleesdk.enums.PXLContentType
import com.pixlee.pixleesdk.enums.PXLWidgetType
import com.pixlee.pixleesdk.ui.widgets.ImageScaleType
import kotlinx.android.synthetic.main.fragment_analytics.*
import kotlinx.android.synthetic.main.module_search.*
import java.util.*

/**
 * This shows how you can fire all analytics of Pixlee.
 * Created by sungjun on 2020-02-13.
 */
class KtxAnalyticsFragment : BaseFragment() {
    override fun getTitleResource(): Int {
        return R.string.title_ktx_analytics
    }

    override fun isBackInUse(): Boolean {
        return if (vWidgetBox.isShown) {
            closeWidget()
            true
        } else {
            false
        }
    }

    val viewModel: KtxAnalyticsViewModel by lazy {
        // get PXLClient
        val album = PXLKtxAlbum(context!!)
        KtxAnalyticsViewModel(album)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_analytics, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // UI Settings
        setViewModelListener()
        setClickListeners()
        setScrollView()
        initFilterClickListeners()

        // Pixlee Settings
        loadPixleeAlbum()
    }

    fun initFilterClickListeners() {
        // set filter buttons
        fabFilter.setOnClickListener { drawerLayout.openDrawer(GravityCompat.END) }
        btnCloseFilter.setOnClickListener { drawerLayout.closeDrawer(GravityCompat.END) }
        btnApply.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.END)
            loadPixleeAlbum()
        }
    }

    fun setViewModelListener() {
        viewModel.loading.observe(this, androidx.lifecycle.Observer {
            vProgress.visibility = if (it) View.VISIBLE else View.INVISIBLE
            if (it) tvStatus.setText(R.string.album_loading_ing)
            enableAlbumButtons(!it)
        })
        viewModel.searchResultEvent.observe(this, EventObserver {
            when (it) {
                is BaseViewModel.Command.Data -> {
                    it.list.firstOrNull()?.also {
                        pxlPhotoView.setContent(it.pxlPhoto, ImageScaleType.FIT_CENTER)
                    }
                    tvStatus.setText(R.string.album_loading_complete)
                    tvStatus.text = if(it.isFirstPage) "First Load" else "Loaded More..."
                    if(it.isFirstPage){
                        // fire analytics after you received more pages
                        viewModel.loadMore()
                    }

                }
                is BaseViewModel.Command.Error -> {
                    it.message?.also {
                        tvStatus.text = getString(R.string.album_loading_failed, it)
                        showToast(getString(R.string.album_loading_failed, it))
                    }

                }
            }
        })
    }

    private fun loadPixleeAlbum() {
        context?.also {
            var searchId: PXLKtxBaseAlbum.SearchId? = null
            // initiate album
            for (i in 0 until radioGroupAlbum.childCount) {
                val rb = radioGroupAlbum.getChildAt(i) as MaterialRadioButton
                if (radioGroupAlbum.checkedRadioButtonId == rb.id) {
                    val text = rb.text.toString()
                    if (text == getString(R.string.radio_album)) {
                        searchId = PXLKtxBaseAlbum.SearchId.Album(BuildConfig.PIXLEE_ALBUM_ID)
                    } else if (text == getString(R.string.radio_pdp)) {
                        searchId = PXLKtxBaseAlbum.SearchId.Product(BuildConfig.PIXLEE_SKU)
                    }
                    break
                }
            }

            if (searchId == null) {
                // if album is not properly started, stop loading it.
                showDialog("No Album", "Album is not properly set. Please check the code and try again")
            } else {
                // set GET request parameters for the API
                viewModel.init(PXLKtxBaseAlbum.Params(
                        searchId = searchId,
                        perPage = readPerPage(),
                        filterOptions = readFilterOptionsFromUI(),
                        sortOptions = readSortOptionsFromUI()
                ))

                PXLClient.regionId = readRegionIdFromUI()

                PXLAlbumSortOptions().apply {
                    sortType = PXLAlbumSortType.RECENCY
                    descending = true
                }

                // retrieve the first page
                viewModel.getFirstPage()
            }
        }
    }

    private fun setClickListeners() {
        btWidgetExample.setOnClickListener(View.OnClickListener { openWidget() })
        btOpenWidget.setOnClickListener(View.OnClickListener {
            showMessage("openedWidget(..)")
            viewModel.openedWidget(PXLWidgetType.photowall)
            // Alternative: album.openedWidget("<Customized name>");
        })
        btWidgetVisible.setOnClickListener(View.OnClickListener {
            showMessage("widgetVisible(..)")
            viewModel.widgetVisible(PXLWidgetType.photowall)
            // Alternative: album.widgetVisible("<Customized name>");
        })
        btLoadMore.setOnClickListener(View.OnClickListener {
            viewModel.getNextPage()
        })
        btOpenedLightbox.setOnClickListener(View.OnClickListener {
            showMessage("openedLightbox()")
            viewModel.openedLightbox()
        })
        btActionClicked.setOnClickListener(View.OnClickListener {
            showMessage("actionClicked()")
            viewModel.actionClicked("<link you want>")
        })
        btAddToCart.setOnClickListener(View.OnClickListener {
            showMessage("addToCart()")
            viewModel.addToCart( BuildConfig.PIXLEE_SKU, "12000", 3)
            // Alternative: viewModel.addToCart("13000",2, "AUD");
        })
        btConversion.setOnClickListener(View.OnClickListener {
            showMessage("conversion()")
            val cartContents: ArrayList<HashMap<String, Any>> = ArrayList()
            val cart1: HashMap<String, Any> = HashMap()
            cart1["price"] = "123"
            cart1["product_sku"] = BuildConfig.PIXLEE_SKU
            cart1["quantity"] = "4"
            cartContents.add(cart1)
            viewModel.conversion(cartContents = cartContents, cartTotal = "123", cartTotalQuantity = 4)
        })
    }

    var widgetStatus: StringBuilder? = null
    var widgetVisible = false
    fun openWidget() {
        viewModel.openedWidget(PXLWidgetType.photowall)

        vWidgetBox.setVisibility(View.VISIBLE)
        val openedWidgetStatus = "openedWidget success"
        addWidgetStaus(true, openedWidgetStatus)
        showToast("$openedWidgetStatus!!\n\nScroll down to fire Widget Visible")
        widgetVisible = false
        val scrollBounds = Rect()
        scrollWidget.getHitRect(scrollBounds)
        scrollWidget.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (pxlPhotoView != null) {
                if (pxlPhotoView.getLocalVisibleRect(scrollBounds)) {
                    if (!widgetVisible) {
                        viewModel.widgetVisible(PXLWidgetType.photowall)
                        val visibleWidgetStatus = "visibleWidget success"
                        addWidgetStaus(false, visibleWidgetStatus)
                        showToast("$visibleWidgetStatus!!")
                        widgetVisible = true
                    }
                    if (!pxlPhotoView.getLocalVisibleRect(scrollBounds)
                            || scrollBounds.height() < pxlPhotoView.height) {
                        Log.i("PXLAnalytics", "btn appears partially")
                    } else {
                        Log.i("PXLAnalytics", "btn appears FULLY!!!")
                    }
                }
            }
        })
    }

    fun closeWidget() {
        scrollWidget.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY -> })
        scrollWidget.fullScroll(ScrollView.FOCUS_UP)
        vWidgetBox.setVisibility(View.GONE)
    }

    fun addWidgetStaus(clearHistory: Boolean, message: String) {
        if (widgetStatus == null || clearHistory) widgetStatus = StringBuilder()
        widgetStatus!!.append("- $message").append("\n")
        tvWidgetStatus.setText(widgetStatus.toString())
    }

    fun setScrollView() {
        tvMsg1.setText(getListMsg("Before Widget Visible", false))
        tvMsg2.setText(getListMsg("After Widget Visible", true))
    }

    fun getListMsg(text: String, isASC: Boolean): String {
        val sb = StringBuilder()
        if (isASC) {
            for (i in 1..100) {
                sb.append("----- $text ${String.format("%03d", i)} ----\n")
            }
        } else {
            for (i in 100 downTo 1) {
                sb.append("----- $text ${String.format("%03d", i)} ----\n")
            }
        }
        return sb.toString()
    }

    private fun enableAlbumButtons(enabled: Boolean) {
        // This conditional means that you can use openWidget() and widgetVisible() on PXLPdpAlbum after receiving album data by firing loadNextPageOfPhotos() is successfully done.
        btOpenWidget.isEnabled = enabled
        btWidgetVisible.isEnabled = enabled
        btWidgetExample.isEnabled = enabled

        btLoadMore.isEnabled = enabled
        btOpenedLightbox.isEnabled = enabled
        btActionClicked.isEnabled = enabled
    }

    private fun showMessage(methodName: String) {
        val message = getString(R.string.xxx_is_called, methodName)
        tvStatus.setText(message)
        showToast(message)
    }


    fun readPerPage(): Int {
        // Set textViewPerPage filter if text is not empty
        val minTwitterFollowers = textViewPerPage.text.toString()
        return if (!minTwitterFollowers.isEmpty()) {
            Integer.valueOf(minTwitterFollowers)
        } else 20

        // a default for perPage
    }

    fun readRegionIdFromUI(): Int?{
        val data = textViewRegionId.text.toString()
        return if (data.isNotEmpty()) {
            Integer.valueOf(data)
        } else null
    }

    fun readSortOptionsFromUI(): PXLAlbumSortOptions {
        val sortOptions = PXLAlbumSortOptions()
        // Set sortType filter if a radio button is selected
        for (i in 0 until radioGroupSortType.childCount) {
            val rb = radioGroupSortType.getChildAt(i) as MaterialRadioButton
            if (radioGroupSortType.checkedRadioButtonId == rb.id) {
                val text = rb.text.toString()
                if (text == PXLAlbumSortType.RECENCY.value) sortOptions.sortType = PXLAlbumSortType.RECENCY
                else if (text == PXLAlbumSortType.APPROVED_TIME.value) sortOptions.sortType = PXLAlbumSortType.APPROVED_TIME
                else if (text == PXLAlbumSortType.RANDOM.value) sortOptions.sortType = PXLAlbumSortType.RANDOM
                else if (text == PXLAlbumSortType.PIXLEE_SHARES.value) sortOptions.sortType = PXLAlbumSortType.PIXLEE_SHARES
                else if (text == PXLAlbumSortType.PIXLEE_LIKES.value) sortOptions.sortType = PXLAlbumSortType.PIXLEE_LIKES
                else if (text == PXLAlbumSortType.POPULARITY.value) sortOptions.sortType = PXLAlbumSortType.POPULARITY
                else if (text == PXLAlbumSortType.DYNAMIC.value) sortOptions.sortType = PXLAlbumSortType.DYNAMIC
                break
            }
        }

        // Set sorting direction
        if (radioGroupSortDirection.checkedRadioButtonId == radioGroupSortDirectionASC.id) sortOptions.descending = false
        else if (radioGroupSortDirection.checkedRadioButtonId == radioGroupSortDirectionDESC.id) sortOptions.descending = true
        return sortOptions
    }

    fun readFilterOptionsFromUI(): PXLAlbumFilterOptions {
        val filterOptions = PXLAlbumFilterOptions()

        // Set minTwitterFollowers filter if text is not empty
        val minTwitterFollowers = textViewMinTwitterFollowers.text.toString()
        if (!minTwitterFollowers.isEmpty()) {
            filterOptions.minTwitterFollowers = Integer.valueOf(minTwitterFollowers)
        }

        // Set minInstagramFollowers filter if text is not empty
        val minInstagramFollowers = textViewMinInstagramFollowers.text.toString()
        if (!minInstagramFollowers.isEmpty()) {
            filterOptions.minInstagramFollowers = Integer.valueOf(minInstagramFollowers)
        }

        // Set hasProduct filter if false or not true is set
        for (i in 0 until radioGroupHasPermission.childCount) {
            val rb = radioGroupHasPermission.getChildAt(i) as MaterialRadioButton
            if (radioGroupHasPermission.checkedRadioButtonId == rb.id) {
                val text = rb.text.toString()
                if (text == getString(R.string.radio_false)) filterOptions.hasPermission = false else if (text == getString(R.string.radio_true)) filterOptions.hasPermission = true
                break
            }
        }

        // Set hasProduct filter if false or not true is set
        for (i in 0 until radioGroupHasProduct.childCount) {
            val rb = radioGroupHasProduct.getChildAt(i) as MaterialRadioButton
            if (radioGroupHasProduct.checkedRadioButtonId == rb.id) {
                val text = rb.text.toString()
                if (text == getString(R.string.radio_false)) filterOptions.hasProduct = false else if (text == getString(R.string.radio_true)) filterOptions.hasProduct = true
                break
            }
        }

        // Set inStockOnly filter if false or not true is set
        for (i in 0 until radioGroupInStockOnly.childCount) {
            val rb = radioGroupInStockOnly.getChildAt(i) as MaterialRadioButton
            if (radioGroupInStockOnly.checkedRadioButtonId == rb.id) {
                val text = rb.text.toString()
                if (text == getString(R.string.radio_false)) filterOptions.inStockOnly = false else if (text == getString(R.string.radio_true)) filterOptions.inStockOnly = true
                break
            }
        }

        // Set contentSource filter if any of its check boxes is selected
        val contentSource: ArrayList<PXLContentSource> = ArrayList()
        if (radioGroupContentSourceInstagramFeed.isChecked) contentSource.add(PXLContentSource.INSTAGRAM_FEED)
        if (radioGroupContentSourceInstagramStory.isChecked) contentSource.add(PXLContentSource.INSTAGRAM_STORY)
        if (radioGroupContentSourceTwitter.isChecked) contentSource.add(PXLContentSource.TWITTER)
        if (radioGroupContentSourceFacebook.isChecked) contentSource.add(PXLContentSource.FACEBOOK)
        if (radioGroupContentSourceApi.isChecked) contentSource.add(PXLContentSource.API)
        if (radioGroupContentSourceDesktop.isChecked) contentSource.add(PXLContentSource.DESKTOP)
        if (radioGroupContentSourceEmail.isChecked) contentSource.add(PXLContentSource.EMAIL)
        if (contentSource.isNotEmpty()) filterOptions.contentSource = contentSource

        // Set contentType filter if any of its check boxes is selected
        val contentType: ArrayList<PXLContentType> = ArrayList()
        if (radioGroupContentTypeImage.isChecked) contentType.add(PXLContentType.IMAGE)
        if (radioGroupContentTypeVideo.isChecked) contentType.add(PXLContentType.VIDEO)
        if (contentType.isNotEmpty()) filterOptions.contentType = contentType

        // Apart from the examples above, there are more filters you can implement in you app.
        // These are the example codes

        // ###### date filter examples ######
        // fo.submittedDateEnd = new Date(2019, 7, 16);
        // fo.submittedDateStart = new Date(2019, 7, 17);
        // fo.filterByRadius = "21.3069,-157.8583,20";  radius filter example

        // ###### in_categories filter example ######
        // ArrayList incategories = new ArrayList<Integer>();
        // incategories.add(1234);
        // incategories.add(5678);
        // fo.inCategories = incategories;

        // ###### filter_by_userhandle filter example ######
        // HashMap userHandleFilter = new HashMap<String, Object> ();
        // userHandleFilter.put("contains", new String[] {"test1", "test2"});
        // fo.filterByUserhandle = userHandleFilter;

        // ###### computer_vision filter example ######
        // HashMap computerVisionFilter = new HashMap<String, Object> ();
        // computerVisionFilter.put("contains", new String[] {"hat"});
        // fo.computerVision = computerVisionFilter;
        return filterOptions
    }
}
