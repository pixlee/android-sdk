package com.pixlee.pixleesdk.data.api

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pixlee.pixleesdk.util.Event

/**
 * Created by sungjun on 1/15/21.
 * There are paths of analytics APIs
 */
object AnalyticsEvents {
    // Basic events
    const val loadMore = "loadMore" // automatic only
    const val widgetVisible = "widgetVisible" // both
    const val openedWidget = "openedWidget" // both
    const val openedLightbox = "openedLightbox" // both
    const val actionClicked = "actionClicked" // manual only

    // Commerce events
    const val addToCart = "addToCart" // manual only
    const val conversion = "conversion" // manual only
}