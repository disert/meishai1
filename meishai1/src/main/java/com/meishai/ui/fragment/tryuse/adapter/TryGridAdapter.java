package com.meishai.ui.fragment.tryuse.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.app.util.DateUtil;
import com.meishai.entiy.TryInfo;
import com.meishai.net.VolleyHelper;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.NetworkImageView;

public class TryGridAdapter extends BaseAdapter {
    private Context context;
    private List<TryInfo> infos;
    private ImageLoader imageLoader = null;

    // private Map<Long, CountDownTextView> countDownMap = new HashMap<Long,
    // CountDownTextView>();

    public TryGridAdapter(Context mContext, List<TryInfo> infos) {
        super();
        this.context = mContext;
        this.infos = infos;
        imageLoader = VolleyHelper.getImageLoader(mContext);
    }

    public void setInfos(List<TryInfo> infos) {
        this.infos = infos;
    }

    @Override
    public int getCount() {
        return infos.size();
    }

    @Override
    public Object getItem(int position) {
        return infos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (null == convertView) {
            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.tryuse_child_item, null);
            holder.thumb = (NetworkImageView) convertView
                    .findViewById(R.id.thumb);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.price = (TextView) convertView.findViewById(R.id.price);
            holder.snum = (TextView) convertView.findViewById(R.id.snum);
            holder.endtime = (TextView) convertView.findViewById(R.id.endtime);
            holder.appnum = (TextView) convertView.findViewById(R.id.appnum);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        TryInfo info = infos.get(position);
        holder.thumb.setImageUrl(info.getThumb(), imageLoader);
        holder.title.setText(info.getTitle());
        String tryuserPrice = context.getString(R.string.tryuser_price);
        holder.price.setText(String.format(tryuserPrice, info.getPrice()));
        String tryuserSnum = context.getString(R.string.tryuser_snum);
        holder.snum.setText(String.format(tryuserSnum, info.getSnum()));
        String tryuserAppnum = context.getString(R.string.tryuser_appnum);
        holder.endtime.setText(DateUtil.timeFormat(info.getEndtime()));
        // CountDownTextView countDownTextView = null;
        // if (countDownMap.containsKey(info.getSid())) {
        // countDownTextView = countDownMap.get(info.getSid());
        // countDownTextView.cancel();
        // }
        // countDownTextView = new CountDownTextView(context, holder.endtime,
        // "",
        // info.getEndtime());
        // countDownMap.put(info.getSid(), countDownTextView);
        // countDownTextView.start();

        holder.appnum.setText(String.format(tryuserAppnum, info.getAppnum()));
        return convertView;
    }

    /**
     * 停止所有的计时器
     */
    // public void cancelAllDownView() {
    // if (null != countDownMap && !countDownMap.isEmpty()) {
    // for (Long sid : countDownMap.keySet()) {
    // countDownMap.get(sid).cancel();
    // }
    // countDownMap.clear();
    // }
    // }

    public class ViewHolder {
        private NetworkImageView thumb;
        private TextView title;
        private TextView price;
        private TextView snum;
        public TextView endtime;
        private TextView appnum;
    }

}
