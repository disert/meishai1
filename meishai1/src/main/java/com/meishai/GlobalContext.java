package com.meishai;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;

import com.emoji.MsgEmojiModle;
import com.meishai.app.cache.DiskImageCache;
import com.meishai.app.cache.LruImageCache;
import com.meishai.dao.Config;
import com.meishai.net.volley.RequestQueue;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.Volley;

public class GlobalContext extends Application {

    private static GlobalContext globalContext;


    //用于执行配置文件put的线程池
    private ExecutorService configPool = Executors.newFixedThreadPool(8);

    private Config mConfig;
    private RequestQueue mRequestQueue;
    private List<MsgEmojiModle> emojis;
    public Map<String, String> EMOJI_MAP = new HashMap<String, String>();
    private ImageLoader mImageLoader;


    public List<MsgEmojiModle> getEmojis() {
        return emojis;
    }

    public void setEmojis(List<MsgEmojiModle> emojis) {
        this.emojis = emojis;
        if (null != this.emojis && !this.emojis.isEmpty()) {
            for (MsgEmojiModle eModle : emojis) {
                EMOJI_MAP.put(eModle.getText(), eModle.getFace());
            }
        }
    }

    public ExecutorService getConfigPool() {
        return configPool;
    }

    public static GlobalContext getInstance() {
//		System.out.print("globalContext"+(globalContext == null ? "null" : "true"));
        return globalContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        globalContext = this;
        mConfig = new Config(this);
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext(), mConfig.getCrashLogDir());

    }

    public Config getConfig() {
        return mConfig;
    }

    public synchronized RequestQueue getRequestQueue() {
        if (null == mRequestQueue) {
            mRequestQueue = Volley.newRequestQueue(this);
        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(getRequestQueue(), LruImageCache.instance());
        }
        return mImageLoader;
    }

    /**
     * 获取json数据的缓存
     *
     * @return
     */
    public SharedPreferences getCacheConfig() {
        SharedPreferences sp = getSharedPreferences("cache_config", Activity.MODE_PRIVATE);

        return sp;
    }


}
