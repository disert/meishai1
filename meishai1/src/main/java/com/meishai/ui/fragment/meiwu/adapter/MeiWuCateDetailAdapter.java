package com.meishai.ui.fragment.meiwu.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.meishai.app.widget.layout.MeiWuItemLayout;
import com.meishai.entiy.StratListRespData;
import com.meishai.entiy.StrategyResqData.StratData;
import com.meishai.net.volley.toolbox.ImageLoader;

/**
 * 主界面 精选fragment对应的适配器 2.0
 *
 * @author Administrator yl
 */
public class MeiWuCateDetailAdapter extends BaseAdapter {

    private Context mContext;
    private ImageLoader mImageLoader;

    // 所有数据
    private StratListRespData mData;

    public MeiWuCateDetailAdapter(Context context, ImageLoader imageLoader) {
        // mInflater = LayoutInflater.from(context);
        mContext = context;
        mImageLoader = imageLoader;
    }

    /**
     * 初始化数据
     *
     * @param data
     */
    public void setData(StratListRespData data) {
        mData = data;

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mData == null || mData.list == null || mData.list.size() == 0) {
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
        StratData item = (StratData) getItem(position);
        if (null == convertView) {
            convertView = new MeiWuItemLayout(mContext);
        }
        ((MeiWuItemLayout) convertView).setData(item, mImageLoader);


        return convertView;
    }

}
