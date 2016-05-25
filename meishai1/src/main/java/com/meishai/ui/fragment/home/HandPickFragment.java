package com.meishai.ui.fragment.home;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.meishai.R;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.HomeInfo;
import com.meishai.entiy.UserInfo;
import com.meishai.net.ReqData;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.StringRequest;
import com.meishai.ui.base.BaseFragment;
import com.meishai.ui.fragment.home.adapter.HomeListAdapter;
import com.meishai.util.AndroidUtil;
import com.meishai.util.CacheConfigUtils;
import com.meishai.util.DebugLog;
import com.meishai.util.DiskImageCacheUtil;
import com.meishai.util.GsonHelper;
import com.meishai.util.PullToRefreshHelper;
import com.nimbusds.jose.JOSEException;

/**
 * 主页精选所对应的fragment
 *
 * @author Administrator yl
 */
public class HandPickFragment extends BaseFragment {

    private View mRootView;

    // 自动刷新listview以及它对应的adapter
    private PullToRefreshListView mListView;
    private HomeListAdapter mPostListAdapter;
    private boolean isLogin = false;
    // 当前的页数
    private int mPage = 1;

    // 主页数据的bean
    private HomeInfo mHomeInfo;

    private String userid;
    private boolean isLoading = false;
    private String url;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (null == mRootView) {
            // 可以复用布局,一个下拉刷新listview
            mRootView = inflater.inflate(R.layout.home_timeline, container,
                    false);
            initView(mRootView);
            mListView.setVisibility(View.INVISIBLE);
            // getRequestData(1);
        }


        return mRootView;
    }

    private void initView(View v) {
        mListView = (PullToRefreshListView) v
                .findViewById(R.id.home_timeline_listview);
        mListView.setMode(Mode.PULL_FROM_START);
        PullToRefreshHelper.initIndicatorStart(mListView);
        PullToRefreshHelper.initIndicator(mListView);
        // 写适配器
        mPostListAdapter = new HomeListAdapter(getActivity(), getImageLoader());
        // mPostListAdapter.setRootView(mRootView);
        mListView.setAdapter(mPostListAdapter);

        mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                if (refreshView.getCurrentMode() == Mode.PULL_FROM_START) {
                    getRequestData(1);

                } else if (refreshView.getCurrentMode() == Mode.PULL_FROM_END) {
                    if (mHomeInfo == null) {
                        getRequestData(1);
                        return;
                    } else if (mPage < mHomeInfo.pages) {
                        getRequestData(mPage + 1);
                    } else {
                        mListView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                AndroidUtil
                                        .showToast(R.string.drop_down_list_footer_no_more_text);
                                setNetComplete();
                            }
                        }, 500);
                    }
                }

            }
        });
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //倒数第二个的时候加载新数据
                if (totalItemCount - (firstVisibleItem + visibleItemCount) == 1 && !isLoading) {
                    isLoading = true;
                    DebugLog.w("总数item:" + totalItemCount + "当前item:" + (firstVisibleItem + visibleItemCount));
                    if (mPage < mHomeInfo.pages) {
                        getRequestData(mPage + 1);
                    } else {
                        mListView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                AndroidUtil
                                        .showToast(R.string.drop_down_list_footer_no_more_text);
                                setNetComplete();
                            }
                        }, 500);
                    }
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
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        isLogin = userInfo.isLogin();
        userid = userInfo.getUserID();
        ReqData reqData = new ReqData();
        reqData.setC("index");
        reqData.setA("init");
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", userInfo.getUserID());
        data.put("page", String.valueOf(page));
        data.put("pagesize", String.valueOf(10));
        reqData.setData(data);

        try {
            url = getString(R.string.base_url) + reqData.toReqString();
            DebugLog.d(url);
            sendMsg(url);

        } catch (JOSEException e) {
            e.printStackTrace();
        }
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
                mHomeInfo = GsonHelper.parseObject(response, HomeInfo.class);
                mPage = mHomeInfo.page;// 获取当前的页数
                mPostListAdapter.setData(mHomeInfo);

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
            if (mHomeInfo == null) {
                showProgress("", getString(R.string.network_wait));
                getRequestData(1);
            } else {
                mPostListAdapter.setData(mHomeInfo);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mRootView != null && null != mRootView.getParent()) {
            ViewGroup parent = (ViewGroup) mRootView.getParent();
            parent.removeView(mRootView);
        }
    }

    @Override
    public void failt(Object ojb) {
        AndroidUtil.showToast(R.string.reqFailed);
        //网络加载失败时,加载缓存
        String cache = CacheConfigUtils.getCache(url);
        checkRequestData(cache);
        setNetComplete();
    }

    @Override
    public void updateUI(Object obj) {
        if (obj == null || TextUtils.isEmpty(obj.toString())) {
            return;
        }
        String response = obj.toString();
        //缓存
        CacheConfigUtils.putCache(url, response);
        //检查数据
        checkRequestData(response);

        setNetComplete();
    }
}