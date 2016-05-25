package com.meishai.app.widget.layout;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.meishai.R;
import com.meishai.entiy.PostItem.PictureInfo;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.ListImageListener;

public class PicAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;

    private ImageLoader mImageLoader;
    private ArrayList<PictureInfo> mData;
    // 0:表示表格展示 1:表示列表展示
    private int SHOW_TYPE = 0;

    public PicAdapter(Context context, ImageLoader imageLoader, ArrayList<PictureInfo> list) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);

        mImageLoader = imageLoader;
        mData = list;
    }

    //设置展示方式
    public void setListShow() {
        SHOW_TYPE = 1;
    }

    public void setOnItemClickListener() {

    }


    @Override
    public int getCount() {
        if (null == mData) {
            return 0;
        } else {
            return mData.size() > 9 ? 9 : mData.size();
        }
    }

    @Override
    public PictureInfo getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final PictureInfo item = getItem(position);
        final PicViewHolder holder;

        if (null == convertView) {
            if (SHOW_TYPE == 1) {
                convertView = mInflater.inflate(R.layout.lay_pic_list, parent, false);
            } else {
                convertView = mInflater.inflate(R.layout.lay_pic, parent, false);
            }

            holder = new PicViewHolder();
            holder.p_pic = (ImageView) convertView.findViewById(R.id.p_pic);
            convertView.setTag(holder);

        } else {
            holder = (PicViewHolder) convertView.getTag();
        }

        holder.p_pic.setTag(item.url);
        ListImageListener listener = new ListImageListener(holder.p_pic, R.drawable.place_default, R.drawable.place_default, item.url);
        mImageLoader.get(item.url, listener);

        return convertView;
    }

    public static class PicViewHolder {
        ImageView p_pic;
    }

}
