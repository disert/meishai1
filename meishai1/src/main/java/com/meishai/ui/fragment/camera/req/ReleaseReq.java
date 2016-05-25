package com.meishai.ui.fragment.camera.req;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.text.TextUtils;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.dao.MeiShaiSP;
import com.meishai.net.MeishaiRequest;
import com.meishai.net.ReqData;
import com.meishai.net.RespData;
import com.meishai.net.VolleyHelper;
import com.meishai.net.volley.Cache.Entry;
import com.meishai.net.volley.RequestQueue;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.StringRequest;
import com.meishai.ui.base.CacheErrorListener;
import com.meishai.util.DebugLog;
import com.meishai.util.GsonHelper;
import com.nimbusds.jose.JOSEException;

public class ReleaseReq {
    /**
     * 发表->选择分类
     *
     * @param context
     * @param data
     * @param listener
     * @param errorListener
     */
    public static void choosecatalog(Context context, Map<String, String> data,
                                     Listener<RespData> listener, ErrorListener errorListener) {
        ReqData reqData = new ReqData();
        reqData.setC("post");
        reqData.setA("choosecatalog");
        reqData.setData(data);
        try {
            String reqString = reqData.toReqString();
            String url = context.getString(R.string.base_url) + reqString;
            DebugLog.d("分类:" + url);
            VolleyHelper.getRequestQueue(context).add(
                    new MeishaiRequest(url, listener, errorListener));
        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param context
     * @param data
     * @param listener
     * @param errorListener
     */
    public static void addcomment(Context context, Map<String, String> data,
                                  Listener<RespData> listener, ErrorListener errorListener) {
        ReqData reqData = new ReqData();
        reqData.setC("post");
        reqData.setA("addcomment");
        reqData.setData(data);
        try {
            String reqString = reqData.toReqString();
            String url = context.getString(R.string.base_url) + reqString;
            getRequestQueue().add(
                    new MeishaiRequest(url, listener, errorListener));
            // VolleyHelper.getRequestQueue(context).add(
            // new MeishaiRequest(url, listener, new
            // CacheErrorListener<RespData>(url, listener, errorListener)));
        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    /**
     * 晒晒@修改帖子@获取数据 老版本,为以前代码不出错而留
     *
     * @param context
     * @param data
     * @param listener
     * @param errorListener
     */
    public static void edit(Context context, Map<String, String> data,
                            Listener<RespData> listener, ErrorListener errorListener) {
        ReqData reqData = new ReqData();
        reqData.setC("post");
        reqData.setA("edit");
        reqData.setData(data);
        try {
            String reqString = reqData.toReqString();
            String url = context.getString(R.string.base_url) + reqString;
            DebugLog.d("获取修改帖子数据:" + url);
            VolleyHelper.getRequestQueue(context).add(
                    new MeishaiRequest(url, listener, errorListener));
        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    public static void edit1(Context context, Map<String, String> data,
                             Listener<String> listener, ErrorListener errorListener) {
        ReqData reqData = new ReqData();
        reqData.setC("post");
        reqData.setA("edit");
        reqData.setData(data);
        try {
            String reqString = reqData.toReqString();
            String url = context.getString(R.string.base_url) + reqString;
            DebugLog.d("获取修改帖子数据:" + url);
            VolleyHelper.getRequestQueue(context).add(
                    new StringRequest(url, listener, errorListener));
        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    /**
     * 美晒@发布@获取数据
     *
     * @param context
     * @param gid
     * @param tids
     * @param listener
     * @param errorListener
     */
    public static void pullReleaseData(Context context, int gid, String tids,
                                       Listener<String> listener, ErrorListener errorListener) {
        ReqData reqData = new ReqData();
        reqData.setC("create");
        reqData.setA("post");
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", MeiShaiSP.getInstance().getUserInfo().getUserID());
        data.put("gid", gid + "");
        data.put("tids", tids);

        reqData.setData(data);
        try {
            String reqString = reqData.toReqString();
            String url = context.getString(R.string.base_url) + reqString;
            DebugLog.d("发布 获取数据:" + url);
            VolleyHelper.getRequestQueue(context).add(
                    new StringRequest(url, listener, errorListener));
        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加链接,链接源
     *
     * @param context
     * @param keyword
     * @param listener
     * @param errorListener
     */
    public static void linkedSource(Context context, String keyword,
                                    Listener<String> listener, ErrorListener errorListener) {
        ReqData reqData = new ReqData();
        reqData.setC("create");
        reqData.setA("website");
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", MeiShaiSP.getInstance().getUserInfo().getUserID());
        data.put("keyword", keyword);
        reqData.setData(data);
        try {
            String reqString = reqData.toReqString();
            String url = context.getString(R.string.base_url) + reqString;
            DebugLog.d("添加链接,链接源:" + url);
            VolleyHelper.getRequestQueue(context).add(
                    new StringRequest(url, listener, errorListener));
        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取贴纸的请求
     *
     * @param context
     * @param listener
     * @param errorListener
     */
    public static void sticker(Context context, Listener<String> listener,
                               ErrorListener errorListener) {
        ReqData reqData = new ReqData();
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", MeiShaiSP.getInstance().getUserInfo().getUserID());
        reqData.setC("create");
        reqData.setA("sticker");
        reqData.setData(data);

        try {
            String reqString = reqData.toReqString();
            String url = context.getString(R.string.base_url) + reqString;
            DebugLog.d("贴纸:" + url);
            Entry entry = getRequestQueue().getCache().get(url);
            String cache = null;
            if ((entry != null) && (entry.data != null)) {
                cache = new String(entry.data);
            }
            if (TextUtils.isEmpty(cache)) {// 没缓存
                getRequestQueue().add(
                        new StringRequest(url, listener,
                                new CacheErrorListener(url, listener,
                                        errorListener)));
            } else {// 有缓存
                listener.onResponse(cache);
            }
        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    public static RequestQueue getRequestQueue() {
        return GlobalContext.getInstance().getRequestQueue();
    }

}
