package com.meishai.ui.fragment.usercenter.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.entiy.UserCredit;

public class CreditAdapter extends BaseAdapter {

    private Context mContext;
    private List<UserCredit> credits = null;

    public CreditAdapter(Context context, List<UserCredit> credits) {
        this.mContext = context;
        this.credits = credits;

    }

    public void setCredits(List<UserCredit> credits) {
        this.credits = credits;
    }

    @Override
    public int getCount() {
        return credits.size();
    }

    @Override
    public Object getItem(int position) {
        return credits.get(position);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CreditViewHolder holder = null;
        if (null == convertView) {
            holder = new CreditViewHolder();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.user_credit_item, null);
            holder.mCreditTime = (TextView) convertView
                    .findViewById(R.id.credit_time);
            holder.mCreditTitle = (TextView) convertView
                    .findViewById(R.id.credit_title);
            holder.mCreditValue = (TextView) convertView
                    .findViewById(R.id.credit_value);
            holder.line_view = (View) convertView.findViewById(R.id.line_view);
            convertView.setTag(holder);

        } else {
            holder = (CreditViewHolder) convertView.getTag();
        }

        UserCredit credit = this.credits.get(position);
        holder.mCreditTime.setText(credit.getTime());
        holder.mCreditTitle.setText(credit.getTitle());
        holder.mCreditValue.setText(credit.getValue());
        if (this.credits.size() + 1 == position) {
            holder.line_view.setVisibility(View.GONE);
        }
        return convertView;
    }

    class CreditViewHolder {
        private TextView mCreditTime;
        private TextView mCreditTitle;
        private TextView mCreditValue;
        private View line_view;
    }

}
