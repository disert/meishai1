package com.meishai.ui.tab;

import com.meishai.ui.base.BaseFragment;
import com.meishai.ui.fragment.usercenter.UserCenterFragment;

public class UserCenterTab extends ChildMainFragment {
    @Override
    protected void onInitFragment() {
        super.onInitFragment();
        initFragment(UserCenterFragment.class);
    }

    public void refreshFragment() {
        UserCenterFragment f = (UserCenterFragment) getFragment(UserCenterFragment.class.getName());
        if (null != f) {
            f.refreshUserInfo();
        }
    }

    // @Override
    // public void onActivityResult(int requestCode, int resultCode, Intent
    // data) {
    // Fragment f = getFragment(UserInfoFragment.class.getName());
    // if (null != f) {
    // f.onActivityResult(requestCode, resultCode, data);
    // }
    // }
}
