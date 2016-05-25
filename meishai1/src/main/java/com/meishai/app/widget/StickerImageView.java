package com.meishai.app.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

public class StickerImageView extends ImageView {

    private Bitmap mOperaBitmap;// 贴纸图片的copy对象,用于进行操作的
    private int operaHeight;
    private int operaWidth;
    private Bitmap mStickerBitmap;// 贴纸的图片
    private int stickerHeight; // 贴纸图片的高度
    private int stickerWidth; // 贴纸图片的宽度

    private int mHeight; // 控件自身的高度
    private int mWidth; // 控件自身的宽度

    private float startX; // 按下的点
    private float startY;//
    private Paint paint = new Paint();

    private boolean isShowSticker;// 是否显示贴纸
    private boolean isInit;// 控件的宽高是否出售

    private float stickerTop; // 贴纸的左上角的位置
    private float stickerLeft;

    private boolean isMove = false;// 是不是移动
    private boolean isScale = false;// 是否是拉伸
    private boolean isRotate = false;// 是否是旋转
    private boolean isDelete = false;// 是否是删除

    private float operationX;// 操作点的圆心位置
    private float operationY;

    private float deleteX;// 删除点的圆心位置
    private float deleteY;

    private int operatSize = 20;// 操作点的
    private int deleteSize = 20;// 删除点的半径
    private long downTime;
    private double moveX;
    private double moveY;

    private float maxStickerWidth = 200;

    public boolean isShowSticker() {
        return isShowSticker;
    }

    public void setShowSticker(boolean isShowSticker) {
        this.isShowSticker = isShowSticker;
        if (isShowSticker) {
            isMove = false;
            isScale = false;
            isDelete = false;
            isInit = false;
        }
    }

    public StickerImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public StickerImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StickerImageView(Context context) {
        this(context, null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isInit) {
            init();
        }

        if (mStickerBitmap != null && isShowSticker) {

            if (isDelete) {
                delete();
                return;
            } else if (isScale) {
                scale();
            } else if (isRotate) {
                rotate();
            } else if (isMove) {
                move();
            }
            // 删除点
            paint.setColor(0xff555555);
            canvas.drawCircle(deleteX, deleteY, deleteSize, paint);
            // 操作点
            paint.setColor(0xffeeeeee);
            canvas.drawCircle(operationX, operationY, operatSize, paint);
            // 画边框
            canvas.drawLine(stickerLeft, stickerTop,
                    stickerLeft + operaWidth, stickerTop, paint);
            canvas.drawLine(stickerLeft + operaWidth, stickerTop,
                    stickerLeft + operaWidth, stickerTop + operaHeight, paint);
            canvas.drawLine(stickerLeft + operaWidth, stickerTop + operaHeight,
                    stickerLeft, stickerTop + operaHeight, paint);
            canvas.drawLine(stickerLeft, stickerTop + operaHeight,
                    stickerLeft, stickerTop, paint);
            // 贴纸
            canvas.drawBitmap(mOperaBitmap, stickerLeft, stickerTop, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();
        // Log.w("MainActivity", "当前动作:" + action);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                downTime = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isShowSticker) {
                    return false;
                }
                float currentX = event.getX();
                float currentY = event.getY();
                moveX = currentX - startX;
                moveY = currentY - startY;

                if (isOperation(startX, startY)) {// 操作贴纸
                    setOperatState(currentX, currentY);
                } else if (isTouchSticker(startX, startY)) {// 移动贴纸
                    isDelete = false;
                    isScale = false;
                    isMove = true;
                    if (isMove(moveX, moveY)) {
                        setMoveState();
                        // 重置起点
                        startX = currentX;
                        startY = currentY;
                    }
                } else {
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isDelete(startX, startY)) {
                    // 隐藏贴纸
                    long upTime = System.currentTimeMillis();
                    if ((upTime - downTime) < 1000) {
                        float upX = event.getX();
                        float upY = event.getY();
                        double absX = Math.abs(startX - upX);
                        double absY = Math.abs(startY - upY);
                        if ((absX + absY) < 15) {
                            setDeleteState();
                        }
                    }
                }
                break;

            default:
                break;
        }
        invalidate();
        return true;
    }

    //  旋转和缩放挺难搞的,该view也弃用
    public void setOperatState(double x, double y) {
        double moveX = x - startX;
        double moveY = y - startY;
        if (Math.abs(moveX) > 0) {
            setScaleState();
        } else if (Math.abs(moveY) > 0) {
            setRotateState();
        }

    }

    public void setSticker(Bitmap bitmap) {
        mStickerBitmap = bitmap;
        stickerHeight = bitmap.getHeight();
        stickerWidth = bitmap.getWidth();
        // 复制一个附件
        Matrix matrix = new Matrix();
        matrix.postScale(maxStickerWidth / stickerWidth, maxStickerWidth
                / stickerWidth); // 长和宽放大缩小的比例
        mOperaBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
        operaHeight = mOperaBitmap.getHeight();
        operaWidth = mOperaBitmap.getWidth();

        isInit = false;

        invalidate();
    }

    /**
     * 触屏的是否是贴纸
     *
     * @param x
     * @param y
     * @return
     */
    public boolean isTouchSticker(double x, double y) {

        if (x <= stickerLeft + stickerWidth && x >= stickerLeft
                && y <= stickerTop + stickerHeight && y >= stickerTop) {
            return true;
        }
        return false;
    }

    /**
     * 是否执行移动
     *
     * @param moveX 要移动的X坐标的偏移量
     * @param moveY 要移动的Y坐标的偏移量
     * @return
     */
    public boolean isMove(double moveX, double moveY) {
        if (moveX < 0) {// 左移
            if ((stickerLeft + moveX) < 0) {
                return false;
            }
        } else if (moveX > 0) {// 右移
            if ((stickerLeft + stickerWidth + moveX) > mWidth) {
                return false;
            }
        } else if (moveY < 0) {// 上移
            if ((stickerTop + moveY) < 0) {
                return false;
            }

        } else if (moveY > 0) {// 下移

            if ((stickerTop + stickerHeight + moveY) > mHeight) {
                return false;
            }
        }
        return true;
    }

    /**
     * 点击的是否是删除点
     *
     * @param x 点击位置的x坐标
     * @param y 点击位置的Y坐标
     * @return
     */
    public boolean isDelete(double x, double y) {
        double absX = Math.abs(deleteX - x);
        double absY = Math.abs(deleteY - y);

        if (absX <= deleteSize && absY <= deleteSize) {
            return true;
        }

        return false;
    }

    /**
     * 点击的是否是操作点
     *
     * @param x 点击位置的x坐标
     * @param y 点击位置的Y坐标
     * @return
     */
    public boolean isOperation(double x, double y) {
        double absX = Math.abs(operationX - x);
        double absY = Math.abs(operationY - y);

        if (absX <= deleteSize && absY <= deleteSize) {
            return true;
        }

        return false;
    }

    public void setRotateState() {
        isDelete = false;
        isMove = false;
        isRotate = true;
        isScale = false;
    }

    public void setScaleState() {
        isDelete = false;
        isMove = false;
        isRotate = false;
        isScale = true;
    }

    public void setMoveState() {
        isDelete = false;
        isMove = true;
        isRotate = false;
        isScale = false;

    }

    public void setDeleteState() {
        isDelete = true;
        isScale = false;
        isRotate = false;
        isMove = false;
    }

    public void init() {
        mHeight = getMeasuredHeight();
        mWidth = getMeasuredWidth();

        stickerTop = mHeight / 2 - operaHeight / 2;
        stickerLeft = mWidth / 2 - operaWidth / 2;

        operationX = stickerLeft + operaWidth;
        operationY = stickerTop + operaHeight;

        deleteX = stickerLeft;
        deleteY = stickerTop;

        isInit = true;
    }

    public void move() {
        stickerTop += moveY;// 设置顶点的位置,实现移动
        stickerLeft += moveX;

        operationX += moveX;
        operationY += moveY;

        deleteX = stickerLeft;
        deleteY = stickerTop;
    }

    public void scale() {
        // 2.创建一个画板,把位图交给画板
        Canvas canvas = new Canvas(mOperaBitmap);
        // // 3.创建一个画笔
        Paint paint = new Paint();
        // // 4.定义一个画图的规则
        Matrix matrix = new Matrix();// 1:1照着画
        // // 5.设置规则
        float sx = (float) (1 + moveX / operaWidth);
        float sy = (float) (1 + moveY / operaHeight);
        matrix.setScale(sx, sy);

        // // 6.在画板中使用指定的位图,规则和画笔来画原来设置好的空位图.
        canvas.drawBitmap(mStickerBitmap, matrix, paint);
        canvas.save();
        operaWidth += moveX;
        operaHeight += moveY;

        operationX += moveX;
        operationY += moveY;


    }

    public void rotate() {

    }

    public void delete() {
        isShowSticker = false;
        isInit = false;
        if (mStickerBitmap != null && !mStickerBitmap.isRecycled()) {
            // 回收并且置为null
            mStickerBitmap.recycle();
            mStickerBitmap = null;
        }
        if (mOperaBitmap != null && !mOperaBitmap.isRecycled()) {
            // 回收并且置为null
            mOperaBitmap.recycle();
            mOperaBitmap = null;
        }

    }

}
