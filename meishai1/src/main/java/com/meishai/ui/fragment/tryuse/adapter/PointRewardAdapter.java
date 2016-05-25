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
 * 福利社 - 积分商城 fragment对应的适配器 2.0
 *
 * @author Administrator yl
 */
public class PointRewardAdapter extends BaseAdapter {

    // private LayoutInflater mInflater;
    private Context mContext;
    private ImageLoader mImageLoader;
    //	private List<Blurb> item;
//
    private final int TYPE_HEADER = 0;
    private final int TYPE_ITEM = 1;

    // 所有数据
    private PointRewardRespData mData;

    public PointRewardAdapter(Context context, ImageLoader imageLoader) {
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

        return (mData.list.size() + 1) / 2 + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else {

            return TYPE_ITEM;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public Object getItem(int position) {
        if (position == 0) {
            return mData.typedata;
        } else {
            int startPosition = (position - 1) * 2;
            int endPosition = startPosition + 1;
            List<PointData> item = new ArrayList<PointData>();
            item.clear();
            item.add(mData.list.get(startPosition));
            if (endPosition < mData.list.size()) {
                item.add(mData.list.get(endPosition));
            }

            return item;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        int type = getItemViewType(position);
        Object obj = getItem(position);
        switch (type) {
            case TYPE_HEADER://头
                if (convertView == null) {
                    convertView = new PointRewardHeader(mContext);
                }
                ((PointRewardHeader) convertView).setData(mData.typedata, mImageLoader);
                break;
            case TYPE_ITEM://item
                if (convertView == null) {
                    convertView = new PointRewardItemLayout(mContext);
                }
                List<PointData> item1 = (List<PointData>) obj;
                if (item1.size() == 2) {
                    ((PointRewardItemLayout) convertView).setData(item1.get(0), item1.get(1), mImageLoader);
                } else {
                    ((PointRewardItemLayout) convertView).setData(item1.get(0), null, mImageLoader);
                }
                break;

            default:
                break;
        }

        return convertView;
    }


}
