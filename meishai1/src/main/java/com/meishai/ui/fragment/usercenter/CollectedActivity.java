package com.meishai.ui.fragment.usercenter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.ui.base.BaseFragmentActivity;
import com.meishai.ui.sliding.PagerSlidingTabStrip;

/**
 * 美物界面 2.0
 *
 * @author yl
 */
public class CollectedActivity extends FragmentActivity {
    private TextView mTitle;
    private PagerSlidingTabStrip mTabs;
    private DisplayMetrics dm;
    private ViewPager mPager;
    private MyPagerAdapter mAdapter;

    //	private CollectedSpecialFragment specialFragment;//专场
    private CollectedSKUFragment skuFragment; //美物
    private CollectedStratFragment stratFragment;//攻略
    private CollectedPostFragment meishaiFragment;//美晒
    private int index;

    public static Intent newIntent(int index) {
        Intent intent = new Intent(GlobalContext.getInstance(),
                CollectedActivity.class);
        intent.putExtra("index", index);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.collected_activity);
        dm = getResources().getDisplayMetrics();
        initView();
        setTabsValue();
        initData();
    }


    private void initData() {
        index = getIntent().getIntExtra("index", 0);
        if (index > mAdapter.getCount()) {
            index = 0;
        }
        mPager.setCurrentItem(index);
    }


    private void initView() {
        findViewById(R.id.backMain).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
        mTitle = (TextView) findViewById(R.id.title);
        mTabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        mPager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mPager.setOffscreenPageLimit(3);
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

        private final String[] titles = {"攻略", "美物", "晒晒"};

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
                case 0:// 攻略
                    if (stratFragment == null) {
                        stratFragment = new CollectedStratFragment();
                    }
                    return stratFragment;

//			case 1:// 专场
//				if (specialFragment == null) {
//					specialFragment = new CollectedSpecialFragment();
//				}
//				return specialFragment;
                case 1:// 美物
                    if (skuFragment == null) {
                        skuFragment = new CollectedSKUFragment();
                    }
                    return skuFragment;
                case 2:// 美晒
                    if (meishaiFragment == null) {
                        meishaiFragment = new CollectedPostFragment();
                    }
                    return meishaiFragment;
                default:
                    return null;
            }
        }
    }


}
