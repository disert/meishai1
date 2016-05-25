package com.meishai.ui.fragment.meiwu.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.meishai.R;
import com.meishai.app.widget.layout.MeiWuItemLayout;
import com.meishai.app.widget.layout.MeiWuSKUItemLayout;
import com.meishai.app.widget.layout.MeiWuStratDetailHeaderLayout;
import com.meishai.entiy.SKUResqData.Blurb;
import com.meishai.entiy.SpecialDetailResqData;
import com.meishai.entiy.StrategyResqData;
import com.meishai.entiy.StratDetailRespData.HeadData;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.util.AndroidUtil;

/**
 * 美物-专场详情的适配器
 *
 * @author Administrator yl
 */
public class MeiWuSpecialAdapter extends BaseAdapter {

    private Activity mContext;
    private ImageLoader mImageLoader;
    private SpecialDetailResqData mDatas;
    private final int TYPE_HEADER = 0;
    private final int TYPE_ITEM = 1;

    private List<Blurb> item;

    public MeiWuSpecialAdapter(Activity context, ImageLoader imageLoader) {
        mContext = context;
        mImageLoader = imageLoader;
        item = new ArrayList<Blurb>();
    }

    public void setData(SpecialDetailResqData datas) {
        mDatas = datas;
        notifyDataSetChanged();
    }

    public void addData(List<Blurb> datas) {
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
        return (mDatas.list.size() + 1) / 2 + 1;
    }

    @Override
    public Object getItem(int position) {
        if (position == 0) {
            return mDatas.topicdata;
        } else {
            int startPosition = (position - 1) * 2;
            int endPosition = startPosition + 1;
            item.clear();
            item.add(mDatas.list.get(startPosition));
            if (endPosition < mDatas.list.size()) {
                item.add(mDatas.list.get(endPosition));
            }

            return item;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        Object obj = getItem(position);
        if (type == TYPE_HEADER) {
            if (convertView == null) {
                // 头
                convertView = new MeiWuStratDetailHeaderLayout(mContext);
            }
            ((MeiWuStratDetailHeaderLayout) convertView).setData(
                    (HeadData) obj, mImageLoader);

//            HeadData info = (HeadData) obj;
//			convertView = View.inflate(mContext,
//					R.layout.meiwu_strat_detail_header, null);
//			ImageView image = (ImageView) convertView.findViewById(R.id.meiwu_strat_detail_image);
//			TextView bitTitle = (TextView) convertView.findViewById(R.id.meiwu_strat_detail_title);
//			convertView.findViewById(R.id.meiwu_strat_detail_desc).setVisibility(View.GONE);
//			bitTitle.setText(info.title);
//			image.setTag(info.image);
//			ListImageListener listener = new ListImageListener(image,
//					R.drawable.place_default, R.drawable.place_default,
//					info.image);
//			mImageLoader.get(info.image, listener);

        } else {
            if (convertView == null) {
                convertView = new MeiWuSKUItemLayout(mContext);
            }
            List<Blurb> item1 = (List<Blurb>) getItem(position);
            if (item1.size() == 2) {
                ((MeiWuSKUItemLayout) convertView).setData(item1.get(0), item1.get(1), mImageLoader);
            } else {
                ((MeiWuSKUItemLayout) convertView).setData(item1.get(0), null, mImageLoader);
            }
        }

        return convertView;
    }
}
