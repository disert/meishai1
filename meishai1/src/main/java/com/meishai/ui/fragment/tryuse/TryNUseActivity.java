package com.meishai.ui.fragment.tryuse;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.entiy.TryInfo.TryInfoType;
import com.meishai.entiy.TryInfoMore;
import com.meishai.ui.fragment.MeishaiWebviewActivity;
import com.meishai.ui.fragment.tryuse.TryNUseFragment.UiRefreshListener;

/**
 * 试用新界面
 *
 * @author sh
 */
public class TryNUseActivity extends FragmentActivity implements OnClickListener {

    private ViewPager pager;
    private Button backMain;
    private TryNChildFragment tryIngFragment;
    private TryNChildFragment tryOverragment;

    // 进行中
    private Button btn_ing;
    private View ing_line;
    // 已过期
    private Button btn_over;
    private View over_line;

    private Button mBtnMore;
    // 0:表示当前选中的为 进行中 1:表示当前选中的为 已过期
    private int currentCheck = 0;

    private String title = "";

    public static Intent newIntent(String title) {
        Intent intent = new Intent(GlobalContext.getInstance(),
                TryNUseActivity.class);
        intent.putExtra("title", title);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tryuse_n);
        try {
            Bundle bundle = getIntent().getExtras();
            if (null != bundle) {
                title = bundle.getString("title");
            }
        } catch (Exception e) {

        }
        this.initView();
    }

    private void initView() {
        backMain = (Button) this.findViewById(R.id.backMain);
        if (!TextUtils.isEmpty(title)) {
            backMain.setText(title);
        }
        backMain.setVisibility(View.VISIBLE);
        backMain.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        pager = (ViewPager) this.findViewById(R.id.try_pager);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        pager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int page) {
                if (currentCheck == 0) {
                    btn_over.setTextColor(getResources().getColor(
                            R.color.txt_save));
                    over_line.setVisibility(View.VISIBLE);
                    btn_ing.setTextColor(getResources().getColor(
                            R.color.white));
                    ing_line.setVisibility(View.GONE);
                    currentCheck = 1;
                } else if (currentCheck == 1) {
                    btn_ing.setTextColor(getResources().getColor(
                            R.color.txt_save));
                    ing_line.setVisibility(View.VISIBLE);
                    btn_over.setTextColor(getResources().getColor(
                            R.color.white));
                    over_line.setVisibility(View.GONE);
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
        btn_ing = (Button) this.findViewById(R.id.btn_ing);
        ing_line = this.findViewById(R.id.ing_line);
        btn_ing.setOnClickListener(this);
        btn_over = (Button) this.findViewById(R.id.btn_over);
        over_line = this.findViewById(R.id.over_line);
        btn_over.setOnClickListener(this);
        mBtnMore = (Button) this.findViewById(R.id.btn_more);
    }

    private void updateUI(TryInfoMore more) {
        if (null != more) {
            if (more.getIsmore() == 1) {
                mBtnMore.setText(more.getText());
                mBtnMore.setVisibility(View.VISIBLE);
            } else {
                mBtnMore.setVisibility(View.GONE);
            }

            if (more.getIswap() == 1) {
                // 跳转wap页面
                mBtnMore.setTag(more.getUrl());
                mBtnMore.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        startActivity(MeishaiWebviewActivity.newIntent(v
                                .getTag().toString()));
                    }
                });
            } else {
                mBtnMore.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        startActivity(TryUseActivity
                                .newIntent(getString(R.string.tryuse)));
                    }
                });
            }
        }
    }

    private boolean isRefresh = false;

    public class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private final String[] titles = TryInfoType.getAllTypeRemark();

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
            UiRefreshListener refreshListener = new UiRefreshListener() {

                @Override
                public void refreshUI(TryInfoMore more) {
                    if (!isRefresh) {
                        isRefresh = true;
                        updateUI(more);
                    }

                }
            };
            // status=1为进行中；status=0为已结束
            switch (position) {
                case 0:
                    if (null == tryIngFragment) {
                        tryIngFragment = TryNChildFragment.newInstance("1", true);
                        tryIngFragment.setUiRefreshListener(refreshListener);
                    }
                    return tryIngFragment;
                case 1:
                    if (null == tryOverragment) {
                        tryOverragment = TryNChildFragment.newInstance("0", false);
                        tryOverragment.setUiRefreshListener(refreshListener);
                    }
                    return tryOverragment;
                default:
                    return null;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ing:
                if (currentCheck == 0) {
                    return;
                }
                pager.setCurrentItem(0);
                break;
            case R.id.btn_over:
                if (currentCheck == 1) {
                    return;
                }
                pager.setCurrentItem(1);
                break;
            default:
                break;
        }
    }

    // @Override
    // public void onHiddenChanged(boolean hidden) {
    // if (null != tryIngFragment) {
    // tryIngFragment.onHiddenChanged(hidden);
    // }
    // if (null != tryOverragment) {
    // tryOverragment.onHiddenChanged(hidden);
    // }
    // super.onHiddenChanged(hidden);
    // }


}
