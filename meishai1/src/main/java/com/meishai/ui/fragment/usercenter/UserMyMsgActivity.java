package com.meishai.ui.fragment.usercenter;

import android.content.Intent;
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
import android.widget.TextView;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.app.widget.CustomProgress;
import com.meishai.entiy.CateResqData;
import com.meishai.ui.sliding.PagerSlidingTabStrip;

/**
 * 文件名：
 * 描    述：我的-我的消息
 * 作    者：yl
 * 时    间：2016/2/24
 * 版    权：
 */
public class UserMyMsgActivity extends FragmentActivity {

    private TextView mTitle;
    private PagerSlidingTabStrip mTabs;
    private DisplayMetrics dm;
    private ViewPager mPager;
    private MyPagerAdapter mAdapter;

    private CustomProgress mProgressDialog;
    private CateResqData mData;
    //我的消息
    private UserMyMsgFragment mMyMsgFragment;
    //我的私信
    private UserMyMsgFragment mMyPriMsgFragment;
    //试用提醒
    private UserMyMsgFragment mMyTrailMsgFragment;

    public static Intent newIntent() {
        Intent intent = new Intent(GlobalContext.getInstance(),
                UserMyMsgActivity.class);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.collected_activity);
        dm = getResources().getDisplayMetrics();
        initView();
        setTabsValue();
    }


    private void initView() {
        findViewById(R.id.backMain).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTitle = (TextView) findViewById(R.id.title);
        mTitle.setText("我的消息");
        mTabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        mPager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mPager.setOffscreenPageLimit(2);
        mPager.setAdapter(mAdapter);


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
                TypedValue.COMPLEX_UNIT_SP, 12, dm));
        mTabs.setTabPaddingLeftRight(26);
        mTabs.setIndicatorColor(getResources().getColor(R.color.title_bg));
        mTabs.setSelectedTextColor(getResources().getColor(R.color.title_bg));
        mTabs.setTextColor(0xff777777);
        mTabs.setTabBackground(0);
    }


    public class MyPagerAdapter extends FragmentPagerAdapter {
        private String titles[] = {"我的消息", "我的私信", "试用提醒"};

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {

                case 1:
                    if (mMyPriMsgFragment == null) {
                        mMyPriMsgFragment = new UserMyMsgFragment();
                        Bundle args = new Bundle();
                        args.putInt("tableid", 1);
                        mMyPriMsgFragment.setArguments(args);
                    }
                    return mMyPriMsgFragment;
                case 2:
                    if (mMyTrailMsgFragment == null) {
                        mMyTrailMsgFragment = new UserMyMsgFragment();
                        Bundle args = new Bundle();
                        args.putInt("tableid", 2);
                        mMyTrailMsgFragment.setArguments(args);
                    }
                    return mMyTrailMsgFragment;
                case 0:
                    if (mMyMsgFragment == null) {
                        mMyMsgFragment = new UserMyMsgFragment();
                        Bundle args = new Bundle();
                        args.putInt("tableid", 3);
                        mMyMsgFragment.setArguments(args);
                    }
                    return mMyMsgFragment;
                default:
                    return null;
            }

        }
    }

    public void showProgress(String title, String message) {
        if (null == mProgressDialog) {
            mProgressDialog = CustomProgress
                    .show(this, message, true, null);
        } else {
            if (!mProgressDialog.isShowing()) {
                mProgressDialog.show();
            }
        }
    }

    public void hideProgress() {
        if (null != mProgressDialog && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

}
