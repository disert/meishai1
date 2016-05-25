package com.meishai.ui.fragment.usercenter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.reflect.TypeToken;
import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.ExchangeAddress;
import com.meishai.net.RespData;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.ui.base.BaseActivity;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.usercenter.adapter.UserAddressAdapter;
import com.meishai.ui.fragment.usercenter.req.MemberReq;
import com.meishai.util.AndroidUtil;
import com.meishai.util.GsonHelper;

/**
 * 个人信息->收货地址列表
 *
 * @author sh
 */
public class UserAddressActivity extends BaseActivity implements
        OnClickListener {

    private Context mContext = UserAddressActivity.this;
    private Button mBtnBack;
    private LinearLayout lay_content;
    private ListView mAddressList;
    private UserAddressAdapter addressAdapter;

    public static Intent newIntent() {
        Intent intent = new Intent(GlobalContext.getInstance(),
                UserAddressActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_address);
    }

    @Override
    protected void onStart() {
        this.initView();
        this.loadData();
        super.onStart();
    }

    private void initView() {
        addressAdapter = new UserAddressAdapter(mContext,
                new ArrayList<ExchangeAddress>());
        lay_content = (LinearLayout) this.findViewById(R.id.lay_content);
        mBtnBack = (Button) this.findViewById(R.id.backMain);
        mBtnBack.setOnClickListener(this);
        mAddressList = (ListView) this.findViewById(R.id.address_list);
        mAddressList.setAdapter(addressAdapter);
    }

    private void initOrderAddressAdapter(List<ExchangeAddress> addresses) {
        addressAdapter.setAddresss(addresses);
        addressAdapter.notifyDataSetChanged();
    }

    private void loadData() {
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        showProgress("", getString(R.string.network_wait));
        MemberReq.address(mContext, data, new Listener<RespData>() {

            @Override
            public void onResponse(RespData response) {
                hideProgress();
                if (response.isSuccess()) {
                    Type type = new TypeToken<List<ExchangeAddress>>() {
                    }.getType();
                    List<ExchangeAddress> resultAddress = GsonHelper
                            .parseObject(GsonHelper.toJson(response.getData()),
                                    type);
                    if (null != resultAddress && !resultAddress.isEmpty()) {
                        lay_content.setVisibility(View.VISIBLE);
                        initOrderAddressAdapter(resultAddress);
                    }
                } else {
                    AndroidUtil.showToast(response.getTips());
                }
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
                AndroidUtil.showToast(mContext.getString(R.string.reqFailed));
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backMain:
                finish();
                break;
            default:
                break;
        }
    }
}
