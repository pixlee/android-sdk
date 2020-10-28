package com.pixlee.pixleeandroidsdk.ui.gallery

import androidx.lifecycle.viewModelScope
import com.pixlee.pixleeandroidsdk.ui.BaseViewModel
import com.pixlee.pixleesdk.client.PXLKtxAlbum
import com.pixlee.pixleesdk.data.PXLPhoto
import kotlinx.coroutines.launch

/**
 * Created by sungjun on 9/18/20.
 */
class KtxGalleryViewModel(pxlKtxAlbum: PXLKtxAlbum) : BaseViewModel(pxlKtxAlbum) {
    fun getPhotoWithId(albumPhotoId: String) {
        viewModelScope.launch {
            try {
                pxlKtxAlbum.getPhotoWithId(albumPhotoId)
            } catch (e: Exception) {
                // error handling
            }
        }
    }

    fun getPhotoWithId(pxlPhoto: PXLPhoto) {
        viewModelScope.launch {
            try {
                pxlKtxAlbum.getPhotoWithId(pxlPhoto)
            } catch (e: Exception) {
                // error handling
            }
        }
    }

    fun getPhotoFromRegion(albumPhotoId: String, regionId: Int?) {
        viewModelScope.launch {
            try {
                pxlKtxAlbum.getPhotoFromRegion(albumPhotoId, regionId)
            } catch (e: Exception) {
                // error handling
            }
        }
    }

    fun getPhotoFromRegion(pxlPhoto: PXLPhoto, regionId: Int?) {
        viewModelScope.launch {
            try {
                pxlKtxAlbum.getPhotoFromRegion(pxlPhoto, regionId)
            } catch (e: Exception) {
                // error handling
            }
        }
    }
}