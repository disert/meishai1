package com.meishai.entiy;

import java.util.List;

/**
 * 个人主页返回的信息 2.0
 *
 * @author Administrator yl
 */
public class HomePageDatas {
    public List<PostInfo> list;
    public int page;
    public int pages;
    public int pagesize;
    public ShareData sharedata;
    public int success;
    public String tips;
    public int total;
    public UserInfo userinfo;

    public class UserInfo {
        public String areaname;
        public String avatar;
        public int fans_num;
        public int follow_num;
        public String intro;
        public int isattention;
        public int isdaren;
        public int userid;
        public String username;

        public String group_bgcolor;//	4BD7FF
        public String group_name;//	V2会员
        public int post_num;//	65
        public String text;//	加入美晒132天，共收获了8978676个赞，这里显示一行，文字颜色为#999999
    }

    public class PostInfo {
        public String avatar;
        public String description;
        public int isdaren;
        public int iszan;
        public String pic_url;
        public String image;
        public int pid;
        public int userid;
        public String username;
        public int view_num;
        public int zan_num;

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            PostInfo postInfo = (PostInfo) o;

            if (isdaren != postInfo.isdaren)
                return false;
            if (iszan != postInfo.iszan)
                return false;
            if (pid != postInfo.pid)
                return false;
            if (userid != postInfo.userid)
                return false;
            if (view_num != postInfo.view_num)
                return false;
            if (zan_num != postInfo.zan_num)
                return false;
            if (!avatar.equals(postInfo.avatar))
                return false;
            if (!description.equals(postInfo.description))
                return false;
            if (!pic_url.equals(postInfo.pic_url))
                return false;
            if (!image.equals(postInfo.image))
                return false;
            return username.equals(postInfo.username);

        }

        @Override
        public int hashCode() {
            int result = avatar.hashCode();
            result = 31 * result + description.hashCode();
            result = 31 * result + isdaren;
            result = 31 * result + iszan;
            result = 31 * result + pic_url.hashCode();
            result = 31 * result + image.hashCode();
            result = 31 * result + pid;
            result = 31 * result + userid;
            result = 31 * result + username.hashCode();
            result = 31 * result + view_num;
            result = 31 * result + zan_num;
            return result;
        }
    }
}
