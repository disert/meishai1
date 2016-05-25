package com.meishai.ui.fragment.home;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.app.util.StringUtil;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.CateInfo;
import com.meishai.entiy.HomePageDatas;
import com.meishai.entiy.HomepageUser;
import com.meishai.entiy.ImageData;
import com.meishai.entiy.ShareData;
import com.meishai.entiy.UserInfo;
import com.meishai.net.ReqData;
import com.meishai.net.VolleyHelper;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.StringRequest;
import com.meishai.ui.base.BaseActivity;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.camera.BitmapUtils;
import com.meishai.ui.fragment.home.adapter.HomePageAdapter;
import com.meishai.ui.popup.SharePopupWindow;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;
import com.meishai.util.GsonHelper;
import com.meishai.util.PullToRefreshHelper;
import com.nimbusds.jose.JOSEException;

/**
 * 晒晒->个人主页 2.0
 *
 * @author yl
 */
public class HomePageActivity2 extends BaseActivity {

    private Context mContext = HomePageActivity2.this;
    private String uid = "";

    private int mPage = 1;

    private ImageLoader imageLoader = null;

    // 标题栏部分
    private TextView title;
    private Button backMain;
    private ImageView ibShare;

    private SharePopupWindow share;

    // 帖子list
    private PullToRefreshListView pullRefreshListView;
    private ListView listView;
    // 数据
    private HomePageDatas pageDatas;

    private HomePageAdapter adapter;
    private LinearLayout lay_main;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ConstantSet.POST_DELETE.equals(action)) {//TODO 删除帖子的广播
                //				getRequestData(mPage);
                int pid = intent.getIntExtra("pid", 0);
                adapter.delete(pid);
            } else if (ConstantSet.POST_MOD.equals(action)) {//修改帖子的广播
                getRequestData(mPage);
            }
        }
    };
    private boolean isLoading;

    public static Intent newIntent(String userid) {
        Intent intent = new Intent(GlobalContext.getInstance(),
                HomePageActivity2.class);
        intent.putExtra(ConstantSet.USERID, userid);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage1);
        imageLoader = VolleyHelper.getImageLoader(mContext);
        uid = getIntent().getExtras().getString(ConstantSet.USERID);
        if (StringUtil.isBlank(uid)) {
            uid = "";
        }
        this.initView();
        this.initListener();
        showProgress("", mContext.getString(R.string.network_wait));
        this.getRequestData(1);
        // 注册一个数据被删除修改的广播
        registerReceiver();
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConstantSet.POST_DELETE);
        filter.addAction(ConstantSet.POST_MOD);
        registerReceiver(mBroadcastReceiver, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (pageDatas == null) {
            getRequestData(mPage);
        } else {
            adapter.notifyDataSetChanged();
        }

    }

    private void initListener() {
        backMain.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ibShare.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 1.个人主页的分享事件
                if (null != share) {
                    share.showAtLocation(lay_main, Gravity.BOTTOM
                            | Gravity.CENTER_HORIZONTAL, 0, 0);
                }
            }
        });
    }

    private void initView() {

        lay_main = (LinearLayout) findViewById(R.id.root);
        // 标题栏部分
        backMain = (Button) this.findViewById(R.id.backMain);
        title = (TextView) findViewById(R.id.title);
        ibShare = (ImageView) findViewById(R.id.share);

        // 帖子list
        pullRefreshListView = (PullToRefreshListView) this
                .findViewById(R.id.refresh_gv);
        pullRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        PullToRefreshHelper.initIndicatorStart(pullRefreshListView);
        PullToRefreshHelper.initIndicator(pullRefreshListView);
        listView = pullRefreshListView.getRefreshableView();
        adapter = new HomePageAdapter(mContext, imageLoader);
        listView.setAdapter(adapter);

        // Set a listener to be invoked when the list should be refreshed.
        pullRefreshListView
                .setOnRefreshListener(new OnRefreshListener2<ListView>() {

                    @Override
                    public void onPullDownToRefresh(
                            PullToRefreshBase<ListView> refreshView) {
                        getRequestData(1);
                    }

                    @Override
                    public void onPullUpToRefresh(
                            PullToRefreshBase<ListView> refreshView) {
                        if (pageDatas == null) {
                            getRequestData(1);
                            return;
                        }
                        if (mPage < pageDatas.pages) {
                            getRequestData(mPage + 1);
                        } else {
                            pullRefreshListView.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    AndroidUtil
                                            .showToast(R.string.drop_down_list_footer_no_more_text);
                                    pullRefreshListView.onRefreshComplete();
                                }
                            }, 500);
                        }

                    }

                });
        pullRefreshListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //倒数第二个的时候加载新数据
                if (totalItemCount - (firstVisibleItem + visibleItemCount) == 1 && !isLoading) {
                    isLoading = true;
                    DebugLog.w("总数item:" + totalItemCount + "当前item:" + (firstVisibleItem + visibleItemCount));
                    if (mPage < pageDatas.pages) {
                        getRequestData(mPage + 1);
                    } else {
                        pullRefreshListView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                AndroidUtil
                                        .showToast(R.string.drop_down_list_footer_no_more_text);
                                pullRefreshListView.onRefreshComplete();
                            }
                        }, 500);
                    }
                }
            }
        });

    }

    private void getRequestData(int page) {

        //		showProgress("", mContext.getString(R.string.network_wait));

        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        ReqData reqData = new ReqData();
        reqData.setC("user");
        reqData.setA("init");
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", userInfo.getUserID());
        data.put("uid", uid);
        data.put("page", page + "");
        reqData.setData(data);

        try {
            String url = getString(R.string.base_url) + reqData.toReqString();
            DebugLog.d("个人主页:" + url);
            getRequestQueue().add(
                    new StringRequest(url, new Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            hideProgress();
                            checkRequestData(response);
                            // updataUI();
                            setNetComplete();

                        }

                    }, new ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            DebugLog.e("net error:" + error.getMessage());

                            AndroidUtil.showToast(R.string.reqFailed);

                            setNetComplete();
                        }
                    }));

        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    protected void checkRequestData(String response) {
        try {
            JSONObject json = new JSONObject(response);
            int success = json.getInt("success");
            if (success == 1) {
                pageDatas = GsonHelper.parseObject(response,
                        HomePageDatas.class);
                if (pageDatas.page == 1) {
                    title.setText(pageDatas.userinfo.username);
                    configShareContent(pageDatas.sharedata);
                    adapter.setData(pageDatas);
                } else {
                    if (pageDatas.page != mPage) {
                        adapter.addData(pageDatas.list);
                    }
                }
                mPage = pageDatas.page;
                setNetComplete();
            } else {
                AndroidUtil.showToast(mContext
                        .getString(R.string.reqDataFailed));
            }
        } catch (JSONException e) {
            DebugLog.e("json error:" + e.getMessage());
        }

    }

    private void setNetComplete() {
        hideProgress();
        pullRefreshListView.onRefreshComplete();
        isLoading = false;
    }

    private void configShareContent(ShareData shareData) {
        if (null == shareData) {
            return;
        }
        share = new SharePopupWindow(this);
        share.initShareParams(shareData);
        //		share.setTid(pageDatas.)
        share.showShareWindow();
    }

    public interface HomePageListener {
        public void refreshUI(HomepageUser user);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }
}
