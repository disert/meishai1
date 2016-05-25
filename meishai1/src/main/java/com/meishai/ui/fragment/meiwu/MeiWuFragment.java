package com.meishai.ui.fragment.meiwu;

import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.ui.base.BaseFragment;
import com.meishai.ui.sliding.PagerSlidingTabStrip;

/**
 * 美物界面 2.0
 *
 * @author yl
 */
public class MeiWuFragment extends BaseFragment {
    private View view;
    private TextView mTitle;
    private PagerSlidingTabStrip mTabs;
    private DisplayMetrics dm;
    private ViewPager mPager;
    private MyPagerAdapter mAdapter;

    private MeiWuSpecialFragment specialFragment;// 品质专场
    //	private MeiWuSKUFragment skuFragment; // 单品推荐
    private MeiWuShopsFragment shopsFragment; // 品质好店
    private MeiWuStratFragment stratFragment;// 热门攻略
    private ImageView mSearch;

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
        mSearch = (ImageView) view.findViewById(R.id.search);
        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, SearchActivity.class));
            }
        });
        mTabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        mPager = (ViewPager) view.findViewById(R.id.pager);
        mAdapter = new MyPagerAdapter(getChildFragmentManager());
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

        private final String[] titles = {"热门攻略", "品质专场", "品质好店"};

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
                case 0:// 热门攻略
                    if (stratFragment == null) {
                        stratFragment = new MeiWuStratFragment();
                    }
                    return stratFragment;
                case 1:// 品质专场
                    if (specialFragment == null) {
                        specialFragment = new MeiWuSpecialFragment();
                    }
                    return specialFragment;
                case 2:// 品质好店
                    if (shopsFragment == null) {
                        shopsFragment = new MeiWuShopsFragment();
                    }
                    return shopsFragment;
                default:
                    return null;
            }
        }
    }


}
