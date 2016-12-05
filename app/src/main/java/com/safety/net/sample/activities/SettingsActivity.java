package com.safety.net.sample.activities;

import com.safety.net.sample.R;
import com.safety.net.sample.fragments.SettingsFragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

public class SettingsActivity extends BaseActivity {

    // settings fragment
    private Fragment mSettingsFragment;
    // settings fragment tag
    private String SETTINGS_FRAGMENT_TAG = "SETTINGS_FRAGMENT_TAG";

    public static Intent getStartingIntent(Context packageContext) {
        return new Intent(packageContext, SettingsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initActionBar();

        FragmentManager fragmentManager = getFragmentManager();
        if (savedInstanceState == null) {
            initSettingsFragment(getFragmentManager());
        } else {

            mSettingsFragment = fragmentManager.findFragmentByTag(SETTINGS_FRAGMENT_TAG);
        }
    }

    private void initActionBar() {
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setDisplayShowTitleEnabled(true);
        }
    }

    private void initSettingsFragment(FragmentManager fragmentManager) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        mSettingsFragment = SettingsFragment.newInstance();
        fragmentTransaction.replace(R.id.contentLinearLayout, mSettingsFragment, SETTINGS_FRAGMENT_TAG);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                onBackPressed();
                return true;
            default :
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected boolean shouldRegisterToBus() {
        return false;
    }
}