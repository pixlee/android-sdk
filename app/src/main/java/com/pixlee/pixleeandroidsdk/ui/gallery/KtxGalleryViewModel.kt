package com.pixlee.pixleeandroidsdk.ui.gallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.pixlee.pixleeandroidsdk.ui.BaseViewModel
import com.pixlee.pixleesdk.client.PXLKtxAlbum
import com.pixlee.pixleesdk.data.PXLPhoto
import com.pixlee.pixleesdk.ui.viewholder.PhotoWithImageScaleType
import com.pixlee.pixleesdk.util.Event
import kotlinx.coroutines.launch
import org.json.JSONObject

/**
 * Created by sungjun on 9/18/20.
 */
class KtxGalleryViewModel(pxlKtxAlbum: PXLKtxAlbum) : BaseViewModel(pxlKtxAlbum) {
    protected val _toastMessage = MutableLiveData<Event<String>>()
    val toastMessage: LiveData<Event<String>>
        get() = _toastMessage

    protected val _uploadStatus = MutableLiveData<Boolean>().apply { value = false }
    val uploadStatus: LiveData<Boolean>
        get() = _uploadStatus

    fun uploadPhoto(localMediaPath: String, title: String, email: String, username: String, approved: Boolean, productSKUs: List<String>? = null, categoryNames: List<String>? = null, connectedUser: JSONObject? = null) {
        _uploadStatus.value = true
        viewModelScope.launch {
            try {
                _toastMessage.value = Event("Uploading, Please wait..")
                pxlKtxAlbum.postMediaWithFile(localMediaPath, title, email, username, approved, productSKUs, categoryNames, connectedUser)
                _uploadStatus.value = false
                _toastMessage.value = Event("Uploaded Successfully")
            } catch (e: Exception) {
                e.printStackTrace()
                _uploadStatus.value = false
                _toastMessage.value = Event("Upload Failed")
            }
        }
    }

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
}