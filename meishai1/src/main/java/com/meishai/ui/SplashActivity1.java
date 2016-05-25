package com.meishai.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.entiy.SplashData;
import com.meishai.entiy.SplashRespData;
import com.meishai.net.volley.Response;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.ImageRequest;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.common.req.PublicReq;
import com.meishai.util.CacheConfigUtils;
import com.meishai.util.DebugLog;
import com.meishai.util.DiskImageCacheUtil;
import com.meishai.util.GsonHelper;
import com.meishai.util.ImageUtil;
import com.meishai.util.SkipUtils;

import java.io.File;


/**
 * 启动加载页 - 广告
 *
 * @author sh
 */
public class SplashActivity1 extends Activity {

    private final int SPLASH_DISPLAY_LENGHT = 3000; // 延迟3秒
    private ImageView splash_image;
    private ImageView splash_logo;
    private TextView splash_next;
    private Bitmap bitmap;
    private SplashData splashData;

    private boolean isclicked = false;
    private int time = 3;
    private CountDownTimer timer;

    public static Intent newIntent(String splashPath, SplashData splashData) {
        Intent intent = new Intent(GlobalContext.getInstance(), SplashActivity1.class);
        intent.putExtra("splashData", splashData);
        intent.putExtra("splashPath", splashPath);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash);


        initView();


        //初始化动画
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.splash_in);
        animation.setDuration(SPLASH_DISPLAY_LENGHT);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                //动画开始
                timer = new CountDownTimer(3000, 1000) {
                    public void onTick(long millisUntilFinished) {
                        splash_next.setText(time-- + " 跳过");
                    }

                    public void onFinish() {
                        splash_next.setText(time-- + " 跳过");
                        Intent mainIntent = new Intent(SplashActivity1.this,
                                MainActivity.class);
                        SplashActivity1.this.startActivity(mainIntent);
                        SplashActivity1.this.finish();
                    }
                }.start();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (isclicked) {
                    return;
                }
                //动画结束

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                //动画重播
            }
        });


        findViewById(R.id.splash_root).setAnimation(animation);

    }

    private void initView() {
        //初始化view
        splash_image = (ImageView) this.findViewById(R.id.splash_image);
        splash_logo = (ImageView) this.findViewById(R.id.splash_logo);
        splash_next = (TextView) this.findViewById(R.id.splash_next);
        splash_next.setVisibility(View.VISIBLE);
        splash_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SplashActivity1.this, MainActivity.class));
                finish();
            }
        });

        //初始化数据
        File cachePath = new File(DiskImageCacheUtil.getInstance().getCacheDir(ConstantSet.SPLASH_PATH), "splash_image.jpg");
        if (cachePath.exists()) {
            String splashPath = cachePath.getAbsolutePath();
            bitmap = BitmapFactory.decodeFile(splashPath);
        }
        //获取启动图的数据
        SplashRespData data = GsonHelper.parseObject(CacheConfigUtils.getCache("splash_data"), SplashRespData.class);
        if (data != null) {//如果有,再判断返回值是否是0,
            if (data.success == 0) {//是0关闭自己,打开主页面
                startActivity(new Intent(SplashActivity1.this, MainActivity.class));
                finish();
            } else {//否则,记录启动图的数据
                splashData = data.data;
            }
        } else {//如果没有数据,直接打开主界面
            startActivity(new Intent(SplashActivity1.this, MainActivity.class));
            finish();
        }
        if (bitmap != null) {
            splash_logo.setVisibility(View.GONE);
            splash_image.setImageBitmap(bitmap);
            splash_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isclicked = true;
                    timer.cancel();
                    startActivity(MainActivity.newIntent(splashData));
                    finish();

                }
            });
        } else {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }


    }

}
