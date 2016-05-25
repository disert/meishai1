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
import com.meishai.entiy.TrialInfo;
import com.meishai.net.RespData;
import com.meishai.net.VolleyHelper;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.NetworkImageView;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.usercenter.req.TrialReq;
import com.meishai.util.AndroidUtil;

/**
 * 试用-提交订单号
 *
 * @author sh
 */
public class TrialOrderDialog extends Dialog {

    private Context mContext;
    private CustomProgress mProgressDialog;
    private ImageLoader imageLoader = null;
    private Button mBtnCancel;
    private Button mBtnSubmit;
    private TrialInfo trialInfo;
    // 试用图片
    private NetworkImageView mThumb;
    // 试用编号
    private TextView mSid;
    // 下单价格
    private TextView mPrice;
    // 邮费状态
    private TextView mFee;
    // 返还金额
    private TextView mFprice;
    private EditText mOrderNo;

    public TrialOrderDialog(Context context) {
        super(context, R.style.dialog_transparent);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_trial_order);
        imageLoader = VolleyHelper.getImageLoader(mContext);
        this.initView();
        this.setListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.fillData();
    }

    public void setTrialInfo(TrialInfo trialInfo) {
        this.trialInfo = trialInfo;
    }

    private void fillData() {
        this.mThumb.setImageUrl(trialInfo.getThumb(), imageLoader);
        this.mSid.setText(String.valueOf(trialInfo.getSid()));
        this.mFee.setText(trialInfo.getFee());
        this.mPrice.setText(String.format(mContext.getString(R.string.price),
                trialInfo.getPrice()));
        this.mFprice.setText(String.format(mContext.getString(R.string.fprice),
                trialInfo.getFprice()));
        this.mOrderNo.setText(trialInfo.getOrderno());
    }

    private void initView() {
        this.mBtnCancel = (Button) this.findViewById(R.id.btn_cancel);
        this.mBtnSubmit = (Button) this.findViewById(R.id.btn_submit);
        this.mThumb = (NetworkImageView) this.findViewById(R.id.thumb);
        this.mSid = (TextView) this.findViewById(R.id.sid);
        this.mPrice = (TextView) this.findViewById(R.id.price);
        this.mFee = (TextView) this.findViewById(R.id.fee);
        this.mFprice = (TextView) this.findViewById(R.id.fprice);
        this.mOrderNo = (EditText) this.findViewById(R.id.et_orderno);
    }

    private void tryOrder() {
        String orderNo = this.mOrderNo.getText().toString();
        if (StringUtil.isBlank(orderNo)) {
            AndroidUtil.showToast(R.string.tip_input_order);
            return;
        }
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance()
                .getUserInfo().getUserID());
        data.put("appid", String.valueOf(trialInfo.getAppid()));
        data.put("orderno", orderNo);
        if (null == mProgressDialog) {
            mProgressDialog = CustomProgress.show(mContext,
                    mContext.getString(R.string.network_wait), true, null);
        } else {
            mProgressDialog.show();
        }
        TrialReq.tryOrderAdd(mContext, data, new Listener<RespData>() {

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
                AndroidUtil.showToast(mContext.getString(R.string.reqFailed));
            }

        });
    }

    private void setListener() {
        this.mBtnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        this.mBtnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                tryOrder();
            }
        });
    }
}
