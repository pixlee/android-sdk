package com.pixlee.pixleesdk.client;

import android.content.Context;
import android.provider.Settings.Secure;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.pixlee.pixleesdk.data.repository.AnalyticsDataSource;
import com.pixlee.pixleesdk.data.repository.BasicDataSource;
import com.pixlee.pixleesdk.network.NetworkModule;

/***
 * Manages the configuration of volley and calls to the api. Intended to be used as a singleton,
 * so access should occur via the getInstance() method.
 */
public class PXLClient {
    private static PXLClient mInstance;
    private Context mCtx;

    public static String apiKey = null;
    public static String secretKey = null;
    public static String android_id = null;

    private BasicDataSource basicRepo;
    private AnalyticsDataSource analyticsRepo;

    private PXLClient(Context context) {
        if (PXLClient.apiKey == null ) {
            throw new IllegalArgumentException("no apiKey, please set apiKey before start");
        }

        Logger.addLogAdapter(new AndroidLogAdapter());
        mCtx = context;
        android_id = Secure.getString(mCtx.getContentResolver(), Secure.ANDROID_ID);
    }

    public BasicDataSource getBasicRepo() {
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
        PXLClient.secretKey = null;
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