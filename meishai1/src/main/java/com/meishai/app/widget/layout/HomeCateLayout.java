package com.meishai.app.widget.layout;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.app.widget.RoundCornerImageView;
import com.meishai.entiy.CateInfo;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.ListImageListener;
import com.meishai.ui.fragment.home.ChannelShowFragmentActivity;
import com.meishai.ui.fragment.home.HomeCateActivity;

@Deprecated
public class HomeCateLayout extends LinearLayout {

    private GridView mGridView;
    private ImageButton mIbShow;

    private ImageLoader mImageLoader;
    private List<CateInfo> mCateList;
    private CateGridAdapter mCateAdapter;

    private Bitmap mBmpArrow;

    public HomeCateLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public HomeCateLayout(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        mBmpArrow = BitmapFactory.decodeResource(getResources(), R.drawable.down);


        LayoutInflater inflater = LayoutInflater.from(context);
        View convertView = inflater.inflate(R.layout.home_handpick_cate_item, this, true);

        mGridView = (GridView) convertView.findViewById(R.id.cate_gridview);
        mCateAdapter = new CateGridAdapter(context);

        mGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                CateInfo info = mCateList.get(position);
                if (info.cid == CateInfo.CID_ADD) {
                    Intent intent = new Intent(getContext(), HomeCateActivity.class);
                    getContext().startActivity(intent);

                } else {
                    Intent intent = ChannelShowFragmentActivity.newIntent(info.cid, info.image, info.name);
                    getContext().startActivity(intent);
                }

            }

        });

        mIbShow = (ImageButton) convertView.findViewById(R.id.cate_show_ib);
        convertView.findViewById(R.id.cate_show_llayout).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                roateBitmapArrow();
                mCateAdapter.setShow();
            }
        });
    }

    private void roateBitmapArrow() {
        Matrix matrix = new Matrix();
        matrix.postRotate(180);
        mBmpArrow = Bitmap.createBitmap(mBmpArrow, 0, 0, mBmpArrow.getWidth(), mBmpArrow.getHeight(), matrix, true);
        mIbShow.setImageBitmap(mBmpArrow);
    }


    public void setData(List<CateInfo> list, ImageLoader imageLoader) {
        if (mCateList == list) {
            return;
        }

        mCateList = list;


        mImageLoader = imageLoader;
        mGridView.setAdapter(mCateAdapter);
    }


    private class CateGridAdapter extends BaseAdapter {

        private LayoutInflater inflate;

        private boolean mIsShow = false;

        public CateGridAdapter(Context context) {
            inflate = LayoutInflater.from(context);
        }

        public boolean isShow() {
            return mIsShow;
        }

        public void setShow() {
            mIsShow = !mIsShow;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            int count = mCateList.size();
            if (mIsShow && count > 8) {
                count = count > 16 ? 16 : count;
            } else {
                count = count > 8 ? 8 : count;
            }

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
                convertView = inflate.inflate(R.layout.handpick_cate_item, parent, false);
                holder = new ViewHolder();
                holder.image = (RoundCornerImageView) convertView.findViewById(R.id.cate_iv);
                holder.name = (TextView) convertView.findViewById(R.id.cate_tv);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            CateInfo item = (CateInfo) getItem(position);
            if (item.cid == CateInfo.CID_ADD) {
                holder.image.setTag("add");

                holder.image.setImageResource(R.drawable.ic_add);
                holder.name.setText(R.string.add);

            } else {
                holder.image.setRoundness(6);
                holder.image.setTag(item.image);
                ListImageListener listener = new ListImageListener(
                        holder.image, 0,
                        0, item.image);
                mImageLoader.get(item.image, listener);

                holder.name.setText(item.name);
            }

            return convertView;
        }


        private class ViewHolder {
            private RoundCornerImageView image;
            private TextView name;
        }
    }

}
