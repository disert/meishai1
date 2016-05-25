package com.meishai.ui.fragment.home.req;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.meishai.R;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.UserInfo;
import com.meishai.net.MeishaiRequest;
import com.meishai.net.ReqData;
import com.meishai.net.RespData;
import com.meishai.net.VolleyHelper;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.toolbox.StringRequest;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.util.DebugLog;
import com.nimbusds.jose.JOSEException;

/**
 * 帖子动作
 *
 * @author sh
 */
public class PostReq {
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
        reqData.setC("post");
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

    public static void fav1(Context context, int pid,
                            Listener<String> listener, ErrorListener errorListener) {
        ReqData reqData = new ReqData();
        reqData.setC("post");
        reqData.setA("fav");

        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        data.put("pid", String.valueOf(pid));
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
     * 赞
     *
     * @param context
     * @param data
     * @param listener
     * @param errorListener
     */
    public static void zan(Context context, Map<String, String> data,
                           Listener<RespData> listener, ErrorListener errorListener) {
        ReqData reqData = new ReqData();
        reqData.setC("post");
        reqData.setA("zan");
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

    public static void deletePost(Context context, int pid,
                                  Listener<String> listener, ErrorListener errorListener) {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        ReqData reqData = new ReqData();
        reqData.setC("post");

        reqData.setA("del");
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", userInfo.getUserID());
        data.put("pid", String.valueOf(pid));
        reqData.setData(data);

        try {
            String url = context.getString(R.string.base_url) + reqData.toReqString();
            DebugLog.d("美物-攻略:" + url);
            VolleyHelper.getRequestQueue(context).add(
                    new StringRequest(url, listener, errorListener));

        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }
}
