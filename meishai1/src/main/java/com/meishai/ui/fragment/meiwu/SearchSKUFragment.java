package com.meishai.ui.fragment.meiwu;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.meishai.R;
import com.meishai.entiy.SKUResqData;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.ui.base.BaseFragment;
import com.meishai.ui.fragment.meiwu.adapter.MeiWuSKUAdapter;
import com.meishai.ui.fragment.meiwu.req.MeiWuReq;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;
import com.meishai.util.GsonHelper;
import com.meishai.util.PullToRefreshHelper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 美物 - 单品 2.0
 *
 * @author yl
 */
public class SearchSKUFragment extends BaseFragment {
    private View view;
    private PullToRefreshGridView mRefrashGridView;
    private SKUResqData mData;
    private MeiWuSKUAdapter mAdapter;
    private String cacheKey = "SearchSKUFragment";
    private String mKey = "";
    private boolean isInit = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.meiwu_sku, null);
        this.initView();
        this.getRequestData(mKey);
        return view;
    }

    private void initView() {
        mRefrashGridView = (PullToRefreshGridView) view
                .findViewById(R.id.meiwu_gridview);
        mRefrashGridView.setMode(Mode.PULL_FROM_START);
        GridView gridview = mRefrashGridView.getRefreshableView();
//		gridview.setNumColumns(2);
        PullToRefreshHelper.initIndicatorStart(mRefrashGridView);
        PullToRefreshHelper.initIndicator(mRefrashGridView);
        mAdapter = new MeiWuSKUAdapter(mContext, getImageLoader());
        mRefrashGridView.setAdapter(mAdapter);

        mRefrashGridView.setOnRefreshListener(new OnRefreshListener<GridView>() {

            @Override
            public void onRefresh(
                    PullToRefreshBase<GridView> refreshView) {
                if (refreshView.getCurrentMode() == Mode.PULL_FROM_START) {
                    getRequestData(mKey);
                } else if (refreshView.getCurrentMode() == Mode.PULL_FROM_END) {
                    if (mData == null) {
                        getRequestData(mKey);
                        setNetComplete();
                        return;
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
        isInit = true;
//		mRefrashGridView.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				// TODO 商品详情
//				Blurb item = (Blurb) mAdapter.getItem(position);
//				buyReq(item);
//				Intent intent = MeiWuGoodsDetailActivity.newIntent(item.pid);
//				startActivity(intent);
//
//			}
//		});
    }

    private void getRequestData(final String key) {
        DebugLog.w("SearchSKUFragment 的 getRequestData");
        if (TextUtils.isEmpty(key) || !isInit) {
            return;
        }
        MeiWuReq.search(key, 3, new Listener<String>() {

            @Override
            public void onResponse(String response) {
                //处理数据
                checkRespData(response);
                setNetComplete();
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                AndroidUtil.showToast(R.string.reqFailed);
                setNetComplete();
            }
        });
    }

    protected void checkRespData(String response) {
        try {
            JSONObject obj = new JSONObject(response);
            int success = obj.getInt("success");
            if (success == 1) {
                SKUResqData resqData = GsonHelper.parseObject(response,
                        SKUResqData.class);
                if (resqData == null) {
                    return;
                }
                mData = resqData;

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

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getRequestData(mKey);
        } else {
            // 相当于Fragment的onPause
        }
    }

    /**
     * 若当前该fragment是显示的,并且关键字发生了变化,那么该方法会被调用
     *
     * @param key 改变后的关键字
     */
    public void setKey(String key) {
        DebugLog.w("当前fragment:SearchSKUFragment");
        if (!TextUtils.isEmpty(key) && !key.equals(mKey)) {
            mKey = key;
            getRequestData(mKey);
        }
    }

}
