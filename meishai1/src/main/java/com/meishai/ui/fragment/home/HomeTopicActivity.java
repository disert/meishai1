package com.meishai.ui.fragment.home;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;

import com.meishai.R;
import com.meishai.app.widget.TopBackLayout;
import com.meishai.entiy.CateTopic;
import com.meishai.ui.sliding.PagerSlidingTabStrip;

/**
 * 美晒->话题
 *
 * @author sh
 */
public class HomeTopicActivity extends FragmentActivity {

    private TopBackLayout backLayout;
    private PagerSlidingTabStrip tabs;
    private DisplayMetrics dm;
    private ViewPager pager;
    // 热门
    private TopicFragment hotFragment;
    // 关注
    private TopicFragment favFragment;
    // 最新
    private TopicFragment latestTopicFragment;
    private CateTopic cateTopic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_topic);
        cateTopic = getIntent().getParcelableExtra("cateTopic");
        dm = getResources().getDisplayMetrics();
        initView();
        setTabsValue();
    }

    private void initView() {
        backLayout = (TopBackLayout) findViewById(R.id.back_layout);
        backLayout.setOnBackListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        pager = (ViewPager) findViewById(R.id.pager);
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        tabs.setViewPager(pager);
        if (null != cateTopic) {
            if (CateTopic.HOT_TOPIC == cateTopic.getType()) {
                pager.setCurrentItem(0);
            } else if (CateTopic.FAV_TOPIC == cateTopic.getType()) {
                pager.setCurrentItem(1);
            } else if (CateTopic.LETAEST_TOPIC == cateTopic.getType()) {
                pager.setCurrentItem(2);
            }
        }
    }

    private void setTabsValue() {
        tabs.setShouldExpand(true);
        tabs.setDividerColor(Color.TRANSPARENT);
        tabs.setUnderlineHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 0.5f, dm));
        tabs.setIndicatorHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 2, dm));
        tabs.setTextSize((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 14, dm));
        tabs.setIndicatorColor(getResources().getColor(R.color.title_bg));
        tabs.setSelectedTextColor(getResources().getColor(R.color.title_bg));
        tabs.setTabBackground(0);
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private final String[] titles = {"热门话题", "关注话题", "最新话题"};

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
                case 0:
                    if (hotFragment == null) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("type", CateTopic.HOT_TOPIC);
                        hotFragment = new TopicFragment();
                        hotFragment.setArguments(bundle);
                    }
                    return hotFragment;
                case 1:
                    if (favFragment == null) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("type", CateTopic.FAV_TOPIC);
                        favFragment = new TopicFragment();
                        favFragment.setArguments(bundle);
                    }
                    return favFragment;
                case 2:
                    if (latestTopicFragment == null) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("type", CateTopic.LETAEST_TOPIC);
                        latestTopicFragment = new TopicFragment();
                        latestTopicFragment.setArguments(bundle);
                    }
                    return latestTopicFragment;
                default:
                    return null;
            }
        }
    }

}
