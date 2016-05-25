/**
 */
package com.meishai.ui.fragment.home;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.meishai.R;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.HomeInfo;
import com.meishai.entiy.TopicItem;
import com.meishai.entiy.UserInfo;
import com.meishai.net.ReqData;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.StringRequest;
import com.meishai.ui.base.BaseFragment;
import com.meishai.ui.fragment.home.adapter.AttentAdapter;
import com.meishai.util.AndroidUtil;
import com.meishai.util.CacheConfigUtils;
import com.meishai.util.DebugLog;
import com.meishai.util.GsonHelper;
import com.meishai.util.PullToRefreshHelper;
import com.nimbusds.jose.JOSEException;

/**
 * @ClassName: TopicFragment1
 * @Description: 晒晒->关注
 */
public class AttentFragment extends BaseFragment {

    public final static int TYPE_HOT = 1;
    public final static int TYPE_ATTENTION = 2;
    public final static int TYPE_LATEST = 3;

    private View mRootView;

    private PullToRefreshListView mListView;
    private AttentAdapter mAdapter;
    private int mPage = 1;
    private HomeInfo mHomeInfo;
    private boolean isLoading;
    private String url;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (null == mRootView) {
            mRootView = inflater.inflate(R.layout.home_timeline, null);
            initView(mRootView);

//			getRequestAttent(mPage);
        }

        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (null != parent) {
            parent.removeView(mRootView);
        }

        return mRootView;
    }


    private void initView(View v) {
        mListView = (PullToRefreshListView) v.findViewById(R.id.home_timeline_listview);
        mListView.setMode(Mode.PULL_FROM_START);
        PullToRefreshHelper.initIndicatorStart(mListView);
        PullToRefreshHelper.initIndicator(mListView);
        mAdapter = new AttentAdapter(getActivity(), getImageLoader());
        mListView.setAdapter(mAdapter);

        mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                if (refreshView.getCurrentMode() == Mode.PULL_FROM_START) {
                    getRequestAttent(1);

                } else if (refreshView.getCurrentMode() == Mode.PULL_FROM_END) {
                    if (mHomeInfo == null) {
                        getRequestAttent(1);
                        return;
                    }
                    if (mPage < mHomeInfo.pages) {
                        getRequestAttent(mPage + 1);
                    } else {
                        mListView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                AndroidUtil
                                        .showToast(R.string.drop_down_list_footer_no_more_text);
                                mListView.onRefreshComplete();
                            }
                        }, 500);
                    }
                }

            }
        });

        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (!(parent.getAdapter().getItem(
                        position) instanceof TopicItem)) {//进行类型判断
                    DebugLog.w("类型不匹配");
                    return;
                }
                TopicItem topic = (TopicItem) parent.getAdapter().getItem(
                        position);
                Intent intent = TopicShowActivity.newIntent(topic.tid);
                startActivity(intent);

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
                        getRequestAttent(mPage + 1);
                    } else {
                        mListView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                AndroidUtil
                                        .showToast(R.string.drop_down_list_footer_no_more_text);
                                mListView.onRefreshComplete();
                            }
                        }, 500);
                    }
                }
            }
        });


    }

    private void getRequestAttent(final int page) {
//        showProgress("", getString(R.string.network_wait));

        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        ReqData reqData = new ReqData();
        reqData.setC("index");
        reqData.setA("follow");
        Map<String, String> dataList = new HashMap<String, String>();
        dataList.put("userid", userInfo.getUserID());
        dataList.put("page", String.valueOf(page));
        dataList.put("pagesize", String.valueOf(10));
        reqData.setData(dataList);

        try {
            url = getString(R.string.base_url) + reqData.toReqString();
            DebugLog.d(url);
            sendMsg(url);

        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    private boolean checkData(String response) {

        if (TextUtils.isEmpty(response)) {
            return false;
        }
        try {
            JSONObject jsonObj = new JSONObject(response);
            if (jsonObj.getInt("success") == 1) {
                HomeInfo info = GsonHelper.parseObject(response, HomeInfo.class);
                if (info.page == 1) {
                    mHomeInfo = info;
                } else {
                    mHomeInfo.list.addAll(info.list);
                    mHomeInfo.page = info.page;
                }
                mPage = mHomeInfo.page;//获取当前的页数
                mAdapter.setData(mHomeInfo);

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
                getRequestAttent(1);
            } else {
                mAdapter.setData(mHomeInfo);
            }
        } else {
            // 相当于Fragment的onPause
        }
    }

    @Override
    public void failt(Object ojb) {
        hideProgress();
        AndroidUtil.showToast(R.string.reqFailed);
        checkData(CacheConfigUtils.getCache(url));
        mListView.onRefreshComplete();
        isLoading = false;
    }

    @Override
    public void updateUI(Object obj) {
        hideProgress();
        isLoading = false;
        if (obj == null || TextUtils.isEmpty(obj.toString())) {
            return;
        }
        String response = obj.toString();

        CacheConfigUtils.putCache(url, response);
        if (checkData(response)) {
            mListView.onRefreshComplete();

        } else {

            mListView.onRefreshComplete();
            showToast(R.string.drop_down_list_footer_no_more_text);
        }
    }
}