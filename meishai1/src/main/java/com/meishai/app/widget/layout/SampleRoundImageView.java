package com.meishai.app.widget.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.meishai.R;

/**
 * 文件名：SampleRoundImageView
 * 描    述： 一个简单的显示圆角图片的控件,
 * 作    者： yl
 * 时    间：2015/12/11
 * 版    权：
 */
public class SampleRoundImageView extends LinearLayout {

    private final Context mContext;
    private ImageView mImage;
    private View view;
    private View mlb;
    private View mrb;
    private View mlt;
    private View mrt;

    public SampleRoundImageView(Context context) {
        this(context, null);
    }

    public SampleRoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    private void initView() {
        view = View.inflate(mContext, R.layout.round_corner_image, this);
        mImage = (ImageView) view.findViewById(R.id.round_image);

        mlb = view.findViewById(R.id.round_lb);
        mrb = view.findViewById(R.id.round_rb);
        mlt = view.findViewById(R.id.round_lt);
        mrt = view.findViewById(R.id.round_rt);
    }

    /**
     * 获得图片的imageview
     *
     * @return
     */
    public ImageView getImageView() {
        return mImage;
    }

    /**
     * 隐藏圆角
     */
    public SampleRoundImageView hideRound() {
        mlb.setVisibility(GONE);
        mrb.setVisibility(GONE);
        mlt.setVisibility(GONE);
        mrt.setVisibility(GONE);
        return this;
    }
}
