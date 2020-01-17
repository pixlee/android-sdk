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

    public static String apiKey = null;
    public static String secretKey = null;
    public static String android_id = null;

    private BasicDataSource basicRepo;
    private AnalyticsDataSource analyticsRepo;

    public BasicDataSource getBasicrepo() {
        if (basicRepo == null) {
            basicRepo = NetworkModule.generateBasicRepository();
        }

        return basicRepo;
    }

    public AnalyticsDataSource getAnalyticsRepo() {
        if (analyticsRepo == null) {
            analyticsRepo = NetworkModule.getAnalyticsRepository();
        }
        return analyticsRepo;
    }

    private PXLClient(Context context) {
        if (PXLClient.apiKey == null ) {
            throw new IllegalArgumentException("no apiKey, please set apiKey before start");
        }

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
}