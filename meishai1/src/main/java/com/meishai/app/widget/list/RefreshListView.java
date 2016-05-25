package com.meishai.app.widget.list;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.meishai.R;


public class RefreshListView extends ListView implements OnScrollListener {

    private float mLastY = -1; // save event y
    private Scroller mScroller; // 用于回滚
    private OnScrollListener mScrollListener; // 回滚监听
    // 触发刷新和加载更多接口.
    private IOnRefreshListener mListViewListener;

    private LinearLayout headerMainlayout;    //头部布局

    // -- 头部的View
    private RefreshListViewHeader mHeaderView;
    // 查看头部的内容，用它计算头部高度，和隐藏它
    // 当禁用的时候刷新
    private RelativeLayout mHeaderViewContent;
    private TextView mHeaderTimeView;
    private int mHeaderViewHeight; // 头部View的高
    private boolean mEnablePullRefresh = true;
    private boolean mPullRefreshing = false; // 是否刷新.
    // -- 底部的View
    private RefreshListViewFooter mFooterView;
    private boolean mEnablePullLoad;
    private boolean mPullLoading;
    private boolean mIsFooterReady = false;
    // 总列表项，用于检测列表视图的底部
    private int mTotalItemCount;
    // for mScroller, 滚动页眉或者页脚
    private int mScrollBack;
    private final static int SCROLLBACK_HEADER = 0;// 顶部
    private final static int SCROLLBACK_FOOTER = 1;// 下部
    private final static int SCROLL_DURATION = 400; // 滚动回时间
    private final static int PULL_LOAD_MORE_DELTA = 50; // 当大于50PX的时候，加载更多
    private final static float OFFSET_RADIO = 1.8f; // support iOS like pull
    // feature.

    /**
     * @param context
     */
    public RefreshListView(Context context) {
        super(context);
        initWithContext(context);
    }

    public RefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWithContext(context);
    }

    public RefreshListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mScroller = new Scroller(context, new DecelerateInterpolator());
        initWithContext(context);
    }

    private void initWithContext(Context context) {
        mScroller = new Scroller(context, new DecelerateInterpolator());
        // RefreshView need the scroll event, and it will dispatch the event to
        // user's listener (as a proxy).
        super.setOnScrollListener(this);

        // 初始化头部View
        headerMainlayout = new LinearLayout(context);
        headerMainlayout.setOrientation(LinearLayout.VERTICAL);
        headerMainlayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        // 头部View
        mHeaderView = new RefreshListViewHeader(context);
        mHeaderViewContent = (RelativeLayout) mHeaderView.findViewById(R.id.xlistview_header_content);
        mHeaderTimeView = (TextView) mHeaderView.findViewById(R.id.xlistview_header_time);


        headerMainlayout.addView(mHeaderView);

        addHeaderView(headerMainlayout);// 把头部这个视图添加进去
        // 初始化底部的View
        mFooterView = new RefreshListViewFooter(context);
        // 初始化头部高度
        mHeaderView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                mHeaderViewHeight = mHeaderViewContent.getHeight();
                getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    /**
     * 增加头部内容展示区
     *
     * @param view
     */
    public void addHeaderContentView(View view) {
        headerMainlayout.addView(view);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        // 确定RefreshListViewFooter是最后底部的View, 并且只有一次
        if (mIsFooterReady == false) {
            mIsFooterReady = true;
            addFooterView(mFooterView);
        }
        super.setAdapter(adapter);
    }

    /**
     * 下拉刷新提示文字
     *
     * @param text
     */
    public void setHeaderViewHint(String text) {
        mHeaderView.setHintText(text);
    }

    /**
     * 向上拖动加载更多
     *
     * @param text
     */
    public void setFooterViewHint(String text) {
        mFooterView.setHintText(text);
    }

    /**
     * 启用或禁用下拉刷新功能.
     *
     * @param enable
     */
    public void setPullRefreshEnable(boolean enable) {
        mEnablePullRefresh = enable;
        if (!mEnablePullRefresh) { // 禁用,隐藏内容
            mHeaderViewContent.setVisibility(View.INVISIBLE);// 如果为false则隐藏下拉刷新功能
        } else {
            mHeaderViewContent.setVisibility(View.VISIBLE);// 否则就显示下拉刷新功能
        }
    }

    /**
     * 启用或禁用加载更多的功能.
     *
     * @param enable
     */
    public void setPullLoadEnable(boolean enable) {
        mEnablePullLoad = enable;
        if (!mEnablePullLoad) {
            mFooterView.hide();// 隐藏
            mFooterView.setOnClickListener(null);
        } else {
            mPullLoading = false;
            mFooterView.show();// 显示
            mFooterView.setState(RefreshListViewFooter.STATE_NORMAL);
            // both "上拉" 和 "点击" 将调用加载更多.
            mFooterView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    startLoadMore();
                }
            });
        }
    }

    /**
     * 停止刷新, 重置头视图.
     */
    public void stopRefresh() {
        if (mPullRefreshing == true) {
            mPullRefreshing = false;
            resetHeaderHeight();
        }
    }

    /**
     * stop load more, reset footer view.
     */
    public void stopLoadMore() {
        if (mPullLoading == true) {
            mPullLoading = false;
            mFooterView.setState(RefreshListViewFooter.STATE_NORMAL);
        }
    }

    /**
     * 設置最後一次刷新時間
     *
     * @param time
     */
    @SuppressLint("SimpleDateFormat")
    public void setRefreshTime(String time) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日   HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        //获取当前时间
        String str = formatter.format(curDate);
        mHeaderTimeView.setText(str);
    }

    /**
     * 当刷新任务执行完毕时, 回调此方法, 去刷新界面
     */
    public void onRefreshFinish() {
        stopRefresh();
        stopLoadMore();
        setRefreshTime("刚刚");
    }

    private void invokeOnScrolling() {
        if (mScrollListener instanceof OnRefreshScrollListener) {
            OnRefreshScrollListener l = (OnRefreshScrollListener) mScrollListener;
            l.onRefreshScrolling(this);
        }
    }

    private void updateHeaderHeight(float delta) {
        mHeaderView.setVisiableHeight((int) delta + mHeaderView.getVisiableHeight());
        if (mEnablePullRefresh && !mPullRefreshing) { // 未处于刷新状态，更新箭头
            if (mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
                mHeaderView.setState(RefreshListViewHeader.STATE_READY);
            } else {
                mHeaderView.setState(RefreshListViewHeader.STATE_NORMAL);
            }
        }
        setSelection(0); // scroll to top each time
    }

    /**
     * 重置头视图的高度
     */
    private void resetHeaderHeight() {
        int height = mHeaderView.getVisiableHeight();
        if (height == 0) // 不显示.
            return;
        // 不显示刷新和标题的时候，什么都不显示
        if (mPullRefreshing && height <= mHeaderViewHeight) {
            return;
        }
        int finalHeight = 0; // 默认：滚动回头.
        // 当滚动回显示所有头标题时候，刷新
        if (mPullRefreshing && height > mHeaderViewHeight) {
            finalHeight = mHeaderViewHeight;
        }
        mScrollBack = SCROLLBACK_HEADER;
        mScroller.startScroll(0, height, 0, finalHeight - height, SCROLL_DURATION);
        // 触发刷新
        invalidate();
    }

    // 改变底部视图高度
    private void updateFooterHeight(float delta) {
        int height = mFooterView.getBottomMargin() + (int) delta;
        if (mEnablePullLoad && !mPullLoading) {
            if (height > PULL_LOAD_MORE_DELTA) { // 高度足以调用加载更多
                mFooterView.setState(RefreshListViewFooter.STATE_READY);
            } else {
                mFooterView.setState(RefreshListViewFooter.STATE_NORMAL);
            }
        }
        mFooterView.setBottomMargin(height);
        // setSelection(mTotalItemCount - 1); // scroll to bottom
    }

    private void resetFooterHeight() {
        int bottomMargin = mFooterView.getBottomMargin();
        if (bottomMargin > 0) {
            mScrollBack = SCROLLBACK_FOOTER;
            mScroller.startScroll(0, bottomMargin, 0, -bottomMargin, SCROLL_DURATION);
            invalidate();
        }
    }

    // 开始加载更多
    private void startLoadMore() {
        mPullLoading = true;
        mFooterView.setState(RefreshListViewFooter.STATE_LOADING);
        if (mListViewListener != null) {
            mListViewListener.onLoadMore();
        }
    }

    // 触发事件
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mLastY == -1) {
            mLastY = ev.getRawY();
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float deltaY = ev.getRawY() - mLastY;
                mLastY = ev.getRawY();
//				System.out.println("数据监测：" + getFirstVisiblePosition() + "---->" + getLastVisiblePosition());
                if (getFirstVisiblePosition() == 0 && (mHeaderView.getVisiableHeight() > 0 || deltaY > 0)) {
                    // 第一项显示,标题显示或拉下来.
                    updateHeaderHeight(deltaY / OFFSET_RADIO);
                    invokeOnScrolling();
                } else if (getLastVisiblePosition() == mTotalItemCount - 1 && (mFooterView.getBottomMargin() > 0 || deltaY < 0)) {
                    // 最后一页，已停止或者想拉起
                    updateFooterHeight(-deltaY / OFFSET_RADIO);
                }
                break;
            default:
                mLastY = -1; // 重置
                if (getFirstVisiblePosition() == 0) {
                    // 调用刷新,如果头部视图高度大于设定高度。
                    if (mEnablePullRefresh && mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
                        mPullRefreshing = true;// 那么刷新
                        mHeaderView.setState(RefreshListViewHeader.STATE_REFRESHING);
                        if (mListViewListener != null) {
                            mListViewListener.onRefresh();
                        }
                    }
                    resetHeaderHeight();// 刷新完毕，重置头部高度，也就是返回上不
                }
                if (getLastVisiblePosition() == mTotalItemCount - 1) {
                    // 调用加载更多.
                    if (mEnablePullLoad && mFooterView.getBottomMargin() > PULL_LOAD_MORE_DELTA) {
                        startLoadMore();// 如果底部视图高度大于可以加载高度，那么就开始加载
                    }
                    resetFooterHeight();// 重置加载更多视图高度
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            if (mScrollBack == SCROLLBACK_HEADER) {
                mHeaderView.setVisiableHeight(mScroller.getCurrY());
            } else {
                mFooterView.setBottomMargin(mScroller.getCurrY());
            }
            postInvalidate();
            invokeOnScrolling();
        }
        super.computeScroll();
    }

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        mScrollListener = l;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (mScrollListener != null) {
            mScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // 发送到用户的监听器
        mTotalItemCount = totalItemCount;
        if (mScrollListener != null) {
            mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }

    public void setRefreshListListener(IOnRefreshListener l) {
        mListViewListener = l;
    }

    /**
     * 你可以监听到列表视图，OnScrollListener 或者这个. 他将会被调用 , 当头部或底部触发的时候
     */
    public interface OnRefreshScrollListener extends OnScrollListener {

        public void onRefreshScrolling(View view);
    }
}
