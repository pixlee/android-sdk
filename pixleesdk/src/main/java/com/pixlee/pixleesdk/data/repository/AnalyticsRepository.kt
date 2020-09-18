package com.pixlee.pixleesdk.data.repository

import com.pixlee.pixleesdk.client.PXLClient
import com.pixlee.pixleesdk.data.api.AnalyticsAPI
import com.pixlee.pixleesdk.network.NetworkModule
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call

/**
 * Created by sungjun on 9/11/20.
 */

/**
 * The entry point for accessing analytics data.
 */
interface AnalyticsDataSource {

    /***
     * Makes a call to the Pixlee Analytics API (limitless beyond). Appends api key, unique id and platform to the request body.
     * on success/error.
     * @param requestPath - path to hit (will be appended to the base Pixlee Analytics api endpoint)
     * @param body - key/values to be stored in analytics events
     * @return false if no api key set yet, true otherwise
     */
    fun makeAnalyticsCall(
            requestPath: String,
            body: JSONObject
    ): Call<String>
}

/**
 * This object transfers analytics data to the server using AnalyticsAPI.java, a Retrofit HTTP API class.
 */
class AnalyticsRepository(var api: AnalyticsAPI) : AnalyticsDataSource {
    override fun makeAnalyticsCall(requestPath: String, json: JSONObject): Call<String> {
        try {
            json.put("API_KEY", PXLClient.apiKey)
            json.put("uid", PXLClient.android_id)
            json.put("platform", "android")
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return api.makeAnalyticsCall(NetworkModule.analyticsUrl + requestPath, json.toString().toRequestBody(PXLClient.mediaType))
    }
}
