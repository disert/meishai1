package com.meishai.ui.fragment.tryuse;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.meishai.R;
import com.meishai.entiy.TryInfo.TryInfoType;
import com.meishai.ui.base.BaseFragment;
import com.meishai.ui.sliding.PagerSlidingTabStrip;
import com.meishai.ui.sliding.PagerSlidingTabStripHelper;

/**
 * 试用主界面
 *
 * @author sh
 */
public class TryUseFragment extends BaseFragment implements OnClickListener {
    private View view;
    private PagerSlidingTabStrip tabs;

    private ImageButton btn_cate;
    // private TryChildFragment tryAllFragment;
    // private TryChildFragment tryJpFragment;
    private TryChildFragment tryTaskFragment;
    private TryChildFragment tryHbragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tryuse, null);
        this.initView();
        this.setTabsValue();
        return view;
    }

    private void initView() {
        ViewPager pager = (ViewPager) view.findViewById(R.id.try_pager);
        tabs = (PagerSlidingTabStrip) view.findViewById(R.id.try_tabs);
        pager.setAdapter(new MyPagerAdapter(getChildFragmentManager()));
        tabs.setViewPager(pager);
        btn_cate = (ImageButton) view.findViewById(R.id.btn_cate);
        btn_cate.setOnClickListener(this);
    }

    private void setTabsValue() {
        PagerSlidingTabStripHelper.setTabsValue(tabs);
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private final String[] titles = TryInfoType.getAllTypeRemark();

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                // case 0:
                // if (null == tryAllFragment) {
                // tryAllFragment = TryChildFragment
                // .newInstance(TryInfoType.TRY_ALL.getType());
                // }
                // return tryAllFragment;
                // case 1:
                // if (null == tryJpFragment) {
                // tryJpFragment = TryChildFragment
                // .newInstance(TryInfoType.TRY_JP.getType());
                // }
                // return tryJpFragment;
                case 0:
                    if (null == tryTaskFragment) {
                        tryTaskFragment = TryChildFragment
                                .newInstance(TryInfoType.TRY_TASK.getType());
                    }
                    return tryTaskFragment;
                case 1:
                    if (null == tryHbragment) {
                        tryHbragment = TryChildFragment
                                .newInstance(TryInfoType.TRY_HB.getType());
                    }
                    return tryHbragment;
                default:
                    return null;
            }
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.btn_cate:
                intent.setClass(mContext, TryUseCateActivity.class);
                startActivity(intent);
                break;

            default:
                break;
        }

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        // if (null != tryAllFragment) {
        // tryAllFragment.onHiddenChanged(hidden);
        // }
        // if (null != tryJpFragment) {
        // tryJpFragment.onHiddenChanged(hidden);
        // }
        if (null != tryTaskFragment) {
            tryTaskFragment.onHiddenChanged(hidden);
        }
        if (null != tryHbragment) {
            tryHbragment.onHiddenChanged(hidden);
        }
        super.onHiddenChanged(hidden);
    }

}
