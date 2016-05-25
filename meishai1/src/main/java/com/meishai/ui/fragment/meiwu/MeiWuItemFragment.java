package com.meishai.ui.fragment.meiwu;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.meishai.R;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.ui.base.BaseFragment;
import com.meishai.ui.fragment.meiwu.adapter.Cid12Adapter;
import com.meishai.ui.fragment.meiwu.adapter.CidAllAdapter;
import com.meishai.ui.fragment.meiwu.adapter.CidMinusAdapter;
import com.meishai.ui.fragment.meiwu.adapter.MeiwuAdapter;
import com.meishai.ui.fragment.meiwu.req.MeiWuReq;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;
import com.meishai.util.PullToRefreshHelper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 美物 - 所有分类
 * <p/>
 * 2016年1月20日09:41:34
 *
 * @author yl
 */
public class MeiWuItemFragment extends BaseFragment {
    private int currentPage = 1;
    private int pages;
    private View view;
    private PullToRefreshListView mListView;
    private MeiwuAdapter mAdapter;
    private boolean isLoading;
    private int mCid;
    private String mData;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //2：在onCreateView方法内复用RootView
        if (view == null) {
            view = inflater.inflate(R.layout.meiwu_listview, null);
            mCid = getArguments().getInt("cid", 0);
            currentPage = 1;
            this.initView();

            showProgress("", getString(R.string.network_wait));
            getRequestData(currentPage);
        }
        return view;
    }

    private void initView() {
        mListView = (PullToRefreshListView) view
                .findViewById(R.id.meiwu_listview);
        mListView.setMode(Mode.PULL_FROM_START);
        PullToRefreshHelper.initIndicatorStart(mListView);
        PullToRefreshHelper.initIndicator(mListView);

        // 写适配器
        mAdapter = getAdapter();
        mListView.setAdapter(mAdapter);
        mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                if (refreshView.getCurrentMode() == Mode.PULL_FROM_START) {
                    getRequestData(1);
                }

            }
        });
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (totalItemCount - (firstVisibleItem + visibleItemCount) == 1 && !isLoading) {
                    isLoading = true;
                    DebugLog.w("总数item:" + totalItemCount + "当前item:" + (firstVisibleItem + visibleItemCount));
                    if (currentPage < pages) {
                        getRequestData(currentPage + 1);
                    } else {
                        mListView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
//								AndroidUtil
//										.showToast(R.string.drop_down_list_footer_no_more_text);
                                setNetComplete();
                            }
                        }, 500);
                    }
                }
            }
        });
    }

    /**
     * 根据cid判断所需要的适配器
     *
     * @return
     */
    private MeiwuAdapter getAdapter() {
        if (mCid < 0) {
            return new CidMinusAdapter(getActivity(), getImageLoader());
        } else if (mCid == 12) {
            return new Cid12Adapter(getActivity(), getImageLoader());
        } else {
            return new CidAllAdapter(getActivity(), getImageLoader());
        }
//		switch (mCid){
//		case -1://专辑
//		case 12://每日一色
//		default:
//		}
    }

    protected void getRequestData(final int page) {

//			showProgress("", getString(R.string.network_wait));
        sendMsg(MeiWuReq.meiwuSubUrl(page, mCid));
    }

    @Override
    public void updateUI(Object obj) {
        if (obj == null || TextUtils.isEmpty(obj.toString())) return;
        String response = obj.toString();
        setNetComplete();
        if (!TextUtils.isEmpty(response)) {
            JSONObject obj1 = null;
            try {
                obj1 = new JSONObject(response);
                if (obj1.getInt("success") == 1) {
                    mData = response;
                    currentPage = obj1.getInt("page");
                    pages = obj1.getInt("pages");
                    mAdapter.setData(response);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void failt(Object ojb) {
        AndroidUtil.showToast(R.string.reqFailed);
        setNetComplete();
    }

    private void setNetComplete() {
        mListView.onRefreshComplete();
        hideProgress();
        isLoading = false;
    }

//	@Override
//	public void setUserVisibleHint(boolean isVisibleToUser) {
//		super.setUserVisibleHint(isVisibleToUser);
//		if (isVisibleToUser) {
//			if (mData == null) {
//				showProgress("", getString(R.string.network_wait));
//				getRequestData(currentPage);
//			} else {
//				mAdapter.setData(mData);
////				mAdapter.notifyDataSetChanged();
//			}
//		} else {
//			// 相当于Fragment的onPause
//		}
//	}


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //1：在onDestroyView方法内把Fragment的RootView从ViewPager中remove
        if (view != null && view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }
}
