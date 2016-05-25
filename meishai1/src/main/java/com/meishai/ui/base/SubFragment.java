package com.meishai.ui.base;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.View;

/**
 * 无底部TabHost的Fragment 问题:从返回键返回时 TabHost还是隐藏的
 *
 * @author sh
 */
public class SubFragment extends BaseFragment {
    private FragmentTabHost mTabHost = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTabHost = (FragmentTabHost) getActivity().findViewById(android.R.id.tabhost);
        mTabHost.setVisibility(View.GONE);
    }

    public void doBackClick() {
        super.doBackClick();
        mTabHost.setVisibility(View.VISIBLE);
    }

}
