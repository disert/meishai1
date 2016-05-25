package com.meishai.ui.fragment.tryuse.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.meishai.app.widget.layout.PointRewardHeader;
import com.meishai.app.widget.layout.PointRewardItemLayout;
import com.meishai.entiy.PointRewardRespData;
import com.meishai.entiy.PointRewardRespData.PointData;
import com.meishai.net.volley.toolbox.ImageLoader;

/**
 * 福利社 - 积分商城 - 分类列表fragment对应的适配器 2.0
 *
 * @author Administrator yl
 */
public class PointRewardCateAdapter extends BaseAdapter {

    private Context mContext;
    private ImageLoader mImageLoader;
    //

    // 所有数据
    private PointRewardRespData mData;

    public PointRewardCateAdapter(Context context, ImageLoader imageLoader) {
        // mInflater = LayoutInflater.from(context);
        mContext = context;
        mImageLoader = imageLoader;
    }

    /**
     * 初始化数据
     *
     * @param data
     */
    public void setData(PointRewardRespData data) {
        mData = data;

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mData == null || mData.list == null || mData.list.size() == 0) {
            return 0;
        }

        return (mData.list.size() + 1) / 2;
    }

    @Override
    public Object getItem(int position) {
        int startPosition = position * 2;
        int endPosition = startPosition + 1;
        List<PointData> item = new ArrayList<PointData>();
        item.add(mData.list.get(startPosition));
        if (endPosition < mData.list.size()) {
            item.add(mData.list.get(endPosition));
        }

        return item;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        List<PointData> item = (List<PointData>) getItem(position);
        if (convertView == null) {
            convertView = new PointRewardItemLayout(mContext);
        }
        if (item.size() == 2) {
            ((PointRewardItemLayout) convertView).setData(item.get(0),
                    item.get(1), mImageLoader);
        } else {
            ((PointRewardItemLayout) convertView).setData(item.get(0), null,
                    mImageLoader);
        }

        return convertView;
    }

}
