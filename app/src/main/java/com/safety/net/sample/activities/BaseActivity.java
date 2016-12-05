package com.safety.net.sample.activities;

import com.safety.net.sample.R;

import org.greenrobot.eventbus.EventBus;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import icepick.Icepick;

public abstract class BaseActivity extends AppCompatActivity {

    // Butterknife unbinder
    private Unbinder unbinder;
    // Views
    @Nullable @BindView(R.id.toolbar) Toolbar toolbar;
    // Action bar
    protected ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
        setContentView(getLayoutRes());
        initToolbar();
    }

    protected int getLayoutRes() {
        return R.layout.activity_base;
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        unbinder = ButterKnife.bind(this);
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();

        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(false);
            mActionBar.setDisplayShowTitleEnabled(true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (shouldRegisterToBus()) {
            EventBus.getDefault().register(this);
        }
    }

    protected abstract boolean shouldRegisterToBus();

    @Override protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (shouldRegisterToBus()) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}