package com.meishai.ui.fragment.meiwu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.entiy.PointExchange.PointExchangeType;
import com.meishai.ui.fragment.MeishaiWebviewActivity;
import com.meishai.ui.sliding.PagerSlidingTabStrip;
import com.meishai.ui.sliding.PagerSlidingTabStripHelper;

/**
 * 发现->积分
 *
 * @author sh
 */
public class FindPointActivity extends FragmentActivity implements
        OnClickListener {

    private Button mBtnBack;
    private Button mEranPoints;
    private PagerSlidingTabStrip tabs;
    private String title = "";

    // 全部福利
    private FindPointChildFragment allFragment;
    // 幸运抽奖
    private FindPointChildFragment luckFragment;
    // 疯狂竞拍
    private FindPointChildFragment crazyFragment;
    // 积分兑换
    private FindPointChildFragment exchangeFragment;

    public static Intent newIntent(String title) {
        Intent intent = new Intent(GlobalContext.getInstance(),
                FindPointActivity.class);
        intent.putExtra("title", title);
        return intent;
    }

    public static Intent newIntent() {
        Intent intent = new Intent(GlobalContext.getInstance(),
                FindPointActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_point);
        if (null != getIntent().getExtras()) {
            title = getIntent().getExtras().getString("title");
        }
        this.initView();
        this.setTabsValue();
    }

    private void initView() {
        mBtnBack = (Button) this.findViewById(R.id.backMain);
        mBtnBack.setOnClickListener(this);
        if (!TextUtils.isEmpty(title)) {
            mBtnBack.setText(title);
        }
        mEranPoints = (Button) this.findViewById(R.id.eran_points);
        mEranPoints.setOnClickListener(this);
        ViewPager pager = (ViewPager) this.findViewById(R.id.point_pager);
        tabs = (PagerSlidingTabStrip) this.findViewById(R.id.point_tabs);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        pager.setOffscreenPageLimit(3);
        tabs.setViewPager(pager);
    }

    private void setTabsValue() {
        PagerSlidingTabStripHelper.setTabsValue(tabs);
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private final String[] titles = PointExchangeType.getAllTypeRemark();

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
                    if (null == allFragment) {
                        allFragment = FindPointChildFragment.newInstance(
                                PointExchangeType.POINT_ALL.getType(), true);
                    }
                    return allFragment;
                case 1:
                    if (null == luckFragment) {
                        luckFragment = FindPointChildFragment.newInstance(
                                PointExchangeType.POINT_LUCK.getType(), false);
                    }
                    return luckFragment;
                case 2:
                    if (null == crazyFragment) {
                        crazyFragment = FindPointChildFragment.newInstance(
                                PointExchangeType.POINT_CRAZY.getType(), false);
                    }
                    return crazyFragment;
                case 3:
                    if (null == exchangeFragment) {
                        exchangeFragment = FindPointChildFragment.newInstance(
                                PointExchangeType.POINT_EXCHANGE.getType(), false);
                    }
                    return exchangeFragment;
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
            case R.id.eran_points:
                startActivity(MeishaiWebviewActivity
                        .newIntent("http://www.meishai.com/index.php?m=content&c=point&a=task"));
                // startActivity(FindEranPointActivity.newIntent(""));
                break;
            default:
                break;
        }
    }
}
