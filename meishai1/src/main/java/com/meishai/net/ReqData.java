package com.meishai.net;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import java.util.Map;

import net.minidev.json.JSONObject;

import com.meishai.GlobalContext;
import com.meishai.util.AndroidUtil;
import com.meishai.util.Arithmetic;
import com.meishai.util.Constant;
import com.meishai.util.DebugLog;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;

/**
 * 请求数据封装
 *
 * @author sh
 */
public class ReqData {

    private String c;
    private String a;
    private String f = "android";
    private String token = Arithmetic.getMD5Str("memberloginandroid");
    private String m;
    private String v;
    private String w;//是否是WiFi


    private Map<String, String> data;

    public ReqData() {
        Context context = GlobalContext.getInstance();
        try {
            //版本号
            v = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            ApplicationInfo appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            //网络状态
            ConnectivityManager connectMgr = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = connectMgr.getActiveNetworkInfo();
            if (info != null) {
                if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                    w = "1";
                } else {
                    w = "0";
                }
            } else {
                w = "0";
            }
            //渠道id
            m = appInfo.metaData.getString("com.meishai.channelid");
//			AndroidUtil.showToast("渠道id:"+m);
        } catch (PackageManager.NameNotFoundException e) {
            DebugLog.w("获取版本号失败");
            e.printStackTrace();
        }
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public String getF() {
        return f;
    }

    public String getToken() {
        return token;
    }

    public String getM() {
        return m;
    }

    public void setM(String m) {
        this.m = m;
    }

    public String getV() {
        return v;
    }

    public String getW() {
        return w;
    }

    public void setW(String w) {
        this.w = w;
    }

    public void setV(String v) {
        this.v = v;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

    public static JWTClaimsSet getReqClaimsSet(ReqData reqData) {
        JWTClaimsSet ct = new JWTClaimsSet();
        ct.setCustomClaim("c", reqData.getC());
        ct.setCustomClaim("a", reqData.getA());
        ct.setCustomClaim("f", reqData.getF());
        ct.setCustomClaim("m", reqData.getM());
        ct.setCustomClaim("v", reqData.getV());
        ct.setCustomClaim("w", reqData.getW());
        ct.setCustomClaim("token", reqData.getToken());
        if (null != reqData.getData() && !reqData.getData().isEmpty()) {
            JSONObject dataObj = new JSONObject(reqData.getData());
            ct.setCustomClaim("data", dataObj);
        }
        return ct;
    }

    public String toReqString() throws JOSEException {
        JWTClaimsSet ct = getReqClaimsSet(this);
        Payload payload = new Payload(ct.toJSONObject());
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
        JWSObject obj = new JWSObject(header, payload);
        JWSSigner signer = new MACSigner(Constant.KEY);
        obj.sign(signer);
        return obj.serialize();
    }
}
