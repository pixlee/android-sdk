package com.pixlee.pixleeandroidsdk.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pixlee.pixleeandroidsdk.Event
import com.pixlee.pixleeandroidsdk.ext.launchVMScope
import com.pixlee.pixleesdk.client.PXLKtxAlbum
import com.pixlee.pixleesdk.client.PXLKtxBaseAlbum
import com.pixlee.pixleesdk.data.PXLPhoto
import com.pixlee.pixleesdk.ui.viewholder.PhotoWithImageScaleType
import com.pixlee.pixleesdk.ui.widgets.PXLPhotoView
import com.pixlee.pixleesdk.util.px

/**
 * Created by sungjun on 9/18/20.
 */
open class BaseViewModel(val pxlKtxAlbum: PXLKtxAlbum) : ViewModel() {

    protected val _resultEvent = MutableLiveData<Event<Command>>()

    // KtxGalleryFragment.kt will observe this event
    val searchResultEvent: LiveData<Event<Command>>
        get() = _resultEvent

    sealed class Command {
        class Data(val list: List<PhotoWithImageScaleType>, val isFirstPage: Boolean) : Command()
        class Error(val message: String?) : Command()
    }

    protected val _loading = MutableLiveData<Boolean>().apply { value = false }
    val loading: LiveData<Boolean>
        get() = _loading

    var cellHeightInPixel: Int = 200.px.toInt()
    val allPXLPhotos = ArrayList<PXLPhoto>()

    /**
     * This is to set essential request parameters
     */
    fun init(params:PXLKtxBaseAlbum.Params) {
        pxlKtxAlbum.params = params
    }

    /**
     * retrieve the first page from Pixlee server
     */
    fun getFirstPage() {
        allPXLPhotos.clear()
         pxlKtxAlbum.resetState()
         getNextPage()

        // alternative
        // viewModelScope.launch(..) { pxlKtxAlbum.getFirstPage() }
    }

    /**
     * retrieve the next page from Pixlee server
     */
    fun getNextPage() {
        launchVMScope({
            canLoadMore = false
            // show a loading UI on the mobile screen
            _loading.value = true
            pxlKtxAlbum.getNextPage().let {
                if (it.photos.isNotEmpty()) {
                    val newList = ArrayList<PhotoWithImageScaleType>()
                    it.photos.forEach {
                        newList.add(PhotoWithImageScaleType(it, PXLPhotoView.ImageScaleType.FIT_CENTER, cellHeightInPixel))
                    }
                    allPXLPhotos.addAll(it.photos)
                    _resultEvent.value = Event(Command.Data(newList, it.page==1))
                }

                canLoadMore = it.next
                _loading.value = false
            }
        }, {
            // Callback for a failed call to loadNextPageOfPhotos
            Log.e("pixlee", String.format("Failed to fetch next page of photos: %s", it.message))
            _resultEvent.value = Event(Command.Error(it.message))
            canLoadMore = true
            _loading.value = false
        })
    }

    var canLoadMore = true
    val LIST_VISIBLE_THRESHOLD = 5
    fun listScrolled(visibleItemCount: Int, lastVisibleItemPosition: Int, totalItemCount: Int) {
        val canScroll = visibleItemCount + lastVisibleItemPosition + LIST_VISIBLE_THRESHOLD >= totalItemCount
        if (visibleItemCount + lastVisibleItemPosition + LIST_VISIBLE_THRESHOLD >= totalItemCount) {
            if (canLoadMore) {
                getNextPage()
            }
        }
    }
}