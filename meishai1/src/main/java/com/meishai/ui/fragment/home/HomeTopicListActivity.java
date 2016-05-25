package com.meishai.ui.fragment.home;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.meishai.R;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.CateResponseData;
import com.meishai.entiy.UserInfo;
import com.meishai.net.ReqData;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.StringRequest;
import com.meishai.ui.base.BaseActivity;
import com.meishai.ui.fragment.home.adapter.HomeTopicListAdapter;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;
import com.meishai.util.GsonHelper;
import com.meishai.util.PullToRefreshHelper;
import com.nimbusds.jose.JOSEException;

/**
 * 美晒->话题列表
 *
 * @author yl
 */
public class HomeTopicListActivity extends BaseActivity implements
        OnClickListener {

    private PullToRefreshGridView mGridView;
    private int mPage = 1;
    private CateResponseData mResponseData;
    private HomeTopicListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_topic_list_activity);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mResponseData == null) {
            getRequestData(mPage);
        } else {
            mAdapter.setDatas(mResponseData);
        }
    }

    private void initView() {
        findViewById(R.id.backMain).setOnClickListener(this);

        mGridView = (PullToRefreshGridView) findViewById(R.id.gridview);
        mGridView.setMode(Mode.BOTH);
        mGridView.getRefreshableView().setNumColumns(2);
        PullToRefreshHelper.initIndicatorStart(mGridView);
        PullToRefreshHelper.initIndicator(mGridView);
        mAdapter = new HomeTopicListAdapter(this, getImageLoader());
        mGridView.setAdapter(mAdapter);
        mGridView.setOnRefreshListener(new OnRefreshListener<GridView>() {

            @Override
            public void onRefresh(PullToRefreshBase<GridView> refreshView) {
                if (refreshView.getCurrentMode() == Mode.PULL_FROM_START) {
                    getRequestData(1);

                } else if (refreshView.getCurrentMode() == Mode.PULL_FROM_END) {
                    if (mResponseData == null) {
                        getRequestData(1);
                        return;
                    }
                    if (mPage < mResponseData.pages) {
                        getRequestData(mPage + 1);
                    } else {
                        mGridView.postDelayed(new Runnable() {
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

        mGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Intent intent = TopicShowActivity.newIntent((int) id);
                startActivity(intent);
            }
        });

    }

    protected void getRequestData(int page) {
        showProgress("", getString(R.string.network_wait));

        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        // typeid:区分是分类还是话题,分类列表:typeid=1,话题列表:typeid=2
        ReqData reqData = new ReqData();
        reqData.setC("index");
        reqData.setA("topic");
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", userInfo.getUserID());
        data.put("page", String.valueOf(page));
        data.put("typeid", String.valueOf(2));
        reqData.setData(data);

        try {
            String url = getString(R.string.base_url) + reqData.toReqString();
            DebugLog.d(url);
            getRequestQueue().add(
                    new StringRequest(url, new Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            checkRequestData(response);

                            setNetComplete();
                        }
                    }, new ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            DebugLog.e("net error:" + error.getMessage());

                            AndroidUtil.showToast(R.string.reqFailed);

                            setNetComplete();
                        }
                    }));

        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    protected void setNetComplete() {
        hideProgress();
        mGridView.onRefreshComplete();
    }

    protected void checkRequestData(String response) {
        try {
            JSONObject obj = new JSONObject(response);
            int success = obj.getInt("success");
            if (success == 1) {
                CateResponseData responseData = GsonHelper.parseObject(
                        response, CateResponseData.class);
                if (responseData.page == 1) {
                    mResponseData = responseData;
                } else {
                    mResponseData.list.addAll(responseData.list);
                    mResponseData.page = responseData.page;
                    mPage = mResponseData.page;
                }

                mAdapter.setDatas(mResponseData);
                hideProgress();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            hideProgress();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backMain:
                finish();
                break;

            default:
                break;
        }
    }

}
