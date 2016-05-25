package com.meishai.entiy;

import com.google.gson.annotations.Expose;

/**
 * 达人
 *
 * @author sh
 */
public class Master {

    // 已关注
    public static final int HAS_ATENTION = 1;
    // 未关注
    public static final int NO_ATENTION = 0;
    @Expose
    private long userid;
    @Expose
    private String username;
    @Expose
    private String avatar;
    @Expose
    private long areaid;
    @Expose
    private String areaname;
    @Expose
    private String intro;
    @Expose
    private int post_num;
    @Expose
    private int fans_num;
    @Expose
    private int zan_num;
    @Expose
    private int isattention;

    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public long getAreaid() {
        return areaid;
    }

    public void setAreaid(long areaid) {
        this.areaid = areaid;
    }

    public String getAreaname() {
        return areaname;
    }

    public void setAreaname(String areaname) {
        this.areaname = areaname;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public int getPost_num() {
        return post_num;
    }

    public void setPost_num(int post_num) {
        this.post_num = post_num;
    }

    public int getFans_num() {
        return fans_num;
    }

    public void setFans_num(int fans_num) {
        this.fans_num = fans_num;
    }

    public int getZan_num() {
        return zan_num;
    }

    public void setZan_num(int zan_num) {
        this.zan_num = zan_num;
    }

    public int getIsattention() {
        return isattention;
    }

    public void setIsattention(int isattention) {
        this.isattention = isattention;
    }

}
