package com.meishai.ui.fragment.message;

import android.content.Context;

import com.meishai.R;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.UserInfo;
import com.meishai.net.ReqData;
import com.meishai.net.VolleyHelper;
import com.meishai.net.volley.Response;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.StringRequest;
import com.meishai.ui.fragment.usercenter.LoginActivity;
import com.meishai.util.DebugLog;
import com.meishai.util.ToastUtlis;
import com.nimbusds.jose.JOSEException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/6/9.
 */
public class MessageReq {
    public static void checkBuy(final Context context,String gid,String type,String groupid,Response.Listener<String> listener){
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        if(!userInfo.isLogin()){
            context.startActivity(LoginActivity.newIntent());
            return;
        }
        ReqData reqData = new ReqData();
        reqData.setC("mall");
        reqData.setA("buycall");
        Map<String, String> data = new HashMap<String, String>();
        data.put("gid", gid);
        data.put("type", type);
        data.put("groupid", groupid);
        data.put("userid", userInfo.getUserID());
        reqData.setData(data);

        try {
            String url = context.getString(R.string.base_url) + reqData.toReqString();
            DebugLog.d(url);
            VolleyHelper.getRequestQueue(context).add(new StringRequest(url, listener, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    ToastUtlis.showToast(context, "数据加载失败");
                }
            }));

        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }
}
