package com.safety.net.sample.utils;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

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
        this.mSecureRandom = new SecureRandom();
        this.mCallback = callback;
    }

    public void connect() {
        mGoogleApiClient.connect();
    }

    // get configured google api client
    private GoogleApiClient getGoogleApiClient(Context context) {
        return new GoogleApiClient
                .Builder(context)
                .addApi(SafetyNet.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        // on connect run safety net api
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
        // get timestamp of the request
        final long requestTimestamp = System.currentTimeMillis();
        // get the request nonce
        final byte[] requestNonce = generateRequestNonce();
        SafetyNet.SafetyNetApi
                .attest(mGoogleApiClient, requestNonce)
                .setResultCallback(new ResultCallback<SafetyNetApi.AttestationResult>() {
                    @Override
                    public void onResult(@NonNull SafetyNetApi.AttestationResult attestationResult) {
                        // the status of the attestation result
                        Status status = attestationResult.getStatus();
                        // the json web signature
                        String jwsResult = attestationResult.getJwsResult();

                        // Check if status is not success
                        if (!status.isSuccess()) {
                            mCallback.onError("SafetyNetApi attestationResult status not success");
                        // Check if the jws result is empty
                        } else if (TextUtils.isEmpty(jwsResult)) {
                            mCallback.onError("SafetyNetApi jwsResult is empty");
                        } else {
                            mCallback.onResult(jwsResult, requestTimestamp, requestNonce);
                        }
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
        void onResult(String jwsResult, long timestamp, byte[] requestNonce);
    }
}