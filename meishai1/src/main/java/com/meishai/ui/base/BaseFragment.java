package com.meishai.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.app.cache.DiskImageCache;
import com.meishai.app.cache.LruImageCache;
import com.meishai.app.widget.CustomProgress;
import com.meishai.net.VolleyHelper;
import com.meishai.net.volley.RequestQueue;
import com.meishai.net.volley.Response;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.NetworkImageView;
import com.meishai.net.volley.toolbox.StringRequest;
import com.meishai.ui.base.BaseFragmentActivity.OnBackPressedListener;
import com.meishai.ui.tab.ChildMainFragment;
import com.meishai.util.DebugLog;
import com.umeng.analytics.MobclickAgent;

import java.util.Objects;

public abstract class BaseFragment extends Fragment {

    protected Context mContext;
    protected static final int MSG_UPDATE_UI = 1;
    protected static final int MSG_LOADDATA_FAILT = 2;
    protected Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_UPDATE_UI:
                    updateUI(msg.obj);
                    break;
                case MSG_LOADDATA_FAILT:
                    failt(msg.obj);
                    break;
                default:
                    break;
            }
        }
    };
    protected Response.Listener<String> listener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Message message = Message.obtain();
            message.obj = response;
            message.what = MSG_UPDATE_UI;
            mHandler.sendMessage(message);
        }
    };
    protected Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            mHandler.sendEmptyMessage(MSG_LOADDATA_FAILT);
        }
    };

    protected void sendMsg(String url) {
        getRequestQueue().add(new StringRequest(url, listener, errorListener));
    }

    public void failt(Object ojb) {
    }

    public void updateUI(Object obj) {
    }

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
        return GlobalContext.getInstance().getRequestQueue();
    }

    public void setHeadImage(String url, NetworkImageView imageView) {

        imageView.setDefaultImageResId(R.drawable.head_default);
        imageView.setErrorImageResId(R.drawable.head_default);
        imageView.setImageUrl(url, getImageLoader());
    }

    protected ImageLoader getImageLoader() {
        return VolleyHelper.getImageLoader(mContext);
    }


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
        //DebugLog.d("onResume");
        doBackPress();
        MobclickAgent.onResume(mContext);
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
        MobclickAgent.onPause(mContext);
    }

    protected void gotoFragment(Class<?> cls, String name, Bundle args) {
        Fragment parent = getParentFragment();
        if (parent instanceof ChildMainFragment) {
            Fragment frg = ((ChildMainFragment) parent).newFragmentInstance(cls, name, args);
            frg.setArguments(args);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.hide(this);
            transaction.add(R.id.main_child_frame, frg, name);
            transaction.addToBackStack(cls.getName());
            transaction.setCustomAnimations(R.anim.move_left_in, R.anim.move_left_out);
            // transaction.commit();
            transaction.commitAllowingStateLoss();
        } else {
            DebugLog.d("this frament is impossible:" + cls.getName());
        }
    }
}
