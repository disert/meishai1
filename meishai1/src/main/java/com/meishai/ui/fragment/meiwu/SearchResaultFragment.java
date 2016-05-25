package com.meishai.ui.fragment.meiwu;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.meishai.R;
import com.meishai.ui.base.BaseFragment;
import com.meishai.ui.sliding.PagerSlidingTabStrip;
import com.meishai.util.DebugLog;

/**
 * 美物界面 2.0
 *
 * @author yl
 */
public class SearchResaultFragment extends BaseFragment {
    private View view;
    private PagerSlidingTabStrip mTabs;
    private DisplayMetrics dm;
    private ViewPager mPager;
    private MyPagerAdapter mAdapter;

    private SearchShaiShaiFragment mShaiShaiFragment = new SearchShaiShaiFragment();// 美晒
    private SearchSKUFragment skuFragment = new SearchSKUFragment(); // 单品推荐
    private SearchStratFragment stratFragment = new SearchStratFragment();
    ;// 热门攻略
    private String mKey;
    private int mType;//默认显示的搜索结果的类型,0 晒晒 1 美物


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.meiwu, null);
        dm = getResources().getDisplayMetrics();
        this.initView();
        setTabsValue();
        updateKey(mKey);

        if (mType == SearchActivity.TYPE_MEIWU) {
            mPager.setCurrentItem(1);
        } else {
            mPager.setCurrentItem(0);
        }
        return view;
    }

    private void initView() {
        view.findViewById(R.id.lay_top).setVisibility(View.GONE);
        mType = getArguments().getInt("type", 0);


        mTabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        mPager = (ViewPager) view.findViewById(R.id.pager);
        mAdapter = new MyPagerAdapter(getChildFragmentManager());
        mPager.setOffscreenPageLimit(2);
        mPager.setAdapter(mAdapter);

        mPager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                DebugLog.w("onPageSelected被调用");
                opera(position, mKey);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

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
                TypedValue.COMPLEX_UNIT_SP, 10, dm));
        mTabs.setIndicatorColor(getResources().getColor(R.color.title_bg));
        mTabs.setSelectedTextColor(getResources().getColor(R.color.title_bg));
        mTabs.setTextColor(0xff777777);
        mTabs.setTabBackground(0);
    }


    public class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private final String[] titles = {"晒晒", "攻略", "美物"};

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
                case 1:// 热门攻略
                    stratFragment.setKey(mKey);
                    return stratFragment;
                case 0:// 晒晒
                    mShaiShaiFragment.setKey(mKey);
                    return mShaiShaiFragment;
                case 2:// 单品推荐
                    skuFragment.setKey(mKey);
                    return skuFragment;
                default:
                    return null;
            }
        }
    }

    /**
     * 当搜索的关键字发生改变时被调用
     *
     * @param key 改变后的关键字
     */
    public void setKey(String key) {
        updateKey(key);
        mKey = key;
    }

    /**
     * 关键字发生变化时需要做的操作
     *
     * @param key
     */
    private void updateKey(String key) {
        DebugLog.w("setKey:" + key);
        //判断数据
        if (TextUtils.isEmpty(key) || key.equals(mKey)) {
            return;
        }
        //判断是否初始化
        if (mPager == null) {
            return;
        }
        int position = mPager.getCurrentItem();

        opera(position, key);
    }

    private void opera(int position, String key) {
        stratFragment.setKey(key);
        mShaiShaiFragment.setKey(key);
        skuFragment.setKey(key);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateKey(mKey);
    }
}
