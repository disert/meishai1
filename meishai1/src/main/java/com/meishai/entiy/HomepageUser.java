package com.meishai.entiy;

import com.google.gson.annotations.Expose;

/**
 * 个人主页头部信息
 *
 * @author sh
 */
public class HomepageUser {

    // 会员ID
    @Expose
    private long userid;
    // 会员头像
    @Expose
    private String avatar;
    // 会员名称
    @Expose
    private String username;
    // 当前页面会员介绍
    @Expose
    private String intro;
    // 当前登陆会员是否关注了该会员，isattention=1已关注，isattention=0未关注
    @Expose
    private Integer isattention = 1;

    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public Integer getIsattention() {
        if (null == isattention) {
            isattention = 1;
        }
        return isattention;
    }

    public void setIsattention(Integer isattention) {
        this.isattention = isattention;
    }

}
