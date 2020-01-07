package com.pixlee.pixleesdk.data.api;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Url;


public interface AnalyticsAPI {
    @POST
    Call<String> makeAnalyticsCall(
            @Url String url,
            @Body RequestBody body
    );
}
