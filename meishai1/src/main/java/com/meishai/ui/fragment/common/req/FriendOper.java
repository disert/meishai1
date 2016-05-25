package com.meishai.ui.fragment.common.req;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.meishai.R;
import com.meishai.app.widget.CustomProgress;
import com.meishai.dao.MeiShaiSP;
import com.meishai.net.RespData;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.util.AndroidUtil;

/**
 * 取消会员关注和添加会员关注
 *
 * @author sh
 */
public class FriendOper {

    /**
     * 添加关注
     *
     * @param context
     * @param mProgressDialog
     * @param fuserid
     */
    public static void addfriend(Context context,
                                 final CustomProgress mProgressDialog, final Object obj,
                                 String fuserid, final OnOperSuccessListener onOperSuccessListener) {
        if (null != mProgressDialog) {
            mProgressDialog.setTitle(context.getResources().getString(
                    R.string.add_att_wait));
            mProgressDialog.show();
        }
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        data.put("fuserid", fuserid);
        PublicReq.addfriend(context, data, new Listener<RespData>() {

            @Override
            public void onResponse(RespData response) {
                if (null != mProgressDialog) {
                    mProgressDialog.hide();
                }
                if (response.isSuccess()) {
                    if (null != onOperSuccessListener) {
                        onOperSuccessListener.onSuccess(obj);
                    }
                } else {
                    AndroidUtil.showToast(response.getTips());
                }
            }

        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (null != mProgressDialog) {
                    mProgressDialog.hide();
                }
                AndroidUtil.showToast(R.string.reqFailed);
            }
        });
    }

    /**
     * 取消关注
     *
     * @param context
     * @param mProgressDialog
     * @param fuserid
     */
    public static void delfriend(Context context,
                                 final CustomProgress mProgressDialog, final Object obj,
                                 String fuserid, final OnOperSuccessListener onOperSuccessListener) {
        if (null != mProgressDialog) {
            mProgressDialog.setTitle(context.getResources().getString(
                    R.string.can_att_wait));
            mProgressDialog.show();
        }
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        data.put("fuserid", fuserid);
        PublicReq.delfriend(context, data, new Listener<RespData>() {

            @Override
            public void onResponse(RespData response) {
                if (null != mProgressDialog) {
                    mProgressDialog.hide();
                }
                if (response.isSuccess()) {
                    if (null != onOperSuccessListener) {
                        onOperSuccessListener.onSuccess(obj);
                    }
                } else {
                    AndroidUtil.showToast(response.getTips());
                }
            }

        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (null != mProgressDialog) {
                    mProgressDialog.hide();
                }
                AndroidUtil.showToast(R.string.reqFailed);
            }
        });
    }

    public interface OnOperSuccessListener {
        public void onSuccess(Object obj);
    }

}
