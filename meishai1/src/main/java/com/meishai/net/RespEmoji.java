package com.meishai.net;

import com.google.gson.annotations.Expose;

/**
 * 返回的数据封装
 *
 * @author sh
 */
public class RespEmoji {

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

    // 返回数据
    @Expose
    private Object area;

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

    public boolean isSuccess() {
        return this.success.intValue() == RESP_STATE_SUCCESS;
    }

    public boolean isLogin() {
        return this.success.intValue() == RESP_STATE_NO_LOGIN;
    }

    public Object getArea() {
        return area;
    }

    public void setArea(Object area) {
        this.area = area;
    }

}
