package com.meishai.ui.fragment.usercenter.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.app.widget.CircleNetWorkImageView;
import com.meishai.entiy.MyMsg;
import com.meishai.net.VolleyHelper;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.ui.fragment.home.HomePageActivity;

public class MsgAdapter extends BaseAdapter {

    private Context mContext;
    private List<MyMsg> msgs;
    private ImageLoader imageLoader = null;

    public MsgAdapter(Context mContext, List<MyMsg> msgs) {
        super();
        this.mContext = mContext;
        this.msgs = msgs;
        imageLoader = VolleyHelper.getImageLoader(mContext);
    }

    public void setMsgs(List<MyMsg> msgs) {
        this.msgs = msgs;
    }

    @Override
    public int getCount() {
        return msgs.size();
    }

    @Override
    public Object getItem(int position) {
        return msgs.get(position);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MsgHolder holder = null;
        if (null == convertView) {
            holder = new MsgHolder();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.user_msg_item, null);
            holder.lay_line = (LinearLayout) convertView
                    .findViewById(R.id.lay_line);
            holder.lay_con = (LinearLayout) convertView
                    .findViewById(R.id.lay_con);
            holder.avatar = (CircleNetWorkImageView) convertView
                    .findViewById(R.id.avatar);
            holder.username = (TextView) convertView
                    .findViewById(R.id.username);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.content = (TextView) convertView.findViewById(R.id.content);
            convertView.setTag(holder);

        } else {
            holder = (MsgHolder) convertView.getTag();
        }
        if (position == 0) {
            holder.lay_line.setVisibility(View.VISIBLE);
        } else {
            holder.lay_line.setVisibility(View.GONE);
        }
        MyMsg msg = msgs.get(position);
        holder.lay_con.setTag(msg.getUserid());
        holder.lay_con.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mContext.startActivity(HomePageActivity.newIntent(v.getTag()
                        .toString()));
            }
        });
        holder.avatar.setDefaultImageResId(R.drawable.head_default);
        holder.avatar.setErrorImageResId(R.drawable.head_default);
        holder.avatar.setImageUrl(msg.getAvatar(), imageLoader);
        holder.username.setText(msg.getUsername());
        holder.time.setText(msg.getTime());
        holder.content.setText(msg.getContent());
        return convertView;
    }

    class MsgHolder {
        private LinearLayout lay_line;
        private LinearLayout lay_con;
        private CircleNetWorkImageView avatar;
        private TextView username;
        private TextView time;
        private TextView content;

    }

}
