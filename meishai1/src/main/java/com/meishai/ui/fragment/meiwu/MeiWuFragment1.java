package com.meishai.ui.fragment.meiwu;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.app.widget.EditTextWithDel;
import com.meishai.entiy.CateResqData;
import com.meishai.net.volley.Response;
import com.meishai.net.volley.VolleyError;
import com.meishai.ui.base.BaseFragment;
import com.meishai.ui.fragment.meiwu.req.MeiWuReq;
import com.meishai.ui.sliding.PagerSlidingTabStrip;
import com.meishai.util.DebugLog;
import com.meishai.util.GsonHelper;

/**
 * 美物界面 2.5.0
 *
 * @author yl
 */
public class MeiWuFragment1 extends BaseFragment {

    private View view;
    private TextView mTitle;
    private PagerSlidingTabStrip mTabs;
    private DisplayMetrics dm;
    private ViewPager mPager;
    private MyPagerAdapter mAdapter;

    private EditTextWithDel mSearch;

    private CateResqData mData;//顶部分类栏的数据
    private MeiWuItemFragment[] mFragments;//所有的导航下的fragment

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.meiwu, null);
        dm = getResources().getDisplayMetrics();
        this.initView();
        setTabsValue();
        getRequestData();
        return view;
    }

    private void getRequestData() {
        showProgress("", "加载中..");
        sendMsg(MeiWuReq.meiwuUrl());
    }

    @Override
    public void failt(Object ojb) {
        hideProgress();
    }

    @Override
    public void updateUI(Object obj) {
        if (obj == null || TextUtils.isEmpty(obj.toString())) return;
        String response = obj.toString();
        hideProgress();
        if (!TextUtils.isEmpty(response)) {
            mData = GsonHelper.parseObject(response, CateResqData.class);
            if (mData == null) {
                DebugLog.w("美物返回数据:" + response);
                return;
            }
            if (mData.success == 1) {
                mFragments = new MeiWuItemFragment[mData.list.size()];
                mAdapter.notifyDataSetChanged();
                mTabs.notifyDataSetChanged();
            }
        }
    }

    private void initView() {


        mTitle = (TextView) view.findViewById(R.id.title);
        mSearch = (EditTextWithDel) view.findViewById(R.id.search_et);
        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(SearchActivity.newIntent(SearchActivity.TYPE_MEIWU));
            }
        });
        mTabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        mPager = (ViewPager) view.findViewById(R.id.pager);
        mAdapter = new MyPagerAdapter(getChildFragmentManager());
//        mPager.setOffscreenPageLimit(2);
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
        mTabs.setTabPaddingLeftRight(30);
        mTabs.setIndicatorColor(getResources().getColor(R.color.title_bg));
        mTabs.setSelectedTextColor(getResources().getColor(R.color.title_bg));
        mTabs.setTextColor(0xff777777);
        mTabs.setTabBackground(0);
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
                    mFragments[position] = new MeiWuItemFragment();
                    mFragments[position].setArguments(args);
                }
                return mFragments[position];
            }
            return null;
        }
    }


}
