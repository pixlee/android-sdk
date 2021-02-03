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
    }
}