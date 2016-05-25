package com.meishai.ui.fragment.usercenter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.app.util.DensityUtils;
import com.meishai.app.widget.CircleImageView;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.PostItem.ZanUserInfo;
import com.meishai.entiy.ReleaseData;
import com.meishai.entiy.ShareData;
import com.meishai.entiy.TopicItem;
import com.meishai.entiy.TopicRespData;
import com.meishai.entiy.UserInfo;
import com.meishai.net.ReqData;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.ListImageListener;
import com.meishai.net.volley.toolbox.StringRequest;
import com.meishai.ui.base.BaseActivity;
import com.meishai.ui.base.BaseFragment;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.camera.ImageChooseActivity1;
import com.meishai.ui.fragment.home.HomePageActivity;
import com.meishai.ui.fragment.home.adapter.HomeTopicAdapter;
import com.meishai.ui.fragment.usercenter.adapter.CollectedPostAdapter;
import com.meishai.ui.fragment.usercenter.req.CollectedReq;
import com.meishai.ui.popup.ShareMorePopupWindow;
import com.meishai.util.AndroidUtil;
import com.meishai.util.CacheConfigUtils;
import com.meishai.util.DebugLog;
import com.meishai.util.GsonHelper;
import com.meishai.util.PullToRefreshHelper;
import com.nimbusds.jose.JOSEException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 晒晒->话题->讨论 2.0
 *
 * @author yl
 */
public class CollectedPostFragment extends BaseFragment {

    private PullToRefreshListView mListView;
    private CollectedPostAdapter mAdapter;


    private TopicRespData mData;

    private View view;
    private int mPage;
    private boolean isLoading;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.home_topic_show1, null);
        initView();
        initData();
        return view;
    }

    //    @Override
    //    public void onResume() {
    //        super.onResume();
    //        if (mData == null) {
    //            getRequestData(1);
    //        } else {
    //            mAdapter.setData(mData);
    //        }
    //    }

    private void initView() {


        view.findViewById(R.id.top_parti_action).setVisibility(View.GONE);
        view.findViewById(R.id.top_root).setVisibility(View.GONE);
        mListView = (PullToRefreshListView) view.findViewById(R.id.topic_listview);
        mListView.setMode(Mode.PULL_FROM_START);
        PullToRefreshHelper.initIndicator(mListView);

        mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                getRequestData(1);

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
                    if (mPage < mData.pages) {
                        getRequestData(mPage + 1);
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


    private void initData() {

        mAdapter = new CollectedPostAdapter(getActivity(), getImageLoader());
        mListView.setAdapter(mAdapter);
    }

    private void getRequestData(final int page) {


//        showProgress("", getString(R.string.network_wait));

        CollectedReq.MeiShaiReq(page, new Listener<String>() {
            @Override
            public void onResponse(String response) {
                //处理数据
                checkData(response);
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

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (mData == null) {
                showProgress("", getString(R.string.network_wait));
                getRequestData(mPage);
            } else {
                mAdapter.setData(mData);
                mListView.setAdapter(mAdapter);
            }
        } else {
            // 相当于Fragment的onPause
        }
    }

    private boolean checkData(String response) {
        try {
            JSONObject jsonObj = new JSONObject(response);

            if (jsonObj.getInt("success") == 1) {
                mData = GsonHelper.parseObject(response, TopicRespData.class);
                if (mData != null) {
                    mPage = mData.page;
                    if (mPage == 1) {
                        mAdapter.setData(mData);
                    } else {
                        mAdapter.addData(mData.list);
                    }
                }


                return true;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    protected void setNetComplete() {
        mListView.setVisibility(View.VISIBLE);
        mListView.onRefreshComplete();
        hideProgress();
        isLoading = false;

    }


}
