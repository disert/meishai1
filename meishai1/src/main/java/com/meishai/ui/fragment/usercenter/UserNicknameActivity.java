package com.meishai.ui.fragment.usercenter;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.app.util.StringUtil;
import com.meishai.app.widget.EditTextWithDel;
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
 * 个人信息-我的昵称
 *
 * @ClassName: UserNicknameActivity
 */
public class UserNicknameActivity extends BaseActivity implements
        OnClickListener {

    private Button mBtnCancel, mBtnOk;
    private EditTextWithDel nickName;
    private UserInfo userInfo;
    private String name = null;

    public static Intent newIntent() {
        Intent intent = new Intent(GlobalContext.getInstance(),
                UserNicknameActivity.class);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userInfo = MeiShaiSP.getInstance().getUserInfo();
        setContentView(R.layout.user_nickname);
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
        nickName = (EditTextWithDel) this.findViewById(R.id.nickName);
        mBtnCancel = (Button) this.findViewById(R.id.btn_cancel);
        mBtnCancel.setOnClickListener(this);
        mBtnOk = (Button) this.findViewById(R.id.btn_save);
        mBtnOk.setOnClickListener(this);
        if (userInfo != null && userInfo.getUserNickName() != null) {
            nickName.setText(userInfo.getUserNickName());
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
                saveNickName();
            default:
                break;
        }
    }

    private void saveNickName() {
        name = nickName.getText().toString();
        try {
            DebugLog.d("Nick Name:" + name);
            ReqData reqData = new ReqData();
            reqData.setC("member");
            reqData.setA("username_edit");
            Map<String, String> map = new HashMap<String, String>();
            map.put("userid", userInfo.getUserID());
            map.put("username", name);
            reqData.setData(map);
            String jwtData = reqData.toReqString();
            if (jwtData != null && jwtData.length() > 0) {
                String url = getString(R.string.base_url) + jwtData;
                DebugLog.d(url);
                showProgress("", getString(R.string.network_wait));
                sendRegPhoneReq(url);
            } else {
                DebugLog.d("Get JWT JSONData Error.");
                AndroidUtil.showToast(R.string.reqFailed);
            }
        } catch (JOSEException e) {
            e.printStackTrace();
            AndroidUtil.showToast(R.string.reqFailed);
        }
    }

    private void sendRegPhoneReq(String url) {
        getRequestQueue().add(new StringRequest(url, new Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideProgress();
                DebugLog.d("RSP:" + response);
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

    private boolean checkResult(String json) {
        try {
            org.json.JSONObject jsonObj = new org.json.JSONObject(json);
            String successID = jsonObj.getString("success").trim();
            AndroidUtil.showToast(jsonObj.getString("tips").trim());
            if (successID.equals("1")) {
                userInfo.setUserNickName(name);
                DebugLog.d("UserName:" + userInfo.getUserNickName());
                MeiShaiSP.getInstance().setUserInfo(userInfo);
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
