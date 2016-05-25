package com.meishai.ui.fragment.meiwu;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.entiy.StratListRespData;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.ui.base.BaseActivity;
import com.meishai.ui.fragment.meiwu.adapter.MeiWuCateDetailAdapter;
import com.meishai.ui.fragment.meiwu.req.MeiWuReq;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;
import com.meishai.util.GsonHelper;
import com.meishai.util.PullToRefreshHelper;

/**
 * 美物-分类列表-每一类的列表 (攻略列表)2.0
 *
 * @author yl
 */
public class MeiWuCateDetailActivity extends BaseActivity {

    private int mCid;
    private int currentPage = 1;
    private TextView mTitle;
    private PullToRefreshListView mListView;
    private StratListRespData mData;
    private MeiWuCateDetailAdapter mAdapter;
    private boolean isLoading;
    private String mTitleText;

    public static Intent newIntent(int cid) {
        Intent intent = new Intent(GlobalContext.getInstance()
                .getApplicationContext(), MeiWuCateDetailActivity.class);
        intent.putExtra("cid", cid);
        return intent;
    }

    public static Intent newIntent(int cid, String title) {
        Intent intent = new Intent(GlobalContext.getInstance()
                .getApplicationContext(), MeiWuCateDetailActivity.class);
        intent.putExtra("cid", cid);
        intent.putExtra("title", title);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meiwu_cate_detail);
        mCid = getIntent().getIntExtra("cid", 0);

        this.initView();
        getRequestData(1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mData == null) {
            getRequestData(1);
        } else {
            mAdapter.setData(mData);
        }
    }

    private void initView() {

        findViewById(R.id.backMain).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTitle = (TextView) findViewById(R.id.title);
        String title = getIntent().getStringExtra("title");
        if (!TextUtils.isEmpty(title)) {
            mTitle.setText(title);
        }
        mListView = (PullToRefreshListView) findViewById(R.id.meiwu_cate_detail_listview);
        mListView.setMode(Mode.PULL_FROM_START);
        PullToRefreshHelper.initIndicatorStart(mListView);
        PullToRefreshHelper.initIndicator(mListView);
        mAdapter = new MeiWuCateDetailAdapter(this, getImageLoader());
        mListView.setAdapter(mAdapter);

        mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                if (refreshView.getCurrentMode() == Mode.PULL_FROM_START) {
                    getRequestData(1);
                } else if (refreshView.getCurrentMode() == Mode.PULL_FROM_END) {
                    if (currentPage < mData.pages) {
                        getRequestData(currentPage + 1);
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
//		mListView.setOnItemClickListener(new OnItemClickListener() {
        //
        //			@Override
        //			public void onItemClick(AdapterView<?> parent, View view,
        //									int position, long id) {
        //				// TODO 商品详情,现改为直接跳转到web页面
        //				if (!(mAdapter.getItem(position) instanceof Blurb)) {
        //					DebugLog.w("数据类型错乱");
        //					return;
        //				}
        //				Blurb item = (Blurb) mAdapter.getItem(position);
        //				Intent intent = MeiWuGoodsDetailActivity.newIntent(item.pid);
        //				startActivity(intent);
        //
        //			}
        //		});

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //倒数第二个的时候加载新数据
                if (totalItemCount - (firstVisibleItem + visibleItemCount) == 1 && !isLoading) {
                    isLoading = true;
                    DebugLog.w("总数item:" + totalItemCount + "当前item:" + (firstVisibleItem + visibleItemCount));
                    if (currentPage < mData.pages) {
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

    protected void getRequestData(int page) {
//		showProgress("", getString(R.string.network_wait));
        // 6
        MeiWuReq.strategyListReq(page, mCid, new Listener<String>() {

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

    protected void setNetComplete() {
        mListView.onRefreshComplete();
        hideProgress();
        isLoading = false;
    }

    protected void checkRespData(String response) {
        try {
            JSONObject obj = new JSONObject(response);
            int success = obj.getInt("success");
            if (success == 1) {
                DebugLog.d(response);
                StratListRespData resqData = GsonHelper.parseObject(response,
                        StratListRespData.class);
                if (TextUtils.isEmpty(mTitleText)) {
                    mTitleText = resqData.catname;
                    mTitle.setText(mTitleText);
                }
                if (resqData.page <= 1) {
                    mData = resqData;
                } else {
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

}
