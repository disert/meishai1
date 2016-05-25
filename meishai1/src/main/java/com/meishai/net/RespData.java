package com.meishai.net;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.meishai.entiy.Area;
import com.meishai.entiy.ShareData;
import com.meishai.entiy.TryInfoMore;
import com.meishai.entiy.UserWang;

/**
 * 返回的数据封装
 *
 * @author sh
 */
public class RespData {

    // 未登录
    public static final int RESP_STATE_NO_LOGIN = -1;

    // 成功
    public static final int RESP_STATE_SUCCESS = 1;
    // 失败
    public static final int RESP_STATE_FAIL = 0;

    // 返回状态
    @Expose
    private Integer success;
    // 提示消息 
    @Expose
    private String tips;
    // 我的信用 使用
    @Expose
    private String credit;

    // 我的资金 使用
    // 帐户余额
    @Expose
    private String amount;
    // 我的资金 使用
    // 支付账号
    @Expose
    private String alipay;

    // 签到使用
    // 今天签到积分值
    @Expose
    private Integer today_point;
    // 明天签到积分值
    @Expose
    private Integer tomorrow_point;
    // 我的积分
    @Expose
    private Integer mypoint;
    // 签到规则文字
    @Expose
    private String text;

    // 个人主页使用
    @Expose
    private Object userinfo = null;

    @Expose
    private Object topics = null;

    // 地区数据
    @Expose
    private List<Area> area = null;

    // 试用新首页试用
    @Expose
    private TryInfoMore more;

    //分享的数据
    @Expose
    private ShareData share;

    //是否分享数据
    @Expose
    private Integer isshare;

    // 返回数据
    @Expose
    private Object data;

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public TryInfoMore getMore() {
        return more;
    }

    public void setMore(TryInfoMore more) {
        this.more = more;
    }

    public boolean isSuccess() {
        return this.success.intValue() == RESP_STATE_SUCCESS;
    }

    public boolean isLogin() {
        return this.success.intValue() == RESP_STATE_NO_LOGIN;
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAlipay() {
        return alipay;
    }

    public void setAlipay(String alipay) {
        this.alipay = alipay;
    }

    public Integer getToday_point() {
        return today_point;
    }

    public void setToday_point(Integer today_point) {
        this.today_point = today_point;
    }

    public Integer getTomorrow_point() {
        return tomorrow_point;
    }

    public void setTomorrow_point(Integer tomorrow_point) {
        this.tomorrow_point = tomorrow_point;
    }

    public Integer getMypoint() {
        return mypoint;
    }

    public void setMypoint(Integer mypoint) {
        this.mypoint = mypoint;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Object getUserinfo() {
        return userinfo;
    }

    public void setUserinfo(Object userinfo) {
        this.userinfo = userinfo;
    }

    public Object getTopics() {
        return topics;
    }

    public void setTopics(Object topics) {
        this.topics = topics;
    }

    public List<Area> getArea() {
        return area;
    }

    public void setArea(List<Area> area) {
        this.area = area;
    }

    public ShareData getShare() {
        return share;
    }

    public void setShare(ShareData share) {
        this.share = share;
    }

    public Integer getIsshare() {
        return isshare;
    }

    public void setIsshare(Integer isshare) {
        this.isshare = isshare;
    }

    @Override
    public String toString() {
        return "RespData [success=" + success + ", tips=" + tips + ", credit="
                + credit + ", amount=" + amount + ", alipay=" + alipay
                + ", today_point=" + today_point + ", tomorrow_point="
                + tomorrow_point + ", mypoint=" + mypoint + ", text=" + text
                + ", userinfo=" + userinfo + ", topics=" + topics + ", area="
                + area + ", more=" + more + ", share=" + share + ", isshare="
                + isshare + ", data=" + data + "]";
    }

}
