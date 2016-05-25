package com.meishai.ui.base;

import android.text.TextUtils;

import com.meishai.GlobalContext;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.util.GsonHelper;

/**
 * 当网络请求出错,调用本地缓存的错误监听器
 *
 * @author Administrator
 */
public class CacheErrorListener implements ErrorListener {

    private String url;
    private Listener<String> listener;
    private ErrorListener errorListener;

    /**
     * @param url
     */
    public CacheErrorListener(String url, Listener<String> listener,
                              ErrorListener errorListener) {
        this.url = url;
        this.listener = listener;
        this.errorListener = errorListener;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        String cache = GlobalContext.getInstance().getRequestQueue().getCache()
                .get(url).data.toString();
        if (TextUtils.isEmpty(cache) && errorListener != null) {// 没有缓存
            errorListener.onErrorResponse(error);
        } else {// 有缓存
            listener.onResponse(cache);
        }
    }

}
