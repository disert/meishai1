package com.meishai.ui.fragment.tryuse;

import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.app.widget.CircleImageView;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.TrialDetailRespData;
import com.meishai.entiy.TrialDetailRespData.Picture;
import com.meishai.entiy.TrialDetailRespData.UserInfo;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.ImageLoader.ImageListener;
import com.meishai.net.volley.toolbox.ListImageListener;
import com.meishai.ui.base.BaseActivity;
import com.meishai.ui.fragment.MeishaiWebviewActivity;
import com.meishai.ui.fragment.tryuse.req.FuLiSheReq;
import com.meishai.ui.fragment.usercenter.LoginActivity;
import com.meishai.ui.popup.ShareMorePopupWindow;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;
import com.meishai.util.GsonHelper;

/**
 * 试用详情对应的Activity
 *
 * @author Administrator yl
 */
public class FuliSheDetailActivity extends BaseActivity implements
        OnClickListener {

    public static final int TYPE_FREE_TRIAL = 0;// 免费试用详情
    public static final int TYPE_FLASH_SALE = 1;// 限时抢购详情
    public static final int TYPE_POINT_REWARD = 2;// 积分商城详情
    public static final int TYPE_TASTE = 3;// 品质体验详情
    private int mType = 0;

    private int mGid;

    private ImageView mImage;
    private TextView mImageTitle;
    private TextView mSnum;
    private TextView mPrice;
    private TextView mPromise;
    private TextView mPrize;
    private TextView mSponsor;
    private ImageView mSponsorIcon;
    private TextView mSponsorDetail;
    private TextView mNotice;
    private TextView mNoticeContent;
    private TextView mAppnum;
    private TextView mEndday;
    private Button mControlBtn;
    private LinearLayout mPics;
    private TrialDetailRespData mResqData;

    private TextView mSponsorText;
    private GridView mUsersGridViwe;
    private ShareMorePopupWindow sharePop;
    private RelativeLayout mLayRoot;
    private LinearLayout mUsersRoot;
    private TextView mUsersMore;
    private int mCurrentOper;// 当前选择的操作,0 普通分享 ,1 申请试用 ,2 积分兑换,3 抢购 ,
    private TextView mTitle;

    // 申请本页数据所用到的监听器
    private Listener<String> mListener;
    private ErrorListener mErrorListener;
    // 发送分享请求时用到的监听器
    private Listener<String> mShareListener;
    private ErrorListener mShareErrorListener;
    private View mDataArea;

    public static Intent newIntent(int gid, int type) {

        Intent intent = new Intent(GlobalContext.getInstance()
                .getApplicationContext(), FuliSheDetailActivity.class);
        intent.putExtra("gid", gid);
        intent.putExtra("type", type);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.trial_detail_activity);

        initView();
        getRequestData();
    }

    private void initView() {

        mType = getIntent().getIntExtra("type", 0);
        mGid = getIntent().getIntExtra("gid", 0);
        if (mGid == 0) {
            throw new RuntimeException("gid不能为空");
        }
        mDataArea = findViewById(R.id.data_area);

        mLayRoot = (RelativeLayout) findViewById(R.id.main);
        mTitle = (TextView) findViewById(R.id.title);
        findViewById(R.id.backMain).setOnClickListener(this);
        findViewById(R.id.more).setOnClickListener(this);

        mImage = (ImageView) findViewById(R.id.trial_detail_image);
        mImageTitle = (TextView) findViewById(R.id.trial_detail_title);

        // 试用商品信息
        mSnum = (TextView) findViewById(R.id.trial_detail_snum);
        mPrice = (TextView) findViewById(R.id.trial_detail_price);
        mPromise = (TextView) findViewById(R.id.trial_detail_promise);
        mPrize = (TextView) findViewById(R.id.trial_detail_prize);
        mPrize.setOnClickListener(this);

        // 赞助商信息
        mSponsor = (TextView) findViewById(R.id.trial_detail_sponsor);
        mSponsorText = (TextView) findViewById(R.id.trial_detail_sponsor_text);
        mSponsorIcon = (ImageView) findViewById(R.id.trial_detail_sponsor_icon);
        mSponsorDetail = (TextView) findViewById(R.id.trial_detail_sponsor_detail);
        mSponsorDetail.setOnClickListener(this);

        // 申请的会员信息
        mUsersRoot = (LinearLayout) findViewById(R.id.trial_detail_users_root);
        mUsersMore = (TextView) findViewById(R.id.trial_detail_users_more);
        mUsersGridViwe = (GridView) findViewById(R.id.trial_detail_users_gv);

        // 试用商品图片信息
        mPics = (LinearLayout) findViewById(R.id.trial_detail_pics);

        // 提示信息
        mNotice = (TextView) findViewById(R.id.trial_detail_notice);
        mNoticeContent = (TextView) findViewById(R.id.trial_detail_notice_content);

        // 申请试用栏信息
        mAppnum = (TextView) findViewById(R.id.trial_detail_appnum);
        mEndday = (TextView) findViewById(R.id.trial_detail_endday);
        mControlBtn = (Button) findViewById(R.id.trial_detail_control_btn);
        mControlBtn.setOnClickListener(this);

        mListener = new Listener<String>() {

            @Override
            public void onResponse(String response) {
                checkRespData(response);
                mDataArea.setVisibility(View.VISIBLE);
                hideProgress();
            }
        };
        mErrorListener = new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                AndroidUtil.showToast(R.string.reqFailed);
                hideProgress();
            }
        };
        mShareListener = new Listener<String>() {

            @Override
            public void onResponse(String response) {
                hideProgress();
                try {
                    JSONObject obj = new JSONObject(response);
//					AndroidUtil.showToast(obj.getString("tips"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        mShareErrorListener = new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                AndroidUtil.showToast(R.string.reqFailed);
                hideProgress();
            }
        };

    }

    private void initData() {

        if (mResqData == null || mResqData.data == null) {
            AndroidUtil.showToast("没有获取到有效数据");
            return;
        }

        sharePop = new ShareMorePopupWindow(this, 0) {
            @Override
            public void sharePre(String name) {
                // 分享前请求
                FuLiSheReq.share(mGid, getPlatformType(name), 0,
                        mShareListener, mShareErrorListener);
            }
        };
        // 设置分享页面的分享数据
        sharePop.initShareParams(mResqData.sharedata);
        // 设置分享页面的提示
        sharePop.setHint("分享就可以参加" + mResqData.data.page_title + "咯~");
        // 分享后发送请求 0 普通分享 ,1 申请试用 ,2 积分兑换,3 抢购 ,
        sharePop.setActionListener(new PlatformActionListener() {

            @Override
            public void onError(Platform arg0, int arg1, Throwable arg2) {
                FuLiSheReq.share(mGid,
                        sharePop.getPlatformType(arg0.getName()), -99,
                        mShareListener, mShareErrorListener);
            }

            @Override
            public void onComplete(Platform arg0, int arg1,
                                   HashMap<String, Object> arg2) {
                // 分享请求
                FuLiSheReq.share(mGid,
                        sharePop.getPlatformType(arg0.getName()), 1,
                        mShareListener, mShareErrorListener);
                mResqData.data.isshare = 1;
                // 提交
                if (mCurrentOper == 1) {// 申请试用

                    if (mResqData.data.isorder == 1) {


                        FuLiSheReq.submit(mGid, 0, 0, 0, 0, new Listener<String>() {

                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject obj = new JSONObject(response);
                                    int success = obj.getInt("success");
                                    if (success == 1) {
                                        mControlBtn.setSelected(true);
                                        mControlBtn.setClickable(false);
                                        mControlBtn.setText("已经申请");
                                    } else {
                                        mControlBtn.setSelected(false);
                                        mControlBtn.setClickable(true);
                                        mControlBtn.setText("申请体验");
                                    }
                                    AndroidUtil.showToast(obj.getString("tips"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                DebugLog.d(error.getMessage());
                            }
                        });
                    } else {
                        Intent intent2 = FindPointOrderActivity1.newIntent(mGid);
                        startActivity(intent2);
                    }
                } else if (mCurrentOper == 2 || mCurrentOper == 3) {// 积分兑换 和疯抢
                    // 跳转到兑换界面
                    Intent intent2 = FindPointOrderActivity1.newIntent(mGid);
                    startActivity(intent2);
                } else {// 普通分享
                }
            }

            @Override
            public void onCancel(Platform arg0, int arg1) {
                AndroidUtil.showToast("您取消了分享!");
                int type = sharePop.getPlatformType(arg0.getName());
                FuLiSheReq.share(mGid, type, -1, mShareListener,
                        mShareErrorListener);
            }
        });

        mTitle.setText(mResqData.data.page_title);
        mImageTitle.setText(mResqData.data.title);
        ImageListener listener = ImageLoader.getImageListener(mImage,
                R.drawable.place_default, R.drawable.place_default);
        getImageLoader().get(mResqData.data.image, listener);

        // 商品信息
        mSnum.setText(mResqData.data.snum);
        mPrice.setText(mResqData.data.price);
        if (mType == TYPE_FREE_TRIAL) {// 试用详情
            mPrize.setVisibility(View.GONE);
            mPromise.setVisibility(View.VISIBLE);
            mPromise.setText(mResqData.data.gprice);
            mPrice.setText(mResqData.data.price);
            if (mResqData.data.isorder == 1) {
                mPromise.setVisibility(View.VISIBLE);
                mPromise.setText(mResqData.data.gprice);
            } else {
                mPromise.setVisibility(View.GONE);
            }
        } else if (mType == TYPE_POINT_REWARD || mType == TYPE_FLASH_SALE) {// 积分详情 疯抢详情
            if (mType == TYPE_FLASH_SALE) {
                mPrice.setText(mResqData.data.price);
            } else {
                mPrice.setText(mResqData.data.lowpoint);
            }
            mPrize.setText(mResqData.data.record_text);
            mPrize.setVisibility(View.VISIBLE);
            mPromise.setVisibility(View.GONE);
        } else if (mType == TYPE_TASTE) {// 品质体验详情
//			mPrize.setText(mResqData.data.record_text);
            mPrice.setText(mResqData.data.lowpoint);
            mPrice.setText(mResqData.data.price);
            mPromise.setVisibility(View.GONE);
            mPrize.setVisibility(View.GONE);
        }

        // 赞助商
        mSponsor.setText(mResqData.data.shop_text);
        // 如果赞助商有图片就显示图片,有名字就显示名字,如果都没有就什么的不显示
        if (!TextUtils.isEmpty(mResqData.data.shop_logo)) {
//			mPrize.setVisibility(View.GONE);
            mSponsorIcon.setVisibility(View.VISIBLE);
            mSponsorText.setVisibility(View.GONE);
            ImageListener listener1 = ImageLoader.getImageListener(
                    mSponsorIcon, R.drawable.place_default,
                    R.drawable.place_default);
            getImageLoader().get(mResqData.data.shop_logo, listener1);
        } else if (!TextUtils.isEmpty(mResqData.data.shop_name)) {
            mSponsorIcon.setVisibility(View.GONE);
            mSponsorText.setVisibility(View.VISIBLE);
            mSponsorText.setText(mResqData.data.shop_name);
        } else {
            mSponsorIcon.setVisibility(View.GONE);
            mSponsorText.setVisibility(View.VISIBLE);
            mSponsorText.setText("美晒网");
            mSponsorDetail.setVisibility(View.GONE);
        }

        if (TextUtils.isEmpty(mResqData.data.shop_url)) {
            mSponsorDetail.setVisibility(View.GONE);
        } else {
            mSponsorDetail.setVisibility(View.VISIBLE);
        }
        // 申请会员
        if (mType == TYPE_FREE_TRIAL) {
            if (mResqData.users == null || mResqData.users.isEmpty()) {
                mUsersRoot.setVisibility(View.GONE);
            } else {
                mUsersRoot.setVisibility(View.VISIBLE);
                mUsersMore.setOnClickListener(this);
                MyAdapter adapter = new MyAdapter(getApplicationContext(),
                        mResqData.users);
                mUsersGridViwe.setAdapter(adapter);
            }
        } else {
            mUsersRoot.setVisibility(View.GONE);
        }

        // 提示
        mNotice.setText(mResqData.notice.note_title);
        mNoticeContent.setText(mResqData.notice.note_content);

        // 申请栏
        mEndday.setText(mResqData.data.endday);
        if (mResqData.data.isapp == 1) {
            mControlBtn.setText("已经申请");
        } else {
            mControlBtn.setText(mResqData.data.button_text);
        }
        if (mType == TYPE_FREE_TRIAL || mType == TYPE_TASTE) {
            mAppnum.setText(mResqData.data.appnum);
            if (mResqData.data.isapp == 1) {
                mControlBtn.setSelected(true);
                mControlBtn.setClickable(false);
            }
        } else if (mType == TYPE_FLASH_SALE) {
            mAppnum.setText(mResqData.data.appnum);
        } else if (mType == TYPE_POINT_REWARD) {
            mAppnum.setText(mResqData.data.lastnum);
        }

        if (mResqData.pics != null && mResqData.pics.size() > 0) {
            mPics.setVisibility(View.VISIBLE);
            initPics();
        } else {
            mPics.setVisibility(View.GONE);
        }

    }

    private void initPics() {
        mPics.removeAllViews();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        int dip = AndroidUtil.dip2px(7);
        lp.setMargins(0, dip, 0, 0);

        for (Picture pic : mResqData.pics) {
            ImageView image = new ImageView(this);
            image.setLayoutParams(lp);
            image.setAdjustViewBounds(true);

            ImageListener listener = ImageLoader.getImageListener(image,
                    R.drawable.place_default, R.drawable.place_default);
            getImageLoader().get(pic.pic_url, listener);

            mPics.addView(image);
        }
    }

    private void getRequestData() {
        mDataArea.setVisibility(View.GONE);
        showProgress("", getString(R.string.network_wait));
        if (mType == TYPE_FREE_TRIAL || mType == TYPE_TASTE) {// 试用详情
            FuLiSheReq.trialDetail(mGid, mListener, mErrorListener);
        } else if (mType == TYPE_FLASH_SALE) {// 疯抢详情
            FuLiSheReq.caleDetail(mGid, mListener, mErrorListener);
        } else if (mType == TYPE_POINT_REWARD) {// 积分详情
            FuLiSheReq.pointDetail(mGid, mListener, mErrorListener);
        }
    }

    protected void checkRespData(String response) {
        try {
            JSONObject obj = new JSONObject(response);
            int success = obj.getInt("success");
            if (success == 1) {
                mResqData = GsonHelper.parseObject(response,
                        TrialDetailRespData.class);

                initData();
            } else {
                AndroidUtil.showToast(R.string.reqFailed);
            }

        } catch (JSONException e) {
            DebugLog.e(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        com.meishai.entiy.UserInfo userInfo = MeiShaiSP.getInstance()
                .getUserInfo();
        switch (v.getId()) {
            case R.id.backMain:
                finish();
                break;
            case R.id.more:// 分享
                if (sharePop == null) {
                    sharePop = new ShareMorePopupWindow(this, 0);
                }
                sharePop.setHint1Visibility(View.GONE);
                sharePop.setHintVisibility(View.GONE);

                if (!userInfo.isLogin()) {
                    startActivity(LoginActivity.newOtherIntent());
                    return;
                }

                showPop();
                break;
            case R.id.trial_detail_sponsor_detail:// 赞助商详情
                if (mResqData == null || mResqData.data == null) {
                    return;
                }
                Intent intent = MeishaiWebviewActivity
                        .newIntent(mResqData.data.shop_url);
                startActivity(intent);
                break;
            case R.id.trial_detail_prize:// 查看是否中奖/兑换记录
                if (mResqData == null || mResqData.data == null) {
                    return;
                }
                DebugLog.w(mResqData.data.record_url);
                if (!TextUtils.isEmpty(mResqData.data.record_url)) {
                    startActivity(MeishaiWebviewActivity
                            .newIntent(mResqData.data.record_url));
                }
                break;
            case R.id.trial_detail_control_btn:// 底部控制按钮
                if (sharePop == null) {
                    sharePop = new ShareMorePopupWindow(this, 0);
                }
                sharePop.setHint1Visibility(View.VISIBLE);
                sharePop.setHintVisibility(View.VISIBLE);

                if (mResqData == null || mResqData.data == null) {
                    return;
                }

                if (!userInfo.isLogin()) {
                    startActivity(LoginActivity.newOtherIntent());
                    return;
                }

                if (mType == TYPE_FREE_TRIAL) {
                    // 申请试用
                    mCurrentOper = 1;
                    if (mResqData.data.isshare == 1) {
                        return;
                    } else {
                        showPop();
                    }
                } else if (mType == TYPE_FLASH_SALE) {
                    // 限时抢购
                    mCurrentOper = 3;
                    if (mResqData.data.isshare == 1) {
                        Intent intent2 = FindPointOrderActivity1.newIntent(mGid);
                        startActivity(intent2);
                    } else {
                        showPop();
                    }
                } else if (mType == TYPE_POINT_REWARD || mType == TYPE_TASTE) {
                    // 积分商城  品质体验 我要竞拍
                    mCurrentOper = 2;
                    if (mResqData.data.isshare == 1) {
                        Intent intent2 = FindPointOrderActivity1.newIntent(mGid);
                        startActivity(intent2);
                    } else {
                        showPop();
                    }
                }

                break;
            case R.id.trial_detail_users_more:
                if (mResqData == null || mResqData.data == null) {
                    return;
                }
                Intent intent1 = MeishaiWebviewActivity
                        .newIntent(mResqData.data.user_more);
                startActivity(intent1);

                break;

            default:
                break;
        }
    }

    private void showPop() {
        if (sharePop == null) {
            sharePop = new ShareMorePopupWindow(this, 0);
        }
        if (sharePop.isShowing()) {
            sharePop.dismiss();
        } else {

            sharePop.showAtLocation(mLayRoot, Gravity.BOTTOM
                    | Gravity.CENTER_HORIZONTAL, 0, 0);
        }
    }

    private class MyAdapter extends BaseAdapter {

        private Context context;
        private List<UserInfo> users;

        public MyAdapter(Context context, List<UserInfo> users) {
            this.context = context;
            this.users = users;
        }

        @Override
        public int getCount() {
            if (users != null && users.size() > 0) {
                return users.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return users.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            UserInfo item = (UserInfo) getItem(position);
            if (convertView == null) {
                int gap = AndroidUtil.dip2px(26);
                int marg = AndroidUtil.dip2px(5);
                LayoutParams lp = new LayoutParams(gap, gap);
                // 初始化赞
                CircleImageView image = new CircleImageView(context);
                image.setPadding(marg, marg, marg, marg);

                image.setLayoutParams(lp);
                convertView = image;

            }
            CircleImageView image = (CircleImageView) convertView;
            // 加载图片
            image.setTag(item.avatar);
            ListImageListener listener = new ListImageListener(image, 0, 0,
                    item.avatar);

            getImageLoader().get(item.avatar, listener);
            return convertView;
        }

    }

}
