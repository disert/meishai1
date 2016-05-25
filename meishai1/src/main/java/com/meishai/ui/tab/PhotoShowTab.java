package com.meishai.ui.tab;

import com.meishai.ui.fragment.home.HomeFragment;


public class PhotoShowTab extends ChildMainFragment {

    @Override
    protected void onInitFragment() {
        super.onInitFragment();
        initFragment(HomeFragment.class);
    }

}
