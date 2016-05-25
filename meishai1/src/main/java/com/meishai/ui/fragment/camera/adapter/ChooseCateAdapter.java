package com.meishai.ui.fragment.camera.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.app.widget.CircleNetWorkImageView;
import com.meishai.entiy.CateInfo;
import com.meishai.net.VolleyHelper;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.ui.fragment.home.HomeCateActivity;

public class ChooseCateAdapter extends BaseAdapter {

    private Context context;
    private ImageLoader imageLoader = null;
    private List<CateInfo> cateInfos;

    private CateInfo cate = null;

    public ChooseCateAdapter(Context mContext, List<CateInfo> cateInfos) {
        this.context = mContext;
        imageLoader = VolleyHelper.getImageLoader(mContext);
        this.cateInfos = cateInfos;
    }

    public void setCate(CateInfo cate) {
        this.cate = cate;
    }

    public void setCateInfos(List<CateInfo> cateInfos) {
        this.cateInfos = cateInfos;
    }

    @Override
    public int getCount() {
        return cateInfos.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        return cateInfos.get(position);
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
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.release_cate_item, null);
            holder.image = (CircleNetWorkImageView) convertView
                    .findViewById(R.id.image);
            holder.img_add = (ImageView) convertView.findViewById(R.id.img_add);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (position == cateInfos.size()) {
            holder.image.setVisibility(View.GONE);
            holder.name.setText(R.string.add);
            holder.img_add.setVisibility(View.VISIBLE);
            holder.img_add.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    context.startActivity(HomeCateActivity.newIntent());
                }
            });
        } else {
            holder.img_add.setVisibility(View.GONE);
            holder.name.setVisibility(View.VISIBLE);
            holder.image.setVisibility(View.VISIBLE);
            CateInfo cateInfo = cateInfos.get(position);
            holder.image.setRoundness(6f);
            holder.image.setImageUrl(cateInfo.getImage(), imageLoader);
            holder.name.setText(cateInfo.getName());
            if (null != cate) {
                if (cate.getCid() == cateInfo.getCid()) {
                    convertView.setBackgroundColor(context.getResources()
                            .getColor(R.color.grid_item_press));
                }
            }
        }
        return convertView;
    }

    class ViewHolder {
        private CircleNetWorkImageView image;
        private ImageView img_add;
        private TextView name;
    }

}
