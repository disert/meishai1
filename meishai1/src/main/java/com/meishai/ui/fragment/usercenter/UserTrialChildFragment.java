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
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.meishai.R;
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
import com.meishai.util.PullToRefreshHelper;

/**
 * @ClassName: UserTrialActivity
 * @Description: 我的试用
 */
public class UserTrialChildFragment extends BaseFragment {

    static final String ARG_TYPE = "type";
    private String type = null;
    private View mView;
    private PullToRefreshListView mtryList;
    private TrialAdapter tiralAdapter;
    private List<TrialInfo> trialInfos = null;
    // 初始页(第一页数据)
    private int currentPage = 1;

    public UserTrialChildFragment() {

    }

    public static UserTrialChildFragment newInstance(String type) {
        UserTrialChildFragment f = new UserTrialChildFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        f.setArguments(args);
        return f;
    }

    public void setType(String type) {
        this.type = type;
        currentPage = 1;
        trialInfos.clear();
        this.loadData();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getArguments().getString(ARG_TYPE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.user_trial_child, null);
        currentPage = 1;
        trialInfos = new ArrayList<TrialInfo>();
        this.initView();
        this.loadData();
        return mView;
    }

    private void initView() {
        mtryList = (PullToRefreshListView) mView.findViewById(R.id.try_list);
        PullToRefreshHelper.initIndicatorStart(mtryList);
        PullToRefreshHelper.initIndicator(mtryList);

        mtryList.setOnRefreshListener(new OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                currentPage = 1;
                trialInfos.clear();
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

    private void notifyTrialAdapter() {
        if (null == tiralAdapter) {
            tiralAdapter = new TrialAdapter(mContext, type, trialInfos);
            mtryList.setAdapter(tiralAdapter);
        } else {
            tiralAdapter.settInfos(trialInfos);
            tiralAdapter.notifyDataSetChanged();
        }
    }

    public void loadData() {
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        data.put("status", type);
        data.put("page", String.valueOf(currentPage));
        data.put("pagesize", ConstantSet.PAGE_SIZE);
        TrialReq.tryList(mContext, data, new Listener<RespData>() {
            @Override
            public void onResponse(RespData response) {
                hideProgress();
                if (response.isSuccess()) {
                    onRefreshComplete();
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
                AndroidUtil.showToast(mContext.getString(R.string.reqFailed));
            }
        });
    }

    // 刷新完成
    private void onRefreshComplete() {
        mtryList.onRefreshComplete();
    }

}