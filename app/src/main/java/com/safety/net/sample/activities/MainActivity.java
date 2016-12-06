package com.safety.net.sample.activities;

import com.safety.net.sample.R;
import com.safety.net.sample.fragments.MainFragment;
import com.safety.net.sample.fragments.ResultFragment;
import com.safety.net.sample.fragments.ResultFragmentBuilder;
import com.safety.net.sample.model.ResultModel;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends BaseActivity implements MainFragment.MainFragmentListener {

    // main fragment
    private Fragment mMainFragment;
    // fragment tags
    private String MAIN_FRAGMENT_TAG = "MAIN_FRAGMENT_TAG";
    private String RESULT_FRAGMENT_TAG = "RESULT_FRAGMENT_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            initMainFragment(fragmentManager);
        } else {
            mMainFragment = fragmentManager.findFragmentByTag(MAIN_FRAGMENT_TAG);
        }
    }

    private void initMainFragment(FragmentManager fragmentManager) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        mMainFragment = MainFragment.newInstance();
        fragmentTransaction.replace(R.id.contentLinearLayout, mMainFragment, MAIN_FRAGMENT_TAG);
        fragmentTransaction.commit();
    }

    @Override
    protected boolean shouldRegisterToBus() {
        return false;
    }

    @Override
    public void goToSettings() {
        startActivity(SettingsActivity.getStartingIntent(MainActivity.this));
    }

    @Override
    public void goToResult(ResultModel resultModel) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.contentLinearLayout,
                ResultFragmentBuilder.newResultFragment(resultModel), RESULT_FRAGMENT_TAG);
        fragmentTransaction.commit();
    }
}