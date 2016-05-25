package com.meishai.ui.fragment.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.meishai.R;
import com.meishai.ui.base.BaseFragment;

/**
 * 美晒->话题-> 字内容
 *
 * @author sh
 */
public class HomeTopicChildFragment extends BaseFragment {

    private View mChildView;
    private PullToRefreshListView list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mChildView = inflater.inflate(R.layout.home_topic_child, container,
                false);
        initView();
        return mChildView;
    }

    private void initView() {
        list = (PullToRefreshListView) mChildView.findViewById(R.id.list);
    }
}
