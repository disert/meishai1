package com.meishai.ui.fragment.usercenter;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;

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
 * 用户注册界面-第一步
 *
 * @author sh
 */
public class RegistActivity extends BaseActivity implements OnClickListener {

    private Button mBtnBack;
    private Button mBtnNext;
    private Button getVerifyCode;
    private Button btn_skip;
    private EditText phoneEt, verifyCodeEt;
    private TimeCount time;
    private String phone;

    public static Intent newIntent() {
        Intent intent = new Intent(GlobalContext.getInstance(),
                RegistActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.regist);
        initView();
    }

    private void initView() {
        mBtnBack = (Button) this.findViewById(R.id.backMain);
        mBtnBack.setOnClickListener(this);
        getVerifyCode = (Button) this.findViewById(R.id.verifyCode);
        getVerifyCode.setOnClickListener(this);
        mBtnNext = (Button) this.findViewById(R.id.btn_next);
        mBtnNext.setOnClickListener(this);
        mBtnNext.setClickable(false);
        phoneEt = (EditText) this.findViewById(R.id.phoneNub);
        verifyCodeEt = (EditText) this.findViewById(R.id.verifyCodeET);

        btn_skip = (Button) this.findViewById(R.id.btn_skip);
        btn_skip.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backMain:
                startActivity(LoginActivity.newIntent());
                finish();
                break;
            case R.id.verifyCode:
                getPhone();
                break;
            case R.id.btn_next:
                getVerifyCode();
                break;
            case R.id.btn_skip:
                startActivity(RegistSceondSetpActivity.newIntent());
                finish();
                break;
            default:
                break;
        }
    }

    private void getVerifyCode() {
        String verifyCode = verifyCodeEt.getText().toString();
        if (StringUtil.isVerifyCode(verifyCode, 4)) {
            sendRegFristSetpAction(verifyCode);
        } else {
            AndroidUtil.showToast(R.string.input_vc_hint);
        }
    }

    private void getPhone() {
        String number = phoneEt.getText().toString().trim();
        if (StringUtil.isTelNumAvailable(number)) {
            phone = number;
            setButtonEnable(false);
            sendGetVerifyCodeAction(number);
        } else {
            AndroidUtil.showToast(R.string.invalid_phone_numb);
        }
    }

    private void sendGetVerifyCodeAction(String phone) {
        try {
            DebugLog.d("User Input Phone Nubmer:" + phone);
            ReqData reqData = new ReqData();
            reqData.setC("public");
            reqData.setA("get_phone_code");
            Map<String, String> map = new HashMap<String, String>();
            map.put("userid", "0");
            map.put("phone", phone);
            map.put("type", "reg");
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

    private void sendRegFristSetpAction(String verifyCode) {
        try {
            DebugLog.d("Verify Code:" + verifyCode);
            ReqData reqData = new ReqData();
            reqData.setC("member");
            reqData.setA("reg_step1");
            Map<String, String> map = new HashMap<String, String>();
            map.put("phone", phone);
            map.put("phone_code", verifyCode);
            reqData.setData(map);
            String jwtData = reqData.toReqString();
            if (jwtData != null && jwtData.length() > 0) {
                String url = getString(R.string.base_url) + jwtData;
                DebugLog.d(url);
                showProgress("", getString(R.string.network_wait));
                sendRegSetp1Req(url);
            } else {
                DebugLog.d("Get JWT JSONData Error.");
            }
        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    private void sendRegPhoneReq(String url) {
        getRequestQueue().add(new StringRequest(url, new Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideProgress();
                DebugLog.d("RSP:" + response);
                if (checkResult(response)) {
                    mBtnNext.setClickable(true);
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

    private void sendRegSetp1Req(String url) {
        getRequestQueue().add(new StringRequest(url, new Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideProgress();
                DebugLog.d("RSP:" + response);
                if (checkResult(response)) {
                    storageUserInfo();
                    startActivity(RegistSceondSetpActivity.newIntent());
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

    private void storageUserInfo() {
        UserInfo user = new UserInfo();
        user.setUserMobile(phone);
        MeiShaiSP.getInstance().setUserInfo(user);
    }

    private boolean checkResult(String json) {
        try {
            org.json.JSONObject jsonObj = new org.json.JSONObject(json);
            String successID = jsonObj.getString("success").trim();
            AndroidUtil.showToast(jsonObj.getString("tips").trim());
            if (successID.equals("1"))
                return true;
            else {
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

    }

    // @Override
    // public void onErrorResponse(VolleyError error) {
    // hideProgress();
    // }
    //
    // @Override
    // public void onResponse(String response) {
    // hideProgress();
    // String respData = response;
    // DebugLog.d("RSP:" + respData);
    // }

    private void setButtonEnable(boolean enable) {
        time = new TimeCount(60000, 1000);
        time.start();
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

    @Override
    public void onResume() {
        super.onResume();
    }
}
