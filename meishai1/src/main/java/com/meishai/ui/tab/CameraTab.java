package com.meishai.ui.tab;

import android.content.Intent;

import com.meishai.ui.fragment.camera.CameraSplashFragment;
import com.meishai.util.DebugLog;

public class CameraTab extends ChildMainFragment {

    @Override
    protected void onInitFragment() {
        super.onInitFragment();
        initFragment(CameraSplashFragment.class);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        DebugLog.d("request:" + requestCode + ",result:" + resultCode);

        getFragment(CameraSplashFragment.class.getName()).onActivityResult(
                requestCode, resultCode, data);
    }
}
