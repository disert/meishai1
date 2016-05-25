package com.meishai.ui.fragment.usercenter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.ui.sliding.PagerSlidingTabStrip;
import com.meishai.ui.sliding.PagerSlidingTabStripHelper;

/**
 * 我的->我的收藏
 *
 * @author sh
 */
public class UserFavActivity extends FragmentActivity implements
        OnClickListener {

    // private View mFavView;

    private Button mBtnBack;

    /**
     * 帖子
     */
    private UserFavPostFragment postFragment;
    /**
     * 福利
     */
    private UserFavWelfareFragment welfareFragment;
    /**
     * 试用
     */
    private UserFavTryFragment tryFragment;
    private PagerSlidingTabStrip tabs;
    private ViewPager pager;

    public static Intent newIntent() {
        Intent intent = new Intent(GlobalContext.getInstance(),
                UserFavActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_fav);
        initView();
        setTabsValue();
    }

    // @Override
    // public View onCreateView(LayoutInflater inflater, ViewGroup container,
    // Bundle savedInstanceState) {
    // mFavView = inflater.inflate(R.layout.user_fav, null);
    // initView();
    // setTabsValue();
    // return mFavView;
    // }

    private void initView() {
        pager = (ViewPager) this.findViewById(R.id.pager);
        tabs = (PagerSlidingTabStrip) this.findViewById(R.id.tabs);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        tabs.setViewPager(pager);
        mBtnBack = (Button) this.findViewById(R.id.backMain);
        mBtnBack.setOnClickListener(this);
    }

    private void setTabsValue() {
        PagerSlidingTabStripHelper.setTabsValue(tabs);
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private final String[] titles = {"帖子", "福利", "试用"};

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
                    if (postFragment == null) {
                        postFragment = new UserFavPostFragment();
                    }
                    return postFragment;
                case 1:
                    if (welfareFragment == null) {
                        welfareFragment = new UserFavWelfareFragment();
                    }
                    return welfareFragment;
                case 2:
                    if (tryFragment == null) {
                        tryFragment = new UserFavTryFragment();
                    }
                    return tryFragment;
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
            default:
                break;
        }
    }

}
