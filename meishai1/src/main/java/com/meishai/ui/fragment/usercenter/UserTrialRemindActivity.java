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
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.MyMsg;
import com.meishai.entiy.MyMsg.MsgTableId;
import com.meishai.net.RespData;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.ui.base.BaseActivity;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.usercenter.adapter.MsgAdapter;
import com.meishai.ui.fragment.usercenter.req.MsgReq;
import com.meishai.util.AndroidUtil;
import com.meishai.util.GsonHelper;
import com.meishai.util.PullToRefreshHelper;

/**
 * 我的-试用提醒
 *
 * @ClassName: UserTrialRemindActivity
 */
public class UserTrialRemindActivity extends BaseActivity implements
        OnClickListener {

    private Context mContext = UserTrialRemindActivity.this;
    private int currentPage = 1;
    private Button mBtnBack;
    private PullToRefreshListView mListView;
    private MsgAdapter msgAdapter;
    private List<MyMsg> msgs = null;

    public static Intent newIntent() {
        Intent intent = new Intent(GlobalContext.getInstance(),
                UserTrialRemindActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_trial_remind);
        currentPage = 1;
        msgs = new ArrayList<MyMsg>();
        this.initView();
        this.loadData();
    }

    /**
     * @param
     * @return void
     * @throws
     * @Title: initView
     * @Description:
     */
    private void initView() {
        mBtnBack = (Button) this.findViewById(R.id.backMain);
        mBtnBack.setOnClickListener(this);
        mListView = (PullToRefreshListView) this.findViewById(R.id.list);
        PullToRefreshHelper.initIndicatorStart(mListView);
        PullToRefreshHelper.initIndicator(mListView);
        msgAdapter = new MsgAdapter(mContext, msgs);
        mListView.setAdapter(msgAdapter);

        mListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                currentPage = 1;
                msgs.clear();
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

    private void loadData() {
        showProgress(null, mContext.getString(R.string.network_wait));
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        data.put("tableid", MsgTableId.TRIAL.getType());
        data.put("page", String.valueOf(currentPage));
        data.put("pagesize", ConstantSet.PAGE_SIZE);
        MsgReq.message(mContext, data, new Listener<RespData>() {
            @Override
            public void onResponse(RespData response) {
                hideProgress();
                if (response.isSuccess()) {
                    onRefreshComplete();
                    Type type = new TypeToken<List<MyMsg>>() {
                    }.getType();
                    List<MyMsg> resultMyMsgs = GsonHelper.parseObject(
                            GsonHelper.toJson(response.getData()), type);
                    if (null != resultMyMsgs && !resultMyMsgs.isEmpty()) {
                        msgs.addAll(resultMyMsgs);
                        notifyMsgAdapter();
                    } else if (msgs.isEmpty()) {
                        AndroidUtil.showToast(response.getTips());
                    }
                }
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
                AndroidUtil.showToast(mContext.getString(R.string.reqFailed));
            }
        });
    }

    // 刷新完成
    private void onRefreshComplete() {
        mListView.onRefreshComplete();
    }

    private void notifyMsgAdapter() {
        msgAdapter.setMsgs(msgs);
        msgAdapter.notifyDataSetChanged();
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
