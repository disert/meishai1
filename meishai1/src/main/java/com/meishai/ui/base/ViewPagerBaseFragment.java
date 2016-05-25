package com.meishai.ui.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import com.meishai.R;
import com.meishai.app.cache.LruImageCache;
import com.meishai.app.widget.CustomProgress;
import com.meishai.net.volley.RequestQueue;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.NetworkImageView;
import com.meishai.ui.base.BaseFragmentActivity.OnBackPressedListener;
import com.meishai.ui.tab.ChildMainFragment;
import com.meishai.util.DebugLog;

public abstract class ViewPagerBaseFragment extends Fragment {

    protected boolean isVisible;

    /**
     * 在这里实现Fragment数据的缓加载.
     *
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    protected void onVisible() {
        lazyLoad();
    }

    protected abstract void lazyLoad();

    protected void onInvisible() {
    }

    public Context mContext;
    private ImageLoader imageLoader = null;

    /**
     * <p>
     * Title: onCreate
     * </p>
     * <p>
     * Description:
     * </p>
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    public RequestQueue getRequestQueue() {
        Activity parent = this.getActivity();
        if (parent instanceof BaseFragmentActivity) {
            RequestQueue queue = ((BaseFragmentActivity) parent)
                    .getRequestQueue();
            return queue;
        } else if (parent instanceof BaseActivity) {
            RequestQueue queue = ((BaseActivity) parent).getRequestQueue();
            return queue;
        } else {
            throw new RuntimeException("not found RequestQueue");
        }
    }

    public void setHeadImage(String url, NetworkImageView imageView) {
        if (null == imageLoader) {
            imageLoader = new ImageLoader(getRequestQueue(),
                    LruImageCache.instance());
        }
        imageView.setDefaultImageResId(R.drawable.head_default);
        imageView.setErrorImageResId(R.drawable.head_default);
        imageView.setImageUrl(url, imageLoader);
    }

    // protected ImageLoader getImageLoader() {
    // Activity parent = this.getActivity();
    // if(parent instanceof BaseFragmentActivity) {
    // ImageLoader imageLoadaer =
    // ((BaseFragmentActivity)parent).getImageLoader();
    // return imageLoadaer;
    // } else if(parent instanceof BaseActivity ){
    // ImageLoader imageLoader = ((BaseActivity)parent).getImageLoader();
    // return imageLoader;
    // } else {
    // throw new RuntimeException("not found ImageLoader");
    // }
    // }
    private Toast mToast;
    private CustomProgress mProgressDialog;

    public void showToast(int resId) {
        showToast(getResources().getString(resId));
    }

    public void showToast(String text) {
        if (null == mToast) {
            mToast = Toast
                    .makeText(this.getActivity(), text, Toast.LENGTH_LONG);
        } else {
            mToast.setText(text);
            mToast.setDuration(Toast.LENGTH_LONG);
        }
        mToast.show();
    }

    public void showToast(String text, int duration) {
        if (null == mToast) {
            mToast = Toast.makeText(this.getActivity(), text, duration);
        } else {
            mToast.setText(text);
            mToast.setDuration(duration);
        }
        mToast.show();
    }

    public void cancelToast() {
        if (null != mToast) {
            mToast.cancel();
        }
    }

    public void showProgress(String title, String message) {
        if (null == mProgressDialog) {
            mProgressDialog = CustomProgress
                    .show(mContext, message, true, null);
        } else {
            if (!mProgressDialog.isShowing()) {
                mProgressDialog.show();
            }
        }
    }

    public void hideProgress() {
        if (null != mProgressDialog && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        DebugLog.d("onResume");
        doBackPress();
    }

    private boolean mDoBackClick = false;

    public void doBackClick() {
        FragmentActivity parent = getActivity();
        if (parent instanceof BaseFragmentActivity) {
            ((BaseFragmentActivity) parent).popBackListener();
            mDoBackClick = true;
        }
        getFragmentManager().popBackStackImmediate();
    }

    protected void doBackPress() {
        // AndroidUtil.hideSoftInput(mContext);
        FragmentActivity parent = getActivity();
        if (parent instanceof BaseFragmentActivity && !mDoBackClick) {
            ((BaseFragmentActivity) parent)
                    .setOnBackPressedListener(new OnBackPressedListener() {

                        @Override
                        public boolean onBackPressed() {
                            FragmentManager manager = getFragmentManager();
                            if (null == manager) {
                                return false;
                            } else {
                                return manager.popBackStackImmediate();
                            }
                        }
                    });
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        cancelToast();
        hideProgress();
        mDoBackClick = false;
    }

    protected void gotoFragment(Class<?> cls, String name, Bundle args) {
        Fragment parent = getParentFragment();
        if (parent instanceof ChildMainFragment) {
            Fragment frg = ((ChildMainFragment) parent).newFragmentInstance(
                    cls, name, args);
            FragmentTransaction transaction = getFragmentManager()
                    .beginTransaction();
            transaction.hide(this);
            transaction.add(R.id.main_child_frame, frg, name);
            transaction.addToBackStack(cls.getName());
            transaction.setCustomAnimations(R.anim.move_left_in,
                    R.anim.move_left_out);
            // transaction.commit();
            transaction.commitAllowingStateLoss();
        } else {
            DebugLog.d("this frament is impossible:" + cls.getName());
        }
    }
}
