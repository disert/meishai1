
package com.meishai.ui.fragment.camera;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore.MediaColumns;

import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;
import com.meishai.util.ImageUtil;

/**
 * Tools for handler picture
 */
public final class BitmapUtils {

    private static final String TAG = BitmapUtils.class.getSimpleName();

    /**
     * 压缩和旋转角度
     *
     * @param sourcePath
     * @param targetPath
     * @param maxSize
     */
    public static void compressBitmap(String sourcePath, String targetPath, float maxSize) {

        int degree = AndroidUtil.readPictureDegree(sourcePath);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(sourcePath, options);

        final float originalWidth = options.outWidth;
        final float originalHeight = options.outHeight;

        float convertedWidth;
        float convertedHeight;

        if (originalWidth > originalHeight) {
            convertedWidth = maxSize;
            convertedHeight = maxSize / originalWidth * originalHeight;
        } else {
            convertedHeight = maxSize;
            convertedWidth = maxSize / originalHeight * originalWidth;
        }


        final float ratio = originalWidth / convertedWidth;

        options.inSampleSize = (int) ratio;
        options.inJustDecodeBounds = false;

        Bitmap convertedBitmap = BitmapFactory.decodeFile(sourcePath, options);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        convertedBitmap.compress(Bitmap.CompressFormat.JPEG, 60, byteArrayOutputStream);
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(new File(targetPath));
            fileOutputStream.write(byteArrayOutputStream.toByteArray());
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (0 != degree) {
            Bitmap rotateBitmap = ImageUtil.rotateBitmap(targetPath, degree);
            ImageUtil.saveBitMap(targetPath, rotateBitmap);
        }
    }

    /**
     * 压缩
     *
     * @param sourcePath
     * @param targetPath
     * @param maxSize
     */
    public static void compress(String sourcePath, String targetPath, float maxSize) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(sourcePath, options);

        final float originalWidth = options.outWidth;
        final float originalHeight = options.outHeight;

        float convertedWidth;
        float convertedHeight;

        if (originalWidth > originalHeight) {
            convertedWidth = maxSize;
            convertedHeight = maxSize / originalWidth * originalHeight;
        } else {
            convertedHeight = maxSize;
            convertedWidth = maxSize / originalHeight * originalWidth;
        }


        final float ratio = originalWidth / convertedWidth;

        options.inSampleSize = (int) ratio;
        options.inJustDecodeBounds = false;

        Bitmap convertedBitmap = BitmapFactory.decodeFile(sourcePath, options);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        convertedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(new File(targetPath));
            fileOutputStream.write(byteArrayOutputStream.toByteArray());
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 对图片进行质量压缩
     *
     * @param image
     * @return
     */
    public static void compressImage(String targetPath, Bitmap image) {
        if (image == null) {
            DebugLog.w("image 为 null");
            return;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        DebugLog.w("原图片大小:" + baos.toByteArray().length);
        int options = 100;
        while (baos.toByteArray().length / 1024 > 200) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            options -= 2;//每次都减少2
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            long length = baos.toByteArray().length;
            DebugLog.w("压缩率:" + options + "%," + "图片大小:" + length);
        }
//        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中

        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(new File(targetPath));
            fileOutputStream.write(baos.toByteArray());
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片  
//        return bitmap;  
    }

    /**
     * 根据URI获取图片物理路径
     */
    public static String getAbsoluteImagePath(Uri uri, Activity activity) {

        String[] proj = {
                MediaColumns.DATA
        };
        Cursor cursor = activity.managedQuery(uri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    /**
     * @param path
     * @param maxSize
     * @return
     */
    public static Bitmap decodeBitmap(String path, int maxSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(path, options);

        final int originalWidth = options.outWidth;
        final int originalHeight = options.outHeight;

        int convertedWidth;
        int convertedHeight;

        if (originalWidth > originalHeight) {
            convertedWidth = maxSize;
            convertedHeight = maxSize / originalWidth * originalHeight;
        } else {
            convertedHeight = maxSize;
            convertedWidth = maxSize / originalHeight * originalWidth;
        }

        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inSampleSize = computeSampleSize(options, maxSize, convertedWidth * convertedHeight);

        Bitmap convertedBitmap = BitmapFactory.decodeFile(path, options);

        if (convertedBitmap != null) {
            final int realWidth = convertedBitmap.getWidth();
            final int realHeight = convertedBitmap.getHeight();

        }

        return convertedBitmap;
    }

    /**
     * @param path
     * @param maxWidth
     * @param maxHeight
     * @return
     */
    public static Bitmap decodeBitmap(String path, int maxWidth, int maxHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(path, options);

        final int originalWidth = options.outWidth;
        final int originalHeight = options.outHeight;

        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inSampleSize = computeSampleSize(options, maxWidth, maxWidth * maxHeight);

        Bitmap convertedBitmap = BitmapFactory.decodeFile(path, options);

        if (convertedBitmap != null) {
            final int realWidth = convertedBitmap.getWidth();
            final int realHeight = convertedBitmap.getHeight();

        }

        return convertedBitmap;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {

        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? minSideLength
                : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    private static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {

        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }


    /**
     * 生成8位16进制的缓存因子：规则的8位哈希码，不足前面补零
     *
     * @param string
     * @return
     */
    public static String toRegularHashCode(String string) {
        final String hexHashCode = Integer.toHexString(string.hashCode());
        final StringBuilder stringBuilder = new StringBuilder(hexHashCode);
        while (stringBuilder.length() < 8) {
            stringBuilder.insert(0, '0');
        }
        return stringBuilder.toString();
    }
}
