package com.meishai.ui.fragment.meiwu.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.meishai.R;
import com.meishai.entiy.SpecialInfo;
import com.meishai.entiy.SpecialListRespData;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.ListImageListener;
import com.meishai.ui.fragment.meiwu.MeiWuSpecialShowActivity;
import com.meishai.util.AndroidUtil;

/**
 * 美物-专场详情的适配器
 *
 * @author Administrator yl
 */
public class MeiWuSpecialListAdapter extends BaseAdapter {

    private Context mContext;
    private ImageLoader mImageLoader;
    private SpecialListRespData mDatas;
    private final int TYPE_HEADER = 0;
    private final int TYPE_ITEM = 1;

    public MeiWuSpecialListAdapter(Context context, ImageLoader imageLoader) {
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
        final SpecialInfo info = mDatas.list.get(position);
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.meiwu_special_list_item, null);
        }
        ImageView image = (ImageView) convertView.findViewById(R.id.image);

        image.setTag(info.image);
        ListImageListener listener = new ListImageListener(image,
                R.drawable.place_default, R.drawable.place_default,
                info.image);
        mImageLoader.get(info.image, listener);

        convertView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 专场详情
                Intent intent = MeiWuSpecialShowActivity.newIntent(info.tid);
                mContext.startActivity(intent);
            }
        });
        return convertView;
    }

}
