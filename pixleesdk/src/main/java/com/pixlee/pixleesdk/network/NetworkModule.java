package com.pixlee.pixleesdk.network;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.orhanobut.logger.Logger;
import com.pixlee.pixleesdk.BuildConfig;
import com.pixlee.pixleesdk.data.api.AnalyticsAPI;
import com.pixlee.pixleesdk.data.api.BasicAPI;
import com.pixlee.pixleesdk.data.repository.AnalyticsDataSource;
import com.pixlee.pixleesdk.data.repository.AnalyticsRepository;
import com.pixlee.pixleesdk.data.repository.BasicDataSource;
import com.pixlee.pixleesdk.data.repository.BasicRepository;

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
import retrofit2.converter.scalars.ScalarsConverterFactory;

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
                //.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
    }

    private static Retrofit provideRetrofit(String url, Gson gson, OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(ScalarsConverterFactory.create())
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
        //ok.authenticator(new TokenAuthenticator());

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
                /*if("POST".equals(method)){
                    try {
                        String hmac = computeHmac(reqBody.replace("\\/", "/" ), PXLClient.secretKey);
                        Log.e("pretty", "hmac: |" + hmac + "|");
                        builder.header("Signature", hmac);
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (InvalidKeyException e) {
                        e.printStackTrace();
                    }
                }*/

                /*storage.getCookie()?.also { cookie ->
                    builder.header("Authorization", "Bearer $cookie")

                }
                */

                Response response = chain.proceed(builder.build());
                ResponseBody body = response.body();

                String bodyStr = body.string();
                Log.e("pretty", "**http-num: " + response.code());
                Log.e("pretty", "**http-body: "+ bodyStr);


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

//    static public class TokenAuthenticator implements Authenticator {
//        @Override
//        public Request authenticate(Route route, Response response) {
//            //HttpURLConnection.HTTP_UNAUTHORIZED
//            Log.e("RequestMaker", "===http.status:" + response.code());
//            Log.e("RequestMaker", "===http.url:" + response.request().url().toString());
//
//            Request request = response.request();
//            Request.Builder builder = request.newBuilder();
//            builder.header("Content-Type", "application/json")
//                    .header("Accept", "application/json")
//                    .header("Accept-Encoding", "utf-8")
//                    .header("Signature", hmac)
//                    .method(request.method(), request.body());
//
//            return builder.build();
//        }
//    }

}
