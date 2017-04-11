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

/***
 * Manages the configuration of volley and calls to the api. Intended to be used as a singleton,
 * so access should occur via the getInstance() method.
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
    private Context mCtx;
    private static final String url = "https://distillery.pixlee.com/api/v2";
    private static String apiKey = null;

    private PXLClient(Context context) {
        mCtx = context;
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

    /***
     * Must be called before use. Sets the api key.
     * @param apiKey
     */
    public static void initialize(String apiKey) {
        PXLClient.apiKey = apiKey;
    }

    /***
     * Gets the PXLClient instance or creates it if it doesn't exist.
     * @param context - used for generating the volley request queue.
     * @return
     */
    public static synchronized PXLClient getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new PXLClient(context);
        }
        return mInstance;
    }

    /***
     * Returns or generates the volley request queue.
     * @return
     */
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    /***
     * Adds a request to the volley request queue.
     * @param req
     * @param <T>
     */
    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    /***
     * Returns the volley image loader.
     * @return
     */
    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    /***
     * Makes a call to the Pixlee API. Appends api key to the request. Invokes the given callbacks
     * on success/error.
     * @param requestPath - path to hit (will be appended to the base Pixlee api endpoint)
     * @param callbacks - called after request succeeds or fails
     * @return false if no api key set yet, true otherwise
     */
    public boolean makeCall(String requestPath, HashMap<String, Object> parameters, final RequestCallbacks callbacks) {
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
        //System.err.println(String.format("making a call to %s", finalUrl));
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, finalUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("pxlclient", "got a success response, making callback");
                callbacks.JsonReceived(response);
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