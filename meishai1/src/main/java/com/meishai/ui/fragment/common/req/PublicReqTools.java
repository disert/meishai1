package com.meishai.ui.fragment.common.req;

import android.app.Dialog;
import android.content.Context;
import android.widget.ImageView;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.app.widget.CustomProgress;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.UserInfo;
import com.meishai.net.MeishaiRequest;
import com.meishai.net.ReqData;
import com.meishai.net.RespData;
import com.meishai.net.VolleyHelper;
import com.meishai.net.volley.Request;
import com.meishai.net.volley.Response;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.StringRequest;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.usercenter.LoginActivity;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;
import com.meishai.util.UploadUtilsAsync;
import com.nimbusds.jose.JOSEException;

import java.util.HashMap;
import java.util.Map;

/**
 * 公用请求
 *
 * @author sh
 */
public class PublicReqTools {

    private static Dialog mProgressDialog;

    /**
     * 添加关注
     *
     * @param uid
     * @param view
     */
    public static void addfriend(final String uid, final ImageView view, final Context mContext) {
        if (null == uid) {
            return;
        }
        showProgress("", "请稍候...", mContext);

        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        data.put("fuserid", uid);
        PublicReq.addfriend(mContext, data, new Response.Listener<RespData>() {

            @Override
            public void onResponse(RespData response) {
                hideProgress();
                if (response.isSuccess()) {
                    view.setImageResource(R.drawable.ic_attention_yes);
                    view.setClickable(false);
                } else {
                    AndroidUtil.showToast(response.getTips());
                }
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
                AndroidUtil.showToast(mContext.getString(R.string.reqFailed));
            }
        });
    }

    public static void addfriend(final String uid, final Context context, final Listener<String> listener) {
        if (null == uid) {
            return;
        }
        showProgress("", "请稍候...", context);

        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        data.put("fuserid", uid);
        ReqData reqData = new ReqData();
        reqData.setC("public");
        reqData.setA("addfriend");
        reqData.setData(data);
        try {
            String reqString = reqData.toReqString();
            String url = context.getString(R.string.base_url) + reqString;
            VolleyHelper.getRequestQueue(context).add(
                    new StringRequest(url, listener, new ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            hideProgress();
                            AndroidUtil.showToast("添加关注失败!");
                        }
                    }));
        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }


    public static void showProgress(String title, String message, Context mContext) {
        if (null == mProgressDialog) {
            mProgressDialog = CustomProgress
                    .show(mContext, message, true, null);
        } else {
            if (!mProgressDialog.isShowing()) {
                mProgressDialog.show();
            }
        }
    }

    public static void hideProgress() {
        if (null != mProgressDialog && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

}
