package com.meishai.ui.fragment.home;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.app.widget.EditTextWithDel;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.CommentInfo;
import com.meishai.entiy.CommentRespData;
import com.meishai.entiy.PostItem;
import com.meishai.net.volley.Response;
import com.meishai.net.volley.VolleyError;
import com.meishai.ui.base.BaseActivity;
import com.meishai.ui.fragment.home.adapter.CommentAdapter;
import com.meishai.ui.fragment.home.req.HomeReq;
import com.meishai.ui.fragment.usercenter.LoginActivity;
import com.meishai.ui.popup.CommentPopupWindow;
import com.meishai.ui.popup.CommentPopupWindow1;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;
import com.meishai.util.GsonHelper;
import com.meishai.util.PullToRefreshHelper;

/**
 * 文件名：
 * 描    述：评论详情的activity 用于显示评论的
 * 作    者：yl
 * 时    间：2016/2/4
 * 版    权：
 */
public class CommentActivity extends BaseActivity {

    private int mPid;//帖子唯一标识
    private int mListorder = 0;//排序方式
    private int mPage;//当前页
    private PullToRefreshListView pullRefreshListView;
    private CommentAdapter mAdapter;
    private boolean isLoading;//是否是在加载数据
    private CommentRespData mData;
    private CommentPopupWindow1 commentPopupWindow;
    private View lay_main;
    private EditTextWithDel edit;//底部评论框

    public static Intent newIntent(int pid) {
        Intent intent = new Intent(GlobalContext.getInstance(),
                CommentActivity.class);
        intent.putExtra("pid", pid);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_listview_activity);

        initView();
        initPop();

        getRequestData(1);
    }

    private void initPop() {
        commentPopupWindow = new CommentPopupWindow1(this);
        /**
         * 设置评论成功后的回调
         */
        commentPopupWindow.setOnCommentSuccessListener(new CommentPopupWindow.OnCommentSuccessListener() {

            @Override
            public void onCommentSuccess(CommentInfo info) {
                getRequestData(1);

            }
        });
    }

    private void initView() {
        mPid = getIntent().getIntExtra("pid", 0);
//        lay_main = (LinearLayout) findViewById(R.id.root);
        // 标题栏部分
        lay_main = findViewById(R.id.lay_main);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((TextView) findViewById(R.id.title)).setText("评论详情");
        findViewById(R.id.more).setVisibility(View.GONE);

        //底部的评论框
        findViewById(R.id.comment_bar_root).setVisibility(View.VISIBLE);
        edit = (EditTextWithDel) findViewById(R.id.comment_bar);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MeiShaiSP.getInstance().getUserInfo().isLogin()) {
                    startActivity(LoginActivity.newIntent());
                    return;
                }
                if (mData != null) {
                    PostItem item = new PostItem();
                    item.pid = mPid;
                    commentPopupWindow.setPostItem(item);
                    commentPopupWindow.showPop(lay_main);
                }
            }
        });
        // 帖子list
        pullRefreshListView = (PullToRefreshListView) this
                .findViewById(R.id.listview);
        pullRefreshListView.setMode(PullToRefreshBase.Mode.DISABLED);
        PullToRefreshHelper.initIndicatorStart(pullRefreshListView);
        PullToRefreshHelper.initIndicator(pullRefreshListView);
        mAdapter = new CommentAdapter(this, GlobalContext.getInstance().getImageLoader());
        //设置某个item被点击的监听器
        mAdapter.setCommentListener(new PostShowActivity.OnCommentClickListener() {

            @Override
            public void onClick(CommentInfo commentInfo) {
                PostItem item = new PostItem();
                item.pid = mPid;
                commentPopupWindow.setPostItem(item);
                commentPopupWindow.setCommentInfo(commentInfo);
                commentPopupWindow.showPop(lay_main);
            }
        });
        pullRefreshListView.setAdapter(mAdapter);


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
                    if (mPage < mData.pages) {
                        getRequestData(mPage + 1);
                    }
                }
            }
        });
    }

    private void getRequestData(final int page) {

        HomeReq.commentDetail(this, page, 10, mPid, mListorder, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!TextUtils.isEmpty(response)) {
                    CommentRespData respData = GsonHelper.parseObject(response, CommentRespData.class);
                    if (respData != null) {
                        if (respData.success == 1) {
                            if (respData.page == 1) {
                                mData = respData;
                            } else if (mPage != respData.page) {
                                mData.data.addAll(respData.data);
                            }
                            mPage = respData.page;

                            mAdapter.setData(mData);
                        }
                    } else {
                        AndroidUtil.showToast("json数据解析错误!");
                    }
                } else {
                    AndroidUtil.showToast("没有数据!");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

    }

}
