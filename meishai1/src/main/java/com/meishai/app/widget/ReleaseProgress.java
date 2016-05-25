package com.meishai.app.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import com.meishai.R;
import com.meishai.app.widget.progressbar.RoundProgressBarWidthNumber;

/**
 * 文件名：
 * 描    述：发布的时候使用到的进度条
 * 作    者：yl
 * 时    间：2016/3/15
 * 版    权：
 */
public class ReleaseProgress {
    private final Context mContext;
    private Dialog mDialog;
    private View mView;
    private RoundProgressBarWidthNumber mProgress;

    public ReleaseProgress(Context context) {
        mContext = context;
        mDialog = new Dialog(mContext);

        initDialog();
    }

    private void initDialog() {
        mView = View.inflate(mContext, R.layout.dialog_release_layout, null);
        mProgress = (RoundProgressBarWidthNumber) mView.findViewById(R.id.progress);
        mDialog.setContentView(mView);
    }


}
