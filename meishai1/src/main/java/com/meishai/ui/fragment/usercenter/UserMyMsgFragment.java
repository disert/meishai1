package com.meishai.ui.fragment.usercenter;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.meishai.R;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.MyMsg;
import com.meishai.net.RespData;
import com.meishai.net.volley.Response;
import com.meishai.net.volley.VolleyError;
import com.meishai.ui.base.BaseFragment;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.usercenter.adapter.MsgAdapter;
import com.meishai.ui.fragment.usercenter.req.MsgReq;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;
import com.meishai.util.GsonHelper;
import com.meishai.util.PullToRefreshHelper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件名：
 * 描    述：
 * 作    者：
 * 时    间：2016/2/24
 * 版    权：
 */
public class UserMyMsgFragment extends BaseFragment {
    private View mView;
    private PullToRefreshListView mRefreshList;
    private int mTableid;
    private int mPage;
    private List<MyMsg> msgs;
    private MsgAdapter msgAdapter;
    private boolean isLoading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mView == null) {
            mView = View.inflate(mContext, R.layout.home_topic_child, null);
            initView();
            showProgress(null, mContext.getString(R.string.network_wait));
            loadData(1);
        }

        return mView;

    }


    private void initView() {
        mTableid = getArguments().getInt("tableid", 0);
        msgs = new ArrayList<MyMsg>();
        mPage = 1;

        mRefreshList = (PullToRefreshListView) mView.findViewById(R.id.list);
        mRefreshList.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        PullToRefreshHelper.initIndicatorStart(mRefreshList);

        mRefreshList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                msgs.clear();
                loadData(1);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

            }
        });

        mRefreshList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (totalItemCount - (firstVisibleItem + visibleItemCount) == 1 && !isLoading) {
                    isLoading = true;
                    DebugLog.w("总数item:" + totalItemCount + "当前item:" + (firstVisibleItem + visibleItemCount));
                    loadData(++mPage);
//                    if (mPage < pages) {
//                    }
                }
            }
        });

        msgAdapter = new MsgAdapter(mContext, msgs);
        mRefreshList.setAdapter(msgAdapter);


    }

    private void loadData(int page) {

        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        data.put("tableid", mTableid + "");
        data.put("page", String.valueOf(page));
        data.put("pagesize", ConstantSet.PAGE_SIZE);
        MsgReq.message(mContext, data, new Response.Listener<RespData>() {
            @Override
            public void onResponse(RespData response) {
                hideProgress();
                mRefreshList.onRefreshComplete();
                if (response.isSuccess()) {
                    isLoading = false;
                    Type type = new TypeToken<List<MyMsg>>() {
                    }.getType();
                    List<MyMsg> resultMyMsgs = GsonHelper.parseObject(
                            GsonHelper.toJson(response.getData()), type);
                    if (null != resultMyMsgs && !resultMyMsgs.isEmpty()) {
                        msgs.addAll(resultMyMsgs);
                        msgAdapter.setMsgs(msgs);
                        msgAdapter.notifyDataSetChanged();
                    } else if (msgs.isEmpty()) {
                        AndroidUtil.showToast(response.getTips());
                    }
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
                AndroidUtil.showToast(mContext.getString(R.string.reqFailed));
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mView != null && mView.getParent() != null) {
            ((ViewGroup) mView.getParent()).removeView(mView);
        }
    }
}
