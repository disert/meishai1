package com.album.imageloader;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.album.bean.ImageFloder;
import com.album.imageloader.ListImageDirPopupWindow.OnImageDirSelected;
import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.app.widget.CustomProgress;
import com.meishai.ui.base.BaseActivity;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.util.AndroidUtil;

public class ImageChooseActivity extends BaseActivity implements
        OnImageDirSelected {
    private CustomProgress mProgressDialog;
    /**
     * 存储文件夹中的图片数量
     */
    private int mPicsSize;

    /**
     * 存储最新修改时间
     */
    private long mModTime;

    /**
     * 图片数量最多的文件夹
     */
    private File mImgDir;
    /**
     * 所有的图片
     */
    private List<String> mImgs;
    private Button backMain;
    private Button btnFinish;
    private GridView mGirdView;
    private AlbumAdapter mAdapter;
    /**
     * 临时的辅助类，用于防止同一个文件夹的多次扫描
     */
    private HashSet<String> mDirPaths = new HashSet<String>();

    /**
     * 扫描拿到所有的图片文件夹
     */
    private List<ImageFloder> mImageFloders = new ArrayList<ImageFloder>();

    private RelativeLayout mBottomLy;

    private TextView mChooseDir;
    private TextView mImageCount;
    int totalCount = 0;

    private int mScreenHeight;

    private ListImageDirPopupWindow mListImageDirPopupWindow;
    // 可以选取图片的数量
    private int count = 1;
    // 广播动作
    private String actionName = ConstantSet.ACTION_NAME;

    /**
     * 可以选取图片的数量
     *
     * @param count
     * @return
     */
    public static Intent newIntent(String actionName, int count) {
        AlbumAdapter.mSelectedImage.clear();
        Intent intent = new Intent(GlobalContext.getInstance(),
                ImageChooseActivity.class);
        intent.putExtra("count", count);
        intent.putExtra("actionName", actionName);
        return intent;
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            mProgressDialog.dismiss();
            // 为View绑定数据
            data2View();
            // 初始化展示文件夹的popupWindw
            initListDirPopupWindw();
        }
    };

    /**
     * 为View绑定数据
     */
    private void data2View() {
        if (mImgDir == null) {
            AndroidUtil.showToast("一张图片没扫描到");
            return;
        }

        mImgs = Arrays.asList(mImgDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                if (filename.endsWith(".jpg")
                        || filename.endsWith(".png")
                        || filename.endsWith(".jpeg"))
                    return true;
                return false;
            }
        }));
        /**
         * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
         */
        mAdapter = new AlbumAdapter(getApplicationContext(), mImgs,
                R.layout.grid_item, mImgDir.getAbsolutePath());
        mAdapter.setMaxSelected(count);
        mAdapter.setClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                int selectedCount = AlbumAdapter.mSelectedImage.size();
                updateUI(selectedCount);
            }
        });
        mGirdView.setAdapter(mAdapter);
        mImageCount.setText(totalCount + "张");
    }

    ;

    /**
     * 初始化展示文件夹的popupWindw
     */
    private void initListDirPopupWindw() {
        mListImageDirPopupWindow = new ListImageDirPopupWindow(
                LayoutParams.MATCH_PARENT, (int) (mScreenHeight * 0.7),
                mImageFloders, LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.list_dir, null));
        mListImageDirPopupWindow.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                // 设置背景颜色变暗
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);
            }
        });
        // 设置选择文件夹的回调
        mListImageDirPopupWindow.setOnImageDirSelected(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_choose);
        Bundle bundle = getIntent().getExtras();
        try {
            if (null != bundle) {
                count = bundle.getInt("count");
                actionName = bundle.getString("actionName");
            }
        } catch (Exception e) {
            count = 1;
        }

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
                String firstImage = null;
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = ImageChooseActivity.this
                        .getContentResolver();
                // 只查询jpeg和png的图片
                Cursor mCursor = mContentResolver.query(mImageUri, null,
                        MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[]{"image/jpeg", "image/png"},
                        MediaStore.Images.Media.DATE_MODIFIED);
                while (mCursor.moveToNext()) {
                    // 获取图片的路径
                    String path = mCursor.getString(mCursor
                            .getColumnIndex(MediaStore.Images.Media.DATA));
                    Log.e("TAG", path);
                    // 拿到第一张图片的路径
                    if (firstImage == null)
                        firstImage = path;
                    // 获取该图片的父路径名
                    File parentFile = new File(path).getParentFile();
                    if (parentFile == null)
                        continue;
                    String dirPath = parentFile.getAbsolutePath();
                    ImageFloder imageFloder = null;
                    // 利用一个HashSet防止多次扫描同一个文件夹（不加这个判断，图片多起来还是相当恐怖的~~）
                    if (mDirPaths.contains(dirPath)) {
                        continue;
                    } else {
                        mDirPaths.add(dirPath);
                        // 初始化imageFloder
                        imageFloder = new ImageFloder();
                        imageFloder.setDir(dirPath);
                        imageFloder.setFirstImagePath(path);
                    }
                    if (parentFile.list() == null) continue;
                    int picSize = parentFile.list(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String filename) {
                            if (filename.endsWith(".jpg")
                                    || filename.endsWith(".png")
                                    || filename.endsWith(".jpeg"))
                                return true;
                            return false;
                        }
                    }).length;
                    totalCount += picSize;


                    imageFloder.setCount(picSize);
                    mImageFloders.add(imageFloder);

//					if (picSize > mPicsSize) {
//						mPicsSize = picSize;
//						mImgDir = parentFile;
//					}
                    // 修改时间
                    long modify = parentFile.lastModified();
                    if (modify > mModTime) {
                        mPicsSize = picSize;
                        mModTime = modify;
                        mImgDir = parentFile;
                    }
                }
                mCursor.close();
                // 扫描完成，辅助的HashSet也就可以释放内存了
                mDirPaths = null;
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
        btnFinish = (Button) findViewById(R.id.btn_finish);
        mGirdView = (GridView) findViewById(R.id.id_gridView);
        mChooseDir = (TextView) findViewById(R.id.id_choose_dir);
        mImageCount = (TextView) findViewById(R.id.id_total_count);
        mBottomLy = (RelativeLayout) findViewById(R.id.id_bottom_ly);
    }

    private void initEvent() {
        /**
         * 为底部的布局设置点击事件，弹出popupWindow
         */
        mBottomLy.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mListImageDirPopupWindow
                        .setAnimationStyle(R.style.anim_popup_dir);
                mListImageDirPopupWindow.showAsDropDown(mBottomLy, 0, 0);

                // 设置背景颜色变暗
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = .3f;
                getWindow().setAttributes(lp);
            }
        });
        backMain.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void selected(ImageFloder floder) {

        mImgDir = new File(floder.getDir());
        mImgs = Arrays.asList(mImgDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                if (filename.endsWith(".jpg") || filename.endsWith(".png")
                        || filename.endsWith(".jpeg"))
                    return true;
                return false;
            }
        }));
        /**
         * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
         */
        mAdapter = new AlbumAdapter(getApplicationContext(), mImgs,
                R.layout.grid_item, mImgDir.getAbsolutePath());
        mAdapter.setMaxSelected(count);
        mAdapter.setClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                int selectedCount = AlbumAdapter.mSelectedImage.size();
                updateUI(selectedCount);
            }
        });
        mGirdView.setAdapter(mAdapter);
        // mAdapter.notifyDataSetChanged();
        mImageCount.setText(floder.getCount() + "张");
        mChooseDir.setText(floder.getName());
        mListImageDirPopupWindow.dismiss();
    }

    private void updateUI(int selectedCount) {
        if (selectedCount > 0) {
            btnFinish.setTextColor(getResources().getColor(R.color.txt_save));
            String hasChoose = getString(R.string.btn_has_choose);
            btnFinish.setText(String.format(hasChoose, selectedCount, count));
            btnFinish.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent mIntent = new Intent(actionName);
                    ArrayList<String> list = new ArrayList<String>();
                    list.addAll(AlbumAdapter.mSelectedImage);
                    mIntent.putStringArrayListExtra(ConstantSet.CHOOSE_DATA,
                            list);
                    // 发送广播
                    sendBroadcast(mIntent);
                    finish();
                }
            });
        } else {
            btnFinish.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                }
            });
            btnFinish.setTextColor(getResources().getColor(R.color.txt_color));
            btnFinish.setText(getString(R.string.btn_no_choose));
        }
    }
}
