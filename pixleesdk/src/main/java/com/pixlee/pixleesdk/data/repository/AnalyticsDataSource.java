package com.pixlee.pixleesdk.data.repository;

import org.json.JSONObject;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.Url;

public interface AnalyticsDataSource {
    Call<String> makeAnalyticsCall(
            String requestPath,
            JSONObject body
    );
}
