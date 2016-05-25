package com.meishai.ui.fragment.tryuse.req;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.UserInfo;
import com.meishai.net.ReqData;
import com.meishai.net.RespData;
import com.meishai.net.volley.Request;
import com.meishai.net.volley.RequestQueue;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.toolbox.StringRequest;
import com.meishai.ui.fragment.usercenter.LoginActivity;
import com.meishai.util.DebugLog;
import com.nimbusds.jose.JOSEException;

/**
 * 福利社的请求封装类
 *
 * @author Administrator yl
 */
public class FuLiSheReq {

    /**
     * 福利社-限时抢购的请求
     *
     * @param page
     * @param listener
     * @param errorListener
     */
    public static void flashSale(int page, Listener<String> listener,
                                 ErrorListener errorListener) {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        ReqData reqData = new ReqData();

        reqData.setC("fuli");
        reqData.setA("qiang");
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", userInfo.getUserID());
        data.put("page", String.valueOf(page));
        reqData.setData(data);
        try {
            String reqString = reqData.toReqString();
            String url = getString(R.string.base_url) + reqString;
            DebugLog.d("福利社 - 限时抢购:" + url);
            getRequestQueue().add(
                    new StringRequest(url, listener, errorListener));
        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    /**
     * 福利社-积分商城的请求
     *
     * @param page
     * @param listener
     * @param errorListener
     */
    public static void pointReward(int page, Listener<String> listener,
                                   ErrorListener errorListener) {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        ReqData reqData = new ReqData();
        reqData.setC("fuli");
        reqData.setA("point");
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", userInfo.getUserID());
        data.put("page", String.valueOf(page));
        reqData.setData(data);
        try {
            String reqString = reqData.toReqString();
            String url = getString(R.string.base_url) + reqString;
            DebugLog.d("福利社 - 积分商城:" + url);
            getRequestQueue().add(
                    new StringRequest(url, listener, errorListener));
        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    /**
     * 福利社 - 品质体验
     *
     * @param page
     * @param listener
     * @param errorListener
     */
    public static void taste(int page, Listener<String> listener,
                             ErrorListener errorListener) {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        ReqData reqData = new ReqData();

        reqData.setC("fuli");
        reqData.setA("init");
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", userInfo.getUserID());
        data.put("page", String.valueOf(page));
        reqData.setData(data);
        try {
            String reqString = reqData.toReqString();
            String url = getString(R.string.base_url) + reqString;
            DebugLog.d("福利社 - 品质体验:" + url);
            getRequestQueue().add(
                    new StringRequest(url, listener, errorListener));
        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    /**
     * 福利社-免费试用的请求
     *
     * @param page
     * @param listener
     * @param errorListener
     */
    public static void freeTrial(int page, Listener<String> listener,
                                 ErrorListener errorListener) {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        ReqData reqData = new ReqData();
        reqData.setC("fuli");
        reqData.setA("index");
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", userInfo.getUserID());
        data.put("page", String.valueOf(page));
        reqData.setData(data);
        try {
            String reqString = reqData.toReqString();
            String url = getString(R.string.base_url) + reqString;
            DebugLog.d("福利社 - 免费试用:" + url);
            getRequestQueue().add(
                    new StringRequest(url, listener, errorListener));
        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    public static String freeTrialUrl(int page) {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        ReqData reqData = new ReqData();
        reqData.setC("fuli");
        reqData.setA("index");
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", userInfo.getUserID());
        data.put("page", String.valueOf(page));
        reqData.setData(data);
        try {
            String reqString = reqData.toReqString();
            String url = getString(R.string.base_url) + reqString;
            return url;
        } catch (JOSEException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 福利社-试用详情的请求
     *
     * @param gid
     * @param listener
     * @param errorListener
     */
    public static void trialDetail(int gid, Listener<String> listener,
                                   ErrorListener errorListener) {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        ReqData reqData = new ReqData();

        reqData.setC("fuli");
        reqData.setA("try_show");
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", userInfo.getUserID());
        data.put("gid", String.valueOf(gid));
        reqData.setData(data);
        try {
            String reqString = reqData.toReqString();
            String url = getString(R.string.base_url) + reqString;
            DebugLog.d("福利社 - 试用详情:" + url);
            getRequestQueue().add(
                    new StringRequest(url, listener, errorListener));
        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    /**
     * 福利社@积分商城@分类列表的请求
     *
     * @param typeid
     * @param page
     * @param listener
     * @param errorListener
     */
    public static void cateList(int typeid, int page,
                                Listener<String> listener, ErrorListener errorListener) {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        ReqData reqData = new ReqData();

        reqData.setC("fuli");
        reqData.setA("lists");
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", userInfo.getUserID());
        data.put("typeid", String.valueOf(typeid));
        data.put("page", String.valueOf(page));
        reqData.setData(data);
        try {
            String reqString = reqData.toReqString();
            String url = getString(R.string.base_url) + reqString;
            DebugLog.d("积分商城 - 分类列表:" + url);
            getRequestQueue().add(
                    new StringRequest(url, listener, errorListener));
        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    /**
     * 福利社@限时疯抢@疯抢详情的请求
     *
     * @param gid
     * @param listener
     * @param errorListener
     */
    public static void caleDetail(int gid, Listener<String> listener,
                                  ErrorListener errorListener) {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        ReqData reqData = new ReqData();

        reqData.setC("fuli");
        reqData.setA("qiang_show");
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", userInfo.getUserID());
        data.put("gid", String.valueOf(gid));
        reqData.setData(data);
        try {
            String reqString = reqData.toReqString();
            String url = getString(R.string.base_url) + reqString;
            DebugLog.d("限时疯抢 - 疯抢详情:" + url);
            getRequestQueue().add(
                    new StringRequest(url, listener, errorListener));
        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    /**
     * 福利社@福利详情的请求
     *
     * @param gid
     * @param listener
     * @param errorListener
     */
    public static void fulisheDetail(int gid, Listener<String> listener,
                                     ErrorListener errorListener) {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        ReqData reqData = new ReqData();

        reqData.setC("fuli");
        reqData.setA("show");
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", userInfo.getUserID());
        data.put("gid", String.valueOf(gid));
        reqData.setData(data);
        try {
            String reqString = reqData.toReqString();
            String url = getString(R.string.base_url) + reqString;
            DebugLog.d("福利社 - 详情:" + url);
            getRequestQueue().add(
                    new StringRequest(url, listener, errorListener));
        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    /**
     * 福利社@积分商城@积分详情
     *
     * @param gid
     * @param listener
     * @param errorListener
     */
    public static void pointDetail(int gid, Listener<String> listener,
                                   ErrorListener errorListener) {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        ReqData reqData = new ReqData();

        reqData.setC("fuli");
        reqData.setA("point_show");
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", userInfo.getUserID());
        data.put("gid", String.valueOf(gid));
        reqData.setData(data);
        try {
            String reqString = reqData.toReqString();
            String url = getString(R.string.base_url) + reqString;
            DebugLog.d("积分商城 - 积分详情:" + url);
            getRequestQueue().add(
                    new StringRequest(url, listener, errorListener));
        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    /**
     * 福利社@限时疯抢&积分商城@确认兑换
     *
     * @param gid
     * @param listener
     * @param errorListener
     */
    public static void confirmTrade(int gid, Listener<String> listener,
                                    ErrorListener errorListener) {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        ReqData reqData = new ReqData();
        reqData.setC("fuli");
        reqData.setA("confirm");
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", userInfo.getUserID());
        data.put("gid", String.valueOf(gid));
        reqData.setData(data);
        try {
            String reqString = reqData.toReqString();
            String url = getString(R.string.base_url) + reqString;
            DebugLog.d("积分商城 - 确认兑换:" + url);
            getRequestQueue().add(
                    new StringRequest(url, listener, errorListener));
        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    /**
     * 福利社@分享传参
     *
     * @param gid
     * @param type          1、朋友圈；2、微信；3、QQ空间；4、QQ好友；5、腾讯微博；6、新浪微博
     * @param status        分享状态，返回美晒传1，未返回则传0,错误返回-99
     * @param listener
     * @param errorListener
     */
    public static void share(int gid, int type, int status,
                             Listener<String> listener, ErrorListener errorListener) {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        if (!userInfo.isLogin()) {
            getContext().startActivity(LoginActivity.newOtherIntent());
            return;
        }
        ReqData reqData = new ReqData();

        reqData.setC("fuli");
        reqData.setA("share");
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", userInfo.getUserID());
        data.put("gid", String.valueOf(gid));
        data.put("type", String.valueOf(type));
        data.put("status", String.valueOf(status));
        reqData.setData(data);
        try {
            String reqString = reqData.toReqString();
            String url = getString(R.string.base_url) + reqString;
            DebugLog.d("分享传参:" + url);
            getRequestQueue().add(
                    new StringRequest(url, listener, errorListener));
        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    /**
     * 福利社@免费试用&限时疯抢&积分商城@提交申请
     *
     * @param gid
     * @param aid           地址ID，免费试用传0，限时疯抢、积分商城不能为空
     * @param kid           尺寸id，如果没有则传0
     * @param colorid       颜色id，如果没有则传0
     * @param point         出价积分，如果没有则传0
     * @param listener
     * @param errorListener
     */
    public static void submit(int gid, int aid, int kid, int colorid,
                              int point, Listener<String> listener, ErrorListener errorListener) {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        if (!userInfo.isLogin()) {
            getContext().startActivity(LoginActivity.newOtherIntent());
            return;
        }
        ReqData reqData = new ReqData();

        reqData.setC("fuli");
        reqData.setA("apply");
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", userInfo.getUserID());
        data.put("gid", String.valueOf(gid));
        data.put("aid", String.valueOf(aid));
        data.put("kid", String.valueOf(kid));
        data.put("colorid", String.valueOf(colorid));
        data.put("point", String.valueOf(point));
        reqData.setData(data);
        try {
            String reqString = reqData.toReqString();
            String url = getString(R.string.base_url) + reqString;
            DebugLog.d("积分商城 - 提交申请:" + url);
            getRequestQueue().add(
                    new StringRequest(Request.Method.POST, url, listener,
                            errorListener));
        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    /**
     * 福利社 - 详情 - 直接购买时发送给服务器的数据
     *
     * @param gid
     * @param listener
     * @param errorListener
     */
    public static void buy(int gid, Listener<String> listener,
                           ErrorListener errorListener) {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        // if(!userInfo.isLogin()){
        // getContext().startActivity(LoginActivity.newOtherIntent());
        // return;
        // }
        ReqData reqData = new ReqData();
        reqData.setC("fuli");
        reqData.setA("buy");
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", userInfo.getUserID());
        data.put("gid", String.valueOf(gid));
        reqData.setData(data);

        try {
            String url = getString(R.string.base_url) + reqData.toReqString();
            DebugLog.d("购物:" + url);
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
