package com.meishai.ui.fragment.tryuse.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.app.widget.layout.FreeTrialHeader;
import com.meishai.app.widget.layout.PicListLayout;
import com.meishai.entiy.FreeTrialRespData;
import com.meishai.entiy.FreeTrialRespData.FreeTrailData;
import com.meishai.entiy.HomeInfo;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.ImageLoader.ImageListener;
import com.meishai.net.volley.toolbox.ListImageListener;
import com.meishai.ui.fragment.meiwu.req.MeiWuReq;
import com.meishai.ui.fragment.tryuse.FuliSheDetailActivity1;
import com.meishai.util.SkipUtils;

import java.util.List;

/**
 * 福利社 - 免费试用的适配器
 *
 * @author Administrator yl
 */
public class FreeTrialAdapter extends BaseAdapter {

    private FreeTrialRespData mResqData;
    protected Context mContext;
    private ImageLoader mImageLoader;
    private HomeInfo.SlideInfo[] mSlide;

    public FreeTrialAdapter(Context context, ImageLoader imageLoader) {
        mContext = context;
        mImageLoader = imageLoader;
    }


    public synchronized void setData(FreeTrialRespData datas) {
        mResqData = datas;
        mSlide = getSlides(datas.slide);

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mResqData == null || mResqData.list == null
                || mResqData.list.size() == 0) {
            return 0;
        }
        if (mResqData.slide == null) {
            return mResqData.list.size();
        }

        return mResqData.list.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        if (position == 0) {
            if (mResqData.slide == null) {
                return mResqData.list.get(position);
            } else {
                return mResqData.slide;
            }
        }
        if (mResqData.slide == null) {

            return mResqData.list.get(position);
        } else {
            return mResqData.list.get(position - 1);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && mResqData.slide != null) {
            return 0;
        } else {
            return 1;
        }
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position == 0 && mResqData.slide != null) {
            if (null == convertView) {
                convertView = new PicListLayout(mContext);
            }
            ((PicListLayout) convertView).setData(mSlide, mImageLoader);
        } else {
            Holder holder;
            final FreeTrailData item = (FreeTrailData) getItem(position);
            if (convertView == null) {
                holder = new Holder();
                convertView = View.inflate(mContext, R.layout.free_trial_item,
                        null);
                holder.image = (ImageView) convertView.findViewById(R.id.free_trial_image);
                holder.isnew = (ImageView) convertView.findViewById(R.id.free_trial_new);
                holder.icon = (ImageView) convertView.findViewById(R.id.free_trial_icon);
                holder.allnum = (TextView) convertView.findViewById(R.id.free_trial_allnum);
                holder.appnum = (TextView) convertView.findViewById(R.id.free_trial_appnum);
                holder.title = (TextView) convertView.findViewById(R.id.free_trial_title);
                holder.endday = (TextView) convertView.findViewById(R.id.free_trial_endday);

                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            holder.title.setText(item.title);
            if (item.isnew == 1) {
                holder.isnew.setVisibility(View.VISIBLE);
            } else {
                holder.isnew.setVisibility(View.GONE);
            }
            if (TextUtils.isEmpty(item.icon)) {
                holder.icon.setVisibility(View.GONE);
            } else {
                holder.icon.setVisibility(View.VISIBLE);
                holder.icon.setTag(item.icon);
                ListImageListener listener = new ListImageListener(holder.icon, R.drawable.place_default, R.drawable.place_default, item.icon);
                mImageLoader.get(item.icon, listener);
            }
            holder.allnum.setText(item.allnum);
            holder.appnum.setText(item.appnum);
            holder.endday.setText(item.endday);

            if (!TextUtils.isEmpty(item.image)) {
                holder.image.setTag(item.image);
                ImageListener listener1 = ImageLoader.getImageListener(
                        holder.image, R.drawable.place_default,
                        R.drawable.place_default);
                mImageLoader.get(item.image, listener1);
            } else {
                holder.image.setImageResource(R.drawable.place_default);
            }
            convertView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    //					Intent intent = FuliSheDetailActivity1.newIntent(item.gid, FuliSheDetailActivity1.TYPE_FREE_TRIAL);
                    if (item.iswap == 1) {
                        SkipUtils.skip(mContext, "h5", "", 0, item.h5data);
                    } else {
                        Intent intent = FuliSheDetailActivity1.newIntent(item.gid);
                        mContext.startActivity(intent);
                    }
                }
            });
        }

        return convertView;
    }

    private HomeInfo.SlideInfo[] getSlides(List<HomeInfo.SlideInfo> mSlides) {
        if (mSlides == null) return null;
        HomeInfo.SlideInfo[] slides = new HomeInfo.SlideInfo[mSlides.size()];
        for (int i = 0; i < mSlides.size(); i++) {
            slides[i] = mSlides.get(i);
        }
        return slides;
    }

    class Holder {

        ImageView image;
        ImageView isnew;
        TextView title;
        TextView allnum;
        TextView appnum;
        TextView endday;
        ImageView icon;
    }

}