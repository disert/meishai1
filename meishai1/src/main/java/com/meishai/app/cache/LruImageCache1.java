package com.meishai.app.cache;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.text.TextUtils;

import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.ImageLoader.ImageCache;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.util.DebugLog;
import com.meishai.util.DiskImageCacheUtil;

public class LruImageCache1 {

    private static LruCache<String, Bitmap> mMemoryCache;

    private static LruImageCache1 lruImageCache;

    private LruImageCache1() {
        mMemoryCache = ConstantSet.MEMORY_CACHE;
        if (mMemoryCache == null) {
            int maxMemory = (int) Runtime.getRuntime().maxMemory();
            int cacheSize = maxMemory / 8;
            DebugLog.i("cacheSize:" + cacheSize);
            mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    return bitmap.getRowBytes() * bitmap.getHeight();
                }
            };
            ConstantSet.MEMORY_CACHE = mMemoryCache;
        }
    }

    public static LruImageCache1 instance() {
        if (lruImageCache == null) {
            lruImageCache = new LruImageCache1();
        }
        return lruImageCache;
    }

    public Bitmap getBitmap(String arg0) {
        return mMemoryCache.get(arg0);
    }

    public void putBitmap(String arg0, Bitmap arg1) {
        if (getBitmap(arg0) == null) {
            mMemoryCache.put(arg0, arg1);
        }
    }

}
