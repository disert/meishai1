package com.meishai.ui.fragment.usercenter.req;

import java.util.Map;

import android.content.Context;

import com.meishai.R;
import com.meishai.net.MeishaiRequest;
import com.meishai.net.ReqData;
import com.meishai.net.RespData;
import com.meishai.net.VolleyHelper;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.util.DebugLog;
import com.nimbusds.jose.JOSEException;

/**
 * 用户中心->试用
 *
 * @author sh
 */
public class TrialReq {

    /**
     * 我的试用试用列表
     * 未通过：0，进行中：1，已完成：2，订单待提交：100，报告待提交：200，担保金待申请：300
     *
     * @param context
     * @param data
     * @param listener
     * @param errorListener
     */
    public static void tryList(Context context, Map<String, String> data,
                               Listener<RespData> listener, ErrorListener errorListener) {
        ReqData reqData = new ReqData();
        reqData.setC(ConstantSet.C_MEMBER);
        reqData.setA("try_list");
        reqData.setData(data);
        try {
            String reqString = reqData.toReqString();
            String url = context.getString(R.string.base_url) + reqString;
            DebugLog.d("我的试用:" + url);
            VolleyHelper.getRequestQueue(context).add(
                    new MeishaiRequest(url, listener, errorListener));
        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    /**
     * 放弃试用
     *
     * @param context
     * @param data
     * @param listener
     * @param errorListener
     */
    public static void tryGiveup(Context context, Map<String, String> data,
                                 Listener<RespData> listener, ErrorListener errorListener) {
        ReqData reqData = new ReqData();
        reqData.setC(ConstantSet.C_MEMBER);
        reqData.setA("try_giveup_add");
        reqData.setData(data);
        try {
            String reqString = reqData.toReqString();
            String url = context.getString(R.string.base_url) + reqString;
            VolleyHelper.getRequestQueue(context).add(
                    new MeishaiRequest(url, listener, errorListener));
        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查看详情
     *
     * @param context
     * @param data
     * @param listener
     * @param errorListener
     */
    public static void tryProcess(Context context, Map<String, String> data,
                                  Listener<RespData> listener, ErrorListener errorListener) {
        ReqData reqData = new ReqData();
        reqData.setC(ConstantSet.C_MEMBER);
        reqData.setA("try_process");
        reqData.setData(data);
        try {
            String reqString = reqData.toReqString();
            String url = context.getString(R.string.base_url) + reqString;
            VolleyHelper.getRequestQueue(context).add(
                    new MeishaiRequest(url, listener, errorListener));
        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    /**
     *  提交订单号
     *
     * @param context
     * @param data
     * @param listener
     * @param errorListener
     */
    public static void tryOrder(Context context, Map<String, String> data,
                                Listener<RespData> listener, ErrorListener errorListener) {
        ReqData reqData = new ReqData();
        reqData.setC(ConstantSet.C_MEMBER);
        reqData.setA("try_order");
        reqData.setData(data);
        try {
            String reqString = reqData.toReqString();
            String url = context.getString(R.string.base_url) + reqString;
            VolleyHelper.getRequestQueue(context).add(
                    new MeishaiRequest(url, listener, errorListener));
        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    /**
     * 提交订单号
     *
     * @param context
     * @param data
     * @param listener
     * @param errorListener
     */
    public static void tryOrderAdd(Context context, Map<String, String> data,
                                   Listener<RespData> listener, ErrorListener errorListener) {
        ReqData reqData = new ReqData();
        reqData.setC(ConstantSet.C_MEMBER);
        reqData.setA("try_order_add");
        reqData.setData(data);
        try {
            String reqString = reqData.toReqString();
            String url = context.getString(R.string.base_url) + reqString;
            VolleyHelper.getRequestQueue(context).add(
                    new MeishaiRequest(url, listener, errorListener));
        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }


    /**
     *  提交评价截图
     *
     * @param context
     * @param data
     * @param listener
     * @param errorListener
     */
    public static void tryReceipt(Context context, Map<String, String> data,
                                  Listener<RespData> listener, ErrorListener errorListener) {
        ReqData reqData = new ReqData();
        reqData.setC(ConstantSet.C_MEMBER);
        reqData.setA("try_receipt_add");
        reqData.setData(data);
        try {
            String reqString = reqData.toReqString();
            String url = context.getString(R.string.base_url) + reqString;
            VolleyHelper.getRequestQueue(context).add(
                    new MeishaiRequest(url, listener, errorListener));
        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

}
