package com.meishai.ui.fragment.home.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.app.widget.CustomProgress;
import com.meishai.app.widget.RoundCornerImageView;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.Master;
import com.meishai.entiy.TopicItem;
import com.meishai.net.RespData;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.ListImageListener;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.common.req.PublicReq;
import com.meishai.util.AndroidUtil;

/**
 * 话题
 *
 * @author sh
 */
public class TopicAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater mInflater;
    private ImageLoader mImageLoader;
    private List<TopicItem> mList;
    private CustomProgress mProgressDialog = null;

    public TopicAdapter(Context context, ImageLoader imageLoader) {
        mImageLoader = imageLoader;
        this.context = context;
        mInflater = LayoutInflater.from(context);
        mList = new ArrayList<TopicItem>();
    }

    public void addCollection(Collection<TopicItem> collection) {
        mList.addAll(collection);
        notifyDataSetChanged();
    }

    public void setmList(List<TopicItem> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }

    public void clear() {
        mList.clear();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (null == convertView) {
            convertView = mInflater.inflate(R.layout.home_topic_item, parent,
                    false);
            holder = new ViewHolder();
            holder.lay_line = (LinearLayout) convertView
                    .findViewById(R.id.lay_line);
            holder.thumb = (RoundCornerImageView) convertView
                    .findViewById(R.id.topic_thumb_iv);
            holder.title = (TextView) convertView
                    .findViewById(R.id.topic_title_tv);
            holder.user = (TextView) convertView
                    .findViewById(R.id.topic_user_tv);
            holder.time = (TextView) convertView
                    .findViewById(R.id.topic_time_tv);
            holder.attention = (ImageButton) convertView
                    .findViewById(R.id.topic_attention_ib);
            holder.description = (TextView) convertView
                    .findViewById(R.id.topic_desc_tv);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (position == 0) {
            holder.lay_line.setVisibility(View.VISIBLE);
        } else {
            holder.lay_line.setVisibility(View.GONE);
        }
        TopicItem item = (TopicItem) getItem(position);

        holder.thumb.setTag(item.thumb);
        ListImageListener listener = new ListImageListener(holder.thumb, 0, 0,
                item.thumb);
        mImageLoader.get(item.thumb, listener);

        holder.title.setText(item.title);
        holder.user.setText(item.username);
        holder.time.setText(item.addtime);
        holder.description.setText(item.description);
        holder.attention.setTag(position);
        if (item.isattention == 0) {
            holder.attention.setImageResource(R.drawable.ic_attention_no);
            holder.attention.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int p = (Integer) v.getTag();
                    addtopic(p);

                }
            });
        } else {
            holder.attention.setImageResource(R.drawable.ic_attention_yes);
            holder.attention.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int p = (Integer) v.getTag();
                    deltopic(p);
                }
            });
        }
        return convertView;
    }

    /**
     * 添加关注
     *
     * @param position
     */
    private void addtopic(int position) {
        final TopicItem m = mList.get(position);
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
        data.put("tid", String.valueOf(m.tid));
        PublicReq.addtopic(context, data, new Listener<RespData>() {

            @Override
            public void onResponse(RespData response) {
                if (null != mProgressDialog) {
                    mProgressDialog.hide();
                }
                if (response.isSuccess()) {
                    m.isattention = Master.HAS_ATENTION;
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
    private void deltopic(int position) {
        final TopicItem m = mList.get(position);
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
        data.put("tid", String.valueOf(m.tid));
        PublicReq.deltopic(context, data, new Listener<RespData>() {

            @Override
            public void onResponse(RespData response) {
                if (null != mProgressDialog) {
                    mProgressDialog.hide();
                }
                if (response.isSuccess()) {
                    m.isattention = Master.NO_ATENTION;
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

    private static class ViewHolder {
        private LinearLayout lay_line;
        public RoundCornerImageView thumb;
        public TextView title;
        public TextView user;
        public TextView time;
        public ImageButton attention;
        public TextView description;
    }
}
