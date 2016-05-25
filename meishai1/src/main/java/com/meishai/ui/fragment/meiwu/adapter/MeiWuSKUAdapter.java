package com.meishai.ui.fragment.meiwu.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.meishai.app.widget.layout.MeiWuSKUItemLayout;
import com.meishai.entiy.SKUResqData;
import com.meishai.entiy.SKUResqData.Blurb;
import com.meishai.net.volley.toolbox.ImageLoader;

/**
 * 美物 单品fragment对应的适配器 2.0
 *
 * @author Administrator yl
 */
public class MeiWuSKUAdapter extends BaseAdapter {

    // private LayoutInflater mInflater;
    private Context mContext;
    private ImageLoader mImageLoader;
//	private List<Blurb> item;
//
//	private final int TYPE_HEADER = 0;
//	private final int TYPE_ITEM = 1;

    // 所有数据
    private SKUResqData mData;
    private List<Blurb> item;

    public MeiWuSKUAdapter(Context context, ImageLoader imageLoader) {
        // mInflater = LayoutInflater.from(context);
        mContext = context;
        mImageLoader = imageLoader;
        item = new ArrayList<SKUResqData.Blurb>();
    }

    /**
     * 初始化数据
     *
     * @param data
     */
    public synchronized void setData(SKUResqData data) {
        mData = data;

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mData == null || mData.list == null || mData.list.size() == 0) {
            return 0;
        }

        return (mData.list.size() + 1) / 2;
//		return mData.list.size() ;
    }

//	@Override
//	public int getItemViewType(int position) {
//		if (position == 0) {
//			return TYPE_HEADER;
//		} else {
//
//			return TYPE_ITEM;
//		}
//	}

//	@Override
//	public int getViewTypeCount() {
//		return 2;
//	}

    @Override
    public Object getItem(int position) {
//		 return mData.list.get(position);
//		if (position == 0) {
//			return mData.topic;
//		} else {
        int startPosition = position * 2;
        int endPosition = startPosition + 1;
        item.clear();
        item.add(mData.list.get(startPosition));
        if (endPosition < mData.list.size()) {
            item.add(mData.list.get(endPosition));
        }

        return item;
//		}
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

//		int type = getItemViewType(position);
//		Object obj = getItem(position);
//		switch (type) {
//		case TYPE_HEADER:
//			if(convertView == null){
//				convertView = new HomeTopicLayout(mContext);
//			}
//			((HomeTopicLayout)convertView).setType(HomeTopicLayout.TYPE_SPECAIL);
//			((HomeTopicLayout)convertView).setData(mData.topic, mImageLoader);
//			break;
//		case TYPE_ITEM:
        if (convertView == null) {
            convertView = new MeiWuSKUItemLayout(mContext);
        }
        List<Blurb> item1 = (List<Blurb>) getItem(position);
        if (item1.size() == 2) {
            ((MeiWuSKUItemLayout) convertView).setData(item1.get(0), item1.get(1), mImageLoader);
        } else {
            ((MeiWuSKUItemLayout) convertView).setData(item1.get(0), null, mImageLoader);
        }
//			break;
//
//		default:
//			break;
//		}
//		final Holder holder;
//		final Blurb item = (Blurb) getItem(position);
//		if (convertView == null) {
//			holder = new Holder();
//			convertView = View.inflate(mContext, R.layout.meiwu_sku_item, null);
//			holder.mImage = (ImageView) convertView
//					.findViewById(R.id.meiwu_sku_image);
//			holder.mDesc = (TextView) convertView
//					.findViewById(R.id.meiwu_sku_desc);
//			holder.mPrice = (TextView) convertView
//					.findViewById(R.id.meiwu_sku_price);
//			holder.mLikeNum = (TextView) convertView
//					.findViewById(R.id.meiwu_sku_like_num);
//			convertView.setTag(holder);
//		} else {
//			holder = (Holder) convertView.getTag();
//		}
//
//		holder.mDesc.setText("      "+item.title);
//		holder.mLikeNum.setText(item.props);
//		holder.mPrice.setText(item.price);
//		
//		ImageListener listener1 = ImageLoader.getImageListener(holder.mImage,
//				R.drawable.place_default, R.drawable.place_default);
//		mImageLoader.get(item.image, listener1);


        return convertView;
    }


}
