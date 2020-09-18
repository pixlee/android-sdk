package com.pixlee.pixleesdk.client_ktx

import android.content.Context
import android.provider.Settings.Secure
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.pixlee.pixleesdk.client.PXLClient
import com.pixlee.pixleesdk.data.repository.KtxAnalyticsDataSource
import com.pixlee.pixleesdk.data.repository.KtxBasicDataSource
import com.pixlee.pixleesdk.network.NetworkModule

/**
 * Created by sungjun on 9/18/20.
 */
class PXLClientKtx(val context: Context) {
    companion object{
        private var mInstance: PXLClientKtx? = null
        var apiKey: String? = null
        var secretKey: String? = null
        var android_id: String? = null
        /***
         * Gets the PXLClient instance or creates it if it doesn't exist.
         * @param context - used for generating the volley request queue.
         * @return
         */
        @Synchronized
        fun getInstance(context: Context): PXLClientKtx? {
            if (mInstance == null) {
                mInstance = PXLClientKtx(context)
            }
            return mInstance
        }
    }

    init {
        requireNotNull(PXLClient.apiKey) { "no apiKey, please set apiKey before start" }
        Logger.addLogAdapter(AndroidLogAdapter())
        context.applicationContext
        android_id = Secure.getString(context.applicationContext.contentResolver, Secure.ANDROID_ID)
    }

    /***
     * Must be called before use. Sets the api key.
     * @param apiKey
     * @param secretKey can be null if you don't use POST APIs
     */
    fun initialize(apiKey: String, secretKey: String? = null) {
        PXLClientKtx.apiKey = apiKey
        PXLClientKtx.secretKey = secretKey
    }

    private var basicRepo: KtxBasicDataSource? = null
    private var analyticsRepo: KtxAnalyticsDataSource? = null


    fun getBasicRepo(): KtxBasicDataSource {
        if (basicRepo == null) {
            basicRepo = NetworkModule.generateKtxBasicRepository()
        }
        return basicRepo!!
    }

    fun getAnalyticsRepo(): KtxAnalyticsDataSource {
        if (analyticsRepo == null) {
            analyticsRepo = NetworkModule.getKtxAnalyticsRepository()
        }
        return analyticsRepo!!
    }
}