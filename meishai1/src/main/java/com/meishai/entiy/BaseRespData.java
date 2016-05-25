package com.meishai.entiy;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 请求数据包装类的基类
 *
 * @author Administrator
 */
public class BaseRespData {
    public int page;
    public int pages;
    public int pagesize;
    public int success;
    public String page_title;
    public String tips;
    public int total;


}
