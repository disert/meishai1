package com.meishai.ui.fragment.usercenter.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.entiy.UserTaoBaoInfo;
import com.meishai.ui.fragment.usercenter.UserTaoBaoOperActivity;
import com.meishai.util.AndroidUtil;

public class TaobaoAdapter extends BaseAdapter {
    private final static int TYPE_TAOBAO = 0;
    private final static int TYPE_OTHER = 1;
    private Context mContext;
    private List<UserTaoBaoInfo> baoInfos = null;

    public TaobaoAdapter(Context context, List<UserTaoBaoInfo> baoInfos) {
        this.mContext = context;
        this.baoInfos = baoInfos;
    }

    public void setBaoInfos(List<UserTaoBaoInfo> baoInfos) {
        this.baoInfos = baoInfos;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return baoInfos.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        return baoInfos.get(position);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < baoInfos.size()) {
            return TYPE_TAOBAO;
        } else {
            return TYPE_OTHER;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        switch (getItemViewType(position)) {
            case TYPE_TAOBAO:
                ViewHolder holder = null;
                if (null == convertView) {
                    holder = new ViewHolder();
                    LayoutInflater inflater = LayoutInflater.from(mContext);
                    convertView = inflater.inflate(R.layout.user_taobao_item, null);
                    holder.lay_line = convertView.findViewById(R.id.lay_line);
                    holder.lay_tb_name = (LinearLayout) convertView
                            .findViewById(R.id.lay_tb_name);
                    holder.tb_name = (TextView) convertView
                            .findViewById(R.id.tb_name);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                if (0 == position) {
                    holder.lay_line.setVisibility(View.VISIBLE);
                } else {
                    holder.lay_line.setVisibility(View.GONE);
                }
                final UserTaoBaoInfo baoInfo = baoInfos.get(position);
                holder.tb_name.setText(baoInfo.getTaobaoUserName());
                holder.lay_tb_name.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        mContext.startActivity(UserTaoBaoOperActivity
                                .newModIntent(baoInfo));
                    }
                });
                break;
            case TYPE_OTHER:
                ViewHolderOther holderOther = null;
                if (null == convertView) {
                    holderOther = new ViewHolderOther();
                    LayoutInflater inflater = LayoutInflater.from(mContext);
                    convertView = inflater
                            .inflate(R.layout.user_taobao_other, null);
                    holderOther.lay_other_line = convertView
                            .findViewById(R.id.lay_other_line);
                    holderOther.taobao_add = (RelativeLayout) convertView
                            .findViewById(R.id.taobao_add);
                    convertView.setTag(holderOther);
                } else {
                    holderOther = (ViewHolderOther) convertView.getTag();
                }
                if (0 == position) {
                    holderOther.lay_other_line.setVisibility(View.VISIBLE);
                } else {
                    holderOther.lay_other_line.setVisibility(View.GONE);
                }
                holderOther.taobao_add
                        .setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                if (null != baoInfos && baoInfos.size() >= 5) {
                                    AndroidUtil.showToast(R.string.tip_taobao_max);
                                    return;
                                }
                                mContext.startActivity(UserTaoBaoOperActivity
                                        .newAddIntent());
                            }
                        });
                break;
            default:
                break;
        }
        return convertView;
    }

    class ViewHolder {
        private View lay_line;
        private LinearLayout lay_tb_name;
        private TextView tb_name;
    }

    class ViewHolderOther {
        private View lay_other_line;
        private RelativeLayout taobao_add;
    }
}
