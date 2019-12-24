package com.pixlee.pixleesdk.network;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.orhanobut.logger.Logger;
import com.pixlee.pixleesdk.BuildConfig;
import com.pixlee.pixleesdk.data.api.AnalyticsAPI;
import com.pixlee.pixleesdk.data.api.BasicAPI;

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
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkModule {
    public static BasicAPI getBasicAPI() {
        return provideRetrofit(
                NetworkModule.url,
                provideGSon(),
                provideOkHttpClient(getRequestInterceptor())
        ).create(BasicAPI.class);
    }

    public static AnalyticsAPI getAnalyticsAPI() {
        return provideRetrofit(
                NetworkModule.analyticsUrl,
                provideGSon(),
                provideOkHttpClient(getRequestInterceptor())
        ).create(AnalyticsAPI.class);
    }

    private static final String url = "https://distillery.pixlee.com/api/v2";
    private static final String analyticsUrl = "https://inbound-analytics.pixlee.com";

    private static final Long timeout_read = 30L;
    private static final Long timeout_connect = 20L;
    private static final Long timeout_write = 30L;

    private static Gson provideGSon() {
        return new GsonBuilder()
                .create();
    }

    private static Retrofit provideRetrofit(String url, Gson gson, OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();
    }

    private static OkHttpClient provideOkHttpClient(Interceptor interceptor) {
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

        /*dispatcher = Dispatcher()
        httpClientBuilder.dispatcher(dispatcher)*/

        ok.addInterceptor(interceptor);
        return ok.build();
    }


    private static Interceptor getRequestInterceptor() {
        return new Interceptor() {
            @NotNull
            @Override
            public Response intercept(@NotNull Chain chain) throws IOException {

                Request original = chain.request();
                Log.e("pretty", "Interceptor.url.host: ${original.url.host}");
                Log.e("pretty", "Interceptor.url.path: ${original.url}");

                Request.Builder builder = original.newBuilder();
                builder.header("Accept", "application/json");
                /*storage.getCookie()?.also { cookie ->
                    builder.header("Authorization", "Bearer $cookie")

                }
                */

                Response response = chain.proceed(builder.build());
                ResponseBody body = response.body();

                String bodyStr = body.toString();
                Log.e("pretty", "**http-num: ${response.code}");
                Log.e("pretty", "**http-body: $body");


                Response.Builder builder2 = response.newBuilder();

                return builder2.body(
                        ResponseBody.create(
                                body.contentType()
                                , bodyStr.getBytes(Charsets.UTF_8)
                        )
                ).build();
            }
        };
    }
}
