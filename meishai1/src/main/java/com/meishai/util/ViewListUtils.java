package com.meishai.util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.meishai.entiy.StratDetailRespData;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.NetworkImageView;

import java.util.List;

/**
 * 文件名：ViewListUtils
 * 描    述：生成view列表的工具类
 * 作    者：
 * 时    间：2015/12/28
 * 版    权：
 */
public class ViewListUtils {

    /**
     * 为组件填充默认的imageview
     *
     * @param group       需要被填充的组件
     * @param pics        图片的数组
     * @param context
     * @param imageLoader
     */
    public static void addImageViews(Context context, ViewGroup group, List<StratDetailRespData.Picture> pics, ImageLoader imageLoader) {
        if (pics == null || pics.isEmpty()) {
            return;
        }
        group.removeAllViews();


        ViewGroup.LayoutParams layoutParams = null;
        if (group instanceof LinearLayout) {
            layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        } else if (group instanceof FrameLayout) {
            layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        } else if (group instanceof RelativeLayout) {
            layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        } else if (group instanceof AbsListView) {
            layoutParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        } else {
            throw new RuntimeException("暂时不支持该组件的添加!");
        }


        for (int i = 0; i < pics.size(); i++) {
            NetworkImageView imageView = new NetworkImageView(context);
            imageView.setLayoutParams(layoutParams);
            imageView.setImageUrl(pics.get(i).pic_path, imageLoader);
            group.addView(imageView);
        }
    }

//    public static List<View> generateViews(List datas){
//        List<View>
//    }

}
