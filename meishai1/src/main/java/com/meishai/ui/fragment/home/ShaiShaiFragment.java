package com.meishai.ui.fragment.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.app.widget.dragtop.AttachUtil;
import com.meishai.entiy.HomePageDatas;
import com.meishai.entiy.PostItem;
import com.meishai.net.VolleyHelper;
import com.meishai.net.volley.Response;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.ListImageListener;
import com.meishai.ui.base.BaseFragment;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.home.adapter.HomeListAdapter;
import com.meishai.ui.fragment.home.req.HomeReq;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;
import com.meishai.util.GsonHelper;
import com.meishai.util.ImageAdapter;
import com.meishai.util.PullToRefreshHelper;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * 文件名：
 * 描    述：个人主页 晒晒
 * 作    者： yl
 * 时    间：2016/1/27
 * 版    权：
 */
public class ShaiShaiFragment extends BaseFragment {

    private View mRootView;

    // 自动刷新listview以及它对应的adapter
    private PullToRefreshGridView mRefreshGridView;
    private MyAdapter mPostListAdapter;
    private boolean isLogin = false;
    // 当前的页数
    private int mPage = 1;

    // 主页数据的bean
    private HomePageDatas mDatas;

    private String userid;
    private boolean isLoading = false;
    private int padding = AndroidUtil.dip2px(3);

    //帖子被删除的广播
    private BroadcastReceiver mPostDeleteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int pid = intent.getIntExtra("pid", 0);
            if (mDatas != null && mDatas.list != null) {
                for (int i = 0; i < mDatas.list.size(); i++) {
                    HomePageDatas.PostInfo info = mDatas.list.get(i);
                    if (info.pid == pid) {
                        mDatas.list.remove(info);
                        if (mPostListAdapter != null) {
                            mPostListAdapter.notifyDataSetChanged();
                        }
                        return;
                    }
                }
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userid = getArguments().getString("uid");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //注册一个帖子被删除的广播
        if (mContext != null) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(ConstantSet.POST_DELETE);
            mContext.registerReceiver(mPostDeleteReceiver, filter);
        }
        mRootView = inflater.inflate(R.layout.meiwu_sku, container,
                false);
        initView(mRootView);

        return mRootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mContext != null && mPostDeleteReceiver != null)
            mContext.unregisterReceiver(mPostDeleteReceiver);
    }

    private void initView(View v) {

        mRefreshGridView = (PullToRefreshGridView) v
                .findViewById(R.id.meiwu_gridview);
        mRefreshGridView.setMode(PullToRefreshBase.Mode.DISABLED);
        mRefreshGridView.getRefreshableView().setNumColumns(3);
        mRefreshGridView.setPadding(padding, 0, 0, 0);
//        mRefreshGridView.setNumColumns(3);
//        PullToRefreshHelper.initIndicatorStart(mRefreshGridView);
//        PullToRefreshHelper.initIndicator(mRefreshGridView);
        // 写适配器
        mPostListAdapter = new MyAdapter();
        mRefreshGridView.setAdapter(mPostListAdapter);


        mRefreshGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem > 0) {

                }
                //倒数第二个的时候加载新数据
//                DebugLog.w("总数item:" + totalItemCount + "当前item:" + (firstVisibleItem + visibleItemCount));
                if (totalItemCount - (firstVisibleItem + visibleItemCount) <= 1 && !isLoading) {
                    isLoading = true;
                    if (mDatas == null) return;
                    if (mPage < mDatas.pages) {
                        getRequestData(mPage + 1);
                    } else {
                        mRefreshGridView.postDelayed(new Runnable() {
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
        mRefreshGridView.setVisibility(View.VISIBLE);
//        mRefreshGridView.onRefreshComplete();
        hideProgress();
        isLoading = false;
    }

    /**
     * 请求指定页的数据
     */
    private void getRequestData(int page) {
//        showProgress("", getString(R.string.network_wait));
        if (TextUtils.isEmpty(userid)) {
            AndroidUtil.showToast("userid为null");
            return;
        }
        HomeReq.homePageShaiShai(mContext, userid, page, 10, new Response.Listener<String>() {
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
                HomePageDatas datas = GsonHelper.parseObject(response, HomePageDatas.class);
                if (datas == null) {
                    AndroidUtil.showToast("Json解析失败!");
                    return false;
                }
                if (datas.page == 1) {
                    mDatas = datas;
                } else {
                    if (datas.page != mPage) {
                        mDatas.list.addAll(datas.list);
                    }
                }
                mPage = datas.page;// 获取当前的页数

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
            if (mDatas == null) {
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
            int count = 3;
            double viewWidht = (screenWidth - padding) / count;
            mLayoutParams = new AbsListView.LayoutParams((int) viewWidht, (int) viewWidht);
        }

        @Override
        public int getCount() {
            if (mDatas == null || mDatas.list == null || mDatas.list.isEmpty()) {
                return 0;
            }
            return mDatas.list.size();
        }

        @Override
        public Object getItem(int position) {
            return mDatas.list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final HomePageDatas.PostInfo item = (HomePageDatas.PostInfo) getItem(position);
            if (convertView == null) {
                convertView = new ImageView(mContext);
                convertView.setLayoutParams(mLayoutParams);
                convertView.setPadding(0, padding, padding, 0);
            }
            ImageView imageView = (ImageView) convertView;
            imageView.setTag(item.image);
            ListImageListener listener = new ListImageListener(imageView, R.drawable.place_default, R.drawable.place_default, item.image);
            VolleyHelper.getImageLoader(mContext).get(item.image, listener);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PostItem postItem = new PostItem();
                    postItem.pid = item.pid;
                    mContext.startActivity(PostShowActivity.newIntent(postItem, PostShowActivity.FROM_POST));
                }
            });

            return convertView;
        }
    }
}
