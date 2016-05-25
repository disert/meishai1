package com.meishai.ui.fragment.meiwu.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.app.widget.CircleNetWorkImageView;
import com.meishai.entiy.SaleCate;
import com.meishai.net.VolleyHelper;
import com.meishai.net.volley.toolbox.ImageLoader;

/**
 * 特价 ->分类 适配器
 *
 * @author sh
 */
public class SaleCateAdapter extends BaseAdapter {

    private Context context;
    private List<SaleCate> cates;
    private ImageLoader imageLoader = null;

    public SaleCateAdapter(Context mContext, List<SaleCate> cates) {
        this.context = mContext;
        this.cates = cates;
        imageLoader = VolleyHelper.getImageLoader(mContext);
    }

    @Override
    public int getCount() {
        return cates.size();
    }

    public void setCates(List<SaleCate> cates) {
        this.cates = cates;
    }

    @Override
    public Object getItem(int position) {
        return cates.get(position);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CateViewHolder holder = null;
        if (null == convertView) {
            holder = new CateViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.find_sale_catalog_item,
                    null);
            holder.image = (CircleNetWorkImageView) convertView
                    .findViewById(R.id.image);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            convertView.setTag(holder);
        } else {
            holder = (CateViewHolder) convertView.getTag();
        }
        SaleCate cate = cates.get(position);
        holder.image.setDefaultImageResId(R.drawable.ob_db);
        holder.image.setErrorImageResId(R.drawable.ob_db);
        holder.image.setImageUrl(cate.getImage(), imageLoader);
        holder.name.setText(cate.getName());
        return convertView;
    }

    class CateViewHolder {
        private CircleNetWorkImageView image;
        private TextView name;

    }
}
