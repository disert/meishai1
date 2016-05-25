package com.meishai.util;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.UserInfo;
import com.meishai.net.ReqData;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.toolbox.StringRequest;
import com.nimbusds.jose.JOSEException;

public class SendRequstUtils {
    /**
     * 请求指定页的数据
     */
    public static void getRequestData(Context context, int page, Listener<String> listener, ErrorListener errorListener) {

        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        ReqData reqData = new ReqData();
        reqData.setC("index");
        reqData.setA("init");
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", userInfo.getUserID());
        data.put("page", String.valueOf(page));
        data.put("pagesize", String.valueOf(10));
        reqData.setData(data);

        try {
            String url = context.getString(R.string.base_url) + reqData.toReqString();
            GlobalContext.getInstance().getRequestQueue().add(
                    new StringRequest(url, listener, errorListener));

        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

}
