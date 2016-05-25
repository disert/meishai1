package com.meishai.ui.fragment.meiwu;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.meishai.R;
import com.meishai.entiy.SpecialListRespData;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.ui.base.BaseFragment;
import com.meishai.ui.fragment.meiwu.req.MeiWuReq;
import com.meishai.ui.fragment.usercenter.adapter.CollectedSpecialAdapter;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;
import com.meishai.util.GsonHelper;
import com.meishai.util.PullToRefreshHelper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 收藏_专场
 *
 * @author yl
 */
public class SearchSpecialFragment extends BaseFragment {
    private View view;
    private PullToRefreshListView mListView;
    private CollectedSpecialAdapter mAdapter;
    private SpecialListRespData mData;
    private String cacheKey = "SearchSpecialFragment";
    private String mKey = "";
    private boolean isInit = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.meiwu_listview, null);
        this.initView();
        getRequestData(mKey);
        return view;
    }

    private void initView() {

        mListView = (PullToRefreshListView) view
                .findViewById(R.id.meiwu_listview);
        mListView.setMode(Mode.BOTH);
        PullToRefreshHelper.initIndicatorStart(mListView);
        PullToRefreshHelper.initIndicator(mListView);

        // 写适配器
        mAdapter = new CollectedSpecialAdapter(getActivity(), getImageLoader());
        mListView.setAdapter(mAdapter);
        mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                if (refreshView.getCurrentMode() == Mode.PULL_FROM_START) {
                    getRequestData(mKey);
                } else if (refreshView.getCurrentMode() == Mode.PULL_FROM_END) {
                    if (mData == null) {
                        getRequestData(mKey);
                        return;
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
        isInit = true;
    }

    protected void getRequestData(final String key) {
        DebugLog.w("SearchSpecialFragment 的 getRequestData");
        if (TextUtils.isEmpty(key) || !isInit) {
            return;
        }
        MeiWuReq.search(key, 2, new Listener<String>() {

            @Override
            public void onResponse(String response) {


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
                SpecialListRespData resqData = GsonHelper.parseObject(response,
                        SpecialListRespData.class);
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

    private void setNetComplete() {
        mListView.onRefreshComplete();
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
        DebugLog.w("当前fragment:SearchSpecialFragment");
        if (!TextUtils.isEmpty(key) && !key.equals(mKey)) {
            mKey = key;
            getRequestData(mKey);
        }
    }


}
