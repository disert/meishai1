package com.meishai.ui.fragment.meiwu.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.meishai.app.widget.layout.ListImageView;
import com.meishai.app.widget.layout.MeiWuStratDetailHeaderLayout;
import com.meishai.app.widget.layout.MeiWuStratDetailItemLayout1;
import com.meishai.app.widget.layout.MeiWuStratTempid3temLayout;
import com.meishai.app.widget.layout.PicLayout;
import com.meishai.entiy.StratDetailRespData.HeadData;
import com.meishai.entiy.StratDetailRespData1;
import com.meishai.net.volley.toolbox.ImageLoader;

/**
 * 美物-攻略详情的适配器 修改版 2015年12月29日16:15:11
 *
 * @author Administrator yl
 */
public class MeiWuStratTempid3Adapter extends BaseAdapter {

    private StratDetailRespData1 mData;
    private final int TYPE_HEADER = 0;
    private final int TYPE_ITEM = 1;
    private final int TYPE_PIC = 2;
    private final int TYPE_RECOMMEND = 3;
    private Context mContext;
    private ImageLoader mImageLoader;

    public MeiWuStratTempid3Adapter(Context context, ImageLoader imageLoader) {
        mContext = context;
        mImageLoader = imageLoader;
    }

    public synchronized void setData(StratDetailRespData1 data) {
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mData == null || mData.list == null || mData.list.size() == 0) {
            return 0;
        }
        int count = mData.list.size() + 1;
        if (mData.advertisement != null || mData.welfare != null) {
            count++;
            //			return mData.list.size() + 1;// +head
        }
        if (mData.correlation != null && !mData.correlation.isEmpty()) {
            count++;
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else if (position == getCount() - 2) {//倒数第二个,如果有广告,并且有推荐,那么就是推荐,否则是普通item
            if (mData.correlation != null && !mData.correlation.isEmpty() && (mData.advertisement != null || mData.welfare != null)) {
                return TYPE_RECOMMEND;
            } else {
                return TYPE_ITEM;
            }
        } else if (position == getCount() - 1) {// 最后一个 如果有广告就是广告,如果没广告有推荐就是推荐,都没有就是普通的item
            if (mData.advertisement != null || mData.welfare != null) {
                return TYPE_PIC;
            } else if (mData.correlation != null && !mData.correlation.isEmpty()) {
                return TYPE_RECOMMEND;
            } else {
                return TYPE_ITEM;
            }
        } else {


            return TYPE_ITEM;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 4;
    }

    @Override
    public Object getItem(int position) {
        int type = getItemViewType(position);
        if (position == 0) {
            return mData.topicdata;
        } else if (position == getCount() - 2) {//倒数第二个
            if (type == TYPE_RECOMMEND) {
                return mData.correlation;
            } else {
                return mData.list.get(position - 1);
            }
        } else if (position == getCount() - 1) {//最后一个
            if (type == TYPE_PIC) {
                PicLayout.PicInfo info = new PicLayout.PicInfo();
                info.mAdvertisement = mData.advertisement;
                info.mWelfare = mData.welfare;
                return info;
            } else if (type == TYPE_RECOMMEND) {
                return mData.correlation;
            }
        }

        return mData.list.get(position - 1);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        Object item = getItem(position);
        if (TYPE_HEADER == type) {// 头
            if (convertView == null) {
                // 头
                convertView = new MeiWuStratDetailHeaderLayout(mContext);
            }
            ((MeiWuStratDetailHeaderLayout) convertView).setData(
                    (HeadData) item, mImageLoader);

        } else if (TYPE_RECOMMEND == type) {// 推荐
            // item
            if (convertView == null) {
                convertView = new ListImageView(mContext);
            }
            ((ListImageView) convertView).setData(mData.correlation, mImageLoader);

        } else if (TYPE_ITEM == type) {// 一般item
            // item
            if (convertView == null) {
                convertView = new MeiWuStratTempid3temLayout(mContext);
            }
            ((MeiWuStratTempid3temLayout) convertView).setData(
                    (StratDetailRespData1.stratDetailItem) item, mImageLoader);

        } else if (TYPE_PIC == type) {
            if (convertView == null) {// 广告和奖品
                convertView = new PicLayout(mContext);
            }
            PicLayout.PicInfo info = (PicLayout.PicInfo) getItem(position);
            ((PicLayout) convertView).setData(info, mImageLoader);
        }

        return convertView;
    }

}
