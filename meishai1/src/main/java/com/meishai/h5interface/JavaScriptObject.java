package com.meishai.h5interface;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.meishai.alipay.PayResult;
import com.meishai.entiy.PayInfoResponseBean;
import com.meishai.entiy.UnityOrderResponseBean;
import com.meishai.net.volley.Response;
import com.meishai.net.volley.VolleyError;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.common.req.PublicReq;
import com.meishai.util.AlipayUtils;
import com.meishai.util.ConnectServerManager;
import com.meishai.util.GsonHelper;
import com.meishai.util.WxPayUtils;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author yangling
 * @version V1.0
 * @date 2016年7月12日16:11:32
 * @Description: h5注入的接口
 */

public class JavaScriptObject {
    String tag = "JavaScriptObject";
    private Activity mContxt;
    private IWXAPI api;
    private final int HANDLER_ORDER_RESPONSE = 0;
    private final int HANDLER_ALIPAY_RESPONSE = 1;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case HANDLER_ORDER_RESPONSE:
                    Log.w(tag, msg.obj.toString());
                    parseXml(msg.obj);
                    break;
                case HANDLER_ALIPAY_RESPONSE:
                    alipayCtrl(msg);
                    break;
            }
        }
    };

    private void alipayCtrl(Message msg) {
        PayResult payResult = new PayResult((String) msg.obj);
        /**
         * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
         * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
         * docType=1) 建议商户依赖异步通知
         */
        String resultInfo = payResult.getResult();// 同步返回需要验证的信息

        String resultStatus = payResult.getResultStatus();
        // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
        if (TextUtils.equals(resultStatus, "9000")) {
            Toast.makeText(mContxt, "支付成功", Toast.LENGTH_SHORT).show();
        } else {
            // 判断resultStatus 为非"9000"则代表可能支付失败
            // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
            if (TextUtils.equals(resultStatus, "8000")) {
                Toast.makeText(mContxt, "支付结果确认中", Toast.LENGTH_SHORT).show();

            } else {
                // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                Toast.makeText(mContxt, "支付失败", Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void parseXml(Object obj) {
        if(obj != null) {
            UnityOrderResponseBean bean = WxPayUtils.readStringXmlOut(obj.toString());
            if(!TextUtils.isEmpty(bean.getReturn_code()) && "SUCCESS".equals(bean.getReturn_code())){
                //第二步,唤起支付
                PayReq request = new PayReq();
                request.appId = ConstantSet.APP_ID_WX;
                request.partnerId = ConstantSet.MCH_ID_WX;
                request.prepayId= bean.getPrepay_id();
                request.packageValue = "Sign=WXPay";
                request.nonceStr= bean.getNonce_str();
                request.timeStamp= System.currentTimeMillis()/1000  +"";
                String sign = WxPayUtils.generaSign(request,ConstantSet.MCH_KEY_WX);
                request.sign= sign;
                Toast.makeText(mContxt,"正常调起支付!",Toast.LENGTH_SHORT).show();
                api.sendReq(request);
            }else {
                Toast.makeText(mContxt,"生成订单失败:"+bean.getReturn_msg(),Toast.LENGTH_SHORT).show();
            }
        }
    }

    public JavaScriptObject(Activity mContxt) {
        this.mContxt = mContxt;
        api = WXAPIFactory.createWXAPI(mContxt,ConstantSet.APP_ID_WX);
    }

    @JavascriptInterface
    public void pay(String json) {
        Toast.makeText(mContxt, "调用本地方法:" + json, Toast.LENGTH_SHORT).show();
        PublicReq.pay(mContxt, json, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!TextUtils.isEmpty(response)) {
                    PayInfoResponseBean bean = GsonHelper.parseObject(response, PayInfoResponseBean.class);
                    if (bean != null && bean.getData() != null) {
                        //支付方式:`1 微信 , 2 支付宝
                        switch (bean.getData().getPayid()) {
                            case 1://微信
                                wxPay(bean.getData());
                                break;
                            case 2://支付宝
                                alipay(bean.getData());
                                break;
                        }
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    /**
     * 支付宝支付
     *
     * @param data
     */
    private void alipay(PayInfoResponseBean.DataBean data) {
        String orderInfo = AlipayUtils.getOrderInfo(data);

        /**
         * 特别注意，这里的签名逻辑需要放在服务端，切勿将私钥泄露在代码中！
         */
        String sign = AlipayUtils.sign(orderInfo);
        try {
            /**
             * 仅需对sign 做URL编码
             */
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        /**
         * 完整的符合支付宝参数规范的订单信息
         */
        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + AlipayUtils.getSignType();

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(mContxt);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo, true);

                Message msg = new Message();
                msg.what = HANDLER_ALIPAY_RESPONSE;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * 微信支付
     *
     * @param data
     */
    private void wxPay(PayInfoResponseBean.DataBean data) {
        //第一步:统一下单
        order(data);
    }

    /**
     * 微信支付第一步:统一下单
     *
     * @param data
     */
    private void order(PayInfoResponseBean.DataBean data) {
        Map<String, String> paramList = new HashMap<>();

        paramList.put("appid", ConstantSet.APP_ID_WX);
        paramList.put("body", data.getBody());
        paramList.put("mch_id", ConstantSet.MCH_ID_WX);
        paramList.put("nonce_str", WxPayUtils.generaNonceStr());
        paramList.put("notify_url", ConstantSet.CALL_BACK_WX);
        paramList.put("out_trade_no", data.getOut_trade_no());
        paramList.put("spbill_create_ip", WxPayUtils.getWifiLocalIPAddress(mContxt));
        int total = (int)(Double.parseDouble(data.getTotal_fee()) * 100);
        paramList.put("total_fee", total+"");
        paramList.put("trade_type", "APP");
        String sign = WxPayUtils.generaSign(paramList,ConstantSet.MCH_KEY_WX);
        paramList.put("sign", sign);

        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append("<xml>");
        Set<String> set = paramList.keySet();
        for (String key : set) {
            xmlBuilder.append("<").append(key).append(">");
            xmlBuilder.append(paramList.get(key));
            xmlBuilder.append("</").append(key).append(">");

        }
        xmlBuilder.append("</xml>");

        xmlBuilder.toString();

        Log.w(tag,xmlBuilder.toString());
        Toast.makeText(mContxt,"正在生成订单..",Toast.LENGTH_SHORT).show();
        ConnectServerManager.execute("https://api.mch.weixin.qq.com/pay/unifiedorder", xmlBuilder.toString(), mHandler, HANDLER_ORDER_RESPONSE);
    }
}
