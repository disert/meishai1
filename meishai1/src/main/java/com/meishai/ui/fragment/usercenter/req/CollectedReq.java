package com.meishai.ui.fragment.usercenter.req;

import android.content.Context;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.UserInfo;
import com.meishai.net.MeishaiRequest;
import com.meishai.net.ReqData;
import com.meishai.net.RespData;
import com.meishai.net.VolleyHelper;
import com.meishai.net.volley.RequestQueue;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.toolbox.StringRequest;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.util.DebugLog;
import com.nimbusds.jose.JOSEException;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户中心-我的信用
 *
 * @author sh
 */
public class CollectedReq {
    /**
     * 我的收藏_攻略的请求
     *
     * @param page
     * @param listener
     * @param errorListener
     */
    public static void strategyReq(int page, Listener<String> listener,
                                   ErrorListener errorListener) {

        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        ReqData reqData = new ReqData();
        reqData.setC("member");
        reqData.setA("meiwu_love");
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", userInfo.getUserID());
        data.put("typeid", 5 + "");
        data.put("page", String.valueOf(page));
        reqData.setData(data);
        String url = "";
        try {
            url = getString(R.string.base_url) + reqData.toReqString();
            DebugLog.d("我的收藏-攻略:" + url);
            getRequestQueue().add(
                    new StringRequest(url, listener, errorListener));

        } catch (JOSEException e) {
            e.printStackTrace();
        }

    }

    /**
     * 我的收藏_专场 请求
     *
     * @param page
     * @param listener
     * @param errorListener
     */
    public static void specialReq(int page, Listener<String> listener,
                                  ErrorListener errorListener) {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        ReqData reqData = new ReqData();
        reqData.setC("member");
        reqData.setA("meiwu_love");
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", userInfo.getUserID());
        data.put("typeid", String.valueOf(4));
        data.put("page", String.valueOf(page));
        reqData.setData(data);
        String url = "";
        try {
            url = getString(R.string.base_url) + reqData.toReqString();
            DebugLog.d("我的收藏-专场:" + url);
            getRequestQueue().add(
                    new StringRequest(url, listener, errorListener));

        } catch (JOSEException e) {
            e.printStackTrace();
        }

    }

    /**
     * 我的收藏-美物 请求
     *
     * @param page
     * @param listener
     * @param errorListener
     */
    public static void SKUReq(int page, Listener<String> listener,
                              ErrorListener errorListener) {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();

        ReqData reqData = new ReqData();
        reqData.setC("member");
        reqData.setA("meiwu_fav");
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", userInfo.getUserID());
        data.put("page", String.valueOf(page));
        reqData.setData(data);
        String url = "";
        try {
            url = getString(R.string.base_url) + reqData.toReqString();
            DebugLog.d("我的收藏-美物:" + url);
            getRequestQueue().add(
                    new StringRequest(url, listener, errorListener));

        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    /**
     * 我的收藏-美晒 请求
     *
     * @param page
     * @param listener
     * @param errorListener
     */
    public static void MeiShaiReq(int page, Listener<String> listener,
                                  ErrorListener errorListener) {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();

        ReqData reqData = new ReqData();
        reqData.setC("member");
        reqData.setA("meishai_fav");
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", userInfo.getUserID());
        data.put("page", String.valueOf(page));

        reqData.setData(data);
        String url = "";
        try {
            url = getString(R.string.base_url) + reqData.toReqString();
            DebugLog.d("我的收藏-美晒:" + url);
            getRequestQueue().add(
                    new StringRequest(url, listener, errorListener));

        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    public static String getString(int resid) {
        return GlobalContext.getInstance().getString(resid);
    }

    public static RequestQueue getRequestQueue() {
        return GlobalContext.getInstance().getRequestQueue();
    }

    public static Context getContext() {
        return GlobalContext.getInstance().getApplicationContext();
    }
}
