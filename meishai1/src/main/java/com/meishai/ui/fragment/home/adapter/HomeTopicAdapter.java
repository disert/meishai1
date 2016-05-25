package com.meishai.ui.fragment.home.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.meishai.app.widget.layout.HomePageHeader;
import com.meishai.app.widget.layout.HomePageItem;
import com.meishai.app.widget.layout.HomeTopicHeader;
import com.meishai.entiy.HomePageDatas;
import com.meishai.entiy.HomePageDatas.PostInfo;
import com.meishai.entiy.TopicRespData;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.util.AndroidUtil;

public class HomeTopicAdapter extends BaseAdapter {

    private final int mTid;
    private Context mContext;
    private ImageLoader mImageLoader;
    private TopicRespData mDatas;
    private final int TYPE_HEADER = 0;
    private final int TYPE_ITEM = 1;

    private List<PostInfo> item;

    public HomeTopicAdapter(Context context, ImageLoader imageLoader, int tid) {
        mContext = context;
        mImageLoader = imageLoader;
        mTid = tid;
        item = new ArrayList<PostInfo>();
    }

    public synchronized void setData(TopicRespData datas) {
        mDatas = datas;
        notifyDataSetChanged();
    }

    public void addData(List<HomePageDatas.PostInfo> datas) {
        if (datas == null) {
            AndroidUtil.showToast("没有更多数据了!");
            return;
        }
        mDatas.list.addAll(datas);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mDatas == null) {
            return 0;
        } else if ((mDatas.list == null || mDatas.list.isEmpty()) && mDatas.topicdata1 != null) {
            return 1;
        }
        return (mDatas.list.size() + 1) / 2 + 1;
    }

    @Override
    public Object getItem(int position) {
        if (position == 0) {
            return mDatas.topicdata1;
        } else {
            int startPosition = (position - 1) * 2;
            int endPosition = startPosition + 1;
            item.clear();
            item.add(mDatas.list.get(startPosition));
            if (endPosition < mDatas.list.size()) {
                item.add(mDatas.list.get(endPosition));
            }

            return item;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        Object obj = getItem(position);
        if (type == TYPE_HEADER) {
            if (convertView == null) {

                convertView = new HomeTopicHeader(mContext);
            }
            ((HomeTopicHeader) convertView).setTid(mTid);
            ((HomeTopicHeader) convertView).setData(mDatas.topicdata, mImageLoader);

        } else {
            if (convertView == null) {

                convertView = new HomePageItem(mContext);
            }
            List<PostInfo> item1 = (List<PostInfo>) obj;
            if (item1.size() == 2) {
                ((HomePageItem) convertView).setData(item1.get(0), item1.get(1), mImageLoader);
            } else {
                ((HomePageItem) convertView).setData(item1.get(0), null, mImageLoader);
            }
        }

        return convertView;
    }

}
