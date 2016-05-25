package com.meishai.util;

import com.meishai.GlobalContext;

import android.util.DisplayMetrics;

public class SizeAdapterUtils {

    /**
     * 按比例收缩或拉伸图片,使图片的高度等于屏幕的高度
     *
     * @param size 存放图片宽高的数据类
     * @return 返回按照屏幕宽度按比例拉伸后的图片的宽高
     */
    public static Size adapteSize(Size size) {
        double adapteHeight = 0;
        double adapteWidth = 0;
        // 获取手机的尺寸
        DisplayMetrics dm = GlobalContext.getInstance().getResources()
                .getDisplayMetrics();
        double screenWidth = dm.widthPixels;
        double ratio = screenWidth / size.width;
        adapteWidth = size.width * ratio;
        adapteHeight = size.height * ratio;

        return new Size(adapteWidth, adapteHeight);
    }

    public static class Size {
        public Size(double adapteWidth, double adapteHeight) {
        }

        public double width;
        public double height;
    }

}
