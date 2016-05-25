package com.meishai.ui.fragment.home;

import java.lang.reflect.Type;
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
import com.meishai.entiy.CatalogInfo;
import com.meishai.entiy.PostItem;
import com.meishai.entiy.UserInfo;
import com.meishai.net.ReqData;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.StringRequest;
import com.meishai.ui.base.BaseFragment;
import com.meishai.ui.fragment.home.ChannelShowFragmentActivity.OnReqSuccessListener;
import com.meishai.util.AndroidUtil;
import com.meishai.util.GsonHelper;
import com.meishai.util.PullToRefreshHelper;
import com.nimbusds.jose.JOSEException;

/**
 * @ClassName: ChannelActivity
 * @Description: 晒晒->精选->频道内容
 */
public class ChannelShowChildFragment extends BaseFragment {

    private View mView;
    private PullToRefreshListView mListView;
    private LatestPostAdapter mPostListAdapter;
    private OnReqSuccessListener listener;
    private int mId;
    private int mPage = 1;
    // 1：最新发布；2：最新回复；3：精彩
    private int listorder = 1;

//	private ViewScrollListener mScrollListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mId = getArguments().getInt("id", 0);
        listorder = getArguments().getInt("listorder", 1);
    }

    public void setListener(OnReqSuccessListener listener) {
        this.listener = listener;
    }

//	public void setOnScrollListener(ViewScrollListener l) {
//		mScrollListener = l;
//	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.home_channel_show_child, null);
            initView();
            getRequest(mPage);
        }

        ViewGroup parent = (ViewGroup) mView.getParent();
        if (null != parent) {
            parent.removeView(mView);
        }

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

        mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {

                if (refreshView.getCurrentMode() == Mode.PULL_FROM_START) {
                    getRequest(1);

                } else if (refreshView.getCurrentMode() == Mode.PULL_FROM_END) {
                    getRequest(mPage + 1);
                }
            }
        });

//		final GestureDetector gestureListener = new GestureDetector(mScrollListener);
//		mListView.getRefreshableView().setOnTouchListener(new OnTouchListener() {
//			
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				return gestureListener.onTouchEvent(event);
//			}
//		});
    }

    private ReqData mReqData;
    private Map<String, String> mMap = new HashMap<String, String>();

    private void getRequest(final int page) {
        if (null == mReqData) {
            mReqData = new ReqData();
            mReqData.setC("post");
            mReqData.setA("lists");
            UserInfo user = MeiShaiSP.getInstance().getUserInfo();
            mMap.put("userid", user.getUserID());
            mMap.put("cid", String.valueOf(mId));
            mMap.put("listorder", String.valueOf(listorder));
            mMap.put("pagesize", String.valueOf(10));
        }
        mMap.put("page", String.valueOf(page));
        mReqData.setData(mMap);
        try {
            String url = getString(R.string.base_url) + mReqData.toReqString();
            getRequestQueue().add(
                    new StringRequest(url, new Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            hideProgress();
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

                if (page == 1) {
                    mPostListAdapter.clear();
                }

                mPostListAdapter.addCollection(items);
                mPage = page;

                // 分类数据
                JSONArray catalogArray = jsonObj.getJSONArray("catalog");
                Type catalogType = new TypeToken<Collection<CatalogInfo>>() {
                }.getType();
                List<CatalogInfo> catalogInfos = GsonHelper.parseObject(catalogArray.toString(), catalogType);
                if (null != listener) {
                    listener.onSuccess(catalogInfos.get(0));
                }

                return true;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
}
