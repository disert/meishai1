package com.meishai.ui.fragment.tryuse;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.app.util.DateUtil;
import com.meishai.app.widget.CountDownTextView;
import com.meishai.app.widget.CustomProgress;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.ShareData;
import com.meishai.entiy.TryuseDetail;
import com.meishai.entiy.TryuseDetail.Pic;
import com.meishai.net.RespData;
import com.meishai.net.VolleyHelper;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.NetworkImageView;
import com.meishai.ui.base.BaseActivity;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.dialog.PerfectPersonalDialog;
import com.meishai.ui.fragment.meiwu.FindPointOrderActivity;
import com.meishai.ui.fragment.tryuse.req.TryReq;
import com.meishai.ui.fragment.usercenter.LoginActivity;
import com.meishai.ui.popup.SharePopupWindow;
import com.meishai.util.AndroidUtil;
import com.meishai.util.GsonHelper;

/**
 * 试用详情
 *
 * @author sh
 */
public class TryuseDetailActivity extends BaseActivity implements
        OnClickListener {

    private Activity mContext = TryuseDetailActivity.this;
    private CountDownTextView countDownTextView = null;
    private ImageLoader imageLoader = null;
    private String backTitle = "";
    private Button mBtnBack;
    private long id;
    private NetworkImageView thumb;
    private TextView title;
    private TextView app_tv;
    private TextView endtime;
    private TextView tv_snum;
    private TextView text1;
    private TextView text2;
    private LinearLayout lay_pic;
    private Button mBtnOper;
    private View view_line;
    private Button btn_fav;
    private Button btn_share;

    private PerfectPersonalDialog personalDialog;

    private CustomProgress mProgressDialog;

    private SharePopupWindow share;

    private boolean isProgress = true;

    // private AreaPopupWindow areaPopup;

    public static Intent newIntent(Object id, String title) {
        Intent intent = new Intent(GlobalContext.getInstance(),
                TryuseDetailActivity.class);
        intent.putExtra(ConstantSet.BUNDLE_ID, Long.parseLong(id.toString()));
        intent.putExtra("title", title);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageLoader = VolleyHelper.getImageLoader(mContext);
        setContentView(R.layout.tryuse_detail);
        if (null != getIntent().getExtras()) {
            id = getIntent().getExtras().getLong(ConstantSet.BUNDLE_ID);
            backTitle = getIntent().getExtras().getString("title");
        }
        this.initView();
        this.initDialog();
        this.initPopup();
    }

    @Override
    protected void onResume() {
        this.loadData();
        super.onResume();
    }

    private void showProgressDialog() {
        if (null == mProgressDialog) {
            mProgressDialog = CustomProgress.show(mContext,
                    mContext.getString(R.string.network_wait), true, null);
        } else {
            mProgressDialog.show();
        }
    }

    private void initView() {
        mBtnBack = (Button) findViewById(R.id.backMain);
        if (!TextUtils.isEmpty(backTitle)) {
            mBtnBack.setText(backTitle);
        }
        mBtnBack.setOnClickListener(this);
        thumb = (NetworkImageView) findViewById(R.id.thumb);
        title = (TextView) findViewById(R.id.title);
        app_tv = (TextView) findViewById(R.id.app_tv);
        endtime = (TextView) findViewById(R.id.endtime);
        tv_snum = (TextView) findViewById(R.id.tv_snum);
        text1 = (TextView) findViewById(R.id.text1);
        text2 = (TextView) findViewById(R.id.text2);
        view_line = (View) findViewById(R.id.view_line);
        lay_pic = (LinearLayout) findViewById(R.id.lay_pic);
        mBtnOper = (Button) findViewById(R.id.btn_oper);
        // mBtnOper.setOnClickListener(this);
        btn_fav = (Button) findViewById(R.id.btn_fav);
        btn_fav.setOnClickListener(this);
        btn_share = (Button) this.findViewById(R.id.btn_share);
        btn_share.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (null != share) {
                    share.showAtLocation(
                            TryuseDetailActivity.this.findViewById(R.id.main),
                            Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                }
            }
        });
    }

    private void initDialog() {
        personalDialog = new PerfectPersonalDialog(mContext);
    }

    private void initPopup() {
        // areaPopup = new AreaPopupWindow(this, new OnAreaChangeListener() {
        //
        // @Override
        // public void onChange(int provinceid, String provinceName,
        // int areaid, String name) {
        // if (null != personalDialog) {
        // personalDialog.setArea(provinceid, provinceName, areaid,
        // name);
        // }
        // }
        // });
    }

    private void configShareContent(ShareData shareData) {
        if (null == shareData) {
            return;
        }
        share = new SharePopupWindow(this);
        share.initShareParams(shareData);
        share.showShareWindow();
    }

    private void loadData() {
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        data.put("sid", String.valueOf(id));
        if (isProgress) {
            showProgress("", getString(R.string.network_wait));
        }
        TryReq.show(mContext, data, new Listener<RespData>() {

            @Override
            public void onResponse(RespData response) {
                if (isProgress) {
                    hideProgress();
                    isProgress = false;
                }
                if (response.isSuccess()) {
                    TryuseDetail details = GsonHelper.parseObject(
                            GsonHelper.toJson(response.getData()),
                            TryuseDetail.class);
                    if (null != details) {
                        updateDataView(details);
                        configShareContent(details.getSharedata());
                    }
                } else {
                    AndroidUtil.showToast(response.getTips());
                }

            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (isProgress) {
                    hideProgress();
                    isProgress = false;
                }
                AndroidUtil.showToast(mContext.getString(R.string.reqFailed));
            }
        });
    }

    private void updateDataView(TryuseDetail detail) {
        if (null != detail) {
            thumb.setDefaultImageResId(R.drawable.head_default);
            thumb.setErrorImageResId(R.drawable.head_default);
            thumb.setImageUrl(detail.getThumb(), imageLoader);
            title.setText(detail.getTitle());
            String fEndtime = mContext
                    .getString(R.string.find_point_detail_ori_endtime);
            endtime.setText(fEndtime);
            countDownTextView = new CountDownTextView(this, endtime,
                    Long.parseLong(detail.getEndtime()));
            countDownTextView.start();

            endtime.setText(String.format(fEndtime,
                    DateUtil.timeFormat(Long.parseLong(detail.getEndtime()))));
            String fAppTv = mContext.getString(R.string.tryuse_detail_app_tv);
            app_tv.setText(String.format(fAppTv, detail.getAppnum(),
                    detail.getChecknum(), detail.getOrdernum()));
            String fTvSnum = mContext.getString(R.string.tryuse_detail_tv_snum);
            tv_snum.setText(String.format(fTvSnum, detail.getSnum(),
                    detail.getPrice(), detail.getGprice()));
            text1.setText(detail.getText1());
            text2.setText(detail.getText2());
            if (detail.getIsapp() == 1) {
                mBtnOper.setBackgroundResource(R.color.txt_color);
            } else {
                mBtnOper.setOnClickListener(this);
            }
            addPicView(detail);
        }
    }

    private void addPicView(TryuseDetail detail) {

        if (null != detail.getPicList()) {
            for (Pic p : detail.getPicList()) {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                View picViw = inflater.inflate(R.layout.tryuse_detail_pic_item,
                        null);
                NetworkImageView pic = (NetworkImageView) picViw
                        .findViewById(R.id.pic);
                LayoutParams params = (LayoutParams) pic.getLayoutParams();
                params.height = Integer.parseInt(p.getHeight());
                pic.setImageUrl(p.getUrl(), imageLoader);
                lay_pic.addView(picViw);
            }
        } else {
            view_line.setVisibility(View.INVISIBLE);
            lay_pic.setVisibility(View.INVISIBLE);
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
                apply();
                break;
            case R.id.btn_fav:
                Animation shake = AnimationUtils.loadAnimation(mContext,
                        R.anim.button_shake);
                btn_fav.startAnimation(shake);
                fav();
                break;
            default:
                break;
        }
    }

    private void apply() {
        mBtnOper.setEnabled(false);
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        data.put("sid", String.valueOf(id));
        showProgressDialog();
        TryReq.app(mContext, data, new Listener<RespData>() {

            @Override
            public void onResponse(RespData response) {
                // success：-3（跳转到确认收货地址页面）
                // success：-2（会员资料不全，打开补充会员资料面板）
                // success：-1（未登录，跳转到登录页面）
                // success：0（错误）
                // success：1（成功）
                mBtnOper.setEnabled(true);
//				mBtnOper.setClickable(true);
                mProgressDialog.hide();
                if (response.isSuccess()) {
                    AndroidUtil.showToast(response.getTips());
                } else if (response.getSuccess().intValue() == -2) {
                    personalDialog.setSid(id);
                    personalDialog.show();
                } else if (response.getSuccess().intValue() == -1) {
                    // 未登录
                    startActivity(LoginActivity.newOtherIntent());
                } else if (response.getSuccess().intValue() == -3) {
                    startActivity(FindPointOrderActivity.newIntent(id));
                } else {
                    AndroidUtil.showToast(response.getTips());
                }
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mBtnOper.setEnabled(true);
                mProgressDialog.hide();
                AndroidUtil.showToast(mContext.getString(R.string.reqFailed));
            }
        });
    }

    private void fav() {
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        data.put("sid", String.valueOf(id));
        TryReq.fav(mContext, data, new Listener<RespData>() {

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
        super.onDestroy();
    }
}
