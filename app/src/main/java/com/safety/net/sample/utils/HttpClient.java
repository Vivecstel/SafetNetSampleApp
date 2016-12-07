package com.safety.net.sample.utils;

import com.google.gson.Gson;

import com.safety.net.sample.model.SafetyNetResponse;
import com.safety.net.sample.model.VerifyRequest;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSocketFactory;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.platform.Platform;

public class HttpClient {

    // Tag
    private final String TAG = HttpClient.this.getClass().getSimpleName();
    // Constants
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";

    private Gson mGson;

    public HttpClient(Gson gson) {
        this.mGson = gson;
    }

    public <T> T executePostRequest(String url, VerifyRequest verifyRequest,
                                    SSLSocketFactory socketFactory, Class<T> classOfT) {
        if (socketFactory == null) {
            Log.d(TAG, "socketFactory is null");
            return null;
        }
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .sslSocketFactory(socketFactory, Platform.get().trustManager(socketFactory))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MediaType.parse(APPLICATION_JSON), mGson.toJson(verifyRequest)))
                .addHeader(CONTENT_TYPE, APPLICATION_JSON)
                .build();

        try {
            Response response = okHttpClient.newCall(request).execute();
            T t = mGson.fromJson(response.body().charStream(), classOfT);
            response.body().close();

            return t;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        return null;
    }
}