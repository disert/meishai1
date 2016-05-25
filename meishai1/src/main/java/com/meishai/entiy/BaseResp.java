package com.meishai.entiy;

import java.util.List;

/**
 * 文件名：
 * 描    述：
 * 作    者：
 * 时    间：2016/2/19
 * 版    权：
 */
public class BaseResp<T> {

    public int page;
    public int pages;
    public int pagesize;
    public int success;
    public String tips;
    public String page_title;//	更多达人
    public int total;
    public List<T> list;
}
