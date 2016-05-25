package com.meishai.ui.fragment.tryuse;

import android.content.Context;
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
import android.widget.ImageButton;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.entiy.TryInfo.TryInfoType;
import com.meishai.ui.sliding.PagerSlidingTabStrip;
import com.meishai.ui.sliding.PagerSlidingTabStripHelper;

/**
 * 试用主界面
 *
 * @author sh
 */
public class TryUseActivity extends FragmentActivity implements OnClickListener {
    private Context mContext = TryUseActivity.this;
    private String title = "";
    private PagerSlidingTabStrip tabs;
    private Button backMain;
    private ImageButton btn_cate;
    // private TryChildFragment tryAllFragment;
    // private TryChildFragment tryJpFragment;
    private TryChildFragment tryTaskFragment;
    private TryChildFragment tryHbragment;

    private String source = "";

    public static Intent newIntent(String title) {
        Intent intent = new Intent(GlobalContext.getInstance(),
                TryUseActivity.class);
        intent.putExtra("title", title);
        return intent;
    }

    public static Intent newHbIntent() {
        Intent intent = new Intent(GlobalContext.getInstance(),
                TryUseActivity.class);
        intent.putExtra("source", "hb");
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tryuse);
        try {
            Bundle bundle = getIntent().getExtras();
            if (null != bundle) {
                title = bundle.getString("title");
                source = bundle.getString("source");
            }
        } catch (Exception e) {

        }

        this.initView();
        this.setTabsValue();
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
        ViewPager pager = (ViewPager) this.findViewById(R.id.try_pager);
        tabs = (PagerSlidingTabStrip) this.findViewById(R.id.try_tabs);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        if (TextUtils.equals(source, "hb")) {
            pager.setCurrentItem(3);
        }

        tabs.setViewPager(pager);
        btn_cate = (ImageButton) this.findViewById(R.id.btn_cate);
        btn_cate.setOnClickListener(this);
    }

    private void setTabsValue() {
        PagerSlidingTabStripHelper.setTabsValue(tabs);
    }

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
            switch (position) {
                // case 0:
                // if (null == tryAllFragment) {
                // tryAllFragment = TryChildFragment
                // .newInstance(TryInfoType.TRY_ALL.getType());
                // }
                // return tryAllFragment;
                // case 1:
                // if (null == tryJpFragment) {
                // tryJpFragment = TryChildFragment
                // .newInstance(TryInfoType.TRY_JP.getType());
                // }
                // return tryJpFragment;
                case 0:
                    if (null == tryTaskFragment) {
                        tryTaskFragment = TryChildFragment
                                .newInstance(TryInfoType.TRY_TASK.getType());
                    }
                    return tryTaskFragment;
                case 1:
                    if (null == tryHbragment) {
                        tryHbragment = TryChildFragment
                                .newInstance(TryInfoType.TRY_HB.getType());
                    }
                    return tryHbragment;
                default:
                    return null;
            }
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.btn_cate:
                intent.setClass(mContext, TryUseCateActivity.class);
                startActivity(intent);
                break;

            default:
                break;
        }

    }

}
