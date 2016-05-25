package com.meishai.app.widget.layout;


import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.meishai.R;
import com.meishai.entiy.SlideItem;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.NetworkImageView;
import com.meishai.util.AndroidUtil;
import com.meishai.util.ImageAdapter;
import com.meishai.util.SkipUtils;

public class AutoScrollLayout extends RelativeLayout {

    private final static int MSG_SCROLL_TOP = 100;

    private ViewPager mViewPager;
    private LinearLayout mDotLayout;

    private ImageView mImageDotGroup[];
    private NetworkImageView mImageTops[][];

    private TopAdapter mAdapter;

    private Handler mHandler;

    private boolean mIsStopByTouch = false;
    private boolean mIsAutoScroll = false;

    private boolean mStopScrollWhenTouch = true;
    private long mInterval = 5000;

    //private int 				mData[];
    private SlideItem mData[];

    private int mTestImages[] = new int[]{};

    private ImageLoader mImageLoader;
    private Context mContext;
    private int mScreenWidth;

    public AutoScrollLayout(Context context) {
        this(context, null);
    }

    public AutoScrollLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoScrollLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        View convertView = inflater.inflate(R.layout.top_auto_scroll, this, true);
        mViewPager = (ViewPager) convertView.findViewById(R.id.top_viewpager);
        mDotLayout = (LinearLayout) convertView.findViewById(R.id.top_dot_layout);

        mViewPager.getParent().requestDisallowInterceptTouchEvent(true);

        mHandler = new MyHandler();
        DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
        mScreenWidth = dm.widthPixels;
//		DebugLog.w("屏幕宽度:"+mScreenWidth);
    }


    public void setData(SlideItem data[], ImageLoader imageLoader) {
        if (mData == data || data.length == 0) {
            return;
        }

        mData = data;
        mImageLoader = imageLoader;

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int px = AndroidUtil.dip2px(8);
        lp.setMargins(px, px, 0, px);

        if (mData.length > 1) {
            mDotLayout.removeAllViews();
            mImageDotGroup = new ImageView[mData.length];
            for (int i = 0; i < mImageDotGroup.length; i++) {
                mImageDotGroup[i] = new ImageView(getContext());
                mImageDotGroup[i].setImageResource(R.drawable.dot);
                mImageDotGroup[i].setLayoutParams(lp);

                mDotLayout.addView(mImageDotGroup[i]);
            }
        }


        mImageTops = new NetworkImageView[2][mData.length];
        for (int i = 0; i < mImageTops.length; i++) {
            for (int j = 0; j < mImageTops[i].length; j++) {
                mImageTops[i][j] = new NetworkImageView(getContext());
                mImageTops[i][j].setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                mImageTops[i][j].setScaleType(ImageView.ScaleType.FIT_XY);

                mImageTops[i][j].setImageUrl(mData[j].image, mImageLoader);
                //mImageTops[i][j].setImageResource(mTestImages[j]);

//				DebugLog.d("image:" + mData[j].image);
//				mImageTops[i][j].setTag(mData[j].image);
//				ListImageListener listenerPic = new ListImageListener(
//						mImageTops[i][j], 0, 0, mData[j].image);
//				mImageLoader.get(mData[j].image, listenerPic);

                mImageTops[i][j].setTag(mData[j]);
                mImageTops[i][j].setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        SlideItem tag = (SlideItem) v.getTag();
                        SkipUtils.skip(mContext, tag.type, tag.value, tag.tempid, tag.h5data);

                    }
                });

            }
        }


        mAdapter = new TopAdapter();
        mViewPager.setAdapter(mAdapter);

        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                if (null != mImageDotGroup) {
                    int index = arg0 % mImageDotGroup.length;
                    for (int i = 0; i < mImageDotGroup.length; i++) {
                        if (index == i) {
                            mImageDotGroup[i].setEnabled(true);
                        } else {
                            mImageDotGroup[i].setEnabled(false);
                        }
                    }
                }

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

        mViewPager.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //DebugLog.d(event.toString());

                if (mStopScrollWhenTouch) {
                    if ((event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) && mIsAutoScroll) {
                        mIsStopByTouch = true;
                        stopAutoScroll();

                    } else if (event.getAction() == MotionEvent.ACTION_UP && mIsStopByTouch) {
                        startAutoScroll();
                    }
                }

                return false;

            }
        });

        //mViewPager.setCurrentItem(mAdapter.getCount() / 2 - (mAdapter.getCount() / 2 % mImageTops[0].length));

        reLayout();

        startAutoScroll();
    }

    private int getIdByString(String value) {
        int id = 0;
        try {
            id = Integer.parseInt(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    private void reLayout() {
        //现在图片的比例是750*380,大约是2:1的样子
//		int height = mScreenWidth/2;
        Point point = ImageAdapter.getViewRealWH(1, 0, 750, 380);

        LayoutParams lp = new LayoutParams(point.x, point.y);
        //lp.setMargins(0, AndroidUtil.dip2px(3), AndroidUtil.dip2px(4), 0);

        mViewPager.setLayoutParams(lp);
    }

    public void startAutoScroll() {
        if (mImageTops[0].length > 1) {
            mIsAutoScroll = true;
            sendScrollMessage(mInterval);
        }
    }

    public void stopAutoScroll() {
        if (mImageTops[0].length > 1) {
            mIsAutoScroll = false;
            mHandler.removeMessages(MSG_SCROLL_TOP);
        }
    }

    private void sendScrollMessage(long delayTimeInMills) {
        /** remove messages before, keeps one message is running at most **/
        mHandler.removeMessages(MSG_SCROLL_TOP);
        mHandler.sendEmptyMessageDelayed(MSG_SCROLL_TOP, delayTimeInMills);
    }


    private class TopAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            if (mImageTops[0].length > 1) {
                return Integer.MAX_VALUE;
            } else {
                return mImageTops[0].length;
            }

        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //super.destroyItem(container, position, object);
            container.removeView((View) object);
            object = null;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //return super.instantiateItem(container, position);
            View v = mImageTops[position / mImageTops[0].length % 2][position % mImageTops[0].length];
            ViewGroup parent = (ViewGroup) v.getParent();
            if (null != parent) {
                parent.removeView(v);
            }
            container.addView(v);
            return v;
        }

    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_SCROLL_TOP && mIsAutoScroll) {
                int index = mViewPager.getCurrentItem();
                mViewPager.setCurrentItem((index + 1) % Integer.MAX_VALUE);

                sendScrollMessage(mInterval);
            }
        }
    }

}
