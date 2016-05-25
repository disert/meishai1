package com.emoji;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;
import android.view.View;

import com.meishai.GlobalContext;
import com.meishai.net.volley.Response;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.ImageRequest;

public class UrlImageSpan extends ImageSpan {
    private URLDrawable mUrlDrawable;

    public UrlImageSpan(View content, String url) {
        super(new URLDrawable(content, GlobalContext.getInstance()));
        mUrlDrawable = (URLDrawable) getDrawable();
        fetchImage(url);
    }

    private void fetchImage(String url) {
        ImageRequest imageRequest = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        mUrlDrawable.refreshDrawable(new BitmapDrawable(
                                response));
                    }
                }, 0, 0, Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        GlobalContext.getInstance().getRequestQueue().add(imageRequest);

    }

    public UrlImageSpan(Drawable d) {
        super(d);
    }
}