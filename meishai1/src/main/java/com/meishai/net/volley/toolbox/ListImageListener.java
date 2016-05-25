package com.meishai.net.volley.toolbox;

import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.ImageLoader.ImageContainer;
import com.meishai.net.volley.toolbox.ImageLoader.ImageListener;
import com.meishai.util.DebugLog;

import android.os.Environment;
import android.widget.ImageView;

import java.io.File;


public class ListImageListener implements ImageListener {

    public interface OnSetImageBitmap {
        void onSetImageBitmap(ImageView imageView);
    }


    private ImageView mImageView;
    private int mDefaultImageResId;
    private int mErrorImageResId;
    private String mTag;

    public OnSetImageBitmap mListener;

    public ListImageListener(ImageView imageView, int defaultImageResId, int errorImageResId, String tag) {
        mImageView = imageView;
        mDefaultImageResId = defaultImageResId;
        mErrorImageResId = errorImageResId;
        mTag = tag;
    }

    public ListImageListener(ImageView imageView, int defaultImageResId, int errorImageResId, String tag, OnSetImageBitmap l) {
        this(imageView, defaultImageResId, errorImageResId, tag);
        mListener = l;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        if (mErrorImageResId != 0 && mImageView.getTag() != null && mTag.equals(mImageView.getTag())) {
            mImageView.setImageResource(mErrorImageResId);
        }
    }

    @Override
    public void onResponse(ImageContainer response, boolean isImmediate) {
        if (mImageView.getTag() != null && mTag.equals(mImageView.getTag())) {
            if (response.getBitmap() != null) {

//				getCacheDir();
                mImageView.setImageBitmap(response.getBitmap());
                if (mListener != null) {
                    mListener.onSetImageBitmap(mImageView);
                }
            } else if (mDefaultImageResId != 0) {
                mImageView.setImageResource(mDefaultImageResId);
            }
        }
    }

    private String getCacheDir() {
        String cacheDir = "";

        cacheDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/meishai/image";
        File file = new File(cacheDir);
        if (!file.exists()) {
            file.mkdirs();
        }
        DebugLog.w("data路径:" + cacheDir);
        return cacheDir;

    }

    public int getDefaultImageResId() {
        return mDefaultImageResId;
    }


}
