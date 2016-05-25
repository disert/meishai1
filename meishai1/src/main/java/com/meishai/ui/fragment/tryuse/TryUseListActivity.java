package com.meishai.ui.fragment.tryuse;

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

import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.TryInfo;
import com.meishai.net.RespData;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.ui.base.BaseActivity;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.tryuse.adapter.TryGridAdapter;
import com.meishai.ui.fragment.tryuse.req.TryReq;
import com.meishai.util.AndroidUtil;
import com.meishai.util.GsonHelper;
import com.meishai.util.PullToRefreshHelper;

/**
 * 试用->列表
 *
 * @author sh
 */
public class TryUseListActivity extends BaseActivity implements OnClickListener {
    private Context mContext = TryUseListActivity.this;
    private int currentPage = 1;
    private Button backMain;
    private List<TryInfo> infos = null;
    private PullToRefreshGridView gridView;
    private TryGridAdapter tryGridAdapter;
    private String cid = "";

    public static Intent newIntent(String cid) {
        Intent intent = new Intent(GlobalContext.getInstance(),
                TryUseListActivity.class);
        intent.putExtra("cid", cid);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tryuse_list);
        if (null != getIntent().getExtras()) {
            cid = getIntent().getExtras().getString("cid");
        }
        currentPage = 1;
        infos = new ArrayList<TryInfo>();
        this.initView();
        this.loadData();
    }

    private void initView() {
        backMain = (Button) findViewById(R.id.backMain);
        backMain.setOnClickListener(this);
        gridView = (PullToRefreshGridView) this.findViewById(R.id.try_grid);
        PullToRefreshHelper.initIndicator(gridView);
        tryGridAdapter = new TryGridAdapter(mContext, infos);
        gridView.setAdapter(tryGridAdapter);
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
                Intent intent = new Intent(mContext, TryuseDetailActivity.class);
                TryInfo info = (TryInfo) parent.getAdapter().getItem(position);
                intent.putExtra(ConstantSet.BUNDLE_ID, info.getSid());
                startActivity(intent);
            }
        });
    }

    private void notifySaleAdapter() {
        tryGridAdapter.setInfos(infos);
        tryGridAdapter.notifyDataSetChanged();
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
        data.put("cid", cid);
        TryReq.search(mContext, data, new Listener<RespData>() {

            @Override
            public void onResponse(RespData response) {
                if (response.isSuccess()) {
                    Type type = new TypeToken<List<TryInfo>>() {
                    }.getType();
                    List<TryInfo> resultInfos = GsonHelper.parseObject(
                            GsonHelper.toJson(response.getData()), type);

                    onRefreshComplete();
                    if (null != resultInfos && !resultInfos.isEmpty()) {
                        infos.addAll(resultInfos);
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
