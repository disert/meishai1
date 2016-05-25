package com.meishai.ui.fragment.meiwu.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.meishai.app.widget.layout.ArticleItem;
import com.meishai.app.widget.layout.MeiWuStratDetailHeaderLayout;
import com.meishai.app.widget.layout.MeiWuStratDetailItemLayout;
import com.meishai.app.widget.layout.PicLayout;
import com.meishai.entiy.StratDetailRespData;
import com.meishai.entiy.StratDetailRespData.HeadData;
import com.meishai.entiy.StratDetailRespData.SpecialItem;
import com.meishai.net.volley.toolbox.ImageLoader;

/**
 * 美物-攻略详情的适配器
 *
 * @author Administrator yl
 */
public class MeiWuStratDetailAdapter extends BaseAdapter {

    private StratDetailRespData mData;
    private final int TYPE_HEADER = 0;
    private final int TYPE_ITEM = 1;
    private final int TYPE_PIC = 2;
    private final int TYPE_ARTICLE = 3;
    private Context mContext;
    private ImageLoader mImageLoader;

    public MeiWuStratDetailAdapter(Context context, ImageLoader imageLoader) {
        mContext = context;
        mImageLoader = imageLoader;
    }

    public void setData(StratDetailRespData data) {
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mData == null || mData.list == null || mData.list.size() == 0) {
            return 0;
        }
        int count = mData.list.size() + 2;
        if (mData.advertisement != null || mData.welfare != null) {
            count++;
//			return mData.list.size() + 1;// +head
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        }
        if (position == 1) {
            return TYPE_ARTICLE;
        }

        if (position == getCount() - 1) {// 最后一个 广告
            if (mData.advertisement != null || mData.welfare != null) {
                return TYPE_PIC;
            }
        }


        return TYPE_ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return 4;
    }

    @Override
    public Object getItem(int position) {
        if (position == 0) {
            return mData.topicdata;
        }
        if (position == 1) {
            return mData.article;
        }
        if (position == getCount() - 1) {//最后一个
            PicLayout.PicInfo info = new PicLayout.PicInfo();
            info.mAdvertisement = mData.advertisement;
            info.mWelfare = mData.welfare;
            return info;
        }

        return mData.list.get(position - 2);
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

        } else if (TYPE_ARTICLE == type) {// 文章
            // item
            if (convertView == null) {
                convertView = new ArticleItem(mContext);
            }
            ((ArticleItem) convertView).setData(mData.article, mImageLoader);

        } else if (TYPE_ITEM == type) {// 一般item
            // item
            if (convertView == null) {
                convertView = new MeiWuStratDetailItemLayout(mContext);
            }
            ((MeiWuStratDetailItemLayout) convertView).setData(
                    (SpecialItem) item, mImageLoader);

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
