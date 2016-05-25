package com.meishai.ui.fragment.tryuse.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.entiy.FreeTrialRespData;
import com.meishai.entiy.FreeTrialRespData.FreeTrailData;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.ImageLoader.ImageListener;
import com.meishai.ui.fragment.tryuse.FuliSheDetailActivity1;

/**
 * 福利社 - 免费试用的适配器
 *
 * @author Administrator yl
 */
public class TasteAdapter extends BaseAdapter {
    private FreeTrialRespData mResqData;
    protected Context mContext;
    private ImageLoader mImageLoader;

    public TasteAdapter(Context context, ImageLoader imageLoader) {
        mContext = context;
        mImageLoader = imageLoader;
    }

    @Override
    public int getCount() {
        if (mResqData == null || mResqData.list == null
                || mResqData.list.size() == 0) {
            return 0;
        }
        return mResqData.list.size();
    }

    public synchronized void setData(FreeTrialRespData datas) {
        mResqData = datas;
        notifyDataSetChanged();
    }

    @Override
    public Object getItem(int position) {
        return mResqData.list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        final FreeTrailData item = (FreeTrailData) getItem(position);
        if (convertView == null) {
            holder = new Holder();
            convertView = View.inflate(mContext, R.layout.free_trial_item,
                    null);
            holder.image = (ImageView) convertView.findViewById(R.id.free_trial_image);
            holder.isnew = (TextView) convertView.findViewById(R.id.free_trial_new);
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
        holder.allnum.setText(item.allnum);
        holder.appnum.setText(item.appnum);
        holder.endday.setText(item.endday);

        holder.image.setTag(item.image);
        ImageListener listener1 = ImageLoader.getImageListener(
                holder.image, R.drawable.place_default,
                R.drawable.place_default);
        mImageLoader.get(item.image, listener1);
        convertView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = FuliSheDetailActivity1.newIntent(item.gid, 0);
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }

    class Holder {
        ImageView image;
        TextView isnew;
        TextView title;
        TextView allnum;
        TextView appnum;
        TextView endday;

    }

}