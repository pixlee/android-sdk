package com.pixlee.pixleesdk;

import android.content.Context;
import android.provider.Settings.Secure;
import android.util.Base64;
import android.util.Log;

import com.pixlee.pixleesdk.data.api.AnalyticsAPI;
import com.pixlee.pixleesdk.data.repository.AnalyticsDataSource;
import com.pixlee.pixleesdk.data.repository.BasicDataSource;
import com.pixlee.pixleesdk.network.NetworkModule;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private Context mCtx;

    static String apiKey = null;
    public static String secretKey = null;
    private static String android_id = null;

    BasicDataSource basicRepo;
    AnalyticsDataSource analyticsRepo;

    public synchronized BasicDataSource getBasicrepo() throws Exception {
//        if (PXLClient.apiKey == null || PXLClient.secretKey == null) {
//            throw new Exception();
//        }
        if (basicRepo == null) {
            basicRepo = NetworkModule.generateBasicRepository();
        }

        return basicRepo;
    }

    public AnalyticsDataSource getAnalyticsRepo() throws Exception {
//        if (PXLClient.apiKey == null || PXLClient.secretKey == null) {
//            throw new Exception();
//        }

        if (analyticsRepo == null) {
            analyticsRepo = NetworkModule.getAnalyticsRepository();
        }
        return analyticsRepo;
    }

    private PXLClient(Context context) {
        mCtx = context;

        android_id = Secure.getString(mCtx.getContentResolver(), Secure.ANDROID_ID);

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


    public String computeHmac(String baseString, String secretKey)
            throws NoSuchAlgorithmException, InvalidKeyException, IllegalStateException, UnsupportedEncodingException {

        SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(), "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(key);
        byte[] bytes = mac.doFinal(baseString.getBytes());
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }


    /***
     * Makes a call to the Pixlee API. Appends api key to the request. Invokes the given callbacks
     * on success/error.
     * @param requestPath - path to hit (will be appended to the base Pixlee api endpoint)
     * @param callbacks - called after request succeeds or fails
     * @return false if no api key set yet, true otherwise
     */
    /*public boolean makeCall(String requestPath, HashMap<String, Object> parameters, final RequestCallbacks callbacks) {
        if (PXLClient.apiKey == null) {
            return false;
        }


        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s=%s", KeyApiKey, apiKey));
        if (parameters != null && parameters.size() > 0) {
            for (Map.Entry<String, Object> param : parameters.entrySet()) {
                sb.append(String.format("&%s=%s", param.getKey(), param.getValue().toString()));
            }
        }
        String finalUrl = String.format("%s/%s?%s", url, requestPath, sb.toString());
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
    }*/

    /***
     * Makes a POST call to the Pixlee API. Appends api key to the request body and signs the request using the secret key.
     * @param requestPath - path to hit (will be appended to the base Pixlee api endpoint)
     * @param body - key/values to be passed in the POST body
     * @return false if no api key or secret set yet, true otherwise
     */
    /*public boolean makePostCall(final String requestPath, final JSONObject body) {
        if (PXLClient.apiKey == null || PXLClient.secretKey == null) {
            return false;
        }
        String paramString = String.format("%s=%s", KeyApiKey, apiKey);
        String finalUrl = String.format("%s/%s?%s", url, requestPath, paramString);

        Log.d(TAG, "POST");

        Log.d(TAG, finalUrl);

        getBasicrepo()
                .postMedia()
        final String requestBody = body.toString().replace("\\/", "/");

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
                Log.w(TAG, error.toString());
            }
        }) {
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
                    signature = computeHmac(requestBody, PXLClient.secretKey);

                } catch (Exception e) {
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

        sr.setShouldCache(false);
        this.addToRequestQueue(sr);
        return true;
    }*/

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

        try {
            body.put("API_KEY", PXLClient.apiKey.toString());
            body.put("uid", android_id.toString());
            body.put("platform", "android");

            getAnalyticsRepo()
                    .makeAnalyticsCall(requestPath, body)
                   .enqueue(
                           new Callback<String>() {
                               @Override
                               public void onResponse(Call<String> call, Response<String> response) {
                                   try {
                                       JSONObject json = new JSONObject(response.body());
                                       //JsonReceived(json);
                                   } catch (JSONException e) {
                                       e.printStackTrace();
                                   }
                               }

                               @Override
                               public void onFailure(Call<String> call, Throwable t) {
                                   /*if (handlers != null) {
                                       handlers.DataLoadFailedHandler(t.toString());
                                   }*/
                               }
                           }
                   );
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

}