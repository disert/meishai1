package com.meishai.entiy;

import java.util.HashMap;
import java.util.Map;

/**
 * 第三方登录临时对象
 *
 * @author sh
 */
public class OpenLogin {
    @Override
    public String toString() {
        return "OpenLogin [connectid=" + connectid + ", nick=" + nick
                + ", token=" + token + ", avatar=" + avatar + ", unionid="
                + unionid + "]";
    }

    // 登陆获取的connectid
    private String connectid;
    // 登陆获取的昵称
    private String nick;
    // 登陆获取的TOKEN
    private String token;
    // 登陆获取的会员头像
    private String avatar;
    //unionid
    private String unionid;

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public String getConnectid() {
        return connectid;
    }

    public void setConnectid(String connectid) {
        this.connectid = connectid;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("connectid", connectid);
        map.put("nick", nick);
        map.put("token", token);
        map.put("avatar", avatar);
        if (unionid != null) {
            map.put("unionid", unionid);
        }
        return map;
    }

}
