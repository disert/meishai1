package com.meishai.ui.fragment.home;

import android.content.Intent;
import android.graphics.Color;
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
import com.meishai.entiy.CateResponseData;
import com.meishai.entiy.CateResqData;
import com.meishai.net.volley.Response;
import com.meishai.net.volley.VolleyError;
import com.meishai.ui.base.BaseActivity;
import com.meishai.ui.fragment.home.req.HomeReq;
import com.meishai.ui.fragment.meiwu.MeiWuItemFragment;
import com.meishai.ui.fragment.meiwu.req.MeiWuReq;
import com.meishai.ui.fragment.usercenter.CollectedPostFragment;
import com.meishai.ui.fragment.usercenter.CollectedSKUFragment;
import com.meishai.ui.fragment.usercenter.CollectedStratFragment;
import com.meishai.ui.sliding.PagerSlidingTabStrip;
import com.meishai.util.DebugLog;
import com.meishai.util.GsonHelper;

/**
 * 文件名：
 * 描    述：主页 - 热门活动
 * 作    者：
 * 时    间：2016/2/20
 * 版    权：
 */
public class HotActionActivity extends FragmentActivity {
    private TextView mTitle;
    private PagerSlidingTabStrip mTabs;
    private DisplayMetrics dm;
    private ViewPager mPager;
    private MyPagerAdapter mAdapter;

    private CustomProgress mProgressDialog;
    private CateResqData mData;
    private HotActionFragment[] mFragments;

    public static Intent newIntent() {
        Intent intent = new Intent(GlobalContext.getInstance(),
                HotActionActivity.class);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.collected_activity);
        dm = getResources().getDisplayMetrics();
        initView();
        setTabsValue();
        getRequestData();
    }


    private void initView() {
        findViewById(R.id.backMain).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTitle = (TextView) findViewById(R.id.title);
        mTitle.setText("精选美物");
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
                TypedValue.COMPLEX_UNIT_SP, 12, dm));
        mTabs.setTabPaddingLeftRight(26);
        mTabs.setIndicatorColor(getResources().getColor(R.color.title_bg));
        mTabs.setSelectedTextColor(getResources().getColor(R.color.title_bg));
        mTabs.setTextColor(0xff777777);
        mTabs.setTabBackground(0);
    }

    private void getRequestData() {
        showProgress("", "加载中..");
        HomeReq.hotActionNavi(this, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideProgress();
                if (!TextUtils.isEmpty(response)) {

                    mData = GsonHelper.parseObject(response, CateResqData.class);
                    if (mData == null) {
                        DebugLog.w("美物返回数据:" + response);
                        return;
                    }
                    if (mData.success == 1) {
                        mFragments = new HotActionFragment[mData.list.size()];
                        mAdapter.notifyDataSetChanged();
                        mTabs.notifyDataSetChanged();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
            }
        });
    }


    public class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return mData.list.get(position).catname;
        }

        @Override
        public int getCount() {
            if (mData == null) {
                return 0;
            }
            return mData.list.size();
        }

        @Override
        public long getItemId(int position) {
            return mData.list.get(position).cid;
        }

        @Override
        public Fragment getItem(int position) {
            Bundle args = new Bundle();
            args.putInt("cid", (int) getItemId(position));

            if (mFragments != null) {
                if (mFragments[position] == null) {
                    mFragments[position] = new HotActionFragment();
                    mFragments[position].setArguments(args);
                }
                return mFragments[position];
            }
            return null;
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
