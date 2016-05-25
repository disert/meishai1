package com.meishai.ui.fragment.meiwu;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.meishai.R;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.Master;
import com.meishai.net.RespData;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.ui.base.BaseFragment;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.meiwu.adapter.MasterAdapter;
import com.meishai.ui.fragment.meiwu.req.FindReq;
import com.meishai.util.AndroidUtil;
import com.meishai.util.GsonHelper;
import com.meishai.util.PullToRefreshHelper;

/**
 * 发现->达人 (废弃)
 *
 * @author sh
 */
public class FindMasterFragment extends BaseFragment implements OnClickListener {
    private int currentPage = 1;
    private View view;
    private Button mBtnBack;
    private Button mBtnApply;
    private List<Master> masters = null;
    private PullToRefreshListView mMastertList;
    private MasterAdapter masterAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.find_master, null);
        currentPage = 1;
        masters = new ArrayList<Master>();
        this.initView();
        this.loadData();
        return view;
    }

    private void initView() {
        mBtnBack = (Button) view.findViewById(R.id.backMain);
        mBtnBack.setOnClickListener(this);
        mBtnApply = (Button) view.findViewById(R.id.btn_apply);
        mBtnApply.setOnClickListener(this);
        mMastertList = (PullToRefreshListView) view
                .findViewById(R.id.master_list);
        PullToRefreshHelper.initIndicatorStart(mMastertList);
        PullToRefreshHelper.initIndicator(mMastertList);
        masterAdapter = new MasterAdapter(mContext, masters);
        // masterAdapter.setCompleteListener(new OnCompleteListener() {
        //
        // @Override
        // public void onComplete() {
        //
        // }
        // });
        mMastertList.setAdapter(masterAdapter);

        mMastertList.setOnRefreshListener(new OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(mContext,
                        System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
                                | DateUtils.FORMAT_SHOW_DATE
                                | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                currentPage = 1;
                masters.clear();
                loadData();
            }

            @Override
            public void onPullUpToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                currentPage++;
                loadData();
            }
        });
    }

    private void notifyMasterAdapter() {
        masterAdapter.setMasters(masters);
        masterAdapter.notifyDataSetChanged();
    }

    private void loadData() {
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        data.put("page", String.valueOf(currentPage));
        FindReq.daren(mContext, data, new Listener<RespData>() {

            @Override
            public void onResponse(RespData response) {
                if (response.isSuccess()) {
                    Type type = new TypeToken<List<Master>>() {
                    }.getType();
                    List<Master> resultMasters = GsonHelper.parseObject(
                            GsonHelper.toJson(response.getData()), type);

                    onRefreshComplete();
                    if (null != resultMasters && !resultMasters.isEmpty()) {
                        masters.addAll(resultMasters);
                        notifyMasterAdapter();
                    }
                } else {
                    AndroidUtil.showToast(response.getTips());
                }

            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                AndroidUtil.showToast(mContext.getString(R.string.reqFailed));
            }
        });
    }

    // 刷新完成
    private void onRefreshComplete() {
        mMastertList.onRefreshComplete();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backMain:
                backToMain();
                break;
            case R.id.btn_apply:
                Intent intent = new Intent(mContext,
                        FindMasterApplyWebviewActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    protected void backToMain() {
        doBackClick();
    }

    // 操作之后 回调
    public interface OnCompleteListener {
        public void onComplete();
    }
}
