package com.safety.net.sample.fragments;

import com.birbit.android.jobqueue.JobManager;
import com.safety.net.sample.BuildConfig;
import com.safety.net.sample.R;
import com.safety.net.sample.application.SafetyNetSampleApplication;
import com.safety.net.sample.events.SafetyNetJobResultEvent;
import com.safety.net.sample.jobs.SafetyNetJob;
import com.safety.net.sample.model.ResultModel;
import com.safety.net.sample.utils.PreferenceUtils;
import com.scottyab.safetynet.SafetyNetHelper;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.OnClick;
import icepick.State;

public class MainFragment extends BaseFragment {

    // Tag
    private final String TAG = MainFragment.this.getClass().getSimpleName();
    // Views
    @BindView(R.id.username) EditText username;
    @BindView(R.id.password) EditText password;
    @BindView(R.id.loginButton) Button loginButton;
    // Progress dialog
    private ProgressDialog mProgressDialog;
    // loading
    @State boolean loading = false;
    // listener
    private MainFragmentListener mListener;

    public interface MainFragmentListener {
        void goToSettings();
        void goToResult(ResultModel resultModel);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (MainFragmentListener) getContext();
        } catch (ClassCastException e) {
            throw new ClassCastException(getContext().toString()
                    + " must implement MainFragmentListener");
        }
    }

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_main;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setRetainInstance(true);
        setHasOptionsMenu(true);

        initProgressDialog();
    }

    private void initProgressDialog() {
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getString(R.string.loading));
        mProgressDialog.setCancelable(false);
        if (loading) {
            mProgressDialog.show();
        }
    }

    @Override
    public boolean shouldRegisterToBus() {
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settingsItem :
                mListener.goToSettings();
                return true;
            default :
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dismissProgressDialog();
    }

    private void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @OnClick(R.id.loginButton) void login() {
        if (!TextUtils.isEmpty(BuildConfig.SAFETY_NET_API_KEY)) {
            if (validations()) {
                runSafetyNetApi();
            }
        } else {
            Toast.makeText(getActivity(), R.string.safetyNetApiKeyEmpty, Toast.LENGTH_LONG).show();
        }
    }

    private boolean validations() {
        if (TextUtils.isEmpty(username.getText())) {
            Toast.makeText(getActivity(), R.string.usernameEmpty, Toast.LENGTH_LONG).show();
            return false;
        } else if (TextUtils.isEmpty(password.getText())) {
            Toast.makeText(getActivity(), R.string.passwordEmpty, Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }

    private void runSafetyNetApi() {
        loading = true;
        mProgressDialog.show();
        final Boolean shouldUseWrapper = PreferenceUtils.getBoolean(getActivity(),
                getString(R.string.useSafetyNetWrapperKey), false);
        if (shouldUseWrapper) {
            runWithWrapper();
        } else {
            runWithJob();
        }
    }

    private void runWithWrapper() {
        final SafetyNetHelper safetyNetHelper = new SafetyNetHelper(BuildConfig.SAFETY_NET_API_KEY);
        safetyNetHelper.requestTest(getActivity(), new SafetyNetHelper.SafetyNetWrapperCallback() {
            @Override
            public void error(int errorCode, String msg) {
                Log.e(TAG, msg);
                goToResult(null, msg);
            }

            @Override
            public void success(boolean ctsProfileMatch) {
                goToResult(ctsProfileMatch, "");
            }
        });
    }

    private void goToResult(Boolean ctsProfileMatch, String message) {
        loading = false;
        dismissProgressDialog();
        mListener.goToResult(new ResultModel(username.getText().toString(),
                ctsProfileMatch, message));
    }

    private void runWithJob() {
        JobManager jobManager = SafetyNetSampleApplication.getInstance().getJobManager();
        jobManager.addJobInBackground(new SafetyNetJob(getActivity()));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleSafetyNetJobResultEvent(SafetyNetJobResultEvent event) {
        Log.d(TAG, "SafetyNetJobResultEvent received");
        goToResult(event.getCtsProfileMatch(), event.getMessage());
    }
}