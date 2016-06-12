package com.meishai.ui.tab;

import com.meishai.ui.fragment.message.MessageFragment;


public class MessageTab extends ChildMainFragment {

    @Override
    protected void onInitFragment() {
        super.onInitFragment();
        initFragment(MessageFragment.class);
//        initFragment(MeiWuFragment1.class);
    }
}
