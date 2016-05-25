package com.meishai.ui.fragment.home;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.meishai.R;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.TopicItem;
import com.meishai.entiy.UserInfo;
import com.meishai.net.ReqData;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.StringRequest;
import com.meishai.ui.base.BaseFragment;
import com.meishai.ui.fragment.home.adapter.TopicAdapter;
import com.meishai.ui.fragment.home.req.HomeSearchReq;
import com.meishai.util.AndroidUtil;
import com.meishai.util.GsonHelper;
import com.meishai.util.PullToRefreshHelper;
import com.nimbusds.jose.JOSEException;

/**
 * 晒晒搜索->话题
 *
 * @author sh
 */
public class HomeTopicSearchFragment extends BaseFragment {
    private View mRootView;
    private PullToRefreshListView mListView;
    private TopicAdapter mAdapter;
    // 搜索关键词
    private String keyword = "";
    private int mPage = 1;
    private OnTopicSearchListener listener;
    private List<TopicItem> topicItems = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (null == mRootView) {
            mRootView = inflater.inflate(R.layout.home_timeline, null);
            initView(mRootView);
        }
        topicItems = new ArrayList<TopicItem>();
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (null != parent) {
            parent.removeView(mRootView);
        }
        return mRootView;
    }

    public OnTopicSearchListener getListener() {
        if (null == listener) {
            listener = new OnTopicSearchListener() {

                @Override
                public void onTopicSearch(String kword) {
                    keyword = kword;
                    mPage = 1;
                    topicItems = new ArrayList<TopicItem>();
                    getRequestTopic();
                }
            };
        }
        return listener;
    }

    private void initView(View v) {
        mListView = (PullToRefreshListView) v
                .findViewById(R.id.home_timeline_listview);
        mListView.setMode(Mode.PULL_FROM_END);
        PullToRefreshHelper.initIndicator(mListView);
        mAdapter = new TopicAdapter(getActivity(), getImageLoader());
        mListView.setAdapter(mAdapter);
        mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
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
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        ReqData reqData = new ReqData();
        reqData.setC("post");
        reqData.setA("search");
        Map<String, String> dataList = new HashMap<String, String>();
        dataList.put("userid", userInfo.getUserID());
        // 话题搜索
        dataList.put("type", HomeSearchReq.SEARCH_TOPIC);
        dataList.put("keyword", keyword);
        dataList.put("page", String.valueOf(mPage));
        dataList.put("pagesize", String.valueOf(10));
        reqData.setData(dataList);
        try {
            String url = getString(R.string.base_url) + reqData.toReqString();
            getRequestQueue().add(
                    new StringRequest(url, new Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            hideProgress();
                            if (checkData(response)) {
                                mListView.onRefreshComplete();
                            } else {
                                mListView.onRefreshComplete();
                                showToast(R.string.drop_down_list_footer_no_more_text);
                                mListView.setMode(Mode.DISABLED);
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
                    }));

        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    private boolean checkData(String response) {
        try {
            JSONObject jsonObj = new JSONObject(response);
            if (jsonObj.getInt("success") == 1) {
                JSONArray array = jsonObj.getJSONArray("data");
                Type type = new TypeToken<Collection<TopicItem>>() {
                }.getType();
                Collection<TopicItem> items = GsonHelper.parseObject(
                        array.toString(), type);
                topicItems.addAll(items);
//				mAdapter.addCollection(items);
            } else {
                return false;
            }
        } catch (JSONException e) {
            mAdapter.setmList(topicItems);
            e.printStackTrace();
            return false;
        }
        mAdapter.setmList(topicItems);
        return true;
    }


    public interface OnTopicSearchListener {
        public void onTopicSearch(String kword);
    }
}
