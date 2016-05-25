package com.meishai.ui.fragment.usercenter;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.app.util.StringUtil;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.UserInfo;
import com.meishai.net.ReqData;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.StringRequest;
import com.meishai.ui.base.BaseActivity;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;
import com.nimbusds.jose.JOSEException;

/**
 * 安全设置->绑定邮箱
 *
 * @author sh
 */
public class UserSecEmailActivity extends BaseActivity implements
        OnClickListener {

    private Context mContext = UserSecEmailActivity.this;
    private Button mBtnCancel, mBtnSave, getVerifyCode;
    private EditText emailET, vcET;
    private TimeCount time;
    private UserInfo userInfo;
    private String email, verifyCode;

    public static Intent newIntent() {
        Intent intent = new Intent(GlobalContext.getInstance(),
                UserSecEmailActivity.class);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userInfo = MeiShaiSP.getInstance().getUserInfo();
        setContentView(R.layout.user_sec_email);
        initView();
    }

    /**
     * @param
     * @return void
     * @throws
     * @Title: initView
     * @Description:
     */
    private void initView() {
        mBtnCancel = (Button) this.findViewById(R.id.btn_cancel);
        mBtnCancel.setOnClickListener(this);
        mBtnSave = (Button) this.findViewById(R.id.btn_save);
        mBtnSave.setOnClickListener(this);
        getVerifyCode = (Button) this.findViewById(R.id.btn_verify_code);
        getVerifyCode.setOnClickListener(this);
        emailET = (EditText) this.findViewById(R.id.userEmail);
        vcET = (EditText) this.findViewById(R.id.verifyCode);
    }

    /**
     * <p>
     * Title: onClick
     * </p>
     * <p>
     * Description:
     * </p>
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                AndroidUtil.hideSoftInput(mContext);
                finish();
                break;
            case R.id.btn_save:
                changeUserEmail();
                break;
            case R.id.btn_verify_code:
                getVerifyCode();
                break;
            default:
                break;
        }
    }

    private void getVerifyCode() {
        String number = emailET.getText().toString().trim();
        if (StringUtil.isNotBlank(number)) {
            email = number;
            sendGetVerifyCodeAction(number);
        } else {
            AndroidUtil.showToast(R.string.invalid_email_not_null);
        }
        // if (StringUtil.checkEmail(number)) {
        // email = number;
        // sendGetVerifyCodeAction(number);
        // } else {
        // AndroidUtil.showToast(R.string.invalid_email);
        // }
    }

    private void changeUserEmail() {
        String number = emailET.getText().toString().trim();
        if (StringUtil.isBlank(number)) {
            AndroidUtil.showToast(R.string.invalid_email_not_null);
            return;
        }
        email = number;

        String vc = vcET.getText().toString();
        if (StringUtil.isVerifyCode(vc, 4)) {
            verifyCode = vc;
            changeUserEmailReq(verifyCode);
        } else {
            AndroidUtil.showToast(R.string.input_vc_hint);
        }
    }

    private void setButtonEnable(boolean enable) {
        time = new TimeCount(30000, 1000);
        time.start();
    }

    private void sendGetVerifyCodeAction(String email) {
        try {
            ReqData reqData = new ReqData();
            reqData.setC("public");
            reqData.setA("get_email_code");
            Map<String, String> map = new HashMap<String, String>();
            map.put("userid", userInfo.getUserID());
            map.put("email", email);
            reqData.setData(map);
            String jwtData = reqData.toReqString();
            if (jwtData != null && jwtData.length() > 0) {
                String url = getString(R.string.base_url) + jwtData;
                DebugLog.d(url);
                showProgress("", getString(R.string.network_wait));
                sendRegCodeReq(url);
            } else {
                DebugLog.d("Get JWT JSONData Error.");
            }
        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    private void sendRegCodeReq(String url) {
        getRequestQueue().add(new StringRequest(url, new Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideProgress();
                if (checkResultCode(response)) {
                    mBtnSave.setClickable(true);
                }
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
                AndroidUtil.showToast(R.string.reqFailed);
            }
        }));
    }

    private void sendRegPhoneReq(String url) {
        getRequestQueue().add(new StringRequest(url, new Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideProgress();
                DebugLog.d("RSP:" + response);
                if (checkResult(response)) {
                    mBtnSave.setClickable(true);
                    finish();
                }
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
                AndroidUtil.showToast(R.string.reqFailed);
            }
        }));
    }

    private boolean checkResultCode(String json) {
        try {
            org.json.JSONObject jsonObj = new org.json.JSONObject(json);
            String successID = jsonObj.getString("success").trim();
            AndroidUtil.showToast(jsonObj.getString("tips").trim());
            if (successID.equals("1")) {
                setButtonEnable(false);
                return true;
            } else {
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

    }

    private boolean checkResult(String json) {
        try {
            org.json.JSONObject jsonObj = new org.json.JSONObject(json);
            String successID = jsonObj.getString("success").trim();
            AndroidUtil.showToast(jsonObj.getString("tips").trim());
            if (successID.equals("1")) {
                userInfo.setUserEmail(email);
                MeiShaiSP.getInstance().setUserInfo(userInfo);
                userInfo = MeiShaiSP.getInstance().getUserInfo();
                if (null != time) {
                    time.onFinish();
                }
                return true;
            } else {
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

    }

    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            getVerifyCode.setText("重新验证");
            getVerifyCode.setClickable(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程显示
            getVerifyCode.setClickable(false);
            getVerifyCode.setText(millisUntilFinished / 1000 + "秒");
        }
    }

    private void changeUserEmailReq(String verifyCode) {
        try {
            ReqData reqData = new ReqData();
            reqData.setC("member");
            reqData.setA("email_verify");
            Map<String, String> map = new HashMap<String, String>();
            map.put("userid", userInfo.getUserID());
            map.put("email", email);
            map.put("email_code", verifyCode);
            reqData.setData(map);
            String jwtData = reqData.toReqString();
            if (jwtData != null && jwtData.length() > 0) {
                String url = getString(R.string.base_url) + jwtData;
                DebugLog.d(url);
                showProgress("", getString(R.string.network_wait));
                sendRegPhoneReq(url);
            } else {
                DebugLog.d("Get JWT JSONData Error.");
            }
        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }
}
