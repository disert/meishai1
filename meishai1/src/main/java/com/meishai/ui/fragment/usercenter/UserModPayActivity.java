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
import android.widget.TextView;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.app.util.StringUtil;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.PayInfo;
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
 * 个人信息-支付信息-修改支付信息
 *
 * @ClassName: UserModPayFragment
 */
public class UserModPayActivity extends BaseActivity implements OnClickListener {

    private Button mBtnCancel, mBtnSave;
    private TextView title;
    private EditText etPayID, etPayName;
    private int typeID = 0;
    private PayInfo pay;
    private UserInfo userInfo;
    private String id, name;

    public static Intent newIntent(int type) {
        Intent intent = new Intent(GlobalContext.getInstance(),
                UserModPayActivity.class);
        intent.putExtra("Type", type);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_mod_pay);
        typeID = getIntent().getExtras().getInt("Type");
        pay = MeiShaiSP.getInstance().getPayInfo();
        userInfo = MeiShaiSP.getInstance().getUserInfo();
        initView();
    }

    /**
     * @param
     * @return void
     * @throws
     * @Title: initView
     */
    private void initView() {
        mBtnCancel = (Button) this.findViewById(R.id.btn_cancel);
        mBtnCancel.setOnClickListener(this);
        mBtnSave = (Button) this.findViewById(R.id.btn_save);
        mBtnSave.setOnClickListener(this);
        title = (TextView) this.findViewById(R.id.title);
        etPayID = (EditText) this.findViewById(R.id.payID);
        etPayName = (EditText) this.findViewById(R.id.payName);

        // 添加
        if (typeID == 0) {
            title.setText(R.string.title_add_pay);
        } else {
            // 修改
            title.setText(R.string.title_mod_pay);
            etPayID.setText(pay.getPayID());
            etPayName.setText(pay.getPayName());
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
                checkTypID();
                break;
            default:
                break;
        }
    }

    private void checkTypID() {
        id = etPayID.getText().toString();
        name = etPayName.getText().toString();
        if (StringUtil.checkUserNamePwd(id, 6)
                && StringUtil.checkUserNamePwd(name, 2)) {
            if (typeID == 0) {
                addPayInfo(id, name);
            } else {
                modifyPayInfo(id, name);
            }
        } else {
            AndroidUtil.showToast(R.string.invalid_id_name);
        }

    }

    private void addPayInfo(String id, String name) {
        try {
            ReqData reqData = new ReqData();
            reqData.setC("member");
            reqData.setA("payinfo_add");
            Map<String, String> map = new HashMap<String, String>();
            map.put("userid", userInfo.getUserID());
            map.put("alipay", id);
            map.put("payname", name);
            reqData.setData(map);
            String jwtData = reqData.toReqString();
            if (jwtData != null && jwtData.length() > 0) {
                String url = getString(R.string.base_url) + jwtData;
                DebugLog.d(url);
                showProgress("", getString(R.string.network_wait));
                sendAddPayReq(url);
            } else {
                DebugLog.d("Get JWT JSONData Error.");
            }
        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加支付信息和修改支付信息，结果处理方式是一样，故此用同一个方法。
     *
     * @param url
     */
    private void sendAddPayReq(String url) {
        getRequestQueue().add(new StringRequest(url, new Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideProgress();
                DebugLog.d("RSP:" + response);
                checkAddResult(response);
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
                AndroidUtil.showToast(R.string.reqFailed);
            }
        }));
    }

    private void storagePayInfo(PayInfo payInfo) {
        MeiShaiSP.getInstance().setPayInfo(payInfo);
    }

    private boolean checkAddResult(String json) {
        try {
            org.json.JSONObject jsonObj = new org.json.JSONObject(json);
            String successID = jsonObj.getString("success").trim();
            AndroidUtil.showToast(jsonObj.getString("tips").trim());
            if (successID.equals("1")) {
                PayInfo payInfo = new PayInfo();
                payInfo.setPayID(id);
                payInfo.setPayName(name);
                storagePayInfo(payInfo);
                return true;
            } else if (successID.equals("-2")) {
                PayInfo payInfo = new PayInfo();
                payInfo.setPayID(id);
                payInfo.setPayName(name);
                storagePayInfo(payInfo);
                startActivity(UserSecMobileActivity.newIntent());
                // gotoFragment(UserSecMobileFragment.class,UserSecMobileFragment.class.getName(),
                // null);
                return true;
            } else if (successID.equals("-1")) {
                PayInfo payInfo = new PayInfo();
                payInfo.setPayID(id);
                payInfo.setPayName(name);
                storagePayInfo(payInfo);
                // gotoFragment(UserLoginFragment.class,UserLoginFragment.class.getName(),
                // null);
                return true;
            } else {
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void modifyPayInfo(String id, String name) {
        try {
            ReqData reqData = new ReqData();
            reqData.setC("member");
            reqData.setA("payinfo_edit");
            Map<String, String> map = new HashMap<String, String>();
            map.put("userid", userInfo.getUserID());
            map.put("alipay", id);
            map.put("payname", name);
            reqData.setData(map);
            String jwtData = reqData.toReqString();
            if (jwtData != null && jwtData.length() > 0) {
                String url = getString(R.string.base_url) + jwtData;
                DebugLog.d(url);
                showProgress("", "请稍后...");
                sendAddPayReq(url);
            } else {
                DebugLog.d("Get JWT JSONData Error.");
            }
        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }
}
