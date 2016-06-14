package com.meishai.ui.fragment.message;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.app.widget.EditTextWithDel;
import com.meishai.entiy.CommentInfo;
import com.meishai.net.volley.Response;
import com.meishai.ui.base.BaseActivity;
import com.meishai.ui.fragment.meiwu.adapter.MeiwuAdapter;
import com.meishai.ui.fragment.message.adapter.CommentAdapter;
import com.meishai.ui.fragment.message.adapter.FansAdapter;
import com.meishai.ui.fragment.message.adapter.FavAdapter;
import com.meishai.ui.fragment.message.adapter.NotiAdapter;
import com.meishai.ui.fragment.message.adapter.ZanAdapter;
import com.meishai.ui.popup.CommentPopupWindow;
import com.meishai.ui.popup.CommentPopupWindow1;
import com.meishai.util.AndroidUtil;
import com.meishai.util.PullToRefreshHelper;

/**
 * 文件名：
 * 描    述：展示简单列表的Activity,这里用来展示评论,点赞,收藏,通知,粉丝
 * 作    者：yl
 * 时    间：2016/6/13
 * 版    权：
 */
public class ListActivity extends BaseActivity {

    private PullToRefreshListView pullRefreshListView;
    private MeiwuAdapter mAdapter;
    private boolean isLoading;//是否是在加载数据
    private CommentPopupWindow1 commentPopupWindow;
    private View lay_main;
    private EditTextWithDel edit;//底部评论框
    private int mType;//页面类型
    public static final int TYPE_ZAN = 0;//点赞
    public static final int TYPE_COM = 1;//评论
    public static final int TYPE_FAV = 2;//收藏
    public static final int TYPE_NOTI = 3;//通知
    public static final int TYPE_FANS = 4;//粉丝

    public static Intent newIntent(int type) {
        Intent intent = new Intent(GlobalContext.getInstance(),
                ListActivity.class);
        intent.putExtra("type", type);
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
        mType = getIntent().getIntExtra("type", 0);
        // 标题栏部分
        lay_main = findViewById(R.id.lay_main);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.more).setVisibility(View.GONE);
        //底部的评论框
        findViewById(R.id.comment_bar_root).setVisibility(View.GONE);
        // 帖子list
        pullRefreshListView = (PullToRefreshListView) this
                .findViewById(R.id.listview);
        pullRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        PullToRefreshHelper.initIndicatorStart(pullRefreshListView);
        PullToRefreshHelper.initIndicator(pullRefreshListView);
        mAdapter = initAdapter(mType);
        ((TextView) findViewById(R.id.title)).setText(mAdapter.getTitle());
        //设置某个item被点击的监听器
        pullRefreshListView.setAdapter(mAdapter);


        pullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                getRequestData(1);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                if (mAdapter.hasPage()) {
                    getRequestData(mAdapter.getPage());
                } else {
                    pullRefreshListView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            AndroidUtil
                                    .showToast(R.string.drop_down_list_footer_no_more_text);
                            isLoading = false;
                            pullRefreshListView.onRefreshComplete();
                        }
                    }, 500);
                }
            }
        });
        //        pullRefreshListView.setOnScrollListener(new AbsListView.OnScrollListener() {
        //            @Override
        //            public void onScrollStateChanged(AbsListView view, int scrollState) {
        //
        //            }
        //
        //            @Override
        //            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        //                //倒数第二个的时候加载新数据
        //                if (totalItemCount - (firstVisibleItem + visibleItemCount) == 1 && !isLoading) {
        //                    DebugLog.w("总数item:" + totalItemCount + "当前item:" + (firstVisibleItem + visibleItemCount));
        //                    if (mAdapter.hasPage()) {
        //                        getRequestData(mAdapter.getPage());
        //                    }
        //                }
        //            }
        //        });
    }

    private MeiwuAdapter initAdapter(int type) {
        switch (type) {
            case TYPE_COM:
                return new CommentAdapter(this, getImageLoader());
            case TYPE_FANS:
                return new FansAdapter(this, getImageLoader());
            case TYPE_FAV:
                return new FavAdapter(this, getImageLoader());
            case TYPE_NOTI:
                return new NotiAdapter(this, getImageLoader());
            case TYPE_ZAN:
                return new ZanAdapter(this, getImageLoader());
        }
        return null;
    }


    private Response.Listener<String> mListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            isLoading = false;
            pullRefreshListView.onRefreshComplete();
            if (!TextUtils.isEmpty(response)) {
                mAdapter.setData(response);
            } else {
                AndroidUtil.showToast("没有数据!");
            }
        }
    };

    private void getRequestData(final int page) {
        isLoading = true;
        switch (mType) {
            case TYPE_ZAN:
                MessageReq.zan(this, page, mListener);
                break;
            case TYPE_NOTI:
                MessageReq.notification(this, page, mListener);
                break;
            case TYPE_FAV:
                MessageReq.fav(this, page, mListener);
                break;
            case TYPE_FANS:
                MessageReq.fans(this, page, mListener);
                break;
            case TYPE_COM:
                MessageReq.comment(this, page, mListener);
                break;
        }

    }

}
