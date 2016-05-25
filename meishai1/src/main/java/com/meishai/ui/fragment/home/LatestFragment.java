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
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.toolbox.StringRequest;
import com.meishai.ui.base.BaseFragment;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;
import com.meishai.util.GsonHelper;
import com.meishai.util.PullToRefreshHelper;
import com.nimbusds.jose.JOSEException;


/**
 * @ClassName: NewestFragment
 * @Description: 晒晒->最新
 */
@Deprecated
public class LatestFragment extends BaseFragment {

    private View mRootView;

    private PullToRefreshListView mListView;
    private LatestPostAdapter mPostListAdapter;
    private int mPage = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (null == mRootView) {
            mRootView = inflater.inflate(R.layout.home_latest_timeline, container, false);
            initView(mRootView);

            getRequestLatestPost(mPage);
        }


        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (null != parent) {
            parent.removeView(mRootView);
        }

        return mRootView;
    }

    private void initView(View v) {
        mListView = (PullToRefreshListView) v.findViewById(R.id.home_timeline_listview);
        mListView.setMode(Mode.BOTH);
        PullToRefreshHelper.initIndicatorStart(mListView);
        PullToRefreshHelper.initIndicator(mListView);
        mPostListAdapter = new LatestPostAdapter(getActivity(), getImageLoader());
        mListView.setAdapter(mPostListAdapter);


        mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                if (refreshView.getCurrentMode() == Mode.PULL_FROM_START) {
                    getRequestLatestPost(1);

                } else if (refreshView.getCurrentMode() == Mode.PULL_FROM_END) {
                    getRequestLatestPost(mPage + 1);

                }
            }
        });

    }


    private void getRequestLatestPost(final int page) {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        ReqData reqData = new ReqData();
        reqData.setC("post");
        reqData.setA("index");
        Map<String, String> dataList = new HashMap<String, String>();
        dataList.put("userid", userInfo.getUserID());
        dataList.put("listorder", String.valueOf(0));
        dataList.put("page", String.valueOf(page));
        dataList.put("pagesize", String.valueOf(3));
        reqData.setData(dataList);

        try {
            String url = getString(R.string.base_url) + reqData.toReqString();
            DebugLog.d(url);
            getRequestQueue().add(new StringRequest(url, new Listener<String>() {
                @Override
                public void onResponse(String response) {
                    hideProgress();
                    //DebugLog.d(response);

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
                JSONArray jsonArray = jsonObj.getJSONArray("data");

                Type type = new TypeToken<Collection<PostItem>>() {
                }.getType();
                Collection<PostItem> items = GsonHelper.parseObject(jsonArray.toString(), type);

                //DebugLog.d("add PostItem:" + items.size());
                if (1 == page) {
                    mPostListAdapter.clear();
                }
                mPostListAdapter.addCollection(items);
                mPage = page;


                return true;
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        //DebugLog.d(response);

        return false;
    }


    @Override
    public void failt(Object ojb) {

    }

    @Override
    public void updateUI(Object obj) {

    }
}