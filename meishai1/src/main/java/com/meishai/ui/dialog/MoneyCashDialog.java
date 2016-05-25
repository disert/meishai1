package com.meishai.ui.dialog;

import java.util.HashMap;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.app.util.StringUtil;
import com.meishai.app.widget.CustomProgress;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.UserCash;
import com.meishai.net.RespData;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.usercenter.req.MoneyReq;
import com.meishai.util.AndroidUtil;
import com.meishai.util.GsonHelper;

/**
 * 我的资金-申请提现
 *
 * @author sh
 */
public class MoneyCashDialog extends Dialog {

    private Context mContext;
    private Button mBtnClose;
    private Button mBtnSubmit;
    private CustomProgress mProgressDialog;
    private TextView amount;
    private TextView fee;
    private TextView phone;
    private TextView alipay;
    private EditText apply_cash;

    public MoneyCashDialog(Context context) {
        super(context, R.style.dialog_transparent);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_money_cash);
        this.initView();
        this.setListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.loadData();
    }

    private void initView() {
        this.mBtnClose = (Button) this.findViewById(R.id.btn_close);
        this.mBtnSubmit = (Button) this.findViewById(R.id.btn_submit);
        this.amount = (TextView) this.findViewById(R.id.amount);
        this.fee = (TextView) this.findViewById(R.id.fee);
        this.phone = (TextView) this.findViewById(R.id.phone);
        this.alipay = (TextView) this.findViewById(R.id.alipay);
        this.apply_cash = (EditText) this.findViewById(R.id.apply_cash);
    }

    private void loadData() {
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance()
                .getUserInfo().getUserID());
//		if (null == mProgressDialog) {
        mProgressDialog = CustomProgress.show(mContext,
                mContext.getString(R.string.network_wait), true, null);
//		} 
//		else {
//			mProgressDialog.show();
//		}
        MoneyReq.cash(mContext, data, new Listener<RespData>() {
            @Override
            public void onResponse(RespData response) {
                mProgressDialog.hide();
                if (response.isSuccess()) {
                    UserCash cash = GsonHelper.parseObject(
                            GsonHelper.toJson(response.getData()),
                            UserCash.class);
                    if (null != cash) {
                        fillData(cash);
                    } else {
                        AndroidUtil.showToast(response.getTips());
                    }
                } else {
                    AndroidUtil.showToast(response.getTips());
                }
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.hide();
                AndroidUtil.showToast(R.string.reqFailed);
            }
        });
    }

    private void fillData(UserCash cash) {
        if (null != cash) {
            amount.setText(String.format(
                    mContext.getString(R.string.txt_money_value),
                    cash.getAmount()));
            fee.setText(cash.getFee());
            phone.setText(cash.getPhone());
            alipay.setText(cash.getAlipay());
        } else {
            amount.setText("");
            fee.setText("");
            phone.setText("");
            alipay.setText("");
        }
    }

    private void setListener() {
        this.mBtnClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        this.mBtnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cashAdd();

            }
        });
    }

    private void cashAdd() {
        String cashValue = this.apply_cash.getText().toString();
        if (StringUtil.isBlank(cashValue)) {
            AndroidUtil.showToast(R.string.txt_apply_cash_tip);
            return;
        }
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance()
                .getUserInfo().getUserID());
        data.put("money", cashValue);
        mProgressDialog = CustomProgress.show(mContext,
                mContext.getString(R.string.network_wait), true, null);
        MoneyReq.cashAdd(mContext, data, new Listener<RespData>() {
            @Override
            public void onResponse(RespData response) {
                mProgressDialog.hide();
                AndroidUtil.showToast(response.getTips());
                if (response.isSuccess()) {
                    dismiss();
                }
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.hide();
                AndroidUtil.showToast(R.string.reqFailed);
            }
        });
    }
}
