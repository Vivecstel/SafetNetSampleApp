package com.safety.net.sample.fragments;

import com.hannesdorfmann.fragmentargs.FragmentArgs;

import org.greenrobot.eventbus.EventBus;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment extends Fragment {

    // ButterKnife unbinder
    private Unbinder unbinder;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentArgs.inject(this);
    }

    @LayoutRes
    protected abstract int getLayoutRes();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return getLayoutRes() == 0 ? null : inflater.inflate(getLayoutRes(), container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (shouldRegisterToBus()) EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (shouldRegisterToBus()) EventBus.getDefault().unregister(this);
    }

    public abstract boolean shouldRegisterToBus();

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}