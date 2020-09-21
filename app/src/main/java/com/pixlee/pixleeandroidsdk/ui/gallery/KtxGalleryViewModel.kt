package com.pixlee.pixleeandroidsdk.ui.gallery

import com.pixlee.pixleeandroidsdk.ui.BaseViewModel
import com.pixlee.pixleesdk.data.repository.KtxAnalyticsDataSource
import com.pixlee.pixleesdk.data.repository.KtxBasicDataSource

/**
 * Created by sungjun on 9/18/20.
 */
class KtxGalleryViewModel(ktxBasicDataSource: KtxBasicDataSource,
                          ktxAnalyticsDataSource: KtxAnalyticsDataSource)
    : BaseViewModel(ktxBasicDataSource, ktxAnalyticsDataSource)