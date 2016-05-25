package com.meishai.ui.dialog;

import java.util.HashMap;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.meishai.R;
import com.meishai.app.widget.CustomProgress;
import com.meishai.dao.MeiShaiSP;
import com.meishai.net.RespData;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.common.req.PublicReq;
import com.meishai.util.AndroidUtil;

/**
 * 发私信
 *
 * @author sh
 */
public class SendPriMsgDialog extends Dialog {

    private Context mContext;
    private String tuserid = "";
    private CustomProgress mProgressDialog;
    private Button mBtnCancel;
    private Button mBtnSubmit;
    private EditText ed_msg;

    public SendPriMsgDialog(Context context) {
        super(context, R.style.dialog_transparent);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_send_msg);
        this.initView();
    }

    public void setTuserid(String tuserid) {
        this.tuserid = tuserid;
    }

    private void initView() {
        this.mBtnCancel = (Button) this.findViewById(R.id.btn_cancel);
        mBtnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        this.mBtnSubmit = (Button) this.findViewById(R.id.btn_submit);
        this.mBtnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (checkData()) {
                    send();
                }
            }
        });
        this.ed_msg = (EditText) this.findViewById(R.id.ed_msg);
    }

    private boolean checkData() {
        String msg = this.ed_msg.getText().toString();
        if (TextUtils.isEmpty(msg)) {
            AndroidUtil.showToast("私信内容不能为空");
            return false;
        }
        return true;
    }

    private void send() {
        if (null == mProgressDialog) {
            mProgressDialog = CustomProgress.show(mContext,
                    mContext.getString(R.string.network_wait), true, null);
        } else {
            mProgressDialog.show();
        }
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        data.put("tuserid", tuserid);
        data.put("content", this.ed_msg.getText().toString());
        PublicReq.send(mContext, data, new Listener<RespData>() {

            @Override
            public void onResponse(RespData response) {
                if (null != mProgressDialog) {
                    mProgressDialog.hide();
                }
                AndroidUtil.showToast(response.getTips());
                if (response.isSuccess()) {
                    dismiss();
                }

            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (null != mProgressDialog) {
                    mProgressDialog.hide();
                }
                AndroidUtil.showToast(mContext.getString(R.string.reqFailed));
            }
        });
    }
}
