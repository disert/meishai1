package com.meishai.ui.fragment.usercenter.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.entiy.SpecialInfo;
import com.meishai.entiy.SpecialListRespData;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.ListImageListener;
import com.meishai.ui.fragment.meiwu.MeiWuSpecialShowActivity;
import com.meishai.util.AndroidUtil;

import java.util.List;

/**
 * 我的收藏 - 专场的适配器
 *
 * @author Administrator yl
 */
public class CollectedSpecialAdapter extends BaseAdapter {

    private Context mContext;
    private ImageLoader mImageLoader;
    private SpecialListRespData mDatas;

    public CollectedSpecialAdapter(Context context, ImageLoader imageLoader) {
        mContext = context;
        mImageLoader = imageLoader;
    }

    public void setData(SpecialListRespData datas) {
        mDatas = datas;
        notifyDataSetChanged();
    }

    public void addData(List<SpecialInfo> datas) {
        if (datas == null) {
            AndroidUtil.showToast("没有更多数据了!");
            return;
        }
        mDatas.list.addAll(datas);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mDatas == null || mDatas.list == null || mDatas.list.size() == 0) {
            return 0;
        }
        return mDatas.list.size();
    }


    @Override
    public Object getItem(int position) {

        return mDatas.list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        final SpecialInfo info = (SpecialInfo) getItem(position);
        MyHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext,
                    R.layout.meiwu_special_list_item, null);
            holder = new MyHolder();
            holder.image = (ImageView) convertView.findViewById(R.id.image);
            holder.bitTitle = (TextView) convertView.findViewById(R.id.big_title);
            holder.smallTitle = (TextView) convertView.findViewById(R.id.small_title);
            convertView.setTag(holder);
        } else {
            holder = (MyHolder) convertView.getTag();
        }
        holder.bitTitle.setText(info.title);
        holder.smallTitle.setText(info.subtitle);

        holder.image.setTag(info.image);
        ListImageListener listener = new ListImageListener(holder.image,
                R.drawable.place_default, R.drawable.place_default,
                info.image);
        mImageLoader.get(info.image, listener);

        convertView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 专场详情
                Intent intent = MeiWuSpecialShowActivity
                        .newIntent(info.tid);
                mContext.startActivity(intent);
            }
        });
        return convertView;
    }

    class MyHolder {
        public ImageView image;
        public TextView bitTitle;
        public TextView smallTitle;
    }

}
