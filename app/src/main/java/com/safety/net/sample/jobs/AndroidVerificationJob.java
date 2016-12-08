package com.safety.net.sample.jobs;

import com.google.android.gms.safetynet.SafetyNetApi;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.safety.net.sample.events.AndroidVerificationJobResultEvent;
import com.safety.net.sample.utils.AndroidVerificationHelper;
import com.safety.net.sample.utils.ValidationHelper;

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
    // Exponential back off
    private static final int MAX_RETRIES = 5;
    private static final int EXPONENTIAL_BACK_OFF = 1000;
    // validation helper
    private ValidationHelper mValidationHelper;
    // api key
    private String mApiKey;
    // timestamp
    private long mTimestamp;
    // Request nonce
    private byte[] mRequestNonce;
    // Attestation Result
    private SafetyNetApi.AttestationResult mAttestationResult;

    public AndroidVerificationJob(Context context, String apiKey, long timestamp, byte[] requestNonce,
                                  SafetyNetApi.AttestationResult attestationResult) {
        super(new Params(PRIORITY));

        this.mValidationHelper = new ValidationHelper(context);
        this.mApiKey = apiKey;
        this.mTimestamp = timestamp;
        this.mRequestNonce = requestNonce;
        this.mAttestationResult = attestationResult;
    }

    @Override
    public void onAdded() {
        Log.d(TAG, "onAdded");
    }

    @Override
    public void onRun() throws Throwable {
        Log.d(TAG, "onRun");
        AndroidVerificationHelper androidVerificationHelper = new AndroidVerificationHelper(
                mValidationHelper, mApiKey, new AndroidVerificationHelper.AndroidVerificationHelperCallback() {
            @Override
            public void onResult(Boolean ctsProfileMatch, String message) {
                EventBus.getDefault().post(new AndroidVerificationJobResultEvent(ctsProfileMatch, message));
            }
        });
        androidVerificationHelper.validate(mAttestationResult, mTimestamp, mRequestNonce);
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {
        Log.d(TAG, "onCancel");
        final String message = throwable != null ? throwable.getMessage() : "";
        EventBus.getDefault().post(new AndroidVerificationJobResultEvent(null, message));
    }

    @Override
    protected int getRetryLimit() {
        return MAX_RETRIES;
    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return RetryConstraint.createExponentialBackoff(runCount, EXPONENTIAL_BACK_OFF);
    }
}