package com.meishai.ui.base;

import java.util.Stack;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.LruCache;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.app.listener.IDialogProtocol;
import com.meishai.app.util.DialogManager;
import com.meishai.app.widget.CustomDialog;
import com.meishai.app.widget.CustomDialog.Builder;
import com.meishai.net.volley.RequestQueue;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.ImageLoader.ImageCache;
import com.meishai.net.volley.toolbox.Volley;

public class BaseFragmentActivity extends FragmentActivity implements
        IDialogProtocol {

    private static final int DIALOG_EXIT_APP = 1;
    protected RequestQueue mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRequestQueue = GlobalContext.getInstance().getRequestQueue();
    }

    private int mCacheCount;

    protected void initImageCacheCount(int count) {
        if (null != mImageLoader) {
            throw new RuntimeException(
                    "BaseFragmentActivity: bitmap cache count must set before getImageLoader");
        }
        mCacheCount = count;
    }

    private ImageLoader mImageLoader = null;

    protected ImageLoader getImageLoader() {
        if (mCacheCount <= 0) {
            throw new RuntimeException("Bitmap cache count <= 0");
        }
        final LruCache<String, Bitmap> lruCache = new LruCache<String, Bitmap>(
                mCacheCount);
        ImageCache imageCache = new ImageCache() {

            @Override
            public Bitmap getBitmap(String url, ImageLoader.ImageContainer container, ImageLoader.ImageListener imageListener) {
                return getBitmap(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                lruCache.put(url, bitmap);
            }

            //			@Override
            public Bitmap getBitmap(String url) {
                return lruCache.get(url);
            }
        };
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(getRequestQueue(), imageCache);
        }
        return mImageLoader;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mRequestQueue.cancelAll(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public interface OnBackPressedListener {

        public boolean onBackPressed();
    }

    private Stack<OnBackPressedListener> mBackListenerStack = new Stack<BaseFragmentActivity.OnBackPressedListener>();

    public void setOnBackPressedListener(OnBackPressedListener listener) {
        mBackListenerStack.push(listener);
    }

    @Override
    public void onBackPressed() {
        if (mBackListenerStack.isEmpty()) {
            exit();
        } else {
            OnBackPressedListener listener = mBackListenerStack.pop();
            if (null != listener) {
                if (!listener.onBackPressed()) {
                    exit();
                }
            } else {
                exit();
            }
        }
    }

    public void popBackListener() {
        mBackListenerStack.pop();
    }

    private void exit() {
        CustomDialog builder = createDialogBuilder(this,
                getString(R.string.button_text_tips),
                getString(R.string.exit_dialog_title),
                getString(R.string.button_text_no),
                getString(R.string.button_text_yes)).create(DIALOG_EXIT_APP);
        builder.show();
    }

    @Override
    public Builder createDialogBuilder(Context context, String title,
                                       String message, String positiveBtnName, String negativeBtnName) {
        return DialogManager.createMessageDialogBuilder(context, title,
                message, positiveBtnName, negativeBtnName, this);
    }

    /**
     * 取消事件
     */
    @Override
    public void onPositiveBtnClick(int id, DialogInterface dialog, int which) {
    }

    /**
     * 确认事件
     */
    @Override
    public void onNegativeBtnClick(int id, DialogInterface dialog, int which) {
        switch (id) {
            case DIALOG_EXIT_APP:
                mBackListenerStack.clear();
                finish();
                break;
            default:
                break;
        }
    }
}
