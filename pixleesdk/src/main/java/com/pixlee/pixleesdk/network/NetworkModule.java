package com.pixlee.pixleesdk.network;

import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.orhanobut.logger.Logger;
import com.pixlee.pixleesdk.BuildConfig;
import com.pixlee.pixleesdk.annotation.FieldURL;
import com.pixlee.pixleesdk.data.api.AnalyticsAPI;
import com.pixlee.pixleesdk.data.api.BasicAPI;
import com.pixlee.pixleesdk.data.repository.AnalyticsDataSource;
import com.pixlee.pixleesdk.data.repository.AnalyticsRepository;
import com.pixlee.pixleesdk.data.repository.BasicDataSource;
import com.pixlee.pixleesdk.data.repository.BasicRepository;
import com.squareup.moshi.FromJson;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.ToJson;
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
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
import retrofit2.converter.moshi.MoshiConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * This class generates Data Source classes that include Retrofit HTTP API interfaces.
 * Retrofit Document: https://square.github.io/retrofit/
 */
public class NetworkModule {
    public static BasicDataSource generateBasicRepository() {
        return new BasicRepository(
                provideRetrofit(
                        NetworkModule.url,
                        provideGSon(),
                        provideOkHttpClient(getRequestInterceptor())
                ).create(BasicAPI.class)
        );
    }

    public static AnalyticsDataSource getAnalyticsRepository() {
        return new AnalyticsRepository(
                provideRetrofit(
                        NetworkModule.analyticsUrl,
                        provideGSon(),
                        provideOkHttpClient(getRequestInterceptor())
                ).create(AnalyticsAPI.class)
        );
    }

    public static final String url = "https://distillery.pixlee.com/api/v2/";
    public static final String analyticsUrl = "https://inbound-analytics.pixlee.com/";

    private static final Long timeout_read = 30L;
    private static final Long timeout_connect = 20L;
    private static final Long timeout_write = 30L;

    private static Gson provideGSon() {
        return new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DOTS)
                .create();
    }

    private static Moshi provideMoshi(){
        Moshi moshi = new Moshi.Builder()
                .add(new URLAdapter())  //String -> URL
                .add(new Rfc3339DateJsonAdapter()) //string -> Date
                .build();
        return moshi;
    }

    static class URLAdapter {
        @ToJson
        String toJson(URL card) {
            return card.toString();
        }

        @FromJson
        URL fromJson(@FieldURL String url) {
            /*if (card.length() != 2) throw new JsonDataException("Unknown card: " + card);

            char rank = card.charAt(0);
            switch (card.charAt(1)) {
                case 'C': return new Card(rank, Suit.CLUBS);
                case 'D': return new Card(rank, Suit.DIAMONDS);
                case 'H': return new Card(rank, Suit.HEARTS);
                case 'S': return new Card(rank, Suit.SPADES);
                default: throw new JsonDataException("unknown suit: " + card);
            }*/
            return null;
        }
    }


    private static Retrofit provideRetrofit(String url, Gson gson, OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(MoshiConverterFactory.create())
                //.addConverterFactory(ScalarsConverterFactory.create())
                //.addConverterFactory(GsonConverterFactory.create(gson))
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

        ok.addInterceptor(interceptor);
        return ok.build();
    }


    private static Interceptor getRequestInterceptor() {
        return new Interceptor() {
            @NotNull
            @Override
            public Response intercept(@NotNull Chain chain) throws IOException {

                Request original = chain.request();
                Log.e("pretty", "Interceptor.url.host: " + original.url().host());
                Log.e("pretty", "Interceptor.url.url: " + original.url());
                Log.e("pretty", "Interceptor.url.method: " + original.method());

                Request.Builder builder = original.newBuilder();

                builder.header("Accept", "application/json");
                builder.header("Content-Type", "application/json");
                builder.header("Accept-Encoding", "utf-8");

                Response response = chain.proceed(builder.build());
                ResponseBody body = response.body();

                String bodyStr = body.string();
                Log.e("pretty", "**http-num: " + response.code());
                Log.e("pretty", "**http-body: "+ body.string());

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
