package com.meishai.ui.fragment.meiwu;

import org.json.JSONException;
import org.json.JSONObject;

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
import android.view.View.OnClickListener;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.app.widget.CustomProgress;
import com.meishai.entiy.SpecialCateRespData;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.ui.fragment.meiwu.req.MeiWuReq;
import com.meishai.ui.sliding.PagerSlidingTabStrip;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;
import com.meishai.util.GsonHelper;

/**
 * 美物-品质专场(专场列表) 2.0
 *
 * @author yl
 */
public class MeiWuSpecialListActivity extends FragmentActivity {
    private PagerSlidingTabStrip mTabs;
    private DisplayMetrics dm;
    private ViewPager mPager;
    private MyPagerAdapter mAdapter;
    private CustomProgress mProgressDialog;
    private MeiWuSpecialListFragment mFragments[];

    private int mCid;
    private SpecialCateRespData mData;
    private int mPage = 1;

    public static Intent newIntent(int cid) {
        Intent intent = new Intent(GlobalContext.getInstance().getApplicationContext(), MeiWuSpecialListActivity.class);
        intent.putExtra("cid", cid);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meiwu_special_list);
        dm = getResources().getDisplayMetrics();
        this.initView();
//		getRequestData(1);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mData == null) {
            getRequestData(mPage);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void initView() {


        findViewById(R.id.backMain).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        mPager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mAdapter);
        mTabs.setViewPager(mPager);
        setTabsValue();

        mCid = getIntent().getIntExtra("cid", 0);
    }

    protected void getRequestData(int page) {
        //6
        MeiWuReq.specialCateReq(new Listener<String>() {

            @Override
            public void onResponse(String response) {
                checkRespData(response);
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                AndroidUtil.showToast(R.string.reqFailed);
            }
        });
    }

    protected void checkRespData(String response) {
        try {
            JSONObject obj = new JSONObject(response);
            int success = obj.getInt("success");
            if (success == 1) {
                mData = GsonHelper.parseObject(response, SpecialCateRespData.class);

                mFragments = new MeiWuSpecialListFragment[mData.cate.size()];


                mTabs.notifyDataSetChanged();
                mAdapter.notifyDataSetChanged();
            } else {
                AndroidUtil.showToast(R.string.reqFailed);

            }

        } catch (JSONException e) {
            DebugLog.e(e.getMessage());
            e.printStackTrace();
        }
    }

    private void setTabsValue() {
        mTabs.setShouldExpand(true);
        mTabs.setDividerColor(Color.TRANSPARENT);
        mTabs.setUnderlineHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 0.5f, dm));
        mTabs.setIndicatorHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 2, dm));
        mTabs.setTextSize((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 14, dm));
        mTabs.setIndicatorColor(getResources().getColor(R.color.title_bg));
        mTabs.setSelectedTextColor(getResources().getColor(R.color.title_bg));
        mTabs.setTabBackground(0);
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

    public class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public CharSequence getPageTitle(int position) {
            if (mData == null || mData.cate == null) {
                return "全部";
            }
            return mData.cate.get(position).catname;
        }

        @Override
        public int getCount() {
            if (mData == null || mData.cate == null) {
                return 0;
            }
            return mData.cate.size();
        }

        @Override
        public Fragment getItem(int position) {
            MeiWuSpecialListFragment fragment;
            if (mFragments[position] == null) {
                fragment = new MeiWuSpecialListFragment();
            } else {
                fragment = mFragments[position];
            }
            Bundle args = new Bundle();
            args.putInt("cid", mData.cate.get(position).cid);
            fragment.setArguments(args);
            return fragment;
        }
    }


}
