package com.meishai.ui.fragment.home.req;

import java.util.Map;

import android.content.Context;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.net.MeishaiRequest;
import com.meishai.net.ReqData;
import com.meishai.net.RespData;
import com.meishai.net.VolleyHelper;
import com.meishai.net.volley.Request;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.toolbox.StringRequest;
import com.meishai.util.DebugLog;
import com.nimbusds.jose.JOSEException;

/**
 * 美晒分类
 *
 * @author sh
 */
public class HomeCateReq {

    /**
     * 美晒分类左侧数据 1.0 已弃用
     *
     * @param context
     * @param data
     * @param listener
     * @param errorListener
     */
    @Deprecated
    public static void channel(Context context, Map<String, String> data,
                               Listener<RespData> listener, ErrorListener errorListener) {
        ReqData reqData = new ReqData();
        reqData.setC("post");
        reqData.setA("channel");
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
     * 美晒分类右侧数据 1.0已弃用
     *
     * @param context
     * @param data
     * @param listener
     * @param errorListener
     */
    @Deprecated
    public static void catalogs(Context context, Map<String, String> data,
                                Listener<RespData> listener, ErrorListener errorListener) {
        ReqData reqData = new ReqData();
        reqData.setC("post");
        reqData.setA("catalogs");
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
     * 分类页面的请求 2.0
     *
     * @param context
     * @param data
     * @param listener
     * @param errorListener c:index,
     *                      <p/>
     *                      a:topic,
     *                      <p/>
     *                      f:iphone,//iphone端传入:iphone，Android端传入:android
     *                      <p/>
     *                      w:1,//是否wifi网络，wifi网络：w=1，非wifi网络：w=0
     *                      <p/>
     *                      token:md5(c值+a值+f值+密钥),
     *                      <p/>
     *                      data:{
     *                      <p/>
     *                      userid:1,//当前登录会员ID,未登陆请传入0
     *                      <p/>
     *                      typeid:区分是分类还是话题,分类列表:typeid=1,话题列表:typeid=2
     *                      <p/>
     *                      }
     */
    public static void cate(Context context, Map<String, String> data,
                            Listener<String> listener, ErrorListener errorListener) {
        ReqData reqData = new ReqData();
        reqData.setC("index");
        reqData.setA("topic");
        reqData.setData(data);
        try {
            String reqString = reqData.toReqString();
            String url = context.getString(R.string.base_url) + reqString;
            DebugLog.w(url);
            GlobalContext.getInstance().getRequestQueue().add(new StringRequest(url, listener, errorListener));
        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }
}
