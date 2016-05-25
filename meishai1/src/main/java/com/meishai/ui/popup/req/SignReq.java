package com.meishai.ui.popup.req;

import java.util.Map;

import android.content.Context;

import com.meishai.R;
import com.meishai.net.MeishaiRequest;
import com.meishai.net.ReqData;
import com.meishai.net.RespData;
import com.meishai.net.VolleyHelper;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.nimbusds.jose.JOSEException;

/**
 * 签到
 *
 * @author sh
 */
public class SignReq {
    /**
     * 签到
     *
     * @param context
     * @param data
     * @param listener
     * @param errorListener
     */
    public static void sign(Context context, Map<String, String> data,
                            Listener<RespData> listener, ErrorListener errorListener) {
        ReqData reqData = new ReqData();
        reqData.setC("public");
        reqData.setA("sign");
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
