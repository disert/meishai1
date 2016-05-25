/**
 */
package com.meishai.ui.fragment.home;

import java.lang.reflect.Type;
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
import com.meishai.app.widget.layout.LatestPostAdapter.OnDelClickListener;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.HomepageUser;
import com.meishai.entiy.PostItem;
import com.meishai.entiy.SlideItem;
import com.meishai.entiy.UserInfo;
import com.meishai.net.ReqData;
import com.meishai.net.RespData;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.toolbox.StringRequest;
import com.meishai.net.volley.VolleyError;
import com.meishai.ui.base.BaseFragment;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.home.HomePageActivity.HomePageListener;
import com.meishai.ui.fragment.home.req.HomepageReq;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;
import com.meishai.util.GsonHelper;
import com.meishai.util.PullToRefreshHelper;
import com.nimbusds.jose.JOSEException;

/**
 * @author sh
 * @ClassName: HomePageHomeFragment
 * @Description: 个人主页->她的主页
 */
public class HomePageHomeFragment extends BaseFragment {

    private View mRootView;
    private HomePageListener homePageListener;
    private PullToRefreshListView mListView;
    private LatestPostAdapter mPostListAdapter;
    private int mPage = 1;
    private String uid = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uid = getArguments().getString("uid");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (null == mRootView) {
            mRootView = inflater.inflate(R.layout.home_latest_timeline,
                    container, false);
            initView(mRootView);
            getRequestIndexPost();
        }
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (null != parent) {
            parent.removeView(mRootView);
        }
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
        mPostListAdapter = new LatestPostAdapter(getActivity(), getImageLoader());

        mPostListAdapter.setOnDelListener(new OnDelClickListener() {

            @Override
            public void onDelClickListener(int position) {
                getRequestDelPost(position);
            }
        });

        mListView.setAdapter(mPostListAdapter);

        mListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                mPage = 1;
                mPostListAdapter.clear();
                getRequestIndexPost();
            }

            @Override
            public void onPullUpToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                mPage++;
                getRequestIndexPost();
            }
        });

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

    private void getRequestIndexPost() {
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        data.put("page", String.valueOf(mPage));
        data.put("uid", uid);
        data.put("pagesize", String.valueOf(3));
        HomepageReq.index(mContext, data, new Listener<RespData>() {

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
                    Type type = new TypeToken<List<PostItem>>() {
                    }.getType();
                    List<PostItem> resultUserPostItems = GsonHelper
                            .parseObject(GsonHelper.toJson(response.getData()),
                                    type);

                    mListView.onRefreshComplete();
                    if (null != resultUserPostItems
                            && !resultUserPostItems.isEmpty()) {
                        mPostListAdapter.addCollection(resultUserPostItems);
                    } else {
                        mListView.onRefreshComplete();
                        // AndroidUtil.showToast(R.string.drop_down_list_footer_no_more_text);
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


    private void getRequestDelPost(final int position) {
        PostItem postItem = mPostListAdapter.getItem(position);
        showProgress("", "正在删除...");

        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        ReqData reqData = new ReqData();
        reqData.setC("post");
        reqData.setA("del");
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", userInfo.getUserID());
        data.put("pid", String.valueOf(postItem.pid));
        reqData.setData(data);

        try {
            String url = getString(R.string.base_url) + reqData.toReqString();
            getRequestQueue().add(new StringRequest(url, new Listener<String>() {
                @Override
                public void onResponse(String response) {
                    hideProgress();
                    try {
                        JSONObject jsonObj = new JSONObject(response);
                        if (jsonObj.getInt("success") == 1) {
                            mPostListAdapter.delItem(position);

                        } else {
                            AndroidUtil.showToast("删除失败");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        AndroidUtil.showToast("删除失败");
                    }

                }
            }, new ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    DebugLog.e("net error:" + error.getMessage());
                    AndroidUtil.showToast(R.string.reqFailed);
                    hideProgress();
                }
            }));

        } catch (JOSEException e) {
            e.printStackTrace();
        }

    }
}