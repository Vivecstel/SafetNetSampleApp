package com.safety.net.sample.fragments;

import com.hannesdorfmann.fragmentargs.annotation.Arg;
import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs;
import com.safety.net.sample.R;

@FragmentWithArgs
public class ResultFragment extends BaseFragment {

    // Arguments
    @Arg String username;
    @Arg boolean ctsProfileMatch;
    @Arg String message;
    // views

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_result;
    }

    @Override
    public boolean shouldRegisterToBus() {
        return false;
    }
}