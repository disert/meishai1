package com.meishai.ui.fragment.common.req;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.H5Data;
import com.meishai.entiy.UserInfo;
import com.meishai.net.MeishaiRequest;
import com.meishai.net.ReqData;
import com.meishai.net.RespData;
import com.meishai.net.VolleyHelper;
import com.meishai.net.volley.Request;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.toolbox.StringRequest;
import com.meishai.ui.fragment.usercenter.LoginActivity;
import com.meishai.util.DebugLog;
import com.nimbusds.jose.JOSEException;

/**
 * 公用请求
 *
 * @author sh
 */
public class PublicReq {

    /**
     * 加关注
     *
     * @param context
     * @param data
     * @param listener
     * @param errorListener
     */
    public static void addfriend(Context context, Map<String, String> data,
                                 Listener<RespData> listener, ErrorListener errorListener) {
        ReqData reqData = new ReqData();
        reqData.setC("public");
        reqData.setA("addfriend");
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
     * 取消会员关注
     *
     * @param context
     * @param data
     * @param listener
     * @param errorListener
     */
    public static void delfriend(Context context, Map<String, String> data,
                                 Listener<RespData> listener, ErrorListener errorListener) {
        ReqData reqData = new ReqData();
        reqData.setC("public");
        reqData.setA("delfriend");
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
     * 私信
     *
     * @param context
     * @param data
     * @param listener
     * @param errorListener
     */
    public static void send(Context context, Map<String, String> data,
                            Listener<RespData> listener, ErrorListener errorListener) {
        ReqData reqData = new ReqData();
        reqData.setC("public");
        reqData.setA("send");
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
     * 个人主页-关注
     *
     * @param context
     * @param data
     * @param listener
     * @param errorListener
     */
    public static void follow(Context context, Map<String, String> data,
                              Listener<RespData> listener, ErrorListener errorListener) {
        ReqData reqData = new ReqData();
        reqData.setC("user");
        reqData.setA("follow");
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
     * 关注话题
     *
     * @param context
     * @param data
     * @param listener
     * @param errorListener
     */
    public static void addtopic(Context context, Map<String, String> data,
                                Listener<RespData> listener, ErrorListener errorListener) {
        ReqData reqData = new ReqData();
        reqData.setC("public");
        reqData.setA("addtopic");
        reqData.setData(data);
        try {
            String reqString = reqData.toReqString();
            String url = context.getString(R.string.base_url) + reqString;
            DebugLog.d("话题关注:" + url);
            VolleyHelper.getRequestQueue(context).add(
                    new MeishaiRequest(url, listener, errorListener));
        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    /**
     * 取消话题关注
     *
     * @param context
     * @param data
     * @param listener
     * @param errorListener
     */
    public static void deltopic(Context context, Map<String, String> data,
                                Listener<RespData> listener, ErrorListener errorListener) {
        ReqData reqData = new ReqData();
        reqData.setC("public");
        reqData.setA("deltopic");
        reqData.setData(data);
        try {
            String reqString = reqData.toReqString();
            String url = context.getString(R.string.base_url) + reqString;
            DebugLog.d("话题取消关注:" + url);
            VolleyHelper.getRequestQueue(context).add(
                    new MeishaiRequest(url, listener, errorListener));
        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取地区数据
     *
     * @param context
     * @param data
     * @param listener
     * @param errorListener
     */
    public static void area(Context context, Map<String, String> data,
                            Listener<RespData> listener, ErrorListener errorListener) {
        ReqData reqData = new ReqData();
        reqData.setC("public");
        reqData.setA("area");
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
     * 获取表情
     *
     * @param context
     * @param data
     * @param listener
     * @param errorListener
     */
    public static void emoji(Context context, Map<String, String> data,
                             Listener<String> listener, ErrorListener errorListener) {
        ReqData reqData = new ReqData();
        reqData.setC("public");
        reqData.setA("emoji");
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
     * 获取邮箱验证码
     *
     * @param context
     * @param data
     * @param listener
     * @param errorListener
     */
    public static void getEmailCode(Context context, Map<String, String> data,
                                    Listener<String> listener, ErrorListener errorListener) {
        ReqData reqData = new ReqData();
        reqData.setC("public");
        reqData.setA("get_email_code");
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
     * 获取升级数据
     *
     * @param context
     * @param data
     * @param listener
     * @param errorListener
     */
    public static void upgrade(Context context, Map<String, String> data,
                               Listener<String> listener, ErrorListener errorListener) {
        ReqData reqData = new ReqData();
        reqData.setC("public");
        reqData.setA("upgrade");
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
     * 获得启动广告数据的请求
     *
     * @param context
     * @param listener
     * @param errorListener
     */
    public static void splashReq(Context context,
                                 Listener<String> listener, ErrorListener errorListener) {
        ReqData reqData = new ReqData();
        reqData.setC("public");
        reqData.setA("startup");
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", MeiShaiSP.getInstance().getUserInfo().getUserID());
        reqData.setData(data);
        try {
            String reqString = reqData.toReqString();
            String url = context.getString(R.string.base_url) + reqString;
            DebugLog.d("启动广告图:" + url);
            VolleyHelper.getRequestQueue(context).add(
                    new StringRequest(url, listener, errorListener));
        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获得h5页面链接的请求
     *
     * @param controller
     * @param action
     * @param listener
     * @param errorListener
     */
    public static void h5Req(String controller, String action, Listener<String> listener, ErrorListener errorListener) {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        if (!userInfo.isLogin()) {
            GlobalContext.getInstance().startActivity(LoginActivity.newOtherIntent());
            return;
        }
        ReqData reqData = new ReqData();
        reqData.setC("public");
        reqData.setA("geturl");
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", userInfo.getUserID());
        data.put("controller", String.valueOf(controller));
        data.put("action", String.valueOf(action));
        reqData.setData(data);
        try {
            String reqString = reqData.toReqString();
            String url = GlobalContext.getInstance().getString(R.string.base_url) + reqString;
            DebugLog.d("申请Url:" + url);
            GlobalContext.getInstance().getRequestQueue().add(
                    new StringRequest(Request.Method.POST, url, listener,
                            errorListener));
        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    /**
     * 渠道统计
     *
     * @param listener
     * @param errorListener
     */
    public static void channel(int marketid, Listener<String> listener, ErrorListener errorListener) {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        if (!userInfo.isLogin()) {
            GlobalContext.getInstance().startActivity(LoginActivity.newOtherIntent());
            return;
        }
        ReqData reqData = new ReqData();
        reqData.setC("public");
        reqData.setA("market");
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", userInfo.getUserID());
        data.put("marketid", String.valueOf(marketid));
        reqData.setData(data);
        try {
            String reqString = reqData.toReqString();
            String url = GlobalContext.getInstance().getString(R.string.base_url) + reqString;
            DebugLog.d("渠道统计:" + url);
            GlobalContext.getInstance().getRequestQueue().add(
                    new StringRequest(Request.Method.POST, url, listener,
                            errorListener));
        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    public static void h5Requst(H5Data h5Data, Listener<String> listener, ErrorListener errorListener) {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        ReqData reqData = new ReqData();
        reqData.setC("public");
        reqData.setA("geturl");
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", userInfo.getUserID());
        data.put("action", h5Data.action);
        data.put("controller", h5Data.controller);
        data.put("parameter", h5Data.parameter == null ? "" : h5Data.parameter);
        data.put("islogin", h5Data.islogin + "");
        reqData.setData(data);
        try {
            String reqString = reqData.toReqString();
            String url = GlobalContext.getInstance().getString(R.string.base_url) + reqString;
            DebugLog.d("请求h5链接:" + url);
            GlobalContext.getInstance().getRequestQueue().add(
                    new StringRequest(Request.Method.POST, url, listener,
                            errorListener));
        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

}
