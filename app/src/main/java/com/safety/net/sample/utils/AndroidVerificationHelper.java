package com.safety.net.sample.utils;

import com.google.gson.Gson;

import com.safety.net.sample.application.SafetyNetSampleApplication;
import com.safety.net.sample.model.SafetyNetResponse;
import com.safety.net.sample.model.VerifyRequest;
import com.safety.net.sample.model.VerifyResponse;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Base64;

public class AndroidVerificationHelper {

    // Verification Url
    private static final String URL = "https://www.googleapis.com/androidcheck/v1/attestations/verify?key=";
    // validation helper
    private ValidationHelper mValidationHelper;
    // Device verification api key
    private String mApiKey;
    // Gson
    private Gson mGson;
    // Callback
    private AndroidVerificationHelperCallback mCallback;

    public AndroidVerificationHelper(ValidationHelper validationHelper, String apiKey,
                                     AndroidVerificationHelperCallback callback) {
        this.mValidationHelper = validationHelper;
        this.mApiKey = apiKey;
        this.mGson = SafetyNetSampleApplication.getInstance().getGson();
        this.mCallback = callback;
    }

    // Validation method of attestationResult
    @SuppressWarnings("ConstantConditions")
    public void validate(@NonNull String jwsResult, long timestamp, @NonNull byte[] requestNonce) {
        // parse the jws result
        final SafetyNetResponse safetyNetResponse = parseJwsResult(jwsResult);
        // validate safety net response with validation helper
        if (mValidationHelper.validateSafetyNetResponse(safetyNetResponse, timestamp, requestNonce)) {
            // if api key is empty there no need to call android verification service
            if (TextUtils.isEmpty(mApiKey)) {
                mCallback.onResult(safetyNetResponse.getCtsProfileMatch(),
                        "Android Verification Api Key missing");
            } else {
                // execute post request needed for android verification api
                HttpClient httpClient = new HttpClient(mGson);
                VerifyResponse verifyResponse = httpClient.executePostRequest(URL + mApiKey,
                        new VerifyRequest(jwsResult), VerifyResponse.class);
                // if response is null something went wrong with the request
                if (verifyResponse == null) {
                    mCallback.onResult(safetyNetResponse.getCtsProfileMatch(),
                            "Android verification response is empty");
                } else {
                    // check if signature is valid
                    if (verifyResponse.getIsValidSignature()) {
                        mCallback.onResult(safetyNetResponse.getCtsProfileMatch(), "");
                    } else {
                        mCallback.onResult(safetyNetResponse.getCtsProfileMatch(),
                                "Android verification response : not valid signature");
                    }
                }
            }
        // safety net response failed to pass the validation tests
        } else {
            mCallback.onResult(null, "Safety Net response validation failed");
        }
    }

    // Parse the Jws result to safety net response
    private SafetyNetResponse parseJwsResult(String jwsResult) {
        // Header.Payload.Signature jws result parts
        // Split the result at . to get the parts
        String[] jwtResultParts = jwsResult.split("\\.");
        // Check if the previous array has 3 parts in order to later use the second of them
        if (jwtResultParts.length == 3) {
            // Decode the first part with Base64
            String decodedPayload = new String(Base64.decode(jwtResultParts[1], Base64.NO_WRAP));
            // Parse it with gson
            return mGson.fromJson(decodedPayload, SafetyNetResponse.class);
        } else {
            return null;
        }
    }

    // The android verification helper callback
    public interface AndroidVerificationHelperCallback {
        void onResult(Boolean ctsProfileMatch, String message);
    }
}