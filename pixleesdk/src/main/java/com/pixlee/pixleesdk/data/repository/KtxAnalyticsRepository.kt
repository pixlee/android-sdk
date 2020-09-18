package com.pixlee.pixleesdk.data.repository

import com.pixlee.pixleesdk.client.PXLClient
import com.pixlee.pixleesdk.data.api.AnalyticsAPI
import com.pixlee.pixleesdk.data.api.KtxAnalyticsAPI
import com.pixlee.pixleesdk.network.NetworkModule
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call

/**
 * Created by sungjun on 9/11/20.
 */

/**
 * The entry point for accessing analytics data.
 */
interface KtxAnalyticsDataSource {

    /***
     * Makes a call to the Pixlee Analytics API (limitless beyond). Appends api key, unique id and platform to the request body.
     * on success/error.
     * @param requestPath - path to hit (will be appended to the base Pixlee Analytics api endpoint)
     * @param body - key/values to be stored in analytics events
     * @return false if no api key set yet, true otherwise
     */
    suspend fun makeAnalyticsCall(requestPath: String, body: JSONObject): String
}

/**
 * This object transfers analytics data to the server using AnalyticsAPI.java, a Retrofit HTTP API class.
 */
class KtxAnalyticsRepository(var api: KtxAnalyticsAPI) : KtxAnalyticsDataSource {
    override suspend fun makeAnalyticsCall(requestPath: String, json: JSONObject): String {
        try {
            json.put("API_KEY", PXLClient.apiKey)
            json.put("uid", PXLClient.android_id)
            json.put("platform", "android")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString())
        return api.makeAnalyticsCall(NetworkModule.analyticsUrl + requestPath, body)
    }
}
