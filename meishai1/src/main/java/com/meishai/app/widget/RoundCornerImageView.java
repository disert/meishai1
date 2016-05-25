package com.meishai.app.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 显示圆角图片的自定义imageview,在使用时不能为其设置padding值,否则会失效,可通过setRoundWidthHight方法设置图片的圆角大小
 * <p/>
 * 虽然已经对其进行了优化,但是仍然会很耗内存,所以可以偶尔嵌套,不建议经常使用,更好的方式是使用背景图片在做圆角
 */
public class RoundCornerImageView extends ImageView {

    private final float density = getContext().getResources().getDisplayMetrics().density;
    private float roundness;

    public RoundCornerImageView(Context context) {
        super(context);
        init();
    }

    public RoundCornerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RoundCornerImageView(Context context, AttributeSet attrs,
                                int defStyle) {
        super(context, attrs, defStyle);
        init();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas2 = new Canvas(bitmap);
        super.onDraw(canvas2);
        drawLiftUp(canvas2);
        drawRightUp(canvas2);
        drawLiftDown(canvas2);
        drawRightDown(canvas2);
        canvas.drawBitmap(bitmap, 0, 0, paint2);
        bitmap.recycle();

    }

    public int getRoundWidth() {
        return roundWidth;
    }

    /**
     * 设置圆角的宽高
     */
    public void setRoundWidthHight(int round) {
        this.roundWidth = round;
        this.roundHeight = round;
        invalidate();
    }

    public int getRoundHeight() {
        return roundHeight;
    }

    public void setRoundHeight(int roundHeight) {
        this.roundHeight = roundHeight;
    }

    //TODO 设置圆角
    private int roundWidth = 7;
    private int roundHeight = 7;
    private Paint paint;
    private Paint paint2;

    private void drawLiftUp(Canvas canvas) {
        Path path = new Path();
        path.moveTo(0, roundHeight);
        path.lineTo(0, 0);
        path.lineTo(roundWidth, 0);
        path.arcTo(new RectF(
                        0,
                        0,
                        roundWidth * 2,
                        roundHeight * 2),
                -90,
                -90);
        path.close();
        canvas.drawPath(path, paint);
    }

    private void drawLiftDown(Canvas canvas) {
        Path path = new Path();
        path.moveTo(0, getHeight() - roundHeight);
        path.lineTo(0, getHeight());
        path.lineTo(roundWidth, getHeight());
        path.arcTo(new RectF(
                        0,
                        getHeight() - roundHeight * 2,
                        0 + roundWidth * 2,
                        getHeight()),
                90,
                90);
        path.close();
        canvas.drawPath(path, paint);
    }

    private void drawRightDown(Canvas canvas) {
        Path path = new Path();
        path.moveTo(getWidth() - roundWidth, getHeight());
        path.lineTo(getWidth(), getHeight());
        path.lineTo(getWidth(), getHeight() - roundHeight);
        path.arcTo(new RectF(
                getWidth() - roundWidth * 2,
                getHeight() - roundHeight * 2,
                getWidth(),
                getHeight()), 0, 90);
        path.close();
        canvas.drawPath(path, paint);
    }

    private void drawRightUp(Canvas canvas) {
        Path path = new Path();
        path.moveTo(getWidth(), roundHeight);
        path.lineTo(getWidth(), 0);
        path.lineTo(getWidth() - roundWidth, 0);
        path.arcTo(new RectF(
                        getWidth() - roundWidth * 2,
                        0,
                        getWidth(),
                        0 + roundHeight * 2),
                -90,
                90);
        path.close();
        canvas.drawPath(path, paint);
    }

    public float getRoundness() {
        return this.roundness / this.density;
    }

    public void setRoundness(float roundness) {
        this.roundness = roundness * this.density;
    }

    private void init() {
//        setRoundness(5);

        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));

        paint2 = new Paint();
        paint2.setXfermode(null);
    }


}
