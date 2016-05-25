package com.meishai.ui.fragment.tryuse;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.app.widget.CustomProgress;
import com.meishai.entiy.ButtonData;
import com.meishai.entiy.ColorInfo;
import com.meishai.entiy.ConfirmExchange;
import com.meishai.entiy.ExchangeAddress;
import com.meishai.entiy.ExchangeSize;
import com.meishai.entiy.FuLiGoodsInfo;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.ui.base.BaseActivity;
import com.meishai.ui.fragment.tryuse.adapter.OrderAddressAdapter1;
import com.meishai.ui.fragment.tryuse.req.FuLiSheReq;
import com.meishai.ui.fragment.usercenter.LoginActivity;
import com.meishai.util.AndroidUtil;
import com.meishai.util.GsonHelper;

/**
 * 发现->积分->确认订单
 *
 * @author yl
 */
public class FindPointOrderActivity1 extends BaseActivity implements
        OnClickListener {

    private Context mContext = FindPointOrderActivity1.this;
    private Button mBtnBack;
    private TextView title_order;
    private LinearLayout lay_content;
    private RelativeLayout lay_bottom;
    private int mGid;
    private ListView mAddressList;
    private OrderAddressAdapter1 orderAddresssAdapter;
    private TextView userpoint;
    private Button mBtnOper;
    // 每次加价积分
    private long addPoint = 0;

    private CustomProgress mProgressDialog;

    private ConfirmExchange mExchange;

    public static Intent newIntent(int gid) {
        Intent intent = new Intent(GlobalContext.getInstance(),
                FindPointOrderActivity1.class);
        intent.putExtra("gid", gid);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_point_order);
        orderAddresssAdapter = new OrderAddressAdapter1(mContext);
        mGid = getIntent().getExtras().getInt("gid");
        this.initView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Serializable temp = data.getSerializableExtra("address");
            if (temp != null && temp instanceof ExchangeAddress) {
                ExchangeAddress address = (ExchangeAddress) temp;
                mExchange.setUseraddress(address);
                orderAddresssAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onResume() {
        if (mExchange == null) {
            this.loadData();
        } else {
            if (orderAddresssAdapter != null) {
                if (orderAddresssAdapter.isrefresh()) {
                    loadData();
                } else {
                    orderAddresssAdapter.notifyDataSetChanged();
                }
            }
        }
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
        if (null != exchange) {
            orderAddresssAdapter.setExchange(exchange);
            orderAddresssAdapter.notifyDataSetChanged();
        }
    }

    private void updateUI(ConfirmExchange confirmExchange) {
        if (null != confirmExchange && confirmExchange.getBottom() != null) {
            ButtonData data = confirmExchange.getBottom();

            mBtnOper.setText(confirmExchange.getBottom().app_button_text);
            userpoint.setText(data.text);
            //设置积分颜色
            int color = 0;
            if (!TextUtils.isEmpty(data.text_color)) {
                if (data.text_color.startsWith("#")) {
                    color = Color.parseColor(data.text_color);
                } else {
                    color = Color.parseColor("#FF" + data.text_color);
                }
                userpoint.setTextColor(color);
            }
            //设置按钮状态
            if (data.app_button == 1) {
                mBtnOper.setClickable(true);
                mBtnOper.setSelected(false);
            } else {
                mBtnOper.setClickable(false);
                mBtnOper.setSelected(true);

            }

            title_order.setText(confirmExchange.getButtontext());


        }
    }


    private void loadData() {
        showProgressDialog();
        FuLiSheReq.confirmTrade(mGid, new Listener<String>() {

            @Override
            public void onResponse(String response) {
                mProgressDialog.hide();
                mExchange = GsonHelper.parseObject(response,
                        ConfirmExchange.class);
                if (null != mExchange && mExchange.isSuccess()) {
                    lay_content.setVisibility(View.VISIBLE);
                    lay_bottom.setVisibility(View.VISIBLE);
                    initOrderAddressAdapter(mExchange);
                    updateUI(mExchange);
                } else if (mExchange.getSuccess().intValue() == 0) {
                    // 未登录
                    startActivity(LoginActivity.newOtherIntent());
                } else {

                    AndroidUtil.showToast(mExchange.getTips());
                }
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.hide();
                AndroidUtil.showToast(mContext.getString(R.string.reqFailed));
            }
        });
    }

    private void apply() {

        int aid = 0;
        int kid = 0;
        int colorid = 0;
        int point1 = 0;
        // 地址ID，不能为空
        if (null != orderAddresssAdapter.getCheckAddress()) {
            aid = orderAddresssAdapter.getCheckAddress().getAid();
        }
        // 尺寸id，如果没有则传0
        ExchangeSize exchangeSize = orderAddresssAdapter.getCheckedSize();
        kid = null == exchangeSize ? 0 : exchangeSize.getKid();
        // 颜色id,如果没有则传0
        ColorInfo checkedColor = orderAddresssAdapter.getCheckedColor();
        colorid = null == checkedColor ? 0 : checkedColor.getColorid();
        // 出价积分，如果没有则传0
        point1 = orderAddresssAdapter.getCurrentPoint();
        //TODO 边界检查
        if (mExchange == null) {
            AndroidUtil.showToast("请检查出价积分!");
            return;
        }
        FuLiGoodsInfo info = mExchange.getGoodsdata().get(0);
        if (point1 < info.point || point1 > info.userpoint) {
            AndroidUtil.showToast("请检查出价积分!");
            return;
        }
        showProgressDialog();
        FuLiSheReq.submit(mGid, aid, kid, colorid, point1,
                new Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        mProgressDialog.hide();
                        try {
                            JSONObject obj = new JSONObject(response);
                            mBtnOper.setClickable(true);
                            AndroidUtil.showToast(obj.getString("tips"));
                            if (obj.getInt("success") == 1) {
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mBtnOper.setClickable(true);
                        mProgressDialog.hide();
                        //有时候明明申请成功了,却会弹出网络请求失败
                        AndroidUtil.showToast(mContext.getString(R.string.reqFailed));
                        finish();
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
