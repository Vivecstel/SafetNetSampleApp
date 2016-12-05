package com.safety.net.sample.jobs;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.safety.net.sample.events.SafetyNetJobResultEvent;

import org.greenrobot.eventbus.EventBus;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.security.SecureRandom;

public class SafetyNetJob extends Job {

    // Log tag
    private final String TAG = SafetyNetJob.this.getClass().getSimpleName();
    // Priority
    private static final int PRIORITY = 1;
    //
    private Context context;
    private SecureRandom secureRandom;
    //
    private GoogleApiClient mGoogleApiClient;

    public SafetyNetJob(Context context) {
        super(new Params(PRIORITY));

        this.context = context.getApplicationContext();
        this.secureRandom = new SecureRandom();
    }

    @Override
    public void onAdded() {
        Log.d(TAG, "onAdded");
    }

    @Override
    public void onRun() throws Throwable {
        Log.d(TAG, "onRun");
        mGoogleApiClient = getGoogleApiClient();
        mGoogleApiClient.connect();
    }

    private GoogleApiClient getGoogleApiClient() {
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
                        EventBus.getDefault().post(new SafetyNetJobResultEvent("Google Play services connection failed"));
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
                            EventBus.getDefault().post(new SafetyNetJobResultEvent(jwsResult));
                        } else {
                            EventBus.getDefault().post(new SafetyNetJobResultEvent("SafetyNetApi.AttestationResult success == false or empty payload"));
                        }
                    }
                });
    }

    private byte[] generateRequestNonce() {
        byte[] nonce = new byte[32];
        this.secureRandom.nextBytes(nonce);
        return nonce;
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {
        Log.d(TAG, "onCancel");
        EventBus.getDefault().post(new SafetyNetJobResultEvent(throwable != null ? throwable.getMessage() : ""));
    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return RetryConstraint.createExponentialBackoff(runCount, 1000);
    }
}