package com.pixlee.pixleeandroidsdk.ui.analytics

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.core.widget.NestedScrollView
import com.pixlee.pixleeandroidsdk.BuildConfig
import com.pixlee.pixleeandroidsdk.EventObserver
import com.pixlee.pixleeandroidsdk.R
import com.pixlee.pixleeandroidsdk.ui.BaseFragment
import com.pixlee.pixleeandroidsdk.ui.BaseViewModel
import com.pixlee.pixleesdk.client.PXLKtxAlbum
import com.pixlee.pixleesdk.client.PXLKtxBaseAlbum
import com.pixlee.pixleesdk.enums.PXLWidgetType
import com.pixlee.pixleesdk.ui.widgets.ImageScaleType
import kotlinx.android.synthetic.main.fragment_analytics.*
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

        // Pixlee Settings
        initPixleeAlbum()
        loadPixleeAlbum()
    }

    fun setViewModelListener() {
        viewModel.loading.observe(this, androidx.lifecycle.Observer {
            vProgress.visibility = if (it) View.VISIBLE else View.INVISIBLE
            if (it) tvStatus.setText(R.string.album_loading_ing)
            enableAlbumButtons(!it)
        })
        viewModel.searchResultEvent.observe(this, EventObserver {
            when (it) {
                is BaseViewModel.ImageCommand.Data -> {
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
                is BaseViewModel.ImageCommand.Error -> {
                    it.message?.also {
                        tvStatus.text = getString(R.string.album_loading_failed, it)
                        showToast(getString(R.string.album_loading_failed, it))
                    }

                }
            }
        })
    }

    private fun initPixleeAlbum() {
        val params = PXLKtxBaseAlbum.Params(PXLKtxBaseAlbum.SearchId.Album(BuildConfig.PIXLEE_ALBUM_ID))
        // Alternative val params = PXLKtxBaseAlbum.Params(PXLKtxBaseAlbum.SearchId.Product(BuildConfig.PIXLEE_SKU))
        viewModel.init(params)
    }

    private fun loadPixleeAlbum() {
        viewModel.getFirstPage()
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
                            || scrollBounds.height() < pxlPhotoView.getHeight()) {
                        Log.i("PXLAnalytics", "BTN APPEAR PARCIALY")
                    } else {
                        Log.i("PXLAnalytics", "BTN APPEAR FULLY!!!")
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
}
