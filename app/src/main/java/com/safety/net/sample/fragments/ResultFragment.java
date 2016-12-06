package com.safety.net.sample.fragments;

import com.hannesdorfmann.fragmentargs.annotation.Arg;
import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs;
import com.safety.net.sample.R;
import com.safety.net.sample.model.ResultModel;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;

@FragmentWithArgs
public class ResultFragment extends BaseFragment {

    // Arguments
    @Arg
    ResultModel resultModel;
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
            ctsProfileMatchImageView.setImageDrawable(changeDrawableColor(R.drawable.check, R.color.green));
        } else {
            ctsProfileMatchImageView.setImageDrawable(changeDrawableColor(R.drawable.cancel, R.color.red));
        }
    }

    private Drawable changeDrawableColor(@DrawableRes int drawableRes, @ColorRes int colorRes) {
        Drawable drawable = ContextCompat.getDrawable(getActivity(), drawableRes).mutate();
        int color = ContextCompat.getColor(getActivity(), colorRes);

        DrawableCompat.setTint(drawable, color);
        DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN);

        return drawable;
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