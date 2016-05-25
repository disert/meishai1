package com.meishai.app.widget.layout;


import android.content.Context;
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
import com.meishai.app.widget.RoundCornerImageView;
import com.meishai.entiy.HomeInfo.SlideInfo;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.ListImageListener;
import com.meishai.util.AndroidUtil;
import com.meishai.util.SkipUtils;

public class AutoScrollLayout1 extends RelativeLayout {

    private final static int MSG_SCROLL_TOP = 100;

    private ViewPager mViewPager;
    private TopAdapter mAdapter;

    private LinearLayout mDotLayout;//点的容器

    private ImageView mImageDotGroup[];
    private RoundCornerImageView mImageTops[][];


    private Handler mHandler;

    private boolean mIsStopByTouch = false;
    private boolean mIsAutoScroll = false;
    private boolean mStopScrollWhenTouch = true;
    private long mInterval = 5000;//延时间

    //private int 				mData[];
    private SlideInfo mData[];

    private int mTestImages[] = new int[]{};

    private ImageLoader mImageLoader;

    private Context mContext;
    private RelativeLayout mRoot;
    private int mScreenWidth;


    public AutoScrollLayout1(Context context) {
        this(context, null);
    }

    public AutoScrollLayout1(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoScrollLayout1(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View convertView = inflater.inflate(R.layout.top_auto_scroll, this, true);
        mViewPager = (ViewPager) convertView.findViewById(R.id.top_viewpager);
        mDotLayout = (LinearLayout) convertView.findViewById(R.id.top_dot_layout);
        mRoot = (RelativeLayout) convertView.findViewById(R.id.top_layout);


        mViewPager.getParent().requestDisallowInterceptTouchEvent(true);//设置不让父窗体拦截事件
        mHandler = new MyHandler();

        DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
        mScreenWidth = dm.widthPixels;
    }

    public void setData(SlideInfo data[], ImageLoader imageLoader) {
        if (data == null || data.length == 0) {
            setVisibility(GONE);
            return;
        } else if (mData == data) {
            setVisibility(VISIBLE);
            return;
        } else {
            setVisibility(VISIBLE);
        }

        mData = data;
        mImageLoader = imageLoader;
        int padding = AndroidUtil.dip2px(3);
        mRoot.setPadding(padding, padding, padding, 0);

        //设置点的属性,动态添加到点的布局中
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

        //设置图片,并用一个维度来标识是否自动播放
        mImageTops = new RoundCornerImageView[2][mData.length];
        for (int i = 0; i < mImageTops.length; i++) {
            for (int j = 0; j < mImageTops[i].length; j++) {
                mImageTops[i][j] = new RoundCornerImageView(getContext());
                mImageTops[i][j].setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                mImageTops[i][j].setScaleType(ImageView.ScaleType.FIT_XY);//设置缩放比例
                mImageTops[i][j].setRoundWidthHight(15);
//				mImageTops[i][j].setImageUrl(mData[j].image, mImageLoader);
                mImageTops[i][j].setTag(mData[j].image);
                ListImageListener listenerPic = new ListImageListener(
                        mImageTops[i][j], 0, 0, mData[j].image);
                mImageLoader.get(mData[j].image, listenerPic);

                final SlideInfo tag = mData[j];
//				mImageTops[i][j].setTag(mData[j]);
                //点击事件,根据点击的图片类型跳转到对应的页面
                mImageTops[i][j].setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
//						SlideInfo tag = (SlideInfo) v.getTag();
                        SkipUtils.skip(mContext, tag.type, tag.value, tag.tempid, tag.h5data);
                    }


                });

            }
        }

        //设置适配器
        mAdapter = new TopAdapter();
        mViewPager.setAdapter(mAdapter);
        //图片改变监听器,用于校准点的显示
        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                //校准点的显示
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

        //触摸监听器,判断是否需要停止自动播放
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


    private void reLayout() {
        //设置viewpager的宽高
        //TODO 首页 幻灯片高度适配入口
        //现在图片的比例是750*380,大约是2:1的样子
        int height = mScreenWidth / 2;
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, height);
        //lp.setMargins(0, AndroidUtil.dip2px(3), AndroidUtil.dip2px(4), 0);

        mViewPager.setLayoutParams(lp);
    }

    public void startAutoScroll() {
        //如果有图片
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

    private int getIdByString(String value) {
        int id = 0;
        try {
            id = Integer.parseInt(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
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
