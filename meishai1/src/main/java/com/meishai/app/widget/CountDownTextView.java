package com.meishai.app.widget;

import android.content.Context;
import android.os.CountDownTimer;
import android.widget.TextView;

import com.meishai.app.util.DateUtil;

/**
 * 倒计时控件
 *
 * @author sh
 */
public class CountDownTextView extends CountDownTimer {
    public static final long TIME_COUNT_FUTURE = 60000;
    public static final int TIME_COUNT_INTERVAL = 1000;

    private Context mContext;
    private TextView mTextView;
    private String mOriginalText;

    public CountDownTextView() {
        super(TIME_COUNT_FUTURE, TIME_COUNT_INTERVAL);
    }

    public CountDownTextView(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
    }

    public CountDownTextView(Context context, TextView mTextView,
                             long millisInFuture) {
        super(millisInFuture, TIME_COUNT_INTERVAL);
        init(context, mTextView);
    }

    public CountDownTextView(Context context, TextView mTextView,
                             String mOriginalText, long millisInFuture) {
        super(millisInFuture, TIME_COUNT_INTERVAL);
        init(context, mTextView, mOriginalText);
    }

    private void init(Context context, TextView mTextView) {
        this.mContext = context;
        this.mTextView = mTextView;
        this.mOriginalText = mTextView.getText().toString();
    }

    private void init(Context context, TextView mTextView, String mOriginalTex) {
        this.mContext = context;
        this.mTextView = mTextView;
        this.mOriginalText = mOriginalTex;
    }

    @Override
    public void onFinish() {
        if (mContext != null && mTextView != null) {
        }
    }

    @Override
    public void onTick(long millisUntilFinished) {
        if (mContext != null && mTextView != null) {
            mTextView.setText(mOriginalText
                    + DateUtil.timeFormat(millisUntilFinished));
        }
    }
}
