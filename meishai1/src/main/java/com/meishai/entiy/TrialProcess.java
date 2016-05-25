package com.meishai.entiy;

import com.google.gson.annotations.Expose;

public class TrialProcess {
    // 时间
    @Expose
    private String time;
    // 文字
    @Expose
    private String text;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
