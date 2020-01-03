package com.pixlee.pixleesdk.data.api;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface AnalyticsAPI {
    @POST
    Call<String> makeAnalyticsCall(
            @Url String url,
            @Body RequestBody body
    );
}
