package com.meishai.entiy;

import java.util.List;

/**
 * 文 件 名：
 * 描    述：消息 - 粉丝 返回数据的封装类
 * 作    者：yangling
 * 时    间：2016-06-14
 * 版    权：
 */

public class MessageFansRespBean extends BaseRespData {

    /**
     * userid : 82208
     * avatar : http://img.meishai.com/avatar/9/3/82208/90x90.jpg
     * username : 遥远的未来
     * title : 关注了你
     * fans : 0笔记，0粉丝
     * isattention : 0
     */

    private List<ListBean> list;

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        private String userid;
        private String avatar;
        private String username;
        private String title;
        private String fans;
        private int isattention;

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

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getFans() {
            return fans;
        }

        public void setFans(String fans) {
            this.fans = fans;
        }

        public int getIsattention() {
            return isattention;
        }

        public void setIsattention(int isattention) {
            this.isattention = isattention;
        }
    }
}
