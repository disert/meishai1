package com.meishai.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.entiy.SplashData;
import com.meishai.entiy.SplashRespData;
import com.meishai.net.volley.Response;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.ImageRequest;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.camera.BitmapUtils;
import com.meishai.ui.fragment.common.req.PublicReq;
import com.meishai.util.CacheConfigUtils;
import com.meishai.util.DebugLog;
import com.meishai.util.DiskImageCacheUtil;
import com.meishai.util.GsonHelper;
import com.meishai.util.ImageUtil;

import java.io.File;


/**
 * 启动加载页1
 *
 * @author sh
 */
public class SplashActivity extends Activity {

    private final int SPLASH_DISPLAY_LENGHT = 1500; // 延迟3秒


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash1);


        Animation animation = AnimationUtils.loadAnimation(this, R.anim.splash_in);
        animation.setDuration(SPLASH_DISPLAY_LENGHT);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                //动画开始


            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //动画结束
//				File cachePath = new File(DiskImageCacheUtil.getInstance().getCacheDir(ConstantSet.SPLASH_PATH), "splash_image.jpg");
                String splashData = CacheConfigUtils.getCache("splash_data");
                SplashRespData data = GsonHelper.parseObject(splashData, SplashRespData.class);
                if (data != null && data.success == 1) {
                    //有广告图
                    startActivity(new Intent(SplashActivity.this, SplashActivity1.class));
                    finish();
                } else {
                    //没广告图
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                //动画重播
            }
        });
        findViewById(R.id.splash_root).setAnimation(animation);

    }


}
