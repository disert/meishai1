package com.meishai.entiy;

import java.util.List;

/**
 * 文 件 名：
 * 描    述：消息 - 评论 返回数据的封装类
 * 作    者：yangling
 * 时    间：2016-06-14
 * 版    权：
 */

public class MessageComRespBean extends BaseRespData {

    /**
     * userid : 82208
     * avatar : http://img.meishai.com/avatar/9/3/82208/90x90.jpg
     * username : 遥远的未来
     * title : 回复
     * content : 很不错，我也喜欢哦
     * reply : 我的评论：喜欢💓赫拉气垫BB霜
     * addtime : 2016-06-04 18:02:37
     * value : 135126
     * image : http://img.meishai.com/2016/0604/20160604051412626.jpg_300x300.jpg
     */

    private List<ListBean> list;

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        private int userid;
        private String avatar;
        private String username;
        private String title;
        private String content;
        private String reply;
        private String addtime;
        private int value;
        private String image;

        public int getUserid() {
            return userid;
        }

        public void setUserid(int userid) {
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

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getReply() {
            return reply;
        }

        public void setReply(String reply) {
            this.reply = reply;
        }

        public String getAddtime() {
            return addtime;
        }

        public void setAddtime(String addtime) {
            this.addtime = addtime;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }
}
