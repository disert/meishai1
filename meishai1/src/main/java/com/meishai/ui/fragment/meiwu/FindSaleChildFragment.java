package com.meishai.ui.fragment.meiwu;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.meishai.R;
import com.meishai.app.widget.CustomProgress;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.Bargains;
import com.meishai.net.RespData;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.ui.base.BaseFragment;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.meiwu.adapter.SaleGridAdapter;
import com.meishai.ui.fragment.meiwu.req.FindReq;
import com.meishai.util.AndroidUtil;
import com.meishai.util.GsonHelper;
import com.meishai.util.PullToRefreshHelper;

/**
 * 发现->特价(分类)
 *
 * @author sh
 */
@SuppressLint("ValidFragment")
public class FindSaleChildFragment extends BaseFragment {

    private int currentPage = 1;
    private View view;
    static final String ARG_TYPE = "type";
    private String type = null;
    private List<Bargains> bargains = null;
    private PullToRefreshGridView gridView;
    private SaleGridAdapter saleGridAdapter;

    private CustomProgress mCustomProgress;

    private FindSaleChildFragment() {

    }

    public static FindSaleChildFragment newInstance(String type) {
        FindSaleChildFragment f = new FindSaleChildFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getArguments().getString(ARG_TYPE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (view == null) {
            view = inflater.inflate(R.layout.find_sale_list, null);
            currentPage = 1;
            bargains = new ArrayList<Bargains>();
            this.initView();
            this.loadData();
        }

        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }


        return view;
    }

    private void initView() {
        gridView = (PullToRefreshGridView) view.findViewById(R.id.sale_grid);
        PullToRefreshHelper.initIndicator(gridView);
        saleGridAdapter = new SaleGridAdapter(mContext, bargains);
        gridView.setAdapter(saleGridAdapter);
        gridView.setOnRefreshListener(new OnRefreshListener2<GridView>() {

            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<GridView> refreshView) {
                String label = DateUtils.formatDateTime(mContext,
                        System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
                                | DateUtils.FORMAT_SHOW_DATE
                                | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

            }

            @Override
            public void onPullUpToRefresh(
                    PullToRefreshBase<GridView> refreshView) {
                currentPage++;
                loadData();
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Bargains bargains = (Bargains) parent.getAdapter().getItem(
                        position);
                Intent intent = new Intent(mContext,
                        FindSaleWebviewActivity.class);
                intent.putExtra("bargains", bargains);
                startActivity(intent);
            }
        });

    }

    private void notifySaleAdapter() {
        saleGridAdapter.setaBargains(bargains);
        saleGridAdapter.notifyDataSetChanged();
    }

    // 刷新完成
    private void onRefreshComplete() {
        gridView.onRefreshComplete();
    }

    private void loadData() {
        if (null == mCustomProgress) {
            mCustomProgress = CustomProgress.show(mContext,
                    mContext.getString(R.string.network_wait), true, null);
        } else {
            mCustomProgress.show();
        }

        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        data.put("page", String.valueOf(currentPage));
        data.put("type", this.type);
        FindReq.tejia(mContext, data, new Listener<RespData>() {

            @Override
            public void onResponse(RespData response) {
                if (null != mCustomProgress) {
                    mCustomProgress.dismiss();
                }
                if (response.isSuccess()) {
                    Type type = new TypeToken<List<Bargains>>() {
                    }.getType();
                    List<Bargains> resultBargains = GsonHelper.parseObject(
                            GsonHelper.toJson(response.getData()), type);

                    onRefreshComplete();
                    if (null != resultBargains && !resultBargains.isEmpty()) {
                        bargains.addAll(resultBargains);
                        notifySaleAdapter();
                    }
                } else {
                    AndroidUtil.showToast(response.getTips());
                }

            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (null != mCustomProgress) {
                    mCustomProgress.dismiss();
                }
                AndroidUtil.showToast(mContext.getString(R.string.reqFailed));
            }
        });
    }
}
