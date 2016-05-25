package com.meishai.ui.fragment.usercenter.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.app.util.StringUtil;
import com.meishai.app.widget.CircleNetWorkImageView;
import com.meishai.entiy.UserMoney;
import com.meishai.net.VolleyHelper;
import com.meishai.net.volley.toolbox.ImageLoader;

/**
 * 我的资金
 *
 * @author sh
 */
public class MoneyAdapter extends BaseAdapter {

    private Context mContext;
    private ImageLoader imageLoader = null;
    private List<UserMoney> moneys = null;

    public MoneyAdapter(Context context, List<UserMoney> moneys) {
        this.mContext = context;
        this.moneys = moneys;
        imageLoader = VolleyHelper.getImageLoader(mContext);

    }

    public void setMoneys(List<UserMoney> moneys) {
        this.moneys = moneys;
    }

    @Override
    public int getCount() {
        return moneys.size();
    }

    @Override
    public Object getItem(int position) {
        return moneys.get(position);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MoneyViewHolder holder = null;
        if (null == convertView) {
            holder = new MoneyViewHolder();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.user_money_item, null);
            holder.mThumb = (CircleNetWorkImageView) convertView
                    .findViewById(R.id.thumb);
            holder.mTitle = (TextView) convertView.findViewById(R.id.title);
            holder.mValue = (TextView) convertView.findViewById(R.id.value);
            holder.mTime = (TextView) convertView.findViewById(R.id.time);
            holder.mStatus = (TextView) convertView.findViewById(R.id.status);
            convertView.setTag(holder);

        } else {
            holder = (MoneyViewHolder) convertView.getTag();
        }

        UserMoney money = this.moneys.get(position);
        holder.mThumb.setImageUrl(money.getThumb(), imageLoader);
        holder.mTitle.setText(money.getTitle());
        holder.mTime.setText(money.getTime());
        holder.mStatus.setText(money.getStatus());
        if (StringUtil.isNotBlank(money.getValue())) {
            holder.mValue.setText(String.format(
                    mContext.getString(R.string.txt_money_value),
                    money.getValue()));
        } else {
            holder.mValue.setText("");
        }
        return convertView;
    }

    class MoneyViewHolder {
        private CircleNetWorkImageView mThumb;
        private TextView mTitle;
        private TextView mValue;
        private TextView mTime;
        private TextView mStatus;
    }

}
