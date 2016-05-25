package com.meishai.ui.fragment.tryuse;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
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
import com.meishai.entiy.TryInfo;
import com.meishai.net.RespData;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.ui.base.BaseFragment;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.tryuse.TryNUseFragment.UiRefreshListener;
import com.meishai.ui.fragment.tryuse.adapter.TryListAdapter;
import com.meishai.ui.fragment.tryuse.req.TryReq;
import com.meishai.util.AndroidUtil;
import com.meishai.util.GsonHelper;
import com.meishai.util.PullToRefreshHelper;

/**
 * 试用(新)->子Fragment
 *
 * @author sh
 */
public class TryNChildFragment extends BaseFragment {

    private int currentPage = 1;
    private View view;
    static final String ARG_STATUS = "status";
    private String status = null;
    private List<TryInfo> infos = null;
    private PullToRefreshListView listView;
    private TryListAdapter tryListAdapter;

    private UiRefreshListener refreshListener;

    private CustomProgress mCustomProgress;

    private boolean isShow = false;

    public TryNChildFragment() {
    }

    public static TryNChildFragment newInstance(String type, boolean isShow) {
        TryNChildFragment f = new TryNChildFragment();
        Bundle args = new Bundle();
        args.putString(ARG_STATUS, type);
        args.putBoolean("isShow", isShow);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        status = getArguments().getString(ARG_STATUS);
        isShow = getArguments().getBoolean("isShow");
        view = inflater.inflate(R.layout.tryuse_child_n, null);
        currentPage = 1;
        infos = new ArrayList<TryInfo>();
        this.initView();
        this.loadData();
        return view;
    }

    public void setUiRefreshListener(UiRefreshListener refreshListener) {
        this.refreshListener = refreshListener;
    }

    private void initView() {
        listView = (PullToRefreshListView) view.findViewById(R.id.list);
        PullToRefreshHelper.initIndicatorStart(listView);
        PullToRefreshHelper.initIndicator(listView);
        tryListAdapter = new TryListAdapter(mContext, infos);
        listView.setAdapter(tryListAdapter);
        listView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                currentPage = 1;
                infos.clear();
                loadData();

            }

            @Override
            public void onPullUpToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                currentPage++;
                loadData();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(mContext, TryuseDetailActivity.class);
                TryInfo info = (TryInfo) parent.getAdapter().getItem(position);
                intent.putExtra(ConstantSet.BUNDLE_ID, info.getSid());
                startActivity(intent);
            }
        });
    }

    // 刷新完成
    private void onRefreshComplete() {
        listView.onRefreshComplete();
    }

    private void notifyAdapter() {
        tryListAdapter.setInfos(infos);
        tryListAdapter.notifyDataSetChanged();
    }

    private void loadData() {
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        data.put("page", String.valueOf(currentPage));
        data.put("status", this.status);

        if (isShow) {
            if (null == mCustomProgress) {
                mCustomProgress = CustomProgress.show(mContext,
                        mContext.getString(R.string.network_wait), true, null);
            } else {
                mCustomProgress.show();
            }
        }

        TryReq.init(mContext, data, new Listener<RespData>() {

            @Override
            public void onResponse(RespData response) {
                if (null != mCustomProgress) {
                    mCustomProgress.dismiss();
                }
                onRefreshComplete();
                if (response.isSuccess()) {
                    Type type = new TypeToken<List<TryInfo>>() {
                    }.getType();
                    List<TryInfo> resultInfos = GsonHelper.parseObject(
                            GsonHelper.toJson(response.getData()), type);
                    if (null != resultInfos && !resultInfos.isEmpty()) {
                        infos.addAll(resultInfos);
                        notifyAdapter();
                    }
                    if (null != refreshListener) {
                        refreshListener.refreshUI(response.getMore());
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
