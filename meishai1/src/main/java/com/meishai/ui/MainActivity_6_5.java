package com.meishai.ui;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.emoji.MsgEmojiModle;
import com.google.gson.reflect.TypeToken;
import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.app.util.AppUtils;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.ReleaseData;
import com.meishai.entiy.SplashData;
import com.meishai.entiy.Upgrade;
import com.meishai.entiy.UserInfo;
import com.meishai.net.RespEmoji;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.service.LoadSplashService;
import com.meishai.ui.base.BaseFragmentActivity;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.dialog.CompleteReceiver;
import com.meishai.ui.dialog.UpgradeDialog;
import com.meishai.ui.fragment.camera.ChooseImageActivity;
import com.meishai.ui.fragment.common.req.PublicReq;
import com.meishai.ui.fragment.usercenter.LoginActivity;
import com.meishai.ui.tab.FindTab;
import com.meishai.ui.tab.PhotoShowTab;
import com.meishai.ui.tab.UseFullTab;
import com.meishai.ui.tab.UserCenterTab;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;
import com.meishai.util.GsonHelper;
import com.meishai.util.SkipUtils;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.sharesdk.framework.ShareSDK;

public class MainActivity_6_5 extends BaseFragmentActivity {


    private TextView mTvHome, mTvFind, mTvTry, mTvUser;
    public ImageView mTvCamer;
    private final String TAG_HOME = "home";
    private final String TAG_FIND = "find";
    private final String TAG_TRY = "try";
    private final String TAG_USER = "user";

    private final int ID_HOME = 0;
    private final int ID_FIND = 1;
    private final int ID_TRY = 2;
    private final int ID_USER = 3;

    private Fragment mHome = new PhotoShowTab();
    private Fragment mFind = new FindTab();
    private Fragment mTry = new UseFullTab();
    private Fragment mUser = new UserCenterTab();

    private UpgradeDialog upgradeDialog = null;

    private CompleteReceiver completeReceiver = null;

    private ImageView mIvFind;

    private ImageView mIvTry;

    private ImageView mIvUser;

    private LinearLayout mLlFind;

    private LinearLayout mLlTry;

    private LinearLayout mLlUser;
    private int currentTag = -1;

    public static Intent newIntent(SplashData splashData) {
        Intent intent = new Intent(GlobalContext.getInstance(), MainActivity_6_5.class);
        intent.putExtra("splashData", splashData);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        ShareSDK.initSDK(this);
        //加载启动图
        startService(new Intent(this, LoadSplashService.class));

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        ConstantSet.SCREEN_WIDTH = dm.widthPixels;
        ConstantSet.SCREEN_HEIGHT = dm.heightPixels;

        // 关闭log
        //		 MobclickAgent.setDebugMode(false);
        //		 MobclickAgent.openActivityDurationTrack(false);
        //		 MobclickAgent.updateOnlineConfig(this);

        //百度自动更新sdk
        //		BDAutoUpdateSDK.uiUpdateAction(this, new UICheckUpdateCallback() {
        //			@Override
        //			public void onCheckComplete() {
        //				//检查更新完成
        //			}
        //		});

        //360自动更新sdk
        //		UpdateManager.checkUpdate(this);
        //自定义更新
        upgrade(this);

        initView();
        initListener();
        registerBoradcastReceiver();
        showHome();


        completeReceiver = new CompleteReceiver();
        registerReceiver(completeReceiver, new IntentFilter(
                DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        emoji(this);

        SplashData splashData = getIntent().getParcelableExtra("splashData");
        if (splashData != null) {
            SkipUtils.skipSplash(this, splashData);
        }
    }


    private void initView() {
        mTvHome = (TextView) findViewById(R.id.tab_home_tv);
        mTvFind = (TextView) findViewById(R.id.tab_find_tv);
        mTvCamer = (ImageView) findViewById(R.id.tab_camer_tv);
        mTvTry = (TextView) findViewById(R.id.tab_try_tv);
        mTvUser = (TextView) findViewById(R.id.tab_user_tv);

        mIvHome = (ImageView) findViewById(R.id.tab_home_icon);
        mIvFind = (ImageView) findViewById(R.id.tab_find_icon);
        mIvTry = (ImageView) findViewById(R.id.tab_try_icon);
        mIvUser = (ImageView) findViewById(R.id.tab_user_icon);

        mLlHome = (LinearLayout) findViewById(R.id.tab_home_container);
        mLlFind = (LinearLayout) findViewById(R.id.tab_find_container);
        mLlTry = (LinearLayout) findViewById(R.id.tab_try_container);
        mLlUser = (LinearLayout) findViewById(R.id.tab_user_container);
    }

    private void initListener() {
        mLlHome.setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        showHome();
                    }
                });

        mLlFind.setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        showFind();
                    }
                });

        findViewById(R.id.tab_camer_tv).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        showCamer();
                    }
                });

        mLlTry.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showTry();
            }
        });

        mLlUser.setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        showUser();
                    }
                });
    }

    public void showHome() {
        mTvHome.setSelected(true);
        mIvHome.setSelected(true);

        mTvFind.setSelected(false);
        mIvFind.setSelected(false);

        mTvCamer.setSelected(false);

        mTvTry.setSelected(false);
        mIvTry.setSelected(false);

        mTvUser.setSelected(false);
        mIvUser.setSelected(false);

        switchFragment(ID_HOME);
    }

    private void showFind() {
        mTvHome.setSelected(false);
        mIvHome.setSelected(false);

        mTvFind.setSelected(true);
        mIvFind.setSelected(true);

        mTvCamer.setSelected(false);

        mTvTry.setSelected(false);
        mIvTry.setSelected(false);

        mTvUser.setSelected(false);
        mIvUser.setSelected(false);

        switchFragment(ID_FIND);
    }

    private void showCamer() {
        mTvHome.setSelected(false);
        mIvHome.setSelected(false);

        mTvFind.setSelected(false);
        mIvFind.setSelected(false);

        mTvCamer.setSelected(true);

        mTvTry.setSelected(false);
        mIvTry.setSelected(false);

        mTvUser.setSelected(false);
        mIvUser.setSelected(false);

        if (MeiShaiSP.getInstance().getUserInfo().isLogin()) {
            startActivity(ChooseImageActivity.newIntent(new ReleaseData(), ConstantSet.MAX_IMAGE_COUNT));
            //			startActivity(new Intent(this, ImageChooseActivity1.class));
        } else {
            startActivity(LoginActivity.newOtherIntent());
        }
    }

    private void showTry() {
        mTvHome.setSelected(false);
        mIvHome.setSelected(false);

        mTvFind.setSelected(false);
        mIvFind.setSelected(false);

        mTvCamer.setSelected(false);

        mTvTry.setSelected(true);
        mIvTry.setSelected(true);

        mTvUser.setSelected(false);
        mIvUser.setSelected(false);

        switchFragment(ID_TRY);
    }

    private void showUser() {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        if (userInfo != null && userInfo.getUserID() != null
                && userInfo.getUserID().trim().length() > 0) {
            mTvHome.setSelected(false);
            mIvHome.setSelected(false);

            mTvFind.setSelected(false);
            mIvFind.setSelected(false);

            mTvCamer.setSelected(false);

            mTvTry.setSelected(false);
            mIvTry.setSelected(false);

            mTvUser.setSelected(true);
            mIvUser.setSelected(true);
            switchFragment(ID_USER);
        } else {
            startActivity(LoginActivity.newIntent());
        }
    }

    private void switchFragment(int tag) {
        if (currentTag == tag) {
            return;
        }
        currentTag = tag;
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        Fragment frg;
        switch (tag) {
            case ID_HOME:
                frg = getSupportFragmentManager().findFragmentByTag(TAG_HOME);
                if (null == frg) {
                    transaction.add(R.id.realtabcontent, mHome, TAG_HOME);
                } else {
                    transaction.show(frg);
                }
                frg = getSupportFragmentManager().findFragmentByTag(TAG_TRY);
                if (null != frg) {
                    transaction.hide(frg);
                }

                frg = getSupportFragmentManager().findFragmentByTag(TAG_FIND);
                if (null != frg) {
                    transaction.hide(frg);
                }

                frg = getSupportFragmentManager().findFragmentByTag(TAG_USER);
                if (null != frg) {
                    transaction.hide(frg);
                }

                break;

            case ID_FIND:
                frg = getSupportFragmentManager().findFragmentByTag(TAG_FIND);
                if (null == frg) {
                    transaction.add(R.id.realtabcontent, mFind, TAG_FIND);
                } else {
                    transaction.show(frg);
                }
                frg = getSupportFragmentManager().findFragmentByTag(TAG_HOME);
                if (null != frg) {
                    transaction.hide(frg);
                }

                frg = getSupportFragmentManager().findFragmentByTag(TAG_TRY);
                if (null != frg) {
                    transaction.hide(frg);
                }

                frg = getSupportFragmentManager().findFragmentByTag(TAG_USER);
                if (null != frg) {
                    transaction.hide(frg);
                }

                break;
            case ID_TRY:
                frg = getSupportFragmentManager().findFragmentByTag(TAG_TRY);
                if (null == frg) {
                    transaction.add(R.id.realtabcontent, mTry, TAG_TRY);
                } else {
                    transaction.show(frg);
                }
                frg = getSupportFragmentManager().findFragmentByTag(TAG_HOME);
                if (null != frg) {
                    transaction.hide(frg);
                }

                frg = getSupportFragmentManager().findFragmentByTag(TAG_FIND);
                if (null != frg) {
                    transaction.hide(frg);
                }

                frg = getSupportFragmentManager().findFragmentByTag(TAG_USER);
                if (null != frg) {
                    transaction.hide(frg);
                }

                break;
            case ID_USER:
                frg = getSupportFragmentManager().findFragmentByTag(TAG_USER);
                if (null == frg) {
                    transaction.add(R.id.realtabcontent, mUser, TAG_USER);
                } else {
                    transaction.show(frg);
                }
                frg = getSupportFragmentManager().findFragmentByTag(TAG_HOME);
                if (null != frg) {
                    transaction.hide(frg);
                }

                frg = getSupportFragmentManager().findFragmentByTag(TAG_TRY);
                if (null != frg) {
                    transaction.hide(frg);
                }

                frg = getSupportFragmentManager().findFragmentByTag(TAG_FIND);
                if (null != frg) {
                    transaction.hide(frg);
                }

                break;
        }

        transaction.commitAllowingStateLoss();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        DebugLog.d("request:" + requestCode + ",result:" + resultCode);
        if (null != mUser) {
            mUser.onActivityResult(requestCode, resultCode, data);
        }
        // getSupportFragmentManager().findFragmentByTag(
        // mTabHost.getCurrentTabTag()).onActivityResult(requestCode,
        // resultCode, data);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConstantSet.ACTION_LOGIN_SUCCESS)) {
                ((UserCenterTab) mUser).refreshFragment();
                showUser();
            } else if (action.equals(ConstantSet.ACTION_SHOW_HOME)) {
                showHome();
            }
        }
    };

    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(ConstantSet.ACTION_LOGIN_SUCCESS);
        myIntentFilter.addAction(ConstantSet.ACTION_SHOW_HOME);
        // 注册广播
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    private long mExitTime;

    private ImageView mIvHome;

    private LinearLayout mLlHome;

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                AndroidUtil.showToast("再按一次退出");
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 升级
     *
     * @param context
     */
    public void upgrade(final Context context) {
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        int versionCode = AppUtils.getVersionCode(context);
        if (-1 == versionCode) {
            return;
        }
        data.put("version", versionCode + "");
        PublicReq.upgrade(context, data, new Listener<String>() {
            @Override
            public void onResponse(String resp) {
                Upgrade response = GsonHelper.parseObject(resp, Upgrade.class);
                if (null != response && response.getSuccess() != null && response.getSuccess().equals("1")) {
                    // 显示升级提示
                    if (null == upgradeDialog) {
                        upgradeDialog = new UpgradeDialog(context, completeReceiver);
                    }
                    upgradeDialog.setUpgrade(response);
                    upgradeDialog.show();
                }
            }

        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                DebugLog.d(error.getMessage());
            }
        });
    }

    public void emoji(Context context) {
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        PublicReq.emoji(context, data, new Listener<String>() {
            @Override
            public void onResponse(String resp) {

                RespEmoji response = GsonHelper.parseObject(resp,
                        RespEmoji.class);
                if (response == null) {
                    AndroidUtil.showToast("数据解析错误");
                    return;
                }
                if (response.isSuccess()) {
                    Type type = new TypeToken<List<MsgEmojiModle>>() {
                    }.getType();
                    List<MsgEmojiModle> emojis = GsonHelper.parseObject(
                            GsonHelper.toJson(response.getArea()), type);
                    GlobalContext.getInstance().setEmojis(emojis);
                }
            }

        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                DebugLog.d(error.getMessage());
            }
        });
    }

}