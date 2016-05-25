package com.meishai.app.widget.layout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


import com.meishai.entiy.PostItem;
import com.meishai.entiy.TopicItem;
import com.meishai.net.volley.toolbox.ImageLoader;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class TopicShowAdapter extends BaseAdapter {

    private final static int TYPE_0 = 0;
    private final static int TYPE_1 = 1;

    private Context mContext;
    private ImageLoader mImageLoader;

    private TopicItem mTopic;
    private List<PostItem> mList;

    public TopicShowAdapter(Context context, ImageLoader imageloader) {
        mContext = context;
        mImageLoader = imageloader;
        mList = new ArrayList<PostItem>();
    }

    public void addCollection(Collection<PostItem> items) {
        mList.addAll(items);
        notifyDataSetChanged();
    }

    public void setTopicData(TopicItem topic) {
        mTopic = topic;
    }

    @Override
    public int getCount() {
        return mList.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        if (0 == position) {
            return mTopic;

        } else {
            return mList.get(position - 1);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getItemViewType(int position) {
        if (0 == position) {
            return TYPE_0;
        } else {
            return TYPE_1;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            switch (getItemViewType(position)) {
                case TYPE_0:
                    convertView = new TopicInfoLayout(mContext);
                    break;

                case TYPE_1:
                    convertView = new PostTimeLineLayout(mContext, false);
                    break;
                default:
                    break;
            }
        }

        if (convertView instanceof TopicInfoLayout) {
            ((TopicInfoLayout) convertView).setData(mTopic, mImageLoader);

        } else if (convertView instanceof PostTimeLineLayout) {
            Object item = getItem(position);
            if (item instanceof PostItem) {
                ((PostTimeLineLayout) convertView).setData((PostItem) item, mImageLoader);
            }
        }

        return convertView;
    }


}
