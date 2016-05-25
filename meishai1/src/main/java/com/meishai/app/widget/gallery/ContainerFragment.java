package com.meishai.app.widget.gallery;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.lib.photoview.AnimationRect;
import com.meishai.ui.base.BaseFragment;
import com.nineoldandroids.animation.ObjectAnimator;

public class ContainerFragment extends BaseFragment {

    private TextView mWait;
    private TextView mError;
    private ProgressBar mProgressView;

    public static ContainerFragment newInstance(String url, AnimationRect rect,
                                                boolean animationIn, boolean firstOpenPage) {
        ContainerFragment fragment = new ContainerFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        bundle.putParcelable("rect", rect);
        bundle.putBoolean("animationIn", animationIn);
        bundle.putBoolean("firstOpenPage", firstOpenPage);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gallery_container_layout, container, false);

        mProgressView = (ProgressBar) view.findViewById(R.id.loading);
        mWait = (TextView) view.findViewById(R.id.wait);
        mError = (TextView) view.findViewById(R.id.error);

        Bundle bundle = getArguments();
        String url = bundle.getString("url");
        boolean animateIn = bundle.getBoolean("animationIn");

        displayPicture(url, animateIn);

        return view;
    }


    @SuppressLint("NewApi")
    private void displayPicture(String url, boolean animateIn) {
        GalleryAnimationActivity activity = (GalleryAnimationActivity) getActivity();

        AnimationRect rect = getArguments().getParcelable("rect");
        boolean firstOpenPage = getArguments().getBoolean("firstOpenPage");

        if (firstOpenPage) {
            animateIn = false;
            if (animateIn) {
                ObjectAnimator animator = activity.showBackgroundAnimate();
                animator.start();
            } else {
                activity.showBackgroundImmediately();
            }
            getArguments().putBoolean("firstOpenPage", false);
        }

        Fragment fragment = GeneralPictureFragment.newInstance(url, rect, animateIn);
        getChildFragmentManager().beginTransaction().replace(R.id.child, fragment)
                .commitAllowingStateLoss();
    }


    public boolean canAnimateCloseActivity() {
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.child);
        if (fragment instanceof GeneralPictureFragment) {
            return true;
        } else if (fragment instanceof GifPictureFragment) {
            return true;
        } else {
            return false;
        }
    }

    public void animationExit(ObjectAnimator backgroundAnimator) {
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.child);
        if (fragment instanceof GeneralPictureFragment) {
            GeneralPictureFragment child = (GeneralPictureFragment) fragment;
            child.animationExit(backgroundAnimator);
        } else if (fragment instanceof GifPictureFragment) {
            GifPictureFragment child = (GifPictureFragment) fragment;
            child.animationExit(backgroundAnimator);
        }
    }

}
