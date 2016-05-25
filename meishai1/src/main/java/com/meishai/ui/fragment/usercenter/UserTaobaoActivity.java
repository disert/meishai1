package com.meishai.ui.fragment.usercenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.UserInfo;
import com.meishai.entiy.UserTaoBaoInfo;
import com.meishai.net.ReqData;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.StringRequest;
import com.meishai.ui.base.BaseActivity;
import com.meishai.ui.fragment.usercenter.adapter.TaobaoAdapter;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;
import com.nimbusds.jose.JOSEException;

/**
 * 个人信息-淘宝账号
 *
 * @author sh
 */
public class UserTaobaoActivity extends BaseActivity implements OnClickListener {

    private Button mBtnCancel;
    private List<UserTaoBaoInfo> taobaoInfoList;
    private UserInfo userInfo;
    private TaobaoAdapter taobaoAdapter;
    private ListView list;

    public static Intent newIntent() {
        Intent intent = new Intent(GlobalContext.getInstance(),
                UserTaobaoActivity.class);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userInfo = MeiShaiSP.getInstance().getUserInfo();
        setContentView(R.layout.user_taobao);
    }

    @Override
    protected void onStart() {
        taobaoInfoList = new ArrayList<UserTaoBaoInfo>();
        initView();
        sycServerUserInfo();
        super.onStart();
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
        list = (ListView) this.findViewById(R.id.list);
        taobaoAdapter = new TaobaoAdapter(this, taobaoInfoList);
        list.setAdapter(taobaoAdapter);
    }

    private void sycServerUserInfo() {
        try {
            ReqData reqData = new ReqData();
            reqData.setC("member");
            reqData.setA("tb_user");
            Map<String, String> map = new HashMap<String, String>();
            map.put("userid", userInfo.getUserID());
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
                checkResult(response);
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
            if (successID.equals("1")) {
                JSONArray jsonArray = jsonObj.getJSONArray("data");
                int size = jsonArray.length();
                List<UserTaoBaoInfo> tbList = new ArrayList<UserTaoBaoInfo>();
                for (int i = 0; i < size; i++) {
                    JSONObject obj = (JSONObject) jsonArray.get(i);
                    UserTaoBaoInfo tbInfo = new UserTaoBaoInfo();
                    tbInfo.setUserID(obj.getString("userid").trim());
                    tbInfo.setTaobaoUserID(obj.getString("tb_user_id").trim());
                    tbInfo.setTaobaoUserName(obj.getString("tb_user_name")
                            .trim());
                    tbInfo.setIndex(obj.getInt("isindex") == 1 ? true : false);
                    tbList.add(tbInfo);
                }
                taobaoInfoList.clear();
                taobaoInfoList.addAll(tbList);
                taobaoAdapter.setBaoInfos(taobaoInfoList);
                return true;
            } else {
                AndroidUtil.showToast(jsonObj.getString("tips").trim());
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
            default:
                break;
        }
    }
}
