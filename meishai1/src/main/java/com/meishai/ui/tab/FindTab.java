package com.meishai.ui.tab;

import com.meishai.ui.fragment.meiwu.MeiWuFragment1;


public class FindTab extends ChildMainFragment {

    @Override
    protected void onInitFragment() {
        super.onInitFragment();
//		initFragment(MeiWuFragment.class);
        initFragment(MeiWuFragment1.class);
    }
}
