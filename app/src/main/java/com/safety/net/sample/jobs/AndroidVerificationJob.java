package com.safety.net.sample.jobs;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.safety.net.sample.events.AndroidVerificationEvent;
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
    // validation helper
    private ValidationHelper mValidationHelper;
    // api key
    private String mApiKey;
    // timestamp
    private long mTimestamp;
    // Request nonce
    private byte[] mRequestNonce;
    // jws result
    private String mJwsResult;

    public AndroidVerificationJob(Context context, String apiKey, String jwsResult,
                                  long timestamp, byte[] requestNonce) {
        super(new Params(Priority.NORMAL));

        this.mValidationHelper = new ValidationHelper(context);
        this.mApiKey = apiKey;
        this.mJwsResult = jwsResult;
        this.mTimestamp = timestamp;
        this.mRequestNonce = requestNonce;
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
                EventBus.getDefault().post(new AndroidVerificationEvent(ctsProfileMatch, message));
            }
        });
        androidVerificationHelper.validate(mJwsResult, mTimestamp, mRequestNonce);
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {
        Log.d(TAG, "onCancel");
        final String message = throwable != null ? throwable.getMessage() : "";
        EventBus.getDefault().post(new AndroidVerificationEvent(null, message));
    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return RetryConstraint.CANCEL;
    }
}