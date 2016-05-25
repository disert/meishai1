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

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.app.util.StringUtil;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.UserInfo;
import com.meishai.entiy.UserIntro;
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
 * 个人介绍
 *
 * @ClassName: UserIntrodActivity
 */
public class UserIntrodActivity extends BaseActivity implements OnClickListener {

    private Button mBtnCancel, mBtnCommit;
    private EditText introdEt;
    private UserInfo userInfo;
    private UserIntro userIntrol;
    private String introl;

    public static Intent newIntent() {
        Intent intent = new Intent(GlobalContext.getInstance(),
                UserIntrodActivity.class);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_introd);
        userInfo = MeiShaiSP.getInstance().getUserInfo();
        if (userInfo != null && userInfo.getUserID() != null) {
            userIntrol = MeiShaiSP.getInstance().getUserIntro(
                    userInfo.getUserID());
        }
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
        mBtnCommit = (Button) this.findViewById(R.id.btn_save);
        mBtnCommit.setOnClickListener(this);
        introdEt = (EditText) this.findViewById(R.id.userIntrod);
        refreshUI(userIntrol);
        syncUserIntorl();
    }

    private void refreshUI(UserIntro inrol) {
        if (inrol != null && inrol.getIntrol() != null) {
            introdEt.setText(inrol.getIntrol());
            mBtnCommit.setText(R.string.btn_mod);
        }
    }

    private void syncUserIntorl() {
        try {
            ReqData reqData = new ReqData();
            reqData.setC("member");
            reqData.setA("intro");
            Map<String, String> map = new HashMap<String, String>();
            map.put("userid", userInfo.getUserID());
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

    private void doRegPhoneReq(String url) {
        getRequestQueue().add(new StringRequest(url, new Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideProgress();
                if (checkResult(response)) {
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

    private void sendRegPhoneReq(String url) {
        getRequestQueue().add(new StringRequest(url, new Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideProgress();
                DebugLog.d("RSP:" + response);
                if (checkResult(response)) {
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

    private void storageUserIntrol(UserIntro userIntrol) {
        MeiShaiSP.getInstance().setUserIntro(userIntrol);
        refreshUI(userIntrol);
    }

    private boolean checkResult(String json) {
        try {
            org.json.JSONObject jsonObj = new org.json.JSONObject(json);
            String successID = jsonObj.getString("success").trim();
            AndroidUtil.showToast(jsonObj.getString("tips").trim());
            if (successID.equals("1")) {
                UserIntro userIntrol = new UserIntro();
                if (introl == null) {
                    userIntrol.setUserID(jsonObj.getJSONObject("data")
                            .getString("userid").trim());
                    userIntrol.setIntrol(jsonObj.getJSONObject("data")
                            .getString("intro").trim());
                } else {
                    userIntrol.setUserID(userInfo.getUserID());
                    userIntrol.setIntrol(introl);
                }
                storageUserIntrol(userIntrol);
                return true;
            } else {
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

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
                finish();
                break;
            case R.id.btn_save:
                saveUserIntrolInfo();
                break;
            default:
                break;
        }
    }

    private void saveUserIntrolInfo() {
        introl = getIntroData();
        if (introl != null) {
            doSaveIntrolReq(introl);
        }

    }

    private void doSaveIntrolReq(String introl) {
        try {
            ReqData reqData = new ReqData();
            reqData.setC("member");
            reqData.setA("intro_edit");
            Map<String, String> map = new HashMap<String, String>();
            map.put("userid", userInfo.getUserID());
            map.put("intro", introl);
            reqData.setData(map);
            String jwtData = reqData.toReqString();
            if (jwtData != null && jwtData.length() > 0) {
                String url = getString(R.string.base_url) + jwtData;
                DebugLog.d(url);
                showProgress("", getString(R.string.network_wait));
                doRegPhoneReq(url);
            } else {
                DebugLog.d("Get JWT JSONData Error.");
            }
        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    private String getIntroData() {
        String data = introdEt.getText().toString();
        if (StringUtil.checkUserNamePwd(data, 6)) {
            return data;
        } else {
            AndroidUtil.showToast(R.string.invalid_Intro);
            return null;
        }
    }
}
