package com.meishai.ui.fragment.tryuse;

import org.json.JSONException;
import org.json.JSONObject;

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
import com.meishai.entiy.FreeTrialRespData;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.ui.base.BaseFragment;
import com.meishai.ui.fragment.tryuse.adapter.FreeTrialAdapter;
import com.meishai.ui.fragment.tryuse.req.FuLiSheReq;
import com.meishai.util.AndroidUtil;
import com.meishai.util.CacheConfigUtils;
import com.meishai.util.DebugLog;
import com.meishai.util.GsonHelper;
import com.meishai.util.PullToRefreshHelper;

/**
 * 福利社 - 免费试用2.0
 *
 * @author yl
 */
public class FreeTrialFragment extends BaseFragment {
    private View view;
    private PullToRefreshListView mListView;
    private FreeTrialRespData mResqData;
    private FreeTrialAdapter mAdapter;
    private int currentPage = 1;
    private String cacheKey = "FreeTrialFragment";
    private boolean isLoading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.meiwu_listview, null);
        this.initView();
        return view;
    }

    private void initView() {

        view.findViewById(R.id.lay_top).setVisibility(View.VISIBLE);

        mListView = (PullToRefreshListView) view
                .findViewById(R.id.meiwu_listview);
        mListView.setMode(Mode.PULL_FROM_START);
        PullToRefreshHelper.initIndicatorStart(mListView);
        PullToRefreshHelper.initIndicator(mListView);

        // 写适配器
        mAdapter = new FreeTrialAdapter(mContext, getImageLoader());
        mListView.setAdapter(mAdapter);
        mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {

                if (refreshView.getCurrentMode() == Mode.PULL_FROM_START) {
                    getRequestData(1);
                } else if (refreshView.getCurrentMode() == Mode.PULL_FROM_END) {
                    if (mResqData == null) {
                        getRequestData(1);
                        return;
                    }
                    if (currentPage < mResqData.pages) {
                        getRequestData(++currentPage);
                    } else {
                        mListView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                AndroidUtil
                                        .showToast(R.string.drop_down_list_footer_no_more_text);
                                setNetComplete();
                            }
                        }, 500);
                    }
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
//					DebugLog.w("总数item:" + totalItemCount + "当前item:" + (firstVisibleItem + visibleItemCount));
                    if (currentPage < mResqData.pages) {
                        getRequestData(++currentPage);
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

    private void getRequestData(final int page) {
//		showProgress("", getString(R.string.network_wait));
        sendMsg(FuLiSheReq.freeTrialUrl(page));
        FuLiSheReq.freeTrial(page, new Listener<String>() {

            @Override
            public void onResponse(String response) {


            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    @Override
    public void failt(Object ojb) {
        AndroidUtil.showToast(R.string.reqFailed);

        //拉取数据失败,就使用缓存的数据
        String cache = CacheConfigUtils.getCache(cacheKey + "##" + currentPage);
        if (!TextUtils.isEmpty(cache)) {
            checkRespData(cache);
        }

        setNetComplete();
    }

    @Override
    public void updateUI(Object obj) {
        if (obj == null || TextUtils.isEmpty(obj.toString())) return;
        String response = obj.toString();
        //缓存数据
        CacheConfigUtils.putCache(cacheKey + "##" + currentPage, response);

        checkRespData(response);
        setNetComplete();
    }

    protected void checkRespData(String response) {
        try {
            JSONObject obj = new JSONObject(response);
            int success = obj.getInt("success");
            if (success == 1) {
                FreeTrialRespData resqData = GsonHelper
                        .parseObject(response, FreeTrialRespData.class);
                if (resqData.page == 1) {
                    mResqData = resqData;

                } else {
                    mResqData.list.addAll(resqData.list);
                    // mAdapter.addData(datas)
                    mResqData.page = resqData.page;
                }
                currentPage = mResqData.page;
                mAdapter.setData(mResqData);
                mAdapter.notifyDataSetChanged();
            } else {
                AndroidUtil.showToast(R.string.reqFailed);
            }

        } catch (JSONException e) {
            DebugLog.e(e.getMessage());
            e.printStackTrace();
        }
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
//			if(mResqData == null){
//				this.getRequestData(1);
//			}else{
//				mAdapter.setData(mResqData);
//			}
//		} else {
//			// 相当于Fragment的onPause
//		}
//	}

    @Override
    public void onResume() {
        if (mResqData == null) {
            showProgress("", getString(R.string.network_wait));
            this.getRequestData(1);
        } else {
            mAdapter.setData(mResqData);
        }
        super.onResume();
    }
}
