/**
 */
package com.meishai.ui.fragment.home;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.meishai.R;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.HomepageUser;
import com.meishai.entiy.TopicItem;
import com.meishai.net.RespData;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.ui.base.BaseFragment;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.home.HomePageActivity.HomePageListener;
import com.meishai.ui.fragment.home.adapter.TopicAdapter;
import com.meishai.ui.fragment.home.req.HomepageReq;
import com.meishai.util.AndroidUtil;
import com.meishai.util.GsonHelper;
import com.meishai.util.PullToRefreshHelper;

/**
 * @author sh
 * @ClassName: HomePageTopicFragment
 * @Description: 个人主页-> 她的话题
 */
public class HomePageTopicFragment extends BaseFragment {

    private View mRootView;
    private PullToRefreshListView mListView;
    private TopicAdapter mAdapter;
    private int mPage = 1;

    private HomePageListener homePageListener;
    private String uid = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uid = getArguments().getString("uid");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.home_timeline, null);
        initView(mRootView);
        getRequestTopic();
        return mRootView;
    }

    public void setHomePageListener(HomePageListener homePageListener) {
        this.homePageListener = homePageListener;
    }

    private void initView(View v) {
        mListView = (PullToRefreshListView) v
                .findViewById(R.id.home_timeline_listview);
        mListView.setMode(Mode.BOTH);
        PullToRefreshHelper.initIndicatorStart(mListView);
        PullToRefreshHelper.initIndicator(mListView);
        mAdapter = new TopicAdapter(getActivity(), getImageLoader());
        mListView.setAdapter(mAdapter);

        mListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                mPage = 1;
                mAdapter.clear();
                getRequestTopic();
            }

            @Override
            public void onPullUpToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                mPage++;
                getRequestTopic();
            }
        });

        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                TopicItem topic = (TopicItem) parent.getAdapter().getItem(
                        position);
                Intent intent = TopicShowActivity.newIntent(topic);
                startActivity(intent);

            }
        });

    }

    private void getRequestTopic() {
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        data.put("page", String.valueOf(mPage));
        data.put("uid", uid);
        data.put("pagesize", String.valueOf(3));
        HomepageReq.topic(mContext, data, new Listener<RespData>() {

            @Override
            public void onResponse(RespData response) {
                hideProgress();
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
                    Type type = new TypeToken<List<TopicItem>>() {
                    }.getType();
                    List<TopicItem> resultUserTopicItems = GsonHelper
                            .parseObject(GsonHelper.toJson(response.getData()),
                                    type);

                    mListView.onRefreshComplete();
                    if (null != resultUserTopicItems
                            && !resultUserTopicItems.isEmpty()) {
                        mAdapter.addCollection(resultUserTopicItems);
                    } else {
                        mListView.onRefreshComplete();
                        // AndroidUtil
                        // .showToast(R.string.drop_down_list_footer_no_more_text);
                        mListView.setMode(Mode.DISABLED);
                    }
                } else {
                    AndroidUtil.showToast(response.getTips());
                    // mListView.onRefreshComplete();
                    // mListView.setMode(Mode.DISABLED);
                }
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
                AndroidUtil.showToast(R.string.reqFailed);
                mListView.onRefreshComplete();
                mPage--;
            }
        });
    }

}