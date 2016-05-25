package com.meishai.entiy;

import com.google.gson.annotations.Expose;

public class UserFans {
    // 会员ID
    @Expose
    private long userid;
    // 会员头像
    @Expose
    private String avatar;
    // 会员名称
    @Expose
    private String username;
    // 帖子数量
    @Expose
    private Integer post_num = 0;
    // 粉丝数量
    @Expose
    private Integer fans_num = 0;
    // 赞数量
    @Expose
    private Integer zan_num = 0;
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

    public Integer getPost_num() {
        if (null == post_num) {
            post_num = 0;
        }
        return post_num;
    }

    public void setPost_num(Integer post_num) {
        this.post_num = post_num;
    }

    public Integer getFans_num() {
        if (null == fans_num) {
            fans_num = 0;
        }
        return fans_num;
    }

    public void setFans_num(Integer fans_num) {
        this.fans_num = fans_num;
    }

    public Integer getZan_num() {
        if (null == zan_num) {
            zan_num = 0;
        }
        return zan_num;
    }

    public void setZan_num(Integer zan_num) {
        this.zan_num = zan_num;
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
