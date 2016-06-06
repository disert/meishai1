package com.meishai.ui.tab;

import com.meishai.ui.fragment.home.FindFragment;


public class FindTab extends ChildMainFragment {

    @Override
    protected void onInitFragment() {
        super.onInitFragment();
//        initFragment(MeiWuFragment1.class);
        initFragment(FindFragment.class);
    }
}
