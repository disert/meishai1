package com.meishai.ui.fragment.meiwu;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
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
import com.meishai.entiy.SpecialListRespData;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.ui.base.BaseFragment;
import com.meishai.ui.fragment.meiwu.adapter.SpecialAdapter;
import com.meishai.ui.fragment.meiwu.req.MeiWuReq;
import com.meishai.util.AndroidUtil;
import com.meishai.util.CacheConfigUtils;
import com.meishai.util.DebugLog;
import com.meishai.util.GsonHelper;
import com.meishai.util.PullToRefreshHelper;

/**
 * 美物 - 品质专场 2.0
 *
 * @author yl
 */
public class MeiWuSpecialFragment extends BaseFragment {
    private int currentPage = 1;
    private View view;
    private PullToRefreshListView mListView;
    private SpecialAdapter mAdapter;
    private SpecialListRespData mData;
    private String cacheKey = "MeiWuSpecialFragment";
    private boolean mScrollState;
    private boolean isLoading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.meiwu_listview, null);
        currentPage = 1;
        this.initView();
        return view;
    }

    private void initView() {

        mListView = (PullToRefreshListView) view
                .findViewById(R.id.meiwu_listview);
        mListView.setMode(Mode.PULL_FROM_START);
        PullToRefreshHelper.initIndicatorStart(mListView);
        PullToRefreshHelper.initIndicator(mListView);

        // 写适配器
        mAdapter = new SpecialAdapter(getActivity(), getImageLoader());
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

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (totalItemCount - (firstVisibleItem + visibleItemCount) == 1 && !isLoading) {
                    isLoading = true;
                    DebugLog.w("总数item:" + totalItemCount + "当前item:" + (firstVisibleItem + visibleItemCount));
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

    protected void getRequestData(final int page) {
//		showProgress("", getString(R.string.network_wait));
        MeiWuReq.specialReq(page, new Listener<String>() {

            @Override
            public void onResponse(String response) {

                //缓存数据
                CacheConfigUtils.putCache(cacheKey + "##" + page, response);

                checkRespData(response);
                setNetComplete();
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                AndroidUtil.showToast(R.string.reqFailed);
                //拉取数据失败,就使用缓存的数据
                String cache = CacheConfigUtils.getCache(cacheKey + "##" + page);
                if (!TextUtils.isEmpty(cache)) {
                    checkRespData(cache);
                }

                setNetComplete();
            }
        });
    }

    protected void checkRespData(String response) {
        try {
            JSONObject obj = new JSONObject(response);
            int success = obj.getInt("success");
            if (success == 1) {
                SpecialListRespData resqData = GsonHelper.parseObject(response,
                        SpecialListRespData.class);
                if (resqData.page == 1) {
                    mData = resqData;

                } else {
                    mData.list.addAll(resqData.list);
                    // mAdapter.addData(datas)
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

    private void setNetComplete() {
        mListView.onRefreshComplete();
        hideProgress();
        isLoading = false;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (mData == null) {
                showProgress("", getString(R.string.network_wait));
                getRequestData(currentPage);
            } else {
                //				AndroidUtil.showToast("strat被调用:" + isVisibleToUser);
                //				mAdapter.notifyDataSetChanged();
                mAdapter.setData(mData);
                mListView.setAdapter(mAdapter);
            }
        } else {
            // 相当于Fragment的onPause
        }
    }
}
