package com.meishai.ui.base;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.meishai.GlobalContext;
import com.meishai.app.cache.DiskImageCache;
import com.meishai.app.cache.LruImageCache;
import com.meishai.app.widget.CustomProgress;
import com.meishai.net.VolleyHelper;
import com.meishai.net.volley.RequestQueue;
import com.meishai.net.volley.Response;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.StringRequest;
import com.umeng.analytics.MobclickAgent;

public class BaseActivity extends Activity {


    protected RequestQueue getRequestQueue() {
        return GlobalContext.getInstance().getRequestQueue();
    }

    protected ImageLoader getImageLoader() {
        return VolleyHelper.getImageLoader(this);
    }

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
    private Toast mToast;
    private CustomProgress mProgressDialog;
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

    protected void showToast(int resId) {
        showToast(getResources().getString(resId));
    }

    protected void showToast(String text) {
        if (null == mToast) {
            mToast = Toast.makeText(this, text, Toast.LENGTH_LONG);
        } else {
            mToast.setText(text);
            mToast.setDuration(Toast.LENGTH_LONG);
        }
        mToast.show();
    }

    public void showToast(String text, int duration) {
        if (null == mToast) {
            mToast = Toast.makeText(this, text, duration);
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
                    .show(this, message, true, null);
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
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        cancelToast();
        hideProgress();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        hideProgress();
        cancelToast();
        super.onDestroy();
    }
}
