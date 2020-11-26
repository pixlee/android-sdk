package com.pixlee.pixleeandroidsdk.ui

import android.util.Log
import android.util.TypedValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pixlee.pixleeandroidsdk.Event
import com.pixlee.pixleesdk.client.PXLKtxAlbum
import com.pixlee.pixleesdk.client.PXLKtxBaseAlbum
import com.pixlee.pixleesdk.data.PXLLive
import com.pixlee.pixleesdk.data.PXLPhoto
import com.pixlee.pixleesdk.data.PXLRegion
import com.pixlee.pixleesdk.enums.PXLPhotoSize
import com.pixlee.pixleesdk.ui.viewholder.PhotoWithImageScaleType
import com.pixlee.pixleesdk.ui.widgets.PXLPhotoView
import com.pixlee.pixleesdk.ui.widgets.TextPadding
import com.pixlee.pixleesdk.ui.widgets.TextViewStyle
import com.pixlee.pixleesdk.util.px
import kotlinx.coroutines.launch

/**
 * Created by sungjun on 9/18/20.
 */
open class BaseViewModel(val pxlKtxAlbum: PXLKtxAlbum) : ViewModel() {

    protected val _resultEvent = MutableLiveData<Event<ImageCommand>>()

    // KtxGalleryFragment.kt will observe this event
    val searchResultEvent: LiveData<Event<ImageCommand>>
        get() = _resultEvent

    sealed class ImageCommand {
        class Data(val list: List<PhotoWithImageScaleType>, val isFirstPage: Boolean) : ImageCommand()
        class Error(val message: String?) : ImageCommand()
    }

    protected val _loading = MutableLiveData<Boolean>().apply { value = false }
    val loading: LiveData<Boolean>
        get() = _loading

    var cellHeightInPixel: Int = 200.px.toInt()
    val allPXLPhotos = ArrayList<PXLPhoto>()

    protected val _regions = MutableLiveData<Command<List<PXLRegion>>>().apply { Command.NoData }
    val regions: LiveData<Command<List<PXLRegion>>>
        get() = _regions

    fun loadRegions() {
        _regions.value = Command.Loading
        viewModelScope.launch {
            try {
                _regions.value = Command.Data(pxlKtxAlbum.getRegions())
            } catch (e: Exception) {
                Log.e("pixlee", String.format("Failed to fetch next page of photos: %s", e.message))
                _regions.value = Command.NoData
            }
        }
    }

    protected val _lives = MutableLiveData<Command<List<PXLLive>>>().apply { Command.NoData }
    val lives: LiveData<Command<List<PXLLive>>>
        get() = _lives

    fun loadLives() {
        _lives.value = Command.Loading
        viewModelScope.launch {
            try {
                _lives.value = Command.Data(pxlKtxAlbum.getLives())

            } catch (e: Exception) {
                Log.e("pixlee", String.format("Failed to fetch lives: %s", e.message))
                _lives.value = Command.NoData
            }
        }
    }

    protected val _eventPxlPhoto = MutableLiveData<Event<Command<PXLPhoto>>>()
    val eventLiveDetail: LiveData<Event<Command<PXLPhoto>>>
        get() = _eventPxlPhoto

    fun getLivePhotoFromRegion(albumPhotoId: String, regionId: Int?) {
        _eventPxlPhoto.value = Event(Command.Loading)
        viewModelScope.launch {
            try {
                _eventPxlPhoto.value = Event(Command.Data(pxlKtxAlbum.getPhotoFromRegion(albumPhotoId, regionId)))
            } catch (e: Exception) {
                // error handling
                _eventPxlPhoto.value = Event(Command.NoData)
            }
        }
    }

    /**
     * This is to set essential request parameters
     */
    fun init(params: PXLKtxBaseAlbum.Params) {
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

    var customizedConfiguration: PXLPhotoView.Configuration = PXLPhotoView.Configuration()

    var liveHashMap: HashMap<String, PXLLive>? = null

    /**
     * retrieve the next page from Pixlee server
     */
    fun getNextPage() {
        viewModelScope.launch {
            try {

                if (liveHashMap == null) {
                    _lives.value = Command.Loading
                    try {
                        val data = pxlKtxAlbum.getLives()
                        liveHashMap = HashMap()
                        data.forEach {
                            liveHashMap?.put(it.albumPhotoId.toString(), it)
                        }
                        _lives.value = Command.Data(data)
                    } catch (e: Exception) {
                        Log.e("pixlee", String.format("Failed to fetch lives: %s", e.message))
                        _lives.value = Command.NoData
                    }
                }


                canLoadMore = false
                // show a loading UI on the mobile screen
                _loading.value = true
                pxlKtxAlbum.getNextPage().let {
                    val newList = ArrayList<PhotoWithImageScaleType>()
                    if (it.photos.isNotEmpty()) {
                        it.photos.forEach {
                            Log.e("pxlvideo", "pxlvideo.url: ${it.videoUrl.toString()}")
                            Log.e("pxlvideo", "pxlvideo.big: ${it.getUrlForSize(PXLPhotoSize.BIG)}")

                            if (liveHashMap == null || liveHashMap!![it.albumPhotoId]==null) {
                                newList.add(PhotoWithImageScaleType(pxlPhoto = it,
                                        configuration = customizedConfiguration.copy().apply {
                                            mainTextViewStyle = TextViewStyle().apply {
                                                text = "${newList.size}\n${customizedConfiguration.mainTextViewStyle?.text ?: ""}"
                                                size = 30.px
                                                sizeUnit = TypedValue.COMPLEX_UNIT_PX
                                                typeface = null
                                                textPadding = TextPadding(bottom = 30.px.toInt())
                                            }
                                        },
                                        heightInPixel = cellHeightInPixel,
                                        isLoopingVideo = true,
                                        soundMuted = false))
                            }

                        }
                        allPXLPhotos.addAll(it.photos)
                    }
                    _resultEvent.value = Event(ImageCommand.Data(newList, it.page == 1))

                    canLoadMore = it.next
                    _loading.value = false
                }
            } catch (e: Exception) {
                // Callback for a failed call to loadNextPageOfPhotos
                Log.e("pixlee", String.format("Failed to fetch next page of photos: %s", e.message))
                _resultEvent.value = Event(ImageCommand.Error(e.message))
                canLoadMore = true
                _loading.value = false
            }
        }
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