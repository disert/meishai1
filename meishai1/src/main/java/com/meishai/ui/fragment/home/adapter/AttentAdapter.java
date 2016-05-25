package com.meishai.ui.fragment.home.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.meishai.app.widget.layout.PostTimeLineLayout1;
import com.meishai.entiy.HomeInfo;
import com.meishai.entiy.ItemInfo;
import com.meishai.net.volley.toolbox.ImageLoader;

/**
 * 主界面->关注 2.0
 *
 * @author yl
 */
public class AttentAdapter extends BaseAdapter {
    private Context context;
    private ImageLoader mImageLoader;
    private List<ItemInfo> mList;

    public AttentAdapter(Context context, ImageLoader imageLoader) {
        mImageLoader = imageLoader;
        this.context = context;
        mList = new ArrayList<ItemInfo>();
    }

    public void addCollection(Collection<ItemInfo> collection) {
        mList.addAll(collection);
        notifyDataSetChanged();
    }

    public synchronized void setData(HomeInfo info) {
        this.mList = info.list;
        notifyDataSetChanged();
    }

    public void clear() {
        mList.clear();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Object item = getItem(position);
        if (null == convertView) {
            convertView = new PostTimeLineLayout1(context, false);
        }
        ((PostTimeLineLayout1) convertView).setData((ItemInfo) item, mImageLoader);
        return convertView;
    }


}
