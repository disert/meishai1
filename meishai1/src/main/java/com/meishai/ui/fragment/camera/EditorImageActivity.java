package com.meishai.ui.fragment.camera;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.album.utils.ImageLoader;
import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.app.widget.CustomProgress;
import com.meishai.entiy.ReleaseData;
import com.meishai.net.VolleyHelper;
import com.meishai.net.volley.toolbox.ListImageListener;
import com.meishai.ui.base.BaseFragmentActivity;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.tab.UserCenterTab;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DataCleanManager;
import com.meishai.util.DiskImageCacheUtil;
import com.meishai.util.ImageUtil;
import com.meishai.util.LoadImageTask;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件名：EditorImageActivity
 * 描    述：多张图片一起编辑的编辑页面
 * 作    者：yl
 * 时    间：2015/12/22
 * 版    权：
 */
public class EditorImageActivity extends BaseFragmentActivity implements
        View.OnClickListener {

    private static final int ADD_REQUEST_CODE = 100;//从选择图片返回的添加图片的请求码
    private static final int EDIT_REQUEST_CODE = 101;//从发布界面返回的请求码
    private final String TAG_CROP = "crop";
    private final String TAG_FILTER = "filter";
    private final String TAG_TAGS = "tags";

    private static final int ID_LABEL = 2;
    private static final int ID_FILTER = 1;
    private static final int ID_TAGS = 0;

    private TagsFragment1 mTagsFragment = new TagsFragment1();// 贴纸
    private LabelFragment mCropFragment = new LabelFragment();// 裁剪
    private FilterFragment2 mFilterFragment = new FilterFragment2();// 滤镜

    private Button mConfirm;
    private ImageButton mBack;
    // 滤镜
    private LinearLayout mFilter;
    private TextView mFilterText;
    private ImageView mFilterIcon;
    // 裁剪
    private TextView mCropText;
    private LinearLayout mCrop;
    private ImageView mCropIcon;
    // 贴纸
    private LinearLayout mTags;
    private TextView mTagsText;
    private ImageView mTagsIcon;

    private Bitmap mBitmap;// 编辑前的图片
    private CustomProgress mProgressDialog;//进度条
    private ReleaseData mReleaseData;//发布数据

    private LinearLayout mPics;
    private ImageView mAddPic;
    private ArrayList<String> mSelectedPics;//所有图片的集合
    private int mCurrentPicPosition = 0;//当前操作的图片的索引
    private FrameLayout mImageFrame;//图片的frame
    private List<EditorImageFragment> mEditeImageFragments;


    /**
     * @param releaseData  已有的发布数据
     * @param selectedPics 选中图片的地址
     * @return
     */
    public static Intent newIntent(ReleaseData releaseData, ArrayList<String> selectedPics) {
        Intent intent = new Intent(GlobalContext.getInstance(),
                EditorImageActivity.class);
        intent.putExtra("releaseData", releaseData);
        intent.putExtra("selectedPics", selectedPics);
        intent.putExtra("mode", 0);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.image_editor_activity1);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        //顶部图片列表
        mPics = (LinearLayout) findViewById(R.id.image_edit_pics);
        //添加图片按钮
        mAddPic = (ImageView) findViewById(R.id.image_edit_add);
        //图片编辑区域
        mImageFrame = (FrameLayout) findViewById(R.id.image_edit_image_frame);
        //下一步
        mConfirm = (Button) findViewById(R.id.image_edit_confirm);
        //取消
        mBack = (ImageButton) findViewById(R.id.image_edit_back);
        //滤镜
        mFilter = (LinearLayout) findViewById(R.id.image_edit_filter);
        mFilterText = (TextView) findViewById(R.id.image_edit_filter_text);
        mFilterIcon = (ImageView) findViewById(R.id.image_edit_filter_icon);
        //裁剪
        mCrop = (LinearLayout) findViewById(R.id.image_edit_crop);
        mCropText = (TextView) findViewById(R.id.image_edit_crop_text);
        mCropIcon = (ImageView) findViewById(R.id.image_edit_crop_icon);
        //贴纸
        mTags = (LinearLayout) findViewById(R.id.image_edit_tags);
        mTagsText = (TextView) findViewById(R.id.image_edit_tags_text);
        mTagsIcon = (ImageView) findViewById(R.id.image_edit_tags_icon);


    }

    private void initData() {
        //启动时传入过来的数据
        mReleaseData = getIntent().getParcelableExtra("releaseData");
        //保存图片的原始路径
        mSelectedPics = (ArrayList<String>) getIntent().getSerializableExtra("selectedPics");

        //添加图片按钮是否显示
        if (mSelectedPics != null && (mSelectedPics.size() < ConstantSet.MAX_IMAGE_COUNT)) {
            mAddPic.setVisibility(View.VISIBLE);
        } else {
            mAddPic.setVisibility(View.GONE);
        }


        initPics();
        initPicsFragment();

        showCrop();

    }

    private void initListener() {
        mCrop.setOnClickListener(this);
        mTags.setOnClickListener(this);
        mFilter.setOnClickListener(this);
        mBack.setOnClickListener(this);
        mConfirm.setOnClickListener(this);
        mAddPic.setOnClickListener(this);
        // 为贴纸fragment设置贴纸选中监听器
        mTagsFragment.setStickerListener(new TagsFragment1.StickerListener() {

            @Override
            public void controlBitmap(Bitmap bmp, int tid, View view) {
                mEditeImageFragments.get(mCurrentPicPosition).setSticker(bmp, tid, view);
            }
        });
        // 为滤镜设置滤镜选择监听器
        mFilterFragment.setFilterListener(new FilterFragment2.OnFilterSelectedListener() {

            @Override
            public void SelectedFilter(int index, View view) {
                mEditeImageFragments.get(mCurrentPicPosition).setFilter(index, view);
            }
        });
        //为裁剪设置裁剪监听器
        mCropFragment.setOnCtrlLinstenner(new LabelFragment.OnCtrlLinstenner() {
            @Override
            public void ctrlChange(int ctrlType) {
                // 裁剪操作的处理
                mEditeImageFragments.get(mCurrentPicPosition).setCrop(ctrlType);
            }
        });
    }

    //    @Override
    //    public void onWindowFocusChanged(boolean hasFocus) {
    //        DebugLog.w("onWindowFocusChanged");
    //        if (hasFocus) {// 当页面加载完毕
    //            int width = mImageFrame.getWidth();
    //            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
    //                    width, width);
    //            //            mEditeImageFragments.get(mCurrentPicPosition).resetSticker();
    //        } else {
    //            super.onWindowFocusChanged(hasFocus);
    //        }
    //    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_edit_back:// 返回
                finish();
                break;
            case R.id.image_edit_confirm:// 编辑完成
                confirm();
                break;
            case R.id.image_edit_filter:// 滤镜
                showFilter();
                break;
            case R.id.image_edit_crop:// 裁剪
                showCrop();
                break;
            case R.id.image_edit_tags:// 贴纸
                showSticker();
                break;
            case R.id.image_edit_add:// 添加图片
                addPic();
                break;
            default:
                break;
        }
    }

    /**
     * TODO 添加图片
     */
    private void addPic() {

        if (mReleaseData.getPaths() != mSelectedPics) {
            mReleaseData.setPaths(mSelectedPics);
        }

        startActivityForResult(ChooseImageActivity.newIntent(
                        mReleaseData,
                        ConstantSet.MAX_IMAGE_COUNT - mSelectedPics.size(),
                        ConstantSet.RETURN_MODE),
                ADD_REQUEST_CODE);
    }

    /**
     * 下一步
     */
    private void confirm() {
        // 保存当前正在编辑的图片
        mEditeImageFragments.get(mCurrentPicPosition).saveImage();

        //获取所有编辑好的图片,并启动发布界面
        new HandleTask().execute();


    }

    /**
     * 贴纸
     */
    private void showSticker() {
        mTagsIcon.setSelected(true);
        mFilterIcon.setSelected(false);
        mCropIcon.setSelected(false);
        mTagsText.setSelected(true);
        mFilterText.setSelected(false);
        mCropText.setSelected(false);

        //        mSticker.setEditable(true);
        switchFragment(ID_TAGS);
    }

    /**
     * 裁剪
     */
    private void showCrop() {
        mTagsIcon.setSelected(false);
        mFilterIcon.setSelected(false);
        mCropIcon.setSelected(true);


        mTagsText.setSelected(false);
        mFilterText.setSelected(false);
        mCropText.setSelected(true);


        switchFragment(ID_LABEL);
    }

    /**
     * 滤镜
     */
    private void showFilter() {
        mTagsIcon.setSelected(false);
        mFilterIcon.setSelected(true);
        mCropIcon.setSelected(false);

        mTagsText.setSelected(false);
        mFilterText.setSelected(true);
        mCropText.setSelected(false);

        //        mSticker.setEditable(false);

        switchFragment(ID_FILTER);
    }

    /**
     * 显示对应tag的fragment,主要是用于滤镜,贴纸,裁剪这三个fragment的
     *
     * @param tag
     */
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

                frg = getSupportFragmentManager().findFragmentByTag(TAG_CROP);
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

                frg = getSupportFragmentManager().findFragmentByTag(TAG_CROP);
                if (null != frg) {
                    transaction.hide(frg);
                }

                break;
            case ID_LABEL:
                frg = getSupportFragmentManager().findFragmentByTag(TAG_CROP);
                if (null == frg) {
                    transaction.add(R.id.image_edit_frame, mCropFragment,
                            TAG_CROP);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            //添加图片的
            if (requestCode == ADD_REQUEST_CODE) {
                List<String> paths = (ArrayList<String>) data.getSerializableExtra("paths");
                mSelectedPics.addAll(paths);
                mReleaseData.setPaths(mSelectedPics);
                addPics(paths);
                addPicFragment(paths);
            } else if (requestCode == EDIT_REQUEST_CODE) {//发布界面的返回
                if (data == null) {
                    finish();
                } else {
                    ReleaseData releaseData = data.getParcelableExtra("releaseData");
                    mReleaseData = releaseData;
                    mReleaseData.setPaths(mSelectedPics);//发布页面的图片是处理后的图片,设置成先前的
                    addPic();
                }
            }

        }
    }

    /**
     * 添加图片后,要增加对应的图片编辑的fragment
     *
     * @param paths
     */
    private void addPicFragment(List<String> paths) {
        if (mEditeImageFragments == null)
            mEditeImageFragments = new ArrayList<EditorImageFragment>();

        if (paths != null && !paths.isEmpty()) {
            for (String path : paths) {
                EditorImageFragment edit = new EditorImageFragment();
                Bundle bundle = new Bundle();
                bundle.putString("path", path);
                edit.setArguments(bundle);
                mEditeImageFragments.add(edit);
            }

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

    //    @Override
    //    public boolean onKeyDown(int keyCode, KeyEvent event) {
    //        if (keyCode == KeyEvent.KEYCODE_BACK) {
    //            if (!isMod) {// 如果是修改图片{
    //                startActivity(ImageChooseActivity1
    //                        .newIntent(mReleaseData, mode));
    //            }
    //            finish();
    //            return true;
    //        }
    //        return super.onKeyDown(keyCode, event);
    //    }


    /**
     * 初始化图片
     */
    private void initPics() {

        if (mSelectedPics != null && !mSelectedPics.isEmpty()) {
            mPics.removeAllViews();
            int size = AndroidUtil.dip2px(42);
            int margin = AndroidUtil.dip2px(10);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(size, size);
            lp.setMargins(0, 0, margin, 0);
            for (final String path : mSelectedPics) {
                //                View view = View.inflate(this, R.layout.border_image, null);
                //                ImageView imageView = (ImageView) view.findViewById(R.id.image);
                ImageView imageView = new ImageView(this);
                imageView.setBackgroundResource(R.drawable.sticker_item_selector);
                imageView.setPadding(3, 3, 3, 3);
                imageView.setLayoutParams(lp);
                imageView.setLayoutParams(lp);
                //加载图片
                if (!TextUtils.isEmpty(path)) {
                    if (path.startsWith("http")) {//网络图片
                        imageView.setTag(path);
                        ListImageListener listener = new ListImageListener(imageView, R.drawable.place_default, R.drawable.place_default, path);
                        VolleyHelper.getImageLoader(this).get(path, listener);
                    } else {//本地图片
                        ImageLoader.getInstance().loadImage(path, imageView);
                        //                        new LoadImageTask(this, path, imageView, 5).execute();
                    }
                }
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //切换图片和与之对应的fragment
                        switchImage(v, path);
                    }
                });
                mPics.addView(imageView);
            }

            //设置选中的图片状态
            setCurrentPic(mPics.getChildAt(0));
        }
    }

    /**
     * 添加图片后 要为图片容器添加显示图片的view
     *
     * @param paths
     */
    private void addPics(List<String> paths) {

        if (paths != null && !paths.isEmpty()) {
            int size = AndroidUtil.dip2px(42);
            int margin = AndroidUtil.dip2px(10);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(size, size);
            lp.setMargins(0, 0, margin, 0);
            for (final String path : paths) {
                //                View view = View.inflate(this, R.layout.border_image, null);
                ImageView imageView = new ImageView(this);
                imageView.setBackgroundResource(R.drawable.sticker_item_selector);
                imageView.setPadding(3, 3, 3, 3);
                imageView.setLayoutParams(lp);
                //加载图片
                if (!TextUtils.isEmpty(path)) {
                    if (path.startsWith("http")) {//网络图片
                        imageView.setTag(path);
                        ListImageListener listener = new ListImageListener(imageView, R.drawable.place_default, R.drawable.place_default, path);
                        getImageLoader().get(path, listener);
                    } else {//本地图片
                        imageView.setImageBitmap(ImageUtil.getImageFromSDCard(path));
                    }
                }
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //切换图片和与之对应的fragment
                        switchImage(v, path);
                    }
                });
                mPics.addView(imageView);
            }
            if (mSelectedPics.size() >= ConstantSet.MAX_IMAGE_COUNT) {
                mAddPic.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 切换图片的操作
     *
     * @param v    当前展示图片的view,用于改变它的选中状态
     * @param path 当前选中图片的路径
     */
    private void switchImage(View v, String path) {
        if (mCurrentPicPosition == mSelectedPics.indexOf(path)) return;
        //提示当前的fragment保存编辑的图片数据
        mEditeImageFragments.get(mCurrentPicPosition).saveImage();
        //设置view的选中状态
        setCurrentPic(v);
        //设置图片编辑效果展示的fragment
        mCurrentPicPosition = mSelectedPics.indexOf(path);
        showPicFragment(mCurrentPicPosition);
        //设置当前图片的编辑的状态(滤镜,贴纸,裁剪 3货的状态)
        setEditState();
    }

    /**
     * 设置当前图片的编辑的状态(滤镜,贴纸,裁剪 3货的状态)
     */
    private void setEditState() {
        EditorImageFragment currentFragment = mEditeImageFragments.get(mCurrentPicPosition);
        currentFragment.initImageState(mTagsFragment, mFilterFragment, mCropFragment);
    }

    /**
     * 设置当前编辑的图片的选中状态
     *
     * @param view
     */
    private void setCurrentPic(View view) {
        int count = mPics.getChildCount();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                mPics.getChildAt(i).setSelected(false);
            }
            view.setSelected(true);

        }
    }

    /**
     * 初始化图片编辑区域的fragment
     */
    private void initPicsFragment() {
        mEditeImageFragments = new ArrayList<EditorImageFragment>();

        if (mSelectedPics != null && !mSelectedPics.isEmpty()) {
            for (String path : mSelectedPics) {
                EditorImageFragment edit = new EditorImageFragment();
                Bundle bundle = new Bundle();
                bundle.putString("path", path);
                edit.setArguments(bundle);
                mEditeImageFragments.add(edit);
            }
            showPicFragment(0);

        }
    }

    /**
     * 设置当前图片所对应的编辑fragment
     *
     * @param currentPicPosition
     */
    private void showPicFragment(int currentPicPosition) {

        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        Fragment fragment = mEditeImageFragments.get(currentPicPosition);
        transaction.replace(R.id.image_edit_image_frame, fragment, "EditPic" + currentPicPosition);
        transaction.commit();
        //        Fragment frg;
        //        for (int i = 0; i < mEditeImageFragments.size(); i++) {
        //            frg = getSupportFragmentManager().findFragmentByTag("EditPic" + i);
        //            if (null != frg) {
        //                transaction.hide(frg);
        //            }
        //        }
        //        frg = getSupportFragmentManager().findFragmentByTag("EditPic" + currentPicPosition);
        //        if (null == frg) {
        //            transaction.add(R.id.image_edit_image_frame, mEditeImageFragments.get(currentPicPosition), "EditPic" + currentPicPosition);
        //        } else {
        //            transaction.show(frg);
        ////            mEditeImageFragments.get(currentPicPosition).resetImage();
        //        }
        //        transaction.commitAllowingStateLoss();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 对所有图片进行处理的一般任务类
     */
    class HandleTask extends AsyncTask {

        /**
         * 执行前
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress("", "处理中,请稍候!");
        }

        /**
         * 执行后
         *
         * @param o
         */
        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            hideProgress();
            //启动发布页面
            //            startActivityForResult(ReleaseActivity1.newIntent(mReleaseData), EDIT_REQUEST_CODE);
            startActivityForResult(ReleaseActivity1.newIntent(mReleaseData), EDIT_REQUEST_CODE);
        }

        /**
         * 执行中
         *
         * @param params
         * @return
         */
        @Override
        protected Object doInBackground(Object[] params) {
            ArrayList<String> pics = new ArrayList<String>();

            //获取每个图片的编辑结果和拼接tid
            StringBuilder tids = new StringBuilder();
            boolean isComma = false;//控制是否在拼接tid时要添加","逗号,第一次字符不为空之前都不加,之后才需要加
            for (int i = 0; i < mEditeImageFragments.size(); i++) {
                pics.add(mEditeImageFragments.get(i).getSavePath());
                //所有选过的贴纸
                String temp = mEditeImageFragments.get(i).getTid();

                if (isComma && !TextUtils.isEmpty(temp)) {
                    tids.append("," + temp);
                } else {
                    tids.append(temp);
                }
                if (!TextUtils.isEmpty(temp)) {
                    isComma = true;
                }
            }
            //设置数据
            mReleaseData.setPaths(pics);
            if (TextUtils.isEmpty(mReleaseData.getTids())) {
                mReleaseData.setTids(tids.toString());
            } else {
                if (tids.length() != 0) {
                    mReleaseData.setTids(mReleaseData.getTids() + "," + tids.toString());
                }
            }
            return null;
        }
    }

}
