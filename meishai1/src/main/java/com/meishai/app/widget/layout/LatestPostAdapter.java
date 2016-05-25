package com.meishai.app.widget.layout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.meishai.entiy.PostItem;
import com.meishai.net.volley.toolbox.ImageLoader;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


public class LatestPostAdapter extends BaseAdapter {

    public interface OnDelClickListener {
        void onDelClickListener(int position);
    }

    private OnDelClickListener mDelListener;

    private Context mContext;
    private ImageLoader mImageLoader;
    private List<PostItem> mList;

    public LatestPostAdapter(Context context, ImageLoader imageLoader) {
        mContext = context;
        mImageLoader = imageLoader;
        mList = new ArrayList<PostItem>();
    }

    public void addCollection(Collection<PostItem> items) {
        mList.addAll(items);
        notifyDataSetChanged();
    }

    public void setmList(List<PostItem> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }

    public void delItem(int position) {
        mList.remove(position);
        notifyDataSetChanged();
    }

    public void clear() {
        mList.clear();
    }

    public void setOnDelListener(OnDelClickListener l) {
        mDelListener = l;
    }


    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public PostItem getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = new PostTimeLineLayout(mContext, false);
        }

        PostItem item = (PostItem) getItem(position);
        PostTimeLineLayout layout = ((PostTimeLineLayout) convertView);
        layout.setData(item, mImageLoader);

        if (mDelListener != null) {
            layout.setOnDeleteClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mDelListener.onDelClickListener(position);
                }
            });
        }


        return convertView;
    }

}