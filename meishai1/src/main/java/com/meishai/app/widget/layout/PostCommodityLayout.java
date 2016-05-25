package com.meishai.app.widget.layout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.meishai.R;
import com.meishai.entiy.CommodityInfo;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.ListImageListener;
import com.meishai.ui.fragment.MeishaiWebviewActivity;
import com.meishai.util.AndroidUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 帖子->商品
 *
 * @author li
 */
public class PostCommodityLayout extends LinearLayout {

    private List<CommodityInfo> mListData;
    private LinearLayout mContainer;

    public PostCommodityLayout(Context context) {
        super(context);
        init();
    }

    private void init() {
        mListData = new ArrayList<CommodityInfo>();
    }


    public void setData(List<CommodityInfo> list, ImageLoader imageLoader) {
        if (list.size() == 0 || list == mListData) {
            return;
        }
        mListData = list;

        removeAllViews();

        View root = LayoutInflater.from(getContext()).inflate(R.layout.post_commodity_container, this, true);
        LinearLayout container = (LinearLayout) root.findViewById(R.id.container);

        container.removeAllViews();

        View v = LayoutInflater.from(getContext()).inflate(R.layout.post_commodity_item, null);

        //横线
        if (null != mListData && !mListData.isEmpty()) {
            container.addView(getLine());
        }
        for (CommodityInfo item : mListData) {

            ViewHolder holder = new ViewHolder();
            holder.thumb = (ImageView) v.findViewById(R.id.thumb_iv);
            holder.title = (TextView) v.findViewById(R.id.title_tv);
            holder.price = (TextView) v.findViewById(R.id.price_tv);

            try {
                ListImageListener l = new ListImageListener(holder.thumb, 0, 0,
                        item.thumb);
                holder.thumb.setTag(item.thumb);
                imageLoader.get(item.thumb, l);
                holder.title.setText(item.title);
                holder.price.setText("￥" + item.price + "元");

                final String url = item.url;
                v.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        getContext().startActivity(
                                MeishaiWebviewActivity.newIntent(url));
                    }
                });

                container.addView(v);
                container.addView(getLine());
            } catch (Exception e) {
                e.printStackTrace();
            }

//			View line = new View(getContext());
//			line.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, AndroidUtil.dip2px(0.5f)));
//			line.setBackgroundColor(lineColor);

        }

    }

    private View getLine() {
        int lineColor = getResources().getColor(R.color.line_color);
        View line = new View(getContext());
        line.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, AndroidUtil.dip2px(0.5f)));
        line.setBackgroundColor(lineColor);
        return line;
    }


    public static class ViewHolder {
        ImageView thumb;
        TextView title;
        TextView price;
    }
}
