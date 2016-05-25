package com.meishai.net.volley.toolbox;

import com.meishai.R;

import android.content.Context;
import android.util.AttributeSet;


public class CoverImageView extends NetworkImageView {

    public CoverImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        inintData();
    }

    public CoverImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inintData();
    }

    public CoverImageView(Context context) {
        super(context);
        inintData();
    }

    private void inintData() {
        setDefaultImageResId(R.drawable.ic_launcher);
        setErrorImageResId(R.drawable.ic_launcher);
    }

}
