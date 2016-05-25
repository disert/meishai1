package com.meishai.ui.fragment.meiwu;

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
import com.meishai.ui.fragment.meiwu.adapter.FindPointListAdapter;
import com.meishai.ui.fragment.meiwu.req.FindReq;
import com.meishai.util.AndroidUtil;
import com.meishai.util.GsonHelper;
import com.meishai.util.PullToRefreshHelper;

/**
 * 发现->积分(分类)
 *
 * @author sh
 */
public class FindPointChildFragment extends BaseFragment {

    private int currentPage = 1;
    private View view;
    static final String ARG_TYPE = "type";
    private String type = null;
    private List<PointExchange> pointExchanges = null;
    private PullToRefreshListView mPointList;
    private FindPointListAdapter pointListAdapter;

    private CustomProgress mCustomProgress;

    // 是否显示请求等待
    private boolean isShow = false;

    public FindPointChildFragment() {

    }

    public static FindPointChildFragment newInstance(String type, boolean isShow) {
        FindPointChildFragment f = new FindPointChildFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        args.putBoolean("isShow", isShow);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getArguments().getString(ARG_TYPE);
        isShow = getArguments().getBoolean("isShow");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.find_point_cate, null);
            currentPage = 1;
            pointExchanges = new ArrayList<PointExchange>();
            this.initView();
            this.loadData();
        }

        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }


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
        mPointList
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        PointExchange exchange = (PointExchange) parent
                                .getAdapter().getItem(position);
                        startActivity(FindPointDetailActivity.newIntent(
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

        if (isShow) {
            if (null == mCustomProgress) {
                mCustomProgress = CustomProgress.show(mContext,
                        mContext.getString(R.string.network_wait), true, null);
            } else {
                mCustomProgress.show();
            }
        }
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        data.put("page", String.valueOf(currentPage));
        data.put("type", this.type);
        FindReq.point(mContext, data, new Listener<RespData>() {

            @Override
            public void onResponse(RespData response) {
                if (null != mCustomProgress) {
                    mCustomProgress.dismiss();
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
                if (null != mCustomProgress) {
                    mCustomProgress.dismiss();
                }
                AndroidUtil.showToast(mContext.getString(R.string.reqFailed));
            }
        });
    }
}
