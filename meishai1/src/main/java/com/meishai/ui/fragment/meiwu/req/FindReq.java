package com.meishai.ui.fragment.meiwu.req;

import java.util.Map;

import android.content.Context;

import com.meishai.R;
import com.meishai.net.MeishaiRequest;
import com.meishai.net.ReqData;
import com.meishai.net.RespData;
import com.meishai.net.VolleyHelper;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.toolbox.StringRequest;
import com.nimbusds.jose.JOSEException;

public class FindReq {
    /**
     * 特价
     *
     * @param context
     * @param data
     * @param listener
     * @param errorListener
     */
    public static void tejia(Context context, Map<String, String> data,
                             Listener<RespData> listener, ErrorListener errorListener) {
        ReqData reqData = new ReqData();
        reqData.setC("tejia");
        reqData.setA("index");
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
     * 特价-搜索
     *
     * @param context
     * @param data
     * @param listener
     * @param errorListener
     */
    public static void search(Context context, Map<String, String> data,
                              Listener<RespData> listener, ErrorListener errorListener) {
        ReqData reqData = new ReqData();
        reqData.setC("tejia");
        reqData.setA("search");
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
     * 积分
     *
     * @param context
     * @param data
     * @param listener
     * @param errorListener
     */
    public static void point(Context context, Map<String, String> data,
                             Listener<RespData> listener, ErrorListener errorListener) {
        ReqData reqData = new ReqData();
        reqData.setC("point");
        reqData.setA("index");
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
     * 特价-分类
     *
     * @param context
     * @param data
     * @param listener
     * @param errorListener
     */
    public static void catalog(Context context, Map<String, String> data,
                               Listener<RespData> listener, ErrorListener errorListener) {
        ReqData reqData = new ReqData();
        reqData.setC("tejia");
        reqData.setA("catalog");
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
     * 积分兑换详情
     *
     * @param context
     * @param data
     * @param listener
     * @param errorListener
     */
    public static void show(Context context, Map<String, String> data,
                            Listener<RespData> listener, ErrorListener errorListener) {
        ReqData reqData = new ReqData();
        reqData.setC("point");
        reqData.setA("show");
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
     * 达人
     *
     * @param context
     * @param data
     * @param listener
     * @param errorListener
     */
    public static void daren(Context context, Map<String, String> data,
                             Listener<RespData> listener, ErrorListener errorListener) {
        ReqData reqData = new ReqData();
        reqData.setC("daren");
        reqData.setA("index");
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
     * 获取积分兑换订单数据
     *
     * @param context
     * @param data
     * @param listener
     * @param errorListener
     */
    public static void confirm(Context context, Map<String, String> data,
                               Listener<String> listener, ErrorListener errorListener) {
        ReqData reqData = new ReqData();
        reqData.setC("point");
        reqData.setA("confirm");
        reqData.setData(data);
        try {
            String reqString = reqData.toReqString();
            String url = context.getString(R.string.base_url) + reqString;
            VolleyHelper.getRequestQueue(context).add(
                    new StringRequest(url, listener, errorListener));
        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    /**
     * 兑换申请
     *
     * @param context
     * @param data
     * @param listener
     * @param errorListener
     */
    public static void apply(Context context, Map<String, String> data,
                             Listener<RespData> listener, ErrorListener errorListener) {
        ReqData reqData = new ReqData();
        reqData.setC("point");
        reqData.setA("apply");
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
     * 收藏
     *
     * @param context
     * @param data
     * @param listener
     * @param errorListener
     */
    public static void fav(Context context, Map<String, String> data,
                           Listener<RespData> listener, ErrorListener errorListener) {
        ReqData reqData = new ReqData();
        reqData.setC("point");
        reqData.setA("fav");
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
