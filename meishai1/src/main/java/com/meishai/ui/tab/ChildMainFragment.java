package com.meishai.ui.tab;

import java.util.HashMap;
import java.util.Map;

import com.meishai.R;
import com.meishai.ui.base.BaseFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ChildMainFragment extends BaseFragment {

    private View mRootView;
    private Map<String, BaseFragment> mFragmentMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == mRootView) {
            mRootView = inflater.inflate(R.layout.main_child_home, container, false);
            onInitFragment();
        }
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (null != parent) {
            parent.removeView(mRootView);
        }
        return mRootView;
    }

    protected void onInitFragment() {
    }

    protected void initFragment(Class<?> cls) {
        mFragmentMap = new HashMap<String, BaseFragment>();
        Fragment frg = newFragmentInstance(cls, cls.getName(), null);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.main_child_frame, frg, cls.getName());
        transaction.commitAllowingStateLoss();
    }

    public BaseFragment newFragmentInstance(Class<?> cls, String name, Bundle args) {
        BaseFragment frg = mFragmentMap.get(name);
        if (null == frg) {
            try {
                frg = (BaseFragment) cls.newInstance();
                mFragmentMap.put(name, frg);
            } catch (java.lang.InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        frg.setArguments(args);
        return frg;
    }

    public BaseFragment getFragment(String name) {
        if (null == mFragmentMap) {
            return null;
        }
        return mFragmentMap.get(name);
    }

//	@Override
//	protected void doBackPress() {
//		//super.doBackPress();
//	}
//	
}