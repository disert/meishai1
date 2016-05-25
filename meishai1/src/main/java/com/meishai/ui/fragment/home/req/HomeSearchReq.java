package com.meishai.ui.fragment.home.req;

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
 * 晒晒搜索
 *
 * @author sh
 */
public class HomeSearchReq {

    // 帖子搜索
    public static final String SEARCH_POST = "1";
    // 话题搜索
    public static final String SEARCH_TOPIC = "2";
    // 会员搜索
    public static final String SEARCH_USER = "3";

    /**
     * 搜索类型，type=1:帖子搜索；type=2:话题搜索；type=3:会员搜索
     *
     * @param context
     * @param data
     * @param listener
     * @param errorListener
     */
    public static void search(Context context, Map<String, String> data,
                              Listener<RespData> listener, ErrorListener errorListener) {
        ReqData reqData = new ReqData();
        reqData.setC("post");
        reqData.setA("search");
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
