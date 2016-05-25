package com.meishai.ui.fragment.meiwu.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.meishai.app.widget.CircleNetWorkImageView;
import com.meishai.app.widget.CustomProgress;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.Master;
import com.meishai.net.RespData;
import com.meishai.net.VolleyHelper;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.common.req.PublicReq;
import com.meishai.ui.fragment.meiwu.FindMasterFragment.OnCompleteListener;
import com.meishai.ui.fragment.home.HomePageActivity;
import com.meishai.util.AndroidUtil;

public class MasterAdapter extends BaseAdapter {
    private Context context;
    private List<Master> masters;
    private ImageLoader imageLoader = null;
    private CustomProgress mProgressDialog = null;
    private OnCompleteListener completeListener = null;

    public MasterAdapter(Context mContext, List<Master> masters) {
        this.context = mContext;
        this.masters = masters;
        imageLoader = VolleyHelper.getImageLoader(mContext);
    }

    public void setCompleteListener(OnCompleteListener completeListener) {
        this.completeListener = completeListener;
    }

    @Override
    public int getCount() {
        return masters.size();
    }

    public void setMasters(List<Master> masters) {
        this.masters = masters;
    }

    @Override
    public Object getItem(int position) {
        return masters.get(position);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MasterViewHolder holder = null;
        if (null == convertView) {
            holder = new MasterViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.find_master_item, null);
            holder.lay_line = (LinearLayout) convertView
                    .findViewById(R.id.lay_line);
            holder.lay_homepage = (RelativeLayout) convertView
                    .findViewById(R.id.lay_homepage);
            holder.avatar = (CircleNetWorkImageView) convertView
                    .findViewById(R.id.avatar);
            holder.username = (TextView) convertView
                    .findViewById(R.id.username);
            holder.areaname = (TextView) convertView
                    .findViewById(R.id.areaname);
            holder.intro = (TextView) convertView.findViewById(R.id.intro);
            holder.post_num = (TextView) convertView
                    .findViewById(R.id.post_num);
            holder.fans_num = (TextView) convertView
                    .findViewById(R.id.fans_num);
            holder.btn_attention = (Button) convertView
                    .findViewById(R.id.btn_attention);
            holder.btn_no_attention = (Button) convertView
                    .findViewById(R.id.btn_no_attention);
            convertView.setTag(holder);
        } else {
            holder = (MasterViewHolder) convertView.getTag();
        }
        if (position == 0) {
            holder.lay_line.setVisibility(View.VISIBLE);
        } else {
            holder.lay_line.setVisibility(View.GONE);
        }
        Master master = masters.get(position);
        holder.avatar.setDefaultImageResId(R.drawable.ob_db);
        holder.avatar.setErrorImageResId(R.drawable.ob_db);
        holder.avatar.setImageUrl(master.getAvatar(), imageLoader);
        holder.username.setText(master.getUsername());
        String fAreaName = context.getString(R.string.find_master_areaname);
        holder.areaname.setText(String.format(fAreaName, master.getAreaname()));
        holder.intro.setText(master.getIntro());
        String fPostNum = context.getString(R.string.find_master_post_num);
        holder.post_num.setText(String.format(fPostNum, master.getPost_num()));
        String fFansNum = context.getString(R.string.find_master_fans_num);
        holder.fans_num.setText(String.format(fFansNum, master.getFans_num()));
        holder.btn_attention.setTag(position);
        holder.btn_no_attention.setTag(position);
        if (master.getIsattention() == Master.NO_ATENTION) {
            holder.btn_attention.setVisibility(View.VISIBLE);
            holder.btn_no_attention.setVisibility(View.GONE);
            holder.btn_attention.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int p = (Integer) v.getTag();
                    addfriend((int) p);
                }
            });
        } else {
            holder.btn_attention.setVisibility(View.GONE);
            holder.btn_no_attention.setVisibility(View.VISIBLE);
            holder.btn_no_attention
                    .setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            int p = (Integer) v.getTag();
                            delfriend(p);
                        }
                    });
        }
        holder.lay_homepage.setTag(String.valueOf(master.getUserid()));
        holder.lay_homepage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, HomePageActivity.class);
                intent.putExtra(ConstantSet.USERID, v.getTag().toString());
                context.startActivity(intent);
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
        final Master m = masters.get(position);
        if (null == m) {
            return;
        }
        String message = context.getString(R.string.add_att_wait);
        if (null == mProgressDialog) {
            mProgressDialog = CustomProgress.show(context, message, true, null);
        } else {
            mProgressDialog.setMessage(message);
            mProgressDialog.show();
        }
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        data.put("fuserid", String.valueOf(m.getUserid()));
        PublicReq.addfriend(context, data, new Listener<RespData>() {

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
                AndroidUtil.showToast(context.getString(R.string.reqFailed));
            }
        });
    }

    /**
     * 取消关注
     *
     * @param position
     */
    private void delfriend(int position) {
        final Master m = masters.get(position);
        if (null == m) {
            return;
        }
        String message = context.getString(R.string.can_att_wait);
        if (null == mProgressDialog) {
            mProgressDialog = CustomProgress.show(context, message, true, null);
        } else {
            mProgressDialog.setMessage(message);
            mProgressDialog.show();
        }
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        data.put("fuserid", String.valueOf(m.getUserid()));
        PublicReq.delfriend(context, data, new Listener<RespData>() {

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
                AndroidUtil.showToast(context.getString(R.string.reqFailed));
            }
        });
    }

    // class FriendListener implements Listener<RespData> {
    //
    // @Override
    // public void onResponse(RespData response) {
    // if (null != mProgressDialog) {
    // mProgressDialog.hide();
    // }
    // if (response.isSuccess()) {
    // } else {
    // AndroidUtil.showToast(response.getTips());
    // }
    // }
    //
    // }

    class MasterViewHolder {
        private LinearLayout lay_line;
        private RelativeLayout lay_homepage;
        private CircleNetWorkImageView avatar;
        private TextView username;
        private TextView areaname;
        private TextView intro;
        private TextView post_num;
        private TextView fans_num;
        private Button btn_attention;
        private Button btn_no_attention;
    }
}
