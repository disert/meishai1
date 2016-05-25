/**
 */
package com.meishai.ui.fragment.usercenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.entiy.TrialInfo;
import com.meishai.ui.popup.TrialFilterPopupWindow;
import com.meishai.ui.sliding.PagerSlidingTabStrip;
import com.meishai.ui.sliding.PagerSlidingTabStripHelper;

/**
 * 我的试用
 *
 * @author sh
 */
public class UserTrialActivity extends FragmentActivity implements
        OnClickListener {

    // private View mTrialView;
    private Context mContext = UserTrialActivity.this;
    private LinearLayout lay_main;

    private Button mBtnBack;
    /**
     * 未通过
     */
    private UserTrialChildFragment noPassFragment;
    /**
     * 进行中
     */
    private UserTrialChildIngFragment trialingFragment;
    /**
     * 已完成
     */
    private UserTrialChildFragment finishFragment;
    private PagerSlidingTabStrip tabs;
    private ViewPager pager;

    /**
     * 筛选
     */
    private Button mBtnFilter;
    private TrialFilterPopupWindow filterPopupWindow;

    public static Intent newIntent() {
        Intent intent = new Intent(GlobalContext.getInstance(),
                UserTrialActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_trial);
        initView();
        setTabsValue();
        initPopup();
    }

    private void initView() {
        lay_main = (LinearLayout) this.findViewById(R.id.lay_main);
        pager = (ViewPager) this.findViewById(R.id.pager);
        tabs = (PagerSlidingTabStrip) this.findViewById(R.id.tabs);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        tabs.setViewPager(pager);
        pager.setOffscreenPageLimit(2);
        pager.setCurrentItem(1);
        mBtnBack = (Button) this.findViewById(R.id.backMain);
        mBtnBack.setOnClickListener(this);
        mBtnFilter = (Button) this.findViewById(R.id.btn_filter);
        mBtnFilter.setOnClickListener(this);
        tabs.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int page) {
                if (page == 1) {
                    mBtnFilter.setVisibility(View.VISIBLE);
                } else {
                    mBtnFilter.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
    }

    private void setTabsValue() {
        PagerSlidingTabStripHelper.setTabsValue(tabs);
    }

    private void initPopup() {
        // 未通过：0，进行中：1，已完成：2，订单待提交：100，报告待提交：200，担保金待申请：300
        filterPopupWindow = new TrialFilterPopupWindow(mContext);
        filterPopupWindow.setClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.lay_ing:
                        // 进行中
                        trialingFragment.setType(TrialInfo.TRIAL_ING);
                        break;
                    case R.id.lay_dtj:
                        // 订单待提交
                        trialingFragment.setType("100");
                        break;
                    case R.id.lay_bgdtj:
                        // 报告待提交
                        trialingFragment.setType("200");
                        break;
                    case R.id.lay_dbj:
                        // 担保金待申请
                        trialingFragment.setType("300");
                        break;
                    default:
                        break;
                }

            }
        });
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private final String[] titles = {"未通过", "进行中", "已完成"};

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
                    if (noPassFragment == null) {
                        noPassFragment = UserTrialChildFragment
                                .newInstance(TrialInfo.TRIAL_NO_PASS);
                    }
                    return noPassFragment;
                case 1:
                    if (trialingFragment == null) {
                        trialingFragment = UserTrialChildIngFragment
                                .newInstance(TrialInfo.TRIAL_ING);
                    }
                    return trialingFragment;
                case 2:
                    if (finishFragment == null) {
                        finishFragment = UserTrialChildFragment
                                .newInstance(TrialInfo.TRIAL_FINISH);
                    }
                    return finishFragment;
                default:
                    return null;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backMain:
                finish();
                break;
            case R.id.btn_filter:
                filterPopupWindow.showPop(lay_main);
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (null != trialingFragment) {
                trialingFragment
                        .onActivityResult(requestCode, resultCode, data);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
