package com.meishai.ui.fragment.home.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.meishai.R;
import com.meishai.entiy.ChannelData;
import com.meishai.ui.fragment.home.HomeCateActivity.LeftItemClickListener;

public class HomeCateAdapter extends BaseAdapter {
    private Context mContext = null;
    private List<ChannelData> channelDatas = null;
    private LayoutInflater inflater = null;
    // 默认显示的Channel
    private ChannelData currentChannel = null;
    private LeftItemClickListener listener = null;
    ;

    public HomeCateAdapter(Context context, List<ChannelData> channelDatas) {
        super();
        this.mContext = context;
        this.channelDatas = channelDatas;
        inflater = LayoutInflater.from(context);
    }

    public void setListener(LeftItemClickListener listener) {
        this.listener = listener;
    }

    public void setChannelDatas(List<ChannelData> channelDatas) {
        this.channelDatas = channelDatas;
    }

    public ChannelData getCurrentChannel() {
        return currentChannel;
    }

    public void setCurrentChannel(ChannelData currentChannel) {
        this.currentChannel = currentChannel;
        notifyDataSetChanged();
    }

    public List<ChannelData> getChannelDatas() {
        return channelDatas;
    }

    @Override
    public int getCount() {
        return channelDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return channelDatas.get(position);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (null == convertView) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.home_cate_item, null);
            // holder.lay_cate_item = (LinearLayout) convertView
            // .findViewById(R.id.lay_cate_item);
            holder.name = (Button) convertView.findViewById(R.id.name);
            holder.cate_right = (ImageView) convertView
                    .findViewById(R.id.cate_right);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ChannelData channelData = channelDatas.get(position);
        if (channelData.getChid() == currentChannel.getChid()
                && channelData.getType() == currentChannel.getType()) {
            holder.name.setTextColor(mContext.getResources().getColor(
                    R.color.white));
            holder.name
                    .setBackgroundResource(R.drawable.btn_shaishai_cate_shape_sel);
            holder.cate_right.setVisibility(View.VISIBLE);
        } else {
            holder.cate_right.setVisibility(View.GONE);
            holder.name.setTextColor(mContext.getResources().getColor(
                    R.color.master_username_color));
            holder.name
                    .setBackgroundResource(R.drawable.btn_shaishai_cate_shape);
        }
        holder.name.setText(channelData.getName());
        holder.name.setTag(position);
        CateItemClickListener cateItemClickListener = new CateItemClickListener();
        holder.name.setOnClickListener(cateItemClickListener);
        // holder.lay_cate_item.setTag(position);
        // holder.lay_cate_item.setOnClickListener(cateItemClickListener);
        return convertView;
    }

    class CateItemClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int p = (Integer) v.getTag();
            ChannelData channel = channelDatas.get(p);
            setCurrentChannel(channel);
            if (null != listener) {
                listener.onClick();
            }
        }
    }

    class ViewHolder {
        private LinearLayout lay_cate_item;
        private Button name;
        private ImageView cate_right;
    }

}
