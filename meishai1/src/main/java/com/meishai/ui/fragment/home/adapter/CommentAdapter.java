package com.meishai.ui.fragment.home.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.app.widget.layout.HomePageItem;
import com.meishai.app.widget.layout.PostCommentLayout;
import com.meishai.app.widget.layout.PostShowLinearLayout;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.CommentInfo;
import com.meishai.entiy.CommentRespData;
import com.meishai.entiy.HomePageDatas;
import com.meishai.entiy.PostDetailRespData;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.ui.fragment.home.PostShowActivity;
import com.meishai.ui.fragment.usercenter.LoginActivity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 文件名：
 * 描    述：评论详情的adapter
 * 作    者：yl
 * 时    间：2016/2/4
 * 版    权：
 */
public class CommentAdapter extends BaseAdapter {


    private Context mContext;
    private ImageLoader mImageLoader;

    private CommentRespData mData;
    private PostShowActivity.OnCommentClickListener commentListener;

    /**
     * 评论中的某一楼被点击后的监听事件
     *
     * @param commentListener
     */
    public void setCommentListener(PostShowActivity.OnCommentClickListener commentListener) {
        this.commentListener = commentListener;
    }

    public CommentAdapter(Context context, ImageLoader imageLoader) {
        mContext = context;
        mImageLoader = imageLoader;
    }


    public void addComments(Collection<CommentInfo> collection) {
        mData.data.addAll(collection);
        notifyDataSetChanged();
    }

    public void clearComments() {
        mData.data.clear();
    }

    public synchronized void setData(CommentRespData data) {
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mData == null || mData.data == null || mData.data.isEmpty()) {
            return 0;
        }
        return mData.data.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final CommentInfo item = (CommentInfo) getItem(position);
        if (convertView == null) {
            convertView = new PostCommentLayout(mContext);
        }
        ((PostCommentLayout) convertView).setData(item, mImageLoader);
        ((PostCommentLayout) convertView).getItemLayout()
                .setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        //TODO 点击某行评论时的事件
                        if (commentListener != null) {
                            if (!MeiShaiSP.getInstance().getUserInfo().isLogin()) {
                                mContext.startActivity(LoginActivity.newIntent());
                                return;
                            }
                            commentListener.onClick(item);
                        }
                    }
                });


        return convertView;
    }

}
