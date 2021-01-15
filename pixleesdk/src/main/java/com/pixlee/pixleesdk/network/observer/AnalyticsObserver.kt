package com.pixlee.pixleesdk.network.observer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pixlee.pixleesdk.util.Event

/**
 * Created by sungjun on 1/15/21.
 *
 * This class is for UI testing to display fired Analytics API's event names on the Screen
 * for the testing tool to detect which Analytics API received the successful or failed response.
 */

object AnalyticsObserver {
    private val _instantEvent = MutableLiveData<Event<AnalyticsResult>>()

    // KtxGalleryFragment.kt will observe this event
    val instantEvent: LiveData<Event<AnalyticsResult>>
        get() = _instantEvent

    fun push(analyticsResult: AnalyticsResult){
        _instantEvent.postValue(Event(analyticsResult))
    }
}

class AnalyticsResult(val eventName: String, val isSuccessFul: Boolean)