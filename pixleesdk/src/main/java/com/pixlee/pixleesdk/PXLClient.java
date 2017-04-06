package com.pixlee.pixleesdk;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by jason on 3/30/2017.
 */

public class PXLClient {
    public static final String KeyFilters = "filters";
    public static final String KeySort = "sort";
    public static final String KeyApiKey = "api_key";
    public static final String KeyPage = "page";
    public static final String KeyPerPage = "per_page";

    private static PXLClient mInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static Context mCtx;
    private static final String url = "https://distillery.pixlee.com/api/v2";
    private static String apiKey = null;

    private PXLClient() {
        mRequestQueue = getRequestQueue();
        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<String, Bitmap>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }

    public static void initialize(String apiKey, Context context) {
        PXLClient.apiKey = apiKey;
        PXLClient.mCtx = context;
    }

    public static synchronized PXLClient getInstance() {
        if (mInstance == null) {
            mInstance = new PXLClient();
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }



    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    /***
     * makes a call to the pixlee api
     * @param caller - gives callbacks access to the caller object
     * @param requestHandlers - ugh, shouldn't need both
     * @param requestPath - path to hit (will be appended to the base pixlee api endpoint)
     * @param callbacks - ugh, shouldn't need both
     * @return false if no api key set yet, true otherwise
     */
    public boolean makeCall(String requestPath, HashMap<String, Object> parameters, final Object caller, final RequestCallbacks callbacks) {
        if (PXLClient.apiKey == null) {
            return false;
        }
        String paramString = String.format("%s=%s", KeyApiKey, apiKey);
        if (parameters != null && parameters.size() > 0) {
            for (Map.Entry<String, Object> param : parameters.entrySet()) {
                paramString += String.format("&%s=%s", param.getKey(), param.getValue().toString());
            }
        }
        String finalUrl = String.format("%s/%s?%s", url, requestPath, paramString);
        Log.w("pxlclient", String.format("making a call to %s", finalUrl));
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, finalUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("pxlclient", "got a success response, making callback");
                callbacks.JsonReceived(caller, response);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("pxlclient", "got an error response");
                callbacks.ErrorResponse(error);
            }
        });
        this.addToRequestQueue(jor);
        return true;
    }
}