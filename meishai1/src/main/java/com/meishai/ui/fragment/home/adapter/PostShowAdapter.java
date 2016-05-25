package com.meishai.ui.fragment.home.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.app.widget.layout.PostCommentLayout;
import com.meishai.app.widget.layout.PostCommodityLayout;
import com.meishai.app.widget.layout.PostShowLinearLayout;
import com.meishai.app.widget.layout.PostTimeLineLayout;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.CommentInfo;
import com.meishai.entiy.CommodityInfo;
import com.meishai.entiy.PostItem;
import com.meishai.entiy.UserInfo;
import com.meishai.net.ReqData;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.StringRequest;
import com.meishai.ui.fragment.home.PostShowActivity.OnCommentClickListener;
import com.meishai.ui.fragment.home.req.PostReq;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;

public class PostShowAdapter extends BaseAdapter {

    public interface OnPostClickListenr {
        void onClickComment(View v);

        void onClickSort(View v);
    }

    private final static int TYPE_POST = 0;
    private final static int TYPE_COMMODITY = 1;
    private final static int TYPE_COMMENT_BAR = 2;
    private final static int TYPE_COMMENT = 3;

    private Context mContext;
    private ImageLoader mImageLoader;

    private PostItem mPostItem;
    private List<CommodityInfo> mCommodityList;
    private List<CommentInfo> mCommentList;

    private OnCommentClickListener commentListener;

    private OnPostClickListenr clickListener;
    private OnClickListener delListener;

    public PostShowAdapter(Context context, ImageLoader imageLoader) {
        mContext = context;
        mImageLoader = imageLoader;

        mCommentList = new ArrayList<CommentInfo>();
        mCommodityList = new ArrayList<CommodityInfo>();
    }

    public void setCommentListener(OnCommentClickListener commentListener) {
        this.commentListener = commentListener;
    }

    public void setClickListener(OnPostClickListenr clickListener) {
        this.clickListener = clickListener;
    }

    public void setPostItem(PostItem item) {
        mPostItem = item;
        notifyDataSetChanged();
    }

    public void setCommodityInfo(Collection<CommodityInfo> collection) {
        mCommentList.clear();
        mCommodityList.addAll(collection);
//		notifyDataSetChanged();
    }

    public void addComments(Collection<CommentInfo> collection) {
        mCommentList.addAll(collection);
        notifyDataSetChanged();
    }

    public void clearComments() {
        mCommentList.clear();
    }

    @Override
    public int getCount() {
        if (mCommentList == null || mCommentList.isEmpty()) {
            return TYPE_COMMENT - 1;
        }
        return mCommentList.size() + TYPE_COMMENT;
    }

    @Override
    public Object getItem(int position) {
        switch (position) {
            case 0:
                return mPostItem;
            case 1:
                return mCommodityList;
            case 2:
                return "(" + mCommentList.size() + ")";
            default:
                return mCommentList.get(position - 3);
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
                return TYPE_COMMODITY;
            case 2:
                return TYPE_COMMENT_BAR;
            default:
                return TYPE_COMMENT;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 4;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        switch (getItemViewType(position)) {
            case TYPE_POST:
                if (null == convertView) {
                    convertView = new PostShowLinearLayout(mContext, true);
                }

                ((PostShowLinearLayout) convertView).setData(mPostItem, mImageLoader);
                ((PostShowLinearLayout) convertView).setCommentPop(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (null != clickListener) {
                            clickListener.onClickComment(v);
                        }
                    }
                });
                ((PostShowLinearLayout) convertView).setOnDeleteClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (delListener != null) {
                            delListener.onClick(v);
                        }
                    }
                });
                break;

            case TYPE_COMMENT_BAR:
                ViewHolderBar holder;
                if (null == convertView) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.post_comment_bar_item, parent, false);
                    holder = new ViewHolderBar();
                    holder.comment_bar = (LinearLayout) convertView.findViewById(R.id.comment_bar);
                    holder.count = (TextView) convertView.findViewById(R.id.count_tv);
                    holder.sort = (ImageView) convertView.findViewById(R.id.sort_iv);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolderBar) convertView.getTag();
                }
                if (null != mPostItem || (null != mCommentList && mCommentList.size() > 0)) {
                    holder.comment_bar.setVisibility(View.VISIBLE);
                }
                String comment_count = mContext.getString(R.string.comment_count);
                holder.count.setText(String.format(comment_count, mCommentList.size()));
                // holder.count.setText(getItem(position).toString());
                if (null != mCommentList && mCommentList.size() > 0) {
                    holder.sort.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (clickListener != null) {
                                clickListener.onClickSort(v);
                            }
                        }
                    });
                }

                break;
            case TYPE_COMMODITY:
                if (null == convertView) {
                    convertView = new PostCommodityLayout(mContext);
                }
                ((PostCommodityLayout) convertView).setData(mCommodityList,
                        mImageLoader);
                break;
            case TYPE_COMMENT:
                if (null == convertView) {
                    convertView = new PostCommentLayout(mContext);
                }

                final Object item = getItem(position);
                ((PostCommentLayout) convertView).setData((CommentInfo) item,
                        mImageLoader);
                ((PostCommentLayout) convertView).getItemLayout()
                        .setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                if (null != commentListener) {
                                    commentListener.onClick((CommentInfo) item);
                                }
                            }
                        });
                break;
        }

        return convertView;
    }

    public void setOnDelLisnter(OnClickListener listener) {
        delListener = listener;
    }


    private static class ViewHolderBar {
        LinearLayout comment_bar;
        TextView count;
        ImageView sort;
    }
}
