package com.pixlee.pixleeandroidsdk.ui.gallery

import com.pixlee.pixleeandroidsdk.ext.launchVMScope
import com.pixlee.pixleeandroidsdk.ui.BaseViewModel
import com.pixlee.pixleesdk.client.PXLKtxAlbum
import com.pixlee.pixleesdk.data.PXLPhoto
import com.pixlee.pixleesdk.data.repository.KtxAnalyticsDataSource
import com.pixlee.pixleesdk.data.repository.KtxBasicDataSource

/**
 * Created by sungjun on 9/18/20.
 */
class KtxGalleryViewModel(pxlKtxAlbum: PXLKtxAlbum) : BaseViewModel(pxlKtxAlbum) {
    fun getPhotoWithId(albumPhotoId:String){
        launchVMScope({
            pxlKtxAlbum.getPhotoWithId(albumPhotoId)
        },{
            // error handling
        })
    }

    fun getPhotoWithId(pxlPhoto: PXLPhoto){
        launchVMScope({
            pxlKtxAlbum.getPhotoWithId(pxlPhoto)
        },{
            // error handling
        })
    }
}