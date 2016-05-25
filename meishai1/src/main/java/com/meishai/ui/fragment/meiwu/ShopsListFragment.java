package com.meishai.ui.fragment.meiwu;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.meishai.R;
import com.meishai.app.widget.dragtop.AttachUtil;
import com.meishai.entiy.SKUResqData;
import com.meishai.entiy.ShopsDetailResqData;
import com.meishai.net.volley.Response;
import com.meishai.net.volley.VolleyError;
import com.meishai.ui.base.BaseFragment;
import com.meishai.ui.fragment.MeishaiWebviewActivity;
import com.meishai.ui.fragment.meiwu.adapter.MeiWuSKUAdapter;
import com.meishai.ui.fragment.meiwu.req.MeiWuReq;
import com.meishai.util.AndroidUtil;
import com.meishai.util.CacheConfigUtils;
import com.meishai.util.DebugLog;
import com.meishai.util.GsonHelper;
import com.meishai.util.PullToRefreshHelper;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * 文件名：
 * 描    述：
 * 作    者：yl
 * 时    间：2015/12/5
 * 版    权：
 */
public class ShopsListFragment extends BaseFragment {
    private int currentPage = 1;
    private View view;
    private PullToRefreshGridView mRefrashGridView;
    private ShopsDetailResqData mData;
    private MeiWuSKUAdapter mAdapter;
    private String cacheKey = "MeiWuSKUFragment";
    private int mTid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTid = getArguments().getInt("tid", 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.meiwu_sku, null);
        this.initView();
        currentPage = 1;
        // this.getRequestData(1);
        return view;
    }

    private void initView() {
        mRefrashGridView = (PullToRefreshGridView) view
                .findViewById(R.id.meiwu_gridview);
        mRefrashGridView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        //请求父组件不要拦截我的触摸事件
        if (mRefrashGridView.getParent() != null) {
            mRefrashGridView.getParent().requestDisallowInterceptTouchEvent(true);
        }
        GridView gridview = mRefrashGridView.getRefreshableView();
        //		gridview.setNumColumns(2);
        PullToRefreshHelper.initIndicatorStart(mRefrashGridView);
        PullToRefreshHelper.initIndicator(mRefrashGridView);
        mAdapter = new MeiWuSKUAdapter(mContext, getImageLoader());
        mRefrashGridView.setAdapter(mAdapter);

        mRefrashGridView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<GridView>() {

            @Override
            public void onRefresh(
                    PullToRefreshBase<GridView> refreshView) {
                if (refreshView.getCurrentMode() == PullToRefreshBase.Mode.PULL_FROM_START) {
                    getRequestData(1);
                } else if (refreshView.getCurrentMode() == PullToRefreshBase.Mode.PULL_FROM_END) {
                    if (mData == null) {
                        getRequestData(1);
                        return;
                    }
                    if (currentPage < mData.pages) {
                        getRequestData(currentPage + 1);
                    } else {
                        mRefrashGridView.postDelayed(new Runnable() {
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

        mRefrashGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                EventBus.getDefault().post(AttachUtil.isAdapterViewAttach(view));
            }
        });
    }


    private void getRequestData(final int page) {
        showProgress("", getString(R.string.network_wait));
        MeiWuReq.shopsDetailsReq(page, mTid, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //缓存数据
                CacheConfigUtils.putCache(cacheKey + "##" + page, response);
                //处理数据
                checkRespData(response);
                setNetComplete();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                AndroidUtil.showToast(R.string.reqFailed);
                //拉取数据失败,就使用缓存的数据
                String cache = CacheConfigUtils.getCache(cacheKey + "##" + page);
                if (!TextUtils.isEmpty(cache)) {
                    checkRespData(cache);
                }
                setNetComplete();
            }
        });
    }

    protected void checkRespData(String response) {
        try {
            JSONObject obj = new JSONObject(response);
            int success = obj.getInt("success");
            if (success == 1) {
                ShopsDetailResqData resqData = GsonHelper.parseObject(response,
                        ShopsDetailResqData.class);
                if (resqData == null) {
                    return;
                }
                //请求成功后,要把数据回传给activity,只有第一页的时候传就行了
                if (resqData.page == 1) {
                    if (mDataRefreshListener != null) {
                        mDataRefreshListener.dataChanged(resqData);
                    }
                    mData = resqData;
                } else if (resqData.page > 1) {
                    mData.list.addAll(resqData.list);
                    mData.page = resqData.page;
                }
                currentPage = mData.page;
                mAdapter.setData(mData);
            } else {
                AndroidUtil.showToast(R.string.reqFailed);

            }

        } catch (JSONException e) {
            DebugLog.e(e.getMessage());
            e.printStackTrace();
        }
    }

    protected void setNetComplete() {
        mRefrashGridView.setVisibility(View.VISIBLE);
        mRefrashGridView.onRefreshComplete();
        hideProgress();

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (mData == null) {
                getRequestData(currentPage);
            } else {
                //				AndroidUtil.showToast("SKU被调用:" + isVisibleToUser);
                //				mAdapter.notifyDataSetChanged();
                mAdapter.setData(mData);
                mRefrashGridView.setAdapter(mAdapter);
            }
        } else {
            // 相当于Fragment的onPause
        }
    }

    protected void buyReq(final SKUResqData.Blurb item) {
        // 发送购买的请求.在请求中有URL直接跳转到淘宝
        MeiWuReq.buy(item.pid, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    int success = json.getInt("success");
                    String tips = json.getString("tips");
                    if (success == 1) {
                        if (!TextUtils.isEmpty(tips)) {
                            AndroidUtil.showToast(tips);
                        }
                        Intent intent = MeishaiWebviewActivity.newIntent(item.itemurl);
                        mContext.startActivity(intent);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                AndroidUtil.showToast("购物请求发送失败");
                error.printStackTrace();
            }
        });

    }

    private DataRefreshListener mDataRefreshListener;

    public void setDataRefreshListener(DataRefreshListener dataRefreshListener) {
        mDataRefreshListener = dataRefreshListener;
    }

    /**
     * 监听数据刷新改变的接口
     */
    public interface DataRefreshListener {
        void dataChanged(ShopsDetailResqData data);
    }

}
