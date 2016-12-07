package com.safety.net.sample.jobs;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.safety.net.sample.BuildConfig;
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
        new SafetyNetJobHelper(context, BuildConfig.SAFETY_NET_API_KEY, new SafetyNetJobHelper.SafetyNetJobHelperCallback() {
            @Override
            public void result(Boolean ctsProfileMatch, String message) {
                EventBus.getDefault().post(new SafetyNetJobResultEvent(ctsProfileMatch, message));
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