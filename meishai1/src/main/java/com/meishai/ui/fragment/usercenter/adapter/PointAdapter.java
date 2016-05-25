package com.meishai.ui.fragment.usercenter.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.entiy.UserPoint;

/**
 * 我的积分
 *
 * @author sh
 */
public class PointAdapter extends BaseAdapter {

    private Context mContext;
    private List<UserPoint> points = null;

    public PointAdapter(Context context, List<UserPoint> points) {
        this.mContext = context;
        this.points = points;

    }

    public void setPoints(List<UserPoint> points) {
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
        PointViewHolder holder = null;
        if (null == convertView) {
            holder = new PointViewHolder();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.user_point_item, null);
            holder.mPointTime = (TextView) convertView
                    .findViewById(R.id.point_time);
            holder.mPointTitle = (TextView) convertView
                    .findViewById(R.id.point_title);
            holder.mPointValue = (TextView) convertView
                    .findViewById(R.id.point_value);
            holder.line_view = (View) convertView.findViewById(R.id.line_view);
            convertView.setTag(holder);

        } else {
            holder = (PointViewHolder) convertView.getTag();
        }

        UserPoint point = this.points.get(position);
        holder.mPointTime.setText(point.getTime());
        holder.mPointTitle.setText(point.getTitle());
        holder.mPointValue.setText(point.getValue());
        if (this.points.size() + 1 == position) {
            holder.line_view.setVisibility(View.GONE);
        }
        return convertView;
    }

    class PointViewHolder {
        private TextView mPointTime;
        private TextView mPointTitle;
        private TextView mPointValue;
        private View line_view;
    }

}
