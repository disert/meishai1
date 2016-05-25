package com.meishai.ui.fragment.home.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.app.widget.CircleNetWorkImageView;
import com.meishai.app.widget.CustomProgress;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.Master;
import com.meishai.entiy.UserFans;
import com.meishai.net.RespData;
import com.meishai.net.VolleyHelper;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.common.req.PublicReq;
import com.meishai.ui.fragment.home.HomePageActivity;
import com.meishai.util.AndroidUtil;

public class HomepageFansAdapter extends BaseAdapter {
    private Context mContext = null;
    private List<UserFans> userFans = null;
    private ImageLoader imageLoader = null;
    private LayoutInflater inflater = null;
    private CustomProgress mProgressDialog = null;

    public HomepageFansAdapter(Context context, List<UserFans> userFans) {
        super();
        this.mContext = context;
        this.userFans = userFans;
        inflater = LayoutInflater.from(context);
        imageLoader = VolleyHelper.getImageLoader(mContext);
    }

    public void setUserFans(List<UserFans> userFans) {
        this.userFans = userFans;
    }

    @Override
    public int getCount() {
        return userFans.size();
    }

    @Override
    public Object getItem(int position) {
        return userFans.get(position);
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
            convertView = inflater.inflate(R.layout.homepage_fans_item, null);
            holder.lay_line = (LinearLayout) convertView
                    .findViewById(R.id.lay_line);
            holder.lay_homepage = (RelativeLayout) convertView
                    .findViewById(R.id.lay_homepage);
            holder.lay_homepage = (RelativeLayout) convertView
                    .findViewById(R.id.lay_homepage);
            holder.avatar = (CircleNetWorkImageView) convertView
                    .findViewById(R.id.avatar);
            holder.avatar.setRoundness(4.5f);
            holder.username = (TextView) convertView
                    .findViewById(R.id.username);
            holder.post_num = (TextView) convertView
                    .findViewById(R.id.post_num);
            holder.fans_num = (TextView) convertView
                    .findViewById(R.id.fans_num);
            holder.zan_num = (TextView) convertView.findViewById(R.id.zan_num);
            holder.btn_attention = (ImageButton) convertView
                    .findViewById(R.id.btn_attention);
            holder.btn_no_attention = (ImageButton) convertView
                    .findViewById(R.id.btn_no_attention);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (position == 0) {
            holder.lay_line.setVisibility(View.VISIBLE);
        } else {
            holder.lay_line.setVisibility(View.GONE);
        }
        UserFans fans = userFans.get(position);
        holder.avatar.setImageUrl(fans.getAvatar(), imageLoader);
        holder.username.setText(fans.getUsername());
        String fPostNum = mContext.getString(R.string.homepage_post_num);
        holder.post_num.setText(String.format(fPostNum, fans.getPost_num()));
        String fFansNum = mContext.getString(R.string.homepage_fans_num);
        holder.fans_num.setText(String.format(fFansNum, fans.getFans_num()));
        String fZanNum = mContext.getString(R.string.homepage_zan_num);
        holder.zan_num.setText(String.format(fZanNum, fans.getZan_num()));
        holder.btn_attention.setTag(position);
        holder.btn_no_attention.setTag(position);
        if (fans.getIsattention() == Master.NO_ATENTION) {
            holder.btn_attention.setVisibility(View.GONE);
            holder.btn_no_attention.setVisibility(View.VISIBLE);
            holder.btn_no_attention
                    .setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            int p = (Integer) v.getTag();
                            addfriend(p);
                        }
                    });
        } else {
            holder.btn_attention.setVisibility(View.VISIBLE);
            holder.btn_no_attention.setVisibility(View.GONE);
            holder.btn_attention.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int p = (Integer) v.getTag();
                    delfriend((int) p);
                }
            });
        }
        holder.lay_homepage.setTag(String.valueOf(fans.getUserid()));
        holder.lay_homepage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, HomePageActivity.class);
                intent.putExtra(ConstantSet.USERID, v.getTag().toString());
                mContext.startActivity(intent);
            }
        });
        return convertView;
    }

    /**
     * 添加关注
     *
     * @param position
     */
    private void addfriend(int position) {
        final UserFans m = userFans.get(position);
        if (null == m) {
            return;
        }
        String message = mContext.getString(R.string.add_att_wait);
        if (null == mProgressDialog) {
            mProgressDialog = CustomProgress
                    .show(mContext, message, true, null);
        } else {
            mProgressDialog.setMessage(message);
            mProgressDialog.show();
        }
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        data.put("fuserid", String.valueOf(m.getUserid()));
        PublicReq.addfriend(mContext, data, new Listener<RespData>() {

            @Override
            public void onResponse(RespData response) {
                if (null != mProgressDialog) {
                    mProgressDialog.hide();
                }
                if (response.isSuccess()) {
                    m.setIsattention(Master.HAS_ATENTION);
                    notifyDataSetChanged();
                } else {
                    AndroidUtil.showToast(response.getTips());
                }
            }

        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (null != mProgressDialog) {
                    mProgressDialog.hide();
                }
                AndroidUtil.showToast(mContext.getString(R.string.reqFailed));
            }
        });
    }

    /**
     * 取消关注
     *
     * @param position
     */
    private void delfriend(int position) {
        final UserFans m = userFans.get(position);
        if (null == m) {
            return;
        }
        String message = mContext.getString(R.string.can_att_wait);
        if (null == mProgressDialog) {
            mProgressDialog = CustomProgress
                    .show(mContext, message, true, null);
        } else {
            mProgressDialog.setMessage(message);
            mProgressDialog.show();
        }
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        data.put("fuserid", String.valueOf(m.getUserid()));
        PublicReq.delfriend(mContext, data, new Listener<RespData>() {

            @Override
            public void onResponse(RespData response) {
                if (null != mProgressDialog) {
                    mProgressDialog.hide();
                }
                if (response.isSuccess()) {
                    m.setIsattention(Master.NO_ATENTION);
                    notifyDataSetChanged();
                } else {
                    AndroidUtil.showToast(response.getTips());
                }
            }

        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (null != mProgressDialog) {
                    mProgressDialog.hide();
                }
                AndroidUtil.showToast(mContext.getString(R.string.reqFailed));
            }
        });
    }

    class ViewHolder {
        private LinearLayout lay_line;
        private RelativeLayout lay_homepage;
        private CircleNetWorkImageView avatar;
        private TextView username;
        private TextView post_num;
        private TextView fans_num;
        private TextView zan_num;
        private ImageButton btn_attention;
        // 未关注 可以加关注
        private ImageButton btn_no_attention;
    }

}
