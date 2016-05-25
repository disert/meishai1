package com.meishai.ui.fragment.camera;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.app.widget.ClipView;
import com.meishai.entiy.ReleaseData;
import com.meishai.util.ComUtils;
import com.meishai.util.ImageUtil;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

/**
 * 新的图片裁剪界面-2015年11月25日15:35:56
 * <p/>
 * yl
 */
public class ClipPictureActivity extends Activity implements OnTouchListener,
        OnClickListener {

    private ImageView mSrcPic;
    private ImageButton sure;
    private ClipView clipview;

    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();

    /**
     * 动作标志：无
     */
    private static final int NONE = 0;
    /**
     * 动作标志：拖动
     */
    private static final int DRAG = 1;
    /**
     * 动作标志：缩放
     */
    private static final int ZOOM = 2;
    /**
     * 初始化动作标志
     */
    private int mode = NONE;

    /**
     * 记录起始坐标
     */
    private PointF start = new PointF();
    /**
     * 记录缩放时两指中间点坐标
     */
    private PointF mid = new PointF();
    private float oldDist = 1f;

    private Bitmap bitmap;
    private FrameLayout mFrame;
    private ImageButton mBack;
    private ImageButton mNext;
    private ReleaseData mReleaseData;
    private int edit_mode;
    private String mChoosePath;


    /**
     * @param releaseData 已有的发布数据
     * @param currentPath 选中图片的路径
     * @param mode        添加图片的模式,是普通添加还是修改封面
     * @return
     */
    public static Intent newIntent(ReleaseData releaseData, String currentPath,
                                   int mode) {
        Intent intent = new Intent(GlobalContext.getInstance(),
                ClipPictureActivity.class);
        intent.putExtra("releaseData", releaseData);
        intent.putExtra("mode", mode);
        intent.putExtra("currentPath", currentPath);

        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clip_picture_activity);

        initView();
        initData();


        mFrame = (FrameLayout) findViewById(R.id.frame);

        ViewTreeObserver observer = mSrcPic.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            public void onGlobalLayout() {
                mSrcPic.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                initClipView(mSrcPic.getTop());
            }
        });

    }


    private void initView() {
        mBack = (ImageButton) findViewById(R.id.cut_image_back);
        mBack.setOnClickListener(this);
        mNext = (ImageButton) findViewById(R.id.cut_image_next);
        mNext.setOnClickListener(this);
        mSrcPic = (ImageView) this.findViewById(R.id.src_pic);
        mSrcPic.setOnTouchListener(this);
    }

    private void initData() {

        edit_mode = getIntent().getIntExtra("mode", CameraActivity1.ADD_IMAGE);
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
            bitmap = BitmapFactory.decodeStream(new FileInputStream(mChoosePath));
            mSrcPic.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化截图区域，并将源图按裁剪框比例缩放
     *
     * @param top
     */
    private void initClipView(int top) {
        clipview = new ClipView(ClipPictureActivity.this);
//		clipview.setCustomTopBarHeight(top);

        clipview.addOnDrawCompleteListener(new ClipView.OnDrawListenerComplete() {

            public void onDrawCompelete() {
                clipview.removeOnDrawCompleteListener();
                int clipHeight = clipview.getClipHeight();
                int clipWidth = clipview.getClipWidth();
                int midX = clipview.getClipLeftMargin() + (clipWidth / 2);
                int midY = clipview.getClipTopMargin() + (clipHeight / 2);

                int imageWidth = bitmap.getWidth();
                int imageHeight = bitmap.getHeight();
                // 按裁剪框求缩放比例
                float scale = (clipWidth * 1.0f) / imageWidth;
                if (imageWidth > imageHeight) {
                    scale = (clipHeight * 1.0f) / imageHeight;
                }

                // 起始中心点
                float imageMidX = imageWidth * scale / 2;
                float imageMidY = clipview.getCustomTopBarHeight()
                        + imageHeight * scale / 2;
                mSrcPic.setScaleType(ScaleType.MATRIX);

                // 缩放
                matrix.postScale(scale, scale);
                // 平移
                matrix.postTranslate(midX - imageMidX, midY - imageMidY);

                mSrcPic.setImageMatrix(matrix);
                mSrcPic.setImageBitmap(bitmap);
            }
        });
        mFrame.addView(clipview, new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//		this.addContentView(clipview, new LayoutParams(
//				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    public boolean onTouch(View v, MotionEvent event) {
        ImageView view = (ImageView) v;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                // 设置开始点位置
                start.set(event.getX(), event.getY());
                mode = DRAG;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                if (oldDist > 10f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - start.x, event.getY()
                            - start.y);
                } else if (mode == ZOOM) {
                    float newDist = spacing(event);
                    if (newDist > 10f) {
                        matrix.set(savedMatrix);
                        float scale = newDist / oldDist;
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                }
                break;
        }
        view.setImageMatrix(matrix);
        return true;
    }

    /**
     * 多点触控时，计算最先放下的两指距离
     *
     * @param event
     * @return
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * 多点触控时，计算最先放下的两指中心坐标
     *
     * @param point
     * @param event
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cut_image_back:
                if (mode == CameraActivity1.CHOOSE_IMAGE) {
                    finish();
                    return;
                }
                startActivity(ImageChooseActivity1.newIntent(mReleaseData,
                        edit_mode));
                finish();
                break;
            case R.id.cut_image_next:
                next();
                break;
            default:
                break;
        }

    }

    private void next() {
        Bitmap croppedBitmap = getBitmap();
        String picN = ComUtils.getRandomAlphamericStr(8) + ".jpg";
        File temp = new File(getCacheDir(), picN);
        String cropImagePath = temp.getAbsolutePath();
        File file = ImageUtil.saveBitMap2File(cropImagePath,
                croppedBitmap);

        //当是选择头像的时候,返回头像
        if (edit_mode == CameraActivity1.CHOOSE_IMAGE) {
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
                file.getAbsolutePath(), edit_mode);
        startActivity(intent);
        finish();
    }

    /**
     * 获取裁剪框内截图
     *
     * @return
     */
    private Bitmap getBitmap() {
        // 获取截屏
//		View view = this.getWindow().getDecorView();
        mFrame.setDrawingCacheEnabled(true);
        mFrame.buildDrawingCache();

        // 获取状态栏高度
//		Rect frame = new Rect();
//		this.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
//		int statusBarHeight = frame.top;

        Bitmap finalBitmap = Bitmap.createBitmap(mFrame.getDrawingCache(),
                clipview.getClipLeftMargin(), clipview.getClipTopMargin()
                        /*+ statusBarHeight*/, clipview.getClipWidth(),
                clipview.getClipHeight());

        // 释放资源
        mFrame.destroyDrawingCache();
        return finalBitmap;
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