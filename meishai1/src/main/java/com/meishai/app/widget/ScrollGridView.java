package com.meishai.app.widget;

import com.meishai.util.DebugLog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

/**
 * 不能滚动的gridView,为了与其他listview或者scrollView嵌套使用
 */
public class ScrollGridView extends GridView {

    public interface OnTouchInvalidPositionListener {
        /**
         * motionEvent 可使用 MotionEvent.ACTION_DOWN 或者 MotionEvent.ACTION_UP等来按需要进行判断
         *
         * @return 是否要终止事件的路由
         */
        boolean onTouchInvalidPosition(int motionEvent);
    }

    private OnTouchInvalidPositionListener mTouchInvalidPosListener;

    public ScrollGridView(Context context) {
        super(context);
    }

    public ScrollGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ScrollGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        // 为了让GridView在ScrollView下显示完全, 使GridView不显示滚动条
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);

//		setGridHeight();

        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    @SuppressLint("NewApi")
    private void setGridHeight() {
        int totalHeight = 0;
        int columnCount = getNumColumns();
        int rowCount = getChildCount();
        DebugLog.d("columnCount:" + columnCount);

        for (int i = 0; i < rowCount; i++) {
            View listItem = getChildAt(i);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams lp = getLayoutParams();
        lp.height = totalHeight + getVerticalSpacing() * (rowCount - 1);

    }


    /**
     * 点击空白区域时的响应和处理接口
     */
    public void setOnTouchInvalidPositionListener(OnTouchInvalidPositionListener listener) {
        mTouchInvalidPosListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (mTouchInvalidPosListener == null) {
            return super.onTouchEvent(event);
        }

        if (!isEnabled()) {
            // A disabled view that is clickable still consumes the touch
            // events, it just doesn't respond to them.
            return isClickable() || isLongClickable();
        }

        final int motionPosition = pointToPosition((int) event.getX(), (int) event.getY());


        if (motionPosition == INVALID_POSITION) {
            super.onTouchEvent(event);
            return mTouchInvalidPosListener.onTouchInvalidPosition(event.getActionMasked());
        }

        return super.onTouchEvent(event);
    }

}
