package com.meishai.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.meishai.ui.constant.ConstantSet;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


/**
 * 文件名：LoadImageTask
 * 描    述：加载图片的任务类
 * 作    者：yl
 * 时    间：2015/12/30
 * 版    权：
 */
public class LoadImageTask extends AsyncTask<Void, Void, Bitmap> {
    private Activity activity = null;

    private ImageView mImageView;
    private String path;
    private BitmapFactory.Options options;
    private int count;//一行显示图片的数量
    private int mScreenWidth;

    public LoadImageTask(Activity activity, String path, ImageView imageView) {
        this(activity, path, imageView, 1);
    }

    public LoadImageTask(Activity activity, String path, ImageView imageView, int count) {
        this.path = path;
        this.activity = activity;
        this.mImageView = imageView;
        this.count = count;
        mScreenWidth = activity.getWindowManager().getDefaultDisplay().getWidth();
    }

    @Override
    protected void onPreExecute() {
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(path);
            int size = mScreenWidth / count;
            options = new BitmapFactory.Options();
            int bitWidth = exif.getAttributeInt(
                    ExifInterface.TAG_IMAGE_WIDTH, 0);
            int bitHeight = exif.getAttributeInt(
                    ExifInterface.TAG_IMAGE_LENGTH, 0);
            int sampleSize = 0;
            if (bitWidth > bitHeight) {
                sampleSize = bitHeight / size;
            } else if (bitHeight > bitWidth) {
                sampleSize = bitWidth / size;
            }
            // 内存优化
            options.inSampleSize = sampleSize;// 只加载缩略图
            options.inPreferredConfig = Bitmap.Config.ARGB_4444;// 降低图片的质量
            options.inPurgeable = true;// 当内存不足时,可以被回收
            mImageView.setImageBitmap(ConstantSet.defaultBitmap);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Bitmap doInBackground(Void... params) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(
                    new FileInputStream(path), null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        if (result != null) {
            mImageView.setImageBitmap(result);
        }
    }
}
