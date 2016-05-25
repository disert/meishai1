package com.meishai.ui.fragment.home;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.entiy.BaseResp;
import com.meishai.entiy.DiscloseListRespData;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.ListImageListener;

/**
 * 文件名：
 * 描    述：主页 - 我的爆料的适配器
 * 作    者：yl
 * 时    间：2016/4/9
 * 版    权：
 */
public class DiscloseAdapter extends BaseAdapter {

    private DiscloseListRespData mData;
    private ImageLoader mImageLoader;
    private Context mContext;

    public DiscloseAdapter(Context context, ImageLoader imageLoader) {
        mContext = context;
        mImageLoader = imageLoader;
    }

    public void setData(DiscloseListRespData data) {
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mData == null || mData.list == null) {
            return 0;
        }
        return mData.list.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DiscloseListRespData.DiscloseInfo item = (DiscloseListRespData.DiscloseInfo) getItem(position);
        Holder holder = null;
        if (convertView == null) {
            holder = new Holder();
            convertView = View.inflate(mContext, R.layout.disclose_item_layout, null);
            holder.image = (ImageView) convertView.findViewById(R.id.disclose_item_image);
            holder.name = (TextView) convertView.findViewById(R.id.disclose_item_name);
            holder.from = (TextView) convertView.findViewById(R.id.disclose_item_from);
            holder.time = (TextView) convertView.findViewById(R.id.disclose_item_time);
            holder.check = (TextView) convertView.findViewById(R.id.disclose_item_check);
            holder.desc = (TextView) convertView.findViewById(R.id.disclose_item_desc);

            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        holder.from.setText(item.address);
        holder.name.setText(item.title);
        holder.time.setText(item.addtime);
        holder.check.setText(item.status_text);
        holder.check.setTextColor(Color.parseColor("#" + item.status_color));
        holder.desc.setText(item.content);

        holder.image.setTag(item.image);
        ListImageListener listener = new ListImageListener(holder.image, R.drawable.place_default, R.drawable.place_default, item.image);
        mImageLoader.get(item.image, listener);


        return convertView;
    }

    class Holder {
        TextView from;
        TextView time;
        TextView name;
        TextView check;
        TextView desc;
        ImageView image;
    }
}
