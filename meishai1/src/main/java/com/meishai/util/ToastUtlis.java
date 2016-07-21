package com.meishai.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/4/23.
 */
public class ToastUtlis {
    public static boolean isShowToast = true;

    public static void showToast(Context context, String message) {
        if (isShowToast) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
    }
}
