package com.pixlee.pixleesdk.network;

import android.util.Log;

import com.orhanobut.logger.Logger;
import com.pixlee.pixleesdk.BuildConfig;
import com.pixlee.pixleesdk.data.api.AnalyticsAPI;
import com.pixlee.pixleesdk.data.api.BasicAPI;
import com.pixlee.pixleesdk.data.repository.AnalyticsDataSource;
import com.pixlee.pixleesdk.data.repository.AnalyticsRepository;
import com.pixlee.pixleesdk.data.repository.BasicDataSource;
import com.pixlee.pixleesdk.data.repository.BasicRepository;
import com.pixlee.pixleesdk.network.adaptor.DateAdapter;
import com.pixlee.pixleesdk.network.adaptor.PrimitiveAdapter;
import com.pixlee.pixleesdk.network.adaptor.URLAdapter;
import com.serjltt.moshi.adapters.Wrapped;
import com.squareup.moshi.Moshi;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import kotlin.text.Charsets;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

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

    public static final String url = "https://distillery.pixlee.com/api/v2/";
    public static final String analyticsUrl = "https://inbound-analytics.pixlee.com/";

    private static final Long timeout_read = 30L;
    private static final Long timeout_connect = 20L;
    private static final Long timeout_write = 30L;

    private static Moshi provideMoshi(){
        return new Moshi.Builder()
//                .add(NestingJsonAdapter.FACTORY)
                .add(Wrapped.ADAPTER_FACTORY)
                .add(new PrimitiveAdapter()) //null -> a specified default value, same as the return value of JSONObject.opt{PrimitiveType}(...)
                .add(new URLAdapter())  //String -> URL
                .add(new DateAdapter()) //string -> Date
                .build();
    }

    public static Retrofit provideRetrofit(String url, OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(MoshiConverterFactory.create(provideMoshi()).asLenient())
                .client(okHttpClient)
                .build();
    }

    public static OkHttpClient provideOkHttpClient() {
        OkHttpClient.Builder ok = new OkHttpClient.Builder()
                .connectTimeout(timeout_connect, TimeUnit.SECONDS)
                .readTimeout(timeout_read, TimeUnit.SECONDS)
                .writeTimeout(timeout_write, TimeUnit.SECONDS);

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(@NotNull String s) {
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

        return ok.build();
    }
}
