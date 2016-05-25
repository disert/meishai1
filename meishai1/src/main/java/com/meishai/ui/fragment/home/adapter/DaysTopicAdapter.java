package com.meishai.ui.fragment.home.adapter;

import android.content.Context;
import android.graphics.Point;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.app.widget.layout.SampleRoundImageView;
import com.meishai.entiy.DaysTopicResponseBean;
import com.meishai.entiy.HomePageDatas;
import com.meishai.entiy.PostItem;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.ListImageListener;
import com.meishai.ui.fragment.home.MoreTempidActivity;
import com.meishai.ui.fragment.home.PostShowActivity;
import com.meishai.ui.fragment.home.TopicShowActivity;
import com.meishai.util.AndroidUtil;
import com.meishai.util.ImageAdapter;

/**
 * Created by Administrator on 2016/5/21.
 */
public class DaysTopicAdapter extends ListItemAdapter<DaysTopicResponseBean.ListBean> {
    private final Context mContext;

    public DaysTopicAdapter(Context context) {
        mContext = context;
    }

    @Override
    public View getConvertView(int position, View convertView, ViewGroup parent, final DaysTopicResponseBean.ListBean item, ImageLoader imageLoader) {
        Holder holder;
        if (convertView == null) {
            holder = new Holder();
            SampleRoundImageView view = new SampleRoundImageView(mContext);
            convertView = view;
            holder.image = view.getImageView();
            Point point = ImageAdapter.getViewRealWH(1, 0, 750, 380);
            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(point.x, point.y);
            convertView.setLayoutParams(lp);

            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        int padding = AndroidUtil.dip2px(4);
        if(position == 0){
            convertView.setPadding(padding, padding, padding, padding);
        }else {
            convertView.setPadding(padding, 0, padding, padding);
        }

        holder.item = item;
        holder.image.setTag(item.getPic_url());
        ListImageListener listener = new ListImageListener(holder.image, R.drawable.place_default, R.drawable.place_default, item.getPic_url());
        imageLoader.get(item.getPic_url(), listener);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Holder hold = (Holder) v.getTag();
                PostItem postItem = new PostItem();
                postItem.pid = hold.item.getPid();
                mContext.startActivity(PostShowActivity.newIntent(postItem, PostShowActivity.FROM_POST));
            }
        });

        return convertView;
    }

    class Holder {
        public ImageView image;
        public DaysTopicResponseBean.ListBean item;
    }
}
