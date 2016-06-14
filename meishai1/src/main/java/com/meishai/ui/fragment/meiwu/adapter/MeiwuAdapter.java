package com.meishai.ui.fragment.meiwu.adapter;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.widget.BaseAdapter;

import com.meishai.net.volley.toolbox.ImageLoader;

/**
 * 文件名：MeiwuAdapter
 * 描    述：美物的适配器,在内部,根据cid的不同会调用不同的适配器
 * 作    者：
 * 时    间：2016/1/20
 * 版    权：
 */
public abstract class MeiwuAdapter extends BaseAdapter {

    protected final int mScreenWidth;
    protected Activity mContext;
    protected ImageLoader mImageLoader;

    public MeiwuAdapter(Activity context, ImageLoader imageLoader) {
        mContext = context;
        mImageLoader = imageLoader;
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels;
    }

    public void setData(String datas) {
        initData(datas);
        notifyDataSetChanged();
    }

    public int getPage(){
        return 0;
    }
    public String getTitle(){
        return "";
    }
    public boolean hasPage(){
        return false;
    }

    protected abstract void initData(String data);
}
