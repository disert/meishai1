package com.meishai.ui.fragment.usercenter;

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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
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
import com.meishai.ui.fragment.home.PostShowActivity;
import com.meishai.util.AndroidUtil;
import com.meishai.util.GsonHelper;
import com.meishai.util.PullToRefreshHelper;
import com.nimbusds.jose.JOSEException;

/**
 * 我的->我的收藏->帖子
 *
 * @author sh
 */
public class UserFavPostFragment extends BaseFragment {

    private View mView;
    private PullToRefreshListView mListView;
    private LatestPostAdapter mPostListAdapter;
    private int mPage = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.home_channel_show_child, null);
        initView();
        getRequest();
        return mView;
    }

    private void initView() {
        mListView = (PullToRefreshListView) mView
                .findViewById(R.id.channel_show_listview);
        mListView.setMode(Mode.BOTH);
        PullToRefreshHelper.initIndicatorStart(mListView);
        PullToRefreshHelper.initIndicator(mListView);
        mPostListAdapter = new LatestPostAdapter(getActivity(),
                getImageLoader());
        mListView.setAdapter(mPostListAdapter);
        mListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                mPage = 1;
                mPostListAdapter.clear();
                getRequest();
            }

            @Override
            public void onPullUpToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                mPage++;
                getRequest();

            }
        });

        // mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
        //
        // @Override
        // public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        //
        // mPage++;
        // getRequest();
        // }
        // });
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                PostItem item = (PostItem) parent.getAdapter()
                        .getItem(position);
                startActivity(PostShowActivity.newIntent(item,
                        PostShowActivity.FROM_POST));
            }
        });
    }

    private ReqData mReqData;
    private Map<String, String> mMap = new HashMap<String, String>();

    private void getRequest() {
        if (null == mReqData) {
            mReqData = new ReqData();
            mReqData.setC("member");
            mReqData.setA("fav");
            UserInfo user = MeiShaiSP.getInstance().getUserInfo();
            mMap.put("userid", user.getUserID());
            mMap.put("type", "post");
        }
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
                mPostListAdapter.addCollection(items);
                return true;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
}
