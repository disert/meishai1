package com.meishai.ui.fragment.home.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.meishai.entiy.BaseRespData;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.ui.base.BaseActivity;

import java.util.List;

/**
 * 文件名：
 * 描    述：当现实的数据是简单的列表时抽取出来的基类,子类只需实现getConvertView方法即可
 * 作    者：yl
 * 时    间：2016/2/18
 * 版    权：
 */
public abstract class ListItemAdapter<T> extends BaseAdapter {

    private List<T> mData;
    private ImageLoader mImageLoader;

    public void setData(List<T> data, ImageLoader imageLoader) {
        mData = data;
        mImageLoader = imageLoader;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mData == null || mData.isEmpty()) {
            return 0;
        }
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getConvertView(position, convertView, parent, (T) getItem(position), mImageLoader);
    }

    public abstract View getConvertView(int position, View convertView, ViewGroup parent, T item, ImageLoader imageLoader);
}
