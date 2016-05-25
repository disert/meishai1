package com.meishai.ui.fragment.home.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.app.widget.CircleNetWorkImageView;
import com.meishai.entiy.CateTopic;
import com.meishai.entiy.CateTopic.Topics;
import com.meishai.net.VolleyHelper;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.NetworkImageView;
import com.meishai.ui.fragment.home.HomeTopicActivity;
import com.meishai.ui.fragment.home.TopicShowActivity;

public class HomeCateTopicAdapter extends BaseAdapter {
    private Context mContext = null;
    // 话题分类数据
    private List<CateTopic> cateTopics = null;
    // 话题图片展示数据
    private List<Topics> topics = null;
    private LayoutInflater inflater = null;
    private ImageLoader imageLoader = null;

    public HomeCateTopicAdapter(Context context, List<CateTopic> cateTopics,
                                List<Topics> topics) {
        super();
        this.mContext = context;
        this.cateTopics = cateTopics;
        this.topics = topics;
        inflater = LayoutInflater.from(context);
        imageLoader = VolleyHelper.getImageLoader(mContext);
    }

    public void setCateTopics(List<CateTopic> cateTopics) {
        this.cateTopics = cateTopics;
    }

    public void setTopics(List<Topics> topics) {
        this.topics = topics;
    }

    @Override
    public int getCount() {
        return cateTopics.size();
    }

    @Override
    public Object getItem(int position) {
        return cateTopics.get(position);
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
            convertView = inflater.inflate(R.layout.home_cate_topic_item, null);
            holder.lay_topic = (RelativeLayout) convertView
                    .findViewById(R.id.lay_topic);
            holder.image = (CircleNetWorkImageView) convertView
                    .findViewById(R.id.image);
            holder.image.setRoundness(6f);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.lay_topics = (LinearLayout) convertView
                    .findViewById(R.id.lay_topics);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        CateTopic cateTopic = cateTopics.get(position);
        holder.image.setImageUrl(cateTopic.getImage(), imageLoader);
        holder.name.setText(cateTopic.getName());
        holder.lay_topic.setTag(position);
        holder.lay_topic.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int p = (Integer) v.getTag();
                CateTopic topic = cateTopics.get(p);
                Intent intent = new Intent(mContext, HomeTopicActivity.class);
                intent.putExtra("cateTopic", topic);
                mContext.startActivity(intent);
            }
        });
        if (position + 1 == cateTopics.size()) {
            if (null == this.topics || this.topics.isEmpty()) {
                holder.lay_topics.setVisibility(View.GONE);
            } else {
                buildTopics(holder.lay_topics);
            }
        } else {
            holder.lay_topics.setVisibility(View.GONE);
        }
        return convertView;
    }

    private void buildTopics(LinearLayout lay_topics) {
        lay_topics.removeAllViews();
        for (Topics pics : topics) {
            View picView = inflater.inflate(R.layout.home_cate_topic_pic_item,
                    null);
            NetworkImageView imageView = (NetworkImageView) picView
                    .findViewById(R.id.t_pic);
            imageView.setImageUrl(pics.getImage(), imageLoader);
            imageView.setTag(pics);
            imageView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Topics p = (Topics) v.getTag();
                    Intent intent = TopicShowActivity.newIntent((int) p.getTid());
                    mContext.startActivity(intent);
                }
            });
            lay_topics.addView(picView);
        }
        lay_topics.setVisibility(View.VISIBLE);
    }

    class ViewHolder {
        private RelativeLayout lay_topic;
        private CircleNetWorkImageView image;
        private TextView name;
        private LinearLayout lay_topics;
    }

}
