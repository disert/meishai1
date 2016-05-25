package com.meishai.ui.fragment.usercenter.req;

import java.util.Map;

import android.content.Context;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.net.MeishaiRequest;
import com.meishai.net.ReqData;
import com.meishai.net.RespData;
import com.meishai.net.VolleyHelper;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.ui.constant.ConstantSet;
import com.nimbusds.jose.JOSEException;

/**
 * 用户登录
 *
 * @author sh
 */
public class LoginReq {

    public enum OPEN_SOURCE {
        qq, sina, weixin;
    }

    /**
     * 第三方开放平台登录
     *
     * @param context
     * @param source
     * @param data
     * @param listener
     * @param errorListener
     */
    public static void openLogin(Context context, OPEN_SOURCE source,
                                 Map<String, String> data, Listener<RespData> listener,
                                 ErrorListener errorListener) {
        ReqData reqData = new ReqData();
        reqData.setC(ConstantSet.C_MEMBER);
        reqData.setA(source.toString());
        reqData.setData(data);
        try {
            String reqString = reqData.toReqString();
            String url = context.getString(R.string.base_url) + reqString;
            GlobalContext.getInstance().getRequestQueue().add(
                    new MeishaiRequest(url, listener, errorListener));
        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    /**
     * QQ登录
     *
     * @param context
     * @param data
     * @param listener
     * @param errorListener
     */
    public static void qq(Context context, Map<String, String> data,
                          Listener<RespData> listener, ErrorListener errorListener) {
        ReqData reqData = new ReqData();
        reqData.setC(ConstantSet.C_MEMBER);
        reqData.setA("qq");
        reqData.setData(data);
        try {
            String reqString = reqData.toReqString();
            String url = context.getString(R.string.base_url) + reqString;
            GlobalContext.getInstance().getRequestQueue().add(
                    new MeishaiRequest(url, listener, errorListener));
        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    /**
     * sina登录
     *
     * @param context
     * @param data
     * @param listener
     * @param errorListener
     */
    public static void sina(Context context, Map<String, String> data,
                            Listener<RespData> listener, ErrorListener errorListener) {
        ReqData reqData = new ReqData();
        reqData.setC(ConstantSet.C_MEMBER);
        reqData.setA("sina");
        reqData.setData(data);
        try {
            String reqString = reqData.toReqString();
            String url = context.getString(R.string.base_url) + reqString;
            GlobalContext.getInstance().getRequestQueue().add(
                    new MeishaiRequest(url, listener, errorListener));
        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    /**
     * 微信登录
     *
     * @param context
     * @param data
     * @param listener
     * @param errorListener
     */
    public static void weixin(Context context, Map<String, String> data,
                              Listener<RespData> listener, ErrorListener errorListener) {
        ReqData reqData = new ReqData();
        reqData.setC(ConstantSet.C_MEMBER);
        reqData.setA("weixin");
        reqData.setData(data);
        try {
            String reqString = reqData.toReqString();
            String url = context.getString(R.string.base_url) + reqString;
            GlobalContext.getInstance().getRequestQueue().add(
                    new MeishaiRequest(url, listener, errorListener));
        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }
}
