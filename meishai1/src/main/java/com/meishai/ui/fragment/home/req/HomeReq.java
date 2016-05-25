package com.meishai.ui.fragment.home.req;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.UserInfo;
import com.meishai.net.MeishaiRequest;
import com.meishai.net.ReqData;
import com.meishai.net.RespData;
import com.meishai.net.VolleyHelper;
import com.meishai.net.volley.Request;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.StringRequest;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;
import com.nimbusds.jose.JOSEException;

/**
 * 美晒新增接口
 *
 * @author yl
 */
public class HomeReq {

    /**
     * 美晒@首页@达人俱乐部
     *
     * @param context
     * @param listener
     * @param errorListener
     */
    public static void darenTribe(Context context, Listener<String> listener,
                                  ErrorListener errorListener) {
        Map<String, String> data = new HashMap<String, String>();
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        ReqData reqData = new ReqData();
        reqData.setC("index");
        reqData.setA("daren");
        data.put("userid", userInfo.getUserID());
        reqData.setData(data);
        try {
            String reqString = reqData.toReqString();
            String url = context.getString(R.string.base_url) + reqString;
            DebugLog.d("达人俱乐部:" + url);
            VolleyHelper.getRequestQueue(context).add(new StringRequest(url, listener, errorListener));
        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    /**
     * 美晒@首页@每日签到
     *
     * @param context
     * @param listener
     * @param errorListener
     */
    public static void signIn(Context context, Listener<String> listener,
                              ErrorListener errorListener) {
        Map<String, String> data = new HashMap<String, String>();
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        ReqData reqData = new ReqData();
        reqData.setC("index");
        reqData.setA("sign");
        data.put("userid", userInfo.getUserID());
        reqData.setData(data);
        try {
            String reqString = reqData.toReqString();
            String url = context.getString(R.string.base_url) + reqString;
            DebugLog.d("每日签到:" + url);
            VolleyHelper.getRequestQueue(context).add(new StringRequest(url, listener, errorListener));
        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取邀请好友的URL的请求
     *
     * @param context
     * @param listener
     * @param errorListener
     */
    public static void laFriend(Context context, Listener<String> listener,
                                ErrorListener errorListener) {
        Map<String, String> data = new HashMap<String, String>();
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        ReqData reqData = new ReqData();
        reqData.setC("index");
        reqData.setA("invite");
        data.put("userid", userInfo.getUserID());
        reqData.setData(data);
        try {
            String reqString = reqData.toReqString();
            String url = context.getString(R.string.base_url) + reqString;
            DebugLog.d("邀请好友:" + url);
            VolleyHelper.getRequestQueue(context).add(new StringRequest(url, listener, errorListener));
        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    public static void share(Context context, int pid, int type, int status, Listener<String> listener,
                             ErrorListener errorListener) {
        Map<String, String> data = new HashMap<String, String>();
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        ReqData reqData = new ReqData();
        reqData.setC("post");
        reqData.setA("share");
        data.put("userid", userInfo.getUserID());
        data.put("pid", pid + "");
        data.put("type", type + "");
        data.put("status", status + "");
        reqData.setData(data);
        try {
            String reqString = reqData.toReqString();
            String url = context.getString(R.string.base_url) + reqString;
            DebugLog.d("邀请好友:" + url);
            VolleyHelper.getRequestQueue(context).add(new StringRequest(url, listener, errorListener));
        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    /**
     * 2016年1月26日15:05:46
     * <p/>
     * 个人主页的请求,
     *
     * @param context
     * @param uid
     * @param page
     * @param pagesize
     * @param listener
     * @param errorListener
     */
    public static void homePageShaiShai(Context context, String uid, int page, int pagesize, Listener<String> listener,
                                        ErrorListener errorListener) {
        Map<String, String> data = new HashMap<String, String>();
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        ReqData reqData = new ReqData();
        reqData.setC("user");
        reqData.setA("index");
        data.put("userid", userInfo.getUserID());

        data.put("uid", uid + "");
        data.put("page", page + "");
        data.put("pagesize", pagesize + "");
        reqData.setData(data);
        try {
            String reqString = reqData.toReqString();
            String url = context.getString(R.string.base_url) + reqString;
            DebugLog.d("个人主页:" + url);
            VolleyHelper.getRequestQueue(context).add(new StringRequest(url, listener, errorListener));
        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    /**
     * 2016年1月26日15:10:15
     * <p/>
     * 个人主页 关注
     *
     * @param context
     * @param uid
     * @param page
     * @param pagesize
     * @param listener
     * @param errorListener
     */
    public static void homePageFollow(Context context, int uid, int page, int pagesize, Listener<String> listener,
                                      ErrorListener errorListener) {
        Map<String, String> data = new HashMap<String, String>();
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        ReqData reqData = new ReqData();
        reqData.setC("user");
        reqData.setA("follow");
        data.put("userid", userInfo.getUserID());
        data.put("uid", uid + "");
        data.put("page", page + "");
        data.put("pagesize", pagesize + "");
        reqData.setData(data);
        try {
            String reqString = reqData.toReqString();
            String url = context.getString(R.string.base_url) + reqString;
            DebugLog.d("个人主页 关注:" + url);
            VolleyHelper.getRequestQueue(context).add(new StringRequest(url, listener, errorListener));
        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    /**
     * 2016年1月26日15:11:17
     * <p/>
     * 个人主页 粉丝
     *
     * @param context
     * @param uid
     * @param page
     * @param pagesize
     * @param listener
     * @param errorListener
     */
    public static void homePageFans(Context context, int uid, int page, int pagesize, Listener<String> listener,
                                    ErrorListener errorListener) {
        Map<String, String> data = new HashMap<String, String>();
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        ReqData reqData = new ReqData();
        reqData.setC("user");
        reqData.setA("fans");
        data.put("userid", userInfo.getUserID());
        data.put("uid", uid + "");
        data.put("page", page + "");
        data.put("pagesize", pagesize + "");
        reqData.setData(data);
        try {
            String reqString = reqData.toReqString();
            String url = context.getString(R.string.base_url) + reqString;
            DebugLog.d("个人主页 粉丝:" + url);
            VolleyHelper.getRequestQueue(context).add(new StringRequest(url, listener, errorListener));
        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    /**
     * 晒晒详情的请求
     *
     * @param context
     * @param pid
     * @param listener
     * @param errorListener
     */
    public static void postDetail(Context context, int pid, Listener<String> listener,
                                  ErrorListener errorListener) {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        ReqData reqData = new ReqData();
        reqData.setC("post");
        reqData.setA("show");
        Map<String, String> dataCate = new HashMap<String, String>();
        dataCate.put("userid", userInfo.getUserID());
        dataCate.put("pid", String.valueOf(pid));
        reqData.setData(dataCate);

        try {
            String url = context.getString(R.string.base_url) + reqData.toReqString();
            DebugLog.d("晒晒详情:" + url);
            VolleyHelper.getRequestQueue(context).add(
                    new StringRequest(url, listener, errorListener));

        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    /**
     * 评论详情的请求
     *
     * @param context
     * @param page
     * @param pagesize
     * @param pid           帖子的唯一标识
     * @param listorder     排序 listorder=0按楼层从低到高，listorder=1按楼层从高到低
     * @param listener
     * @param errorListener
     */
    public static void commentDetail(Context context, int page, int pagesize, int pid, int listorder, Listener<String> listener,
                                     ErrorListener errorListener) {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        ReqData reqData = new ReqData();

        reqData.setC("post");
        reqData.setA("comment");
        Map<String, String> dataCate = new HashMap<String, String>();
        dataCate.put("userid", userInfo.getUserID());
        dataCate.put("pid", String.valueOf(pid));
        dataCate.put("page", String.valueOf(page));
        dataCate.put("pagesize", String.valueOf(pagesize));
        dataCate.put("listorder", String.valueOf(listorder));
        reqData.setData(dataCate);

        try {
            String url = context.getString(R.string.base_url) + reqData.toReqString();
            DebugLog.d("评论详情:" + url);
            VolleyHelper.getRequestQueue(context).add(
                    new StringRequest(url, listener, errorListener));

        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    /**
     * 晒晒 发现 更多页面  达人之星的请求
     *
     * @param context
     * @param page
     * @param listener
     * @param errorListener
     */
    public static void moreStar(Context context, int page, Listener<String> listener,
                                ErrorListener errorListener) {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        ReqData reqData = new ReqData();

        reqData.setC("index");
        reqData.setA("darens");
        Map<String, String> dataCate = new HashMap<String, String>();
        dataCate.put("userid", userInfo.getUserID());
        dataCate.put("page", String.valueOf(page));
        dataCate.put("pagesize", String.valueOf(10));
        reqData.setData(dataCate);

        try {
            String url = context.getString(R.string.base_url) + reqData.toReqString();
            DebugLog.d("更多-达人之星:" + url);
            VolleyHelper.getRequestQueue(context).add(
                    new StringRequest(url, listener, errorListener));

        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    /**
     * 晒晒 发现 更多页面 其他的请求
     *
     * @param context
     * @param page
     * @param tempid
     * @param cid
     * @param listener
     * @param errorListener
     */
    public static void moreOther(Context context, int page, int tempid, int cid, Listener<String> listener,
                                 ErrorListener errorListener) {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        ReqData reqData = new ReqData();

        reqData.setC("index");
        reqData.setA("catalog");
        Map<String, String> dataCate = new HashMap<String, String>();
        dataCate.put("userid", userInfo.getUserID());
        dataCate.put("pagesize", String.valueOf(10));
        dataCate.put("page", String.valueOf(page));
        dataCate.put("tempid", String.valueOf(tempid));
        dataCate.put("cid", String.valueOf(cid));
        reqData.setData(dataCate);

        try {
            String url = context.getString(R.string.base_url) + reqData.toReqString();
            DebugLog.d("更多-other:" + url);
            VolleyHelper.getRequestQueue(context).add(
                    new StringRequest(url, listener, errorListener));

        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    /**
     * 热门活动的导航栏的请求
     *
     * @param context
     * @param listener
     * @param errorListener
     */
    public static void hotActionNavi(Context context, Listener<String> listener,
                                     ErrorListener errorListener) {
        //c=meiwu&a=item_navigation
        ReqData reqData = new ReqData();

        reqData.setC("meiwu");
        reqData.setA("item_navigation");
        Map<String, String> dataCate = new HashMap<String, String>();
        reqData.setData(dataCate);

        try {
            String url = context.getString(R.string.base_url) + reqData.toReqString();
            DebugLog.d("更多-other:" + url);
            VolleyHelper.getRequestQueue(context).add(
                    new StringRequest(url, listener, errorListener));

        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    /**
     * 热门活动 fragment获取数据所用的请求
     *
     * @param context
     * @param cid
     * @param listener
     * @param errorListener
     */
    public static void hotActionList(Context context, int page, int cid, Listener<String> listener,
                                     ErrorListener errorListener) {
        //c=meiwu&a=item_lists&cid=0
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        ReqData reqData = new ReqData();

        reqData.setC("meiwu");
        reqData.setA("item_lists");
        Map<String, String> dataCate = new HashMap<String, String>();
        dataCate.put("userid", userInfo.getUserID());
        dataCate.put("pagesize", String.valueOf(10));
        dataCate.put("page", String.valueOf(page));
        dataCate.put("cid", String.valueOf(cid));
        reqData.setData(dataCate);

        try {
            String url = context.getString(R.string.base_url) + reqData.toReqString();
            DebugLog.d("更多-other:" + url);
            VolleyHelper.getRequestQueue(context).add(
                    new StringRequest(url, listener, errorListener));

        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    /**
     * 我要爆料的文字请求,
     *
     * @param type          //type=3商品爆料；type=4店铺爆料
     * @param listener
     * @param errorListener
     */
    public static void baoliaoTextReq(Context context, int type, Listener<String> listener, ErrorListener errorListener) {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();

        ReqData reqData = new ReqData();
        reqData.setC("create");
        reqData.setA("baoliao");
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", userInfo.getUserID());
        data.put("type", type + "");
        reqData.setData(data);

        try {
            String url = context.getString(R.string.base_url) + reqData.toReqString();
            DebugLog.d("我要爆料文字+" + type + ":" + url);
            VolleyHelper.getRequestQueue(context).add(
                    new StringRequest(url, listener, errorListener));

        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    public static void baoliaoList(Context context, int page, Listener<String> listener, ErrorListener errorListener) {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        ReqData reqData = new ReqData();
        reqData.setC("member");
        reqData.setA("baoliao");
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", userInfo.getUserID());
        data.put("page", page + "");
        reqData.setData(data);

        try {
            String url = context.getString(R.string.base_url) + reqData.toReqString();
            DebugLog.d("我要爆料列表:" + url);
            VolleyHelper.getRequestQueue(context).add(
                    new StringRequest(url, listener, errorListener));

        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }
}
