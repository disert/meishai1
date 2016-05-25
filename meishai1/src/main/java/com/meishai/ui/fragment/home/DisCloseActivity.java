package com.meishai.ui.fragment.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.app.widget.CustomProgress;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.CateResqData;
import com.meishai.net.volley.Response;
import com.meishai.net.volley.VolleyError;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.home.req.HomeReq;
import com.meishai.ui.fragment.meiwu.MeiWuShopsFragment;
import com.meishai.ui.fragment.meiwu.MeiWuSpecialFragment;
import com.meishai.ui.fragment.meiwu.MeiWuStratFragment;
import com.meishai.ui.fragment.usercenter.LoginActivity;
import com.meishai.ui.sliding.PagerSlidingTabStrip;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;
import com.meishai.util.GsonHelper;

/**
 * 文件名：
 * 描    述：主页 - 我要爆料
 * 作    者：
 * 时    间：2016/3/26
 * 版    权：
 */
public class DisCloseActivity extends FragmentActivity {

    public static final int DISCOLSE_TYPE_GOODS = 3;//爆料类型-商品爆料
    public static final int DISCOLSE_TYPE_STORE = 4;//爆料类型-商铺爆料
    private TextView mTitle;
    private PagerSlidingTabStrip mTabs;
    private DisplayMetrics dm;
    private ViewPager mPager;
    private MyPagerAdapter mAdapter;

    private CustomProgress mProgressDialog;
    private GoodsDiscloseFragment mGoodsDiscloseFragment;
    private GoodsDiscloseFragment mStoreDiscloseFragment;
    private TextView mOther;

    public static Intent newIntent() {
        Intent intent = new Intent(GlobalContext.getInstance(),
                DisCloseActivity.class);
        return intent;
    }

    private void registReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConstantSet.ACTION_NAME);
        registerReceiver(mChooseImageReceiver, filter);
    }

    private BroadcastReceiver mChooseImageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            AndroidUtil.showToast("收到广播");
            if (intent != null) {
                int currentItem = mPager.getCurrentItem();
                if (currentItem == 1) {
                    if (mStoreDiscloseFragment != null) {
                        mStoreDiscloseFragment.chooseImage(intent);
                    }
                } else if (currentItem == 0) {
                    if (mGoodsDiscloseFragment != null) {
                        mGoodsDiscloseFragment.chooseImage(intent);
                    }
                }


            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.collected_activity);
        dm = getResources().getDisplayMetrics();
        initView();
        setTabsValue();
        registReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mChooseImageReceiver);
    }

    private void initView() {
        findViewById(R.id.backMain).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTitle = (TextView) findViewById(R.id.title);
        mOther = (TextView) findViewById(R.id.other);
        mOther.setVisibility(View.VISIBLE);
        mOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MeiShaiSP.getInstance().getUserInfo().isLogin()) {
                    startActivity(LoginActivity.newIntent());
                } else
                    startActivity(MyDisCloseActivity.newIntent());
            }
        });
        mTitle.setText("真实爆料");

        mTabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        mPager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mPager.setOffscreenPageLimit(3);
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
                TypedValue.COMPLEX_UNIT_SP, 10, dm));
        mTabs.setTabPaddingLeftRight(26);
        mTabs.setIndicatorColor(getResources().getColor(R.color.title_bg));
        mTabs.setSelectedTextColor(getResources().getColor(R.color.title_bg));
        mTabs.setTextColor(0xff555555);
        mTabs.setTabBackground(0);
    }


    public class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private final String[] titles = {"商品爆料", "店铺爆料"};

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
                case 0:// 商铺爆料
                    if (mGoodsDiscloseFragment == null) {
                        mGoodsDiscloseFragment = new GoodsDiscloseFragment();
                        Bundle args = new Bundle();
                        args.putInt("DISCLOSE_TYPE", DISCOLSE_TYPE_GOODS);
                        mGoodsDiscloseFragment.setArguments(args);
                    }
                    return mGoodsDiscloseFragment;
                case 1:// 商铺爆料
                    if (mStoreDiscloseFragment == null) {
                        mStoreDiscloseFragment = new GoodsDiscloseFragment();
                        Bundle args = new Bundle();
                        args.putInt("DISCLOSE_TYPE", DISCOLSE_TYPE_STORE);
                        mStoreDiscloseFragment.setArguments(args);
                    }
                    return mStoreDiscloseFragment;
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
