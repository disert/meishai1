package com.meishai.app.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.app.widget.progressbar.RoundProgressBarWidthNumber;
import com.meishai.entiy.ReleaseData;
import com.meishai.ui.fragment.camera.PreviewActivity;
import com.meishai.ui.fragment.camera.ReleaseActivity1;

/**
 * 文件名： 描 述：发布的时候使用到的进度条 作 者：yl 时 间：2016/3/15 版 权：
 */
public class ProgressAction extends Dialog {

    private Context mContext;
    private RoundProgressBarWidthNumber mProgress;

    public ProgressAction(Context context) {
        super(context);
        this.mContext = context;
    }


    public ProgressAction(Context context, boolean cancelable,
                          OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    public ProgressAction(Context context, int theme) {
        super(context, theme);
    }


    public static ProgressAction show(Context context) {
        ProgressAction dialog = new ProgressAction(context,
                R.style.Custom_Progress);
        dialog.setContentView(R.layout.dialog_release_layout);

        // 按返回键是否取消
//		dialog.setCancelable(cancelable);
        dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        // 监听返回键处理
//		dialog.setOnCancelListener(cancelListener);
        // 设置居中
        dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        // 设置背景层透明度
        lp.dimAmount = 0.2f;
        dialog.getWindow().setAttributes(lp);
        // dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        dialog.show();
        return dialog;
    }

    @Override
    public void dismiss() {


        super.dismiss();
    }


    public RoundProgressBarWidthNumber getProgress() {
        return (RoundProgressBarWidthNumber) this
                .findViewById(R.id.progress);
    }

}
