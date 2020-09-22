package com.pixlee.pixleeandroidsdk.ui.analytics

import com.pixlee.pixleeandroidsdk.ext.launchVMScope
import com.pixlee.pixleeandroidsdk.ui.BaseViewModel
import com.pixlee.pixleesdk.client.PXLKtxAlbum
import com.pixlee.pixleesdk.enums.PXLWidgetType
import java.util.ArrayList
import java.util.HashMap

/**
 * Created by sungjun on 9/18/20.
 */
class KtxAnalyticsViewModel(pxlKtxAlbum: PXLKtxAlbum) : BaseViewModel(pxlKtxAlbum) {
    fun openedWidget(pxlWidgetType: PXLWidgetType) {
        launchVMScope({
            pxlKtxAlbum.openedWidget(pxlWidgetType)
        }, {
            // error handling
        })
    }

    fun loadMore() {
        launchVMScope({
            pxlKtxAlbum.loadMore()
        }, {
            // error handling
        })
    }


    fun widgetVisible(pxlWidgetType: PXLWidgetType) {
        launchVMScope({
            pxlKtxAlbum.widgetVisible(pxlWidgetType)
        }, {
            // error handling
        })
    }

    fun openedLightbox() {
        launchVMScope({
            allPXLPhotos.firstOrNull()?.also {
                pxlKtxAlbum.openedLightbox(it)
            }
        }, {
            // error handling
        })
    }

    fun actionClicked(actionLink: String) {
        launchVMScope({
            allPXLPhotos.firstOrNull()?.also {
                pxlKtxAlbum.actionClicked(it, actionLink)
            }
        }, {
            // error handling
        })
    }

    fun addToCart(sku: String, price: String, quantity: Int, currency: String? = null) {
        launchVMScope({
            pxlKtxAlbum.addToCart(sku, price, quantity, currency)
        }, {
            // error handling
        })
    }

    fun conversion(cartContents: ArrayList<HashMap<String, Any>>, cartTotal: String, cartTotalQuantity: Int, orderId: String? = null, currency: String? = null) {
        launchVMScope({
            pxlKtxAlbum.conversion(cartContents, cartTotal, cartTotalQuantity, orderId, currency)
        }, {
            // error handling
        })
    }
}