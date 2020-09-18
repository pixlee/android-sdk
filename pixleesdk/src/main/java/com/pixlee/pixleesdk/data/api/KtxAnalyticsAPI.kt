package com.pixlee.pixleesdk.data.api

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

/**
 * This is for Kotlin.coroutines' suspend
 * - Restful API Access Object for analytics APIs
 * - Retrofit Document: https://square.github.io/retrofit/
 */
interface KtxAnalyticsAPI {
    @POST
    suspend fun makeAnalyticsCall(@Url url: String, @Body body: RequestBody): String
}
