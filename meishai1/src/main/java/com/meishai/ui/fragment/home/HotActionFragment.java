package com.meishai.ui.fragment.home;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.meishai.R;
import com.meishai.entiy.SKUResqData;
import com.meishai.net.volley.Response;
import com.meishai.net.volley.VolleyError;
import com.meishai.ui.base.BaseFragment;
import com.meishai.ui.fragment.home.req.HomeReq;
import com.meishai.ui.fragment.meiwu.adapter.Cid12Adapter;
import com.meishai.ui.fragment.meiwu.adapter.CidAllAdapter;
import com.meishai.ui.fragment.meiwu.adapter.CidMinusAdapter;
import com.meishai.ui.fragment.meiwu.adapter.MeiWuSKUAdapter;
import com.meishai.ui.fragment.meiwu.adapter.MeiwuAdapter;
import com.meishai.ui.fragment.meiwu.req.MeiWuReq;
import com.meishai.ui.fragment.usercenter.req.CollectedReq;
import com.meishai.util.AndroidUtil;
import com.meishai.util.CacheConfigUtils;
import com.meishai.util.DebugLog;
import com.meishai.util.GsonHelper;
import com.meishai.util.PullToRefreshHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 文件名：
 * 描    述：热门活动页面的子fragment
 * 作    者：
 * 时    间：2016/2/20
 * 版    权：
 */
public class HotActionFragment extends BaseFragment {
    private int currentPage = 1;
    private View view;
    private PullToRefreshGridView mRefrashGridView;
    private SKUResqData mData;
    private MeiWuSKUAdapter mAdapter;
    private boolean isLoading;
    private int mCid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.meiwu_sku, null);
            mCid = getArguments().getInt("cid", 0);
            this.initView();
            currentPage = 1;
        }
        // this.getRequestData(1);
        return view;
    }

    private void initView() {

        mRefrashGridView = (PullToRefreshGridView) view
                .findViewById(R.id.meiwu_gridview);
        mRefrashGridView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        GridView gridview = mRefrashGridView.getRefreshableView();
        //		gridview.setNumColumns(2);
        PullToRefreshHelper.initIndicatorStart(mRefrashGridView);
        PullToRefreshHelper.initIndicator(mRefrashGridView);
        mAdapter = new MeiWuSKUAdapter(mContext, getImageLoader());
        mRefrashGridView.setAdapter(mAdapter);

        mRefrashGridView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<GridView>() {

            @Override
            public void onRefresh(
                    PullToRefreshBase<GridView> refreshView) {
                if (refreshView.getCurrentMode() == PullToRefreshBase.Mode.PULL_FROM_START) {
                    getRequestData(1);
                } else if (refreshView.getCurrentMode() == PullToRefreshBase.Mode.PULL_FROM_END) {
                    if (mData == null) {
                        getRequestData(1);
                        return;
                    }
                    if (currentPage < mData.pages) {
                        getRequestData(currentPage + 1);
                    } else {
                        mRefrashGridView.postDelayed(new Runnable() {
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

        mRefrashGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //倒数第二个的时候加载新数据
                if (totalItemCount - (firstVisibleItem + visibleItemCount) == 1 && !isLoading) {
                    isLoading = true;
                    DebugLog.w("总数item:" + totalItemCount + "当前item:" + (firstVisibleItem + visibleItemCount));
                    if (currentPage < mData.pages) {
                        getRequestData(currentPage + 1);
                    } else {
                    }
                }
            }
        });
    }

    private void getRequestData(final int page) {
        //		showProgress("", getString(R.string.network_wait));
        HomeReq.hotActionList(mContext, page, mCid, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //处理数据
                checkRespData(response);
                setNetComplete();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                AndroidUtil.showToast(R.string.reqFailed);
                setNetComplete();
            }
        });
    }

    protected void checkRespData(String response) {
        try {
            JSONObject obj = new JSONObject(response);
            int success = obj.getInt("success");
            if (success == 1) {
                SKUResqData resqData = GsonHelper.parseObject(response,
                        SKUResqData.class);
                if (resqData == null) {
                    return;
                }
                if (resqData.page == 1) {
                    mData = resqData;
                } else if (resqData.page > 1) {
                    if (mData == null || mData.list == null) return;
                    if (resqData.list == null) return;
                    if (!mData.list.containsAll(resqData.list)) {
                        mData.list.addAll(resqData.list);
                        mData.page = resqData.page;
                    }
                }
                currentPage = mData.page;
                mAdapter.setData(mData);
            } else {
                AndroidUtil.showToast(R.string.reqFailed);

            }

        } catch (JSONException e) {
            DebugLog.e(e.getMessage());
            e.printStackTrace();
        }
    }

    protected void setNetComplete() {
        mRefrashGridView.setVisibility(View.VISIBLE);
        mRefrashGridView.onRefreshComplete();
        hideProgress();
        isLoading = false;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (view != null && view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        DebugLog.w("页面:" + mCid + ",可见:" + isVisibleToUser);
        if (isVisibleToUser) {
            if (mData == null) {
                showProgress("", getString(R.string.network_wait));
                getRequestData(currentPage);
            } else {
                mAdapter.setData(mData);
            }
        } else {
            // 相当于Fragment的onPause
        }
    }

}
