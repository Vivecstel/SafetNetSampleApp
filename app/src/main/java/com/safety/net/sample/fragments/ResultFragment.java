package com.safety.net.sample.fragments;

import com.hannesdorfmann.fragmentargs.annotation.Arg;
import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs;
import com.safety.net.sample.R;
import com.safety.net.sample.model.ResultModel;
import com.safety.net.sample.utils.Utils;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;

@FragmentWithArgs
public class ResultFragment extends BaseFragment {

    // Arguments
    @Arg ResultModel resultModel;
    // views
    @BindView(R.id.ctsProfileMatchImageView) AppCompatImageView ctsProfileMatchImageView;
    @BindView(R.id.welcome) TextView welcome;
    @BindView(R.id.message) TextView message;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initUi();
    }

    private void initUi() {
        if (resultModel.getCtsProfileMatch() != null) {
            setImage(resultModel.getCtsProfileMatch());
        } else {
            ctsProfileMatchImageView.setVisibility(View.GONE);
        }
        welcome.setText(getString(R.string.welcome, resultModel.getUsername()));
        message.setText(resultModel.getMessage());
    }

    private void setImage(boolean ctsProfileMatch) {
        if (ctsProfileMatch) {
            ctsProfileMatchImageView.setImageDrawable(Utils.changeDrawableColor(getActivity(),
                    R.drawable.check, R.color.green));
        } else {
            ctsProfileMatchImageView.setImageDrawable(Utils.changeDrawableColor(getActivity(),
                    R.drawable.cancel, R.color.red));
        }
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_result;
    }

    @Override
    public boolean shouldRegisterToBus() {
        return false;
    }
}