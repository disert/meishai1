package com.meishai.entiy;

import com.google.gson.annotations.Expose;

/**
 * 我的福利
 *
 * @author sh
 */
public class UserWelfare {
    // 已晒
    public final static int HAS_SHARE = 1;

    // 福利ID//点晒福利的时候需要传入aid
    @Expose
    private long aid;
    // 福利id 新版
    @Expose
    private long gid;
    // 图片
    @Expose
    private String thumb;
    // 标题
    @Expose
    private String title;
    // 福利渠道
    @Expose
    private String typename;
    // 快递名称
    @Expose
    private String expressname;
    // 快递单号
    @Expose
    private String expressno;
    // isshare=1已晒；isshare=0未晒
    @Expose
    private int isshare;

    public long getAid() {
        return aid;
    }

    public void setAid(long aid) {
        this.aid = aid;
    }

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

    public String getTypename() {
        return typename;
    }

    public void setTypename(String typename) {
        this.typename = typename;
    }

    public String getExpressname() {
        return expressname;
    }

    public void setExpressname(String expressname) {
        this.expressname = expressname;
    }

    public String getExpressno() {
        return expressno;
    }

    public void setExpressno(String expressno) {
        this.expressno = expressno;
    }

    public int getIsshare() {
        return isshare;
    }

    public void setIsshare(int isshare) {
        this.isshare = isshare;
    }

    public long getGid() {
        return gid;
    }

    public void setGid(long gid) {
        this.gid = gid;
    }
}
