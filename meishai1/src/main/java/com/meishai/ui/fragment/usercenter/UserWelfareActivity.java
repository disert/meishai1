package com.meishai.ui.fragment.usercenter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.app.widget.CustomProgress;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.UserWelfare;
import com.meishai.net.RespData;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.ui.base.BaseActivity;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.usercenter.adapter.WelfareAdapter;
import com.meishai.ui.fragment.usercenter.req.WelfareReq;
import com.meishai.util.AndroidUtil;
import com.meishai.util.GsonHelper;
import com.meishai.util.PullToRefreshHelper;

/**
 * 我的 ->我的福利
 *
 * @author sh
 */
public class UserWelfareActivity extends BaseActivity implements
        OnClickListener {
    private Context mContext = UserWelfareActivity.this;
    private int currentPage = 1;
    private Button mBtnBack;
    private WelfareAdapter welfareAdapter;
    private List<UserWelfare> welfares;
    private PullToRefreshListView list;
    private CustomProgress mProgress;

    public static Intent newIntent() {
        Intent intent = new Intent(GlobalContext.getInstance(),
                UserWelfareActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_welfare);
        currentPage = 1;
        welfares = new ArrayList<UserWelfare>();
        this.initView();
        this.loadData();
    }

    private void initView() {
        mBtnBack = (Button) this.findViewById(R.id.backMain);
        mBtnBack.setOnClickListener(this);
        list = (PullToRefreshListView) this.findViewById(R.id.list);
        PullToRefreshHelper.initIndicatorStart(list);
        PullToRefreshHelper.initIndicator(list);
        welfareAdapter = new WelfareAdapter(mContext, welfares);
        list.setAdapter(welfareAdapter);

        list.setOnRefreshListener(new OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                currentPage = 1;
                welfares.clear();
                loadData();
            }

            @Override
            public void onPullUpToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                currentPage++;
                loadData();
            }
        });
    }

    private void notifyAdapter() {
        welfareAdapter.setWelfares(welfares);
        welfareAdapter.notifyDataSetChanged();
    }

    private void loadData() {
        if (null == mProgress) {
            mProgress = CustomProgress.show(mContext,
                    mContext.getString(R.string.network_wait), true, null);
        } else {
            mProgress.show();
        }
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        data.put("page", String.valueOf(currentPage));
        WelfareReq.welfare(mContext, data, new Listener<RespData>() {

            @Override
            public void onResponse(RespData response) {
                if (null != mProgress) {
                    mProgress.hide();
                }
                if (response.isSuccess()) {
                    Type type = new TypeToken<List<UserWelfare>>() {
                    }.getType();
                    List<UserWelfare> resultWelfares = GsonHelper.parseObject(
                            GsonHelper.toJson(response.getData()), type);

                    onRefreshComplete();
                    if (null != resultWelfares && !resultWelfares.isEmpty()) {
                        welfares.addAll(resultWelfares);
                        notifyAdapter();

                    }
                } else {
                    AndroidUtil.showToast(response.getTips());
                }

            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (null != mProgress) {
                    mProgress.hide();
                }
                AndroidUtil.showToast(mContext.getString(R.string.reqFailed));
            }
        });
    }

    // 刷新完成
    private void onRefreshComplete() {
        list.onRefreshComplete();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backMain:
                if (null != mProgress) {
                    mProgress.dismiss();
                }
                finish();
                break;
            default:
                break;
        }
    }

}
