package com.meishai.ui.fragment.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.DaysTopicResponseBean;
import com.meishai.entiy.FreeTrialRespData;
import com.meishai.entiy.UserInfo;
import com.meishai.net.ReqData;
import com.meishai.net.volley.Response;
import com.meishai.net.volley.VolleyError;
import com.meishai.ui.base.BaseActivity;
import com.meishai.ui.fragment.home.adapter.DaysTopicAdapter;
import com.meishai.ui.fragment.tryuse.adapter.FreeTrialAdapter;
import com.meishai.ui.fragment.tryuse.req.FuLiSheReq;
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
 * Created by Administrator on 2016/5/21.
 */
public class DaysTopicActivity extends BaseActivity {
    private static final int TYPE_PONGCETUAN = 3;
    private static final int TYPE_DAYS_TOPIC = 2;
    private PullToRefreshListView mListView;
    private boolean isLoading;
    private int currentPage;
    private DaysTopicAdapter mAdapter;
    private DaysTopicResponseBean mResqData;
    private TextView mTitle;
    private int mType;

    /**
     * 每日话题的intent
     *
     * @param context
     * @return
     */
    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, DaysTopicActivity.class);
        return intent;
    }

    /**
     * 评测团的intent
     *
     * @return
     */
    public static Intent newIntent() {
        Intent intent = new Intent(GlobalContext.getInstance(), DaysTopicActivity.class);
        intent.putExtra("type", TYPE_PONGCETUAN);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.common_listview_activity);

        initView();

        mType = getIntent().getIntExtra("type", TYPE_DAYS_TOPIC);

        getRequestData(1);

    }

    private void initView() {
        mTitle = (TextView) findViewById(R.id.title);
        findViewById(R.id.comment_bar_root).setVisibility(View.GONE);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mListView = (PullToRefreshListView) findViewById(R.id.listview);
        mListView.setBackgroundColor(Color.WHITE);
        mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        PullToRefreshHelper.initIndicatorStart(mListView);
        PullToRefreshHelper.initIndicator(mListView);

        // 写适配器
        mAdapter = new DaysTopicAdapter(this);
        mListView.setAdapter(mAdapter);
        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {

                if (refreshView.getCurrentMode() == PullToRefreshBase.Mode.PULL_FROM_START) {
                    getRequestData(1);
                } else if (refreshView.getCurrentMode() == PullToRefreshBase.Mode.PULL_FROM_END) {
                    if (mResqData == null) {
                        getRequestData(1);
                        return;
                    }
                    if (currentPage < mResqData.pages) {
                        getRequestData(++currentPage);
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
//					DebugLog.w("总数item:" + totalItemCount + "当前item:" + (firstVisibleItem + visibleItemCount));
                    if (currentPage < mResqData.pages) {
                        getRequestData(++currentPage);
                    } else {
                        mListView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
//								AndroidUtil
//										.showToast(R.string.drop_down_list_footer_no_more_text);
                                setNetComplete();
                            }
                        }, 500);
                    }
                }
            }
        });
    }

    private void getRequestData(final int page) {
        switch (mType) {
            case TYPE_DAYS_TOPIC:
                sendMsg(daysTopic(page));
                break;
            case TYPE_PONGCETUAN:
                sendMsg(pingcetuan(page));
                break;
        }
    }

    private String pingcetuan(int page) {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        ReqData reqData = new ReqData();
        reqData.setC("index");
        reqData.setA("pingcetuan");
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", userInfo.getUserID());
        data.put("page", String.valueOf(page));
        reqData.setData(data);
        try {
            String reqString = reqData.toReqString();
            String url = getString(R.string.base_url) + reqString;
            DebugLog.d("url:" + url);
            return url;
        } catch (JOSEException e) {
            e.printStackTrace();
            return "";
        }
    }

    private void setNetComplete() {
        mListView.onRefreshComplete();
        hideProgress();
        isLoading = false;
    }

    @Override
    public void updateUI(Object obj) {
        if (obj == null || TextUtils.isEmpty(obj.toString())) return;
        String response = obj.toString();

        checkRespData(response);
        setNetComplete();
    }

    @Override
    public void failt(Object ojb) {
        AndroidUtil.showToast(R.string.reqFailed);

        setNetComplete();
    }

    protected void checkRespData(String response) {
        try {
            JSONObject obj = new JSONObject(response);
            int success = obj.getInt("success");
            if (success == 1) {
                DaysTopicResponseBean resqData = GsonHelper
                        .parseObject(response, DaysTopicResponseBean.class);
                if (resqData == null) {
                    AndroidUtil.showToast("数据解析失败!");
                    return;
                }

                if (resqData.page == 1) {
                    mResqData = resqData;
                    mTitle.setText(resqData.page_title);

                } else {
                    mResqData.getList().addAll(resqData.getList());
                    // mAdapter.addData(datas)
                    mResqData.page = resqData.page;
                }
                currentPage = mResqData.page;
                mAdapter.setData(mResqData.getList(), getImageLoader());
                mAdapter.notifyDataSetChanged();
            } else {
                AndroidUtil.showToast(R.string.reqFailed);
            }

        } catch (JSONException e) {
            DebugLog.e(e.getMessage());
        }
    }

    public String daysTopic(int page) {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        ReqData reqData = new ReqData();
        reqData.setC("index");
        reqData.setA("huati");
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", userInfo.getUserID());
        data.put("page", String.valueOf(page));
        reqData.setData(data);
        try {
            String reqString = reqData.toReqString();
            String url = getString(R.string.base_url) + reqString;
            DebugLog.d("url:" + url);
            return url;
        } catch (JOSEException e) {
            e.printStackTrace();
            return "";
        }
    }
}
