package com.safety.net.sample.utils;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.gson.Gson;

import com.safety.net.sample.application.SafetyNetSampleApplication;
import com.safety.net.sample.model.SafetyNetResponse;
import com.safety.net.sample.model.VerifyRequest;
import com.safety.net.sample.model.VerifyResponse;
import com.scottyab.safetynet.GoogleApisTrustManager;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

public class AndroidVerificationHelper {

    // Tag
    private final String TAG = AndroidVerificationHelper.this.getClass().getSimpleName();
    // Verification Url
    private static final String URL = "https://www.googleapis.com/androidcheck/v1/attestations/verify?key=";
    // Device verification api key
    private String mApiKey;
    // Package name
    private String mPackageName;
    // Gson
    private Gson mGson;
    // Callback
    private AndroidVerificationHelperCallback mCallback;

    public AndroidVerificationHelper(String apiKey, String packageName,
                                     AndroidVerificationHelperCallback callback) {
        this.mApiKey = apiKey;
        this.mPackageName = packageName;
        this.mGson = SafetyNetSampleApplication.getInstance().getGson();
        this.mCallback = callback;
    }

    // validation method of attestationResult
    @SuppressWarnings("ConstantConditions")
    public void validate(@NonNull SafetyNetApi.AttestationResult attestationResult,
                         @NonNull byte[] requestNonce) {
        Status status = attestationResult.getStatus();
        String jwsResult = attestationResult.getJwsResult();

        if (!status.isSuccess()) {
            throw new RuntimeException("SafetyNetApi attestationResult status not success");
        } else if (TextUtils.isEmpty(jwsResult)) {
            throw new RuntimeException("SafetyNetApi jwsResult is empty");
        } else {
            final SafetyNetResponse safetyNetResponse = parseJwsResult(jwsResult);

            if (validateSafetyNetResponse(safetyNetResponse, requestNonce)) {
                if (TextUtils.isEmpty(mApiKey)) {
                    mCallback.onResult(safetyNetResponse.getCtsProfileMatch(), "Android Verification Api Key missing");
                } else {
                    HttpClient httpClient = new HttpClient(mGson);
                    VerifyResponse verifyResponse = httpClient.executePostRequest(URL + mApiKey,
                            new VerifyRequest(jwsResult), getSSLSocketFactory(), VerifyResponse.class);
                    if (verifyResponse == null) {
                        mCallback.onResult(safetyNetResponse.getCtsProfileMatch(), "Android verification response is empty");
                    } else {
                        if (verifyResponse.getIsValidSignature()) {
                            mCallback.onResult(safetyNetResponse.getCtsProfileMatch(), "");
                        } else {
                            mCallback.onResult(safetyNetResponse.getCtsProfileMatch(), "\"Android verification response : not valid signature");
                        }
                    }
                }
            } else {
                mCallback.onResult(null, "Safety Net response validation failed");
            }
        }
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

    // validate safety net response
    private boolean validateSafetyNetResponse(SafetyNetResponse response, byte[] requestNonce) {
        String requestNonceBase64 = Base64.encodeToString(requestNonce, 0).trim();

        // check if null
        if (response == null) {
            Log.d(TAG, "Safety Net response is null");
            return false;
        //  compare request nonce with response nonce
        } else if (!requestNonceBase64.equals(response.getNonce())) {
            Log.d(TAG, "Request nonce : " + requestNonceBase64 + " doesn't match with response nonce : "
                    + response.getNonce());
            return false;
        //  compare package name with response package name
        } else if (!mPackageName.equalsIgnoreCase(response.getApkPackageName())) {
            Log.d(TAG, "Package name : " + mPackageName + " doesn't match with response package name : "
                    + response.getApkPackageName());
            return false;
        } else { // TODO rest validations
            return true;
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

    // Get the trust managers
    private TrustManager[] getTrustManagers() throws KeyStoreException, NoSuchAlgorithmException {
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init((KeyStore)null);
        TrustManager[] defaultTrustManagers = trustManagerFactory.getTrustManagers();
        TrustManager[] trustManagers = Arrays.copyOf(defaultTrustManagers, defaultTrustManagers.length + 1);
        trustManagers[defaultTrustManagers.length] = new GoogleApisTrustManager(); // TODO check here

        return trustManagers;
    }

    // The android verication helper callback
    public interface AndroidVerificationHelperCallback {
        void onResult(Boolean ctsProfileMatch, String message);
    }
}