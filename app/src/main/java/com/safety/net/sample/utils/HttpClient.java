package com.safety.net.sample.utils;

import com.google.gson.Gson;

import com.safety.net.sample.model.VerifyRequest;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpClient {

    // Tag
    private final String TAG = HttpClient.this.getClass().getSimpleName();
    // Constants
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";
    private static final int TIMEOUT = 15;
    // gson
    private Gson mGson;

    public HttpClient(Gson gson) {
        this.mGson = gson;
    }

    public <T> T executePostRequest(String url, VerifyRequest verifyRequest, Class<T> classOfT) {
        // Configure the ok http client using builder and add the ssl socket factory
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
                .build();

        // Configure the request
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MediaType.parse(APPLICATION_JSON), mGson.toJson(verifyRequest)))
                .addHeader(CONTENT_TYPE, APPLICATION_JSON)
                .build();

        try {
            // Parse the response using gson and the class provided
            Response response = okHttpClient.newCall(request).execute();
            // If not successful return null
            if (response.isSuccessful()) {
                T t = mGson.fromJson(response.body().charStream(), classOfT);
                response.body().close();

                return t;
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        // return null if request is not successful or exception occurred
        return null;
    }
}