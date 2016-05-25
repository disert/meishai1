package com.meishai.ui.fragment.meiwu.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.entiy.PointExchange;
import com.meishai.net.VolleyHelper;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.NetworkImageView;

/**
 * 积分
 *
 * @author sh
 */
public class FindPointListAdapter extends BaseAdapter {

    private Context context;
    private List<PointExchange> pointExchanges;
    private ImageLoader imageLoader = null;
    private LayoutInflater inflater = null;

    public FindPointListAdapter(Context mContext,
                                List<PointExchange> pointExchanges) {
        super();
        this.context = mContext;
        this.pointExchanges = pointExchanges;
        inflater = LayoutInflater.from(context);
        imageLoader = VolleyHelper.getImageLoader(mContext);
    }

    public void setPointExchanges(List<PointExchange> pointExchanges) {
        this.pointExchanges = pointExchanges;
    }

    @Override
    public int getCount() {
        return pointExchanges.size();
    }

    @Override
    public Object getItem(int position) {
        return pointExchanges.get(position);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PointViewHolder holder = null;
        if (null == convertView) {
            holder = new PointViewHolder();
            convertView = inflater.inflate(R.layout.find_point_item, null);
            holder.lay_line = (LinearLayout) convertView
                    .findViewById(R.id.lay_line);
            holder.thumb = (NetworkImageView) convertView
                    .findViewById(R.id.thumb);
            holder.btn_daren = (Button) convertView
                    .findViewById(R.id.btn_daren);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.price = (TextView) convertView.findViewById(R.id.price);
            holder.allnum = (TextView) convertView.findViewById(R.id.allnum);
            holder.groupid = (TextView) convertView.findViewById(R.id.groupid);
            holder.lowpoint = (TextView) convertView
                    .findViewById(R.id.lowpoint);
            holder.type_image = (NetworkImageView) convertView
                    .findViewById(R.id.type_image);
            convertView.setTag(holder);
        } else {
            holder = (PointViewHolder) convertView.getTag();
        }
        if (position == 0) {
            holder.lay_line.setVisibility(View.VISIBLE);
        } else {
            holder.lay_line.setVisibility(View.GONE);
        }
        PointExchange exchange = pointExchanges.get(position);
        holder.thumb.setErrorImageResId(R.drawable.ob_db);
        holder.thumb.setDefaultImageResId(R.drawable.ob_db);
        holder.thumb.setImageUrl(exchange.getThumb(), imageLoader);
        if (exchange.getIsdaren() == 1) {
            holder.btn_daren.setVisibility(View.VISIBLE);
        }
        holder.title.setText(exchange.getTitle());
//		String findPointPrice = context.getString(R.string.find_point_price);
        holder.price
                .setText(exchange.getPrice());
//		String findPointAllnum = context.getString(R.string.find_point_allnum);
        holder.allnum.setText(exchange.getAllnum());
//		String findPointGroupId = context
//				.getString(R.string.find_point_groupid);
//		holder.groupid.setText(String.format(findPointGroupId,
//				exchange.getGroupid()));
        holder.groupid.setText(exchange.getGroupid());
//		String findPointLowpoint = context
//				.getString(R.string.find_point_lowpoint);
        holder.lowpoint.setText(exchange.getLowpoint());
        holder.type_image.setImageUrl(exchange.getIcon(), imageLoader);
        // String sType = String.valueOf(exchange.getType());
        // if (PointExchangeType.POINT_CRAZY.getType().equals(sType)) {
        // holder.type_image.setImageResource(R.drawable.point_crazy);
        // } else if (PointExchangeType.POINT_LUCK.getType().equals(sType)) {
        // holder.type_image.setImageResource(R.drawable.point_luck);
        // } else if (PointExchangeType.POINT_EXCHANGE.getType().equals(sType))
        // {
        // holder.type_image.setImageResource(R.drawable.point_exchange);
        // } else {
        // holder.type_image.setImageResource(R.drawable.point_exchange);
        // }
        return convertView;
    }

    class PointViewHolder {
        private LinearLayout lay_line;
        private NetworkImageView thumb;
        private Button btn_daren;
        private TextView title;
        private TextView price;
        private TextView allnum;
        private TextView groupid;
        private TextView lowpoint;
        private NetworkImageView type_image;
    }

}
