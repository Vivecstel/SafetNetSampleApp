package com.safety.net.sample.utils;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.security.SecureRandom;

public class SafetyNetApiHelper {

    // Nonce token length
    private static final int NONCE_TOKEN_LENGTH = 16;
    // Secure random instance
    private SecureRandom mSecureRandom;
    // Google api client
    private GoogleApiClient mGoogleApiClient;
    // Callback
    private SafetyNetApiHelperCallback mCallback;

    public SafetyNetApiHelper(Context context, @NonNull SafetyNetApiHelperCallback callback) {
        this.mGoogleApiClient = getGoogleApiClient(context);
        mGoogleApiClient.connect();
        this.mSecureRandom = new SecureRandom();
        this.mCallback = callback;
    }

    // get configured google api client
    private GoogleApiClient getGoogleApiClient(Context context) {
        return new GoogleApiClient
                .Builder(context)
                .addApi(SafetyNet.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        // on connect run safet net api
                        runSafetyNet();
                    }
                    @Override
                    public void onConnectionSuspended(int i) {}
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        mCallback.onError("Google Play services connection failed");
                    }
                })
                .build();
    }

    // run safety net with callback
    private void runSafetyNet() {
        final long requestTimestamp = System.currentTimeMillis();
        final byte[] requestNonce = generateRequestNonce();
        SafetyNet.SafetyNetApi
                .attest(mGoogleApiClient, requestNonce)
                .setResultCallback(new ResultCallback<SafetyNetApi.AttestationResult>() {
                    @Override
                    public void onResult(@NonNull SafetyNetApi.AttestationResult attestationResult) {
                        mCallback.onResult(attestationResult, requestTimestamp, requestNonce);
                    }
                });
    }

    // Obtain a single use token
    // Generate request nonce with minimum 16 bytes in length
    // this can be generated better from your own server according to google
    private byte[] generateRequestNonce() {
        byte[] nonce = new byte[NONCE_TOKEN_LENGTH];
        mSecureRandom.nextBytes(nonce);

        return nonce;
    }

    // safety net api helper callback
    public interface SafetyNetApiHelperCallback {
        void onError(String errorMessage);
        void onResult(SafetyNetApi.AttestationResult attestationResult, long timestatmp,
                      byte[] requestNonce);
    }
}