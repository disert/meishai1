package com.meishai.ui.fragment.tryuse;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.meishai.R;
import com.meishai.app.util.DateUtil;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.TryInfo;
import com.meishai.net.RespData;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.ui.base.BaseFragment;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.tryuse.adapter.TryGridAdapter;
import com.meishai.ui.fragment.tryuse.req.TryReq;
import com.meishai.util.AndroidUtil;
import com.meishai.util.GsonHelper;
import com.meishai.util.PullToRefreshHelper;

/**
 * 试用->子Fragment
 *
 * @author sh
 */
public class TryChildFragment extends BaseFragment {

    private int currentPage = 1;
    private View view;
    static final String ARG_TYPE = "type";
    private String type = null;
    private List<TryInfo> infos = null;
    private PullToRefreshGridView gridView;
    private TryGridAdapter tryGridAdapter;
    // 默认不运行
    private boolean isRun = false;

    public TryChildFragment() {
    }

    public static TryChildFragment newInstance(String type) {
        TryChildFragment f = new TryChildFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        type = getArguments().getString(ARG_TYPE);
        view = inflater.inflate(R.layout.tryuse_child, null);
        isRun = false;
        currentPage = 1;
        infos = new ArrayList<TryInfo>();
        this.initView();
        this.loadData();
        return view;
    }

    @Override
    public void onStart() {
        startHandler();
//		isRun = false;
//		currentPage = 1;
//		infos = new ArrayList<TryInfo>();
//		this.initView();
//		this.loadData();
        super.onStart();
    }

    private void initView() {
        gridView = (PullToRefreshGridView) view.findViewById(R.id.try_grid);
        PullToRefreshHelper.initIndicator(gridView);
        tryGridAdapter = new TryGridAdapter(mContext, infos);
        gridView.setAdapter(tryGridAdapter);
        gridView.setOnRefreshListener(new OnRefreshListener2<GridView>() {

            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<GridView> refreshView) {
                String label = DateUtils.formatDateTime(mContext,
                        System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
                                | DateUtils.FORMAT_SHOW_DATE
                                | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

            }

            @Override
            public void onPullUpToRefresh(
                    PullToRefreshBase<GridView> refreshView) {
                currentPage++;
                loadData();
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(mContext, TryuseDetailActivity.class);
                TryInfo info = (TryInfo) parent.getAdapter().getItem(position);
                intent.putExtra(ConstantSet.BUNDLE_ID, info.getSid());
                startActivity(intent);
                stopHandler();
            }
        });
    }

    private Handler handler = new Handler();

    private void notifySaleAdapter() {
        tryGridAdapter.setInfos(infos);
        tryGridAdapter.notifyDataSetChanged();
        if (!isRun) {
            handler.postDelayed(runnable, 1000);
            isRun = true;
        }
    }

    // 刷新完成
    private void onRefreshComplete() {
        gridView.onRefreshComplete();
    }

    private void loadData() {
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        data.put("page", String.valueOf(currentPage));
        data.put("stype", this.type);
        TryReq.index(mContext, data, new Listener<RespData>() {

            @Override
            public void onResponse(RespData response) {
                if (response.isSuccess()) {
                    Type type = new TypeToken<List<TryInfo>>() {
                    }.getType();
                    List<TryInfo> resultInfos = GsonHelper.parseObject(
                            GsonHelper.toJson(response.getData()), type);

                    onRefreshComplete();
                    if (null != resultInfos && !resultInfos.isEmpty()) {
                        infos.addAll(resultInfos);
                        notifySaleAdapter();
                    }
                } else {
                    AndroidUtil.showToast(response.getTips());
                }

            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                AndroidUtil.showToast(mContext.getString(R.string.reqFailed));
            }
        });
    }

    private void startHandler() {
        if (null != handler) {
            handler.postDelayed(runnable, 1000);
        }
    }

    private void stopHandler() {
        if (null != handler) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden) {
            stopHandler();
        } else {
            startHandler();
        }
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onPause() {
        stopHandler();
        super.onPause();
    }

    @Override
    public void onStop() {
        stopHandler();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        stopHandler();
        super.onDestroy();
    }

    private int TIME_COUNT_INTERVAL = 1000;

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // 更新数据
            updateTryinfos();
            // 更新时间
            updateTime();
            handler.postDelayed(this, TIME_COUNT_INTERVAL);
        }
    };

    private void updateTryinfos() {
        if (null == infos || infos.isEmpty()) {
            return;
        }
        for (TryInfo info : infos) {
            info.setEndtime(info.getEndtime() - 1000);
        }
    }

    private void updateTime() {
        if (null == gridView || null == infos || infos.isEmpty()) {
            return;
        }
        int firstVisibleItemIndex = gridView.getRefreshableView()
                .getFirstVisiblePosition();

        for (int i = 0; i < gridView.getRefreshableView().getChildCount(); i++) {
            View v = gridView.getRefreshableView().getChildAt(i);
            TryInfo item = (TryInfo) gridView.getRefreshableView().getAdapter()
                    .getItem(firstVisibleItemIndex + i);
            com.meishai.ui.fragment.tryuse.adapter.TryGridAdapter.ViewHolder vh = (com.meishai.ui.fragment.tryuse.adapter.TryGridAdapter.ViewHolder) v
                    .getTag();
            vh.endtime.setText(DateUtil.timeFormat(item.getEndtime()));
        }

    }
}
