package com.meishai.ui.fragment.camera;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.app.widget.CustomProgress;
import com.meishai.entiy.ReleaseData;
import com.meishai.ui.base.BaseActivity;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.camera.adapter.ChooseImageAdapter;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DataCleanManager;
import com.meishai.util.DebugLog;
import com.meishai.util.DiskImageCacheUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 文件名：ChooseImageActivity
 * 描    述：选择多张图片的图片选择界面
 * 作    者：yl
 * 时    间：2015/12/22
 * 版    权：
 */
public class ChooseImageActivity extends BaseActivity {

    private CustomProgress mProgressDialog;
    /**
     * 所有的图片
     */
    private List<String> mImgs;

    private Button mBack;
    private Button mMore;
    private TextView mTitle;
    private GridView mGirdView;
    private ChooseImageAdapter mAdapter;
    private ReleaseData mReleaseData;
    public int maxSize;//最大可以选择的图片数量
    private int mode;//普通选择还是等待结果
    private TextView mSelectedCount;
    private TextView mMaxCount;
    private Button mNext;

    /**
     * 获得用于启动该activity的intent
     *
     * @param releaseData 已有的发布数据
     * @param maxSize     最大选择的图片数量
     * @return
     */
    public static Intent newIntent(ReleaseData releaseData, int maxSize) {
        Intent intent = new Intent(GlobalContext.getInstance(),
                ChooseImageActivity.class);
        intent.putExtra("releaseData", releaseData);
        intent.putExtra("maxSize", maxSize);
        intent.putExtra("mode", 0);

        return intent;
    }

    /**
     * 获得用于启动该activity的intent
     *
     * @param releaseData 已有的发布数据
     * @param maxSize     最大选择的图片数量
     * @param mode        0是默认值,表示普通选择,等待返回结果ConstantSet.RETURN_MODE 1
     * @return
     */
    public static Intent newIntent(ReleaseData releaseData, int maxSize, int mode) {
        Intent intent = new Intent(GlobalContext.getInstance(),
                ChooseImageActivity.class);
        intent.putExtra("releaseData", releaseData);
        intent.putExtra("maxSize", maxSize);
        intent.putExtra("mode", mode);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_image);
        DataCleanManager.cleanCustomCache(DiskImageCacheUtil.getInstance().getCacheDir(ConstantSet.EDIT_PATH));//清理编辑图片所留下的缓存
        // 获取屏幕的高度
        //        DisplayMetrics outMetrics = new DisplayMetrics();
        //        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        //        mScreenHeight = outMetrics.heightPixels;


        initView();
        getImages();
        initEvent();

    }


    /**
     * 初始化View
     */
    private void initView() {
        maxSize = getIntent().getIntExtra("maxSize", 1);
        mode = getIntent().getIntExtra("mode", 0);
        mReleaseData = getIntent().getParcelableExtra("releaseData");
        mImgs = new ArrayList<String>();

        mBack = (Button) findViewById(R.id.back);
        mBack.setText("");
        mTitle = (TextView) findViewById(R.id.title);
        mTitle.setText("选择图片");
        mMore = (Button) findViewById(R.id.more_button);
        mMore.setText("取消");
        mMore.setVisibility(View.VISIBLE);
        //底部
        mSelectedCount = (TextView) findViewById(R.id.selected_img_count);
        mMaxCount = (TextView) findViewById(R.id.max_img_count);
        mMaxCount.setText(maxSize + "");
        mNext = (Button) findViewById(R.id.next);

        mGirdView = (GridView) findViewById(R.id.id_gridView);
        mAdapter = new ChooseImageAdapter(this, maxSize);
        mAdapter.setSelectedListener(new ChooseImageAdapter.OnSelectedLisntenner() {
            @Override
            public void onSelected(String path) {
                //设置确定按钮的状态
                List<String> images = mAdapter.getSelectedImage();
                if (images != null && !images.isEmpty()) {
                    mSelectedCount.setText(images.size() + "");
                    mNext.setClickable(true);
                } else {
                    mSelectedCount.setText("0");
                    mNext.setClickable(false);
                }
            }
        });
        mGirdView.setAdapter(mAdapter);

    }

    private void initEvent() {
        mBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mMore.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击之后 获得选中的图片,跳转到图片编辑页面
                ArrayList<String> images = mAdapter.getSelectedImage();
                if (images != null && !images.isEmpty()) {
                    //跳转到图片编辑页面

                    if (mode == ConstantSet.COMMEND_MODE) {
                        mReleaseData.setPaths(images);
                        startActivity(EditorImageActivity.newIntent(mReleaseData, images));
                        finish();
                    } else if (mode == ConstantSet.RETURN_MODE) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("paths", images);// 给 bundle 写入数据
                        Intent intent = new Intent();
                        intent.putExtras(bundle);
                        setResult(RESULT_OK, intent);
                        finish();
                    } else if (mode == ConstantSet.ADD_MODE) {
                        ArrayList<String> paths = mReleaseData.getPaths();
                        images.addAll(paths);
                        startActivity(EditorImageActivity.newIntent(mReleaseData, images));
                        finish();

                    }
                } else {
                    //请选择图片
                    AndroidUtil.showToast("请选择图片");
                }
            }
        });
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (mProgressDialog.isShowing())
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
    }

    /**
     * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中 完成图片的扫描
     */
    private void getImages() {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            AndroidUtil.showToast("暂无外部存储");
            return;
        }
        // 显示进度条
        mProgressDialog = CustomProgress.show(this, "正在加载...", true, null);
        //先扫描一遍SD卡
//        sendBroadcast(new Intent(
//                Intent.ACTION_MEDIA_MOUNTED,
//                Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        MediaScannerConnection.scanFile(this,
                new String[]{
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath()
                        , Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()
                        , Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getPath()
                        , Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getPath()
                        , Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath()}, null, null);
        //Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() + "/" + fileName
        DebugLog.w("扫描的目录:" + path);
        new Thread(new Runnable() {
            @Override
            public void run() {

                //扫描外部存储设备的图片
                scanImage(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //扫描内部存储的图片
//                scanImage(MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                //扫描指定目录下的图片
//                scanImage(Environment.getExternalStorageDirectory(),mImgs);
//                scanImage(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),mImgs);

                // 按照图片的修改日期排序
                if (mImgs != null && !mImgs.isEmpty()) {
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
                }


                //去重复,去除releasedata当中有的图片的路径
                if (mReleaseData != null && mReleaseData.getPaths() != null && !mReleaseData.getPaths().isEmpty()) {
                    for (String selectPath : mReleaseData.getPaths()) {
                        if (!TextUtils.isEmpty(selectPath)) {
                            mImgs.remove(selectPath);
                        }
                    }
                }

                // 通知Handler扫描图片完成
                mHandler.sendEmptyMessage(0x110);
            }
        }).start();
    }

    private void scanImage(Uri imageUri) {
        ContentResolver mContentResolver = ChooseImageActivity.this
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
                if (bitHeight < 100 || bitWidth < 100) {
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
        mCursor.close();
    }

    private void scanImage(File dir, List<String> imgs) {
//        DebugLog.e("当前扫描位置:"+dir.getAbsolutePath());

        if (!dir.canRead() || !dir.exists()) return;


        if (dir.isDirectory()) {
//            忽略的目录
            if (dir.getName().equalsIgnoreCase("meishai")
                    || dir.getName().equalsIgnoreCase("system")
                    || dir.getName().equalsIgnoreCase("Andriod")) {
                return;
            }
            File files[] = dir.listFiles();
            for (int i = 0; i < files.length; i++) {
                scanImage(files[i], imgs);
            }

        } else if (dir.isFile()) {
            String path = dir.getAbsolutePath();
            if (path.endsWith(".jpg") || path.endsWith(".png")) {

                // 把过小的文件给干掉
                ExifInterface exif = null;
                try {
                    exif = new ExifInterface(path);
                    int bitWidth = exif.getAttributeInt(
                            ExifInterface.TAG_IMAGE_WIDTH, 0);
                    int bitHeight = exif.getAttributeInt(
                            ExifInterface.TAG_IMAGE_LENGTH, 0);
                    if (bitHeight > 100 && bitWidth > 100) {
                        imgs.add(path);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == ConstantSet.SELECT_PIC_BY_TACK_PHOTO && resultCode == RESULT_OK) {// 拍照返回结果
            if (mAdapter.getPhotoUri() == null) {
                return;
            }
            // 选择图片的返回结果,进入下一步
            String path = AndroidUtil.imageUri2Path(mAdapter.getPhotoUri());
            DebugLog.d("返回路径:" + path);
            if (TextUtils.isEmpty(path)) {
                return;
            }
            File file = new File(path);
            if (!file.exists()) {
                return;
            }
            ArrayList<String> paths = mAdapter.getSelectedImage();
            if (paths != null && !paths.isEmpty()) {
                paths.add(path);
            } else {
                paths = new ArrayList<String>();
                paths.add(path);
            }
            Intent intent = EditorImageActivity.newIntent(mReleaseData, paths);
            //			Intent intent = ClipPictureActivity.newIntent(mReleaseData, path,  mode);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}


