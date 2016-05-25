package com.meishai.app.widget.wheel;

import java.util.List;

import android.content.Context;

/**
 *
 *
 */
public class ShengWheelAdapter implements WheelAdapter {
    private List<String> items_p;

    public ShengWheelAdapter(Context context, List<String> items_p) {
        this.items_p = items_p;
    }

    @Override
    public int getItemsCount() {
        return items_p == null ? 0 : items_p.size();
    }

    @Override
    public String getItem(int index) {
        return items_p.get(index);
    }

    @Override
    public int getMaximumLength() {
        return 0;
    }

}
