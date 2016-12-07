package com.safety.net.sample.jobs;

import com.google.android.gms.safetynet.SafetyNetApi;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.safety.net.sample.events.AndroidVerificationJobResultEvent;
import com.safety.net.sample.utils.AndroidVerificationHelper;

import org.greenrobot.eventbus.EventBus;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public class AndroidVerificationJob extends Job {

    // Log tag
    private final String TAG = AndroidVerificationJob.this.getClass().getSimpleName();
    // Priority
    private static final int PRIORITY = 1;
    // Exponential back off in ms
    private static final int EXPONENTIAL_BACK_OFF = 1000;
    // api key
    private String mApiKey;
    // package name
    private String mPackageName;
    // Attestation Result
    private SafetyNetApi.AttestationResult mAttestationResult;
    // Request nonce
    private byte[] mRequestNonce;

    public AndroidVerificationJob(String apiKey, Context context,
                                  SafetyNetApi.AttestationResult attestationResult, byte[] requestNonce) {
        super(new Params(PRIORITY));

        this.mApiKey = apiKey;
        this.mPackageName = context.getPackageName();
        this.mAttestationResult = attestationResult;
        this.mRequestNonce = requestNonce;
    }

    @Override
    public void onAdded() {
        Log.d(TAG, "onAdded");
    }

    @Override
    public void onRun() throws Throwable {
        Log.d(TAG, "onRun");
        AndroidVerificationHelper androidVerificationHelper = new AndroidVerificationHelper(mApiKey,
                mPackageName, new AndroidVerificationHelper.AndroidVerificationHelperCallback() {
            @Override
            public void onResult(Boolean ctsProfileMatch, String message) {
                EventBus.getDefault().post(new AndroidVerificationJobResultEvent(ctsProfileMatch, message));
            }
        });
        androidVerificationHelper.validate(mAttestationResult, mRequestNonce);
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {
        Log.d(TAG, "onCancel");
        final String message = throwable != null ? throwable.getMessage() : "";
        EventBus.getDefault().post(new AndroidVerificationJobResultEvent(null, message));
    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return RetryConstraint.createExponentialBackoff(runCount, EXPONENTIAL_BACK_OFF);
    }
}