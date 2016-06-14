package com.meishai.ui.fragment.message.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.app.widget.CircleImageView;
import com.meishai.entiy.MessageFansRespBean;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.ui.fragment.home.HomePageActivity;
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

public class FansAdapter extends MeiwuAdapter {
    private MessageFansRespBean mData;
    public FansAdapter(Activity context, ImageLoader imageLoader){
        super(context,imageLoader);
    }

    @Override
    protected void initData(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            if(jsonObject.getInt("success") == 1){
                MessageFansRespBean bean = GsonHelper.parseObject(data, MessageFansRespBean.class);
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
    public String getTitle() {
        return mContext.getString(R.string.new_fans);
    }

    @Override
    public int getCount() {
        if(mData == null || mData.getList() == null || mData.getList().isEmpty()){
            return 0;
        }
        return mData.getList().size();
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
            convertView = View.inflate(mContext,R.layout.item_message_zan,null);
            holder.mTvContent = (TextView) convertView.findViewById(R.id.zan_content);
            holder.mTvTime = (TextView) convertView.findViewById(R.id.zan_time);
            holder.mTvUsername = (TextView) convertView.findViewById(R.id.zan_username);
            holder.mIvAvatar = (CircleImageView) convertView.findViewById(R.id.zan_avatar);
            holder.mIvInvat = (ImageView) convertView.findViewById(R.id.zan_invit);
            holder.mIvInvat.setVisibility(View.GONE);
            convertView.setTag(holder);
        }else {
            holder = (MyHolder) convertView.getTag();
        }
        holder.mItem = (MessageFansRespBean.ListBean) getItem(position);

        holder.mTvUsername.setText(holder.mItem.getUsername());
        holder.mTvContent.setText(holder.mItem.getTitle());
        holder.mTvTime.setText(holder.mItem.getFans());

        ImageLoader.ImageListener listener1 = ImageLoader.getImageListener(holder.mIvAvatar,
                R.drawable.place_default, R.drawable.place_default);
        mImageLoader.get(holder.mItem.getAvatar(), listener1);


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyHolder holder1 = (MyHolder) v.getTag();
                mContext.startActivity(HomePageActivity.newIntent(holder1.mItem.getUserid()));
            }
        });


        return convertView;
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
        TextView mTvContent;
        TextView mTvUsername;
        TextView mTvTime;
        ImageView mIvInvat;
        CircleImageView mIvAvatar;
        MessageFansRespBean.ListBean mItem;
    }
}
