package com.meishai.ui.fragment.tryuse.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.entiy.TryInfo;
import com.meishai.net.VolleyHelper;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.NetworkImageView;

/**
 * 试用新首页Adapter
 *
 * @author sh
 */
public class TryListAdapter extends BaseAdapter {
    private Context context;
    private List<TryInfo> infos;
    private ImageLoader imageLoader = null;

    public TryListAdapter(Context mContext, List<TryInfo> infos) {
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
            convertView = inflater.inflate(R.layout.tryuse_child_n_item, null);
            holder.pic_phone = (NetworkImageView) convertView
                    .findViewById(R.id.pic_phone);
            holder.btn_daren = (Button) convertView
                    .findViewById(R.id.btn_daren);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.allnum = (TextView) convertView.findViewById(R.id.allnum);
            holder.appnum = (TextView) convertView.findViewById(R.id.appnum);
            holder.endday = (TextView) convertView.findViewById(R.id.endday);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        TryInfo info = infos.get(position);
        if (info.getIsdaren() == 1) {
            holder.btn_daren.setVisibility(View.VISIBLE);
        } else {
            holder.btn_daren.setVisibility(View.GONE);
        }
        holder.pic_phone.setImageUrl(info.getPic_phone(), imageLoader);
        holder.title.setText(info.getTitle());
        String tryuserAllnum = context.getString(R.string.tryuser_allnum);
        holder.allnum.setText(String.format(tryuserAllnum, info.getAllnum()));
        String tryuserAppnum = context.getString(R.string.tryuser_appnum_n);
        holder.appnum.setText(String.format(tryuserAppnum, info.getAppnum()));
        String tryuserEndday = context.getString(R.string.tryuser_endday);
        if (info.getEndday() > 0) {
            holder.endday
                    .setText(String.format(tryuserEndday, info.getEndday()));
        } else {
            holder.endday.setText(R.string.has_finish);
        }
        return convertView;
    }

    public class ViewHolder {
        private NetworkImageView pic_phone;
        private Button btn_daren;
        private TextView title;
        private TextView allnum;
        private TextView appnum;
        private TextView endday;
    }

}
