package com.pixlee.pixleeandroidsdk.ui.analytics

import androidx.lifecycle.viewModelScope
import com.pixlee.pixleeandroidsdk.ui.BaseViewModel
import com.pixlee.pixleesdk.client.PXLKtxAlbum
import com.pixlee.pixleesdk.enums.PXLWidgetType
import kotlinx.coroutines.launch
import java.util.*

/**
 * Created by sungjun on 9/18/20.
 */
class KtxAnalyticsViewModel(pxlKtxAlbum: PXLKtxAlbum) : BaseViewModel(pxlKtxAlbum) {
    fun openedWidget(pxlWidgetType: PXLWidgetType) {
        viewModelScope.launch {
            try {
                pxlKtxAlbum.openedWidget(pxlWidgetType)
            } catch (e: Exception) {
                // error handling
            }
        }
    }

    fun loadMore() {
        viewModelScope.launch {
            try {
                pxlKtxAlbum.loadMore()
            } catch (e: Exception) {
                // error handling
            }
        }
    }


    fun widgetVisible(pxlWidgetType: PXLWidgetType) {
        viewModelScope.launch {
            try {
                pxlKtxAlbum.widgetVisible(pxlWidgetType)
            } catch (e: Exception) {
                // error handling
            }
        }
    }

    fun openedLightbox() {
        viewModelScope.launch {
            try {
                allPXLPhotos.firstOrNull()?.also {
                    pxlKtxAlbum.openedLightbox(it)
                }
            } catch (e: Exception) {
                // error handling
            }
        }
    }

    fun actionClicked(actionLink: String) {
        viewModelScope.launch {
            try {
                allPXLPhotos.firstOrNull()?.also {
                    pxlKtxAlbum.actionClicked(it, actionLink)
                }
            } catch (e: Exception) {
                // error handling
            }
        }
    }

    fun addToCart(sku: String, price: String, quantity: Int, currency: String? = null) {
        viewModelScope.launch {
            try {
                pxlKtxAlbum.addToCart(sku, price, quantity, currency)
            } catch (e: Exception) {
                // error handling
            }
        }
    }

    fun conversion(cartContents: ArrayList<HashMap<String, Any>>, cartTotal: String, cartTotalQuantity: Int, orderId: String? = null, currency: String? = null) {
        viewModelScope.launch {
            try {
                pxlKtxAlbum.conversion(cartContents, cartTotal, cartTotalQuantity, orderId, currency)
            } catch (e: Exception) {
                // error handling
            }
        }
    }
}