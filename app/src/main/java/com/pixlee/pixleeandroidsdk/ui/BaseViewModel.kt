package com.pixlee.pixleeandroidsdk.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pixlee.pixleeandroidsdk.Event
import com.pixlee.pixleeandroidsdk.ext.launchVMScope
import com.pixlee.pixleesdk.data.PXLAlbumFilterOptions
import com.pixlee.pixleesdk.data.PXLAlbumSortOptions
import com.pixlee.pixleesdk.data.repository.KtxAnalyticsDataSource
import com.pixlee.pixleesdk.data.repository.KtxBasicDataSource
import com.pixlee.pixleesdk.ui.viewholder.PhotoWithImageScaleType
import com.pixlee.pixleesdk.ui.widgets.PXLPhotoView
import com.pixlee.pixleesdk.util.px

/**
 * Created by sungjun on 9/18/20.
 */
open class BaseViewModel(val ktxBasicDataSource: KtxBasicDataSource, val ktxAnalyticsDataSource: KtxAnalyticsDataSource) : ViewModel() {
    val pxlPhotos: ArrayList<PhotoWithImageScaleType> = ArrayList()
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

    sealed class SearchSetting {
        class Album(val id: String) : SearchSetting()
        class Product(val sku: String) : SearchSetting()
    }

    var searchSetting: SearchSetting? = null
    var perPage = 30
    var filterOptions: PXLAlbumFilterOptions? = null
    var sortOptions: PXLAlbumSortOptions? = null
    var lastPageLoaded: Int = 0
    var cellHeightInPixel: Int = 200.px.toInt()
    var albumId:Int? = null

    /**
     * This is to set essential request parameters
     */
    fun init(searchSetting: SearchSetting, perPage: Int = 30) {
        this.searchSetting = searchSetting
        this.perPage = perPage
    }

    /**
     * retrieve the first page from Pixlee server
     */
    fun getFirstPage() {
        pxlPhotos.clear()
        lastPageLoaded = 0
        getNextPage()
    }

    /**
     * retrieve the next page from Pixlee server
     */
    fun getNextPage() {
        launchVMScope({
            searchSetting?.let {
                val isFirstPage = lastPageLoaded == 0
                canLoadMore = false
                // show a loading UI on the mobile screen
                _loading.value = true
                when (it) {
                    is SearchSetting.Album -> ktxBasicDataSource.getPhotosWithID(it.id, filterOptions, sortOptions, perPage, ++lastPageLoaded)
                    is SearchSetting.Product -> ktxBasicDataSource.getPhotosWithSKU(it.sku, filterOptions, sortOptions, perPage, ++lastPageLoaded)
                }.let {
                    // update albumId with the albumId from the response
                    albumId = it.albumId

                    if (it.photos.isNotEmpty()) {
                        val newList = ArrayList<PhotoWithImageScaleType>()
                        it.photos.forEach {
                            newList.add(PhotoWithImageScaleType(it, PXLPhotoView.ImageScaleType.CENTER_CROP, cellHeightInPixel))
                        }
                        pxlPhotos.addAll(newList)
                        _resultEvent.value = Event(Command.Data(newList, isFirstPage))
                    }

                    canLoadMore = pxlPhotos.size < it.total
                    _loading.value = false
                }
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