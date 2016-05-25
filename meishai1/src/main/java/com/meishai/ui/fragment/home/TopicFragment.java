/**
 */
package com.meishai.ui.fragment.home;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
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
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;
import com.meishai.util.GsonHelper;
import com.meishai.util.PullToRefreshHelper;
import com.nimbusds.jose.JOSEException;

/**
 * @ClassName: TopicFragment
 * @Description: 晒晒->话题
 */
@Deprecated
public class TopicFragment extends BaseFragment {

    public final static int TYPE_HOT = 1;
    public final static int TYPE_ATTENTION = 2;
    public final static int TYPE_LATEST = 3;

    private View mRootView;

    private PullToRefreshListView mListView;
    private TopicAdapter mAdapter;
    private int mPage = 1;

    private int mType = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (null == mRootView) {
            mRootView = inflater.inflate(R.layout.home_timeline, null);
            initData();
            initView(mRootView);

            getRequestTopic(mPage);
        }

        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (null != parent) {
            parent.removeView(mRootView);
        }

        return mRootView;
    }

    private void initData() {
        mType = getArguments().getInt("type");
    }

    private void initView(View v) {
        mListView = (PullToRefreshListView) v.findViewById(R.id.home_timeline_listview);
        mListView.setMode(Mode.BOTH);
        PullToRefreshHelper.initIndicatorStart(mListView);
        PullToRefreshHelper.initIndicator(mListView);
        mAdapter = new TopicAdapter(getActivity(), getImageLoader());
        mListView.setAdapter(mAdapter);

        mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                if (refreshView.getCurrentMode() == Mode.PULL_FROM_START) {
                    getRequestTopic(1);

                } else if (refreshView.getCurrentMode() == Mode.PULL_FROM_END) {
                    getRequestTopic(mPage + 1);
                }

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

    private void getRequestTopic(final int page) {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();

        ReqData reqData = new ReqData();
        reqData.setC("post");
        reqData.setA("topic");
        Map<String, String> dataList = new HashMap<String, String>();
        dataList.put("userid", userInfo.getUserID());
        dataList.put("type", String.valueOf(mType)); // 热门
        dataList.put("page", String.valueOf(page));
        dataList.put("pagesize", String.valueOf(10));
        reqData.setData(dataList);

        try {
            String url = getString(R.string.base_url) + reqData.toReqString();
            DebugLog.d(url);
            getRequestQueue().add(
                    new StringRequest(url, new Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            hideProgress();
                            // DebugLog.d(response);
                            if (checkData(response, page)) {
                                mListView.onRefreshComplete();

                            } else {

                                mListView.onRefreshComplete();

                                showToast(R.string.drop_down_list_footer_no_more_text);
                                //mListView.setMode(Mode.DISABLED);
                            }

                        }
                    }, new ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            hideProgress();
                            AndroidUtil.showToast(R.string.reqFailed);

                            mListView.onRefreshComplete();
                        }
                    }));

        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    private boolean checkData(String response, int page) {
        try {
            JSONObject jsonObj = new JSONObject(response);
            if (jsonObj.getInt("success") == 1) {
                JSONArray array = jsonObj.getJSONArray("data");

                Type type = new TypeToken<Collection<TopicItem>>() {
                }.getType();
                Collection<TopicItem> items = GsonHelper.parseObject(
                        array.toString(), type);

                if (page == 1) {
                    mAdapter.clear();
                }

                mAdapter.addCollection(items);
                mPage = page;

                return true;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

}