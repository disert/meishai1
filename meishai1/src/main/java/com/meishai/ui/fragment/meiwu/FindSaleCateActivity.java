package com.meishai.ui.fragment.meiwu;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.meishai.R;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.SaleCate;
import com.meishai.net.RespData;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.ui.base.BaseActivity;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.meiwu.adapter.SaleCateAdapter;
import com.meishai.ui.fragment.meiwu.req.FindReq;
import com.meishai.util.AndroidUtil;
import com.meishai.util.GsonHelper;

/**
 *  特价->分类
 *
 * @author sh
 */
public class FindSaleCateActivity extends BaseActivity implements
        OnClickListener {

    private int REQUSE_CODE_CATE = 100;
    private Context mContext = FindSaleCateActivity.this;
    private int currentPage = 1;
    private Button mBtnBack;
    private PullToRefreshListView cate_list;
    private List<SaleCate> cates = null;
    private SaleCateAdapter cateAdapter;
    private View start_line;
    private View end_line;
    private EditText search_text;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_sale_catalog);
        currentPage = 1;
        cates = new ArrayList<SaleCate>();
        this.initView();
        this.loadData();
    }

    private void initView() {
        mBtnBack = (Button) findViewById(R.id.backMain);
        mBtnBack.setOnClickListener(this);
        start_line = findViewById(R.id.start_line);
        end_line = findViewById(R.id.end_line);
        search_text = (EditText) findViewById(R.id.search_text);
        search_text.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(mContext,
                        FindSaleSearchActivity.class);
                startActivity(intent);
                // setResult(REQUSE_CODE_CATE);
                // finish();
            }
        });
        cate_list = (PullToRefreshListView) findViewById(R.id.cate_list);
        initIndicator();
        cateAdapter = new SaleCateAdapter(mContext, cates);
        cate_list.setAdapter(cateAdapter);

        cate_list.setOnRefreshListener(new OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
            }

            @Override
            public void onPullUpToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                currentPage++;
                loadData();
            }
        });
        cate_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                SaleCate cate = (SaleCate) parent.getAdapter()
                        .getItem(position);
                Intent intent = new Intent(mContext,
                        FindSaleCateSearchActivity.class);
                intent.putExtra("cate", cate);
                startActivity(intent);
            }
        });

    }

    private void initIndicator() {
        ILoadingLayout endLabels = cate_list.getLoadingLayoutProxy(false, true);
        endLabels.setPullLabel(mContext
                .getString(R.string.refreshlist_footer_hint_normal));
        endLabels.setRefreshingLabel(mContext
                .getString(R.string.refreshlist_header_hint_loading));
        endLabels.setReleaseLabel(mContext
                .getString(R.string.refreshlist_header_hint_ready));
    }

    private void loadData() {
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        data.put("page", String.valueOf(currentPage));
        FindReq.catalog(mContext, data, new Listener<RespData>() {

            @Override
            public void onResponse(RespData response) {
                if (response.isSuccess()) {
                    Type type = new TypeToken<List<SaleCate>>() {
                    }.getType();
                    List<SaleCate> resultSaleCates = GsonHelper.parseObject(
                            GsonHelper.toJson(response.getData()), type);

                    onRefreshComplete();
                    if (null != resultSaleCates && !resultSaleCates.isEmpty()) {
                        cates.addAll(resultSaleCates);
                        notifySaleAdapter();
                    }
                } else {
                    AndroidUtil.showToast(response.getTips());
                }
                showStartAndEndLine();

            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                AndroidUtil.showToast(mContext.getString(R.string.reqFailed));
                showStartAndEndLine();
            }
        });
    }

    // 刷新完成
    private void onRefreshComplete() {
        cate_list.onRefreshComplete();
    }

    private void notifySaleAdapter() {
        cateAdapter.setCates(cates);
        cateAdapter.notifyDataSetChanged();
    }

    private void showStartAndEndLine() {
        if (null != cates && !cates.isEmpty()) {
            this.start_line.setVisibility(View.VISIBLE);
            this.end_line.setVisibility(View.VISIBLE);
        } else {
            this.start_line.setVisibility(View.GONE);
            this.end_line.setVisibility(View.GONE);
        }

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
