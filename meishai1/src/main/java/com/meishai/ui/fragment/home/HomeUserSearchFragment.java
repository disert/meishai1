package com.meishai.ui.fragment.home;

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
import com.meishai.entiy.UserFans;
import com.meishai.net.RespData;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.ui.base.BaseFragment;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.home.adapter.HomepageFansAdapter;
import com.meishai.ui.fragment.home.req.HomeSearchReq;
import com.meishai.util.AndroidUtil;
import com.meishai.util.GsonHelper;
import com.meishai.util.PullToRefreshHelper;

/**
 * 晒晒搜索->会员
 *
 * @author sh
 */
public class HomeUserSearchFragment extends BaseFragment {
    private int currentPage = 1;
    private View view;
    private List<UserFans> userFans = null;
    private HomepageFansAdapter fansAdapter;
    private PullToRefreshListView list;
    // 搜索关键词
    private String keyword = "";

    private OnUserSearchListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.homepage_fans, null);
        currentPage = 1;
        userFans = new ArrayList<UserFans>();
        this.initView();
        // this.loadData();
        return view;
    }

    public OnUserSearchListener getListener() {
        if (null == listener) {
            listener = new OnUserSearchListener() {

                @Override
                public void onUserSearch(String kword) {
                    keyword = kword;
                    currentPage = 1;
                    userFans = new ArrayList<UserFans>();
                    loadData();
                }
            };
        }
        return listener;
    }

    private void initView() {
        list = (PullToRefreshListView) view.findViewById(R.id.list);
        PullToRefreshHelper.initIndicator(list);
        fansAdapter = new HomepageFansAdapter(mContext, userFans);
        list.setAdapter(fansAdapter);

        list.setOnRefreshListener(new OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
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
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        // 会员搜索
        data.put("type", HomeSearchReq.SEARCH_USER);
        data.put("keyword", keyword);
        data.put("page", String.valueOf(currentPage));
        HomeSearchReq.search(mContext, data, new Listener<RespData>() {

            @Override
            public void onResponse(RespData response) {
                if (response.isSuccess()) {
                    Type type = new TypeToken<List<UserFans>>() {
                    }.getType();
                    List<UserFans> resultUserFans = GsonHelper.parseObject(
                            GsonHelper.toJson(response.getData()), type);

                    onRefreshComplete();
                    if (null != resultUserFans && !resultUserFans.isEmpty()) {
                        userFans.addAll(resultUserFans);
                    }
                    notifyAdapter();
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

    // 刷新完成
    private void onRefreshComplete() {
        list.onRefreshComplete();
    }

    private void notifyAdapter() {
        fansAdapter.setUserFans(userFans);
        fansAdapter.notifyDataSetChanged();
    }

    public interface OnUserSearchListener {
        public void onUserSearch(String kword);
    }

}
