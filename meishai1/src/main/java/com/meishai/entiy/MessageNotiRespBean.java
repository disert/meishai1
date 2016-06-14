package com.meishai.entiy;

import java.util.List;

/**
 * 文 件 名：
 * 描    述：消息 - 通知 返回数据的封装类
 * 作    者：yangling
 * 时    间：2016-06-14
 * 版    权：
 */

public class MessageNotiRespBean extends BaseRespData {

    /**
     * userid : 1
     * avatar : http://img.meishai.com/avatar/1/1/1/90x90.jpg
     * username : 美晒天使
     * type : post
     * title : 您的笔记离精选还有一点距离哦
     * content : 亲爱的俏丽小晓，您分享的笔记如果能：
     1、
     2、
     3、
     4、
     * addtime : 2016-06-04 12:41:31
     * value : 111957
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
        private String type;
        private String title;
        private String content;
        private String addtime;
        private String value;

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

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
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

        public String getAddtime() {
            return addtime;
        }

        public void setAddtime(String addtime) {
            this.addtime = addtime;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
