package com.meishai.entiy;

import java.util.List;

/**
 * 文 件 名：
 * 描    述：消息 - 赞 -返回的数据
 * 作    者：yangling
 * 时    间：2016-06-14
 * 版    权：
 */

public class MessageZanRespBean extends BaseRespData {

    /**
     * userid : 81375
     * avatar : http://img.meishai.com/avatar/9/2/81375/90x90.jpg
     * username : 雪夏_xuexia
     * title : 赞了你的笔记
     * addtime : 2016-06-12 12:11:18
     * value : 123145
     * image : http://img.meishai.com/2016/0217/20160217113255183.jpg_300x300.jpg
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
        private String addtime;
        private int value;
        private String image;

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
