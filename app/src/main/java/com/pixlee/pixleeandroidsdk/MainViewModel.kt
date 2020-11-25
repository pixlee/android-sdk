package com.pixlee.pixleeandroidsdk

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pixlee.pixleesdk.client.PXLKtxAlbum
import com.pixlee.pixleesdk.data.PXLPhoto
import kotlinx.coroutines.launch

/**
 * Created by sungjun on 9/18/20.
 */
class MainViewModel(val pxlKtxAlbum: PXLKtxAlbum) : ViewModel() {
    var pxlPhoto:PXLPhoto? = null
    fun updateLive(isLive: Boolean){
        pxlPhoto?.also {
            viewModelScope.launch {
                pxlKtxAlbum.postLives(it, isLive)
            }
        }

    }
}