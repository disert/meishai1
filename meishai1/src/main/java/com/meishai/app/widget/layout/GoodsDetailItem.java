package com.meishai.app.widget.layout;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.entiy.SKUResqData;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.ui.fragment.meiwu.MeiWuGoodsDetailActivity;

import java.util.List;

/**
 * 文件名：
 * 描    述：
 * 作    者：
 * 时    间：2015/12/28
 * 版    权：
 */
public class GoodsDetailItem extends LinearLayout {

    private final Context mContext;
    private View view;
    private GridView mGridView;
    private MyAdapter mAdapter;
    private List<SKUResqData.Blurb> mData;
    private ImageLoader mImageLoader;

    public GoodsDetailItem(Context context) {
        this(context, null);
    }

    public GoodsDetailItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    private void initView() {
        view = View.inflate(mContext, R.layout.view_goods_detail_item, this);

        mGridView = (GridView) view.findViewById(R.id.gridview);
        mGridView.setNumColumns(2);
        mAdapter = new MyAdapter();
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                AndroidUtil.showToast("position:"+position);
                Intent intent = MeiWuGoodsDetailActivity.newIntent(mData.get(position).pid);
                mContext.startActivity(intent);
            }
        });
    }

    public void setData(List<SKUResqData.Blurb> item_list, ImageLoader imageLoader) {
        mImageLoader = imageLoader;
        mData = item_list;
        mAdapter.notifyDataSetChanged();
    }

    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            if (mData == null || mData.isEmpty()) {
                return 0;
            }

            return mData.size();
        }


        @Override
        public Object getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final Holder holder;
            final SKUResqData.Blurb item = (SKUResqData.Blurb) getItem(position);
            if (convertView == null) {
                holder = new Holder();
                convertView = View.inflate(mContext, R.layout.view_goods_detail_item_item, null);
                holder.mImage = (ImageView) convertView
                        .findViewById(R.id.goods_item_image);
                holder.mDesc = (TextView) convertView
                        .findViewById(R.id.goods_item_content);
                holder.mPrice = (TextView) convertView
                        .findViewById(R.id.goods_item_price);
                holder.mPriceText = (TextView) convertView
                        .findViewById(R.id.goods_item_price_text);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            holder.mDesc.setText(item.title);
            holder.mPriceText.setText(item.price_text);
            holder.mPrice.setText(item.price);
            if (!TextUtils.isEmpty(item.image)) {
                ImageLoader.ImageListener listener1 = ImageLoader.getImageListener(holder.mImage,
                        R.drawable.place_default, R.drawable.place_default);
                mImageLoader.get(item.image, listener1);
            } else {
                holder.mImage.setImageResource(R.drawable.place_default);
            }


            return convertView;
        }

    }

    class Holder {
        ImageView mImage;
        TextView mDesc;
        TextView mPrice;
        TextView mPriceText;
    }
}
