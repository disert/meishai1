package com.meishai.entiy;

import com.google.gson.annotations.Expose;

public class PointExchangeDetail {

    // 商品ID
    @Expose
    private long id;
    // 商品图片
    @Expose
    private String thumb;

    // isdaren=1达人专享 显示达人专享标志
    @Expose
    private int isdaren = 0;
    // 商品标题
    @Expose
    private String title;
    // 所需最低积分
    @Expose
    private int lowpoint;
    // 限量
    @Expose
    private int allnum;
    // 剩余数量
    @Expose
    private int lastnum;
    // 结束时间
    @Expose
    private long endtime;
    // 兑换类型 type=1：我要兑换；type=2：我要抽奖；type=3：我要竞拍
    @Expose
    private int type;
    // 文字详情
    @Expose
    private String content;
    // 按钮显示文字
    @Expose
    private String buttontext;
    // 分享用到的数据
    @Expose
    private ShareData sharedata;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public int getIsdaren() {
        return isdaren;
    }

    public void setIsdaren(int isdaren) {
        this.isdaren = isdaren;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getLowpoint() {
        return lowpoint;
    }

    public void setLowpoint(int lowpoint) {
        this.lowpoint = lowpoint;
    }

    public int getAllnum() {
        return allnum;
    }

    public void setAllnum(int allnum) {
        this.allnum = allnum;
    }

    public int getLastnum() {
        return lastnum;
    }

    public void setLastnum(int lastnum) {
        this.lastnum = lastnum;
    }

    public long getEndtime() {
        return endtime;
    }

    public void setEndtime(long endtime) {
        this.endtime = endtime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getButtontext() {
        return buttontext;
    }

    public void setButtontext(String buttontext) {
        this.buttontext = buttontext;
    }

    public ShareData getSharedata() {
        return sharedata;
    }

    public void setSharedata(ShareData sharedata) {
        this.sharedata = sharedata;
    }

}
