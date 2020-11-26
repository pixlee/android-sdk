package com.pixlee.pixleeandroidsdk.ui.live

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pixlee.pixleeandroidsdk.ui.BaseViewModel
import com.pixlee.pixleesdk.client.PXLKtxAlbum
import com.pixlee.pixleesdk.data.PXLPhoto
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Created by sungjun on 9/18/20.
 */
class LiveViewerViewModel(val pxlKtxAlbum: PXLKtxAlbum) : ViewModel() {
    fun postLives(pxlPhoto: PXLPhoto, isLive: Boolean) {
        GlobalScope.launch {
            pxlPhoto?.also {
                pxlKtxAlbum.postLives(it, isLive)
            }
        }

        if(isLive){
            keepRunning(pxlPhoto)
        }else{
            job?.cancel()
        }
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

    var job: Job? = null
    fun keepRunning(pxlPhoto: PXLPhoto){
        job?.cancel()
        job = viewModelScope.launch {
            pxlPhoto?.also {
                while(true){
                    delay(10000)
                    pxlKtxAlbum.postLives(it, true)
                }
            }
        }
    }
}