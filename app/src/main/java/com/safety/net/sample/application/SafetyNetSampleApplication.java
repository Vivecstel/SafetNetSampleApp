package com.safety.net.sample.application;

import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.config.Configuration;

import android.app.Application;

public class SafetyNetSampleApplication extends Application {

    // application instance
    private static SafetyNetSampleApplication instance;
    // job manager
    private JobManager jobManager;

    public SafetyNetSampleApplication() {
        instance = this;
    }

    public static SafetyNetSampleApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        getJobManager();
    }

    private void configureJobManager() {
        Configuration.Builder builder = new Configuration.Builder(this)
                .minConsumerCount(1) //always keep at least one consumer alive
                .maxConsumerCount(3) //up to 3 consumers at a time
                .loadFactor(3) //3 jobs per consumer
                .consumerKeepAlive(120); //wait 2 minute

        jobManager = new JobManager(builder.build());
    }

    public synchronized JobManager getJobManager() {
        if (jobManager == null) {
            configureJobManager();
        }
        return jobManager;
    }
}