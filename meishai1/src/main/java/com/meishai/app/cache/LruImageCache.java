package com.meishai.app.cache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.util.LruCache;
import android.text.TextUtils;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.ImageLoader.ImageCache;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.util.DebugLog;
import com.meishai.util.DiskImageCacheUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LruImageCache implements ImageCache {

    private static LruCache<String, Bitmap> mMemoryCache;

    private static LruImageCache lruImageCache;

    private LruImageCache() {
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

    public static LruImageCache instance() {
        if (lruImageCache == null) {
            lruImageCache = new LruImageCache();
        }
        return lruImageCache;
    }

    //	@Override
    public Bitmap getBitmap(String arg0) {
        return mMemoryCache.get(arg0);
    }

    /**
     * 由于从网上拉取的数据volley会自动的调用put方法，为内存中存储一份，而我们从本地加载的
     * 数据就需要我们自己把其网内存中保存一份了，自己保存一份需要在DiskImageCache的getBitmap
     * 方法内部来做，毕竟它是在在线程中加载的
     *
     * @param url
     * @param container
     * @param imageListener
     * @return
     */
    @Override
    public Bitmap getBitmap(final String url, final ImageLoader.ImageContainer container, final ImageLoader.ImageListener imageListener) {
        //判空,服务器返回的图片链接有误时用默认图片显示,否则用null加载图片会报错
        if (TextUtils.isEmpty(url)) {
            return ConstantSet.defaultBitmap;
        }
        //从内存中获取缓存,
        Bitmap bitmap = getBitmap(url);
        //内存中没有
        if (bitmap == null) {
            //判断本地是否有,有 返回本地的  木有 返回null让volley从网上拉取
            if (DiskImageCacheUtil.getInstance().hasCacheBitmap(url)) {
                return DiskImageCache.instance().getBitmap(url, container, imageListener);
            } else
                return null;
        }
//        如果内存中有,返回内存中的  bitmap不为null,或者本地没有 bitmap是null,直接返回bitmap,
//        if (bitmap != null || !DiskImageCacheUtil.getInstance().hasCacheBitmap(url)) {
//            return bitmap;
//        }
//
//        //走到这,说明内存中没有,并且本地有 从本地获取缓存
//        bitmap = DiskImageCache.instance().getBitmap(url,container,imageListener);

        //内存中有
        return bitmap;
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        if (getBitmap(url) == null) {
            mMemoryCache.put(url, bitmap);
            DiskImageCacheUtil.getInstance().saveCacheBitmap(url, bitmap);
        }
    }

}
