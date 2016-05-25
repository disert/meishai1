package com.meishai.app.widget.layout;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.entiy.StrategyResqData.CateData;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.ListImageListener;
import com.meishai.ui.fragment.meiwu.MeiWuCateDetailActivity;

/**
 * 美物-攻略-分类页面
 *
 * @author Administrator yl
 */
public class MeiWuCateLayout extends LinearLayout {

    private GridView mGridView;

    private ImageLoader mImageLoader;
    private List<CateData> mCateList;
    private CateGridAdapter mCateAdapter;

    private Context mContext;


    public MeiWuCateLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public MeiWuCateLayout(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {

        mContext = context;

        LayoutInflater inflater = LayoutInflater.from(context);
        View convertView = inflater.inflate(R.layout.home_handpick_cate_item1,
                this, true);

        mGridView = (GridView) convertView.findViewById(R.id.cate_gridview);
        mGridView.setNumColumns(5);
        mCateAdapter = new CateGridAdapter(context);

        mGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                CateData info = mCateList.get(position);
                Intent intent = MeiWuCateDetailActivity.newIntent(info.cid);
                mContext.startActivity(intent);

            }

        });

    }


    public void setData(List<CateData> list, ImageLoader imageLoader) {
        if (list == null || list.isEmpty()) {
            setVisibility(GONE);
            return;
        } else {
            setVisibility(VISIBLE);
        }
        if (mCateList == list) {
            return;
        }

        mCateList = list;

        mImageLoader = imageLoader;
        mGridView.setAdapter(mCateAdapter);
    }

    private class CateGridAdapter extends BaseAdapter {

        private LayoutInflater inflate;


        public CateGridAdapter(Context context) {
            inflate = LayoutInflater.from(context);
        }


        @Override
        public int getCount() {
            int count = mCateList.size();


            return count;
        }

        @Override
        public Object getItem(int position) {
            if (position < mCateList.size()) {
                return mCateList.get(position);

            } else {
                return null;
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (null == convertView) {
                convertView = inflate.inflate(R.layout.meiwu_strat_cate_item,
                        parent, false);
                holder = new ViewHolder();
                holder.image = (ImageView) convertView
                        .findViewById(R.id.cate_iv);
                holder.name = (TextView) convertView.findViewById(R.id.cate_tv);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            CateData item = (CateData) getItem(position);
            holder.image.setTag(item.image);
            ListImageListener listener = new ListImageListener(holder.image, 0,
                    0, item.image);
            mImageLoader.get(item.image, listener);

            holder.name.setText(item.catname);

            return convertView;
        }

        private class ViewHolder {
            private ImageView image;
            private TextView name;
        }
    }

}
