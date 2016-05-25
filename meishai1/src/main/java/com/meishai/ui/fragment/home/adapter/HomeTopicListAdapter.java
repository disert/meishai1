package com.meishai.ui.fragment.home.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.app.widget.RoundCornerImageView;
import com.meishai.entiy.CateResponseData;
import com.meishai.entiy.CateResponseData.CateInfo1;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.ListImageListener;
import com.meishai.util.AndroidUtil;

public class HomeTopicListAdapter extends BaseAdapter {
    private CateResponseData datas = null;
    private LayoutInflater inflater = null;
    private ImageLoader imageLoader;
    private Context mContext;

    public HomeTopicListAdapter(Context context, ImageLoader imageLoader) {
        super();
        this.imageLoader = imageLoader;
        mContext = context;
        inflater = LayoutInflater.from(context);
    }

    public void setDatas(CateResponseData datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (datas != null) {
            return datas.list.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return datas.list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return datas.list.get(position).tid;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.home_topic_list_item, null);
            holder.image = (RoundCornerImageView) convertView
                    .findViewById(R.id.item_image);
            holder.image.setRoundness(7);
            holder.title = (TextView) convertView.findViewById(R.id.item_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        CateInfo1 item = (CateInfo1) getItem(position);
        // 图片加载
        holder.image.setTag(item.image);
        ListImageListener listener = new ListImageListener(holder.image,
                R.drawable.head_default, R.drawable.head_default, item.image);
        imageLoader.get(item.image, listener);

        // 设置name
        holder.title.setText(item.title);

        return convertView;
    }

    class ViewHolder {
        private TextView title;
        private RoundCornerImageView image;
    }
}
