package com.meishai.util;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.meishai.GlobalContext;
import com.meishai.R;

public class PullToRefreshHelper {

    /**
     * 生成PullToRefreshBase的下拉刷新Indicator
     *
     * @param pullToRefreshBase
     */
    public static void initIndicatorStart(PullToRefreshBase pullToRefreshBase) {
        ILoadingLayout startLabels = pullToRefreshBase
                .getLoadingLayoutProxy(true, false);
        startLabels.setPullLabel(GlobalContext.getInstance().getString(
                R.string.refreshlist_refresh_pull_label));// 刚下拉时，显示的提示
        startLabels.setRefreshingLabel(GlobalContext.getInstance().getString(
                R.string.refreshlist_header_hint_loading));// 刷新时
        startLabels.setReleaseLabel(GlobalContext.getInstance().getString(
                R.string.refreshlist_header_hint_ready));// 下来达到一定距离时，显示的提示
    }

    /**
     * 生成PullToRefreshBase的Indicator
     *
     * @param pullToRefreshBase
     */
    public static void initIndicator(PullToRefreshBase pullToRefreshBase) {
        ILoadingLayout endLabels = pullToRefreshBase.getLoadingLayoutProxy(false, true);
        endLabels.setPullLabel(GlobalContext.getInstance().getString(
                R.string.refreshlist_footer_hint_normal));
        endLabels.setRefreshingLabel(GlobalContext.getInstance().getString(
                R.string.refreshlist_header_hint_loading));
        endLabels.setReleaseLabel(GlobalContext.getInstance().getString(
                R.string.refreshlist_header_hint_ready));
    }
}
