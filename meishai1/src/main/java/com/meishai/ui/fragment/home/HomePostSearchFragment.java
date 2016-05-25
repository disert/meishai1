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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.meishai.R;
import com.meishai.app.widget.layout.LatestPostAdapter;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.PostItem;
import com.meishai.entiy.UserInfo;
import com.meishai.net.ReqData;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.StringRequest;
import com.meishai.ui.base.BaseFragment;
import com.meishai.ui.fragment.home.req.HomeSearchReq;
import com.meishai.util.AndroidUtil;
import com.meishai.util.GsonHelper;
import com.meishai.util.PullToRefreshHelper;
import com.nimbusds.jose.JOSEException;

/**
 * 晒晒搜索->帖子
 *
 * @author sh
 */
public class HomePostSearchFragment extends BaseFragment {
    private View mView;
    private PullToRefreshListView mListView;
    private LatestPostAdapter mPostListAdapter;
    // 搜索关键词
    private String keyword = "";
    private int mPage = 1;
    private OnPostSearchListener listener;
    private List<PostItem> postItems = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.home_channel_show_child, null);
        postItems = new ArrayList<PostItem>();
        initView();
        return mView;
    }

    public OnPostSearchListener getListener() {
        if (null == listener) {
            listener = new OnPostSearchListener() {
                @Override
                public void onPostSearch(String kword) {
                    keyword = kword;
                    mPage = 1;
                    postItems = new ArrayList<PostItem>();
                    getRequest();
                }
            };
        }
        return listener;
    }

    private void initView() {
        mListView = (PullToRefreshListView) mView
                .findViewById(R.id.channel_show_listview);
        mListView.setMode(Mode.PULL_FROM_END);
        PullToRefreshHelper.initIndicator(mListView);
        mPostListAdapter = new LatestPostAdapter(getActivity(),
                getImageLoader());
        mListView.setAdapter(mPostListAdapter);
        mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                mPage++;
                getRequest();
            }
        });

    }

    private void getRequest() {
        Map<String, String> mMap = new HashMap<String, String>();
        ReqData mReqData = new ReqData();
        mReqData.setC("post");
        mReqData.setA("search");
        UserInfo user = MeiShaiSP.getInstance().getUserInfo();
        mMap.put("userid", user.getUserID());
        // 帖子搜索
        mMap.put("type", HomeSearchReq.SEARCH_POST);
        mMap.put("pagesize", String.valueOf(10));
        mMap.put("keyword", keyword);
        mMap.put("page", String.valueOf(mPage));
        mReqData.setData(mMap);
        try {
            String url = getString(R.string.base_url) + mReqData.toReqString();
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
                            mPage--;
                            mListView.onRefreshComplete();
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
                JSONArray jsonArray = jsonObj.getJSONArray("data");

                Type type = new TypeToken<Collection<PostItem>>() {
                }.getType();
                Collection<PostItem> items = GsonHelper.parseObject(
                        jsonArray.toString(), type);
                postItems.addAll(items);
//				return true;
            } else {
                return false;
            }
        } catch (JSONException e) {
            mPostListAdapter.setmList(postItems);
            e.printStackTrace();
            return false;
        }
        mPostListAdapter.setmList(postItems);
        return true;
    }

    public interface OnPostSearchListener {
        public void onPostSearch(String kword);
    }
}
