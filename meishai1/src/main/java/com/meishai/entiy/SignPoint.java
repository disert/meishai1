package com.meishai.entiy;

import com.google.gson.annotations.Expose;

public class SignPoint {

    // 1表示当天
    public final static int TODAY = 1;
    // 星期 0代表星期日
    @Expose
    private int week;
    // 日期
    @Expose
    private int day;
    // 是否今天，今天的话积分背景是红色，非今天的话积分背景为绿色
    @Expose
    private int istoday;
    // 积分数值
    @Expose
    private int point;

    // 是否有数据 1 表示有数据 0 表示无数据 默认1
    private int hasData = 1;

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getIstoday() {
        return istoday;
    }

    public void setIstoday(int istoday) {
        this.istoday = istoday;
    }

    public int getHasData() {
        return hasData;
    }

    public void setHasData(int hasData) {
        this.hasData = hasData;
    }

    /**
     * 返回true 表示有数据
     *
     * @return
     */
    public boolean isHasData() {
        return this.hasData == 1;
    }

    public static int getToday() {
        return TODAY;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public static SignPoint getNullPoint() {
        SignPoint point = new SignPoint();
        point.setHasData(0);
        return point;
    }

}
