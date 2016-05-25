package com.meishai.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.text.TextUtils;

import com.meishai.GlobalContext;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.ReleaseData;
import com.meishai.entiy.SplashRespData;
import com.meishai.net.ReqData;
import com.meishai.net.RespData;
import com.meishai.net.volley.Response;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.ImageRequest;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.camera.CameraActivity1;
import com.meishai.ui.fragment.common.req.PublicReq;
import com.meishai.ui.fragment.usercenter.LoginActivity;
import com.meishai.util.AndroidUtil;
import com.meishai.util.CacheConfigUtils;
import com.meishai.util.DebugLog;
import com.meishai.util.DiskImageCacheUtil;
import com.meishai.util.GsonHelper;
import com.meishai.util.ImageUtil;
import com.meishai.util.StringUtils;
import com.meishai.util.UploadUtilsAsync;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoadSplashService extends Service {

    private UploadUtilsAsync uploadAsync;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        loadSplash();

        return super.onStartCommand(intent, flags, startId);
    }


    private void loadSplash() {
        //执行启动广告的请求,如果请求成功,会再去加载图片
        PublicReq.splashReq(GlobalContext.getInstance(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (TextUtils.isEmpty(response)) return;
                final SplashRespData respData = GsonHelper.parseObject(response, SplashRespData.class);
                if (respData == null) return;
                CacheConfigUtils.putCache("splash_data", response);

                if (respData.success == 1) {
                    DebugLog.w("广告图数据加载成功");
                    //加载图片
                    GlobalContext.getInstance().getRequestQueue().add(new ImageRequest(respData.data.image, new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(final Bitmap response) {
                            //图片加载成功,保存到指定位置
                            new Thread() {
                                @Override
                                public void run() {

                                    File cachePath = new File(DiskImageCacheUtil.getInstance().getCacheDir(ConstantSet.SPLASH_PATH), "splash_image.jpg");
                                    ImageUtil.saveBitMap2File(cachePath, response);
                                    DebugLog.w("广告图图片加载成功!");
                                    stopSelf();
                                }
                            }.start();

                        }
                    }, 0, 0, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //图片加载失败
                            DebugLog.w("广告图图片加载失败");
                            stopSelf();
                        }
                    }));
                }
            }
            //启动广告加载失败
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DebugLog.w("广告图数据加载失败");
                stopSelf();
            }
        });
    }

}
