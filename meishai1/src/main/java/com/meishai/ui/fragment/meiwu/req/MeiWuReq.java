package com.meishai.ui.fragment.meiwu.req;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.UserInfo;
import com.meishai.net.ReqData;
import com.meishai.net.volley.RequestQueue;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.toolbox.StringRequest;
import com.meishai.ui.fragment.MeishaiWebviewActivity;
import com.meishai.ui.fragment.usercenter.LoginActivity;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;
import com.meishai.util.PackageManagerUtils;
import com.nimbusds.jose.JOSEException;

public class MeiWuReq {

    /**
     * 1.美物-热门攻略请求
     *
     * @param page
     * @param listener
     * @param errorListener
     */
    public static void strategyReq(int page, Listener<String> listener,
                                   ErrorListener errorListener) {

        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        ReqData reqData = new ReqData();
        reqData.setC("meiwu");
        reqData.setA("init");
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", userInfo.getUserID());
        data.put("page", String.valueOf(page));
        reqData.setData(data);
        String url = "";
        try {
            url = getString(R.string.base_url) + reqData.toReqString();
            DebugLog.d("美物-攻略:" + url);
            getRequestQueue().add(
                    new StringRequest(url, listener, errorListener));

        } catch (JOSEException e) {
            e.printStackTrace();
        }

    }

    /**
     * 2.美物-单品推荐 请求
     *
     * @param page
     * @param listener
     * @param errorListener
     */
    public static void SKUReq(int page, Listener<String> listener,
                              ErrorListener errorListener) {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();

        ReqData reqData = new ReqData();
        reqData.setC("meiwu");
        reqData.setA("items");
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", userInfo.getUserID());
        data.put("page", String.valueOf(page));
        reqData.setData(data);
        String url = "";
        try {
            url = getString(R.string.base_url) + reqData.toReqString();
            DebugLog.d("美物-单品:" + url);
            getRequestQueue().add(
                    new StringRequest(url, listener, errorListener));

        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    /**
     * 美物-品质专场 请求
     *
     * @param page
     * @param listener
     * @param errorListener
     */
    public static void specialReq(int page, Listener<String> listener,
                                  ErrorListener errorListener) {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        ReqData reqData = new ReqData();
        reqData.setC("meiwu");
        reqData.setA("quality");
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", userInfo.getUserID());
        data.put("page", String.valueOf(page));
        reqData.setData(data);
        String url = "";
        try {
            url = getString(R.string.base_url) + reqData.toReqString();
            DebugLog.d("美物-品质专场:" + url);
            getRequestQueue().add(
                    new StringRequest(url, listener, errorListener));

        } catch (JOSEException e) {
            e.printStackTrace();
        }

    }

    /**
     * 3.美物-分类请求
     *
     * @param page
     * @param listener
     * @param errorListener
     */
    public static void CateReq(int page, Listener<String> listener,
                               ErrorListener errorListener) {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();

        ReqData reqData = new ReqData();
        reqData.setC("meiwu");
        reqData.setA("cate");
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", userInfo.getUserID());
        data.put("page", String.valueOf(page));
        reqData.setData(data);
        String url = "";
        try {
            url = getString(R.string.base_url) + reqData.toReqString();
            DebugLog.d("美物-分类:" + url);
            getRequestQueue().add(
                    new StringRequest(url, listener, errorListener));

        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    /**
     * 美物 - 品质好店 2015年12月4日14:03:16 新增,替换单品推荐
     *
     * @param page
     * @param listener
     * @param errorListener
     */
    public static void goodShops(int page, Listener<String> listener,
                                 ErrorListener errorListener) {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();

        ReqData reqData = new ReqData();
        reqData.setC("meiwu");
        reqData.setA("shops");
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", userInfo.getUserID());
        data.put("page", String.valueOf(page));
        reqData.setData(data);
        String url = "";
        try {
            url = getString(R.string.base_url) + reqData.toReqString();
            DebugLog.d("美物-品质好店:" + url);
            getRequestQueue().add(
                    new StringRequest(url, listener, errorListener));

        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    /**
     * 4.美物-攻略列表
     *
     * @param page
     * @param cid
     * @param listener
     * @param errorListener
     */
    public static void strategyListReq(int page, int cid,
                                       Listener<String> listener, ErrorListener errorListener) {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();

        ReqData reqData = new ReqData();
        reqData.setC("meiwu");
        reqData.setA("lists");
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", userInfo.getUserID());
        data.put("cid", String.valueOf(cid));
        data.put("page", String.valueOf(page));
        reqData.setData(data);
        String url = "";
        try {
            url = getString(R.string.base_url) + reqData.toReqString();
            DebugLog.d("美物-攻略列表:" + url);
            getRequestQueue().add(
                    new StringRequest(url, listener, errorListener));

        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    /**
     * 5.美物@攻略@攻略详情
     *
     * @param tid
     * @param listener
     * @param errorListener
     */
    public static void strategyDetailsReq(int tid, Listener<String> listener,
                                          ErrorListener errorListener) {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();

        ReqData reqData = new ReqData();
        reqData.setC("meiwu");
        reqData.setA("details");
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", userInfo.getUserID());
        data.put("tid", String.valueOf(tid));
        reqData.setData(data);
        String url = "";
        try {
            url = getString(R.string.base_url) + reqData.toReqString();
            DebugLog.d("美物-攻略详情:" + url);
            getRequestQueue().add(
                    new StringRequest(url, listener, errorListener));

        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    /**
     * 5.美物@攻略@攻略详情  新更改的 2015年11月17日17:25:29
     *
     * @param tid
     * @param listener
     * @param errorListener
     */
    public static void strategyDetailsReq1(int tid, Listener<String> listener,
                                           ErrorListener errorListener) {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();

        //c=meiwu&a=content&tid=337
        ReqData reqData = new ReqData();
        reqData.setC("meiwu");
        reqData.setA("content");
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", userInfo.getUserID());
        data.put("tid", String.valueOf(tid));
        reqData.setData(data);
        String url = "";
        try {
            url = getString(R.string.base_url) + reqData.toReqString();
            DebugLog.d("美物-攻略详情:" + url);
            getRequestQueue().add(
                    new StringRequest(url, listener, errorListener));

        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    public static void stratListReq(int tid, Listener<String> listener,
                                    ErrorListener errorListener) {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();

        //c=meiwu&a=rank&tid=1060
        ReqData reqData = new ReqData();
        reqData.setC("meiwu");
        reqData.setA("rank");
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", userInfo.getUserID());
        data.put("tid", String.valueOf(tid));
        reqData.setData(data);
        String url = "";
        try {
            url = getString(R.string.base_url) + reqData.toReqString();
            DebugLog.d("美物-攻略列表tempid=3:" + url);
            getRequestQueue().add(
                    new StringRequest(url, listener, errorListener));

        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    /**
     * 6.美物@专场@专场分类
     *
     * @param listener
     * @param errorListener
     */
    public static void specialCateReq(Listener<String> listener,
                                      ErrorListener errorListener) {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();

        ReqData reqData = new ReqData();
        reqData.setC("meiwu");
        reqData.setA("special_cate");
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", userInfo.getUserID());
        reqData.setData(data);
        String url = "";
        try {
            url = getString(R.string.base_url) + reqData.toReqString();
            DebugLog.d("美物-专场分类:" + url);
            getRequestQueue().add(
                    new StringRequest(url, listener, errorListener));

        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    /**
     * 6.美物@专场@专场列表
     *
     * @param cid
     * @param listener
     * @param errorListener
     */
    public static void specialListReq(int page, int cid,
                                      Listener<String> listener, ErrorListener errorListener) {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();

        ReqData reqData = new ReqData();
        reqData.setC("meiwu");
        reqData.setA("special");
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", userInfo.getUserID());
        data.put("cid", String.valueOf(cid));
        data.put("page", String.valueOf(page));
        reqData.setData(data);
        String url = "";
        try {
            url = getString(R.string.base_url) + reqData.toReqString();
            DebugLog.d("美物-专场列表:" + url);
            getRequestQueue().add(
                    new StringRequest(url, listener, errorListener));

        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    /**
     * 7.美物@专场@专场详情
     *
     * @param page
     * @param tid
     * @param listener
     * @param errorListener
     */
    public static void specialDetailsReq(int page, int tid,
                                         Listener<String> listener, ErrorListener errorListener) {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        ReqData reqData = new ReqData();
        reqData.setC("meiwu");
        reqData.setA("special_list");
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", userInfo.getUserID());
        data.put("tid", String.valueOf(tid));
        data.put("page", String.valueOf(page));
        reqData.setData(data);

        try {
            String url = getString(R.string.base_url) + reqData.toReqString();
            DebugLog.d("美物-专场详情:" + url);
            getRequestQueue().add(
                    new StringRequest(url, listener, errorListener));

        } catch (JOSEException e) {
            e.printStackTrace();
        }

    }

    /**
     * 品质好店 - 好店详情
     * <p/>
     * 2015年12月4日14:35:29
     *
     * @param page
     * @param tid
     * @param listener
     * @param errorListener
     */
    public static void shopsDetailsReq(int page, int tid,
                                       Listener<String> listener, ErrorListener errorListener) {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        ReqData reqData = new ReqData();
        reqData.setC("meiwu");
        reqData.setA("shop_list");
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", userInfo.getUserID());
        data.put("tid", String.valueOf(tid));
        data.put("page", String.valueOf(page));
        reqData.setData(data);

        try {
            String url = getString(R.string.base_url) + reqData.toReqString();
            DebugLog.d("美物-好店详情:" + url);
            getRequestQueue().add(
                    new StringRequest(url, listener, errorListener));

        } catch (JOSEException e) {
            e.printStackTrace();
        }

    }

    /**
     * 8.商品详情
     *
     * @param pid
     * @param listener
     * @param errorListener
     */
    public static void commodDetailsReq(int pid, Listener<String> listener,
                                        ErrorListener errorListener) {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        ReqData reqData = new ReqData();
        reqData.setC("meiwu");
        reqData.setA("show");
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", userInfo.getUserID());
        data.put("pid", String.valueOf(pid));
        reqData.setData(data);

        try {
            String url = getString(R.string.base_url) + reqData.toReqString();
            DebugLog.d("美物-商品详情:" + url);
            getRequestQueue().add(
                    new StringRequest(url, listener, errorListener));

        } catch (JOSEException e) {
            e.printStackTrace();
        }

    }

    /**
     * 攻略关注(话题关注)
     *
     * @param tid
     * @param listener
     * @param errorListener
     */
    public static void attention(int tid, Listener<String> listener,
                                 ErrorListener errorListener) {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        if (!userInfo.isLogin()) {
            getContext().startActivity(LoginActivity.newOtherIntent());
            return;
        }
        ReqData reqData = new ReqData();
        reqData.setC("public");
        reqData.setA("addtopic");
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", userInfo.getUserID());
        data.put("tid", String.valueOf(tid));
        reqData.setData(data);

        try {
            String url = getString(R.string.base_url) + reqData.toReqString();
            getRequestQueue().add(
                    new StringRequest(url, listener, errorListener));

        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    /**
     * 攻略取消关注(话题关注)
     *
     * @param tid
     * @param listener
     * @param errorListener
     */
    public static void unAttention(int tid, Listener<String> listener,
                                   ErrorListener errorListener) {

        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        if (!userInfo.isLogin()) {
            getContext().startActivity(LoginActivity.newOtherIntent());
            return;
        }
        ReqData reqData = new ReqData();
        reqData.setC("public");
        reqData.setA("deltopic");
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", userInfo.getUserID());
        data.put("tid", String.valueOf(tid));
        reqData.setData(data);

        try {
            String url = getString(R.string.base_url) + reqData.toReqString();
            getRequestQueue().add(
                    new StringRequest(url, listener, errorListener));

        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    /**
     * 收藏
     *
     * @param pid
     * @param listener
     * @param errorListener
     */
    public static void collect(int pid, Listener<String> listener,
                               ErrorListener errorListener) {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        if (!userInfo.isLogin()) {
            getContext().startActivity(LoginActivity.newOtherIntent());
            return;
        }
        ReqData reqData = new ReqData();
        reqData.setC("post");
        reqData.setA("fav");
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", userInfo.getUserID());
        data.put("pid", String.valueOf(pid));
        reqData.setData(data);

        try {
            String url = getString(R.string.base_url) + reqData.toReqString();
            DebugLog.d("收藏:" + url);
            getRequestQueue().add(
                    new StringRequest(url, listener, errorListener));

        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    /**
     * 点赞
     *
     * @param pid
     * @param listener
     * @param errorListener
     */
    public static void zan(int pid, Listener<String> listener,
                           ErrorListener errorListener) {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        if (!userInfo.isLogin()) {
            getContext().startActivity(LoginActivity.newOtherIntent());
            return;
        }
        ReqData reqData = new ReqData();
        reqData.setC("post");
        reqData.setA("zan");
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", userInfo.getUserID());
        data.put("pid", String.valueOf(pid));
        reqData.setData(data);

        try {
            String url = getString(R.string.base_url) + reqData.toReqString();
            DebugLog.d("点赞:" + url);
            getRequestQueue().add(
                    new StringRequest(url, listener, errorListener));

        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    /**
     * 购物的请求,向服务器传递一些数据
     *
     * @param pid
     * @param listener
     * @param errorListener
     */
    public static void buy(int pid, Listener<String> listener,
                           ErrorListener errorListener) {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        // if(!userInfo.isLogin()){
        // getContext().startActivity(LoginActivity.newOtherIntent());
        // return;
        // }
        ReqData reqData = new ReqData();
        reqData.setC("meiwu");
        reqData.setA("buy");
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", userInfo.getUserID());
        data.put("pid", String.valueOf(pid));
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

    /**
     * 用户点击分享时发送的请求
     *
     * @param tid           攻略TID
     * @param type          分享类型：1、朋友圈；2、微信；3、QQ空间；4、QQ好友；5、腾讯微博；6、新浪微博
     * @param status        分享状态，返回美晒传1，未返回则传0
     * @param listener
     * @param errorListener
     */
    public static void share(int tid, int type, int status,
                             Listener<String> listener, ErrorListener errorListener) {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        if (!userInfo.isLogin()) {
            getContext().startActivity(LoginActivity.newOtherIntent());
            return;
        }

        ReqData reqData = new ReqData();
        reqData.setC("meiwu");
        reqData.setA("share");
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", userInfo.getUserID());
        data.put("tid", String.valueOf(tid));
        data.put("type", String.valueOf(type));
        data.put("status", String.valueOf(status));
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

    /**
     * @param pid
     * @param type   分享类型：1、朋友圈；2、微信；3、QQ空间；4、QQ好友；5、腾讯微博；6、新浪微博
     * @param status 分享状态，返回美晒传1，未返回则传0,错误返回-99
     */
    public static void sharePid(int pid, int type, int status) {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        if (!userInfo.isLogin()) {
            getContext().startActivity(LoginActivity.newOtherIntent());
            return;
        }
        ReqData reqData = new ReqData();
        reqData.setC("post");
        reqData.setA("share");
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", userInfo.getUserID());
        data.put("pid", String.valueOf(pid));
        data.put("type", String.valueOf(type));
        data.put("status", String.valueOf(status));
        reqData.setData(data);

        try {
            String url = getString(R.string.base_url) + reqData.toReqString();
            DebugLog.d("购物:" + url);
            getRequestQueue().add(
                    new StringRequest(url, new Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            DebugLog.d("分享请求发送成功");
                            try {
                                JSONObject obj = new JSONObject(response);
                                String tips = obj.getString("tips");
                                if (!TextUtils.isEmpty(tips)) {
//									AndroidUtil.showToast(tips);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            DebugLog.w("分享请求发送出错");
                            error.printStackTrace();
                        }
                    }));

        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    /**
     * 搜索的请求
     *
     * @param key
     * @param type
     * @param listener
     * @param errorListener
     */
    public static void search(String key, int type,
                              Listener<String> listener, ErrorListener errorListener) {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();

        DebugLog.w("搜索请求:" + "key:" + key + ",type:" + type);

        ReqData reqData = new ReqData();
        reqData.setC("search");
        reqData.setA("index");
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", userInfo.getUserID());
        data.put("key", key);
        data.put("type", String.valueOf(type));
        reqData.setData(data);

        try {
            String url = getString(R.string.base_url) + reqData.toReqString();
            DebugLog.d("搜索:" + url);
            getRequestQueue().add(
                    new StringRequest(url, listener, errorListener));

        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    @Deprecated
    public static void searchShaiShai(String key, int type,
                                      Listener<String> listener, ErrorListener errorListener) {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();

        DebugLog.w("搜索请求:" + "key:" + key + ",type:" + type);
        ReqData reqData = new ReqData();
        reqData.setC("meiwu");
        reqData.setA("search");
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", userInfo.getUserID());
        data.put("key", key);
        data.put("type", String.valueOf(type));
        reqData.setData(data);

        try {
            String url = getString(R.string.base_url) + reqData.toReqString();
            DebugLog.d("搜索:" + url);
            getRequestQueue().add(
                    new StringRequest(url, listener, errorListener));

        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    /**
     * 请求美物分类栏的数据
     *
     * @param listener
     * @param errorListener
     */
    public static void meiwu(Listener<String> listener, ErrorListener errorListener) {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();

        ReqData reqData = new ReqData();
        reqData.setC("meiwu");
        reqData.setA("navigation");
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", userInfo.getUserID());
        reqData.setData(data);

        try {
            String url = getString(R.string.base_url) + reqData.toReqString();
            DebugLog.d("美物分类栏:" + url);
            getRequestQueue().add(
                    new StringRequest(url, listener, errorListener));

        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    public static String meiwuUrl() {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        DebugLog.w("userid:" + userInfo.getUserID());
        ReqData reqData = new ReqData();
        reqData.setC("meiwu");
        reqData.setA("navigation");
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", userInfo.getUserID());
        reqData.setData(data);

        try {
            String url = getString(R.string.base_url) + reqData.toReqString();
            return url;

        } catch (JOSEException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 请求美物子分类的数据
     *
     * @param listener
     * @param errorListener
     */
    public static void meiwuSub(int page, int cid, Listener<String> listener, ErrorListener errorListener) {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();

        ReqData reqData = new ReqData();
        reqData.setC("meiwu");
        reqData.setA("index");
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", userInfo.getUserID());
        data.put("cid", cid + "");
        data.put("page", page + "");
        reqData.setData(data);

        try {
            String url = getString(R.string.base_url) + reqData.toReqString();
            DebugLog.d("美物子分类" + cid + ":" + url);
            getRequestQueue().add(
                    new StringRequest(url, listener, errorListener));

        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    public static String meiwuSubUrl(int page, int cid) {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();

        ReqData reqData = new ReqData();
        reqData.setC("meiwu");
        reqData.setA("index");
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", userInfo.getUserID());
        data.put("cid", cid + "");
        data.put("page", page + "");
        reqData.setData(data);

        try {
            String url = getString(R.string.base_url) + reqData.toReqString();
            return url;

        } catch (JOSEException e) {
            e.printStackTrace();
            return "";
        }
    }


    public static void buyReq(final Context context, int pid, final String url,
                              final int istao) {


        // 发送购买的请求.在请求中有URL直接跳转到淘宝
        MeiWuReq.buy(pid, new Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    int success = json.getInt("success");
                    String tips = json.getString("tips");
                    if (success == 1) {
                        if (!TextUtils.isEmpty(tips)) {
                            AndroidUtil.showToast(tips);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                AndroidUtil.showToast("购物请求发送失败");
                error.printStackTrace();
            }
        });

        //跳h5或者淘宝
        boolean install = PackageManagerUtils.isInstall(
                getContext(), "com.taobao.taobao");
        if (istao == 1 && install) {
            // TODO 处理url 启动淘宝
            //对URL进行处理
            String[] split = url.split(":");
            String url1 = "taobao:" + split[1];
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri content_url = Uri.parse(url1);
            intent.setData(content_url);
            //判断这个intent是否有activity能执行,
            PackageManager pm = context.getPackageManager();
            ComponentName cn = intent.resolveActivity(pm);
            if (cn == null) {
                DebugLog.d("没有可以执行隐性intent的activity(淘宝).");
                context.startActivity(MeishaiWebviewActivity
                        .newIntent(url));
            } else {
                context.startActivity(intent);
            }
//							context.startActivity(intent);
        } else {
            DebugLog.w("链接:" + url);
            Intent intent = MeishaiWebviewActivity
                    .newIntent(url);
            context.startActivity(intent);
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
