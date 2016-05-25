package com.meishai.app.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 老版本的圆角图片的view,该自定义view由于需要在ondrow方法中new两个bitmap这是相当占内存的操作,出于性能考虑不建议使用
 */
public class RoundCornerImageView1 extends ImageView {

    private final float density = getContext().getResources().getDisplayMetrics().density;
    private float roundness;
    private OnMeasureListener onMeasureListener;

    public RoundCornerImageView1(Context context) {
        super(context);
        init();
    }

    public RoundCornerImageView1(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RoundCornerImageView1(Context context, AttributeSet attrs,
                                 int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    public void draw(Canvas canvas) {
        final Bitmap composedBitmap;
        final Bitmap originalBitmap;
        final Canvas composedCanvas;
        final Canvas originalCanvas;
        final Paint paint;
        final int height;
        final int width;
        width = getWidth();
        height = getHeight();
        //两个画布,两个位图,一个交给父类去画,另一个自己画框
        composedBitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        originalBitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        composedCanvas = new Canvas(composedBitmap);
        originalCanvas = new Canvas(originalBitmap);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        super.draw(originalCanvas);
        composedCanvas.drawARGB(0, 0, 0, 0);
        composedCanvas.drawRoundRect(new RectF(0, 0, width, height),
                this.roundness, this.roundness, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        composedCanvas.drawBitmap(originalBitmap, 0, 0, paint);
        canvas.drawBitmap(composedBitmap, 0, 0, new Paint());
        composedBitmap.recycle();
        originalBitmap.recycle();
    }

    public float getRoundness() {
        return this.roundness / this.density;
    }

    public void setRoundness(float roundness) {
        this.roundness = roundness * this.density;
    }

    private void init() {
        setRoundness(5);
    }

    public void setOnMeasureListener(OnMeasureListener onMeasureListener) {
        this.onMeasureListener = onMeasureListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 将图片测量的大小回调到onMeasureSize()方法中
        if (onMeasureListener != null) {
            onMeasureListener.onMeasureSize(getMeasuredWidth(),
                    getMeasuredHeight());
        }
    }

    public interface OnMeasureListener {

        public void onMeasureSize(int width, int height);
    }
}

