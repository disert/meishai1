package com.meishai.app.widget;

import com.meishai.R;
import com.meishai.util.DebugLog;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TopBackLayout extends RelativeLayout {

    private Button mBtnBack;
    private TextView mTvTitle;

    private OnClickListener mListener;
    private ImageView mMore;

    public TopBackLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
        getAttrs(context, attrs);
    }

    public TopBackLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        getAttrs(context, attrs);
    }

    public TopBackLayout(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View convertView = inflater.inflate(R.layout.top_back_layout, this, true);

        mBtnBack = (Button) convertView.findViewById(R.id.back_btn);
        mTvTitle = (TextView) convertView.findViewById(R.id.title_tv);
        mMore = (ImageView) convertView.findViewById(R.id.share);

        mBtnBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onClick(v);
                }
            }
        });
    }

    private void getAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.top_back_attr);
        int count = ta.getIndexCount();
        for (int i = 0; i < count; i++) {
            int itemId = ta.getIndex(i);
            switch (itemId) {
                case R.styleable.top_back_attr_titleText:
                    String title = ta.getString(itemId);
                    mTvTitle.setText(title);
                    break;
                case R.styleable.top_back_attr_backText:
                    String back = ta.getString(itemId);
                    mBtnBack.setText(back);
                    break;
                default:
                    break;
            }
            DebugLog.d("itemId:" + itemId);
        }

        ta.recycle();
    }

    public void setOnBackListener(OnClickListener listener) {
        mListener = listener;
    }

    public void setOnMoreListener(OnClickListener listener) {
        mMore.setVisibility(VISIBLE);
        mMore.setOnClickListener(listener);
    }

    public void setBackText(String text) {
        mBtnBack.setText(text);
    }

    public void setTitle(String text) {
        mTvTitle.setText(text);
    }

}
