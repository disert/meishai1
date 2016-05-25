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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
import com.meishai.ui.fragment.tryuse.TryuseDetailActivity;
import com.meishai.ui.fragment.usercenter.adapter.FavTryAdapter;
import com.meishai.ui.fragment.usercenter.req.FavReq;
import com.meishai.util.AndroidUtil;
import com.meishai.util.GsonHelper;
import com.meishai.util.PullToRefreshHelper;

/**
 * 我的->我的收藏->试用
 *
 * @author sh
 */
public class UserFavTryFragment extends BaseFragment {
    private int currentPage = 1;
    private View view;
    private FavTryAdapter tryAdapter;
    private List<TrialInfo> trialInfos = null;
    private PullToRefreshListView mTryList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.user_fav_try, null);
        currentPage = 1;
        trialInfos = new ArrayList<TrialInfo>();
        this.initView();
        this.loadData();
        return view;
    }

    private void initView() {
        mTryList = (PullToRefreshListView) view.findViewById(R.id.try_list);
        PullToRefreshHelper.initIndicatorStart(mTryList);
        PullToRefreshHelper.initIndicator(mTryList);
        tryAdapter = new FavTryAdapter(mContext, trialInfos);
        mTryList.setAdapter(tryAdapter);
        mTryList.setOnRefreshListener(new OnRefreshListener2<ListView>() {
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
        mTryList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,
                                    int positon, long arg3) {
                TrialInfo trialInfo = (TrialInfo) adapterView.getAdapter()
                        .getItem(positon);
                mContext.startActivity(TryuseDetailActivity.newIntent(
                        trialInfo.getSid(), ""));
            }
        });
    }

    private void notifySaleAdapter() {
        tryAdapter.settInfos(trialInfos);
        tryAdapter.notifyDataSetChanged();
    }

    // 刷新完成
    private void onRefreshComplete() {
        mTryList.onRefreshComplete();
    }

    private void loadData() {
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        data.put("page", String.valueOf(currentPage));
        data.put("type", "try");
        FavReq.fav(mContext, data, new Listener<RespData>() {

            @Override
            public void onResponse(RespData response) {
                if (response.isSuccess()) {
                    Type type = new TypeToken<List<TrialInfo>>() {
                    }.getType();
                    List<TrialInfo> resultTrialInfos = GsonHelper.parseObject(
                            GsonHelper.toJson(response.getData()), type);

                    onRefreshComplete();
                    if (null != resultTrialInfos && !resultTrialInfos.isEmpty()) {
                        trialInfos.addAll(resultTrialInfos);
                        notifySaleAdapter();
                    }
                } else {
                    AndroidUtil.showToast(response.getTips());
                }

            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
//				AndroidUtil.showToast(mContext.getString(R.string.reqFailed));
            }
        });
    }

}
