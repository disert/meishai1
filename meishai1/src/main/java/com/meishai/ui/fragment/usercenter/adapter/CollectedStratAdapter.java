package com.meishai.ui.fragment.usercenter.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.meishai.app.widget.layout.AutoScrollLayout1;
import com.meishai.app.widget.layout.MeiWuCateLayout;
import com.meishai.app.widget.layout.MeiWuItemLayout;
import com.meishai.entiy.HomeInfo.SlideInfo;
import com.meishai.entiy.StrategyResqData;
import com.meishai.entiy.StrategyResqData.CateData;
import com.meishai.entiy.StrategyResqData.StratData;
import com.meishai.net.volley.toolbox.ImageLoader;

import java.util.List;

/**
 * 收藏 - 攻略对应的适配器 2.0
 *
 * @author Administrator yl
 */
public class CollectedStratAdapter extends BaseAdapter {


    // private LayoutInflater mInflater;
    private Activity mContext;
    private ImageLoader mImageLoader;


    // 所有数据
    private StrategyResqData mData;

    public CollectedStratAdapter(Activity context, ImageLoader imageLoader) {
        // mInflater = LayoutInflater.from(context);
        mContext = context;
        mImageLoader = imageLoader;
    }


    /**
     * 初始化数据
     *
     * @param data
     */
    public synchronized void setData(StrategyResqData data) {
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

        Object item = getItem(position);


        if (null == convertView) {
            convertView = new MeiWuItemLayout(mContext, MeiWuItemLayout.TYPE_OTHER);
        }
        ((MeiWuItemLayout) convertView).setData((StratData) item,
                mImageLoader);

        return convertView;
    }

}
