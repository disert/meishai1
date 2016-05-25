package com.meishai.ui.fragment.usercenter.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.entiy.TrialInfo;
import com.meishai.net.VolleyHelper;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.NetworkImageView;

public class FavTryAdapter extends BaseAdapter {

    private List<TrialInfo> tInfos = null;
    private Context mContext;
    private ImageLoader imageLoader = null;

    public FavTryAdapter(Context context, List<TrialInfo> trialInfos) {
        this.mContext = context;
        this.tInfos = trialInfos;
        imageLoader = VolleyHelper.getImageLoader(mContext);

    }

    public void settInfos(List<TrialInfo> tInfos) {
        this.tInfos = tInfos;
    }

    @Override
    public int getCount() {
        return tInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return tInfos.get(position);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (null == convertView) {
            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.user_fav_try_item, null);
            holder.lay_line = (LinearLayout) convertView
                    .findViewById(R.id.lay_line);
            holder.tr_title = (TextView) convertView
                    .findViewById(R.id.tr_title);
            holder.thumb = (NetworkImageView) convertView
                    .findViewById(R.id.thumb);
            holder.stype_text = (TextView) convertView
                    .findViewById(R.id.stype_text);
            holder.price = (TextView) convertView.findViewById(R.id.price);
            holder.fee = (TextView) convertView.findViewById(R.id.fee);
            holder.otype_text = (TextView) convertView
                    .findViewById(R.id.otype_text);
            holder.fprice = (TextView) convertView.findViewById(R.id.fprice);
            holder.hprice = (TextView) convertView.findViewById(R.id.hprice);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (position == 0) {
            holder.lay_line.setVisibility(View.VISIBLE);
        } else {
            holder.lay_line.setVisibility(View.GONE);
        }
        TrialInfo info = this.tInfos.get(position);
        holder.tr_title.setText(info.getTitle());
        holder.thumb.setImageUrl(info.getThumb(), imageLoader);
        holder.stype_text.setTextColor(Color.parseColor(info.getStypeColor()));
        holder.stype_text.setText(info.getStypeText());
        holder.price.setText(String.format(mContext.getString(R.string.price),
                info.getPrice()));
        holder.fee.setText(info.getFee());
        holder.otype_text.setText(info.getOtype_text());
        holder.fprice.setText(String.format(
                mContext.getString(R.string.fprice), info.getFprice()));
        if (info.getHprice() > 0) {
            holder.hprice.setText(String.format(
                    mContext.getString(R.string.hprice), info.getHprice()));
            holder.hprice.setVisibility(View.VISIBLE);
        } else {
            holder.hprice.setVisibility(View.GONE);
        }
        return convertView;
    }

    class ViewHolder {
        private LinearLayout lay_line;
        // 试用编号+试用标题
        private TextView tr_title;
        // 试用图片
        private NetworkImageView thumb;
        // 试用类型
        private TextView stype_text;
        // 下单价格
        private TextView price;
        // 邮费状态
        private TextView fee;
        // 下单类型
        private TextView otype_text;
        // 返还金额
        private TextView fprice;
        // 红包金额
        private TextView hprice;
        // 查看详情
        private Button mBtnDetail;
    }
}
