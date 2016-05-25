package com.meishai.app.widget.wheel;

import java.util.List;

import android.content.Context;

/**
 *
 *
 */
public class ShiWheelAdapter implements WheelAdapter {
    private List<String> items_c;

    public ShiWheelAdapter(Context context, List<String> items_c) {
        this.items_c = items_c;
    }

    @Override
    public int getItemsCount() {
        return items_c == null ? 0 : items_c.size();
    }

    @Override
    public String getItem(int index) {
        if (index >= items_c.size()) {
            index = items_c.size() - 1;
        }
        return items_c.get(index);
    }

    @Override
    public int getMaximumLength() {
        return 0;
    }
}
