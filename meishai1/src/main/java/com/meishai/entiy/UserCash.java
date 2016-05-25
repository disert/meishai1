package com.meishai.entiy;

import com.google.gson.annotations.Expose;

public class UserCash {

    // 帐户余额
    @Expose
    private String amount;
    // 手续费
    @Expose
    private String fee;
    // 手机号码
    @Expose
    private String phone;
    // 支付账号
    @Expose
    private String alipay;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAlipay() {
        return alipay;
    }

    public void setAlipay(String alipay) {
        this.alipay = alipay;
    }

}
