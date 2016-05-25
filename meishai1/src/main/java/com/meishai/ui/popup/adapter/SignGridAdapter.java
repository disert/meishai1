package com.meishai.ui.popup.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.app.widget.CircleTextView;
import com.meishai.entiy.SignPoint;

public class SignGridAdapter extends BaseAdapter {

    private Context mContext;
    private List<SignPoint> points;
    private LayoutInflater inflater = null;

    public SignGridAdapter(Context mContext, List<SignPoint> points) {
        super();
        this.mContext = mContext;
        this.points = points;
        inflater = LayoutInflater.from(mContext);
    }

    public void setPoints(List<SignPoint> points) {
        this.points = points;
    }

    @Override
    public int getCount() {
        return points.size();
    }

    @Override
    public Object getItem(int position) {
        return points.get(position);
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
            convertView = inflater.inflate(R.layout.home_sign_item, null);
            holder.day = (TextView) convertView.findViewById(R.id.day);
            holder.point = (CircleTextView) convertView
                    .findViewById(R.id.point);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        SignPoint signPoint = points.get(position);
        if (signPoint.getDay() > 0) {
            holder.day.setText(String.valueOf(signPoint.getDay()));
            if (signPoint.getPoint() > 0) {
                if (signPoint.getIstoday() == SignPoint.TODAY) {
                    holder.point.setBackgroundColor(mContext.getResources()
                            .getColor(R.color.red));
                } else {
                    holder.point.setBackgroundColor(mContext.getResources()
                            .getColor(R.color.gran_02cf));
                }
                holder.point.setText(String.valueOf(signPoint.getPoint()));
            }
        } else {
            holder.day.setText("");
            holder.point.setVisibility(View.GONE);
        }
        return convertView;
    }

    class ViewHolder {
        private TextView day;
        private CircleTextView point;
    }

}
