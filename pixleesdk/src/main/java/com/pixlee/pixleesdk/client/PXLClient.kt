package com.pixlee.pixleesdk.client

import android.content.Context
import android.provider.Settings.Secure
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.pixlee.pixleesdk.data.repository.AnalyticsDataSource
import com.pixlee.pixleesdk.data.repository.BasicDataSource
import com.pixlee.pixleesdk.data.repository.KtxAnalyticsDataSource
import com.pixlee.pixleesdk.data.repository.KtxBasicDataSource
import com.pixlee.pixleesdk.network.NetworkModule
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull

/**
 * Created by sungjun on 9/18/20.
 */
class PXLClient(val context: Context) {
    companion object{
        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        private var mInstance: PXLClient? = null
        var apiKey: String? = null
        var secretKey: String? = null
        var android_id: String? = null

        /***
         * Must be called before use. Sets the api key.
         * @param apiKey
         * @param secretKey can be null if you don't use POST APIs
         */
        fun initialize(apiKey: String, secretKey: String? = null) {
            Companion.apiKey = apiKey
            Companion.secretKey = secretKey
        }

        /***
         * Gets the PXLClient instance or creates it if it doesn't exist.
         * @param context - used for generating the volley request queue.
         * @return
         */
        @Synchronized
        fun getInstance(context: Context): PXLClient {
            if (mInstance == null) {
                mInstance = PXLClient(context)
            }
            return mInstance!!
        }
    }

    init {
        requireNotNull(PXLClient.apiKey) { "no apiKey, please set apiKey before start" }
        Logger.addLogAdapter(AndroidLogAdapter())
        context.applicationContext
        android_id = Secure.getString(context.applicationContext.contentResolver, Secure.ANDROID_ID)
    }

    val ktxBasicRepo : KtxBasicDataSource by lazy {
        NetworkModule.generateKtxBasicRepository()
    }

    val ktxAnalyticsRepo: KtxAnalyticsDataSource by lazy{
        NetworkModule.getKtxAnalyticsRepository()
    }

    val basicRepo : BasicDataSource by lazy {
        NetworkModule.generateBasicRepository()
    }

    val analyticsRepo: AnalyticsDataSource by lazy{
        NetworkModule.getAnalyticsRepository()
    }
}