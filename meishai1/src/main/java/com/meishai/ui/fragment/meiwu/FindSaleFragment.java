package com.meishai.ui.fragment.meiwu;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.meishai.R;
import com.meishai.entiy.Bargains.BargainsType;
import com.meishai.ui.base.BaseFragment;
import com.meishai.ui.sliding.PagerSlidingTabStrip;

/**
 * 发现->特价
 *
 * @author sh
 */
public class FindSaleFragment extends BaseFragment implements OnClickListener {

    private View view;
    private Button mBtnBack;
    private ImageButton mBtnCate;
    /**
     * 全部
     */
    private FindSaleChildFragment allFragment;
    /**
     * 9块9包邮
     */
    private FindSaleChildFragment cate9Fragment;
    /**
     * 20元封顶
     */
    private FindSaleChildFragment cate20Fragment;
    /**
     *  精品折扣
     */
    private FindSaleChildFragment discountFragment;
    private PagerSlidingTabStrip tabs;
    private DisplayMetrics dm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.find_sale, null);
        dm = getResources().getDisplayMetrics();
        this.initView();
        this.setTabsValue();
        return view;
    }

    private void initView() {
        mBtnBack = (Button) view.findViewById(R.id.backMain);
        mBtnBack.setOnClickListener(this);
        mBtnCate = (ImageButton) view.findViewById(R.id.btn_cate);
        mBtnCate.setOnClickListener(this);
        ViewPager pager = (ViewPager) view.findViewById(R.id.sale_pager);
        tabs = (PagerSlidingTabStrip) view.findViewById(R.id.sale_tabs);
        pager.setAdapter(new MyPagerAdapter(getChildFragmentManager()));
        tabs.setViewPager(pager);
    }

    private void setTabsValue() {
        tabs.setShouldExpand(true);
        tabs.setDividerColor(Color.TRANSPARENT);
        tabs.setUnderlineHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 0.5f, dm));
        tabs.setIndicatorHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 2, dm));
        tabs.setTextSize((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 14, dm));
        tabs.setIndicatorColor(getResources().getColor(R.color.title_bg));
        tabs.setSelectedTextColor(getResources().getColor(R.color.title_bg));
        tabs.setTabBackground(0);
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private final String[] titles = BargainsType.getAllTypeRemark();

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
                    if (null == allFragment) {
                        allFragment = FindSaleChildFragment
                                .newInstance(BargainsType.TYPE_ALL.getType());
                    }
                    return allFragment;
                case 1:
                    if (null == cate9Fragment) {
                        cate9Fragment = FindSaleChildFragment
                                .newInstance(BargainsType.TYPE_9k9.getType());
                    }
                    return cate9Fragment;
                case 2:
                    if (null == cate20Fragment) {
                        cate20Fragment = FindSaleChildFragment
                                .newInstance(BargainsType.TYPE_20.getType());
                    }
                    return cate20Fragment;
                case 3:
                    if (null == discountFragment) {
                        discountFragment = FindSaleChildFragment
                                .newInstance(BargainsType.TYPE_DIS.getType());
                    }
                    return discountFragment;
                default:
                    return null;
            }
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.backMain:
                backToMain();
                break;
            case R.id.btn_cate:
                intent.setClass(mContext, FindSaleCateActivity.class);
                startActivity(intent);
                // gotoFragment(FindSaleCateFragment.class,
                // FindSaleCateFragment.class.getName(), null);
                break;
            default:
                break;
        }
    }

    protected void backToMain() {
        doBackClick();
    }
}
