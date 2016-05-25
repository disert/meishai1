package com.meishai.ui.fragment.usercenter.adapter;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.meishai.app.widget.layout.HomePageItem;
import com.meishai.entiy.HomePageDatas;
import com.meishai.entiy.TopicRespData;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.util.AndroidUtil;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Administrator on 2015/11/26.
 * 我的收藏-美晒的适配器
 */
public class CollectedPostAdapter extends BaseAdapter {

    private final Activity mContext;
    private final ImageLoader mImageLoader;
    private TopicRespData mData;
    private List<HomePageDatas.PostInfo> item;

    public CollectedPostAdapter(Activity activity, ImageLoader imageLoader) {
        mContext = activity;
        mImageLoader = imageLoader;
        item = new ArrayList<HomePageDatas.PostInfo>();
    }

    @Override
    public int getCount() {
        if (mData == null || mData.list == null || mData.list.isEmpty()) {
            return 0;
        }
        return (mData.list.size() + 1) / 2;
    }

    @Override
    public Object getItem(int position) {
        int startPosition;
        if (position == 0) {
            startPosition = 0;
        } else {
            startPosition = position * 2;
        }
        int endPosition = startPosition + 1;
        item.clear();
        item.add(mData.list.get(startPosition));
        if (endPosition < mData.list.size()) {
            item.add(mData.list.get(endPosition));
        }

        return item;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Object obj = getItem(position);
        if (convertView == null) {

            convertView = new HomePageItem(mContext);
        }
        List<HomePageDatas.PostInfo> item1 = (List<HomePageDatas.PostInfo>) obj;
        if (item1.size() == 2) {
            ((HomePageItem) convertView).setData(item1.get(0), item1.get(1), mImageLoader);
        } else {
            ((HomePageItem) convertView).setData(item1.get(0), null, mImageLoader);
        }
        return convertView;
    }

    public synchronized void setData(TopicRespData datas) {
        mData = datas;
        notifyDataSetChanged();
    }

    public void addData(List<HomePageDatas.PostInfo> datas) {
        if (datas == null) {
            AndroidUtil.showToast("没有更多数据了!");
            return;
        }
        mData.list.addAll(datas);
        notifyDataSetChanged();
    }
}
