package com.meishai.ui.fragment.camera;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.lib.photoview.AnimationRect;
import com.meishai.lib.photoview.PhotoView;
import com.meishai.lib.photoview.PhotoViewAttacher;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.ui.base.BaseActivity;

/**
 * 文件名：PreviewActivity
 * 描    述：浏览图片的activity
 * 作    者：yl
 * 时    间：2016/1/7
 * 版    权：
 */
public class PreviewActivity extends BaseActivity {

    private PhotoView photoView;

    public static Intent newIntent(String url) {
        Intent intent = new Intent(GlobalContext.getInstance(), PreviewActivity.class);
        intent.putExtra("url", url);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.gallery_general_layout);
        findViewById(R.id.root).setBackgroundColor(0xee000000);

        photoView = (PhotoView) findViewById(R.id.animation);

        // allowClickToCloseGallery
        photoView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                finish();
            }
        });


        final String url = getIntent().getStringExtra("url");
        if (url.startsWith("http")) {
            ImageLoader.ImageListener listener = ImageLoader.getImageListener(photoView, 0, 0);
            getImageLoader().get(url, listener);
        } else {
            photoView.setImageBitmap(BitmapFactory.decodeFile(url));
        }

    }
}
