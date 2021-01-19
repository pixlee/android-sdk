package com.pixlee.pixleesdk.network;

import android.os.Build;
import android.util.Log;

import com.orhanobut.logger.Logger;
import com.pixlee.pixleesdk.BuildConfig;
import com.pixlee.pixleesdk.data.api.AnalyticsAPI;
import com.pixlee.pixleesdk.data.api.BasicAPI;
import com.pixlee.pixleesdk.data.api.KtxAnalyticsAPI;
import com.pixlee.pixleesdk.data.api.KtxBasicAPI;
import com.pixlee.pixleesdk.data.repository.AnalyticsDataSource;
import com.pixlee.pixleesdk.data.repository.AnalyticsRepository;
import com.pixlee.pixleesdk.data.repository.BasicDataSource;
import com.pixlee.pixleesdk.data.repository.BasicRepository;
import com.pixlee.pixleesdk.data.repository.KtxAnalyticsDataSource;
import com.pixlee.pixleesdk.data.repository.KtxAnalyticsRepository;
import com.pixlee.pixleesdk.data.repository.KtxBasicDataSource;
import com.pixlee.pixleesdk.data.repository.KtxBasicRepository;
import com.pixlee.pixleesdk.network.adaptor.BigDecimalAdapter;
import com.pixlee.pixleesdk.network.adaptor.DateAdapter;
import com.pixlee.pixleesdk.network.adaptor.PrimitiveAdapter;
import com.pixlee.pixleesdk.network.adaptor.URLAdapter;
import com.serjltt.moshi.adapters.Wrapped;
import com.squareup.moshi.Moshi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

//import org.jetbrains.annotations.NotNull;

/**
 * This class generates Data Source classes that include Retrofit HTTP API interfaces.
 * Retrofit Document: https://square.github.io/retrofit/
 */
public class NetworkModule {
    public static BasicDataSource generateBasicRepository() {
        return new BasicRepository(
                provideRetrofit(
                        NetworkModule.url,
                        provideOkHttpClient()
                ).create(BasicAPI.class)
        );
    }

    public static AnalyticsDataSource getAnalyticsRepository() {
        return new AnalyticsRepository(
                provideRetrofit(
                        NetworkModule.analyticsUrl,
                        provideOkHttpClient()
                ).create(AnalyticsAPI.class)
        );
    }

    public static KtxBasicDataSource generateKtxBasicRepository() {
        return new KtxBasicRepository(
                provideRetrofit(
                        NetworkModule.url,
                        provideOkHttpClient()
                ).create(KtxBasicAPI.class)
        );
    }

    public static KtxAnalyticsDataSource getKtxAnalyticsRepository() {
        return new KtxAnalyticsRepository(
                provideRetrofit(
                        NetworkModule.analyticsUrl,
                        provideOkHttpClient()
                ).create(KtxAnalyticsAPI.class)
        );
    }

    public static final String url = "https://distillery.pixlee.com/";
    public static final String analyticsUrl = "https://inbound-analytics.pixlee.com/events/";

    private static final Long timeout_read = 60L;
    private static final Long timeout_connect = 60L;
    private static final Long timeout_write = 180L;

    public static Moshi provideMoshi() {
        return new Moshi.Builder()
                .add(Wrapped.ADAPTER_FACTORY)
                .add(new PrimitiveAdapter()) // null -> a specified default value, same as the return value of JSONObject.opt{PrimitiveType}(...)
                .add(new URLAdapter())  // String -> URL
                .add(new DateAdapter()) // String -> Date
                .add(new BigDecimalAdapter()) //String -> BigDecimal
                .build();
    }

    public static Retrofit provideRetrofit(String url, OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(ScalarsConverterFactory.create()) // this converts the response to string if declared in the APIs
                .addConverterFactory(MoshiConverterFactory.create(provideMoshi()).asLenient()) // this converts json into classes if declared in the APIs
                .client(okHttpClient)
                .build();
    }

    static OkHttpClient okHttpClient;

    public static synchronized OkHttpClient provideOkHttpClient() {
        if (okHttpClient == null) {
            OkHttpClient.Builder ok = new OkHttpClient.Builder()
                    .connectTimeout(timeout_connect, TimeUnit.SECONDS)
                    .readTimeout(timeout_read, TimeUnit.SECONDS)
                    .writeTimeout(timeout_write, TimeUnit.SECONDS);

            // enable Tls12 on Pre Lollipop
            try {
                if (Build.VERSION.SDK_INT < 21) {
                    TLSSocketFactory tlsSocketFactory = new TLSSocketFactory();
                    if (tlsSocketFactory.getTrustManager() != null) {
                        ok.sslSocketFactory(tlsSocketFactory, tlsSocketFactory.getTrustManager());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                    @Override
                    public void log(String s) {
                        if (isJSONValid(s))
                            Logger.json(s);
                        else
                            Log.d("pretty", s);
                    }


                    boolean isJSONValid(String jsonInString) {
                        try {
                            new JSONObject(jsonInString);
                        } catch (JSONException ex) {
                            try {
                                new JSONArray(jsonInString);
                            } catch (JSONException ex1) {
                                return false;
                            }

                        }

                        return true;
                    }

                });
                logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                ok.addInterceptor(logging);

            }

            okHttpClient = ok.build();
        }

        return okHttpClient;
    }
}
