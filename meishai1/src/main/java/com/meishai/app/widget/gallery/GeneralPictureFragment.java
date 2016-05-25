package com.meishai.app.widget.gallery;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.meishai.R;
import com.meishai.lib.photoview.AnimationRect;
import com.meishai.lib.photoview.PhotoView;
import com.meishai.lib.photoview.PhotoViewAttacher;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.ImageLoader.ImageListener;
import com.meishai.ui.base.BaseFragment;
import com.meishai.util.AndroidUtil;
import com.meishai.util.AnimationUtility;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;


public class GeneralPictureFragment extends BaseFragment {

    private static final int NAVIGATION_BAR_HEIGHT_DP_UNIT = 48;

    private static final int IMAGEVIEW_SOFT_LAYER_MAX_WIDTH = 2000;
    private static final int IMAGEVIEW_SOFT_LAYER_MAX_HEIGHT = 3000;

    private PhotoView photoView;

    public static final int ANIMATION_DURATION = 300;

    public static GeneralPictureFragment newInstance(String url, AnimationRect rect,
                                                     boolean animationIn) {
        GeneralPictureFragment fragment = new GeneralPictureFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        bundle.putParcelable("rect", rect);
        bundle.putBoolean("animationIn", animationIn);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gallery_general_layout, container, false);

        photoView = (PhotoView) view.findViewById(R.id.animation);

        // allowClickToCloseGallery
        photoView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                getActivity().onBackPressed();
            }
        });


        final String url = getArguments().getString("url");
        boolean animateIn = getArguments().getBoolean("animationIn");
        final AnimationRect rect = getArguments().getParcelable("rect");
        if (url.startsWith("http")) {
            ImageListener listener = ImageLoader.getImageListener(photoView, 0, 0);
            getImageLoader().get(url, listener);
        } else {
            com.album.utils.ImageLoader.getInstance().loadImage(url, photoView);
        }


//        final Runnable endAction = new Runnable() {
//            @Override
//            public void run() {
//                Bundle bundle = getArguments();
//                bundle.putBoolean("animationIn", false);
//            }
//        };
//
//        photoView.getViewTreeObserver()
//                .addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//                    @Override
//                    public boolean onPreDraw() {
//
//                        if (rect == null) {
//                            photoView.getViewTreeObserver().removeOnPreDrawListener(this);
//                            endAction.run();
//                            return true;
//                        }
//
//                        final Rect startBounds = new Rect(rect.scaledBitmapRect);
//                        final Rect finalBounds = AnimationUtility
//                                .getBitmapRectFromImageView(photoView);
//
//                        if (finalBounds == null) {
//                            photoView.getViewTreeObserver().removeOnPreDrawListener(this);
//                            endAction.run();
//                            return true;
//                        }
//
//                        float startScale = (float) finalBounds.width() / startBounds.width();
//
//                        if (startScale * startBounds.height() > finalBounds.height()) {
//                            startScale = (float) finalBounds.height() / startBounds.height();
//                        }
//
//                        int deltaTop = startBounds.top - finalBounds.top;
//                        int deltaLeft = startBounds.left - finalBounds.left;
//
//                        photoView.setPivotY(
//                                (photoView.getHeight() - finalBounds.height()) / 2);
//                        photoView.setPivotX((photoView.getWidth() - finalBounds.width()) / 2);
//
//                        photoView.setScaleX(1 / startScale);
//                        photoView.setScaleY(1 / startScale);
//
//                        photoView.setTranslationX(deltaLeft);
//                        photoView.setTranslationY(deltaTop);
//
//                        photoView.animate().translationY(0).translationX(0)
//                                .scaleY(1)
//                                .scaleX(1).setDuration(ANIMATION_DURATION)
//                                .setInterpolator(
//                                        new AccelerateDecelerateInterpolator())
//                                .withEndAction(endAction);
//
//                        AnimatorSet animationSet = new AnimatorSet();
//                        animationSet.setDuration(ANIMATION_DURATION);
//                        animationSet
//                                .setInterpolator(new AccelerateDecelerateInterpolator());
//
//                        animationSet.playTogether(ObjectAnimator.ofFloat(photoView,
//                                "clipBottom",
//                                AnimationRect.getClipBottom(rect, finalBounds), 0));
//                        animationSet.playTogether(ObjectAnimator.ofFloat(photoView,
//                                "clipRight",
//                                AnimationRect.getClipRight(rect, finalBounds), 0));
//                        animationSet.playTogether(ObjectAnimator.ofFloat(photoView,
//                                "clipTop", AnimationRect.getClipTop(rect, finalBounds), 0));
//                        animationSet.playTogether(ObjectAnimator.ofFloat(photoView,
//                                "clipLeft", AnimationRect.getClipLeft(rect, finalBounds), 0));
//
//                        animationSet.start();
//
//                        photoView.getViewTreeObserver().removeOnPreDrawListener(this);
//                        return true;
//                    }
//                });

        return view;
    }

    public void animationExit(ObjectAnimator backgroundAnimator) {

        if (Math.abs(photoView.getScale() - 1.0f) > 0.1f) {
            photoView.setScale(1, true);
            return;
        }

        getActivity().overridePendingTransition(0, 0);
        animateClose(backgroundAnimator);
    }

    private void animateClose(ObjectAnimator backgroundAnimator) {

        AnimationRect rect = getArguments().getParcelable("rect");

        if (rect == null) {
            photoView.animate().alpha(0);
            backgroundAnimator.start();
            return;
        }

        final Rect startBounds = rect.scaledBitmapRect;
        final Rect finalBounds = AnimationUtility.getBitmapRectFromImageView(photoView);

        if (finalBounds == null) {
            photoView.animate().alpha(0);
            backgroundAnimator.start();
            return;
        }

        if (AndroidUtil.isDevicePort() != rect.isScreenPortrait) {
            photoView.animate().alpha(0);
            backgroundAnimator.start();
            return;
        }

        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            startScale = (float) startBounds.height() / finalBounds.height();
        } else {
            startScale = (float) startBounds.width() / finalBounds.width();
        }

        final float startScaleFinal = startScale;

        int deltaTop = startBounds.top - finalBounds.top;
        int deltaLeft = startBounds.left - finalBounds.left;

        photoView.setPivotY((photoView.getHeight() - finalBounds.height()) / 2);
        photoView.setPivotX((photoView.getWidth() - finalBounds.width()) / 2);

        photoView.animate().translationX(deltaLeft).translationY(deltaTop)
                .scaleY(startScaleFinal)
                .scaleX(startScaleFinal).setDuration(ANIMATION_DURATION)
                .setInterpolator(new AccelerateDecelerateInterpolator());
//                .withEndAction(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        photoView.animate().alpha(0.0f).setDuration(200).withEndAction(
//                                new Runnable() {
//                                    @Override
//                                    public void run() {
//
//                                    }
//                                });
//                    }
//                });

        AnimatorSet animationSet = new AnimatorSet();
        animationSet.setDuration(ANIMATION_DURATION);
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());

        animationSet.playTogether(backgroundAnimator);

        animationSet.playTogether(ObjectAnimator.ofFloat(photoView,
                "clipBottom", 0,
                AnimationRect.getClipBottom(rect, finalBounds)));
        animationSet.playTogether(ObjectAnimator.ofFloat(photoView,
                "clipRight", 0,
                AnimationRect.getClipRight(rect, finalBounds)));
        animationSet.playTogether(ObjectAnimator.ofFloat(photoView,
                "clipTop", 0, AnimationRect.getClipTop(rect, finalBounds)));
        animationSet.playTogether(ObjectAnimator.ofFloat(photoView,
                "clipLeft", 0, AnimationRect.getClipLeft(rect, finalBounds)));

        animationSet.start();
    }
}
