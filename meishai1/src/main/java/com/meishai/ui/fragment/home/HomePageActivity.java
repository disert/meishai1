package com.meishai.ui.fragment.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.app.widget.CustomProgress;
import com.meishai.app.widget.RoundCornerImageView;
import com.meishai.app.widget.dragtop.DragTopLayout;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.HomePageDatas;
import com.meishai.entiy.HomepageUser;
import com.meishai.entiy.Master;
import com.meishai.entiy.ShareData;
import com.meishai.net.RespData;
import com.meishai.net.VolleyHelper;
import com.meishai.net.volley.Response;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.ListImageListener;
import com.meishai.ui.base.BaseFragmentActivity;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.common.req.PublicReq;
import com.meishai.ui.fragment.home.req.HomeReq;
import com.meishai.ui.popup.ShareMorePopupWindow;
import com.meishai.ui.popup.SharePopupWindow;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;
import com.meishai.util.GsonHelper;

import java.util.HashMap;
import java.util.Map;


/**
 * 文件名：HomePageActivity2
 * 描    述： 个人主页,
 * 作    者： yl
 * 时    间：2016/1/26
 * 版    权：
 */
public class HomePageActivity extends FragmentActivity implements View.OnClickListener {

    private Context mContext = HomePageActivity.this;
    private String uid = "";

    private int mPage = 1;

    private ImageLoader imageLoader = null;


    private ShareMorePopupWindow share;


    // 数据
    private HomePageDatas pageDatas;


    // 标题栏部分
    private TextView title;
    private Button backMain;
    private ImageView ibShare;

    private LinearLayout lay_main;

    //头部信息
    private RoundCornerImageView mAvatar;
    private ImageView mMaster;
    private TextView mNiceName;
    private TextView mVip;
    private TextView mInfo;
    private ImageView mAttention;
    private TextView mDesc;

    //晒晒
    private LinearLayout mShaiShai;
    private TextView mShaiShaiTitle;
    private TextView mShaiShaiNum;
    private View mShaiShaiLine;
    private ShaiShaiFragment mShaiFragment;

    //关注
    private LinearLayout mFollow;
    private TextView mFollowTitle;
    private TextView mFollowNum;
    private View mFollowLine;
    private FollowFragment mFollowFragment;

    //粉丝
    private LinearLayout mFans;
    private TextView mFansTitle;
    private TextView mFansNum;
    private View mFansLine;
    private FansFragment mFansFragment;

    private ViewPager mPager;
    private MyPagerAdapter mAdapter;

    // 0:表示当前选中的为 晒晒 1:表示当前选中的为 关注 2:表示当前选中的为 粉丝
    private int currentCheck = -1;
    private CustomProgress mProgressDialog;
    private DragTopLayout content;
    private View mLine;
    private LinearLayout.LayoutParams layoutParams;
    private BroadcastReceiver mScrllReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isScrll = intent.getBooleanExtra("isScrll", false);
            content.setTouchMode(isScrll);
        }
    };

    public static Intent newIntent(String userid) {
        Intent intent = new Intent(GlobalContext.getInstance(),
                HomePageActivity.class);
        intent.putExtra(ConstantSet.USERID, userid);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.homepage2);
        imageLoader = VolleyHelper.getImageLoader(mContext);
        uid = getIntent().getExtras().getString(ConstantSet.USERID);
        if (TextUtils.isEmpty(uid)) {
            uid = "";
        }
        registReceiver();
        this.initView();
        this.initListener();
        this.getRequestData(1);
    }


    private void registReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConstantSet.ACTION_SCRLL_TOPVIEW);
        registerReceiver(mScrllReceiver, filter);
    }

    public void onEvent(Boolean b) {
        content.setTouchMode(b);
    }

    private void initListener() {
        backMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, AndroidUtil.dip2px(0.3f));

        lay_main = (LinearLayout) findViewById(R.id.root);
        // 标题栏部分
        backMain = (Button) this.findViewById(R.id.backMain);
        title = (TextView) findViewById(R.id.title);
        ibShare = (ImageView) findViewById(R.id.share);
        ibShare.setOnClickListener(this);

        content = (DragTopLayout) findViewById(R.id.refresh_gv);
//		content.requestDisallowInterceptTouchEvent(true);
        //头部
        mAvatar = (RoundCornerImageView) findViewById(R.id.home_page_head_avatar);
        mMaster = (ImageView) findViewById(R.id.home_page_head_master);
        mNiceName = (TextView) findViewById(R.id.home_page_head_nicename);
        mVip = (TextView) findViewById(R.id.home_page_head_vip);
        mInfo = (TextView) findViewById(R.id.home_page_head_info);
        mAttention = (ImageView) findViewById(R.id.home_page_head_attention);
        mAttention.setOnClickListener(this);
        mDesc = (TextView) findViewById(R.id.home_page_head_desc);

        //内容
        mShaiShai = (LinearLayout) findViewById(R.id.shaishai);
        mShaiShaiTitle = (TextView) findViewById(R.id.shaishai_title);
        mShaiShaiNum = (TextView) findViewById(R.id.shaishai_num);
        mShaiShaiLine = findViewById(R.id.shaishai_line);
        mShaiShai.setOnClickListener(this);

        mFollow = (LinearLayout) findViewById(R.id.follow);
        mFollowTitle = (TextView) findViewById(R.id.follow_title);
        mFollowNum = (TextView) findViewById(R.id.follow_num);
        mFollowLine = findViewById(R.id.follow_line);
        mFollow.setOnClickListener(this);

        mFans = (LinearLayout) findViewById(R.id.fans);
        mFansTitle = (TextView) findViewById(R.id.fans_title);
        mFansNum = (TextView) findViewById(R.id.fans_num);
        mFansLine = findViewById(R.id.fans_line);
        mFans.setOnClickListener(this);

        mLine = findViewById(R.id.line);

        //要被替换的区域
        mPager = (ViewPager) findViewById(R.id.view_pager);

        mAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mAdapter);
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int page) {
                switchPageStatu(page);

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
        content.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

    }

    private void getRequestData(int page) {
        if (TextUtils.isEmpty(uid)) return;
        content.setVisibility(View.INVISIBLE);
        HomeReq.homePageShaiShai(this, uid, page, 10, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                content.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(response)) {
                    HomePageDatas datas = GsonHelper.parseObject(response, HomePageDatas.class);
                    if (datas != null) {
                        //初始化数据
                        initData(datas);
                    }
                } else {
                    AndroidUtil.showToast("返回数据为空!");
                }
                //                checkRequestData(response);
                //                setNetComplete();

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DebugLog.e("net error:" + error.getMessage());

                AndroidUtil.showToast(R.string.reqFailed);

            }
        });

    }

    private void initData(HomePageDatas datas) {
        HomePageDatas.UserInfo info = datas.userinfo;
        if (info != null) {
            //初始化头部数据
            mAvatar.setTag(info.avatar);
            ListImageListener listener = new ListImageListener(mAvatar, R.drawable.place_default, R.drawable.place_default, info.avatar);
            VolleyHelper.getImageLoader(this).get(info.avatar, listener);

            mNiceName.setText(info.username);
            if (info.isdaren == 1) {
                mMaster.setVisibility(View.VISIBLE);
                mVip.setVisibility(View.VISIBLE);
                mVip.setBackgroundColor(Color.parseColor("#FF" + info.group_bgcolor));
                mVip.setText(info.group_name);
            } else {
                mMaster.setVisibility(View.GONE);
                mVip.setVisibility(View.GONE);
            }
            if (info.isattention == 1) {
                mAttention.setClickable(false);
                mAttention.setImageResource(R.drawable.ic_attention_yes);
            } else {
                mAttention.setClickable(true);
                mAttention.setImageResource(R.drawable.ic_attention_no);

            }
            mInfo.setText(info.text);
            if (TextUtils.isEmpty(info.intro)) {
                mDesc.setVisibility(View.GONE);
            } else {
                mDesc.setVisibility(View.VISIBLE);
                mDesc.setText(info.intro);
            }
            //数量
            mShaiShaiNum.setText(info.post_num + "");
            mFollowNum.setText(info.follow_num + "");
            mFansNum.setText(info.fans_num + "");

            //分享数据
            configShareContent(datas.sharedata);

            //默认显示晒晒
            switchPageStatu(0);

        }
    }


    private void configShareContent(ShareData shareData) {
        if (null == shareData) {
            return;
        }
        share = new ShareMorePopupWindow(this, 0);
        share.setHint1Visibility(View.GONE);
        share.setHintVisibility(View.GONE);
        share.initShareParams(shareData);
    }

    private void switchPageStatu(int page) {
        if (currentCheck == page) {
            return;
        }
        currentCheck = page;

        mShaiShaiTitle.setSelected(false);
        mShaiShaiLine.setVisibility(View.GONE);
        //关注
        mFollowTitle.setSelected(false);
        mFollowLine.setVisibility(View.GONE);
        //粉丝
        mFansTitle.setSelected(false);
        mFansLine.setVisibility(View.GONE);

        switch (page) {
            case 0:
                mShaiShaiTitle.setSelected(true);
                mShaiShaiLine.setVisibility(View.VISIBLE);
                break;
            case 1:
                mFollowTitle.setSelected(true);
                mFollowLine.setVisibility(View.VISIBLE);
                break;
            case 2:
                mFansTitle.setSelected(true);
                mFansLine.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }

        mPager.setCurrentItem(page);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.shaishai://晒晒
                switchPageStatu(0);
                break;
            case R.id.follow://关注
                switchPageStatu(1);
                break;
            case R.id.fans://粉丝
                switchPageStatu(2);
                break;
            case R.id.home_page_head_attention://关注
                addfriend(uid);
                break;
            case R.id.share://分享
                // 1.个人主页的分享事件
                if (null != share) {
                    share.showAtLocation(lay_main, Gravity.BOTTOM
                            | Gravity.CENTER_HORIZONTAL, 0, 0);
                }
                break;
            default:
                break;
        }
    }

    public interface HomePageListener {

        public void refreshUI(HomepageUser user);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mScrllReceiver);
    }

    private void addfriend(final String uid) {
        if (null == uid) {
            return;
        }
        String message = mContext.getString(R.string.add_att_wait);
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
        data.put("fuserid", uid);
        PublicReq.addfriend(mContext, data, new Response.Listener<RespData>() {

            @Override
            public void onResponse(RespData response) {
                if (null != mProgressDialog) {
                    mProgressDialog.hide();
                }
                if (response.isSuccess()) {
                    mAttention.setImageResource(R.drawable.ic_attention_yes);
                    mAttention.setClickable(false);
                } else {
                    AndroidUtil.showToast(response.getTips());
                }
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (null != mProgressDialog) {
                    mProgressDialog.hide();
                }
                AndroidUtil.showToast(mContext.getString(R.string.reqFailed));
            }
        });
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private final String[] titles = {"晒晒", "关注", "粉丝"};

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
            Bundle args = new Bundle();
            args.putString("uid", uid);
            switch (position) {
                case 0:
                    if (mShaiFragment == null) {
                        mShaiFragment = new ShaiShaiFragment();

                        mShaiFragment.setArguments(args);
                    }
                    return mShaiFragment;
                case 1:

                    if (mFollowFragment == null) {
                        mFollowFragment = new FollowFragment();
                        mFollowFragment.setArguments(args);
                    }
                    return mFollowFragment;
                case 2:

                    if (mFansFragment == null) {
                        mFansFragment = new FansFragment();
                        mFansFragment.setArguments(args);
                    }
                    return mFansFragment;
                default:
                    return null;
            }
        }
    }
}
