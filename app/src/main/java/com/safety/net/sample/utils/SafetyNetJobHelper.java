package com.safety.net.sample.utils;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.gson.Gson;

import com.safety.net.sample.application.SafetyNetSampleApplication;
import com.safety.net.sample.model.SafetyNetResponse;
import com.safety.net.sample.model.VerifyRequest;
import com.safety.net.sample.model.VerifyResponse;
import com.scottyab.safetynet.GoogleApisTrustManager;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Base64;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

public class SafetyNetJobHelper {

    // nonce token length
    private static final int NONCE_TOKEN_LENGTH = 16;
    // verification Url
    private static final String URL = "https://www.googleapis.com/androidcheck/v1/attestations/verify?key=";
    // secure random instance
    private SecureRandom mSecureRandom;
    // google api client
    private GoogleApiClient mGoogleApiClient;
    // gson
    private Gson mGson;
    // device verification api key
    private String mApiKey;
    // callback
    private SafetyNetJobHelperCallback mCallback;

    public SafetyNetJobHelper(Context context, @NonNull String apiKey, @NonNull SafetyNetJobHelperCallback callback) {
        this.mGoogleApiClient = getGoogleApiClient(context);
        mGoogleApiClient.connect();
        this.mSecureRandom = new SecureRandom();
        this.mGson = SafetyNetSampleApplication.getInstance().getGson();
        this.mApiKey = apiKey;
        this.mCallback = callback;
    }

    private GoogleApiClient getGoogleApiClient(Context context) {
        return new GoogleApiClient
                .Builder(context)
                .addApi(SafetyNet.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        runSafetyNet();
                    }
                    @Override
                    public void onConnectionSuspended(int i) {}
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        mCallback.result(null, "Google Play services connection failed");
                    }
                })
                .build();
    }

    private void runSafetyNet() {
        final byte[] requestNonce = generateRequestNonce();
        SafetyNet.SafetyNetApi
                .attest(mGoogleApiClient, requestNonce)
                .setResultCallback(new ResultCallback<SafetyNetApi.AttestationResult>() {
                    @Override
                    public void onResult(@NonNull SafetyNetApi.AttestationResult attestationResult) {
                        Status status = attestationResult.getStatus();
                        String jwsResult = attestationResult.getJwsResult();

                        if (!status.isSuccess()) {
                            mCallback.result(null, "SafetyNetApi attestationResult status not success");
                        } else if (TextUtils.isEmpty(jwsResult)) {
                            mCallback.result(null, "SafetyNetApi jwsResult is empty");
                        } else {
                            final SafetyNetResponse safetyNetResponse = parseJwsResult(jwsResult);
                            if (safetyNetResponse != null) {
                                if (TextUtils.isEmpty(mApiKey)) {
                                    mCallback.result(safetyNetResponse.getCtsProfileMatch(), "Android Verification Api Key missing");
                                } else {
                                    HttpClient httpClient = new HttpClient(mGson);
                                    VerifyResponse verifyResponse = httpClient.executePostRequest(URL,
                                            new VerifyRequest(mGson.toJson(safetyNetResponse)), getSSLSocketFactory(), VerifyResponse.class);
                                    if (verifyResponse == null) {
                                        mCallback.result(safetyNetResponse.getCtsProfileMatch(), "Android verification response is empty");
                                    } else {
                                        if (verifyResponse.getIsValidSignature()) {
                                            mCallback.result(safetyNetResponse.getCtsProfileMatch(), "");
                                        } else {
                                            mCallback.result(safetyNetResponse.getCtsProfileMatch(), "\"Android verification response : not valid signature");
                                        }
                                    }
                                }
                            } else {
                                mCallback.result(null, "Safety Net response is empty");
                            }
                        }
                    }
                });
    }

    // Obtain a single use token
    // Generate request nonce with minimum 16 bytes in length
    private byte[] generateRequestNonce() {
        byte[] nonce = new byte[NONCE_TOKEN_LENGTH];
        mSecureRandom.nextBytes(nonce);

        return nonce;
    }

    // Parse the Jws result to safety net response
    private SafetyNetResponse parseJwsResult(String jwsResult) {
        // Split the result at .
        String[] jwtResultParts = jwsResult.split("\\.");
        // check if the previous array has 3 parts in order to later use the first them
        if (jwtResultParts.length == 3) {
            // decode the first part with Base64
            String decodedPayload = new String(Base64.decode(jwtResultParts[1], 0));
            // parse it with gson
            return mGson.fromJson(decodedPayload, SafetyNetResponse.class);
        } else {
            return null;
        }
    }

    // Get the ssl socket factory using TLS instance
    private SSLSocketFactory getSSLSocketFactory() {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, getTrustManagers(), null);
            return sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
            e.printStackTrace();
        }

        return null;
    }


    private TrustManager[] getTrustManagers() throws KeyStoreException, NoSuchAlgorithmException {
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init((KeyStore)null);
        TrustManager[] defaultTrustManagers = trustManagerFactory.getTrustManagers();
        TrustManager[] trustManagers = Arrays.copyOf(defaultTrustManagers, defaultTrustManagers.length + 1);
        trustManagers[defaultTrustManagers.length] = new GoogleApisTrustManager();

        return trustManagers;
    }

    public interface SafetyNetJobHelperCallback {
        void result(Boolean ctsProfileMatch, String message);
    }
}