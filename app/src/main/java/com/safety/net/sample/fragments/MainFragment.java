package com.safety.net.sample.fragments;

import com.safety.net.sample.R;

import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.OnClick;

public class MainFragment extends BaseFragment {

    // Views
    @BindView(R.id.username) EditText username;
    @BindView(R.id.password) EditText password;
    @BindView(R.id.loginButton) Button loginButton;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_main;
    }

    @OnClick(R.id.loginButton) void login() {

    }
}