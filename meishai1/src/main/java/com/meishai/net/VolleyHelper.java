package com.meishai.net;

import android.content.Context;

import com.meishai.GlobalContext;
import com.meishai.app.cache.DiskImageCache;
import com.meishai.app.cache.LruImageCache;
import com.meishai.net.volley.RequestQueue;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.Volley;

public class VolleyHelper {

    public static RequestQueue getRequestQueue(Context context) {
        return GlobalContext.getInstance().getRequestQueue();
    }

    public static ImageLoader getImageLoader(Context context) {
        return GlobalContext.getInstance().getImageLoader();
    }
}
