package com.meishai.ui.fragment.camera;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.app.widget.CustomProgress;
import com.meishai.entiy.ReleaseData;
import com.meishai.ui.base.BaseActivity;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.camera.adapter.AlbumAdapter1;
import com.meishai.util.AndroidUtil;

public class ImageChooseActivity1 extends BaseActivity {
    private CustomProgress mProgressDialog;

    /**
     * 所有的图片
     */
    private List<String> mImgs;

    private Button backMain;
    private GridView mGirdView;
    private AlbumAdapter1 mAdapter;

    int totalCount = 0;

    // private ArrayList<String> mPaths;
    // private IDs ids = new IDs();

    private int mode;
    private ReleaseData mReleaseData;

    private int mScreenHeight;


    /**
     * 获得用于启动该activity的intent
     *
     * @param releaseData 已有的发布数据
     * @param mode        添加图片的模式,是普通添加还是修改封面
     * @return
     */
    public static Intent newIntent(ReleaseData releaseData, int mode) {
        Intent intent = new Intent(GlobalContext.getInstance(),
                ImageChooseActivity1.class);
        intent.putExtra("releaseData", releaseData);
        intent.putExtra("mode", mode);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_choose1);
        // 获取屏幕的高度
        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        mScreenHeight = outMetrics.heightPixels;

        initView();
        getImages();
        initEvent();

    }

    /**
     * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中 完成图片的扫描，最终获得jpg最多的那个文件夹
     */
    private void getImages() {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            AndroidUtil.showToast("暂无外部存储");
            return;
        }
        // 显示进度条
        mProgressDialog = CustomProgress.show(this, "正在加载...", true, null);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = ImageChooseActivity1.this
                        .getContentResolver();
                // 只查询jpeg和png的图片
                Cursor mCursor = mContentResolver.query(imageUri, null,
                        MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[]{"image/jpeg", "image/png"},
                        MediaStore.Images.Media.DATE_MODIFIED);
                while (mCursor.moveToNext()) {
                    // 获取图片的路径
                    String path = mCursor.getString(mCursor
                            .getColumnIndex(MediaStore.Images.Media.DATA));
                    if (mImgs == null) {
                        mImgs = new ArrayList<String>();
                    }
                    // 把过小的文件给干掉
                    ExifInterface exif = null;
                    try {
                        exif = new ExifInterface(path);
                        int bitWidth = exif.getAttributeInt(
                                ExifInterface.TAG_IMAGE_WIDTH, 0);
                        int bitHeight = exif.getAttributeInt(
                                ExifInterface.TAG_IMAGE_LENGTH, 0);
                        if (bitHeight < 200 || bitWidth < 200) {
                            continue;
                        }
                        mImgs.add(path);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (mImgs == null) {
                    mCursor.close();
                    mHandler.sendEmptyMessage(0x110);
                    return;
                }
                // 按照图片的修改日期排序
                Collections.sort(mImgs, new Comparator<String>() {

                    @Override
                    public int compare(String lhs, String rhs) {
                        File file1 = new File(lhs);
                        File file2 = new File(rhs);
                        if (file1.lastModified() < file2.lastModified()) {
                            return 1;// 最后修改的照片在前
                        } else {
                            return -1;
                        }
                    }
                });

                mCursor.close();

                // 通知Handler扫描图片完成
                mHandler.sendEmptyMessage(0x110);
            }
        }).start();
    }

    /**
     * 初始化View
     */
    private void initView() {
        backMain = (Button) findViewById(R.id.backMain);
        mGirdView = (GridView) findViewById(R.id.id_gridView);

        mAdapter = new AlbumAdapter1(this);
        mGirdView.setAdapter(mAdapter);
    }

    private void initEvent() {
        backMain.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mReleaseData == null || mReleaseData.getPaths() == null
                        || mReleaseData.getPaths().size() == 0) {//说明是第一次选择图片
                    finish();
                } else {//说明是添加图片
                    startActivity(CameraActivity1.newIntent(mReleaseData));
                    finish();
                }
            }
        });
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            mProgressDialog.dismiss();
            // 为View绑定数据
            data2View();
            // 初始化展示文件夹的popupWindw
        }
    };

    /**
     * 为View绑定数据
     */
    private void data2View() {
        if (mImgs == null) {
            AndroidUtil.showToast("一张图片没扫描到");
            return;
        }

        mAdapter.setData(mImgs);
        mAdapter.setReleaseDate(mReleaseData);
    }

    ;

    @Override
    protected void onResume() {
        mode = getIntent().getIntExtra("mode", CameraActivity1.ADD_IMAGE);
        mReleaseData = getIntent().getParcelableExtra("releaseData");
        mAdapter.setReleaseDate(mReleaseData);
        ;
        mAdapter.setMode(mode);
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == ConstantSet.SELECT_PIC_BY_TACK_PHOTO) {// 拍照返回结果
            if (mAdapter.getPhotoUri() == null) {

                return;
            }
            // 选择图片的返回结果,进入下一步
            String path = AndroidUtil.imageUri2Path(mAdapter.getPhotoUri());
            AndroidUtil.showToast("返回路径:" + path);
            if (TextUtils.isEmpty(path)) {
                return;
            }
            File file = new File(path);
            if (!file.exists()) {
                return;
            }

            Intent intent = CutImageActivity.newIntent(mReleaseData, path, mode);
//			Intent intent = ClipPictureActivity.newIntent(mReleaseData, path,  mode);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mReleaseData == null || mReleaseData.getPaths() == null
                    || mReleaseData.getPaths().size() == 0) {
                finish();
            } else {
                startActivity(CameraActivity1.newIntent(mReleaseData));
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
