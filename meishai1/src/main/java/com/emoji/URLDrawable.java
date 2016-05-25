package com.emoji;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.meishai.R;
import com.meishai.app.util.DensityUtils;

public class URLDrawable extends BitmapDrawable {
    View content;
    protected Drawable drawable;
    Context mContext;

    @SuppressWarnings("deprecation")
    public URLDrawable(View content, Context context) {
        mContext = context;
        this.content = content;
        this.setBounds(getDefaultImageBounds(context));
        drawable = context.getResources().getDrawable(R.drawable.transparent_background);
        drawable.setBounds(getDefaultImageBounds(context));
    }

    @Override
    public void draw(Canvas canvas) {
        if (drawable != null) {
            drawable.draw(canvas);
        }
    }

    public void refreshDrawable(Drawable iDrawable) {
        drawable = iDrawable;
        int px = DensityUtils.dp2px(mContext, 20);
        drawable.setBounds(new Rect(0, 0, px, px));
        content.requestLayout();
    }

    public Rect getDefaultImageBounds(Context context) {
//		Display display = ((Activity) context).getWindowManager()
//				.getDefaultDisplay();
//		int width = ScreenUtils.getScreenWidth(context) / 3;
//		int height = (int) (width * 3 / 4);
        int px = DensityUtils.dp2px(context, 20);
        Rect bounds = new Rect(0, 0, px, px);
        return bounds;
    }

}
