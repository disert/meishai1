package com.meishai.ui.fragment.tryuse;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.ui.base.BaseFragment;
import com.meishai.ui.sliding.PagerSlidingTabStrip;

/**
 * 福利社主界面 2.0
 *
 * @author yl
 */
public class FuLiSheFragment extends BaseFragment {
    private View view;
    private TextView mTitle;
    private PagerSlidingTabStrip mTabs;
    private DisplayMetrics dm;
    private ViewPager mPager;
    private MyPagerAdapter mAdapter;

    private FlashSaleFragment flashSaleFragment;// 限时疯抢
    //	private PointRewardFragment pointRewardFragment; // 积分商城
    private TasteFragment tasteFragment; // 品质体验
    private FreeTrialFragment freeTrialFragment;// 免费试用

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.meiwu, null);
        dm = getResources().getDisplayMetrics();
        this.initView();
        setTabsValue();
        return view;
    }

    private void initView() {


        mTitle = (TextView) view.findViewById(R.id.title);
        mTitle.setText("福利社");
        mTabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        mPager = (ViewPager) view.findViewById(R.id.pager);
        mAdapter = new MyPagerAdapter(getFragmentManager());
        mPager.setOffscreenPageLimit(2);
        mPager.setAdapter(mAdapter);


        mPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                mTitle.setText(mAdapter.getPageTitle(arg0));
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
        mTabs.setViewPager(mPager);
    }

    private void setTabsValue() {
        mTabs.setShouldExpand(true);
        mTabs.setDividerColor(Color.TRANSPARENT);
        mTabs.setUnderlineHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 0.5f, dm));
        mTabs.setIndicatorHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 2, dm));
        mTabs.setTextSize((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 14, dm));
        mTabs.setIndicatorColor(getResources().getColor(R.color.title_bg));
        mTabs.setSelectedTextColor(getResources().getColor(R.color.title_bg));
        mTabs.setTextColor(0xff777777);
        mTabs.setTabBackground(0);
    }


    public class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private final String[] titles = {"品质体验", "免费试用", "限时疯抢"/*, "积分商城"*/};

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
                case 0:// 品质体验
                    if (tasteFragment == null) {
                        tasteFragment = new TasteFragment();
                    }
                    return tasteFragment;
                case 1:// 免费试用
                    if (freeTrialFragment == null) {
                        freeTrialFragment = new FreeTrialFragment();
                    }
                    return freeTrialFragment;
                case 2:// 限时疯抢
                    if (flashSaleFragment == null) {
                        flashSaleFragment = new FlashSaleFragment();
                    }
                    return flashSaleFragment;
                default:
                    return null;
            }
        }
    }


}
