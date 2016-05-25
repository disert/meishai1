package com.meishai.app.util;

import android.content.Context;
import android.view.View;

import com.meishai.app.listener.IDialogProtocol;
import com.meishai.app.widget.CustomDialog;

/**
 * Description the class 自定义对话框封装类
 */
public class DialogManager {

    /**
     * @param context         上下文对象
     * @param title           标题
     * @param message         提示信息
     * @param positiveBtnName 确定按钮
     * @param negativeBtnName 取消按钮
     * @param listener        按钮监听
     * @return Dialog 对话框对象
     * @Method: createMessageDialog
     * @Description: 创建标题、信息、按钮对话框
     */
    public static CustomDialog.Builder createMessageDialogBuilder(Context context, String title, String message, String positiveBtnName, String negativeBtnName, IDialogProtocol listener) {
        CustomDialog.Builder builder = new CustomDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveBtnName, listener);
        builder.setNegativeButton(negativeBtnName, listener);
        return builder;
    }

    /**
     * @param context 上下文对象
     * @param view    自定义布局
     * @return Dialog 对话框对象
     * @Method: createMessageDialog
     * @Description: 创建自定义对话框
     */
    public static CustomDialog.Builder createMessageDialogBuilder(Context context, View view) {
        CustomDialog.Builder builder = new CustomDialog.Builder(context);
        builder.setContentView(view);
        return builder;
    }
}