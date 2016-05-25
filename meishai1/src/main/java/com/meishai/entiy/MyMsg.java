package com.meishai.entiy;

import com.google.gson.annotations.Expose;

/**
 * 我的消息、我的私信、试用提醒
 *
 * @author sh
 */
public class MyMsg {

    public enum MsgTableId {
        TRIAL("2", "试用提醒"), PRI_LETTER("1", "我的私信"), MSG("3", "我的消息");
        private String type;
        private String remark;

        private MsgTableId(String type, String remark) {
            this.type = type;
            this.remark = remark;
        }

        public String getType() {
            return type;
        }

        public String getRemark() {
            return remark;
        }

    }

    @Expose
    private String userid;
    // 会员头像
    @Expose
    private String avatar;
    // 会员昵称
    @Expose
    private String username;
    // 时间
    @Expose
    private String time;
    // 消息内容
    @Expose
    private String content;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
