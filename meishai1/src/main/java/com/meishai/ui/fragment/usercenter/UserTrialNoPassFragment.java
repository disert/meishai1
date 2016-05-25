/**
 */
package com.meishai.ui.fragment.usercenter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.google.gson.reflect.TypeToken;
import com.meishai.R;
import com.meishai.app.widget.list.IOnRefreshListener;
import com.meishai.app.widget.list.RefreshListView;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.TrialInfo;
import com.meishai.net.RespData;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.ui.base.BaseFragment;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.usercenter.adapter.TrialAdapter;
import com.meishai.ui.fragment.usercenter.req.TrialReq;
import com.meishai.util.AndroidUtil;
import com.meishai.util.GsonHelper;

/**
 * @ClassName: UserTrialNoPassActivity
 * @Description:我的试用->未通过
 */
public class UserTrialNoPassFragment extends BaseFragment implements
        OnClickListener {

    private View mNoPassView;
    private RefreshListView mListview;
    private TrialAdapter tiralAdapter;
    private List<TrialInfo> trialInfos = null;
    // 初始页(第一页数据)
    private int currentPage = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mNoPassView = inflater.inflate(R.layout.user_trial_nopass, null);
        currentPage = 1;
        trialInfos = new ArrayList<TrialInfo>();
        this.initView();
        this.loadData();
        return mNoPassView;
    }

    private void initView() {
        mListview = (RefreshListView) mNoPassView.findViewById(R.id.listview);
        mListview.setPullRefreshEnable(true);
        mListview.setPullLoadEnable(false);
        mListview.setRefreshListListener(new IOnRefreshListener() {

            @Override
            public void onRefresh() {
                mListview.stopRefresh();
                currentPage++;
                loadData();
            }

            @Override
            public void onLoadMore() {
                mListview.stopLoadMore();
            }
        });
    }

    private void notifyTrialAdapter() {
        if (null == tiralAdapter) {
            tiralAdapter = new TrialAdapter(mContext, TrialInfo.TRIAL_NO_PASS,
                    trialInfos);
            mListview.setAdapter(tiralAdapter);
        } else {
            tiralAdapter.settInfos(trialInfos);
            mListview.setAdapter(tiralAdapter);
        }
    }

    public void loadData() {
        showProgress(null, mContext.getString(R.string.network_wait));
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        data.put("status", TrialInfo.TRIAL_NO_PASS);
        data.put("page", String.valueOf(currentPage));
        data.put("pagesize", ConstantSet.PAGE_SIZE);
        TrialReq.tryList(mContext, data, new Listener<RespData>() {
            @Override
            public void onResponse(RespData response) {
                hideProgress();
                if (response.isSuccess()) {
                    Type type = new TypeToken<List<TrialInfo>>() {
                    }.getType();
                    List<TrialInfo> resultTrialInfos = GsonHelper.parseObject(
                            GsonHelper.toJson(response.getData()), type);
                    if (null != resultTrialInfos && !resultTrialInfos.isEmpty()) {
                        trialInfos.addAll(0, resultTrialInfos);
                        notifyTrialAdapter();
                    } else if (trialInfos.isEmpty()) {
                        AndroidUtil.showToast(response.getTips());
                    }
                }
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
                AndroidUtil.showToast(R.string.reqFailed);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }
    }

}