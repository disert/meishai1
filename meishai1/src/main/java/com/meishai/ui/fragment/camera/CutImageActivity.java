package com.meishai.ui.fragment.camera;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.app.util.SPUtils;
import com.meishai.app.widget.layout.CropImageView;
import com.meishai.entiy.ReleaseData;
import com.meishai.ui.base.BaseActivity;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.util.ComUtils;
import com.meishai.util.ImageUtil;

/**
 * 剪切图片的activity
 *
 * @author Administrator yl
 */
public class CutImageActivity extends BaseActivity {

    private ImageButton mNext;
    private ImageButton mBack;
    private CropImageView mImage;

    private String mChoosePath;
    private int mode;
    private ReleaseData mReleaseData;

    /**
     * @param releaseData 已有的发布数据
     * @param currentPath 选中图片的路径
     * @param mode        添加图片的模式,是普通添加还是修改封面
     * @return
     */
    public static Intent newIntent(ReleaseData releaseData, String currentPath,
                                   int mode) {
        Intent intent = new Intent(GlobalContext.getInstance(),
                CutImageActivity.class);
        intent.putExtra("releaseData", releaseData);
        intent.putExtra("mode", mode);
        intent.putExtra("currentPath", currentPath);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.cut_image_activity);

        initView();
        initData();
    }

    private void initData() {

        mode = getIntent().getIntExtra("mode", CameraActivity1.ADD_IMAGE);
        mReleaseData = getIntent().getParcelableExtra("releaseData");
        mChoosePath = getIntent().getStringExtra("currentPath");

        try {
            //加载图片前需要对图片进行处理,否则图片有可能会出oom
            String targetPath = getCacheDir() + BitmapUtils.toRegularHashCode(mChoosePath) + ".jpg";
            if (mChoosePath == null) {
                return;
            }
            BitmapUtils.compressBitmap(mChoosePath, targetPath, 800);
            mChoosePath = targetPath;
            mImage.setImageBitmap(BitmapFactory
                    .decodeStream(new FileInputStream(mChoosePath)));

            mNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 返回裁剪的结果,进入编辑界面
                    Bitmap croppedBitmap = mImage.getCroppedBitmap();


                    String picN = ComUtils.getRandomAlphamericStr(8) + ".jpg";
                    File temp = new File(getCacheDir(), picN);
                    String cropImagePath = temp.getAbsolutePath();
                    File file = ImageUtil.saveBitMap2File(cropImagePath,
                            croppedBitmap);

                    //当是选择头像的时候,返回头像
                    if (mode == CameraActivity1.CHOOSE_IMAGE) {
                        Bundle bundle = new Bundle();
                        bundle.putString("data", file.getAbsolutePath());
                        Intent intent = new Intent();
                        intent.putExtras(bundle);
                        setResult(RESULT_OK, intent);
                        finish();
                        return;
                    }


                    if (mReleaseData == null) {
                        mReleaseData = new ReleaseData();
                    }
                    Intent intent = ImageEditorActivity.newIntent(mReleaseData,
                            file.getAbsolutePath(), mode);
                    startActivity(intent);
                    finish();
                }
            });

            mBack.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (mode == CameraActivity1.CHOOSE_IMAGE) {
                        finish();
                        return;
                    }
                    startActivity(ImageChooseActivity1.newIntent(mReleaseData,
                            mode));
                    finish();
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void initView() {
        mImage = (CropImageView) findViewById(R.id.cropImageView);
        mBack = (ImageButton) findViewById(R.id.cut_image_back);
        mNext = (ImageButton) findViewById(R.id.cut_image_next);

        mImage.setMinFrameSizeInDp(200);
        mImage.setOverlayColor(0xaaffffff);

        mImage.setFrameStrokeWeightInDp(1);
        mImage.setHandleShowMode(CropImageView.ShowMode.NOT_SHOW);
        mImage.setGuideShowMode(CropImageView.ShowMode.NOT_SHOW);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mode == CameraActivity1.CHOOSE_IMAGE) {
                finish();
                return true;
            }
            startActivity(ImageChooseActivity1.newIntent(mReleaseData, mode));
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
