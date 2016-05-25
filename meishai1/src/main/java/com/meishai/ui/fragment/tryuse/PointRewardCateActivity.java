package com.meishai.ui.fragment.tryuse;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.entiy.PointRewardRespData;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.ui.base.BaseActivity;
import com.meishai.ui.fragment.tryuse.adapter.PointRewardCateAdapter;
import com.meishai.ui.fragment.tryuse.req.FuLiSheReq;
import com.meishai.ui.popup.ShareMorePopupWindow;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;
import com.meishai.util.GsonHelper;
import com.meishai.util.PullToRefreshHelper;

/**
 * 积分商城 - 分类列表的activity
 *
 * @author Administrator yl
 */
public class PointRewardCateActivity extends BaseActivity implements
        OnClickListener {

    private PullToRefreshListView mRefreshListView;
    private int mTypeid;
    private int mPage;
    private PointRewardRespData mData;
    private PointRewardCateAdapter mAdapter;
    private TextView mTitle;
    private ShareMorePopupWindow sharePop;
    private LinearLayout mLayRoot;

    public static Intent newIntent(int typeid) {

        Intent intent = new Intent(GlobalContext.getInstance()
                .getApplicationContext(), PointRewardCateActivity.class);
        intent.putExtra("typeid", typeid);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.point_reward_cate_activity);
        initView();
        initData();
    }

    private void initView() {

        mLayRoot = (LinearLayout) findViewById(R.id.root);

        mTitle = (TextView) findViewById(R.id.title);
        findViewById(R.id.backMain).setOnClickListener(this);

        mRefreshListView = (PullToRefreshListView) findViewById(R.id.gridview);
        mRefreshListView.setMode(Mode.BOTH);
        PullToRefreshHelper.initIndicatorStart(mRefreshListView);
        PullToRefreshHelper.initIndicator(mRefreshListView);
    }

    private void initData() {

        mTypeid = getIntent().getIntExtra("typeid", 0);

        mAdapter = new PointRewardCateAdapter(this, getImageLoader());
        mRefreshListView.setAdapter(mAdapter);
        mRefreshListView
                .setOnRefreshListener(new OnRefreshListener<ListView>() {

                    @Override
                    public void onRefresh(
                            PullToRefreshBase<ListView> refreshView) {
                        if (refreshView.getCurrentMode() == Mode.PULL_FROM_START) {
                            getRequestData(1);
                        } else if (refreshView.getCurrentMode() == Mode.PULL_FROM_END) {
                            if (mPage < mData.pages) {
                                getRequestData(mPage + 1);
                            } else {
                                mRefreshListView.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        AndroidUtil
                                                .showToast(R.string.drop_down_list_footer_no_more_text);
                                        mRefreshListView.onRefreshComplete();
                                    }
                                }, 500);
                            }
                        }

                    }
                });

    }

    @Override
    protected void onResume() {
        super.onResume();
        getRequestData(1);

    }

    private void getRequestData(int page) {
//		mLayRoot.setVisibility(View.GONE);
        showProgress("", getString(R.string.network_wait));
        FuLiSheReq.cateList(mTypeid, page, new Listener<String>() {

            @Override
            public void onResponse(String response) {
                checkRespData(response);
//				mLayRoot.setVisibility(View.VISIBLE);
                hideProgress();
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                AndroidUtil.showToast(R.string.reqFailed);
                hideProgress();
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
                mPage = mData.page;
                mAdapter.setData(mData);
            } else {
                AndroidUtil.showToast(R.string.reqFailed);

            }

        } catch (JSONException e) {
            DebugLog.e(e.getMessage());
            e.printStackTrace();
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
