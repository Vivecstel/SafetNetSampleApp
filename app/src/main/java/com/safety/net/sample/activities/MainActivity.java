package com.safety.net.sample.activities;

import com.safety.net.sample.R;
import com.safety.net.sample.fragments.MainFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends BaseActivity {

    // main fragment
    private Fragment mMainFragment;
    // main fragment tag
    private String MAIN_FRAGMENT_TAG = "MAIN_FRAGMENT_TAG";

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
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settingsItem :
                startActivity(SettingsActivity.getStartingIntent(MainActivity.this));
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