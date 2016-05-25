package com.meishai.ui.fragment.camera;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import jp.co.cyberagent.android.gpuimage.GPUImageView;

import org.insta.IF1977Filter;
import org.insta.IFAmaroFilter;
import org.insta.IFBrannanFilter;
import org.insta.IFEarlybirdFilter;
import org.insta.IFHefeFilter;
import org.insta.IFHudsonFilter;
import org.insta.IFInkwellFilter;
import org.insta.IFLomofiFilter;
import org.insta.IFLordKelvinFilter;
import org.insta.IFNashvilleFilter;
import org.insta.IFNormalFilter;
import org.insta.IFRiseFilter;
import org.insta.IFSierraFilter;
import org.insta.IFSutroFilter;
import org.insta.IFToasterFilter;
import org.insta.IFValenciaFilter;
import org.insta.IFWaldenFilter;
import org.insta.IFXproIIFilter;
import org.insta.InstaFilter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.app.widget.CustomProgress;
import com.meishai.app.widget.layout.SingleTouchView;
import com.meishai.entiy.ReleaseData;
import com.meishai.ui.base.BaseFragmentActivity;
import com.meishai.ui.fragment.camera.FilterFragment1.OnFilterSelectedListener;
import com.meishai.ui.fragment.camera.TagsFragment.StickerListener;
import com.meishai.util.AndroidUtil;
import com.meishai.util.ComUtils;

/**
 * 编辑图片的activity,进行滤镜和贴纸的设置
 *
 * @author Administrator yl
 */
public class ImageEditorActivity extends BaseFragmentActivity implements
        OnClickListener {

    private final String TAG_LABEL = "label";
    private final String TAG_FILTER = "filter";
    private final String TAG_TAGS = "tags";

    private static final int ID_LABEL = 2;
    private static final int ID_FILTER = 1;
    private static final int ID_TAGS = 0;

    private TagsFragment mTagsFragment = new TagsFragment();// 贴纸
    private LabelFragment mLabelFragment = new LabelFragment();// 标签
    private FilterFragment1 mFilterFragment = new FilterFragment1();// 滤镜

    private GPUImageView mImage;
    private ImageButton mConfirm;
    private ImageButton mBack;
    // 滤镜
    private TextView mFilter;
    private ImageView mFilterArrow;
    // 标签
    private TextView mLabel;
    private ImageView mLabelArrow;
    // 贴纸
    private TextView mTags;
    private ImageView mTagsArrow;

    private Bitmap mBitmap;// 编辑前的图片
    private CustomProgress mProgressDialog;
    private FrameLayout mImageRoot;
    private SingleTouchView mSticker;
    // 是否是重新修改图片
    private boolean isMod;

    // private ArrayList<String> mPaths;
    // private IDs ids = new IDs();
    private int mode;
    private String operSource;
    private ReleaseData mReleaseData;
    private ImageView mImageResult;

    /**
     * 编辑图片所使用的intent,发布页面会等待这边的返回结果
     *
     * @param currentPic
     * @return
     */
    public static Intent newIntent(String currentPic, String operSource) {
        Intent intent = new Intent(GlobalContext.getInstance(),
                ImageEditorActivity.class);
        intent.putExtra("currentPic", currentPic);
        intent.putExtra("operSource", operSource);
        return intent;
    }

    /**
     * @param releaseData 已有的发布数据
     * @param currentPic  选中图片的地址
     * @param mode        添加图片的模式,普通添加或修改封面
     * @return
     */

    public static Intent newIntent(ReleaseData releaseData, String currentPic,
                                   int mode) {
        Intent intent = new Intent(GlobalContext.getInstance(),
                ImageEditorActivity.class);
        intent.putExtra("releaseData", releaseData);
        intent.putExtra("mode", mode);
        intent.putExtra("currentPic", currentPic);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.image_editor_activity);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        mSticker = (SingleTouchView) findViewById(R.id.image_edit_sticker);
        mImageRoot = (FrameLayout) findViewById(R.id.image_edit_fl);

        mImage = (GPUImageView) findViewById(R.id.image_edit_image);
        mImageResult = (ImageView) findViewById(R.id.image_edit_image_result);
        mConfirm = (ImageButton) findViewById(R.id.image_edit_confirm);
        mBack = (ImageButton) findViewById(R.id.image_edit_back);
        mFilter = (TextView) findViewById(R.id.image_edit_filter);
        mFilterArrow = (ImageView) findViewById(R.id.image_edit_filter_arrow);
        mLabel = (TextView) findViewById(R.id.image_edit_label);
        mLabelArrow = (ImageView) findViewById(R.id.image_edit_label_arrow);
        mTags = (TextView) findViewById(R.id.image_edit_tags);
        mTagsArrow = (ImageView) findViewById(R.id.image_edit_tags_arrow);

    }

    private void initData() {

        operSource = getIntent().getStringExtra("operSource");
        mode = getIntent().getIntExtra("mode", CameraActivity1.ADD_IMAGE);
        mReleaseData = getIntent().getParcelableExtra("releaseData");
        String path = getIntent().getStringExtra("currentPic");
        if (mReleaseData == null) {
            // 如果没有传递发布数据过来,表明是对编辑过的图片进行再次编辑
            mReleaseData = new ReleaseData();
            isMod = true;
        } else {
            isMod = false;
        }
        mFilterFragment.setPath(path);
        try {
            Options options = new Options();
            mBitmap = BitmapFactory.decodeStream(new FileInputStream(path),
                    null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (mBitmap != null) {
            mImage.setImage(mBitmap);
        }

        mSticker.setEditable(false);
        mSticker.setVisibility(View.GONE);
        showTag();

    }

    private void initListener() {
        mLabel.setOnClickListener(this);
        mTags.setOnClickListener(this);
        mFilter.setOnClickListener(this);
        mBack.setOnClickListener(this);
        mConfirm.setOnClickListener(this);
        // 为贴纸fragment设置贴纸选中监听器
        mTagsFragment.setStickerListener(new StickerListener() {

            @Override
            public void controlBitmap(Bitmap bmp, int tid) {

                mReleaseData.setPtid(tid);
                // 对贴纸进行操作
                mSticker.setVisibility(View.VISIBLE);
                mSticker.setEditable(true);
                mSticker.setImageBitmap(bmp);
                // mImage.setShowSticker(true);

            }
        });
        // 为滤镜设置滤镜选择监听器
        mFilterFragment.setFilterListener(new OnFilterSelectedListener() {

            @Override
            public void SelectedFilter(int index) {
                // TODO 滤镜
                // if (info.filter == null) {
                // mImage.setImageBitmap(mBitmap);
                // } else {
                // new processImageTask(info.filter).execute();
                // }
                // mImage.setImage(mBitmap);
                InstaFilter filter = getFilter(index, getApplicationContext());
                mImage.setFilter(filter);

            }
        });

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {// 当页面加载完毕
            int width = mImage.getWidth();
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                    width, width);
            mImage.setLayoutParams(layoutParams);
            mImageResult.setLayoutParams(layoutParams);
            mSticker.setLayoutParams(layoutParams);
            mImageRoot.setLayoutParams(layoutParams);
        } else {
            super.onWindowFocusChanged(hasFocus);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_edit_back:// 返回
                if (!isMod) {// 如果是修改图片{
                    startActivity(ImageChooseActivity1
                            .newIntent(mReleaseData, mode));
                }
                finish();
                break;
            case R.id.image_edit_confirm:// 编辑完成
                confirm();
                break;
            case R.id.image_edit_filter:// 滤镜
                showFilter();
                break;
            case R.id.image_edit_label:// 标签
                showlabel();
                break;
            case R.id.image_edit_tags:// 贴纸
                showTag();
                break;
            default:
                break;
        }
    }

    /**
     * 贴纸
     */
    private void showTag() {
        mTagsArrow.setVisibility(View.VISIBLE);
        mFilterArrow.setVisibility(View.GONE);
        mLabelArrow.setVisibility(View.GONE);

        mTags.setSelected(true);
        mFilter.setSelected(false);
        mLabel.setSelected(false);

        mSticker.setEditable(true);
        switchFragment(ID_TAGS);
    }

    /**
     * 标签
     */
    private void showlabel() {
        mTagsArrow.setVisibility(View.GONE);
        mFilterArrow.setVisibility(View.GONE);
        mLabelArrow.setVisibility(View.VISIBLE);

        mTags.setSelected(false);
        mFilter.setSelected(false);
        mLabel.setSelected(true);

        mSticker.setEditable(false);

        switchFragment(ID_LABEL);
    }

    /**
     * 滤镜
     */
    private void showFilter() {
        mTagsArrow.setVisibility(View.GONE);
        mFilterArrow.setVisibility(View.VISIBLE);
        mLabelArrow.setVisibility(View.GONE);

        mTags.setSelected(false);
        mFilter.setSelected(true);
        mLabel.setSelected(false);

        mSticker.setEditable(false);

        switchFragment(ID_FILTER);
    }

    private void switchFragment(int tag) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        Fragment frg;
        switch (tag) {
            case ID_TAGS:
                frg = getSupportFragmentManager().findFragmentByTag(TAG_TAGS);
                if (null == frg) {
                    transaction.add(R.id.image_edit_frame, mTagsFragment, TAG_TAGS);
                } else {
                    transaction.show(frg);
                }
                frg = getSupportFragmentManager().findFragmentByTag(TAG_FILTER);
                if (null != frg) {
                    transaction.hide(frg);
                }

                frg = getSupportFragmentManager().findFragmentByTag(TAG_LABEL);
                if (null != frg) {
                    transaction.hide(frg);
                }
                break;

            case ID_FILTER:
                frg = getSupportFragmentManager().findFragmentByTag(TAG_FILTER);
                if (null == frg) {
                    transaction.add(R.id.image_edit_frame, mFilterFragment,
                            TAG_FILTER);
                } else {
                    transaction.show(frg);
                }
                frg = getSupportFragmentManager().findFragmentByTag(TAG_TAGS);
                if (null != frg) {
                    transaction.hide(frg);
                }

                frg = getSupportFragmentManager().findFragmentByTag(TAG_LABEL);
                if (null != frg) {
                    transaction.hide(frg);
                }

                break;
            case ID_LABEL:
                frg = getSupportFragmentManager().findFragmentByTag(TAG_LABEL);
                if (null == frg) {
                    transaction.add(R.id.image_edit_frame, mLabelFragment,
                            TAG_LABEL);
                } else {
                    transaction.show(frg);
                }
                frg = getSupportFragmentManager().findFragmentByTag(TAG_FILTER);
                if (null != frg) {
                    transaction.hide(frg);
                }

                frg = getSupportFragmentManager().findFragmentByTag(TAG_TAGS);
                if (null != frg) {
                    transaction.hide(frg);
                }

                break;

        }

        transaction.commitAllowingStateLoss();
    }

    private void confirm() {
        // mDestBitmap
        String picN = ComUtils.getRandomAlphamericStr(8) + ".jpg";
        File temp = new File(getCacheDir(), picN);
        String cropImagePath = temp.getAbsolutePath();
        mImageResult.setVisibility(View.VISIBLE);
        try {
            mImageResult.setImageBitmap(mImage.capture());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mImage.setVisibility(View.GONE);
        mSticker.setEditable(false);

        Bitmap bmp = null;
        try {
            if (mSticker.getVisibility() == View.VISIBLE) {
                // 把图片写入到一个bitmap
                bmp = Bitmap.createBitmap(mImage.capture());
                Canvas canvas = new Canvas(bmp);
                Point point = mSticker.getLTPoint();
                Bitmap bmpSticker = Bitmap.createBitmap(mSticker.getBitmap(), 0, 0,
                        mSticker.getBitmap().getWidth(), mSticker.getBitmap()
                                .getHeight(), mSticker.getBitmapMatrix(), false);
                canvas.drawBitmap(bmpSticker, point.x, point.y, null);
                // canvas.drawBitmap(mSticker.getBitmap(),mSticker.getBitmapMatrix(),null);
                canvas.save();
            } else {
                bmp = mImage.capture();
            }
        } catch (InterruptedException e) {
            AndroidUtil.showToast("图片处理发生异常,请重试");
            return;
        }
        // 对frameLayout截屏
        // 获得的图片正常,贴纸没有了
        // Bitmap bitmap = CaptureViewUtils.convertViewToBitmap(mImageRoot);
        // 下列方式获得的图片是一个黑板,但贴纸还在
        // mImageRoot.buildDrawingCache();
        // Bitmap bitmap = mImageRoot.getDrawingCache();
        BitmapUtils.compressImage(cropImagePath, bmp);// 质量压缩

        // 如果贴纸被隐藏了,就传递一个0
        if (mSticker.getVisibility() == View.GONE) {
            mReleaseData.setPtid(0);
        }

        if (isMod) {// 如果是修改图片,那么由发布界面的result方法处理,传递一个path回去
            Bundle bundle = new Bundle();
            bundle.putString("path", cropImagePath);// 给 bundle 写入数据
            bundle.putInt("ptid", mReleaseData.getPtid());
            Intent intent = new Intent();
            intent.putExtras(bundle);
            setResult(RESULT_OK, intent);
            finish();

        } else {// 如果是一般的编辑就直接启动它

            if (mode == CameraActivity1.MOD_COVER) {// 更换封面
                // 让图片与其最后一张贴纸的id保持一致
                mReleaseData.getPaths().remove(0);
                mReleaseData.getPaths().add(0, cropImagePath);

                mReleaseData.getPtids().remove(0);
                // 要加一个判断,如果这次没有加贴纸就用上一次的,都没有就是0 不用管
                if (mReleaseData.getPtid() != 0)
                    mReleaseData.getPtids().add(0, mReleaseData.getPtid());
            } else {// 普通添加
                mReleaseData.getPaths().add(cropImagePath);
                mReleaseData.getPtids().add(mReleaseData.getPtid());
            }

            Intent intent = CameraActivity1.newIntent(mReleaseData);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBitmap != null && !mBitmap.isRecycled()) {
            // 回收并且置为null
            mBitmap.recycle();
            mBitmap = null;
        }
        System.gc();
    }

    public void showProgress(String title, String message) {
        if (null == mProgressDialog) {
            mProgressDialog = CustomProgress.show(this, message, true, null);
        } else {
            if (!mProgressDialog.isShowing()) {
                mProgressDialog.show();
            }
        }
    }

    public void hideProgress() {
        if (null != mProgressDialog && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!isMod) {// 如果是修改图片{
                startActivity(ImageChooseActivity1
                        .newIntent(mReleaseData, mode));
            }
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private InstaFilter getFilter(int index, Context context) {
        InstaFilter filter = null;
        switch (index) {
            case 0:
                filter = new IFNormalFilter(context);
                break;
            case 1:
                filter = new IFAmaroFilter(context);
                break;
            case 2:
                filter = new IFRiseFilter(context);
                break;
            case 3:
                filter = new IFHudsonFilter(context);
                break;
            case 4:
                filter = new IFXproIIFilter(context);
                break;
            case 5:
                filter = new IFSierraFilter(context);
                break;
            case 6:
                filter = new IFLomofiFilter(context);
                break;
            case 7:
                filter = new IFEarlybirdFilter(context);
                break;
            case 8:
                filter = new IFSutroFilter(context);
                break;
            case 9:
                filter = new IFToasterFilter(context);
                break;
            case 10:
                filter = new IFBrannanFilter(context);
                break;
            case 11:
                filter = new IFInkwellFilter(context);
                break;
            case 12:
                filter = new IFWaldenFilter(context);
                break;
            case 13:
                filter = new IFHefeFilter(context);
                break;
            case 14:
                filter = new IFValenciaFilter(context);
                break;
            case 15:
                filter = new IFNashvilleFilter(context);
                break;
            case 16:
                filter = new IF1977Filter(context);
                break;
            case 17:
                filter = new IFLordKelvinFilter(context);
                break;
        }
        return filter;
    }

}
