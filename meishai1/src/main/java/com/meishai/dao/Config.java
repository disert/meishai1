package com.meishai.dao;

import java.io.File;

import com.meishai.util.DebugLog;

import android.content.Context;

public class Config {

    private String mCacheRoot;


    public Config(Context context) {
        File dir = context.getExternalFilesDir("");
        if (dir == null) {
            mCacheRoot = context.getFilesDir().toString();
        } else {
            mCacheRoot = dir.toString();
        }

        DebugLog.d(mCacheRoot);
        ;
    }

    public String getCrashLogDir() {
        String dir = mCacheRoot + "/crash";
        File f = new File(dir);
        if (!f.exists()) {
            f.mkdirs();
        }
        return dir;
    }

}
