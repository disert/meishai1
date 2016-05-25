package com.meishai.app.util;

public class DateUtil {

    // 将秒数换算成x天x时x分x秒x毫秒
    public static String timeFormat(long s) {
        // long ms = s * 1000;
        long ms = s;
        int ss = 1000;
        int mi = ss * 60;
        int hh = mi * 60;
        int dd = hh * 24;

        long day = ms / dd;
        long hour = (ms - day * dd) / hh;
        long minute = (ms - day * dd - hour * hh) / mi;
        long second = (ms - day * dd - hour * hh - minute * mi) / ss;

        String strDay = day + "天";
        String strHour = hour + "小时";
        String strMinute = minute + "分";
        String strSecond = second + "秒";
        return strDay + strHour + strMinute + strSecond;
    }
}
