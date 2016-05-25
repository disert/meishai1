package com.meishai.ui.fragment.tryuse;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.meishai.R;
import com.meishai.entiy.PointRewardRespData;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.ui.base.BaseFragment;
import com.meishai.ui.fragment.tryuse.adapter.PointRewardAdapter;
import com.meishai.ui.fragment.tryuse.req.FuLiSheReq;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;
import com.meishai.util.GsonHelper;
import com.meishai.util.PullToRefreshHelper;

/**
 * 福利社 - 积分商城 2.0
 *
 * @author yl
 */
public class PointRewardFragment extends BaseFragment {
    private int currentPage = 1;
    private View view;
    private PullToRefreshListView mListView;
    private PointRewardRespData mData;
    private PointRewardAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.meiwu_listview, null);
        this.initView();
        currentPage = 1;
        // this.getRequestData(1);
        return view;
    }

    private void initView() {

        mListView = (PullToRefreshListView) view
                .findViewById(R.id.meiwu_listview);
        mListView.setMode(Mode.BOTH);
        PullToRefreshHelper.initIndicatorStart(mListView);
        PullToRefreshHelper.initIndicator(mListView);

        // 写适配器
        mAdapter = new PointRewardAdapter(mContext, getImageLoader());
        mListView.setAdapter(mAdapter);
        mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                if (refreshView.getCurrentMode() == Mode.PULL_FROM_START) {
                    getRequestData(1);
                } else if (refreshView.getCurrentMode() == Mode.PULL_FROM_END) {
                    if (mData == null) {
                        getRequestData(1);
                        return;
                    }
                    if (currentPage < mData.pages) {
                        getRequestData(currentPage + 1);
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

    private void getRequestData(int page) {
        showProgress("", getString(R.string.network_wait));
        FuLiSheReq.pointReward(page, new Listener<String>() {

            @Override
            public void onResponse(String response) {
                checkRespData(response);
                setNetComplete();
            }
        }, new ErrorListener() {

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
                PointRewardRespData resqData = GsonHelper.parseObject(response,
                        PointRewardRespData.class);
                if (resqData.page == 1) {
                    mData = resqData;
                } else if (resqData.page > 1) {
                    mData.list.addAll(resqData.list);
                    mData.page = resqData.page;
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
        mListView.setVisibility(View.VISIBLE);
        mListView.onRefreshComplete();
        hideProgress();

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (mData == null) {
                getRequestData(currentPage);
            } else {
                mAdapter.setData(mData);
            }
        } else {
            // 相当于Fragment的onPause
        }
    }
}
