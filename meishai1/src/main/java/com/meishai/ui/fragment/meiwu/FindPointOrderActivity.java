package com.meishai.ui.fragment.meiwu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.app.widget.CustomProgress;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.ConfirmExchange;
import com.meishai.entiy.ExchangeAddress;
import com.meishai.entiy.ExchangeSize;
import com.meishai.entiy.PointExchange;
import com.meishai.entiy.PointExchange.PointExchangeType;
import com.meishai.net.RespData;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.ui.base.BaseActivity;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.meiwu.adapter.OrderAddressAdapter;
import com.meishai.ui.fragment.meiwu.req.FindReq;
import com.meishai.ui.fragment.usercenter.LoginActivity;
import com.meishai.util.AndroidUtil;
import com.meishai.util.GsonHelper;

/**
 * 发现->积分->确认订单
 *
 * @author sh
 */
public class FindPointOrderActivity extends BaseActivity implements
        OnClickListener {
    private Context mContext = FindPointOrderActivity.this;
    private Button mBtnBack;
    private TextView title_order;
    private LinearLayout lay_content;
    private RelativeLayout lay_bottom;
    private long id;
    private ListView mAddressList;
    private OrderAddressAdapter orderAddresssAdapter;
    private TextView userpoint;
    private Button mBtnOper;
    private LinearLayout lay_addpoint;
    private EditText point;
    // 加
    private ImageButton btn_plus;
    // 减
    private ImageButton btn_sub;
    // 每次加价积分
    private long addPoint = 0;

    private CustomProgress mProgressDialog;

    public static Intent newIntent(long id) {
        Intent intent = new Intent(GlobalContext.getInstance(),
                FindPointOrderActivity.class);
        intent.putExtra(ConstantSet.BUNDLE_ID, id);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_point_order);
        orderAddresssAdapter = new OrderAddressAdapter(mContext,
                new ArrayList<ExchangeAddress>());
        id = getIntent().getExtras().getLong(ConstantSet.BUNDLE_ID);
        this.initView();
    }

    @Override
    protected void onResume() {
        this.loadData();
        super.onResume();
    }

    private void initView() {
        lay_content = (LinearLayout) this.findViewById(R.id.lay_content);
        lay_bottom = (RelativeLayout) this.findViewById(R.id.lay_bottom);
        mBtnBack = (Button) this.findViewById(R.id.backMain);
        mBtnBack.setOnClickListener(this);
        title_order = (TextView) this.findViewById(R.id.title_order);
        mAddressList = (ListView) this.findViewById(R.id.address_list);
        userpoint = (TextView) this.findViewById(R.id.userpoint);
        mBtnOper = (Button) this.findViewById(R.id.btn_oper);
        mAddressList.setAdapter(orderAddresssAdapter);
        mBtnOper.setOnClickListener(this);
        lay_addpoint = (LinearLayout) this.findViewById(R.id.lay_addpoint);
        point = (EditText) this.findViewById(R.id.point);
        btn_plus = (ImageButton) this.findViewById(R.id.btn_plus);
        btn_sub = (ImageButton) this.findViewById(R.id.btn_sub);
    }

    private void showProgressDialog() {
        if (null == mProgressDialog) {
            mProgressDialog = CustomProgress.show(mContext,
                    mContext.getString(R.string.network_wait), true, null);
        } else {
            mProgressDialog.show();
        }
    }

    private void initOrderAddressAdapter(ConfirmExchange exchange) {
        boolean bool = false;
        if (null != exchange) {
            orderAddresssAdapter.setExchange(exchange);
            bool = true;
        }
        if (null != exchange && null != exchange.getAddressdata()
                && !exchange.getAddressdata().isEmpty()) {
            orderAddresssAdapter.setAddresss(exchange.getAddressdata());
            bool = true;
        }
        if (bool) {
            orderAddresssAdapter.notifyDataSetChanged();
        }
    }

    private void updateUI(ConfirmExchange confirmExchange) {
        if (null != confirmExchange) {
            String findUserpoint = mContext.getString(R.string.find_userpoint);
            userpoint.setText(String.format(findUserpoint,
                    confirmExchange.getUserpoint()));
            mBtnOper.setText(confirmExchange.getButtontext());
            title_order.setText(confirmExchange.getButtontext());
            List<PointExchange> itemData = confirmExchange.getItemdata();
            if (null != itemData && !itemData.isEmpty()) {
                int type = itemData.get(0).getType();
                if (PointExchangeType.POINT_CRAZY.getType().equals(
                        String.valueOf(type))) {
                    lay_addpoint.setVisibility(View.VISIBLE);
                    point.setText(confirmExchange.getPoint() + "");
                    addPoint = confirmExchange.getAddpoint();
                    setPointOperListener();
                }
            }
        }
    }

    private void setPointOperListener() {
        btn_sub.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                long p = Long.parseLong(point.getText().toString());
                if (p >= addPoint) {
                    long s = p - addPoint;
                    point.setText(s + "");
                }
            }
        });
        btn_plus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                long p = Long.parseLong(point.getText().toString());
                long s = p + addPoint;
                point.setText(s + "");
            }
        });

    }

    private void loadData() {
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        data.put("id", String.valueOf(id));
        FindReq.confirm(mContext, data, new Listener<String>() {

            @Override
            public void onResponse(String response) {
                ConfirmExchange exchange = GsonHelper.parseObject(response,
                        ConfirmExchange.class);
                if (null != exchange && exchange.isSuccess()) {
                    lay_content.setVisibility(View.VISIBLE);
                    lay_bottom.setVisibility(View.VISIBLE);
                    initOrderAddressAdapter(exchange);
                    updateUI(exchange);
                } else if (exchange.getSuccess().intValue() == 0) {
                    // 未登录
                    startActivity(LoginActivity.newOtherIntent());
                } else {
                    AndroidUtil.showToast(exchange.getTips());
                }

            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                AndroidUtil.showToast(mContext.getString(R.string.reqFailed));
            }
        });
    }

    private void apply() {
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        data.put("id", String.valueOf(id));
        // 地址ID，不能为空
        if (null != orderAddresssAdapter.getCheckAddress()) {
            data.put("aid",
                    String.valueOf(orderAddresssAdapter.getCheckAddress().getAid()));
        } else {
            data.put("aid", "");
        }
        // 尺寸id，如果没有则传0
        ExchangeSize exchangeSize = orderAddresssAdapter.getCheckedSize();
        data.put(
                "kid",
                null == exchangeSize ? "0" : String.valueOf(exchangeSize
                        .getKid()));
        // 出价积分，如果没有则传0
        String pt = point.getText().toString();
        if (TextUtils.isEmpty(pt)) {
            data.put("point", "0");
        } else {
            data.put("point", pt);
        }
        showProgressDialog();
        FindReq.apply(mContext, data, new Listener<RespData>() {

            @Override
            public void onResponse(RespData response) {
                mBtnOper.setClickable(true);
                mProgressDialog.hide();
                AndroidUtil.showToast(response.getTips());
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mBtnOper.setClickable(true);
                mProgressDialog.hide();
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
            case R.id.btn_oper:
                mBtnOper.setClickable(false);
                apply();
                break;
            default:
                break;
        }
    }
}
