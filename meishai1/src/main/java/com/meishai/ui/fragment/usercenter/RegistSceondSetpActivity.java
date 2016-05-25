package com.meishai.ui.fragment.usercenter;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.app.util.StringUtil;
import com.meishai.app.widget.wheel.AreaWheel.OnAreaChangeListener;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.UserInfo;
import com.meishai.net.ReqData;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.StringRequest;
import com.meishai.ui.base.BaseActivity;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.popup.AreaPopupWindow;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;
import com.nimbusds.jose.JOSEException;

/**
 * 用户注册界面-第二步
 *
 * @author sh
 */
public class RegistSceondSetpActivity extends BaseActivity implements
        OnClickListener {

    private LinearLayout lay_main;
    private RadioGroup mRadioGroup;
    private EditText userName, pwd, email;
    private TextView area;
    private Button mBtnBack, commitBtn;
    private UserInfo user;
    private AreaPopupWindow areaPopup;

    public static Intent newIntent() {
        Intent intent = new Intent(GlobalContext.getInstance(),
                RegistSceondSetpActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.regist2);
        initView();
        initPopup();
    }

    private void initView() {
        lay_main = (LinearLayout) this.findViewById(R.id.lay_main);
        mBtnBack = (Button) this.findViewById(R.id.backMain);
        mBtnBack.setOnClickListener(this);
        commitBtn = (Button) this.findViewById(R.id.btn_finish);
        commitBtn.setOnClickListener(this);
        mRadioGroup = (RadioGroup) this.findViewById(R.id.rgType);

        userName = (EditText) this.findViewById(R.id.etUserName);
        pwd = (EditText) this.findViewById(R.id.etPWD);
//		qq = (EditText) this.findViewById(R.id.etQQ);
        email = (EditText) this.findViewById(R.id.etEmail);
        area = (TextView) this.findViewById(R.id.etArea);
        area.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                areaPopup.showPop(lay_main);

            }
        });
        initRaidoGroup();
    }

    private void initPopup() {
        areaPopup = new AreaPopupWindow(this, new OnAreaChangeListener() {

            @Override
            public void onChange(int provinceid, String provinceName,
                                 int areaid, String name) {
                area.setTag(areaid);
                area.setText(provinceName + "-" + name);
            }
        });
    }

    private void initRaidoGroup() {
        user = MeiShaiSP.getInstance().getUserInfo();
        //默认类型会员  会员:0,商家:1
        user.setUserType(0);
        mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_seller) {
                    user.setUserType(1);
                } else if (checkedId == R.id.rb_member) {
                    user.setUserType(0);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backMain:
//			startActivity(RegistActivity.newIntent());
                startActivity(LoginActivity.newIntent());
                finish();
                break;
            case R.id.btn_finish:
                finishRegist();
                break;
            default:
                break;
        }
    }

    private boolean checkUserInfo() {
        String nickName = getEditTextValue(userName);
        String userPwd = getEditTextValue(pwd);
//		String userQQ = getEditTextValue(qq);
        String userEmail = getEditTextValue(email);
        user.setUserEmail(userEmail);
        if (StringUtil.isNotBlank(nickName)) {
            user.setUserNickName(nickName);
        } else {
            AndroidUtil.showToast(R.string.username_not_null);
            userName.setText("");
            pwd.setText("");
            return false;
        }

        if (StringUtil.isNotBlank(userPwd)) {
            user.setUserPWD(userPwd);
        } else {
            AndroidUtil.showToast(R.string.pwd_not_null);
            pwd.setText("");
            return false;
        }

//		if (StringUtil.checkQQ(userQQ)) {
//			user.setUserQQ(userQQ);
//		} else {
//			AndroidUtil.showToast(R.string.invalid_qq);
//			qq.setText("");
//			pwd.setText("");
//			return false;
//		}

//		if (StringUtil.checkEmail(userEmail)) {
//			user.setUserEmail(userEmail);
//		} else {
//			AndroidUtil.showToast(R.string.invalid_email);
//			email.setText("");
//			pwd.setText("");
//			return false;
//		}

        Object obj = area.getTag();
        if (null != obj) {
            String areaId = area.getTag().toString();
            user.setAreaid(areaId);
        }
        return true;

    }

    private String getEditTextValue(EditText et) {
        return et.getText().toString().trim();
    }

    private void finishRegist() {
        if (checkUserInfo()) {
            sendFinishRegAction();
        }
    }

    private void sendFinishRegAction() {
        try {
            ReqData reqData = new ReqData();
            reqData.setC("member");
            reqData.setA("reg_step2");
            Map<String, String> map = new HashMap<String, String>();
            map.put("phone", StringUtil.isBlank(user.getUserMobile()) ? "" : user.getUserMobile());
            map.put("type", user.getUserType() + "");
            map.put("username", user.getUserNickName());
            map.put("password", user.getUserPWD());
//			map.put("qq", user.getUserQQ());
            map.put("email", user.getUserEmail());
            map.put("areaid", user.getAreaid());
            reqData.setData(map);
            String jwtData = reqData.toReqString();
            if (jwtData != null && jwtData.length() > 0) {
                String url = getString(R.string.base_url) + jwtData;
                DebugLog.d(url);
                showProgress("", getString(R.string.network_wait));
                sendRegReq(url);
            } else {
                DebugLog.d("Get JWT JSONData Error.");
            }
        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    private void sendRegReq(String url) {
        getRequestQueue().add(new StringRequest(url, new Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideProgress();
                DebugLog.d("RSP:" + response);
                if (checkResult(response)) {
                    storageUserInfo();
                    loginSuccess();
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

    // 注册成功发送广播
    private void loginSuccess() {
        Intent mIntent = new Intent(ConstantSet.ACTION_LOGIN_SUCCESS);
        sendBroadcast(mIntent);
        finish();
    }

    private void storageUserInfo() {
        MeiShaiSP.getInstance().setUserInfo(user);
    }

    private boolean checkResult(String json) {
        try {
            org.json.JSONObject jsonObj = new org.json.JSONObject(json);
            String successID = jsonObj.getString("success").trim();
            AndroidUtil.showToast(jsonObj.getString("tips").trim());
            if (successID.equals("1")) {
                user.setUserID(jsonObj.getJSONObject("data")
                        .getString("userid").trim());
                return true;
            } else {
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

}
