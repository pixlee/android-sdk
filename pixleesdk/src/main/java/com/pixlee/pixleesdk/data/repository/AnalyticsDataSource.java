package com.pixlee.pixleesdk.data.repository;

import org.json.JSONObject;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.Url;

/**
 * The entry point for accessing analytics data.
 */
public interface AnalyticsDataSource {

    /***
     * Makes a call to the Pixlee Analytics API (limitless beyond). Appends api key, unique id and platform to the request body.
     * on success/error.
     * @param requestPath - path to hit (will be appended to the base Pixlee Analytics api endpoint)
     * @param body - key/values to be stored in analytics events
     * @return false if no api key set yet, true otherwise
     */
    Call<String> makeAnalyticsCall(
            String requestPath,
            JSONObject body
    );
}
