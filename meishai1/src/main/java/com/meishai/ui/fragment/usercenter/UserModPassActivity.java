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
 * 个人信息-修改密码
 *
 * @ClassName: UserModPassActivity
 */
public class UserModPassActivity extends BaseActivity implements OnClickListener {

    private Button mBtnCancel, mBtnSave;
    private EditText etOldPwd, etNewPwd, etNewPwdRepid;

    private String oldPwd, newPwd;
    private UserInfo userInfo;

    public static Intent newIntent() {
        Intent intent = new Intent(GlobalContext.getInstance(),
                UserModPassActivity.class);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userInfo = MeiShaiSP.getInstance().getUserInfo();
        setContentView(R.layout.user_mod_pass);
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
        etOldPwd = (EditText) this.findViewById(R.id.oldPwd);
        etNewPwd = (EditText) this.findViewById(R.id.newPwd);
        etNewPwdRepid = (EditText) this.findViewById(R.id.newPwdRepit);
    }

    /**
     * <p>Title: onClick</p>
     * <p>Description: </p>
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
                reviseUserPWD();
                break;
            default:
                break;
        }
    }

    private void reviseUserPWD() {
        if (checkUserInputPwd()) {
            doRevisePwdReq();
        }
    }

    private boolean checkUserInputPwd() {
        String old = etOldPwd.getText().toString();
        String str1 = etNewPwd.getText().toString();
        String str2 = etNewPwdRepid.getText().toString();
        if (str1.equals(str2)) {
            if (StringUtil.isNotBlank(str1)) {
                oldPwd = old;
                newPwd = str1;
                return true;
            } else {
                AndroidUtil.showToast(R.string.invalid_new_pwd_not_null);
            }

//			if(StringUtil.checkUserNamePwd(str1, 6)){
//				oldPwd = old;
//				newPwd = str1;
//				return true;
//			}else{
//				AndroidUtil.showToast(R.string.invalid_pwd);
//			}
        } else {
            AndroidUtil.showToast(R.string.invalid_new_pwd);
        }
        return false;
    }

    private void doRevisePwdReq() {
        try {
            ReqData reqData = new ReqData();
            reqData.setC("member");
            reqData.setA("password");
            Map<String, String> map = new HashMap<String, String>();
            map.put("userid", userInfo.getUserID());
            map.put("old_password", oldPwd);
            map.put("new_password", newPwd);
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
                if (checkResult(response)) {
                    storageUserInfo();
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
        MeiShaiSP.getInstance().setUserInfo(userInfo);
    }

    private boolean checkResult(String json) {
        try {
            org.json.JSONObject jsonObj = new org.json.JSONObject(json);
            String successID = jsonObj.getString("success").trim();
            AndroidUtil.showToast(jsonObj.getString("tips").trim());
            if (successID.equals("1")) {
                userInfo.setUserPWD(newPwd);
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
