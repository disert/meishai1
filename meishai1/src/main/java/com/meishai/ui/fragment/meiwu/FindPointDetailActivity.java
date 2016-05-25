package com.meishai.ui.fragment.meiwu;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;

import com.google.gson.reflect.TypeToken;
import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.app.widget.CountDownTextView;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.PointExchangeDetail;
import com.meishai.entiy.ShareData;
import com.meishai.net.RespData;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.NetworkImageView;
import com.meishai.ui.base.BaseActivity;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.meiwu.req.FindReq;
import com.meishai.ui.fragment.usercenter.LoginActivity;
import com.meishai.ui.popup.SharePopupWindow;
import com.meishai.util.AndroidUtil;
import com.meishai.util.GsonHelper;

/**
 * 发现->积分->详情
 *
 * @author sh
 */
public class FindPointDetailActivity extends BaseActivity implements
        OnClickListener, PlatformActionListener, Callback {
    private Context mContext = FindPointDetailActivity.this;
    private String headTitle = "";
    private CountDownTextView countDownTextView = null;
    private ImageLoader imageLoader = null;
    private Button mBtnBack;
    private long id;
    private NetworkImageView thumb;
    private Button btn_daren;
    private TextView title;
    private TextView lowpoint;
    private TextView allnum;
    private TextView lastnum;
    private TextView endtime;
    private TextView content;
    private Button btn_fav;
    private Button mBtnOper;
    private Button btn_share;
    private PointExchangeDetail detail;

    private SharePopupWindow share;

    public static Intent newIntent(Object id, String title) {
        Intent intent = new Intent(GlobalContext.getInstance(),
                FindPointDetailActivity.class);
        intent.putExtra("title", title);
        intent.putExtra(ConstantSet.BUNDLE_ID, Long.parseLong(id.toString()));
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_point_detail);
        imageLoader = getImageLoader();
        id = getIntent().getExtras().getLong(ConstantSet.BUNDLE_ID);
        headTitle = getIntent().getExtras().getString("title");
        this.initView();
    }

    @Override
    protected void onResume() {
        cancelCountDownTextView();
        this.loadData();
        super.onResume();
    }

    private void configShareContent(ShareData shareData) {
        if (null == shareData) {
            return;
        }
        share = new SharePopupWindow(mContext);
        share.setPlatformActionListener(FindPointDetailActivity.this);
        share.initShareParams(shareData);
        share.showShareWindow();
    }

    private void initView() {
        mBtnBack = (Button) this.findViewById(R.id.backMain);
        mBtnBack.setOnClickListener(this);
        if (!TextUtils.isEmpty(headTitle)) {
            mBtnBack.setText(headTitle);
        }
        thumb = (NetworkImageView) this.findViewById(R.id.thumb);
        btn_daren = (Button) this.findViewById(R.id.btn_daren);
        title = (TextView) this.findViewById(R.id.title);
        lowpoint = (TextView) this.findViewById(R.id.lowpoint);
        allnum = (TextView) this.findViewById(R.id.allnum);
        lastnum = (TextView) this.findViewById(R.id.lastnum);
        endtime = (TextView) this.findViewById(R.id.endtime);
        content = (TextView) this.findViewById(R.id.content);
        mBtnOper = (Button) this.findViewById(R.id.btn_oper);
        btn_share = (Button) this.findViewById(R.id.btn_share);
        btn_share.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (null != share) {
                    share.showAtLocation(FindPointDetailActivity.this
                            .findViewById(R.id.main), Gravity.BOTTOM
                            | Gravity.CENTER_HORIZONTAL, 0, 0);
                }
            }
        });
        mBtnOper.setOnClickListener(this);
        btn_fav = (Button) this.findViewById(R.id.btn_fav);
        btn_fav.setOnClickListener(this);
    }

    // private void showShare(boolean silent, String platform, boolean
    // captureView) {
    // Context context = mContext;
    // final OnekeyShare oks = new OnekeyShare();
    // oks.setTitle("分享测试");
    // oks.setTitleUrl("http://mob.com");
    // String customText = "全口分享测试";
    // oks.setText(customText);
    // oks.setSilent(silent);
    // oks.setTheme(OnekeyShareTheme.CLASSIC);
    // oks.disableSSOWhenAuthorize();
    //
    // oks.show(context);
    // }

    private void loadData() {
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        data.put("id", String.valueOf(id));
        FindReq.show(mContext, data, new Listener<RespData>() {

            @Override
            public void onResponse(RespData response) {
                if (response.isSuccess()) {
                    Type type = new TypeToken<List<PointExchangeDetail>>() {
                    }.getType();
                    List<PointExchangeDetail> details = GsonHelper.parseObject(
                            GsonHelper.toJson(response.getData()), type);
                    if (null != details && !details.isEmpty()) {
                        detail = details.get(0);
                        configShareContent(detail.getSharedata());
                        updateDataView(detail);
                    }
                } else {
                    AndroidUtil.showToast(response.getTips());
                }

            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                AndroidUtil.showToast(mContext.getString(R.string.reqFailed));
            }
        });
    }

    private void updateDataView(PointExchangeDetail detail) {
        if (null != detail) {
            thumb.setDefaultImageResId(R.drawable.ob_db);
            thumb.setErrorImageResId(R.drawable.ob_db);
            thumb.setImageUrl(detail.getThumb(), imageLoader);
            if (detail.getIsdaren() == 1) {
                btn_daren.setVisibility(View.VISIBLE);
            }
            title.setText(detail.getTitle());
            String pointLowpoint = mContext
                    .getString(R.string.find_point_lowpoint);
            lowpoint.setText(String.format(pointLowpoint, detail.getLowpoint()));
            String fAllnum = mContext
                    .getString(R.string.find_point_detail_allnum);
            allnum.setText(String.format(fAllnum, detail.getAllnum()));
            String fLastnum = mContext
                    .getString(R.string.find_point_detail_lastnum);
            lastnum.setText(String.format(fLastnum, detail.getLastnum()));
            String fEndtime = mContext
                    .getString(R.string.find_point_detail_ori_endtime);
            endtime.setText(fEndtime);
            countDownTextView = new CountDownTextView(this, endtime,
                    detail.getEndtime());
            countDownTextView.start();
            content.setText(detail.getContent());
            mBtnOper.setText(detail.getButtontext());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backMain:
                cancelCountDownTextView();
                finish();
                break;
            case R.id.btn_oper:
                startActivity(FindPointOrderActivity.newIntent(id));
                break;
            case R.id.btn_fav:
                fav();
                Animation shake = AnimationUtils.loadAnimation(mContext,
                        R.anim.button_shake);
                // shake.reset();
                // shake.setFillAfter(true);
                btn_fav.startAnimation(shake);
                break;
            default:
                break;
        }
    }

    private void fav() {
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        data.put("id", String.valueOf(id));
        FindReq.fav(mContext, data, new Listener<RespData>() {

            @Override
            public void onResponse(RespData response) {
                if (response.isSuccess()) {
                    AndroidUtil.showToast(response.getTips());
                } else if (response.getSuccess().intValue() == 0) {
                    // 未登录
                    startActivity(LoginActivity.newOtherIntent());
                } else {
                    AndroidUtil.showToast(response.getTips());
                }
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                AndroidUtil.showToast(mContext.getString(R.string.reqFailed));
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /** 使用SSO授权必须添加如下代码 */
        // UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(
        // requestCode);
        // if (ssoHandler != null) {
        // ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        // }
    }

    private void cancelCountDownTextView() {
        if (null != countDownTextView) {
            countDownTextView.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        cancelCountDownTextView();
        ShareSDK.stopSDK(this);
        super.onDestroy();
    }

    public void onComplete(Platform plat, int action,
                           HashMap<String, Object> res) {

    }

    public void onCancel(Platform palt, int action) {
    }

    public void onError(Platform palt, int action, Throwable t) {
    }

    public boolean handleMessage(Message msg) {
        return false;
    }

}
