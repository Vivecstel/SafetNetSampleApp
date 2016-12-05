package com.safety.net.sample.utils;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;

import com.safety.net.sample.application.SafetyNetSampleApplication;
import com.safety.net.sample.events.SafetyNetJobResultEvent;
import com.scottyab.safetynet.SafetyNetResponse;

import org.greenrobot.eventbus.EventBus;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Base64;

import java.security.SecureRandom;

public class SafetyNetJobHelper {

    // nonce token length
    private static final int NONCE_TOKEN_LENGTH = 32;
    // secure random instance
    private SecureRandom mSecureRandom;
    // google api client
    private GoogleApiClient mGoogleApiClient;
    // callback
    private SafetyNetJobHelperCallback mCallback;

    public SafetyNetJobHelper(Context context, SafetyNetJobHelperCallback callback) {
        this.mGoogleApiClient = getGoogleApiClient(context);
        mGoogleApiClient.connect();
        this.mSecureRandom = new SecureRandom();
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
                        mCallback.error("Google Play services connection failed");
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

                        if (status.isSuccess() && !TextUtils.isEmpty(jwsResult)) {
                            final SafetyNetResponse safetyNetResponse = parseJwsResult(jwsResult);
                            mCallback.error(safetyNetResponse.toString());
                        } else {
                            mCallback.error("SafetyNetApi.AttestationResult success == false or empty payload");
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
            return SafetyNetSampleApplication.getInstance().getGson().fromJson(decodedPayload, SafetyNetResponse.class);
        } else {
            return null;
        }
    }

    public interface SafetyNetJobHelperCallback {
        void error(String errorMessage);

        void success(boolean ctsProfileMatch);
    }
}