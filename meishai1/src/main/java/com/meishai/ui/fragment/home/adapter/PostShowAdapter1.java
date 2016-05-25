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
import com.meishai.app.widget.layout.PostCommodityLayout;
import com.meishai.app.widget.layout.PostShowLinearLayout;
import com.meishai.entiy.CommentInfo;
import com.meishai.entiy.CommodityInfo;
import com.meishai.entiy.HomePageDatas;
import com.meishai.entiy.PostDetailRespData;
import com.meishai.entiy.PostItem;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.ui.fragment.home.CommentActivity;
import com.meishai.ui.fragment.home.PostShowActivity.OnCommentClickListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PostShowAdapter1 extends BaseAdapter {

    private PostDetailRespData mData;


    private final static int TYPE_POST = 0;//晒晒信息
    private final static int TYPE_COMMENT = 1;//评论
    private final static int TYPE_RELATED = 2;//相关商品

    private Context mContext;
    private ImageLoader mImageLoader;

    private PostItem mPostItem;
    private List<CommentInfo> mCommentList;
    private List<HomePageDatas.PostInfo> mGoodsList;
    private List<HomePageDatas.PostInfo> item;

    private OnCommentClickListener commentListener;

    private PostShowAdapter.OnPostClickListenr clickListener;
    private OnClickListener delListener;

    public PostShowAdapter1(Context context, ImageLoader imageLoader) {
        mContext = context;
        mImageLoader = imageLoader;

        mCommentList = new ArrayList<CommentInfo>();
        item = new ArrayList<HomePageDatas.PostInfo>();
    }

    /**
     * 评论中的某一楼被点击后的监听事件
     *
     * @param commentListener
     */
    public void setCommentListener(OnCommentClickListener commentListener) {
        this.commentListener = commentListener;
    }

    /**
     * 已废弃的监听
     */
//	public void setClickListener(PostShowAdapter.OnPostClickListenr clickListener) {
//		this.clickListener = clickListener;
//	}
    public void addComments(PostDetailRespData data) {
        mCommentList.addAll(data.comdata);
        if ((mPostItem != null) && (data.data != null) && (!data.data.isEmpty()))
            mPostItem.com_num = data.data.get(0).com_num;
        notifyDataSetChanged();
    }

    public void clearComments() {
        mCommentList.clear();
    }

    public synchronized void setData(PostDetailRespData data) {
        mData = data;
        if (mData != null) {
            if (mData.data != null) {
                mPostItem = mData.data.get(0);
            }
            if (mData.comdata != null) {
                mCommentList = mData.comdata;
            }
            if (mData.list != null) {
                mGoodsList = mData.list;
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        int count = 0;
        if (mPostItem == null) {
            return 0;
        } else {
            count = 1;
            if (mCommentList != null && !mCommentList.isEmpty()) {
                count += 1;
            }
            if (mGoodsList != null && !mGoodsList.isEmpty()) {
                count += (mData.list.size() + 1) / 2;
            }
        }
        return count;
    }

    @Override
    public Object getItem(int position) {
        int startPosition = 0;
        int endPosition = 0;

        switch (position) {
            case 0:
                return mPostItem;
            case 1://当有评论时,显示评论,没有则是推荐商品
                if (mCommentList != null && !mCommentList.isEmpty()) {
                    return mCommentList;
                }
                startPosition = (position - 1) * 2;
                endPosition = startPosition + 1;
                item.clear();
                item.add(mGoodsList.get(startPosition));
                if (endPosition < mGoodsList.size()) {
                    item.add(mGoodsList.get(endPosition));
                }
                return item;
            default://当有评论时,显示评论,没有则是推荐商品
                if (mCommentList != null && !mCommentList.isEmpty()) {
                    startPosition = (position - 2) * 2;
                    endPosition = startPosition + 1;

                } else {
                    startPosition = (position - 1) * 2;
                    endPosition = startPosition + 1;
                }
                item.clear();
                item.add(mGoodsList.get(startPosition));
                if (endPosition < mGoodsList.size()) {
                    item.add(mGoodsList.get(endPosition));
                }
                return item;
        }

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                return TYPE_POST;
            case 1:
                if (mCommentList != null && !mCommentList.isEmpty()) {
                    return TYPE_COMMENT;
                }
                return TYPE_RELATED;
            default:
                return TYPE_RELATED;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        switch (getItemViewType(position)) {
            case TYPE_POST:
                if (null == convertView) {
                    convertView = new PostShowLinearLayout(mContext, true);
                }

                ((PostShowLinearLayout) convertView).setData(mPostItem, mImageLoader);
                //在view中该方法为空实现,暂时不清楚具体作用,但可以注释了
//			((PostShowLinearLayout) convertView).setCommentPop(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					if (null != clickListener) {
//						clickListener.onClickComment(v);
//					}
//				}
//			});
                ((PostShowLinearLayout) convertView).setOnDeleteClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (delListener != null) {
                            delListener.onClick(v);
                        }
                    }
                });
                break;

            case TYPE_COMMENT:
                ViewHolderBar holder;
                if (null == convertView) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.post_comment_bar_item, parent, false);
                    holder = new ViewHolderBar();
                    holder.comment_bar = (LinearLayout) convertView.findViewById(R.id.comment_bar);
                    holder.count = (TextView) convertView.findViewById(R.id.count_tv);
                    holder.comments = (LinearLayout) convertView.findViewById(R.id.comments);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolderBar) convertView.getTag();
                }
                if (null != mPostItem || (null != mCommentList && mCommentList.size() > 0)) {
                    holder.comment_bar.setVisibility(View.VISIBLE);
                }
                String comment_count = mContext.getString(R.string.comment_count);
                holder.count.setText(String.format(comment_count, mPostItem.com_num));
                initComent(mCommentList, holder.comments);
                break;
            case TYPE_RELATED:
                Object obj = getItem(position);
                if (convertView == null) {

                    convertView = new HomePageItem(mContext);
                }
                List<HomePageDatas.PostInfo> item1 = (List<HomePageDatas.PostInfo>) obj;
                if (item1.size() == 2) {
                    ((HomePageItem) convertView).setData(item1.get(0), item1.get(1), mImageLoader);
                } else {
                    ((HomePageItem) convertView).setData(item1.get(0), null, mImageLoader);
                }
                //控制线是否显示
                int prePosition = position - 1;
                if (prePosition >= 0) {
                    if (getItemViewType(prePosition) == TYPE_RELATED) {
                        ((HomePageItem) convertView).setLineVisibility(View.GONE);
                    } else {
                        ((HomePageItem) convertView).setLineVisibility(View.VISIBLE);
                    }
                }

                break;
        }

        return convertView;
    }

    private void initComent(List<CommentInfo> commentList, LinearLayout comments) {
        if (commentList != null && !commentList.isEmpty()) {
            comments.removeAllViews();
//			int commentCount = commentList.size();
//			boolean isShowMore = false;
//			if(commentCount > 5){
//				commentCount = 5;
//				isShowMore = true;
//			}

            for (int i = 0; i < commentList.size(); i++) {
                PostCommentLayout view = new PostCommentLayout(mContext);

                final CommentInfo item = commentList.get(i);
                view.setData(item,
                        mImageLoader);
                view.getItemLayout()
                        .setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                if (null != commentListener) {
                                    commentListener.onClick(item);
                                }
                            }
                        });

                comments.addView(view);
            }

            if (mPostItem.com_num > 5) {
                View view = View.inflate(mContext, R.layout.view_button, null);
                Button button = (Button) view.findViewById(R.id.button);
                button.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mContext.startActivity(CommentActivity.newIntent(mPostItem.pid));
                    }
                });
                comments.addView(view);

            }
        }
    }

    /**
     * 设置点击删除的监听
     *
     * @param listener
     */
    public void setOnDelLisnter(OnClickListener listener) {
        delListener = listener;
    }


    private static class ViewHolderBar {
        LinearLayout comment_bar;
        TextView count;
        LinearLayout comments;
    }
}
