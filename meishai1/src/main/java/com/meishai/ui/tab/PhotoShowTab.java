package com.meishai.ui.tab;

import com.meishai.ui.fragment.home.HandPickFragment;


public class PhotoShowTab extends ChildMainFragment {

    @Override
    protected void onInitFragment() {
        super.onInitFragment();
//        initFragment(HomeFragment.class);
        initFragment(HandPickFragment.class);
    }

}
