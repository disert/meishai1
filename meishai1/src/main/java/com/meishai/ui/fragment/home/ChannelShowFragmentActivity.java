package com.meishai.ui.fragment.home;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.app.util.StringUtil;
import com.meishai.app.widget.CircleNetWorkImageView;
import com.meishai.app.widget.CustomProgress;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.CatalogInfo;
import com.meishai.entiy.CateInfo;
import com.meishai.net.RespData;
import com.meishai.net.VolleyHelper;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.camera.CameraActivity;
import com.meishai.ui.fragment.home.req.CateReq;
import com.meishai.ui.sliding.PagerSlidingTabStrip;
import com.meishai.util.AndroidUtil;
import com.meishai.util.ViewScrollListener;

/**
 * @ClassName: ChannelActivity
 * @Description: 晒晒->精选->频道内容
 */
public class ChannelShowFragmentActivity extends FragmentActivity {
    // 精彩晒晒
    public static final int CHANNEL_GOOD = 3;
    // 最新发布
    public static final int CHANNEL_PUBLISH = 1;
    // 最新回复
    public static final int CHANNEL_REPLY = 2;
    private Context mContext = ChannelShowFragmentActivity.this;
    private ImageLoader imageLoader = null;
    private CustomProgress mProgressDialog = null;

    private Button top_back_btn;
    private TextView tvTitle;
    private ImageButton top_camer_ib;

    private RelativeLayout channel_rl;

    private PagerSlidingTabStrip tabs;
    private DisplayMetrics dm;
    private ViewPager pager;

    private CircleNetWorkImageView image;
    private TextView name;
    private TextView desc;
    private ImageButton isadd;

    // 头部分类信息
    private CatalogInfo catalogInfo = null;
    private boolean isRefresh = false;

    // 精彩晒晒
    private ChannelShowChildFragment goodFragment;
    // 最新发布
    private ChannelShowChildFragment publishFragment;
    // 最新回复
    private ChannelShowChildFragment replyFragment;
    // 分类id
    private int id = 0;
    private String title = "";
    private String image1 = "";


//	private MyScrollListener scrollListener;

    public static Intent newIntent(int id, String image, String name) {
        Intent intent = new Intent(GlobalContext.getInstance(),
                ChannelShowFragmentActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("image", image);
        intent.putExtra("name", name);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_channel_main_show);

//		scrollListener = new MyScrollListener();

        imageLoader = VolleyHelper.getImageLoader(this);
        id = getIntent().getIntExtra("id", 0);
        title = getIntent().getStringExtra("name");
        image1 = getIntent().getStringExtra("image");
        // 初始化基本数据
        catalogInfo = new CatalogInfo();
        catalogInfo.setCid(id);
        catalogInfo.setName(title);
        catalogInfo.setImage(image1);
        dm = getResources().getDisplayMetrics();


        initView();
        refreshUI(catalogInfo);
        setTabsValue();
    }

    private void initView() {
        top_back_btn = (Button) this.findViewById(R.id.top_back_btn);
        top_back_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
        tvTitle = (TextView) this.findViewById(R.id.title);
        tvTitle.setText(title);
        top_camer_ib = (ImageButton) this.findViewById(R.id.top_camer_ib);
        top_camer_ib.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null != catalogInfo) {
                    CateInfo cateInfo = new CateInfo();
                    cateInfo.setCid(catalogInfo.getCid());
                    cateInfo.setImage(catalogInfo.getImage());
                    cateInfo.setName(catalogInfo.getName());
                    startActivity(CameraActivity.newPublishIntentAndCate(cateInfo));
                } else {
                    startActivity(CameraActivity.newPublishIntent());
                }
            }
        });

        // 头部内容
        image = (CircleNetWorkImageView) this.findViewById(R.id.image);
        image.setRoundness(6f);
        name = (TextView) this.findViewById(R.id.name);
        desc = (TextView) this.findViewById(R.id.desc);
        isadd = (ImageButton) this.findViewById(R.id.isadd);

        pager = (ViewPager) findViewById(R.id.pager);
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        tabs.setViewPager(pager);

        channel_rl = (RelativeLayout) this.findViewById(R.id.rl_channel_show);
    }

    private void refreshUI(CatalogInfo catalogInfo) {
        if (null != catalogInfo) {
            image.setDefaultImageResId(R.drawable.head_default);
            image.setErrorImageResId(R.drawable.head_default);
            if (!TextUtils.isEmpty(catalogInfo.getImage())) {
                image.setImageUrl(catalogInfo.getImage(), imageLoader);
            }
            name.setText(catalogInfo.getName());
            desc.setText(StringUtil.isBlank(catalogInfo.getDesc()) ? catalogInfo
                    .getName() : catalogInfo.getDesc());
            isadd.setTag(catalogInfo.getCid());
            if (catalogInfo.getIsadd() == CatalogInfo.HAS_ADD) {
                isadd.setImageResource(R.drawable.ic_round_remove);
                isadd.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        int cid = (Integer) v.getTag();
                        delcatalog(cid);
                    }
                });
            } else {
                isadd.setImageResource(R.drawable.ic_round_add);
                isadd.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        int cid = (Integer) v.getTag();
                        addcatalog(cid);
                    }
                });
            }

        }
    }

    /**
     * 分类id
     *
     * @param cid
     */
    private void addcatalog(int cid) {
        String message = "正在添加，请稍候...";
        if (null == mProgressDialog) {
            mProgressDialog = CustomProgress
                    .show(mContext, message, true, null);
        } else {
            mProgressDialog.setMessage(message);
            mProgressDialog.show();
        }
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        data.put("cid", String.valueOf(cid));
        CateReq.addcatalog(mContext, data, new Listener<RespData>() {

            @Override
            public void onResponse(RespData response) {
                if (null != mProgressDialog) {
                    mProgressDialog.hide();
                }
                if (response.isSuccess()) {
                    if (null != catalogInfo) {
                        catalogInfo.setIsadd(CatalogInfo.HAS_ADD);
                        refreshUI(catalogInfo);
                    }
                } else {
                    AndroidUtil.showToast(response.getTips());
                }
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (null != mProgressDialog) {
                    mProgressDialog.hide();
                }
                AndroidUtil.showToast(mContext.getString(R.string.reqFailed));
            }
        });
    }

    private void delcatalog(int cid) {
        String message = "正在删除，请稍候...";
        if (null == mProgressDialog) {
            mProgressDialog = CustomProgress
                    .show(mContext, message, true, null);
        } else {
            mProgressDialog.setMessage(message);
            mProgressDialog.show();
        }
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        data.put("cid", String.valueOf(cid));
        CateReq.delcatalog(mContext, data, new Listener<RespData>() {

            @Override
            public void onResponse(RespData response) {
                if (null != mProgressDialog) {
                    mProgressDialog.hide();
                }
                if (response.isSuccess()) {
                    if (null != catalogInfo) {
                        catalogInfo.setIsadd(CatalogInfo.NO_ADD);
                        refreshUI(catalogInfo);
                    }
                } else {
                    AndroidUtil.showToast(response.getTips());
                }
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (null != mProgressDialog) {
                    mProgressDialog.hide();
                }
                AndroidUtil.showToast(mContext.getString(R.string.reqFailed));
            }
        });
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

        private final String[] titles = {"精彩晒晒", "最新发布", "最新回复"};

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

            OnReqSuccessListener successListener = new OnReqSuccessListener() {

                @Override
                public void onSuccess(CatalogInfo catalogInfo) {
                    if (!isRefresh) {
                        refreshUI(catalogInfo);
                        isRefresh = true;
                    }
                }
            };


            switch (position) {
                case 0:
                    if (goodFragment == null) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("id", id);
                        bundle.putInt("listorder", CHANNEL_GOOD);
                        goodFragment = new ChannelShowChildFragment();
                        goodFragment.setListener(successListener);
//					goodFragment.setOnScrollListener(scrollListener);
                        goodFragment.setArguments(bundle);
                    }
                    return goodFragment;
                case 1:
                    if (publishFragment == null) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("id", id);
                        bundle.putInt("listorder", CHANNEL_PUBLISH);
                        publishFragment = new ChannelShowChildFragment();
                        publishFragment.setListener(successListener);
//					publishFragment.setOnScrollListener(scrollListener);
                        publishFragment.setArguments(bundle);
                    }
                    return publishFragment;
                case 2:
                    if (replyFragment == null) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("id", id);
                        bundle.putInt("listorder", CHANNEL_REPLY);
                        replyFragment = new ChannelShowChildFragment();
                        replyFragment.setListener(successListener);
//					replyFragment.setOnScrollListener(scrollListener);
                        replyFragment.setArguments(bundle);
                    }
                    return replyFragment;
                default:
                    return null;
            }
        }
    }

    public interface OnReqSuccessListener {
        public void onSuccess(CatalogInfo catalogInfo);
    }


    private class MyScrollListener extends ViewScrollListener {

        @Override
        public void onFlingUp(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            channel_rl.setVisibility(View.GONE);
        }

        @Override
        public void onFlingDown(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            channel_rl.setVisibility(View.VISIBLE);
        }

    }

}
