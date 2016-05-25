package com.meishai.entiy;

import com.google.gson.annotations.Expose;

/**
 *  我的积分
 *
 * @author sh
 */
public class UserPoint {
    // 时间
    @Expose
    private String time;
    // 标题
    @Expose
    private String title;
    // 信用值
    @Expose
    private String value;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
