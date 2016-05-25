package com.meishai.ui.fragment.home;

import java.util.HashMap;
import java.util.Map;


import android.content.Context;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.dao.MeiShaiSP;
import com.meishai.net.ReqData;
import com.meishai.net.volley.RequestQueue;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.toolbox.StringRequest;
import com.meishai.ui.fragment.usercenter.LoginActivity;
import com.meishai.util.DebugLog;
import com.nimbusds.jose.JOSEException;

public class HomeData {

    public final static int RET_UNLOGIN = -1;    // 未登陆
    public final static int RET_ERROR = 0;        // 错误 (已经赞过)
    public final static int RET_SUCCESS = 1;    // 成功

    private ReqData reqData = new ReqData();
    private RequestQueue queue = GlobalContext.getInstance().getRequestQueue();
    protected String baseUrl = GlobalContext.getInstance().getResources().getString(R.string.base_url);

    private HomeData() {

    }

    private static HomeData instance = new HomeData();

    public static HomeData getInstance() {
        return instance;
    }

    /**
     * 关注会员
     *
     * @param context
     * @param fuserid
     * @param listener
     * @param errorListener
     */
    public void reqAttention(Context context, String fuserid, Listener<String> listener, ErrorListener errorListener) {
        if (!MeiShaiSP.getInstance().getUserInfo().isLogin()) {
            context.startActivity(LoginActivity.newOtherIntent());
            return;
        }
        reqData.setC("public");
        reqData.setA("addfriend");
        Map<String, String> map = new HashMap<String, String>();
        map.put("userid", MeiShaiSP.getInstance().getUserInfo().getUserID());
        map.put("fuserid", fuserid);
        reqData.setData(map);

        addRequest(listener, errorListener);
    }

    /**
     * 关注话题
     *
     * @param context
     * @param tid
     * @param listener
     * @param errorListener
     */
    public void reqAttentionTopic(Context context, int tid, Listener<String> listener, ErrorListener errorListener) {
        if (!MeiShaiSP.getInstance().getUserInfo().isLogin()) {
            context.startActivity(LoginActivity.newOtherIntent());
            return;
        }


        reqData.setC("public");
        reqData.setA("addtopic");
        Map<String, String> map = new HashMap<String, String>();
        map.put("userid", MeiShaiSP.getInstance().getUserInfo().getUserID());
        map.put("tid", tid + "");
        reqData.setData(map);

        addRequest(listener, errorListener);
    }

    public void reqZan(int pid, Listener<String> listener, ErrorListener errorListener) {
        reqData.setC("post");
        reqData.setA("zan");
        Map<String, String> map = new HashMap<String, String>();
        map.put("userid", MeiShaiSP.getInstance().getUserInfo().getUserID());
        map.put("pid", String.valueOf(pid));
        reqData.setData(map);


        addRequest(listener, errorListener);
    }


    private void addRequest(Listener<String> listener, ErrorListener errorListener) {
        try {
            String url = baseUrl + reqData.toReqString();
            DebugLog.d(url);
            queue.add(new StringRequest(url, listener, errorListener));
        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

}
