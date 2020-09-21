package com.pixlee.pixleeandroidsdk.ui.analytics

import com.pixlee.pixleeandroidsdk.ext.launchVMScope
import com.pixlee.pixleeandroidsdk.ui.BaseViewModel
import com.pixlee.pixleesdk.data.repository.KtxAnalyticsDataSource
import com.pixlee.pixleesdk.data.repository.KtxBasicDataSource
import com.pixlee.pixleesdk.enums.PXLWidgetType
import java.util.ArrayList
import java.util.HashMap

/**
 * Created by sungjun on 9/18/20.
 */
class KtxAnalyticsViewModel(ktxBasicDataSource: KtxBasicDataSource, ktxAnalyticsDataSource: KtxAnalyticsDataSource) : BaseViewModel(ktxBasicDataSource, ktxAnalyticsDataSource) {
    fun openedWidget(pxlWidgetType: PXLWidgetType){
        launchVMScope({
            searchSetting.also {
                when (it) {
                    is SearchSetting.Album -> ktxAnalyticsDataSource.openedWidget(it.id, pxlPhotos.map { it.pxlPhoto }, perPage, lastPageLoaded, pxlWidgetType)
                }
            }
        }, {
            // error handling
        })
    }

    fun widgetVisible(pxlWidgetType: PXLWidgetType){
        launchVMScope({
            searchSetting.also {
                when (it) {
                    is SearchSetting.Album -> ktxAnalyticsDataSource.widgetVisible(it.id, pxlPhotos.map { it.pxlPhoto }, perPage, lastPageLoaded, pxlWidgetType)
                }
            }
        }, {
            // error handling
        })
    }

    fun addToCart(price: String, quantity: Int, currency: String? = null) {
        launchVMScope({
            searchSetting.also {
                when (it) {
                    is SearchSetting.Product -> ktxAnalyticsDataSource.addToCart(it.sku, price, quantity, currency)
                }
            }
        }, {
// error handling
        })
    }

    fun conversion(cartContents: ArrayList<HashMap<String, Any>>, cartTotal: String, cartTotalQuantity: Int, orderId: String? = null, currency: String? = null) {
        launchVMScope({
            ktxAnalyticsDataSource.conversion(cartContents, cartTotal, cartTotalQuantity, orderId, currency)

        }, {
// error handling
        })
    }

    fun openedLightbox() {
        launchVMScope({
            pxlPhotos.firstOrNull()?.also {
                searchSetting.also { searchSetting ->
                    when (searchSetting) {
                        is SearchSetting.Album -> {
                            ktxAnalyticsDataSource.openedLightbox(searchSetting.id, it.pxlPhoto)
                            // alternative: ktxAnalyticsDataSource.openedLightbox(searchSetting.id, it.pxlPhoto.id, it.pxlPhoto.albumPhotoId)
                        }
                    }
                }
            }
        }, {
            // error handling
        })
    }

    fun actionClicked(actionLink: String) {
        launchVMScope({
            pxlPhotos.firstOrNull()?.also {
                searchSetting.also { searchSetting ->
                    when (searchSetting) {
                        is SearchSetting.Album -> {
                            ktxAnalyticsDataSource.actionClicked(searchSetting.id, it.pxlPhoto, actionLink)
                            // alternative: ktxAnalyticsDataSource.actionClicked(searchSetting.id, it.pxlPhoto.id, it.pxlPhoto.albumPhotoId, actionLink)
                        }
                    }
                }
            }
        }, {
            // error handling
        })
    }
}