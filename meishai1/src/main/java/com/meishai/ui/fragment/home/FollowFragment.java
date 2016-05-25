package com.meishai.ui.fragment.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.app.widget.dragtop.AttachUtil;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.HomePageDatas;
import com.meishai.entiy.HomePageFollowRespData;
import com.meishai.entiy.PostItem;
import com.meishai.net.RespData;
import com.meishai.net.VolleyHelper;
import com.meishai.net.volley.Response;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.ListImageListener;
import com.meishai.ui.base.BaseFragment;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.common.req.PublicReq;
import com.meishai.ui.fragment.common.req.PublicReqTools;
import com.meishai.ui.fragment.home.adapter.HomeListAdapter;
import com.meishai.ui.fragment.home.req.HomeReq;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;
import com.meishai.util.GsonHelper;
import com.meishai.util.PullToRefreshHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * 文件名：
 * 描    述：个人主页 关注
 * 作    者： yl
 * 时    间：2016/1/27
 * 版    权：
 */
public class FollowFragment extends BaseFragment {

    private View mRootView;

    // 自动刷新listview以及它对应的adapter
    private PullToRefreshListView mListView;
    private MyAdapter mPostListAdapter;
    private boolean isLogin = false;
    // 当前的页数
    private int mPage = 1;

    // 主页数据的bean
    private HomePageFollowRespData mData;

    private String userid;
    private boolean isLoading = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userid = getArguments().getString("uid");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.home_timeline, container,
                false);
        initView(mRootView);

        return mRootView;
    }

    private void initView(View v) {
        mListView = (PullToRefreshListView) v
                .findViewById(R.id.home_timeline_listview);
        mListView.getRefreshableView().setPadding(0, AndroidUtil.dip2px(8), 0, 0);
        mListView.setMode(PullToRefreshBase.Mode.DISABLED);
        PullToRefreshHelper.initIndicatorStart(mListView);
        PullToRefreshHelper.initIndicator(mListView);
        // 写适配器
        mPostListAdapter = new MyAdapter();
        mListView.setAdapter(mPostListAdapter);

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                //倒数第二个的时候加载新数据
                if (totalItemCount - (firstVisibleItem + visibleItemCount) == 1 && !isLoading) {
                    isLoading = true;
//                    DebugLog.w("总数item:" + totalItemCount + "当前item:" + (firstVisibleItem + visibleItemCount));
                    if (mPage < mData.pages) {
                        getRequestData(mPage + 1);
                    } else {
                        mListView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setNetComplete();
                            }
                        }, 500);
                    }
                } else {
                    boolean isScrll = AttachUtil.isAdapterViewAttach(view);
                    Intent intent = new Intent(ConstantSet.ACTION_SCRLL_TOPVIEW);
                    intent.putExtra("isScrll", isScrll);
                    mContext.sendBroadcast(intent);
                }
            }
        });

    }

    /**
     * 网络请求完成时调用,会把进度条关闭,并设置listview可见
     */
    private void setNetComplete() {
        mListView.setVisibility(View.VISIBLE);
        mListView.onRefreshComplete();
        hideProgress();
        isLoading = false;
    }

    /**
     * 请求指定页的数据
     */
    private void getRequestData(int page) {
        //		showProgress("", getString(R.string.network_wait));
        HomeReq.homePageFollow(mContext, Integer.parseInt(userid), page, 10, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //检查数据
                checkRequestData(response);

                setNetComplete();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DebugLog.e("net error:" + error.getMessage());
                AndroidUtil.showToast(R.string.reqFailed);
                setNetComplete();
            }
        });

    }

    /**
     * 检查请求的数据,如果返回成功会把该数据封装到mHomeInfo中,并更新界面
     *
     * @param response
     * @return
     */
    private boolean checkRequestData(String response) {
        try {
            JSONObject jsonObj = new JSONObject(response);
            if (jsonObj.getInt("success") == 1) {
                // 把数据封装成bean
                HomePageFollowRespData data = GsonHelper.parseObject(response, HomePageFollowRespData.class);
                if (data.page == 1) {
                    mData = data;
                } else {
                    if (data.page != mPage) {
                        mData.list.addAll(data.list);
                    }
                }
                mPage = data.page;// 获取当前的页数
                mPostListAdapter.notifyDataSetChanged();

                return true;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (mData == null) {
                showProgress("", getString(R.string.network_wait));
                getRequestData(1);
            } else {
                mPostListAdapter.notifyDataSetChanged();
            }
        }
    }


    class MyAdapter extends BaseAdapter {
        private AbsListView.LayoutParams mLayoutParams;

        public MyAdapter() {
            DisplayMetrics dm = GlobalContext.getInstance().getApplicationContext().getResources().getDisplayMetrics();
            double screenWidth = dm.widthPixels;
            //计算view在屏幕中的宽度
            double viewWidht = screenWidth / 3;
            mLayoutParams = new AbsListView.LayoutParams((int) viewWidht, (int) viewWidht);
        }

        @Override
        public int getCount() {
            if (mData == null || mData.list == null || mData.list.isEmpty()) {
                return 0;
            }
            return mData.list.size();
        }

        @Override
        public Object getItem(int position) {
            return mData.list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MyHolder holder;
            final HomePageFollowRespData.FollowInfo item = (HomePageFollowRespData.FollowInfo) getItem(position);
            if (convertView == null) {
                holder = new MyHolder();
                convertView = View.inflate(mContext, R.layout.layout_home_page_fans, null);
                holder.avatar = (ImageView) convertView.findViewById(R.id.home_page_head_avatar);
                holder.follow = (ImageView) convertView.findViewById(R.id.home_page_head_attention);
                holder.nicename = (TextView) convertView.findViewById(R.id.home_page_head_nicename);
                holder.desc = (TextView) convertView.findViewById(R.id.home_page_head_info);

                convertView.setTag(holder);
            } else {
                holder = (MyHolder) convertView.getTag();
            }

            //加载数据
            holder.avatar.setTag(item.avatar);
            ListImageListener listener = new ListImageListener(holder.avatar, R.drawable.place_default, R.drawable.place_default, item.avatar);
            getImageLoader().get(item.avatar, listener);

            holder.nicename.setText(item.username);
            holder.desc.setText(item.text);

            if (item.isattention == 1) {
                holder.follow.setImageResource(R.drawable.ic_attention_yes);
                holder.follow.setClickable(false);
            } else {
                holder.follow.setImageResource(R.drawable.ic_attention_no);
                holder.follow.setClickable(true);
            }

            //设置事件
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(HomePageActivity.newIntent(item.userid + ""));
                }
            });

            holder.follow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PublicReqTools.addfriend(item.userid + "", (ImageView) v, mContext);
                }
            });

            return convertView;
        }
    }

    class MyHolder {
        ImageView avatar;
        ImageView follow;
        TextView nicename;
        TextView desc;
    }

}
