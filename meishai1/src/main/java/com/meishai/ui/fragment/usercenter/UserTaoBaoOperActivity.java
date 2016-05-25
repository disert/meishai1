package com.meishai.ui.fragment.usercenter;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.dao.MeiShaiSP;
import com.meishai.dao.MeiShaiSqlite;
import com.meishai.entiy.UserInfo;
import com.meishai.entiy.UserTaoBaoInfo;
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
 * 个人信息-淘宝账号-添加、修改淘宝账号
 *
 * @author sh
 */
public class UserTaoBaoOperActivity extends BaseActivity implements
        OnClickListener {

    private static final String TYPE_ADD = "add";
    private static final String TYPE_MOD = "mod";
    private Context mContext = UserTaoBaoOperActivity.this;
    private Button mBtnCancel, mBtnCommit;
    private TextView title;
    private EditText etUserName;
    private String userName;
    private UserInfo userInfo;
    private UserTaoBaoInfo taobaoInfo;
    private RadioButton rb_yes;
    private RadioButton rb_no;

    public static Intent newAddIntent() {
        Intent intent = new Intent(GlobalContext.getInstance(),
                UserTaoBaoOperActivity.class);
        intent.putExtra("type", TYPE_ADD);
        return intent;
    }

    public static Intent newModIntent(UserTaoBaoInfo baoInfo) {
        Intent intent = new Intent(GlobalContext.getInstance(),
                UserTaoBaoOperActivity.class);
        intent.putExtra("type", TYPE_MOD);
        intent.putExtra("baoInfo", baoInfo);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userInfo = MeiShaiSP.getInstance().getUserInfo();
        setContentView(R.layout.user_taobao_oper);
        initView();
        initData();
    }

    private void initView() {
        title = (TextView) this.findViewById(R.id.title);
        mBtnCancel = (Button) this.findViewById(R.id.btn_cancel);
        mBtnCancel.setOnClickListener(this);
        mBtnCommit = (Button) this.findViewById(R.id.btn_save);
        mBtnCommit.setOnClickListener(this);
        etUserName = (EditText) this.findViewById(R.id.taobaoUserName);
        rb_yes = (RadioButton) this.findViewById(R.id.rb_yes);
        rb_no = (RadioButton) this.findViewById(R.id.rb_no);
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            String type = bundle.getString("type");
            if (TYPE_ADD.equals(type)) {
                title.setText(R.string.title_taobao_add);
            } else if (TYPE_MOD.equals(type)) {
                title.setText(R.string.title_taobao_mod);
                taobaoInfo = (UserTaoBaoInfo) bundle.getSerializable("baoInfo");
                if (null != taobaoInfo) {
                    etUserName.setText(taobaoInfo.getTaobaoUserName());
                    if (taobaoInfo.isIndex()) {
                        rb_yes.setChecked(true);
                    } else {
                        rb_no.setChecked(true);
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.btn_save:
                commitTaoBaoUserInfo();
                break;
            default:
                break;
        }
    }

    protected void commitTaoBaoUserInfo() {
        userName = etUserName.getText().toString();
        addTaobaoUserInfo();
    }

    private void addTaobaoUserInfo() {
        try {
            ReqData reqData = new ReqData();
            Map<String, String> map = new HashMap<String, String>();
            reqData.setC("member");
            if (null == taobaoInfo) {
                reqData.setA("tb_user_add");
            } else {
                reqData.setA("tb_user_edit");
                map.put("tb_user_id", taobaoInfo.getTaobaoUserID());
            }
            map.put("userid", userInfo.getUserID());
            map.put("tb_user_name", userName);
            map.put("isindex", rb_yes.isChecked() ? "1" : "0");
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

    private void storageTaoBaoUserInfoInfo(final UserTaoBaoInfo tbInfo) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                MeiShaiSqlite sqlite = new MeiShaiSqlite(mContext);
                sqlite.insertTaoBaoUser(tbInfo);
            }
        }).start();
    }

    private boolean checkResult(String json) {
        try {
            org.json.JSONObject jsonObj = new org.json.JSONObject(json);
            String successID = jsonObj.getString("success").trim();
            AndroidUtil.showToast(jsonObj.getString("tips").trim());
            if (successID.equals("1")) {
                UserTaoBaoInfo tbInfo = new UserTaoBaoInfo();
                tbInfo.setUserID(userInfo.getUserID());
                tbInfo.setTaobaoUserID("");
                tbInfo.setTaobaoUserName(userName);
                tbInfo.setIndex(false);
                // storageTaoBaoUserInfoInfo(tbInfo);
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
