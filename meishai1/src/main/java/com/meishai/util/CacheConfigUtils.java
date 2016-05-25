package com.meishai.util;

import android.content.SharedPreferences;

import com.meishai.GlobalContext;

/**
 * Created by Administrator on 2015/11/11.
 * 缓存的工具类
 */
public class CacheConfigUtils {

    public static SharedPreferences getCacheConfig() {
        return GlobalContext.getInstance().getCacheConfig();
    }

    public static String getCache(String key) {
        return getCacheConfig().getString(key, "");
    }

    /**
     * 该方法比较耗时,放在子线程中进行
     *
     * @param key
     * @param value
     */
    public static void putCache(final String key, final String value) {

        execute(new Runnable() {
            @Override
            public void run() {
                long start = System.currentTimeMillis();
                SharedPreferences.Editor editor = getCacheConfig().edit();
                editor.putString(key, value);
                editor.commit();
                DebugLog.d("put:" + (start - System.currentTimeMillis()));

            }
        });
    }

    public static void clearCache() {
        SharedPreferences.Editor editor = getCacheConfig().edit();
        editor.clear();
        editor.commit();
    }

    public static void execute(Runnable runnable) {
        GlobalContext.getInstance().getConfigPool().execute(runnable);
    }

}
