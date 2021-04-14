package com.pixlee.pixleesdk.ui.widgets.list

import android.util.Log
import android.util.TypedValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pixlee.pixleesdk.client.PXLKtxAlbum
import com.pixlee.pixleesdk.client.PXLKtxBaseAlbum
import com.pixlee.pixleesdk.data.PXLPhoto
import com.pixlee.pixleesdk.enums.PXLPhotoSize
import com.pixlee.pixleesdk.ui.viewholder.PhotoWithImageScaleType
import com.pixlee.pixleesdk.ui.widgets.PXLPhotoView
import com.pixlee.pixleesdk.ui.widgets.TextPadding
import com.pixlee.pixleesdk.ui.widgets.TextViewStyle
import com.pixlee.pixleesdk.util.Event
import com.pixlee.pixleesdk.util.px

/**
 * Created by sungjun on 9/18/20.
 */
open class ListViewModel(val pxlKtxAlbum: PXLKtxAlbum){

    protected val _resultEvent = MutableLiveData<Event<Command>>()

    // KtxGalleryFragment.kt will observe this event
    val searchResultEvent: LiveData<Event<Command>>
        get() = _resultEvent

    sealed class Command {
        class Data(val list: List<PhotoWithImageScaleType>, val isFirstPage: Boolean) : Command()
        class Error(val message: String?) : Command()
    }

    enum class LoadState {
        LOAD_MORE_BUTTON, LOADING, HIDE
    }

    protected val _loading = MutableLiveData<LoadState>().apply { value = LoadState.LOADING }
    val loading: LiveData<LoadState>
        get() = _loading

    var cellHeightInPixel: Int = 200.px.toInt()
    val allPXLPhotos = ArrayList<PXLPhoto>()
    var canLoadMore = true

    /**
     * This is to set essential request parameters
     */
    fun init(params: PXLKtxBaseAlbum.Params) {
        pxlKtxAlbum.params = params
    }

    /**
     * retrieve the first page from Pixlee server
     */
    suspend fun getFirstPage() {
        allPXLPhotos.clear()
        pxlKtxAlbum.resetState()
        getNextPage()

        // alternative
        // viewModelScope.launch(..) { pxlKtxAlbum.getFirstPage() }
    }

    var customizedConfiguration:PXLPhotoView.Configuration = PXLPhotoView.Configuration()

    var backUpLoadState:LoadState? = LoadState.HIDE
    /**
     * retrieve the next page from Pixlee server
     */
    suspend fun getNextPage() {
        try {
            canLoadMore = false
            // show a loading UI on the mobile screen
            backUpLoadState = _loading.value
            _loading.value = LoadState.LOADING
            pxlKtxAlbum.getNextPage().let {
                val newList = ArrayList<PhotoWithImageScaleType>()
                if (it.photos.isNotEmpty()) {
                    it.photos.forEach {
                        newList.add(PhotoWithImageScaleType(pxlPhoto = it,
                                configuration = customizedConfiguration,
                                heightInPixel = cellHeightInPixel,
                                isLoopingVideo = true,
                                soundMuted = false))
                    }
                    allPXLPhotos.addAll(it.photos)
                }
                _resultEvent.value = Event(Command.Data(newList, it.page == 1))

                canLoadMore = it.next
                _loading.value = if(it.next) LoadState.LOAD_MORE_BUTTON else LoadState.HIDE
            }
        } catch (e: Exception) {
            // Callback for a failed call to loadNextPageOfPhotos
            Log.e("pixlee", String.format("Failed to fetch next page of photos: %s", e.message))
            _resultEvent.value = Event(Command.Error(e.message))
            canLoadMore = true
            _loading.value = backUpLoadState
        }
    }

//    val LIST_VISIBLE_THRESHOLD = 5
//    fun listScrolled(visibleItemCount: Int, lastVisibleItemPosition: Int, totalItemCount: Int) {
//        val canScroll = visibleItemCount + lastVisibleItemPosition + LIST_VISIBLE_THRESHOLD >= totalItemCount
//        if (visibleItemCount + lastVisibleItemPosition + LIST_VISIBLE_THRESHOLD >= totalItemCount) {
//            if (canLoadMore) {
//                getNextPage()
//            }
//        }
//    }
}