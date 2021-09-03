package com.pixlee.pixleeandroidsdk

import android.app.Application
import com.pixlee.pixleesdk.client.PXLClient

/**
 * Created by sungjun on 9/18/20.
 */
class AppApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        // set credentials for the SDK
        PXLClient.initialize(BuildConfig.PIXLEE_API_KEY, BuildConfig.PIXLEE_SECRET_KEY)

        // (Optional) if you use UI components and want
        // to let PXLPhotoRecyclerViewInGrid, PXLPhotoRecyclerView and PXLPhotoProductView
        // fire 'openedWidget', 'widgetVisible' and 'openedLightbox' analytics events, use this.
        PXLClient.autoAnalyticsEnabled = true

        // (Optional) if you use multi-region, you can set your region id here to get photos, a photo, and products available in the region.
        val regionId:Int? = null
        PXLClient.regionId = regionId

        // (Optional) if you need to change timeouts of APIs, you can do it with these.
        PXLClient.apiTimeoutRead = null // null will use 60 seconds
        PXLClient.apiTimeoutConnect = null // null will use 60 seconds
        PXLClient.apiTimeoutWrite = null // null will use 180 seconds
    }
}