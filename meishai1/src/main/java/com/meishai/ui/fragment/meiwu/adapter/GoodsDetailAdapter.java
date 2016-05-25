package com.meishai.ui.fragment.meiwu.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.meishai.app.widget.layout.GoodsDetailHeader;
import com.meishai.app.widget.layout.GoodsDetailItem;
import com.meishai.app.widget.layout.ListImageView;
import com.meishai.entiy.GoodsDetailRespData;
import com.meishai.net.volley.toolbox.ImageLoader;

/**
 * 文件名：GoodsDetailAdapter
 * 描    述：商品详情对应的适配器
 * 作    者：yl
 * 时    间：2015/12/28
 * 版    权：
 */
public class GoodsDetailAdapter extends BaseAdapter {

    private Context mContext;
    private ImageLoader mImageLoader;
    private GoodsDetailRespData mData;

    public GoodsDetailAdapter(Context context, ImageLoader imageLoader) {
        mContext = context;
        mImageLoader = imageLoader;
    }

    public void setData(GoodsDetailRespData data) {
        mData = data;
        notifyDataSetInvalidated();
    }

    @Override
    public int getCount() {
        if (mData != null) {
            return 3;
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (position == 0) {
            return mData;
        } else if (position == 1) {
            return mData.topic_list;
        } else {
            return mData.item_list;
        }

    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        } else if (position == 1) {
            return 1;
        } else {
            return 2;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position == 0) {
            if (convertView == null) {
                convertView = new GoodsDetailHeader(mContext);
            }
            ((GoodsDetailHeader) convertView).setData(mData, mImageLoader);
        } else if (position == 1) {
            if (convertView == null) {
                convertView = new ListImageView(mContext);
            }
            ((ListImageView) convertView).setData(mData.topic_list, mImageLoader);
        } else {
            if (convertView == null) {
                convertView = new GoodsDetailItem(mContext);
            }
            ((GoodsDetailItem) convertView).setData(mData.item_list, mImageLoader);
        }
        return convertView;
    }
}
