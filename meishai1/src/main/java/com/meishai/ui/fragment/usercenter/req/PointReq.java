package com.meishai.ui.fragment.usercenter.req;

import java.util.Map;

import android.content.Context;

import com.meishai.R;
import com.meishai.net.MeishaiRequest;
import com.meishai.net.ReqData;
import com.meishai.net.RespData;
import com.meishai.net.VolleyHelper;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.ui.constant.ConstantSet;
import com.nimbusds.jose.JOSEException;

/**
 * 我的积分
 *
 * @author sh
 */
public class PointReq {

    /**
     * 我的积分
     *
     * @param context
     * @param data
     * @param listener
     * @param errorListener
     */
    public static void point(Context context, Map<String, String> data,
                             Listener<RespData> listener, ErrorListener errorListener) {
        ReqData reqData = new ReqData();
        reqData.setC(ConstantSet.C_MEMBER);
        reqData.setA("point");
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
}
