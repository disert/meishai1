package com.meishai.util;

import java.io.File;
import java.io.FileOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.meishai.GlobalContext;
import com.meishai.ui.constant.ConstantSet;

public class ImageUtil {

    /**
     * 放大缩小
     *
     * @param bitmap
     * @param w
     * @param h
     * @return
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidht = ((float) w / width);
        float scaleHeight = ((float) h / height);
        matrix.postScale(scaleWidht, scaleHeight);
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        return newbmp;
    }

    //将Drawable转化为Bitmap
    public static Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

    //获得圆角图片的方法
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    //获得带倒影的图片方法
    public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {
        final int reflectionGap = 4;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);
        Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, height / 2, width, height / 2, matrix, false);
        Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + height / 2), Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapWithReflection);
        canvas.drawBitmap(bitmap, 0, 0, null);
        Paint deafalutPaint = new Paint();
        canvas.drawRect(0, height, width, height + reflectionGap, deafalutPaint);
        canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);
        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0, bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.CLAMP);
        paint.setShader(shader);
        // Set the Transfer mode to be porter duff and destination in
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        // Draw a rectangle using the paint with our linear gradient
        canvas.drawRect(0, height, width, bitmapWithReflection.getHeight() + reflectionGap, paint);
        return bitmapWithReflection;
    }

    /**
     * 图片旋转
     *
     * @param degree ： 图片被系统旋转的角度
     * @param path   ： 需纠正方向的图片
     * @return 纠向后的图片
     */
    public static Bitmap rotateBitmap(String path, int degree) {
        Bitmap bmp = NativeImageLoader.getInstance().decodeThumbBitmapForFile(path, 0, ConstantSet.SCREEN_HEIGHT);
        Matrix matrixs = new Matrix();
        matrixs.setRotate(degree);
        bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrixs, true);
        return bmp;
    }

    /**
     * 根据Uri获取图片
     *
     * @param ctx
     * @param uri
     * @return
     */
    public static Bitmap getBitmapFromUri(Context ctx, Uri uri) {
        try {
            // 读取uri所在的图片
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(ctx.getContentResolver(), uri);
            return bitmap;
        } catch (Exception e) {
            Log.e("[Android]", e.getMessage());
            Log.e("[Android]", "目录为：" + uri);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 读取SDCard图片
     *
     * @return Bitmap
     */
    public static Bitmap getImageFromSDCard(String filepath) {
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
            DebugLog.d("inSampleSize:" + options.inSampleSize + ",screenWidth:" + ConstantSet.SCREEN_WIDTH + ",imageWidth:" + options.outWidth);
            options.inJustDecodeBounds = false;
            Bitmap bm = BitmapFactory.decodeFile(filepath, options);
            return bm;
        }
        return null;
    }

    /**
     * 保存bitmap到指定文件
     *
     * @param path
     * @param bitmap
     * @return
     */
    public static File saveBitMap2File(String path, Bitmap bitmap) {
        int posIndex = path.lastIndexOf(".");
        String prefix = path.substring(0, posIndex);
        String newPath = prefix + "_temp" + path.substring(posIndex);
        File f = new File(newPath);
        return saveBitMap2File(f, bitmap);
    }

    public static File saveBitMap2FileJpg(File f, Bitmap bitmap) {
        try {
            //			if(f.exists()){
            //				f.delete();
            //			}
            FileOutputStream outputStream = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return f;
    }

    /**
     * 保存bitmap到指定文件
     *
     * @param f
     * @param bitmap
     * @return
     */
    public static File saveBitMap2File(File f, Bitmap bitmap) {
        try {
            //			if(f.exists()){
            //				f.delete();
            //			}
            FileOutputStream outputStream = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return f;
    }


    /**
     * 保存bitmap到指定文件路径
     *
     * @param path
     * @param bitmap
     */
    public static void saveBitMap(String path, Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }
        try {
            File f = new File(path);
            if (f.exists()) {
                f.delete();
            }
            FileOutputStream outputStream = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            DebugLog.w("保存图片的本地成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 释放Bitmap的内存
     */
    public static void releaseDrawable(Drawable drawable) {
        if (drawable == null)
            return;
        if (drawable.getClass() == BitmapDrawable.class) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            //			if (bitmapDrawable.getBitmap().isRecycled()) {
            //				bitmapDrawable.getBitmap().recycle();
            //			}
            bitmapDrawable.setCallback(null);
            bitmapDrawable = null;
        }
        drawable.setCallback(null);
        drawable = null;
    }
}
