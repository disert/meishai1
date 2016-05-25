package com.meishai.ui.fragment.usercenter.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.entiy.ReleaseData;
import com.meishai.entiy.UserWelfare;
import com.meishai.net.VolleyHelper;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.NetworkImageView;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.camera.ChooseImageActivity;
import com.meishai.ui.fragment.meiwu.FindPointDetailActivity;

/**
 * 我的福利
 *
 * @author sh
 */
public class WelfareAdapter extends BaseAdapter {
    private Context context;
    private ImageLoader imageLoader = null;
    private List<UserWelfare> welfares = null;

    public WelfareAdapter(Context context, List<UserWelfare> welfares) {
        super();
        this.context = context;
        this.imageLoader = VolleyHelper.getImageLoader(context);
        this.welfares = welfares;
    }

    public void setWelfares(List<UserWelfare> welfares) {
        this.welfares = welfares;
    }

    @Override
    public int getCount() {
        return welfares.size();
    }

    @Override
    public Object getItem(int position) {
        return welfares.get(position);
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
            convertView = inflater.inflate(R.layout.user_welfare_item, null);
            holder.lay_wal = (RelativeLayout) convertView
                    .findViewById(R.id.lay_wal);
            holder.lay_line = (LinearLayout) convertView
                    .findViewById(R.id.lay_line);
            holder.thumb = (NetworkImageView) convertView
                    .findViewById(R.id.thumb);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.typename = (TextView) convertView
                    .findViewById(R.id.typename);
            holder.expressname = (TextView) convertView
                    .findViewById(R.id.expressname);
            holder.expressno = (TextView) convertView
                    .findViewById(R.id.expressno);
            holder.isshare = (Button) convertView.findViewById(R.id.isshare);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (position == 0) {
            holder.lay_line.setVisibility(View.VISIBLE);
        } else {
            holder.lay_line.setVisibility(View.GONE);
        }
        final UserWelfare welfare = welfares.get(position);

        holder.lay_wal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(FindPointDetailActivity.newIntent(
                        welfare.getGid(), ""));
            }
        });

        holder.thumb.setImageUrl(welfare.getThumb(), imageLoader);
        holder.title.setText(welfare.getTitle());
        String fTypename = context.getResources().getString(
                R.string.txt_typename);
        holder.typename
                .setText(String.format(fTypename, welfare.getTypename()));
        String fExpressname = context.getResources().getString(
                R.string.txt_expressname);
        holder.expressname.setText(String.format(fExpressname,
                welfare.getExpressname()));
        String fExpressno = context.getResources().getString(
                R.string.txt_expressno);
        holder.expressno.setText(String.format(fExpressno,
                welfare.getExpressno()));
        if (welfare.getIsshare() == UserWelfare.HAS_SHARE) {
            holder.isshare
                    .setBackgroundResource(R.drawable.btn_has_share_shape);
            holder.isshare.setText(context.getResources().getString(
                    R.string.txt_has_share));
        } else {
            holder.isshare
                    .setBackgroundResource(R.drawable.btn_sign_point_selector);
            holder.isshare.setText(context.getResources().getString(
                    R.string.txt_isshare));
            holder.isshare.setTag(welfare.getGid());
            holder.isshare.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    long gid = (Long) v.getTag();
                    ReleaseData releaseData = new ReleaseData();
//					releaseData.setAid(aid);
//					Intent intent = ImageChooseActivity1.newIntent(releaseData, CameraActivity1.ADD_IMAGE);
                    releaseData.setGid((int) gid);
                    Intent intent = ChooseImageActivity.newIntent(releaseData, ConstantSet.MAX_IMAGE_COUNT);
                    context.startActivity(intent);
                }
            });

        }
        return convertView;
    }

    class ViewHolder {
        private RelativeLayout lay_wal;
        private LinearLayout lay_line;
        private NetworkImageView thumb;
        private TextView title;
        // 渠道
        private TextView typename;
        // 快递名称
        private TextView expressname;
        // 快递单号
        private TextView expressno;
        private Button isshare;
    }
}
