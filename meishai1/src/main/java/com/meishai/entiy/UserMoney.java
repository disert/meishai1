package com.meishai.entiy;

import com.google.gson.annotations.Expose;

/**
 *  我的资金
 *
 * @author sh
 */
public class UserMoney {

    // 图片
    @Expose
    private String thumb;
    // 标题
    @Expose
    private String title;
    // 时间
    @Expose
    private String time;
    // 金额
    @Expose
    private String value;
    // 结算状态
    @Expose
    private String status;

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
