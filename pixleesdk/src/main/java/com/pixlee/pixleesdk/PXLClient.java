package com.pixlee.pixleesdk;

import android.content.Context;
import android.provider.Settings.Secure;

import com.pixlee.pixleesdk.data.repository.AnalyticsDataSource;
import com.pixlee.pixleesdk.data.repository.BasicDataSource;
import com.pixlee.pixleesdk.network.NetworkModule;

import org.json.JSONObject;

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
        if (PXLClient.apiKey == null || PXLClient.secretKey == null) {
            throw new Exception();
        }
        if (basicRepo == null) {
            basicRepo = NetworkModule.generateBasicRepository();
        }

        return basicRepo;
    }

    public AnalyticsDataSource getAnalyticsRepo() throws Exception {
        if (PXLClient.apiKey == null || PXLClient.secretKey == null) {
            throw new Exception();
        }

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

                               }

                               @Override
                               public void onFailure(Call<String> call, Throwable t) {

                               }
                           }
                   );
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

}