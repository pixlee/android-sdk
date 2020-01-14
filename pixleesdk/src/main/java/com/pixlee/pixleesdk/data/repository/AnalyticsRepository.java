package com.pixlee.pixleesdk.data.repository;

import com.pixlee.pixleesdk.data.api.AnalyticsAPI;
import com.pixlee.pixleesdk.data.api.BasicAPI;
import com.pixlee.pixleesdk.network.NetworkModule;

import org.json.JSONObject;

import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * This object transfers analytics data to the server using AnalyticsAPI.java, a Retrofit HTTP API class.
 */
public class AnalyticsRepository implements AnalyticsDataSource {
    AnalyticsAPI api;

    public AnalyticsRepository(AnalyticsAPI api) {
        this.api = api;
    }


    @Override
    public Call<String> makeAnalyticsCall(String requestPath, JSONObject json) {
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());

        return api.makeAnalyticsCall(NetworkModule.analyticsUrl + requestPath, body);
    }
}
