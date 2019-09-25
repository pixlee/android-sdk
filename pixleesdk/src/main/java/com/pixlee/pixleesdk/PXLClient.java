package com.pixlee.pixleesdk;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Base64;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.AuthFailureError;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONObject;
import org.json.JSONException;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import 	java.io.UnsupportedEncodingException;

import android.provider.Settings.Secure;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Mac;
import java.security.InvalidKeyException;
/***
 * Manages the configuration of volley and calls to the api. Intended to be used as a singleton,
 * so access should occur via the getInstance() method.
 */
public class PXLClient {
    private static final String TAG = "PXLClient";
    public static final String KeyFilters = "filters";
    public static final String KeySort = "sort";
    public static final String KeyApiKey = "api_key";
    public static final String KeyPage = "page";
    public static final String KeyPerPage = "per_page";
    public static final String KeySku = "sku";

    public static final String ACTION_MEDIA = "media";

    private static PXLClient mInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private Context mCtx;
    private static final String url = "https://distillery.pixlee.com/api/v2";
    private static final String analyticsUrl = "https://inbound-analytics.pixlee.com";
    private static String apiKey = null;
    private static String secretKey = null;
    private static String android_id = null;

    private PXLClient(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
        android_id = Secure.getString(mCtx.getContentResolver(), Secure.ANDROID_ID);
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
     * @param secretKey
     */
    public static void initialize(String apiKey, String secretKey) {
        PXLClient.apiKey = apiKey;
        PXLClient.secretKey = secretKey;
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

    public String computeHmac(String baseString, String secretKey)
            throws NoSuchAlgorithmException, InvalidKeyException, IllegalStateException,  UnsupportedEncodingException
    {

        byte[] secret = Base64.decode(secretKey, Base64.DEFAULT);
        SecretKeySpec key = new SecretKeySpec(secret, "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(key);
        byte[] bytes = mac.doFinal(baseString.getBytes("UTF-8"));
        return Base64.encodeToString(bytes, Base64.DEFAULT);
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
        Log.d("pxlclient", finalUrl);
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, finalUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callbacks.JsonReceived(response);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.w(TAG, "got an error response");
                callbacks.ErrorResponse(error);
            }
        });

        jor.setShouldCache(false);
        this.addToRequestQueue(jor);
        return true;
    }

    /***
     * Makes a POST call to the Pixlee API. Appends api key, secret key, unique id and platform to the request body.
     * on success/error.
     * @param requestPath - path to hit (will be appended to the base Pixlee Analytics api endpoint)
     * @param body - key/values to be stored in analytics events
     * @return false if no api key set yet, true otherwise
     */
    public boolean makePostCall(final String requestPath, final JSONObject body) {
        if (PXLClient.apiKey == null || PXLClient.secretKey == null) {
            return false;
        }
        String paramString = String.format("%s=%s", KeyApiKey, apiKey);
        String finalUrl = String.format("%s/%s?%s", url, requestPath, paramString);

        Log.d(TAG, "POST");

        Log.d(TAG, finalUrl);

        final String requestBody = body.toString();//.replace("\\/", "/" );

        StringRequest sr = new StringRequest(Request.Method.POST, finalUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("POST CALL Body", requestBody);
                Log.d("POST CALL ", response);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.w(TAG, "got an error response");
                Log.w("POST BODY", requestBody);
                Log.w(TAG, error.toString());

            }
        }){
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }

            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                String signature = "";
                try {
                    Log.d(TAG, requestBody);
                    signature = computeHmac(requestBody, PXLClient.secretKey);
                    Log.d(TAG, "signature!!");
                    Log.d(TAG, signature);
                } catch (Exception e){
                    e.printStackTrace();
                }
                headers.put("Signature", signature);
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                headers.put("Accept-Encoding", "utf-8");
                return headers;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String responseString = "";
                if (response != null) {
                    responseString = String.valueOf(response.statusCode);
                    // can get more details such as response.headers
                }
                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
            }
        };

        logToCurlRequest(sr);
        sr.setShouldCache(false);
        this.addToRequestQueue(sr);
        return true;
    }

    /***
     * Makes a call to the Pixlee Analytics API (limitless beyond). Appends api key, unique id and platform to the request body.
     * on success/error.
     * @param requestPath - path to hit (will be appended to the base Pixlee Analytics api endpoint)
     * @param body - key/values to be stored in analytics events
     * @return false if no api key set yet, true otherwise
     */
    public boolean makeAnalyticsCall(final String requestPath, final JSONObject body) {
        if (PXLClient.apiKey == null) {
            return false;
        }
        String finalUrl = String.format("%s/%s", analyticsUrl, requestPath);
        Log.d(TAG, finalUrl);

        try{
            body.put("API_KEY", PXLClient.apiKey.toString());
            body.put("uid", android_id.toString());
            body.put("platform", "android");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String requestBody = body.toString();

        StringRequest sr = new StringRequest(Request.Method.POST, finalUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("ANALYTICS CALL Body", requestBody);
                Log.d("ANALYTICS CALL ", response);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.w(TAG, "got an error response");
                Log.w(TAG, error.toString());
            }
        }){
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String responseString = "";
                if (response != null) {
                    responseString = String.valueOf(response.statusCode);
                    // can get more details such as response.headers
                }
                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
            }
        };

        sr.setShouldCache(false);
        this.addToRequestQueue(sr);
        return true;
    }

    private void logToCurlRequest(Request<?> request) {
        StringBuilder builder = new StringBuilder();
        builder.append("curl request: curl ");
        builder.append("-X \"");
        switch (request.getMethod()) {
            case Request.Method.POST:
                builder.append("POST");
                break;
            case Request.Method.GET:
                builder.append("GET");
                break;
            case Request.Method.PUT:
                builder.append("PUT");
                break;
            case Request.Method.DELETE:
                builder.append("DELETE");
                break;
        }
        builder.append("\"");

        try {
            if (request.getBody() != null) {
                builder.append(" -D ");
                String data = new String(request.getBody());
                data = data.replaceAll("\"", "\\\"");
                builder.append("\"");
                builder.append(data);
                builder.append("\"");
            }
            for (String key : request.getHeaders().keySet()) {
                builder.append(" -H '");
                builder.append(key);
                builder.append(": ");
                builder.append(request.getHeaders().get(key));
                builder.append("'");
            }
            builder.append(" \"");
            builder.append(request.getUrl());
            builder.append("\"");

            Log.w("HEHEHEHEHE", "heheh");
            Log.w("HEHEHEHEHE", builder.toString());
        } catch (AuthFailureError e) {
            VolleyLog.wtf("Unable to get body of response or headers for curl logging");
        }
    }
}