package com.meishai.ui.dialog;

import java.util.HashMap;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.app.widget.CustomProgress;
import com.meishai.app.widget.wheel.AreaWheel.OnAreaChangeListener;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.UserProfile;
import com.meishai.entiy.UserWang;
import com.meishai.net.RespData;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.tryuse.req.TryReq;
import com.meishai.ui.popup.AreaPopupWindow;
import com.meishai.util.AndroidUtil;
import com.meishai.util.GsonHelper;
import com.meishai.util.StringUtils;

/**
 * 完善个人资料
 *
 * @author sh
 */
public class PerfectPersonalDialog extends Dialog {

    private Context mContext;

    private AreaPopupWindow areaPopup;

    private long sid;

    private LinearLayout dialog_main;
    // 地区
    private LinearLayout lay_area;
    private TextView area;
    // 手机号码
    private LinearLayout lay_phone;
    private EditText phone;
    // qq
    private LinearLayout lay_qq;
    private EditText qq;
    // 联系旺旺 会员的旺旺数据 存在则用选择，不存在则显示输入框
    // 输入旺旺
    private LinearLayout lay_iwangwang;
    // 选择旺旺
    private LinearLayout lay_cwangwang;
    private LinearLayout lay_radio;
    private EditText wangwang;

    private Button mBtnClose;
    private Button mBtnSubmit;
    private CustomProgress mProgressDialog;
    // 用户个人信息
    private UserProfile profile = null;
    // 旺旺号
    private String tb_user_name = "";
    // 旺旺id
    private long tb_user_id = 0;

    private RadioButton chkButton = null;

    public PerfectPersonalDialog(Context context) {
        super(context, R.style.dialog_transparent);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_perfect_personal);
        this.initView();
        this.setListener();
        this.initPopup();
    }

    private void initPopup() {
        areaPopup = new AreaPopupWindow(mContext, new OnAreaChangeListener() {

            @Override
            public void onChange(int provinceid, String provinceName,
                                 int areaid, String name) {
                setArea(provinceid, provinceName, areaid, name);
            }
        });
    }

    public void setSid(long sid) {
        this.sid = sid;
    }

    @Override
    protected void onStart() {
        super.onStart();
        profile = null;
        chkButton = null;
        tb_user_name = "";
        tb_user_id = 0;
        this.loadData();
    }

    public void setArea(int provinceid, String provinceName, int areaid,
                        String name) {
        area.setTag(areaid);
        area.setText(provinceName + "-" + name);
    }

    private void initView() {
        dialog_main = (LinearLayout) this.findViewById(R.id.dialog_main);
        lay_area = (LinearLayout) this.findViewById(R.id.lay_area);
        area = (TextView) this.findViewById(R.id.area);
        lay_phone = (LinearLayout) this.findViewById(R.id.lay_phone);
        phone = (EditText) this.findViewById(R.id.phone);
        lay_qq = (LinearLayout) this.findViewById(R.id.lay_qq);
        qq = (EditText) this.findViewById(R.id.qq);
        lay_iwangwang = (LinearLayout) this.findViewById(R.id.lay_iwangwang);
        lay_cwangwang = (LinearLayout) this.findViewById(R.id.lay_cwangwang);
        lay_radio = (LinearLayout) this.findViewById(R.id.lay_radio);
        wangwang = (EditText) this.findViewById(R.id.wangwang);
        mBtnClose = (Button) this.findViewById(R.id.btn_close);
        mBtnSubmit = (Button) this.findViewById(R.id.btn_submit);
        area.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                areaPopup.showPop(dialog_main);
            }
        });
    }

    private void loadData() {
        showProgressDialog();
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        data.put("sid", String.valueOf(sid));
        TryReq.profile(mContext, data, new Listener<RespData>() {
            @Override
            public void onResponse(RespData response) {
                mProgressDialog.hide();
                if (response.isSuccess()) {
                    profile = GsonHelper.parseObject(
                            GsonHelper.toJson(response.getData()),
                            UserProfile.class);
                    if (null != profile) {
                        fillData(profile);
                    } else {
                        AndroidUtil.showToast(response.getTips());
                    }
                } else {
                    AndroidUtil.showToast(response.getTips());
                }
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.hide();
                AndroidUtil.showToast(R.string.reqFailed);
            }
        });
    }

    private void fillData(UserProfile profile) {
        if (null != profile) {

            if (null == profile.getAreaid()
                    || profile.getAreaid().intValue() == 0) {
                lay_area.setVisibility(View.VISIBLE);
            } else {
                lay_area.setVisibility(View.GONE);
            }

            if (StringUtils.isBlank(profile.getPhone())
                    || profile.getPhone().equals("0")) {
                lay_phone.setVisibility(View.VISIBLE);
            } else {
                lay_phone.setVisibility(View.GONE);
            }

            if (StringUtils.isBlank(profile.getQq())
                    || profile.getQq().equals("0")) {
                lay_qq.setVisibility(View.VISIBLE);
            } else {
                lay_qq.setVisibility(View.GONE);
            }
        }
        if (null == profile.getWangs() || profile.getWangs().isEmpty()) {
            // 选择旺旺
            lay_cwangwang.setVisibility(View.GONE);
            // 输入旺旺
            lay_iwangwang.setVisibility(View.VISIBLE);
        } else {
            // 输入旺旺
            lay_iwangwang.setVisibility(View.GONE);
            // 选择旺旺
            lay_cwangwang.setVisibility(View.VISIBLE);
            addWangwangView();
        }
    }

    private void addWangwangView() {
        lay_radio.removeAllViews();
        for (UserWang wang : profile.getWangs()) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View wView = inflater.inflate(
                    R.layout.dialog_perfect_personal_wangwang, null);
            RadioButton rbCheck = (RadioButton) wView
                    .findViewById(R.id.rb_check);
            rbCheck.setTag(wang);
            if (wang.getIsindex() == 1) {
                tb_user_id = wang.getTb_user_id();
                chkButton = rbCheck;
                rbCheck.setChecked(true);
            }
            rbCheck.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (null != chkButton) {
                        UserWang chkW = (UserWang) chkButton.getTag();
                        UserWang cW = (UserWang) v.getTag();
                        if (chkW.getTb_user_id() == cW.getTb_user_id()) {
                            return;
                        } else {
                            chkButton.setChecked(false);
                            ((RadioButton) v).setChecked(true);
                            chkButton = (RadioButton) v;
                        }
                    } else {
                        chkButton = (RadioButton) v;
                        chkButton.setChecked(true);
                    }
                    UserWang uW = (UserWang) chkButton.getTag();
                    tb_user_id = uW.getTb_user_id();
                    tb_user_name = uW.getTb_user_name();
                }
            });
            TextView tbName = (TextView) wView.findViewById(R.id.tb_name);
            tbName.setText(wang.getTb_user_name());
            lay_radio.addView(wView);
        }
    }

    private void setListener() {
        this.mBtnClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        this.mBtnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (checkData()) {
                    profileAdd();
                }
            }
        });
    }

    private void showProgressDialog() {
        // if (null == mProgressDialog) {
        mProgressDialog = CustomProgress.show(mContext,
                mContext.getString(R.string.network_wait), true, null);
        // } else {
        // mProgressDialog.show();
        // }
    }

    private boolean checkData() {
        if (null == profile) {
            profile = new UserProfile();
        }
        if ((null == profile.getAreaid() || profile.getAreaid().intValue() == 0)
                && TextUtils.isEmpty(area.getText().toString())) {
            AndroidUtil.showToast(R.string.tip_choose_area);
            return false;
        }
        if ((StringUtils.isBlank(profile.getPhone()) || profile.getPhone()
                .equals("0")) && TextUtils.isEmpty(phone.getText().toString())) {
            AndroidUtil.showToast(R.string.tip_input_phone);
            return false;
        }
        if ((StringUtils.isBlank(profile.getQq()) || profile.getQq()
                .equals("0")) && TextUtils.isEmpty(qq.getText().toString())) {
            AndroidUtil.showToast(R.string.tip_input_qq);
            return false;
        }

        if (null == profile.getWangs() || profile.getWangs().isEmpty()) {
            // 输入旺旺
            if (TextUtils.isEmpty(wangwang.getText().toString())) {
                AndroidUtil.showToast(R.string.tip_input_wang);
                return false;
            }
        } else {
            // 选择旺旺
            if (null == chkButton) {
                AndroidUtil.showToast(R.string.tip_choose_wang);
                return false;
            }
        }
        if (!TextUtils.isEmpty(area.getText().toString())) {
            profile.setAreaid(Integer.parseInt(area.getTag().toString()));
        }
        if (!TextUtils.isEmpty(phone.getText().toString())) {
            profile.setPhone(phone.getText().toString());
        }
        if (!TextUtils.isEmpty(qq.getText().toString())) {
            profile.setQq(qq.getText().toString());
        }

        if (null == profile.getWangs() || profile.getWangs().isEmpty()) {
            // 输入旺旺
            if (!TextUtils.isEmpty(wangwang.getText().toString())) {
                tb_user_name = wangwang.getText().toString();
                tb_user_id = 0;
            }
        } else {
            // 选择旺旺
        }
        return true;
    }

    private void profileAdd() {
        showProgressDialog();
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        data.put("sid", String.valueOf(sid));
        data.put("areaid", profile.getAreaid().toString());
        data.put("phone", profile.getPhone());
        data.put("qq", profile.getQq());
        data.put("tb_user_name", tb_user_name);
        data.put("tb_user_id", tb_user_id + "");
        TryReq.profileAdd(mContext, data, new Listener<RespData>() {
            @Override
            public void onResponse(RespData response) {
                mProgressDialog.hide();
                AndroidUtil.showToast(response.getTips());
                if (response.isSuccess() || response.getSuccess() == -11) {
                    dismiss();
                }
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.hide();
                AndroidUtil.showToast(R.string.reqFailed);
            }
        });
    }
}
