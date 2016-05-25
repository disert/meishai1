package com.meishai.ui.fragment.home.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.app.widget.RoundCornerImageView;
import com.meishai.entiy.CateResponseData.CateInfo1;
import com.meishai.entiy.ReleaseData;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.ListImageListener;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.camera.ChooseImageActivity;
import com.meishai.ui.fragment.camera.ImageChooseActivity1;

/**
 * 分类的activity的适配器
 */
public class HomeCateAdapter1 extends BaseAdapter {
    private List<CateInfo1> datas = null;
    private LayoutInflater inflater = null;
    private ImageLoader imageLoader;
    private Context mContext;

    public HomeCateAdapter1(Context context, List<CateInfo1> datas,
                            ImageLoader imageLoader) {
        super();
        this.datas = datas;
        this.imageLoader = imageLoader;
        mContext = context;
        inflater = LayoutInflater.from(context);
    }

    public void setDatas(List<CateInfo1> datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (datas != null) {
            return datas.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.home_cate_catalog_item1,
                    null);
            holder.image = (RoundCornerImageView) convertView
                    .findViewById(R.id.image);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.details = (ImageButton) convertView
                    .findViewById(R.id.details);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        CateInfo1 item = (CateInfo1) getItem(position);
        // 图片加载
        holder.image.setTag(item.image);
        ListImageListener listener = new ListImageListener(holder.image,
                R.drawable.head_default, R.drawable.head_default, item.image);
        imageLoader.get(item.image, listener);

        // 设置name
        holder.name.setText(item.title);
        holder.details.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                CateInfo1 info = (CateInfo1) getItem(position);
                ReleaseData data = new ReleaseData();
                data.setTid(info.tid);
                Intent intent = ImageChooseActivity1.newIntent(data, 0);
//				Intent intent = ChooseImageActivity.newIntent(data, ConstantSet.MAX_IMAGE_COUNT);
                mContext.startActivity(intent);
            }
        });


        return convertView;
    }

    class ViewHolder {
        private ImageButton details;
        private TextView name;
        private RoundCornerImageView image;
    }
}
