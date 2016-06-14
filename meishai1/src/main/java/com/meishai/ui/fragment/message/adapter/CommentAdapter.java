package com.meishai.ui.fragment.message.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.app.widget.CircleImageView;
import com.meishai.entiy.MessageComRespBean;
import com.meishai.entiy.PostItem;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.ui.fragment.home.PostShowActivity;
import com.meishai.ui.fragment.meiwu.adapter.MeiwuAdapter;
import com.meishai.util.GsonHelper;
import com.meishai.util.ToastUtlis;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 文 件 名：
 * 描    述：评论的适配器
 * 作    者：yangling
 * 时    间：2016-06-13
 * 版    权：
 */

public class CommentAdapter extends MeiwuAdapter {
    private MessageComRespBean mData;
    public CommentAdapter(Activity context,ImageLoader imageLoader){
        super(context,imageLoader);
    }

    @Override
    public int getCount() {
        if(mData == null || mData.getList() == null || mData.getList().isEmpty()){
            return 0;
        }
        return mData.getList().size();
    }


    @Override
    public String getTitle() {
        return mContext.getString(R.string.new_comm);
    }

    @Override
    public Object getItem(int position) {
        return mData.getList().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final MyHolder holder;
        if(convertView == null){
            holder = new MyHolder();
            convertView = View.inflate(mContext,R.layout.item_message_notification,null);
            holder.mTvContent = (TextView) convertView.findViewById(R.id.message_noti_content);
            holder.mTvTitle = (TextView) convertView.findViewById(R.id.message_noti_title);
            holder.mTvReplyConten = (TextView) convertView.findViewById(R.id.message_noti_reply_content);
            holder.mLlReply = (LinearLayout) convertView.findViewById(R.id.message_noti_reply);
            holder.mTvTime = (TextView) convertView.findViewById(R.id.message_noti_time);
            holder.mTvUsername = (TextView) convertView.findViewById(R.id.message_noti_name);
            holder.mIvAvatar = (CircleImageView) convertView.findViewById(R.id.message_noti_avatar);
            holder.mIvImg = (ImageView) convertView.findViewById(R.id.message_noti_image);
            holder.mIvImg.setVisibility(View.VISIBLE);
            holder.mTvTitle.setVisibility(View.VISIBLE);
            convertView.setTag(holder);
        }else {
            holder = (MyHolder) convertView.getTag();
        }
        holder.mItem = (MessageComRespBean.ListBean) getItem(position);

        holder.mTvTitle.setText(holder.mItem.getTitle());
        holder.mTvUsername.setText(holder.mItem.getUsername());
        holder.mTvContent.setText(holder.mItem.getContent());
        holder.mTvTime.setText(holder.mItem.getAddtime());
        if(TextUtils.isEmpty(holder.mItem.getReply())){
            holder.mLlReply.setVisibility(View.GONE);
        }else {
            holder.mLlReply.setVisibility(View.VISIBLE);
            holder.mTvReplyConten.setText(holder.mItem.getReply());

        }

        ImageLoader.ImageListener listener1 = ImageLoader.getImageListener(holder.mIvAvatar,
                R.drawable.place_default, R.drawable.place_default);
        mImageLoader.get(holder.mItem.getAvatar(), listener1);

        ImageLoader.ImageListener listener = ImageLoader.getImageListener(holder.mIvImg,
                R.drawable.place_default, R.drawable.place_default);
        mImageLoader.get(holder.mItem.getImage(), listener);


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyHolder holder1 = (MyHolder) v.getTag();
                //根据数据里的value值跳转到晒晒详情页面
                PostItem item = new PostItem();
                item.pid = holder1.mItem.getValue();
                mContext.startActivity(PostShowActivity.newIntent(item,
                        PostShowActivity.FROM_POST));
            }
        });


        return convertView;
    }

    @Override
    protected void initData(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            if(jsonObject.getInt("success") == 1){
                MessageComRespBean bean = GsonHelper.parseObject(data, MessageComRespBean.class);
                if(bean != null){
                    if(bean.page == 1 || mData == null){
                        mData = bean;
                    }else if(bean.page > mData.page){
                        mData.getList().addAll(bean.getList());
                        mData.page = bean.page;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            ToastUtlis.showToast(mContext, "数据解析错误!");
        }
    }

    @Override
    public boolean hasPage() {
        if(mData != null && mData.page < mData.pages){
            return true;
        }
        return false;
    }

    @Override
    public int getPage() {
        if(mData != null){
            return mData.page+1;
        }
        return 1;
    }

    class MyHolder{
        LinearLayout mLlReply;
        TextView mTvReplyConten;
        TextView mTvContent;
        TextView mTvTitle;
        TextView mTvUsername;
        TextView mTvTime;
        CircleImageView mIvAvatar;
        ImageView mIvImg;
        MessageComRespBean.ListBean mItem;
    }
}
