package com.meishai.ui.fragment.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.app.widget.layout.SingleTouchView;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.ui.base.BaseFragment;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.util.AndroidUtil;
import com.meishai.util.ComUtils;
import com.meishai.util.DebugLog;
import com.meishai.util.DiskImageCacheUtil;
import com.meishai.util.InstaFilterUtils;

import org.insta.InstaFilter;

import java.io.File;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageView;

/**
 * 文件名：EditorImageFragment
 * 描    述：图片编辑效果展示的fragment
 * 作    者：yl
 * 时    间：2015/12/24
 * 版    权：
 */
public class EditorImageFragment extends BaseFragment {

    //    private FrameLayout mImageRoot;
    //    private ImageView mImageResult;
    private String mPath;
    private GPUImageView mImage;
    private SingleTouchView mSticker;
    private FrameLayout mRoot;
    private View convertview;
    private Bitmap mBitmap;
    private String mTid = "";
    private InstaFilter mFilter;//当前使用的滤镜
    private int mFilterIndex;//当前滤镜的索引
    private View mFilterView;//当前选中的滤镜的view

    private int mCurrentType = LabelFragment.CTRL_SQUARE;//当前的裁剪状态

    private boolean mIsShowSticker;
    private Bitmap mStickerBitmap;//贴纸的bitmap
    private Matrix mStickerMatrix;
    private Bundle mStickerBundle;//贴纸的状态
    private View mStickerView;//当前选中的贴纸的view

    private LinearLayout.LayoutParams layoutParams;
    private FrameLayout.LayoutParams lp;
    private int mScreenWidht;
    private String mSavePath;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        convertview = View.inflate(getActivity(), R.layout.image_editor_pic_fragment, null);
        initView();
        initData();

        return convertview;
    }

    private void initView() {
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        mScreenWidht = dm.widthPixels;
        layoutParams = new LinearLayout.LayoutParams(mScreenWidht, mScreenWidht);
        lp = new FrameLayout.LayoutParams(mScreenWidht, mScreenWidht);

        mImage = (GPUImageView) convertview.findViewById(R.id.image_edit_image);
        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSticker.setEditable(false);
            }
        });
        mSticker = (SingleTouchView) convertview.findViewById(R.id.image_edit_sticker);
        mRoot = (FrameLayout) convertview.findViewById(R.id.image_edit_root);
        mRoot.setLayoutParams(layoutParams);
        mImage.setLayoutParams(lp);

    }

    private void initData() {
        if (null == mPath) {//第一次 没数据的情况
            mPath = getArguments().getString("path");
            //加载图片
            if (mPath.startsWith("http")) {
                getImageLoader().get(mPath, new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                        if (response != null && response.getBitmap() != null) {
                            mBitmap = response.getBitmap();
                            mImage.setImage(response.getBitmap());
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        AndroidUtil.showToast("加载图片失败!");
                    }
                });
            } else {
                //图片太模糊
                mBitmap = loadImage(mPath);
            }
            if (mBitmap != null)
                mImage.setImage(mBitmap);//为图片设置数据

            //贴纸
            mSticker.setEditable(false);
            mSticker.setVisibility(View.GONE);

            //初始化编辑后的图片保存地址
            String picN = ComUtils.getRandomAlphamericStr(8) + ".jpg";
            File temp = new File(DiskImageCacheUtil.getInstance().getCacheDir(ConstantSet.EDIT_PATH), picN);
            mSavePath = temp.getAbsolutePath();
        } else { //一次以后,有数据 恢复
            //图片显示方式
            GPUImage.ScaleType scaleType = (mCurrentType == LabelFragment.CTRL_SQUARE ? GPUImage.ScaleType.CENTER_CROP : GPUImage.ScaleType.CENTER_INSIDE);
            mImage.setScaleType(scaleType);
            //为imageview设置图片
            if (mBitmap != null)
                mImage.setImage(mBitmap);
            //滤镜效果
            if (mFilterIndex != 0) {
                setFilter(mFilterIndex, mFilterView);
            }
            //贴纸效果
            initSticker();
        }

    }

    /**
     * 恢复贴纸的状态
     */
    private void initSticker() {
        if (mStickerBundle != null) {
            mIsShowSticker = mStickerBundle.getInt("isVisibility") == View.VISIBLE;
        }
        if (mStickerBitmap != null && mIsShowSticker) {
            mSticker.setImageBitmap(mStickerBitmap);
            mSticker.setMatrix(mStickerMatrix);
            mSticker.resetState(mStickerBundle);
//            mSticker.setLayoutParams(lp);
        } else {
            mSticker.setVisibility(View.GONE);
        }
    }

    public void setPath(String path) {
        mPath = path;
    }

    public void setSticker(Bitmap bmp, int tid, View view) {
        if (mSticker == null) return;
        // 对贴纸进行操作
        mStickerView = view;
        mTid = tid + "";
        mIsShowSticker = true;
        mStickerBitmap = bmp;
        mSticker.setVisibility(View.VISIBLE);
        mSticker.setEditable(true);
        mSticker.setImageBitmap(bmp);
    }

    /**
     * 设置图片的显示类型
     *
     * @param ctrlType
     */
    public void setCrop(int ctrlType) {
        if (ctrlType == mCurrentType) {
            return;
        }
        if (ctrlType != LabelFragment.CTRL_ROTATE) {
            mCurrentType = ctrlType;
        }
        switch (ctrlType) {
            case LabelFragment.CTRL_RECT://长方形
                mImage.setScaleType(GPUImage.ScaleType.CENTER_INSIDE);
                mImage.setImage(mBitmap);
                break;
            case LabelFragment.CTRL_ROTATE://旋转
                Matrix matrix = new Matrix();
                matrix.postRotate(90); //旋转90度
                Bitmap bmp = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
                if (null != mBitmap) {
                    mBitmap.recycle();
                }
                mBitmap = bmp;
                GPUImage.ScaleType scaleType = (mCurrentType == LabelFragment.CTRL_SQUARE ? GPUImage.ScaleType.CENTER_CROP : GPUImage.ScaleType.CENTER_INSIDE);
                mImage.setScaleType(scaleType);
                mImage.setImage(mBitmap);
                break;
            case LabelFragment.CTRL_SQUARE://正方形
                mImage.setScaleType(GPUImage.ScaleType.CENTER_CROP);
                mImage.setImage(mBitmap);
                break;
        }
    }

    public void setFilter(int filterIndex, View view) {
        mFilterView = view;
        mFilterIndex = filterIndex;
        mFilter = InstaFilterUtils.getFilter(filterIndex, getActivity());
        mImage.setFilter(mFilter);
    }

    /**
     * 由于使用的这个框架的imageview重新显示之后没办法正常的显示图片,目前发现能让其重新显示的方法是重新new一个
     */
    public void resetImage() {
        if (mRoot != null) {
            mRoot.removeView(mImage);

            mImage = new GPUImageView(mContext);
            mImage.setLayoutParams(layoutParams);
            mRoot.addView(mImage, 0);

            if (mBitmap != null)
                mImage.setImage(mBitmap);

            //设置滤镜
            mImage.setFilter(InstaFilterUtils.getFilter(mFilterIndex, mContext));
        }
    }

    public void resetSticker() {
        if (mSticker != null && mStickerBitmap != null) {
            mSticker.setImageBitmap(mStickerBitmap);
            mSticker.setMatrix(mStickerMatrix);
            mSticker.resetState(mStickerBundle);
        }
    }


    /**
     * 设置选中的状态
     *
     * @param tagsFragment
     * @param filterFragment
     * @param cropFragment
     */
    public void initImageState(TagsFragment1 tagsFragment, FilterFragment2 filterFragment, LabelFragment cropFragment) {
        if (tagsFragment != null)
            tagsFragment.setselect(mStickerView);
        if (filterFragment != null)
            filterFragment.setselect(mFilterView);
        if (cropFragment != null)
            cropFragment.setSelected(mCurrentType);
    }

    public void saveImage() {
        if (mImage == null) return;
        mSticker.setEditable(false);
        Bitmap bmp = null;
        try {
            if (mSticker.getVisibility() == View.VISIBLE) {
                // 把图片写入到一个bitmap
//                bmp = Bitmap.createBitmap(mImage.capture());
                bmp = mImage.capture();
                Canvas canvas = new Canvas(bmp);
                Point point = mSticker.getLTPoint();
                Bitmap bmpSticker = Bitmap.createBitmap(mStickerBitmap, 0, 0, mStickerBitmap.getWidth(), mStickerBitmap
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
        File file = new File(mSavePath);
        if (file.exists()) {
            file.delete();
        }
        //换个地址试试
        String picN = ComUtils.getRandomAlphamericStr(8) + ".jpg";
        File temp = new File(DiskImageCacheUtil.getInstance().getCacheDir(ConstantSet.EDIT_PATH), picN);
        mSavePath = temp.getAbsolutePath();
//        ImageUtil.saveBitMap(mSavePath, bmp);
        BitmapUtils.compressImage(mSavePath, bmp);// 质量压缩
    }

    @Override
    public void onDestroy() {
        mStickerMatrix = mSticker.getBitmapMatrix();
        mStickerBundle = mSticker.saveState();


        super.onDestroy();
    }

    /**
     * 返回图片编辑后的保存路径,如果没有编辑过需要进行一个处理,建议放在子线程中
     *
     * @return
     */
    public String getSavePath() {
        //当图片还没有被编辑时,这个值是null,那就需要手动的进行裁剪了
        if (TextUtils.isEmpty(mSavePath)) {
            mPath = getArguments().getString("path");
            if (TextUtils.isEmpty(mPath)) {
                throw new RuntimeException("图片路径为空!");
            }
            String picN = ComUtils.getRandomAlphamericStr(8) + ".jpg";
            File temp = new File(DiskImageCacheUtil.getInstance().getCacheDir(ConstantSet.EDIT_PATH), picN);
            mSavePath = temp.getAbsolutePath();


            mBitmap = loadImage(mPath);
            int l, r, t, b, w;
            int bmpwidth = mBitmap.getWidth();
            int bmpheight = mBitmap.getHeight();
            if (bmpheight < bmpwidth) {
                l = (bmpwidth - bmpheight) / 2;
                r = l + bmpheight;
                t = 0;
                b = bmpheight;
                w = bmpheight;
            } else {
                l = 0;
                r = bmpwidth;
                t = (bmpheight - bmpwidth) / 2;
                b = t + bmpwidth;
                w = bmpwidth;
            }


//            Matrix matrix = new Matrix();
            Bitmap bitmap = Bitmap.createBitmap(mBitmap, l, t, w, w);
            BitmapUtils.compressImage(mSavePath, bitmap);// 质量压缩
//            ImageUtil.saveBitMap(mSavePath, bitmap);

        }
        return mSavePath;
    }

    public String getTid() {
        if (mTid == null) {
            return "";
        }
        return mTid;
    }

    private Bitmap loadImage(String filepath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        File file = new File(filepath);
        boolean exists = file.exists();
        if (exists) {
            //TODO 对图片进行加载时的优化
            BitmapFactory.decodeFile(filepath, options);
            double imageWidth = options.outWidth;
            double screenWidth = GlobalContext.getInstance().getApplicationContext().getResources().getDisplayMetrics().widthPixels;
            int inSampleSize = (int) (imageWidth / screenWidth + 0.9);//图片宽度大于屏幕宽度10%就进行优化
            if (inSampleSize > 1) {
                options.inSampleSize = inSampleSize;
            }
            DebugLog.w("inSampleSize:" + options.inSampleSize + ",screenWidth:" + ConstantSet.SCREEN_WIDTH + ",imageWidth:" + options.outWidth);
            options.inJustDecodeBounds = false;
            Bitmap bm = BitmapFactory.decodeFile(filepath, options);

            return bm;
        }
        return null;
    }
}
