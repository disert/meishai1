package com.meishai.ui.fragment.home;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.app.util.StringUtil;
import com.meishai.app.widget.CircleImageView;
import com.meishai.app.widget.CustomProgress;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.HomepageUser;
import com.meishai.entiy.Master;
import com.meishai.net.RespData;
import com.meishai.net.VolleyHelper;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.ListImageListener;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.dialog.SendPriMsgDialog;
import com.meishai.ui.fragment.common.req.PublicReq;
import com.meishai.ui.fragment.home.HomePageActivity.HomePageListener;
import com.meishai.ui.sliding.PagerSlidingTabStrip;
import com.meishai.ui.sliding.PagerSlidingTabStripHelper;
import com.meishai.util.AndroidUtil;

/**
 * 晒晒->个人主页
 *
 * @author sh
 */
public class HomePageActivity1 extends FragmentActivity implements
        OnClickListener {

    private Context mContext = HomePageActivity1.this;
    private CustomProgress mProgressDialog = null;
    private SendPriMsgDialog msgDialog;
    private LinearLayout top_view;
    private Button backMain;
    private PagerSlidingTabStrip tabs;
    private ViewPager viewPager;
    private String uid = "";

    private CircleImageView avatar;
    private TextView username, intro;
    // 关注
    private Button mBtnAtt;
    // 私信
    private Button mBtnPriMsg;
    // 发送对象的会员ID
    private String tuserid = "";
    // 她的主页
    private HomePageHomeFragment pageHomeFragment = null;
    // 她的话题
    private HomePageTopicFragment topicFragment = null;
    // 她的关注
    private HomePageFollowFragment followFragment = null;
    // 她的粉丝
    private HomepageFansFragment fansFragment = null;
    private ImageLoader imageLoader = null;
    private boolean isRefresh = false;

    private CustomProgress mCustomProgress;

    public static Intent newIntent(String userid) {
        Intent intent = new Intent(GlobalContext.getInstance(),
                HomePageActivity1.class);
        intent.putExtra(ConstantSet.USERID, userid);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);
        imageLoader = VolleyHelper.getImageLoader(mContext);
        uid = getIntent().getExtras().getString(ConstantSet.USERID);
        isRefresh = false;
        if (StringUtil.isBlank(uid)) {
            uid = "";
        }
        this.initView();
        this.setTabsValue();
        this.initDialog();
        this.showProgress();
    }

    private void initView() {
        top_view = (LinearLayout) this.findViewById(R.id.top_view);
        backMain = (Button) this.findViewById(R.id.backMain);
        backMain.setOnClickListener(this);
        avatar = (CircleImageView) this.findViewById(R.id.avatar);
        username = (TextView) this.findViewById(R.id.username);
        intro = (TextView) this.findViewById(R.id.intro);
        mBtnAtt = (Button) this.findViewById(R.id.btn_att);
        mBtnPriMsg = (Button) this.findViewById(R.id.btn_pri_msg);
        mBtnPriMsg.setOnClickListener(this);
        viewPager = (ViewPager) this.findViewById(R.id.pager);
        tabs = (PagerSlidingTabStrip) this.findViewById(R.id.tabs);
        viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        tabs.setViewPager(viewPager);
    }

    private void initDialog() {
        msgDialog = new SendPriMsgDialog(this);
    }

    private void setTabsValue() {
        PagerSlidingTabStripHelper.setTabsValue(tabs);
    }

    private void showProgress() {
        if (null == mCustomProgress) {
            mCustomProgress = CustomProgress.show(mContext,
                    mContext.getString(R.string.network_wait), true, null);
        }
    }

    private void updateUI(HomepageUser user) {
        if (null != mCustomProgress) {
            mCustomProgress.hide();
        }
        if (null != user) {
            top_view.setVisibility(View.VISIBLE);
            tabs.setVisibility(View.VISIBLE);
            tuserid = String.valueOf(user.getUserid());
            avatar.setTag(user.getAvatar());
            ListImageListener listener = new ListImageListener(avatar,
                    R.drawable.head_default, R.drawable.head_default,
                    user.getAvatar());
            imageLoader.get(user.getAvatar(), listener);
            username.setText(user.getUsername());
            intro.setText(user.getIntro());
            mBtnAtt.setTag(user);
            if (user.getIsattention().intValue() == Master.HAS_ATENTION) {
                mBtnAtt.setText(mContext.getString(R.string.has_attention));
                mBtnAtt.setBackgroundResource(R.drawable.btn_gray_selector);
                mBtnAtt.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        HomepageUser u = (HomepageUser) v.getTag();
                        delfriend(u);
                    }
                });
            } else {
                mBtnAtt.setText(mContext.getString(R.string.txt_attention));
                mBtnAtt.setBackgroundResource(R.drawable.btn_sign_point_selector);
                mBtnAtt.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        HomepageUser u = (HomepageUser) v.getTag();
                        addfriend(u);
                    }
                });
            }
            mBtnAtt.setVisibility(View.VISIBLE);
            mBtnPriMsg.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 添加关注
     *
     * @param user
     */
    private void addfriend(final HomepageUser user) {
        if (null == user) {
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
        data.put("fuserid", String.valueOf(user.getUserid()));
        PublicReq.addfriend(mContext, data, new Listener<RespData>() {

            @Override
            public void onResponse(RespData response) {
                if (null != mProgressDialog) {
                    mProgressDialog.hide();
                }
                if (response.isSuccess()) {
                    user.setIsattention(Master.HAS_ATENTION);
                    updateUI(user);
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

    /**
     * 取消关注
     *
     * @param user
     */
    private void delfriend(final HomepageUser user) {
        if (null == user) {
            return;
        }
        String message = mContext.getString(R.string.can_att_wait);
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
        data.put("fuserid", String.valueOf(user.getUserid()));
        PublicReq.delfriend(mContext, data, new Listener<RespData>() {

            @Override
            public void onResponse(RespData response) {
                if (null != mProgressDialog) {
                    mProgressDialog.hide();
                }
                if (response.isSuccess()) {
                    user.setIsattention(Master.NO_ATENTION);
                    updateUI(user);
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

    public class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private final String[] titles = {"她的主页", "她的话题", "她的关注", "她的粉丝"};

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
            HomePageListener pageListener = new HomePageListener() {
                @Override
                public void refreshUI(HomepageUser user) {
                    if (!isRefresh) {
                        updateUI(user);
                        isRefresh = true;
                    }
                }
            };

            Bundle args = new Bundle();
            args.putString("uid", uid);
            switch (position) {
                case 0:
                    if (pageHomeFragment == null) {
                        pageHomeFragment = new HomePageHomeFragment();
                        pageHomeFragment.setArguments(args);
                        pageHomeFragment.setHomePageListener(pageListener);
                    }
                    return pageHomeFragment;
                case 1:
                    if (topicFragment == null) {
                        topicFragment = new HomePageTopicFragment();
                        topicFragment.setArguments(args);
                        topicFragment.setHomePageListener(pageListener);
                    }
                    return topicFragment;
                case 2:
                    if (followFragment == null) {
                        followFragment = new HomePageFollowFragment();
                        followFragment.setArguments(args);
                        followFragment.setHomePageListener(pageListener);
                    }
                    return followFragment;
                case 3:
                    if (fansFragment == null) {
                        fansFragment = new HomepageFansFragment();
                        fansFragment.setArguments(args);
                        fansFragment.setHomePageListener(pageListener);
                    }
                    return fansFragment;
                default:
                    return null;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backMain:
                finish();
                break;
            case R.id.btn_pri_msg:
                msgDialog.setTuserid(tuserid);
                msgDialog.show();
                break;
            default:
                break;
        }
    }


}
