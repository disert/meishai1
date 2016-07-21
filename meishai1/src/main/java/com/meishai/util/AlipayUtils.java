package com.meishai.util;

import com.meishai.alipay.SignUtils;
import com.meishai.entiy.PayInfoResponseBean;

/**
 * 文 件 名：
 * 描    述：支付宝支付的工具类
 * 作    者：yangling
 * 时    间：2016-07-15
 * 版    权：
 */

public class AlipayUtils {
    private static final String PARTNER = "2088901047592814";
    private static final String SELLER = "meishaibd@gmail.com";
    // 商户私钥，pkcs8格式
    public static final String RSA_PRIVATE = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAL5ZdRRQxpf87JIqTf9OTKRv4sX8Op1yNalCrGtaNvnTTLqJlmW3PdhnOv36Zk7MDDpp+/+0e0EpHA2lXLlJ8/goVD+kAv6X2DWaEyCzHwftb24N9dwwoG6eFrAntufMy5tSiuqLZA2HF/Z4DLKtCWfiXVy6GhIN5v7pP7StsH7vAgMBAAECgYA3TEe+CiVXfCvB3rZgCAAt+S8ZM0hX+xjpvivW2BOGikMNIfFmRVCbMIorxHW6SH747Vc/FtCDwoyazkJctcZA/iDyrFiywPr9dZrj1FHK7MJg9rm6FMMEFCMWTghIryXSePBvy6ht2AGnRBcS25o08jysY5yAmub5bToS+JLjiQJBAOXeMh43ClTgJVuKly5vvGLmDq1CqjC6bSEB7MPhWVrNFxjp+KjAmrzcHOwIQsMAwB1gEXc10xBaOBxWEsW/JasCQQDT/Sln5quQHNUkBsNfvW/kVsPmNdcOolID4Dkz9U5zFwiop3u01ozbJ92A6ANJonUt6hI0qsZK/dcbmxdZTv/NAkA2dOteqR9zeB6hjzW9tI7mxyXW+iIjpSbRWQlkZnCqyEeeiV91axu9flADwvk+BWF+FBJPMZstFpKQc/vCiUQ9AkEAuJTuwLFxIQJ58+yPoYVpMAD8gi7PGsbrcsSCCmPF8yBTQcbTSBqoewqzK9LZSfxS26+8Om+YSSPNcRQRuIm44QJAJKTElKwi8nEPRoi89RfN2FtTUMp7J0Sv3FT9Wvwss66vxaVjBW+kIiTSWhk6ZxenxC06iN3TrmS4ih8ticAXAQ==";
    // 支付宝公钥
    public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC+WXUUUMaX/OySKk3/Tkykb+LF/DqdcjWpQqxrWjb500y6iZZltz3YZzr9+mZOzAw6afv/tHtBKRwNpVy5SfP4KFQ/pAL+l9g1mhMgsx8H7W9uDfXcMKBunhawJ7bnzMubUorqi2QNhxf2eAyyrQln4l1cuhoSDeb+6T+0rbB+7wIDAQAB";


    /**
     * create the order info. 创建订单信息
     */
    public static String getOrderInfo(PayInfoResponseBean.DataBean data) {

        StringBuilder orderInfo = new StringBuilder();
        // 签约合作者身份ID
        orderInfo.append("partner=\"").append(PARTNER).append("\"")

                // 签约卖家支付宝账号
                .append("&seller_id=\"").append(SELLER).append("\"")

                // 商户网站唯一订单号
                .append("&out_trade_no=\"").append(data.getOut_trade_no()).append("\"")

                // 商品名称
                .append("&subject=\"").append(data.getBody()).append("\"")

                // 商品详情
                .append("&body=\"").append(data.getBody()).append("\"")

                // 商品金额
                .append("&total_fee=\"").append(data.getTotal_fee()).append("\"")

                // 服务器异步通知页面路径
                .append("&notify_url=\"http://notify.msp.hk/notify.htm\"")

                        // 服务接口名称， 固定值
                .append("&service=\"mobile.securitypay.pay\"")

                        // 支付类型， 固定值
                .append("&payment_type=\"1\"")

                        // 参数编码， 固定值
                .append("&_input_charset=\"utf-8\"")

                        // 设置未付款交易的超时时间
                        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
                        // 取值范围：1m～15d。
                        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
                        // 该参数数值不接受小数点，如1.5h，可转换为90m。
                .append("&it_b_pay=\"30m\"")

                        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
                        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

                        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
                .append("&return_url=\"m.alipay.com\"");

        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";

        return orderInfo.toString();
    }
    /**
     * sign the order info. 对订单信息进行签名
     *
     * @param content
     *            待签名订单信息
     */
    public static String sign(String content) {
        return SignUtils.sign(content, RSA_PRIVATE);
    }

    /**
     * get the sign type we use. 获取签名方式
     *
     */
    public static String getSignType() {
        return "sign_type=\"RSA\"";
    }
}
