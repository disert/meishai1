package com.meishai.util;

import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.meishai.GlobalContext;

/**
 * Created by Administrator on 2015/12/3.
 * <p/>
 * 进行屏幕适配的工具类
 */
public class ImageAdapter {

    /**
     * 根据图片的宽高以及图片所需要显示的宽度来计算imageview的在屏幕中的大小
     *
     * @param imageView   需要设置大小的图片控件
     * @param width       希望imageview显示的宽度
     * @param imageWidth  图片的宽度
     * @param imageHeight 图片的高度
     */
    public static void initImageView(ImageView imageView, double width, int imageWidth, int imageHeight) {
        double widthScale = width / imageWidth;

        int height = (int) (widthScale * imageHeight);

        if (imageView.getParent() instanceof LinearLayout) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int) (widthScale * imageWidth), height);
            layoutParams.setMargins(0, AndroidUtil.dip2px(5), 0, 0);
            DebugLog.w("走的是LinearLayout");
            imageView.setLayoutParams(layoutParams);
        } else {
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams((int) (widthScale * imageWidth), height);
            DebugLog.w("走的是ViewGroup");
            imageView.setLayoutParams(lp);
        }
    }

    /**
     * 获取imageview在屏幕中的实际的宽度和高度
     *
     * @param lineCount    一行要显示几个view
     * @param rightPadding view离右边一个view的距离,view的父类应该自己设置一个左边距
     * @param imageWidth   图片的真实宽度
     * @param imageHeight  图片的真实高度
     * @return
     */
    public static Point getViewRealWH(int lineCount, int rightPadding, double imageWidth, double imageHeight) {
        //获得屏幕宽度
        DisplayMetrics dm = GlobalContext.getInstance().getApplicationContext().getResources().getDisplayMetrics();
        double screenWidth = dm.widthPixels;
        //计算view在屏幕中的宽度
        double viewWidht = (screenWidth - rightPadding * (lineCount + 1)) / lineCount;
        //计算图片高度与宽度的比例
        double ratio = imageWidth / imageHeight;
        //计算view在屏幕中的高度
        double viewHeight = viewWidht / ratio;

//        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams((int)(viewWidht+rightPadding),(int)viewHeight);
//        imageView.setLayoutParams(layoutParams);
//        imageView.setPadding(0,0,rightPadding,0);
        Point point = new Point();
        point.x = (int) viewWidht;
        point.y = (int) viewHeight;
        return point;
    }

    public static int getViewRealWH(int lineCount, int rightPadding) {
        //获得屏幕宽度
        DisplayMetrics dm = GlobalContext.getInstance().getApplicationContext().getResources().getDisplayMetrics();
        double screenWidth = dm.widthPixels;
        //计算view在屏幕中的宽度
        int viewWidht = (int) ((screenWidth - rightPadding * (lineCount + 1)) / lineCount);

        return viewWidht;

    }


}
