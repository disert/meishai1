package com.meishai.app.widget.layout;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.meishai.R;
import com.meishai.app.widget.ScrollGridView;
import com.meishai.app.widget.horizontalscrollview.GalleryAdapter;
import com.meishai.entiy.HomeInfo;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.ListImageListener;
import com.meishai.util.AndroidUtil;
import com.meishai.util.ImageAdapter;
import com.meishai.util.SkipUtils;

/**
 * 文件名：
 * 描    述：实现图片列表的自定义view 目前在美物 - alladapter 头部使用
 * 作    者：yl
 * 时    间：2016/2/4
 * 版    权：
 */
public class PicListLayout extends LinearLayout {

    private final Context mContext;
    private ScrollGridView mGridView;
    private int mPadding;
    private MyAdpter mAdpter;
    private HomeInfo.SlideInfo[] mData;
    private ImageLoader mImageLoader;
    private RelativeLayout.LayoutParams mParams;

    public PicListLayout(Context context) {
        this(context, null);
    }

    public PicListLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }


    private void init() {
        View.inflate(mContext, R.layout.view_pic_list, this);

        mPadding = AndroidUtil.dip2px(4);
        Point point = ImageAdapter.getViewRealWH(2, mPadding, 750, 380);
        mParams = new RelativeLayout.LayoutParams(point.x, point.y);
        mGridView = (ScrollGridView) findViewById(R.id.gridView);
        mGridView.setPadding(mPadding, 0, 0, 0);
        mAdpter = new MyAdpter();
        mGridView.setAdapter(mAdpter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HomeInfo.SlideInfo tag = (HomeInfo.SlideInfo) view.getTag();
                SkipUtils.skip(mContext, tag.type, tag.value, tag.tempid, tag.h5data);
            }
        });
    }

    public void setData(HomeInfo.SlideInfo[] data, ImageLoader imageLoader) {
        if (mData == data) return;
        mData = data;
        mImageLoader = imageLoader;
        mAdpter.notifyDataSetChanged();
    }

    public void setOnPicClickListener(AdapterView.OnItemClickListener listenner) {
        mGridView.setOnItemClickListener(listenner);
    }

    class MyAdpter extends BaseAdapter {

        @Override
        public int getCount() {
            if (mData == null || mData.length == 0) {
                return 0;
            }
            return mData.length;
        }

        @Override
        public Object getItem(int position) {
            return mData[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            HomeInfo.SlideInfo item = (HomeInfo.SlideInfo) getItem(position);
            ImageView iv;
            if (convertView == null) {
                convertView = new SampleRoundImageView(mContext).hideRound();
                convertView.setPadding(0, mPadding, mPadding, 0);

            }
            convertView.setTag(item);
            iv = ((SampleRoundImageView) convertView).getImageView();
            iv.setLayoutParams(mParams);

            iv.setTag(item.image);
            ListImageListener listener = new ListImageListener(iv, R.drawable.place_default, R.drawable.place_default, item.image);
            mImageLoader.get(item.image, listener);

            return convertView;
        }
    }

}
