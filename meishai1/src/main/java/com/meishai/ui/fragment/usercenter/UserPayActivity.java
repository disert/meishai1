package com.meishai.ui.fragment.usercenter;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meishai.GlobalContext;
import com.meishai.R;
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
 * 个人信息-支付信息
 *
 * @author sh
 */
public class UserPayActivity extends BaseActivity implements OnClickListener {

    private LinearLayout lay_pay;
    private Button mBtnCancel;
    private Button mBtnApplyMod;
    private TextView tvPayID, tvPayName;
    private UserInfo userInfo;
    private PayInfo pay;

    public static Intent newIntent() {
        Intent intent = new Intent(GlobalContext.getInstance(),
                UserPayActivity.class);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_pay);
        userInfo = MeiShaiSP.getInstance().getUserInfo();
        pay = MeiShaiSP.getInstance().getPayInfo();
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
        lay_pay = (LinearLayout) this.findViewById(R.id.lay_pay);
        mBtnCancel = (Button) this.findViewById(R.id.btn_cancel);
        mBtnCancel.setOnClickListener(this);
        mBtnApplyMod = (Button) this.findViewById(R.id.btn_apply_mod);
        // 标记修改
        mBtnApplyMod.setTag("mod");
        mBtnApplyMod.setOnClickListener(this);
        tvPayID = (TextView) this.findViewById(R.id.payID);
        tvPayName = (TextView) this.findViewById(R.id.payName);
    }

    @Override
    protected void onResume() {
        super.onResume();
        syncUserPayInfo();
    }

    private void initData() {
        tvPayID.setText(pay.getPayID());
        tvPayName.setText(pay.getPayName());
    }

    private void syncUserPayInfo() {
        try {
            ReqData reqData = new ReqData();
            reqData.setC("member");
            reqData.setA("payinfo");
            Map<String, String> map = new HashMap<String, String>();
            map.put("userid", userInfo.getUserID());
            reqData.setData(map);
            String jwtData = reqData.toReqString();
            if (jwtData != null && jwtData.length() > 0) {
                String url = getString(R.string.base_url) + jwtData;
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
     * @param url
     */
    private void sendAddPayReq(String url) {
        getRequestQueue().add(new StringRequest(url, new Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideProgress();
                if (checkAddResult(response)) {
                    mBtnApplyMod.setText(R.string.txt_apply_mod);
                    pay = MeiShaiSP.getInstance().getPayInfo();
                    lay_pay.setVisibility(View.VISIBLE);
                    initData();
                } else {
                    // 标记添加
                    mBtnApplyMod.setText(R.string.title_add_pay);
                    mBtnApplyMod.setTag("add");
                    lay_pay.setVisibility(View.GONE);
                    // startActivity(UserModPayActivity.newIntent(0));
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

    private void storagePayInfo(PayInfo payInfo) {
        MeiShaiSP.getInstance().setPayInfo(payInfo);
    }

    private boolean checkAddResult(String json) {
        try {
            org.json.JSONObject jsonObj = new org.json.JSONObject(json);
            String successID = jsonObj.getString("success").trim();
            if (successID.equals("1")) {
                PayInfo payInfo = new PayInfo();
                payInfo.setPayID(jsonObj.getJSONObject("data")
                        .getString("alipay").trim());
                payInfo.setPayName(jsonObj.getJSONObject("data")
                        .getString("payname").trim());
                storagePayInfo(payInfo);
                return true;
            } else {
                // AndroidUtil.showToast(jsonObj.getString("tips").trim());
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
            case R.id.btn_apply_mod:
                String oper = (String) v.getTag();
                if (oper.equals("mod")) {
                    // 修改
                    startActivity(UserModPayActivity.newIntent(1));
                } else {
                    // 添加
                    startActivity(UserModPayActivity.newIntent(0));
                }
                break;
            default:
                break;
        }
    }
}
