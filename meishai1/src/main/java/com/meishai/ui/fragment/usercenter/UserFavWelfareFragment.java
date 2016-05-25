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
import com.meishai.app.widget.CustomProgress;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.PointExchange;
import com.meishai.net.RespData;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.ui.base.BaseFragment;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.meiwu.FindPointDetailActivity;
import com.meishai.ui.fragment.meiwu.adapter.FindPointListAdapter;
import com.meishai.ui.fragment.usercenter.req.FavReq;
import com.meishai.util.AndroidUtil;
import com.meishai.util.GsonHelper;
import com.meishai.util.PullToRefreshHelper;

/**
 * 我的->我的收藏->福利
 *
 * @author sh
 */
public class UserFavWelfareFragment extends BaseFragment {

    private int currentPage = 1;
    private View view;
    private List<PointExchange> pointExchanges = null;
    private PullToRefreshListView mPointList;
    private FindPointListAdapter pointListAdapter;
    private CustomProgress mProgress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.find_point_cate, null);
        currentPage = 1;
        pointExchanges = new ArrayList<PointExchange>();
        this.initView();
        this.loadData();
        return view;
    }

    private void initView() {
        mPointList = (PullToRefreshListView) view.findViewById(R.id.point_list);
        PullToRefreshHelper.initIndicatorStart(mPointList);
        PullToRefreshHelper.initIndicator(mPointList);
        pointListAdapter = new FindPointListAdapter(mContext, pointExchanges);
        mPointList.setAdapter(pointListAdapter);

        mPointList.setOnRefreshListener(new OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                currentPage = 1;
                pointExchanges.clear();
                loadData();
            }

            @Override
            public void onPullUpToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                currentPage++;
                loadData();
            }
        });

        mPointList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,
                                    int postion, long arg3) {
                PointExchange exchange = (PointExchange) adapterView
                        .getAdapter().getItem(postion);
                mContext.startActivity(FindPointDetailActivity.newIntent(
                        exchange.getId(), ""));
            }
        });
    }

    private void notifySaleAdapter() {
        pointListAdapter.setPointExchanges(pointExchanges);
        pointListAdapter.notifyDataSetChanged();
    }

    // 刷新完成
    private void onRefreshComplete() {
        mPointList.onRefreshComplete();
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
        data.put("type", "welfare");
        FavReq.fav(mContext, data, new Listener<RespData>() {

            @Override
            public void onResponse(RespData response) {
                if (null != mProgress) {
                    mProgress.hide();
                }
                if (response.isSuccess()) {
                    Type type = new TypeToken<List<PointExchange>>() {
                    }.getType();
                    List<PointExchange> resultPointExchanges = GsonHelper
                            .parseObject(GsonHelper.toJson(response.getData()),
                                    type);

                    onRefreshComplete();
                    if (null != resultPointExchanges
                            && !resultPointExchanges.isEmpty()) {
                        pointExchanges.addAll(resultPointExchanges);
                        notifySaleAdapter();
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
}
