package com.meishai.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.ImageView;

import com.meishai.net.volley.toolbox.ImageLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by yl on 2015/11/16.
 * <p/>
 * 本地图片缓存的工具类
 */
public class DiskImageCacheUtil {

    //单例
    private DiskImageCacheUtil() {
    }

    private ExecutorService pool = Executors.newFixedThreadPool(8);
    private static DiskImageCacheUtil instance = new DiskImageCacheUtil();

    public static DiskImageCacheUtil getInstance() {
        return instance;
    }

    //本地图片保存的根目录
    public static final String ROOT_DIR = "/meishai/image";

    /**
     * 保存图片到本地,并在sharedPreference中维护一个(URL-保存路径)的键值对
     *
     * @param key    本地缓存集合中的键,
     * @param bitmap 要缓存的图片
     */
    public void saveCacheBitmap(final String key, final Bitmap bitmap) {
//        if(!key.startsWith("http")){
//            return;
//        }

        if (Looper.myLooper() == Looper.getMainLooper()) {
            //在主线程中
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    if (hasCacheBitmap(key)) {//如果已经有缓存了就直接退出
                        return;
                    }

                    final String cachePath = getCachePath(getImageName(key));
                    CacheConfigUtils.putCache(key, cachePath);
                    ImageUtil.saveBitMap2File(new File(cachePath), bitmap);
                }
            });
        } else {
            //在子线程
            if (hasCacheBitmap(key)) {//如果已经有缓存了就直接退出
                return;
            }

            final String cachePath = getCachePath(getImageName(key));
            CacheConfigUtils.putCache(key, cachePath);
            ImageUtil.saveBitMap2File(new File(cachePath), bitmap);
        }
    }

    /**
     * 根据缓存图片的键获取到缓存的图片
     *
     * @param cacheKey 缓存图片的键
     * @return 当获取成功时返回键所对应的图片, 否则返回null
     */
    public Bitmap getCacheBitmap(String cacheKey) {
        if (hasCacheBitmap(cacheKey)) {
            String name = CacheConfigUtils.getCache(cacheKey);
            Bitmap bmp = ImageUtil.getImageFromSDCard(name);
            return bmp;
        }
        return null;
    }

    /**
     * 是否有图片缓存
     *
     * @param key 用于查找缓存的key
     * @return
     */
    public boolean hasCacheBitmap(String key) {
        boolean hasCache = false;
        String value = CacheConfigUtils.getCache(key);
        hasCache = !TextUtils.isEmpty(value);
        if (hasCache) {
            //当配置文件中有缓存 再检查一下是否有对应的文件
            File file = new File(value);
            boolean exists = file.exists();
            if (!exists) {
                hasCache = false;
                CacheConfigUtils.putCache(key, "");
            }
        }
        return hasCache;
    }

    /**
     * 根据url生成一个缓存的图片名,图片名最好是唯一的,否则容易发生重名文件覆盖的问题
     *
     * @param url 图片URL,用于获取文件名
     * @return
     */
    public String getImageName(String url) {
        //http://img.meishai.com/avatar/8/10/79710/180x180.jpg
        //http://img.meishai.com/2016/0221/20160221101958921.jpg
        String subs[] = url.split("/");
        if (subs == null || subs.length < 4) {
            return ComUtils.randomStr(16);
        }
        StringBuilder name = new StringBuilder();
        for (int i = 3; i < subs.length; i++) {
            name.append("_");
            name.append(subs[i]);
        }
        DebugLog.w("文件名:" + name.toString());
        return name.toString();
    }

    /**
     * 判断sd卡是否装好
     *
     * @return
     */
    private boolean isMounted() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * 根据文件名拼接一个文件的绝对路径
     *
     * @param name 文件名
     * @return 生成的图片的缓存路径
     */
    public String getCachePath(String name) {
        String path = null;
        //检查是否有相同的缓存文件
        File file = new File(getCacheDir(ROOT_DIR), name);
        int i = 0;
        while (file.exists()) {
            file = new File(getCacheDir(ROOT_DIR), i + "_" + name);
            i++;
        }
        path = file.getAbsolutePath();
//        DebugLog.w("拼接好的路径:" + path);
        return path;
    }

    /**
     * 获取图片的缓存目录
     *
     * @return 图片的缓存目录
     */
    public File getCacheDir(String ROOT_DIR) {

        String dir = null;
        if (isMounted()) {//检查sd卡是否存在并装好
            dir = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            dir = Environment.getDataDirectory().getAbsolutePath();
        }
        //检查缓存目录是否存在
        File abRootDir = new File(dir, ROOT_DIR);
        if (!abRootDir.exists()) {
            if (!abRootDir.mkdirs()) {
                abRootDir = new File(dir);
            }
//            DebugLog.w(b?"目录创建成功":"目录创建失败");
        }
        //创建分目录
//        if(abRootDir.listFiles().length > 50){
//
//        }
        return abRootDir;
    }

    /**
     * 清空图片缓存
     */
    public void clearCache() {
        File cacheDir = getCacheDir(ROOT_DIR);
        File list[] = cacheDir.listFiles();
        for (int i = 0; i < list.length; i++) {
            if (!list[i].delete()) {
                DebugLog.d("删除文件失败:" + list[i].getName());
            } else {
                DebugLog.d("删除文件成功:" + list[i].getName());
            }
        }
    }

    public void get(final String cacheKey, final ImageLoader.ImageContainer container, final ImageLoader.ImageListener imageListener) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            //在主线程中
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    Bitmap bmp = getCacheBitmap(cacheKey);
                    container.setBitmap(bmp);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {

                        @Override
                        public void run() {
                            // 在回调中更新ui
                            imageListener.onResponse(container, true);
                        }
                    });

                }
            });
        } else {
            //在子线程
            Bitmap bmp = getCacheBitmap(cacheKey);
            container.setBitmap(bmp);
            new Handler(Looper.getMainLooper()).post(new Runnable() {

                @Override
                public void run() {
                    // 在回调中更新ui
                    imageListener.onResponse(container, true);
                }
            });
        }
    }
}
