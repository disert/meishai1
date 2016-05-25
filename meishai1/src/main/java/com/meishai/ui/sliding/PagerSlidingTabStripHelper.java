package com.meishai.ui.sliding;

import android.content.res.Resources;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.meishai.GlobalContext;
import com.meishai.R;

/**
 * PagerSlidingTabStrip 辅助类
 *
 * @author sh
 */
public class PagerSlidingTabStripHelper {

    public static void setTabsValue(PagerSlidingTabStrip tabs) {
        Resources resources = GlobalContext.getInstance().getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        tabs.setShouldExpand(true);
        tabs.setDividerColor(Color.TRANSPARENT);
        tabs.setUnderlineHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 0.5f, dm));
        tabs.setIndicatorHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 2, dm));
        tabs.setTextSize((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 14, dm));
        tabs.setIndicatorColor(resources.getColor(R.color.title_bg));
        tabs.setSelectedTextColor(resources.getColor(R.color.title_bg));
        tabs.setTabBackground(0);
    }
}
