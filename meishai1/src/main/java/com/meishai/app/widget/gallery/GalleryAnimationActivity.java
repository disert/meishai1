package com.meishai.app.widget.gallery;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.PostItem;
import com.meishai.entiy.PostItem.PictureInfo;
import com.meishai.entiy.UserInfo;
import com.meishai.lib.photoview.AnimationRect;
import com.meishai.net.ReqData;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.StringRequest;
import com.meishai.ui.base.BaseFragmentActivity;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;
import com.meishai.util.GsonHelper;
import com.nimbusds.jose.JOSEException;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;

public class GalleryAnimationActivity extends BaseFragmentActivity {

    private int mPid;
    private ArrayList<String> mUrls;
    private ArrayList<AnimationRect> mRectList;
    private int mInitPosition;

    private ViewPager mViewPager;
    private TextView mTvPosition;

    private View mBackground;

    private ColorDrawable mBackgroundColor;

    public static Intent newIntent(int pid, ArrayList<AnimationRect> rectList, int initPosition) {
        Intent intent = new Intent(GlobalContext.getInstance(), GalleryAnimationActivity.class);
        intent.putExtra("pid", pid);
        intent.putExtra("rect", rectList);
        intent.putExtra("position", initPosition);
        return intent;
    }

    public static Intent newIntent(ArrayList<String> paths, ArrayList<AnimationRect> rectList, int initPosition) {
        Intent intent = new Intent(GlobalContext.getInstance(), GalleryAnimationActivity.class);
        intent.putExtra("paths", paths);
        intent.putExtra("rect", rectList);
        intent.putExtra("position", initPosition);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.galleryactivity_animation_layout);

        //mUrls = (ArrayList)getIntent().getSerializableExtra("url");
        mPid = getIntent().getIntExtra("pid", 0);
        mRectList = getIntent().getParcelableArrayListExtra("rect");
        mUrls = getIntent().getParcelableExtra("paths");
        mInitPosition = getIntent().getIntExtra("position", 0);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mTvPosition = (TextView) findViewById(R.id.position);

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                mTvPosition.setText(String.valueOf(position + 1));
            }

        });


        mViewPager.setCurrentItem(getIntent().getIntExtra("position", 0));
        mViewPager.setOffscreenPageLimit(1);
//		mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());

        TextView sum = (TextView) findViewById(R.id.sum);
        sum.setText(String.valueOf(mRectList.size()));

        mBackground = findViewById(android.R.id.content);

        if (savedInstanceState != null) {
            showBackgroundImmediately();
        }

        if (mUrls == null || mUrls.isEmpty()) {
            getRequestPic();
        } else {
            mViewPager.setAdapter(new ImagePagerAdapter(getSupportFragmentManager()));
            mViewPager.setCurrentItem(mInitPosition);
        }
    }


    public void showBackgroundImmediately() {
        if (mBackground.getBackground() == null) {
            mBackgroundColor = new ColorDrawable(Color.BLACK);
            mBackground.setBackgroundDrawable(mBackgroundColor);
        }
    }

    public ObjectAnimator showBackgroundAnimate() {
        mBackgroundColor = new ColorDrawable(Color.BLACK);
        mBackground.setBackgroundDrawable(mBackgroundColor);
        ObjectAnimator bgAnim = ObjectAnimator.ofInt(mBackgroundColor, "alpha", 0, 255);
        bgAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mBackground.setBackgroundDrawable(mBackgroundColor);
            }
        });
        return bgAnim;
    }

    @Override
    public void onBackPressed() {

        ContainerFragment fragment = mFragmentMap.get(mViewPager.getCurrentItem());
        if (fragment != null && fragment.canAnimateCloseActivity()) {
            mBackgroundColor = new ColorDrawable(Color.BLACK);
            ObjectAnimator bgAnim = ObjectAnimator.ofInt(mBackgroundColor, "alpha", 0);
            bgAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mBackground.setBackgroundDrawable(mBackgroundColor);
//                    mBackground.setBackground(mBackgroundColor);
                }
            });
            bgAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    GalleryAnimationActivity.super.finish();
                    overridePendingTransition(-1, -1);
                }
            });
            fragment.animationExit(bgAnim);
        } else {
            super.onBackPressed();
        }
    }

    private void getRequestPic() {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        ReqData reqData = new ReqData();
        reqData.setC("post");
        reqData.setA("pic");
        Map<String, String> dataCate = new HashMap<String, String>();
        dataCate.put("userid", userInfo.getUserID());
        dataCate.put("pid", String.valueOf(mPid));
        reqData.setData(dataCate);

        try {
            String url = getString(R.string.base_url) + reqData.toReqString();
            //DebugLog.d(url);
            getRequestQueue().add(new StringRequest(url, new Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //DebugLog.d(response);
                    if (!checkData(response)) {
                        AndroidUtil.showToast(R.string.reqFailed);
                    }
                }
            }, new ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    AndroidUtil.showToast(R.string.reqFailed);
                }
            }));


        } catch (JOSEException e) {
            e.printStackTrace();
        }

    }


    private boolean checkData(String response) {
        try {
            JSONObject jsonObj = new JSONObject(response);

            if (jsonObj.getInt("success") == 1) {
                JSONArray jsonArray = jsonObj.getJSONObject("data").getJSONArray("pics");
                Type type = new TypeToken<Collection<PostItem.PictureInfo>>() {
                }.getType();
                Collection<PostItem.PictureInfo> items = GsonHelper.parseObject(jsonArray.toString(), type);
                mUrls = new ArrayList<String>();
                for (PictureInfo item : items) {
                    mUrls.add(item.url);
                }

                mViewPager.setAdapter(new ImagePagerAdapter(getSupportFragmentManager()));
                mViewPager.setCurrentItem(mInitPosition);

                return true;
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }


    private HashMap<Integer, ContainerFragment> mFragmentMap = new HashMap<Integer, ContainerFragment>();
    private boolean mAlreadyAnimateIn = false;

    private class ImagePagerAdapter extends FragmentPagerAdapter {

        public ImagePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            ContainerFragment fragment = mFragmentMap.get(position);
            if (fragment == null) {

                boolean animateIn = (mInitPosition == position) && !mAlreadyAnimateIn;
//				animateIn = false;
                DebugLog.d("url:" + mUrls.size() + ",rect:" + mRectList.size());
                fragment = ContainerFragment.newInstance(mUrls.get(position), mRectList.get(position), animateIn, mInitPosition == position);
                mAlreadyAnimateIn = true;
                mFragmentMap.put(position, fragment);
            }

            return fragment;
        }

        //when activity is recycled, ViewPager will reuse fragment by theirs name, so
        //getItem wont be called, but we need fragmentMap to animate close operation
        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            if (object instanceof Fragment) {
                mFragmentMap.put(position, (ContainerFragment) object);
            }
        }

        @Override
        public int getCount() {
            return mUrls.size();
        }
    }
}
