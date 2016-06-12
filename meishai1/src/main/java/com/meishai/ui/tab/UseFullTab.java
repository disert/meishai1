package com.meishai.ui.tab;

import com.meishai.ui.fragment.tryuse.FuLiSheFragment1;

public class UseFullTab extends ChildMainFragment {

    @Override
    protected void onInitFragment() {
        super.onInitFragment();
//        initFragment(FreeTrialFragment.class);
		initFragment(FuLiSheFragment1.class);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
//		if (null != getFragment(TryUseFragment.class.getName())) {
//			getFragment(TryUseFragment.class.getName()).onHiddenChanged(hidden);
//		}
        super.onHiddenChanged(hidden);
    }
}
