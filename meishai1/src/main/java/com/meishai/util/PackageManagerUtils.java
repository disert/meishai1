package com.meishai.util;

import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

public class PackageManagerUtils {

    /**
     * 判断是否安装pack命名的应用
     *
     * @param context
     * @param pack
     * @return
     */
    public static boolean isInstall(Context context, String pack) {
        if (TextUtils.isEmpty(pack)) {
            return false;
        }
        PackageManager packageManager = context.getPackageManager();
        List<ApplicationInfo> applications = packageManager.getInstalledApplications(0);
        for (ApplicationInfo applicationInfo : applications) {
            if (pack.equals(applicationInfo.packageName)) {
                return true;
            }
        }

        return false;
    }

}
