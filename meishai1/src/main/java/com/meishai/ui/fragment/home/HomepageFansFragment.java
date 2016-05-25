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
import com.meishai.entiy.HomepageUser;
import com.meishai.entiy.UserFans;
import com.meishai.net.RespData;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.ui.base.BaseFragment;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.home.HomePageActivity.HomePageListener;
import com.meishai.ui.fragment.home.adapter.HomepageFansAdapter;
import com.meishai.ui.fragment.home.req.HomepageReq;
import com.meishai.util.AndroidUtil;
import com.meishai.util.GsonHelper;
import com.meishai.util.PullToRefreshHelper;

/**
 * HomepageFansFragment 她的粉丝
 *
 * @author sh
 */
public class HomepageFansFragment extends BaseFragment {

    private HomePageListener homePageListener;
    private String uid = "";
    private int currentPage = 1;
    private View view;
    private List<UserFans> userFans = null;
    private HomepageFansAdapter fansAdapter;
    private PullToRefreshListView list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uid = getArguments().getString("uid");
    }

    public void setHomePageListener(HomePageListener homePageListener) {
        this.homePageListener = homePageListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.homepage_fans, null);
        currentPage = 1;
        userFans = new ArrayList<UserFans>();
        this.initView();
        this.loadData();
        return view;
    }

    private void initView() {
        list = (PullToRefreshListView) view.findViewById(R.id.list);
        PullToRefreshHelper.initIndicatorStart(list);
        PullToRefreshHelper.initIndicator(list);
        fansAdapter = new HomepageFansAdapter(mContext, userFans);
        list.setAdapter(fansAdapter);

        list.setOnRefreshListener(new OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                currentPage = 1;
                userFans.clear();
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
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        data.put("page", String.valueOf(currentPage));
        data.put("uid", uid);
        HomepageReq.fans(mContext, data, new Listener<RespData>() {

            @Override
            public void onResponse(RespData response) {
                if (response.isSuccess()) {

                    Type userType = new TypeToken<List<HomepageUser>>() {
                    }.getType();
                    List<HomepageUser> resultHomepageUser = GsonHelper
                            .parseObject(
                                    GsonHelper.toJson(response.getUserinfo()),
                                    userType);
                    if (null != resultHomepageUser
                            && !resultHomepageUser.isEmpty()
                            && null != homePageListener) {
                        homePageListener.refreshUI(resultHomepageUser.get(0));
                    }
                    Type type = new TypeToken<List<UserFans>>() {
                    }.getType();
                    List<UserFans> resultUserFans = GsonHelper.parseObject(
                            GsonHelper.toJson(response.getData()), type);

                    onRefreshComplete();
                    if (null != resultUserFans && !resultUserFans.isEmpty()) {
                        userFans.addAll(resultUserFans);
                        notifyAdapter();
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

    // 刷新完成
    private void onRefreshComplete() {
        list.onRefreshComplete();
    }

    private void notifyAdapter() {
        fansAdapter.setUserFans(userFans);
        fansAdapter.notifyDataSetChanged();
    }

}
