package com.meishai.ui.fragment.usercenter;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.app.util.StringUtil;
import com.meishai.app.widget.wheel.AreaWheel.OnAreaChangeListener;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.ExchangeAddress;
import com.meishai.entiy.UserInfo;
import com.meishai.net.ReqData;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.StringRequest;
import com.meishai.ui.base.BaseActivity;
import com.meishai.ui.popup.AreaPopupWindow;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;
import com.nimbusds.jose.JOSEException;

/**
 * 个人信息->收货地址->编辑收货地址
 *
 * @author sh
 */
public class UserReceiveAddressActivity extends BaseActivity implements
        OnClickListener {

    // 添加
    private static final String OPER_TYPE_ADD = "add";
    // 修改
    public static final String OPER_TYPE_MOD = "mod";
    private LinearLayout lay_main;
    private Button mBtnCancel, mBtnSave;
    private TextView areaET;
    private EditText addresET, zipsET, realnameET, phoneET;
    private UserInfo userInfo;
    private AreaPopupWindow areaPopup;
    private RadioButton rb_yes;
    private RadioButton rb_no;

    private ExchangeAddress address = null;

    public static Intent newAddIntent() {
        Intent intent = new Intent(GlobalContext.getInstance(),
                UserReceiveAddressActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putString("type", UserReceiveAddressActivity.OPER_TYPE_ADD);
        intent.putExtras(mBundle);
        return intent;
    }

    public static Intent newModIntent(ExchangeAddress address) {
        Intent intent = new Intent(GlobalContext.getInstance(),
                UserReceiveAddressActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putSerializable("address", address);
        mBundle.putString("type", UserReceiveAddressActivity.OPER_TYPE_MOD);
        intent.putExtras(mBundle);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userInfo = MeiShaiSP.getInstance().getUserInfo();
        setContentView(R.layout.user_receive_address);
        initView();
        initData();
        initPopup();
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            String type = bundle.getString("type");
            if (OPER_TYPE_ADD.equals(type)) {
                refreshUI();
            } else if (OPER_TYPE_MOD.equals(type)) {
                address = (ExchangeAddress) getIntent().getSerializableExtra(
                        "address");
                refreshUI();
            }
        }

    }

    /**
     * @param
     * @return void
     * @throws
     * @Title: initView
     */
    private void initView() {
        lay_main = (LinearLayout) this.findViewById(R.id.lay_main);
        mBtnCancel = (Button) this.findViewById(R.id.btn_cancel);
        mBtnCancel.setOnClickListener(this);
        mBtnSave = (Button) this.findViewById(R.id.btn_save);
        mBtnSave.setOnClickListener(this);
        areaET = (TextView) this.findViewById(R.id.area);
        areaET.setOnClickListener(this);
        addresET = (EditText) this.findViewById(R.id.addres);
        zipsET = (EditText) this.findViewById(R.id.zips);
        realnameET = (EditText) this.findViewById(R.id.realName);
        phoneET = (EditText) this.findViewById(R.id.phone);
        rb_yes = (RadioButton) this.findViewById(R.id.rb_yes);
        rb_no = (RadioButton) this.findViewById(R.id.rb_no);
    }

    private void initPopup() {
        areaPopup = new AreaPopupWindow(this, new OnAreaChangeListener() {

            @Override
            public void onChange(int provinceid, String provinceName,
                                 int areaid, String name) {
                areaET.setTag(areaid);
                areaET.setText(provinceName + "-" + name);
            }
        });
    }

    private void revriseDA() {
        ExchangeAddress da = checkUserInput();
        if (da != null && da.getAddress() != null) {
            doRevriseReq(da);
        }
    }

    private ExchangeAddress checkUserInput() {
        ExchangeAddress da = new ExchangeAddress();
        if (areaET.getTag() == null) return null;
        String str1 = areaET.getTag().toString();
        if (TextUtils.isEmpty(str1)) {
            AndroidUtil.showToast(R.string.invalid_choose_addres);
            return null;
        } else {
            da.setAreaid((Integer) areaET.getTag());
        }
        String str2 = addresET.getText().toString();
        if (StringUtil.isNotBlank(str2)) {
            da.setAddress(str2);
        } else {
            AndroidUtil.showToast(R.string.invalid_addres);
            return null;
        }
        String str3 = zipsET.getText().toString();
        if (StringUtil.checkZip(str3, 6)) {
            da.setZipcode(str3);
        } else {
            AndroidUtil.showToast(R.string.invalid_zip);
            return null;
        }
        String str4 = realnameET.getText().toString();
        if (StringUtil.checkUserNamePwd(str4, 2)) {
            da.setRealname(str4);
        } else {
            AndroidUtil.showToast(R.string.invalid_username);
            return null;
        }
        String str5 = phoneET.getText().toString();
//		if (StringUtil.isTelNumAvailable(str5)) {
        da.setPhone(str5);
//		} else {
//			AndroidUtil.showToast(R.string.invalid_phone_numb);
//			return null;
//		}
        return da;
    }

    private void refreshUI() {
        if (address != null) {
            areaET.setTag(address.getAreaid());
            areaET.setText(address.getProvince() + "-" + address.getCity());
            addresET.setText(address.getAddress());
            zipsET.setText(address.getZipcode());
            realnameET.setText(address.getRealname());
            phoneET.setText(address.getPhone());
            mBtnSave.setText(R.string.btn_mod);
            if (address.isDef()) {
                rb_yes.setChecked(true);
            } else {
                rb_no.setChecked(true);
            }
        } else {
            mBtnSave.setText(R.string.add);
        }
    }

    // private void sendRegReq(String url) {
    // getRequestQueue().add(new StringRequest(url, new Listener<String>() {
    // @Override
    // public void onResponse(String response) {
    // hideProgress();
    // if (checkResult(response)) {
    // }
    // }
    // }, new ErrorListener() {
    // @Override
    // public void onErrorResponse(VolleyError error) {
    // hideProgress();
    // AndroidUtil.showToast(R.string.reqFailed);
    // }
    // }));
    // }

    private boolean checkResult(String json) {
        try {
            org.json.JSONObject jsonObj = new org.json.JSONObject(json);
            String successID = jsonObj.getString("success").trim();
            AndroidUtil.showToast(jsonObj.getString("tips").trim());
            if (successID.equals("1")) {
                ExchangeAddress da = new ExchangeAddress();
                da.setAreaid(jsonObj.getJSONObject("data").getInt("areaid"));
                da.setAddress(jsonObj.getJSONObject("data")
                        .getString("address"));
                da.setPhone(jsonObj.getJSONObject("data").getString("phone"));
                da.setRealname(jsonObj.getJSONObject("data").getString(
                        "realname"));
                da.setZipcode(jsonObj.getJSONObject("data")
                        .getString("zipcode"));
                address = da;
                return true;
            } else {
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void doRevriseReq(ExchangeAddress da) {
        try {
            ReqData reqData = new ReqData();
            Map<String, String> map = new HashMap<String, String>();
            reqData.setC("member");
            if (null != address) {
                reqData.setA("address_edit");
                map.put("aid", String.valueOf(address.getAid()));
                // map.put("isdefault", da.getIsdefault() + "");
            } else {
                reqData.setA("address_add");
                // map.put("isdefault", "0");
            }
            map.put("isdefault", rb_yes.isChecked() ? "1" : "0");
            map.put("userid", userInfo.getUserID());
            map.put("areaid", da.getAreaid() + "");
            map.put("address", da.getAddress());
            map.put("zipcode", da.getZipcode());
            map.put("realname", da.getRealname());
            map.put("phone", da.getPhone());
            reqData.setData(map);
            String jwtData = reqData.toReqString();
            if (jwtData != null && jwtData.length() > 0) {
                String url = getString(R.string.base_url) + jwtData;
                showProgress("", getString(R.string.network_wait));
                sendRevrReq(url, da);
            } else {
                DebugLog.d("Get JWT JSONData Error.");
            }
        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    private void sendRevrReq(String url, final ExchangeAddress da) {
        getRequestQueue().add(new StringRequest(url, new Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideProgress();
                if (checkRevrResult(response)) {
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

    private boolean checkRevrResult(String json) {
        try {
            org.json.JSONObject jsonObj = new org.json.JSONObject(json);
            String successID = jsonObj.getString("success").trim();
            AndroidUtil.showToast(jsonObj.getString("tips").trim());
            if (successID.equals("1")) {
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
                revriseDA();
                break;
            case R.id.area:
                areaPopup.showPop(lay_main);
                break;
            default:
                break;
        }
    }
}
