package com.meishai.ui.fragment.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.app.widget.LazyViewPager;
import com.meishai.app.widget.LazyViewPager.OnPageChangeListener;
import com.meishai.ui.base.BaseFragment;

/**
 * 晒晒主界面 2.0
 *
 * @author
 */
public class HomeFragment extends BaseFragment implements OnClickListener {

    private View mRootView;

    /**
     * 精选
     */
    private HandPickFragment careFragment;
    private TextView mBtnPick;
    private View mPickLine;
    /**
     * 发现
     */
//	private AttentFragment attentionFragment;
    private FindFragment mFindFragment;
    private TextView mBtnAtt;
    private View mAttLine;


    private ImageView ib_cate;

    // 0:表示当前选中的为 精选 1:表示当前选中的为 关注
    private int currentCheck;

    private ViewPager mPager;

    private MyPagerAdapter mAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (null == mRootView) {
            mRootView = inflater.inflate(R.layout.home1, container, false);
            initView(mRootView);
//			initData();
        }
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (null != parent) {
            parent.removeView(mRootView);
        }

        return mRootView;
    }


    private void initView(View v) {
        mBtnPick = (TextView) v.findViewById(R.id.btn_pick);
        mPickLine = v.findViewById(R.id.pick_line);
        mBtnPick.setOnClickListener(this);

        mBtnAtt = (TextView) v.findViewById(R.id.btn_attent);
        mAttLine = v.findViewById(R.id.attent_line);
        mBtnAtt.setOnClickListener(this);

        ib_cate = (ImageView) v.findViewById(R.id.ib_cate);
        ib_cate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // 分类页面
                Intent intent = new Intent(mContext, HomeCateActivity1.class);
                startActivity(intent);
            }
        });
        mPager = (ViewPager) v.findViewById(R.id.pager);
        mAdapter = new MyPagerAdapter(getChildFragmentManager());
        mPager.setAdapter(mAdapter);

        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int page) {
                if (currentCheck == 0) {
                    mBtnAtt.setTextColor(mContext.getResources().getColor(
                            R.color.txt_save));
                    mAttLine.setVisibility(View.VISIBLE);
                    mBtnPick.setTextColor(mContext.getResources().getColor(
                            R.color.white));
                    mPickLine.setVisibility(View.GONE);
                    currentCheck = 1;
                } else if (currentCheck == 1) {
                    mBtnPick.setTextColor(mContext.getResources().getColor(
                            R.color.txt_save));
                    mPickLine.setVisibility(View.VISIBLE);
                    mBtnAtt.setTextColor(mContext.getResources().getColor(
                            R.color.white));
                    mAttLine.setVisibility(View.GONE);
                    currentCheck = 0;
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ing:
                if (currentCheck == 0) {
                    return;
                }
                mPager.setCurrentItem(0);
                break;
            case R.id.btn_over:
                if (currentCheck == 1) {
                    return;
                }
                mPager.setCurrentItem(1);
                break;
            case R.id.btn_attent:
            case R.id.btn_pick:
                if (currentCheck == 0) {
                    mBtnAtt.setTextColor(mContext.getResources().getColor(
                            R.color.txt_save));
                    mAttLine.setVisibility(View.VISIBLE);
                    mBtnPick.setTextColor(mContext.getResources().getColor(
                            R.color.white));
                    mPickLine.setVisibility(View.GONE);
                    mPager.setCurrentItem(1);
                    currentCheck = 1;
                } else if (currentCheck == 1) {
                    mBtnPick.setTextColor(mContext.getResources().getColor(
                            R.color.txt_save));
                    mPickLine.setVisibility(View.VISIBLE);
                    mBtnAtt.setTextColor(mContext.getResources().getColor(
                            R.color.white));
                    mAttLine.setVisibility(View.GONE);
                    mPager.setCurrentItem(0);
                    currentCheck = 0;
                }
                break;
            default:
                break;
        }
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (mPager != null) {
                mPager.setCurrentItem(currentCheck);
            }
        } else {
            // 相当于Fragment的onPause
        }
    }

    @Override
    public void failt(Object ojb) {

    }

    @Override
    public void updateUI(Object obj) {

    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private final String[] titles = {"精选", "发现"};

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
                    if (careFragment == null) {
                        careFragment = new HandPickFragment();
                    }
                    return careFragment;
                case 1:

                    if (mFindFragment == null) {
                        mFindFragment = new FindFragment();
                        Bundle args = new Bundle();
                        args.putInt("type", AttentFragment.TYPE_ATTENTION);
                        mFindFragment.setArguments(args);
                    }
                    return mFindFragment;
                default:
                    return null;
            }
        }
    }
}
