package com.meishai.ui.fragment.usercenter;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.app.util.StringUtil;
import com.meishai.app.widget.wheel.AreaWheel.OnAreaChangeListener;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.DeliveryAddres;
import com.meishai.entiy.UserInfo;
import com.meishai.net.ReqData;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.StringRequest;
import com.meishai.ui.base.BaseFragment;
import com.meishai.ui.popup.AreaPopupWindow;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;
import com.nimbusds.jose.JOSEException;

/**
 * 收货地址
 *
 * @ClassName: UserReceiveAddressFragment
 */
public class UserReceiveAddressFragment extends BaseFragment implements
        OnClickListener {

    private LinearLayout lay_main;
    private Button mBtnCancel, mBtnSave;
    private TextView areaET;
    private EditText addresET, zipsET, realnameET, phoneET;
    private UserInfo userInfo;
    private DeliveryAddres da;
    private AreaPopupWindow areaPopup;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userInfo = MeiShaiSP.getInstance().getUserInfo();
        da = MeiShaiSP.getInstance().getDeliveryAddres();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_receive_address, null);
        initView(view);
        initPopup();
        return view;
    }

    /**
     * @param
     * @return void
     * @throws
     * @Title: initView
     * @Description:
     */
    private void initView(View view) {
        lay_main = (LinearLayout) view.findViewById(R.id.lay_main);
        mBtnCancel = (Button) view.findViewById(R.id.btn_cancel);
        mBtnCancel.setOnClickListener(this);
        mBtnSave = (Button) view.findViewById(R.id.btn_save);
        mBtnSave.setOnClickListener(this);
        areaET = (TextView) view.findViewById(R.id.area);
        areaET.setOnClickListener(this);
        addresET = (EditText) view.findViewById(R.id.addres);
        zipsET = (EditText) view.findViewById(R.id.zips);
        realnameET = (EditText) view.findViewById(R.id.realName);
        phoneET = (EditText) view.findViewById(R.id.phone);
        refreshUI(da);
        syncDeliveryAddres();
    }

    private void initPopup() {
        areaPopup = new AreaPopupWindow(mContext, new OnAreaChangeListener() {

            @Override
            public void onChange(int provinceid, String provinceName,
                                 int areaid, String name) {
                areaET.setText(provinceName + "-" + name);
            }
        });
    }

    private void revriseDA() {
        DeliveryAddres da = checkUserInput();
        if (da != null && da.getAddress() != null) {
            doRevriseReq(da);
        }
    }

    private DeliveryAddres checkUserInput() {
        DeliveryAddres da = new DeliveryAddres();
        String str1 = areaET.getText().toString();
        da.setAreaid(1);
        String str2 = addresET.getText().toString();
        if (StringUtil.checkUserNamePwd(str2, 6)) {
            da.setAddress(str2);
        } else {
            AndroidUtil.showToast(R.string.invalid_addres);
            return null;
        }
        String str3 = zipsET.getText().toString();
        if (StringUtil.checkZip(str3, 6)) {
            da.setZipCode(str3);
        } else {
            AndroidUtil.showToast(R.string.invalid_zip);
            return null;
        }
        String str4 = realnameET.getText().toString();
        if (StringUtil.checkUserNamePwd(str4, 2)) {
            da.setRealName(str4);
        } else {
            AndroidUtil.showToast(R.string.invalid_username);
            return null;
        }
        String str5 = phoneET.getText().toString();
        if (StringUtil.isTelNumAvailable(str5)) {
            da.setPhone(str5);
        } else {
            AndroidUtil.showToast(R.string.invalid_phone_numb);
            return null;
        }
        return da;
    }

    private void refreshUI(DeliveryAddres da) {
        if (da != null && da.getPhone() != null) {
            areaET.setText(da.getAreaid() + "");
            addresET.setText(da.getAddress());
            zipsET.setText(da.getZipCode());
            realnameET.setText(da.getRealName());
            phoneET.setText(da.getPhone());
            mBtnSave.setText(R.string.btn_mod);
        } else {
            mBtnSave.setText(R.string.add);
        }
    }

    private void syncDeliveryAddres() {
        try {
            ReqData reqData = new ReqData();
            reqData.setC("member");
            reqData.setA("address");
            Map<String, String> map = new HashMap<String, String>();
            map.put("userid", userInfo.getUserID());
            reqData.setData(map);
            String jwtData = reqData.toReqString();
            if (jwtData != null && jwtData.length() > 0) {
                String url = getString(R.string.base_url) + jwtData;
                DebugLog.d(url);
                showProgress("", "请稍后...");
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

    private void storageDaInfo(DeliveryAddres da) {
        MeiShaiSP.getInstance().setDeliveryAddres(da);
        refreshUI(da);
    }

    private boolean checkResult(String json) {
        try {
            org.json.JSONObject jsonObj = new org.json.JSONObject(json);
            String successID = jsonObj.getString("success").trim();
            AndroidUtil.showToast(jsonObj.getString("tips").trim());
            if (successID.equals("1")) {
                DeliveryAddres da = new DeliveryAddres();
                da.setAreaid(jsonObj.getJSONObject("data").getInt("areaid"));
                da.setAddress(jsonObj.getJSONObject("data")
                        .getString("address"));
                da.setPhone(jsonObj.getJSONObject("data").getString("phone"));
                da.setRealName(jsonObj.getJSONObject("data").getString(
                        "realname"));
                da.setZipCode(jsonObj.getJSONObject("data")
                        .getString("zipcode"));
                storageDaInfo(da);
                return true;
            } else {
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void doRevriseReq(DeliveryAddres da) {
        try {
            ReqData reqData = new ReqData();
            reqData.setC("member");
            reqData.setA("address_edit");
            Map<String, String> map = new HashMap<String, String>();
            map.put("userid", userInfo.getUserID());
            map.put("areaid", da.getAreaid() + "");
            map.put("address", da.getAddress());
            map.put("zip", da.getZipCode());
            map.put("realname", da.getRealName());
            map.put("phone", da.getPhone());
            reqData.setData(map);
            String jwtData = reqData.toReqString();
            if (jwtData != null && jwtData.length() > 0) {
                String url = getString(R.string.base_url) + jwtData;
                DebugLog.d(url);
                showProgress("", "请稍后...");
                sendRevrReq(url, da);
            } else {
                DebugLog.d("Get JWT JSONData Error.");
            }
        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    private void sendRevrReq(String url, final DeliveryAddres da) {
        getRequestQueue().add(new StringRequest(url, new Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideProgress();
                DebugLog.d("RSP:" + response);
                if (checkRevrResult(response)) {
                    storageDaInfo(da);
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

    // private void storageDaInfo(DeliveryAddres da){
    // MeiShaiSP.getInstance(mContext).setDeliveryAddres(da);
    // }

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
                backToMain();
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

    protected void backToMain() {
        doBackClick();
    }
}
