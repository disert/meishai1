package com.meishai.ui.fragment.camera;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.meishai.ui.MainActivity;
import com.meishai.ui.base.BaseFragment;
import com.meishai.util.DebugLog;

public class CameraSplashFragment extends BaseFragment {

    private final static int REQUEST_CAMERA = 0x1001;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        startCamera();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void startCamera() {
        startActivityForResult(CameraActivity.newIntent(CameraActivity.TID_UNKNOWN), REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        DebugLog.d("request:" + requestCode + ",result:" + resultCode);

//		if(requestCode == REQUEST_CAMERA) {
//			//if(resultCode == )
//			((MainActivity)getActivity()).setCurrentTab(0);
//		}
    }


}
