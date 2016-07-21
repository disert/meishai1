package com.meishai.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import com.meishai.entiy.UnityOrderResponseBean;
import com.meishai.wxpay.MD5;
import com.tencent.mm.sdk.modelpay.PayReq;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * @author yangling
 * @version V1.0
 * @date 2016年7月14日17:59:44
 * @Description: 微信支付的工具类
 */

public class WxPayUtils {
    /**
     * 生成nonce_str字段需要的随机数
     * @return
     */
    public static String generaNonceStr() {
        Random randomNum = new Random();
        int theNum = randomNum.nextInt();
        return MD5.md5(theNum + "");
    }

    /**
     * 生成签名算法  -- 统一下单接口有用到
     *
     * @param paramList
     * @param xkey
     * @return
     */
    public static String generaSign(Map<String, String> paramList, String xkey) {
        Set<String> keys = paramList.keySet();
        Object[] objects = keys.toArray();
        Arrays.sort(objects);
        StringBuilder stringA = new StringBuilder();
        for ( Object key : objects) {
            String value = paramList.get(key);
            if(!TextUtils.isEmpty(value)){
                stringA.append(key.toString()).append("=").append(value).append("&");
            }
        }
        //        stringA.deleteCharAt(stringA.length()-1);
        stringA.append("key=").append(xkey);
        Log.w("","string:"+stringA.toString());
        String sign = MD5.md5(stringA.toString()).toUpperCase();
        Log.w("","sign:"+sign);
        return sign;
    }
    public static String generaSign(PayReq request, String xkey) {
        StringBuilder stringA = new StringBuilder();
        stringA.append("appid=").append(request.appId).append("&")
                .append("noncestr=").append(request.nonceStr).append("&")
                .append("package=").append(request.packageValue).append("&")
                .append("partnerid=").append(request.partnerId).append("&")
                .append("prepayid=").append(request.prepayId).append("&")
                .append("timestamp=").append(request.timeStamp).append("&")
                .append("key=").append(xkey);
        Log.w("","string:"+stringA.toString());
        String sign = MD5.md5(stringA.toString()).toUpperCase();
        Log.w("","sign:"+sign);
        return sign;
    }


    /**
     * 获取IP地址 -- 统一下单接口所需要的参数
     * @param context
     * @return
     */
    public static String getWifiLocalIPAddress(Context context) {
        try {
            ConnectivityManager connectMgr = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo info = connectMgr.getActiveNetworkInfo();
            if(info.getType() == ConnectivityManager.TYPE_WIFI){
                //获取wifi服务
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                //判断wifi是否开启
                if (!wifiManager.isWifiEnabled()) {
                    wifiManager.setWifiEnabled(true);
                }
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                int ipAddress = wifiInfo.getIpAddress();
                return intToIp(ipAddress);
            }else if(info.getType() == ConnectivityManager.TYPE_MOBILE) {
                for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                    NetworkInterface intf = en.nextElement();
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress()) {
                            return inetAddress.getHostAddress();
                        }
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
            //            Log.e("WifiPreference IpAddress", ex.toString());
        }
        return null;
    }

    private static String intToIp(int i) {

        return (i & 0xFF ) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ((i >> 16 ) & 0xFF) + "." +
                ( i >> 24 & 0xFF) ;
    }


    public static void xmlParser(String xml){

        PayReq request = new PayReq();
        request.appId = "wxd930ea5d5a258f4f";
        request.partnerId = "1900000109";
        request.prepayId= "1101000000140415649af9fc314aa427";
        request.packageValue = "Sign=WXPay";
        request.nonceStr= "1101000000140429eb40476f8896f4c9";
        request.timeStamp= "1398746574";
        request.sign= "7FFECB600D7157C5AA49810D2D8F28BC2811827B";
//        api.sendReq(req);
    }

    /**
     * @description 将xml字符串转换成map
     * @param xml
     * @return Map
     */
    public static UnityOrderResponseBean readStringXmlOut(String xml) {
        UnityOrderResponseBean bean = new UnityOrderResponseBean();
        Document doc = null;
        try {
            doc = DocumentHelper.parseText(xml); // 将字符串转为XML
            Element rootElt = doc.getRootElement(); // 获取根节点
            @SuppressWarnings("unchecked")
            List<Element> list = rootElt.elements();// 获取根节点下所有节点

            for (Element element : list) { // 遍历节点
                switch (element.getName()){
                    case "return_code":
                        bean.setReturn_code(element.getText());
                        break;
                    case "return_msg":
                        bean.setReturn_msg(element.getText());
                        break;
                    case "appid":
                        bean.setAppid(element.getText());
                        break;
                    case "mch_id":
                        bean.setMch_id(element.getText());
                        break;
                    case "nonce_str":
                        bean.setNonce_str(element.getText());
                        break;
                    case "sign":
                        bean.setSign(element.getText());
                        break;
                    case "result_code":
                        bean.setResult_code(element.getText());
                        break;
                    case "trade_type":
                        bean.setTrade_type(element.getText());
                        break;
                    case "prepay_id":
                        bean.setPrepay_id(element.getText());
                        break;
                }
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }
}
