package com.meishai.ui.fragment.meiwu;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.meishai.R;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.Bargains;
import com.meishai.entiy.SaleCate;
import com.meishai.net.RespData;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.ui.base.BaseActivity;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.meiwu.adapter.SaleGridAdapter;
import com.meishai.ui.fragment.meiwu.req.FindReq;
import com.meishai.util.AndroidUtil;
import com.meishai.util.GsonHelper;
import com.meishai.util.PullToRefreshHelper;

/**
 *  特价->分类->选择分类页面
 *
 * @author sh
 */
public class FindSaleCateSearchActivity extends BaseActivity implements
        OnClickListener {

    private Context mContext = FindSaleCateSearchActivity.this;
    // 搜索关键词
    private SaleCate cate = null;
    private Button mBtnBack;
    private TextView title;
    private int currentPage = 1;
    private List<Bargains> bargains = null;
    private PullToRefreshGridView gridView;
    private SaleGridAdapter saleGridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cate = getIntent().getParcelableExtra("cate");
        currentPage = 1;
        bargains = new ArrayList<Bargains>();
        this.setContentView(R.layout.find_sale_cate_search);
        this.initView();
        if (null != cate) {
            this.refreshUI();
            this.loadData();
        }
    }

    private void refreshUI() {
        title.setText(cate.getName());
    }

    private void initView() {
        mBtnBack = (Button) this.findViewById(R.id.backMain);
        mBtnBack.setOnClickListener(this);
        title = (TextView) this.findViewById(R.id.title);
        gridView = (PullToRefreshGridView) this.findViewById(R.id.sale_grid);
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
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        data.put("page", String.valueOf(currentPage));
        data.put("cid", String.valueOf(cate.getCid()));
        FindReq.tejia(mContext, data, new Listener<RespData>() {

            @Override
            public void onResponse(RespData response) {
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
                AndroidUtil.showToast(mContext.getString(R.string.reqFailed));
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backMain:
                finish();
                break;
            default:
                break;
        }
    }

}
