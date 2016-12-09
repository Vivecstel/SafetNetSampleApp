package com.safety.net.sample.jobs;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.safety.net.sample.events.AndroidVerificationEvent;
import com.safety.net.sample.events.SafetyNetEvent;
import com.safety.net.sample.utils.SafetyNetApiHelper;

import org.greenrobot.eventbus.EventBus;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public class SafetyNetJob extends Job {

    // Log tag
    private final String TAG = SafetyNetJob.this.getClass().getSimpleName();
    // Exponential back off
    private static final int MAX_RETRIES = 5;
    private static final int EXPONENTIAL_BACK_OFF = 1000;

    public SafetyNetJob() {
        super(new Params(Priority.NORMAL));
    }

    @Override
    public void onAdded() {
        Log.d(TAG, "onAdded");
    }

    @Override
    public void onRun() throws Throwable {
        SafetyNetApiHelper safetyNetApiHelper = new SafetyNetApiHelper(getApplicationContext(),
                new SafetyNetApiHelper.SafetyNetApiHelperCallback() {
            @Override
            public void onError(String errorMessage) {
                EventBus.getDefault().post(new AndroidVerificationEvent(null, errorMessage));
            }
            @Override
            public void onResult(String jwsResult, long timestamp, byte[] requestNonce) {
                EventBus.getDefault().post(new SafetyNetEvent(jwsResult, timestamp, requestNonce));
            }
        });
        safetyNetApiHelper.connect();
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {
        Log.d(TAG, "onCancel");
        final String message = throwable != null ? throwable.getMessage() : "";
        EventBus.getDefault().post(new AndroidVerificationEvent(null, message));
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