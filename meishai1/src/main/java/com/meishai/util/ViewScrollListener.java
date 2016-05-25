package com.meishai.util;

import android.content.Context;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

abstract public class ViewScrollListener implements OnGestureListener {

    private static final int FLING_MIN_DISTANCE = 20;    // 滑动最小距离
    private static final int FLING_MIN_VELOCITY = 200;    // 滑动最小速度

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (e1 == null || e2 == null) {
            return false;
        }

        if (e1.getY() - e2.getY() > FLING_MIN_DISTANCE) {
            // && Math.abs(velocityX) > FLING_MIN_VELOCITY) {

            // 手指向上滑动 , listView向下滑动
            onFlingUp(e1, e2, velocityX, velocityY);

        } else if (e2.getY() - e1.getY() > FLING_MIN_DISTANCE) {
            //&& Math.abs(velocityX) > FLING_MIN_VELOCITY) {
            // 向下滑动, listView向上滑动
            onFlingDown(e1, e2, velocityX, velocityY);

        }

        return false;
    }


    abstract public void onFlingUp(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY);

    abstract public void onFlingDown(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY);
}
