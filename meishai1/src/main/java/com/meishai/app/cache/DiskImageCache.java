package com.meishai.app.cache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.util.DiskImageCacheUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2015/11/13.
 * 用于做本地图片缓存的实现类,直接在该接口中写的话会让程序不流程,该接口目前有些无法满足我的需求了
 * <p/>
 * yl
 */
public class DiskImageCache implements ImageLoader.ImageCache {


    private DiskImageCache() {
    }

    public static DiskImageCache mImageCache = new DiskImageCache();
    private ExecutorService pool = Executors.newFixedThreadPool(8);

    public static DiskImageCache instance() {
        return mImageCache;
    }

    @Override
    public Bitmap getBitmap(final String url, final ImageLoader.ImageContainer container, final ImageLoader.ImageListener imageListener) {

        if (DiskImageCacheUtil.getInstance().hasCacheBitmap(url)) {

            if (Looper.myLooper() == Looper.getMainLooper()) {
                //主线程中
                pool.execute(new Runnable() {
                    @Override
                    public void run() {
                        final Bitmap bitmap = DiskImageCacheUtil.getInstance().getCacheBitmap(url);
                        //加载好图片之后要往内存中保存一份
                        if (ConstantSet.MEMORY_CACHE != null && ConstantSet.MEMORY_CACHE.get(url) != null) {
                            ConstantSet.MEMORY_CACHE.put(url, bitmap);
                        }
                        //想在这里面更新界面,需要知道的参数有两个,ImageLoader.ImageContainer container,ImageLoader.ImageListener imageListener
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {

                                if (imageListener != null && container != null) {
                                    container.setBitmap(bitmap);
                                    imageListener.onResponse(container, true);
                                }
                            }
                        });
                    }
                });
            } else {
                //子线程中
                final Bitmap bitmap = DiskImageCacheUtil.getInstance().getCacheBitmap(url);
                //加载好图片之后要往内存中保存一份
                if (ConstantSet.MEMORY_CACHE != null && ConstantSet.MEMORY_CACHE.get(url) != null) {
                    ConstantSet.MEMORY_CACHE.put(url, bitmap);
                }
                //想在这里面更新界面,需要知道的参数有两个,ImageLoader.ImageContainer container,ImageLoader.ImageListener imageListener
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {

                        if (imageListener != null && container != null) {
                            container.setBitmap(bitmap);
                            imageListener.onResponse(container, true);
                        }
                    }
                });

            }

            return ConstantSet.defaultBitmap;
        } else {
            return null;
        }

    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        DiskImageCacheUtil.getInstance().saveCacheBitmap(url, bitmap);
    }

}
