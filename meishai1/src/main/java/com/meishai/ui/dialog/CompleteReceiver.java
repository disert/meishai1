package com.meishai.ui.dialog;

import java.io.File;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class CompleteReceiver extends BroadcastReceiver {
    public String save_path = "";

    @Override
    public void onReceive(Context context, Intent intent) {
        downComplete(context, save_path);
        context.unregisterReceiver(this);
    }

    private void downComplete(Context context, String filePath) {
        File _file = new File(filePath);
        Intent intent = new Intent();
//		System.out.println("安装apk ：" + _file.getName() + " : " + _file.length()
//				+ "--" + _file.getPath() + "--" + _file.canRead() + "--"
//				+ _file.exists());
        intent.setAction("android.intent.action.VIEW");// 向用户显示数据
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 以新压入栈
        intent.addCategory("android.intent.category.DEFAULT");
        Uri abc = Uri.fromFile(_file);
        intent.setDataAndType(abc, "application/vnd.android.package-archive");

        context.startActivity(intent);
    }

};
