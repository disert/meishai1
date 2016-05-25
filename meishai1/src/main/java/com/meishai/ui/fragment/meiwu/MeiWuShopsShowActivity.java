package com.meishai.ui.fragment.meiwu;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.app.widget.dragtop.DragTopLayout;
import com.meishai.entiy.ShopsDetailResqData;
import com.meishai.net.VolleyHelper;
import com.meishai.net.volley.toolbox.ListImageListener;
import com.meishai.ui.sliding.PagerSlidingTabStrip;


/**
 * 美物-好店详情 2015年12月4日14:44:46
 *
 * @author Administrator yl
 */
public class MeiWuShopsShowActivity extends FragmentActivity {


    private DragTopLayout mDragLayout;
    private LinearLayout mTopView;
    private ImageView mTopImage;
    private TextView mTopTitle;
    private TextView mTopSubTitle;
    private ViewPager mViewPager;
    private PagerSlidingTabStrip mTabs;
    private DisplayMetrics dm;

    private ShopsListFragment listFragment;
    private WebViewFragment webViewFragment;
    private MyPageAdapter mAdapter;

    private ShopsDetailResqData mData;
    private ShopsListFragment.DataRefreshListener mDataRefreshListener = new ShopsListFragment.DataRefreshListener() {
        @Override
        public void dataChanged(ShopsDetailResqData data) {
            if (data != null) {
                mData = data;
                updateUI();
            }
        }
    };
    private int mTid;


    public static Intent newIntent(int tid) {
        Intent intent = new Intent(GlobalContext.getInstance(),
                MeiWuShopsShowActivity.class);
        intent.putExtra("tid", tid);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meiwu_shops_show_activity);
        dm = getResources().getDisplayMetrics();
        mTid = getIntent().getIntExtra("tid", 0);
        initView();


    }

    private void initView() {
        findViewById(R.id.backMain).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mDragLayout = (DragTopLayout) findViewById(R.id.drag_layout);
        //顶部view
        mTopView = (LinearLayout) findViewById(R.id.top_view);
        mTopImage = (ImageView) findViewById(R.id.image);
        mTopTitle = (TextView) findViewById(R.id.big_title);
        mTopSubTitle = (TextView) findViewById(R.id.small_title);

        //content view
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mTabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);

        mAdapter = new MyPageAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);

        mTabs.setViewPager(mViewPager);
        setTabsValue();

    }


    private void updateUI() {

        mTopTitle.setText(mData.topicdata.title);
        mTopSubTitle.setText(mData.topicdata.subtitle);
        mTopImage.setTag(mData.topicdata.image);
        ListImageListener listener = new ListImageListener(mTopImage,
                R.drawable.place_default, R.drawable.place_default,
                mData.topicdata.image);
        VolleyHelper.getImageLoader(this).get(mData.topicdata.image, listener);

//        Bundle args = new Bundle();
//        args.putParcelable("urldata",mData.urldata);
//        webViewFragment.setArguments(args);
        webViewFragment.setUrlData(mData.urldata);

    }


    public class MyPageAdapter extends FragmentPagerAdapter {

        private String[] titles = {"全部商品", "商家介绍"};

        public MyPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public Fragment getItem(int position) {

            Bundle args = new Bundle();
            switch (position) {
                case 0:// 全部商品
                    if (listFragment == null) {
                        args.putInt("tid", mTid);
                        listFragment = new ShopsListFragment();
                        listFragment.setDataRefreshListener(mDataRefreshListener);
                        listFragment.setArguments(args);
                    }
                    return listFragment;
                case 1:// 商家介绍
                    if (webViewFragment == null) {
                        webViewFragment = new WebViewFragment();
                        if (mData != null) {
                            args.putParcelable("urldata", mData.urldata);
                        }
                        webViewFragment.setArguments(args);
                    }
                    return webViewFragment;
                default:
                    return null;
            }
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
                TypedValue.COMPLEX_UNIT_SP, 10, dm));
        mTabs.setIndicatorColor(getResources().getColor(R.color.title_bg));
        mTabs.setSelectedTextColor(getResources().getColor(R.color.title_bg));
        mTabs.setTextColor(0xff777777);
        mTabs.setTabBackground(0);
    }

    public ShopsDetailResqData getData() {
        return mData;
    }
}
