package com.meishai.ui.fragment.tryuse.req;

import java.util.Map;

import android.content.Context;

import com.meishai.R;
import com.meishai.net.MeishaiRequest;
import com.meishai.net.ReqData;
import com.meishai.net.RespData;
import com.meishai.net.VolleyHelper;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.nimbusds.jose.JOSEException;

public class TryReq {
    /**
     * 试用
     *
     * @param context
     * @param data
     * @param listener
     * @param errorListener
     */
    public static void index(Context context, Map<String, String> data,
                             Listener<RespData> listener, ErrorListener errorListener) {
        ReqData reqData = new ReqData();
        reqData.setC("try");
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
     * 试用新首页
     *
     * @param context
     * @param data
     * @param listener
     * @param errorListener
     */
    public static void init(Context context, Map<String, String> data,
                            Listener<RespData> listener, ErrorListener errorListener) {
        ReqData reqData = new ReqData();
        reqData.setC("try");
        reqData.setA("init");
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
     * 试用分类
     *
     * @param context
     * @param data
     * @param listener
     * @param errorListener
     */
    public static void catalog(Context context, Map<String, String> data,
                               Listener<RespData> listener, ErrorListener errorListener) {
        ReqData reqData = new ReqData();
        reqData.setC("try");
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
     * 试用详情
     *
     * @param context
     * @param data
     * @param listener
     * @param errorListener
     */
    public static void show(Context context, Map<String, String> data,
                            Listener<RespData> listener, ErrorListener errorListener) {
        ReqData reqData = new ReqData();
        reqData.setC("try");
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
     * 试用申请
     * <p/>
     * success：-2（会员资料不全，打开补充会员资料面板）
     * <p/>
     * success：-1（未登录，跳转到登录页面）
     * <p/>
     * success：0（错误）
     * <p/>
     * success：1（成功）
     *
     * @param context
     * @param data
     * @param listener
     * @param errorListener
     */
    public static void app(Context context, Map<String, String> data,
                           Listener<RespData> listener, ErrorListener errorListener) {
        ReqData reqData = new ReqData();
        reqData.setC("try");
        reqData.setA("app");
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
     * 试用完善个人资料
     *
     * @param context
     * @param data
     * @param listener
     * @param errorListener
     */
    public static void profile(Context context, Map<String, String> data,
                               Listener<RespData> listener, ErrorListener errorListener) {
        ReqData reqData = new ReqData();
        reqData.setC("try");
        reqData.setA("profile");
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
     * 提交个人资料
     *
     * @param context
     * @param data
     * @param listener
     * @param errorListener
     */
    public static void profileAdd(Context context, Map<String, String> data,
                                  Listener<RespData> listener, ErrorListener errorListener) {
        ReqData reqData = new ReqData();
        reqData.setC("try");
        reqData.setA("profile_add");
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
        reqData.setC("try");
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

    /**
     * 搜索
     *
     * @param context
     * @param data
     * @param listener
     * @param errorListener
     */
    public static void search(Context context, Map<String, String> data,
                              Listener<RespData> listener, ErrorListener errorListener) {
        ReqData reqData = new ReqData();
        reqData.setC("try");
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
}
