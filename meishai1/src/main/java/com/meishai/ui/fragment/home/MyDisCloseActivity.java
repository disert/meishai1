package com.meishai.ui.fragment.home;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.entiy.DiscloseListRespData;
import com.meishai.net.volley.Response;
import com.meishai.net.volley.VolleyError;
import com.meishai.ui.base.BaseActivity;
import com.meishai.ui.fragment.home.req.HomeReq;
import com.meishai.util.AndroidUtil;
import com.meishai.util.GsonHelper;
import com.meishai.util.PullToRefreshHelper;

/**
 * 文件名：
 * 描    述：主页 - 我的爆料
 * 作    者：
 * 时    间：2016/4/9
 * 版    权：
 */
public class MyDisCloseActivity extends BaseActivity {

    private TextView mTitle;
    private PullToRefreshListView mRefreshListView;
    private boolean isLoading;
    private int mPage = 1;
    private DiscloseAdapter mAdapter;
    private DiscloseListRespData mData;

    public static Intent newIntent() {
        Intent intent = new Intent(GlobalContext.getInstance(),
                MyDisCloseActivity.class);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.common_listview_activity);
        initView();
        showProgress("", "");
        getRequestData(1);
    }


    private void initView() {
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.comment_bar_root).setVisibility(View.GONE);
        mTitle = (TextView) findViewById(R.id.title);
        mTitle.setText("我的爆料");

        mRefreshListView = (PullToRefreshListView) findViewById(R.id.listview);
        mRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        PullToRefreshHelper.initIndicatorStart(mRefreshListView);
        mAdapter = new DiscloseAdapter(this, getImageLoader());
        mRefreshListView.setAdapter(mAdapter);
        mRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                if (refreshView.getCurrentMode() == PullToRefreshBase.Mode.PULL_FROM_START) {
                    getRequestData(1);
                }
            }
        });

        mRefreshListView.setOnScrollListener(new AbsListView.OnScrollListener() {
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
                        mRefreshListView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                AndroidUtil
                                        .showToast(R.string.drop_down_list_footer_no_more_text);
//                                setNetComplete();
                            }
                        }, 500);
                    }
                }
            }
        });

    }

    public void getRequestData(int page) {
        HomeReq.baoliaoList(this, page, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                setNetComplete();
                if (TextUtils.isEmpty(response)) {
                    AndroidUtil.showToast("返回数据为null");
                    return;
                }
                DiscloseListRespData data = GsonHelper.parseObject(response, DiscloseListRespData.class);
                if (data != null) {
                    if (data.success == 1) {

                        if (data.page <= 1) {
                            mData = data;
                        } else {
                            if (mPage != data.page && mData != null) {
                                mData.list.addAll(data.list);
                            }
                        }
                        mPage = data.page;
                        mAdapter.setData(mData);
                    } else {
                        AndroidUtil.showToast(data.tips);
                    }
                } else {
                    AndroidUtil.showToast("json解析失败!");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                setNetComplete();
                AndroidUtil.showToast("网络连接失败!");
            }
        });
    }

    public void setNetComplete() {
        mRefreshListView.onRefreshComplete();
        hideProgress();
        isLoading = false;
    }


}
