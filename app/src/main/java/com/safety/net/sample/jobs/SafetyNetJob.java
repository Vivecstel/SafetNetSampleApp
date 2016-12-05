package com.safety.net.sample.jobs;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.safety.net.sample.events.SafetyNetJobResultEvent;
import com.safety.net.sample.utils.SafetyNetJobHelper;

import org.greenrobot.eventbus.EventBus;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public class SafetyNetJob extends Job {

    // Log tag
    private final String TAG = SafetyNetJob.this.getClass().getSimpleName();
    // Priority
    private static final int PRIORITY = 1;
    // Exponential back off in ms
    private static final int EXPONENTIAL_BACK_OFF = 1000;
    // Context
    private Context context;

    public SafetyNetJob(Context context) {
        super(new Params(PRIORITY));
        this.context = context.getApplicationContext();
    }

    @Override
    public void onAdded() {
        Log.d(TAG, "onAdded");
    }

    @Override
    public void onRun() throws Throwable {
        Log.d(TAG, "onRun");
        new SafetyNetJobHelper(context, new SafetyNetJobHelper.SafetyNetJobHelperCallback() {
            @Override
            public void error(String errorMessage) {
                EventBus.getDefault().post(new SafetyNetJobResultEvent(null, errorMessage));
            }

            @Override
            public void success(boolean ctsProfileMatch) {
                EventBus.getDefault().post(new SafetyNetJobResultEvent(ctsProfileMatch, ""));
            }
        });
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {
        Log.d(TAG, "onCancel");
        final String message = throwable != null ? throwable.getMessage() : "";
        EventBus.getDefault().post(new SafetyNetJobResultEvent(null, message));
    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return RetryConstraint.createExponentialBackoff(runCount, EXPONENTIAL_BACK_OFF);
    }
}