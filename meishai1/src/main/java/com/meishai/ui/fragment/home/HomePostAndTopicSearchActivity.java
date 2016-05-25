package com.meishai.ui.fragment.home;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.meishai.R;
import com.meishai.app.widget.EditTextWithDel;
import com.meishai.ui.fragment.home.HomePostSearchFragment.OnPostSearchListener;
import com.meishai.ui.fragment.home.HomeTopicSearchFragment.OnTopicSearchListener;
import com.meishai.ui.fragment.home.HomeUserSearchFragment.OnUserSearchListener;
import com.meishai.ui.sliding.PagerSlidingTabStrip;
import com.meishai.ui.sliding.PagerSlidingTabStripHelper;
import com.meishai.util.AndroidUtil;

/**
 * 晒晒 搜索帖子和话题
 *
 * @author sh
 */
public class HomePostAndTopicSearchActivity extends FragmentActivity implements
        OnClickListener {

    private Context mContext = HomePostAndTopicSearchActivity.this;
    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private Button mBtnCancel;
    private EditTextWithDel mSearchText;

    // 帖子搜索
    private HomePostSearchFragment postFragment;
    // 话题搜索
    private HomeTopicSearchFragment topicFragment;
    // 会员搜索
    private HomeUserSearchFragment userFragment;

    private OnPostSearchListener postListerner;
    private OnTopicSearchListener topicListener;
    private OnUserSearchListener userListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_post_topic_search);
        initView();
        initFragment();
    }

    private void initView() {
        mBtnCancel = (Button) this.findViewById(R.id.btn_cancel);
        mBtnCancel.setOnClickListener(this);
        mSearchText = (EditTextWithDel) this.findViewById(R.id.search_text);
        mSearchText.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    loadData();
                    AndroidUtil.hideSoftInput(mContext);
                }
                return false;
            }
        });
        pager = (ViewPager) findViewById(R.id.pager);
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        pager.setOffscreenPageLimit(2);
        tabs.setViewPager(pager);
        PagerSlidingTabStripHelper.setTabsValue(tabs);
    }

    private void initFragment() {
        if (null == postFragment) {
            postFragment = new HomePostSearchFragment();
            postListerner = postFragment.getListener();
        }
        if (null == topicFragment) {
            topicFragment = new HomeTopicSearchFragment();
            topicListener = topicFragment.getListener();
        }
        if (null == userFragment) {
            userFragment = new HomeUserSearchFragment();
            userListener = userFragment.getListener();
        }
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private final String[] titles = {"帖子", "话题", "会员"};

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
                    return postFragment;
                case 1:
                    return topicFragment;
                case 2:
                    return userFragment;
                default:
                    return null;
            }
        }
    }

    private void loadData() {
        String kword = mSearchText.getText().toString();
        postListerner.onPostSearch(kword);
        topicListener.onTopicSearch(kword);
        userListener.onUserSearch(kword);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                finish();
                break;
            default:
                break;
        }
    }

    // public interface OnSearchListener {
    // public void onSearch(String keyword);
    // }
}
