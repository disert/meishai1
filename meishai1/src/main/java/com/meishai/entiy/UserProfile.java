package com.meishai.entiy;

import java.util.List;

import com.google.gson.annotations.Expose;

/**
 * 完善个人资料
 *
 * @author sh
 */
public class UserProfile {
    // 试用ID
    @Expose
    private long sid;
    // 会员ID
    @Expose
    private String userid;
    // 地区ID (数据存在则不显示)
    @Expose
    private Integer areaid;
    // 手机号码 (数据存在则不显示)
    @Expose
    private String phone;
    // QQ号码 (数据存在则不显示)
    @Expose
    private String qq;

    // 试用 完善个人资料
    @Expose
    private List<UserWang> wangs = null;

    public long getSid() {
        return sid;
    }

    public void setSid(long sid) {
        this.sid = sid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public Integer getAreaid() {
        return areaid;
    }

    public void setAreaid(Integer areaid) {
        this.areaid = areaid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public List<UserWang> getWangs() {
        return wangs;
    }

    public void setWangs(List<UserWang> wangs) {
        this.wangs = wangs;
    }

}
