package com.meishai.entiy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.meishai.entiy.PostItem.ZanUserInfo;

public class ItemInfo implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -5153081149803790643L;

    public int isurl;
    public int pid;
    public ItemData postdata;
    public List<TopicInfo> topicdata;
    public String url;
    public UserData userdata;
    public ArrayList<ZanUserInfo> zuserdata;


    public class ItemData implements Serializable {
        /**
         *
         */
        private static final long serialVersionUID = -3167208862271214060L;
        public String addtime;
        public String areaname;
        public int com_num;
        public String description;
        public int iszan;
        public int pic_height;
        public String pic_url;
        public int pic_width;
        public int view_num;
        public int zan_num;
        public int isfav;//: 1, //是否收藏
        public int islink;//: 0, //是否有链接（根据这个值进行判断）
        public int istao;//: 1, //是否打开淘宝APP
        public String link;//: "", //跳转的链接
        public int fav_num;// 收藏数值;

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            ItemData itemData = (ItemData) o;

            if (com_num != itemData.com_num)
                return false;
            if (iszan != itemData.iszan)
                return false;
            if (pic_height != itemData.pic_height)
                return false;
            if (pic_width != itemData.pic_width)
                return false;
            if (view_num != itemData.view_num)
                return false;
            if (zan_num != itemData.zan_num)
                return false;
            if (isfav != itemData.isfav)
                return false;
            if (islink != itemData.islink)
                return false;
            if (istao != itemData.istao)
                return false;
            if (fav_num != itemData.fav_num)
                return false;
            if (!addtime.equals(itemData.addtime))
                return false;
            if (!areaname.equals(itemData.areaname))
                return false;
            if (!description.equals(itemData.description))
                return false;
            if (!pic_url.equals(itemData.pic_url))
                return false;
            return link.equals(itemData.link);

        }

        @Override
        public int hashCode() {
            int result = addtime.hashCode();
            result = 31 * result + areaname.hashCode();
            result = 31 * result + com_num;
            result = 31 * result + description.hashCode();
            result = 31 * result + iszan;
            result = 31 * result + pic_height;
            result = 31 * result + pic_url.hashCode();
            result = 31 * result + pic_width;
            result = 31 * result + view_num;
            result = 31 * result + zan_num;
            result = 31 * result + isfav;
            result = 31 * result + islink;
            result = 31 * result + istao;
            result = 31 * result + link.hashCode();
            result = 31 * result + fav_num;
            return result;
        }
    }

    public class TopicInfo implements Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 7259787835003045668L;
        public int tid;
        public String title;

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            TopicInfo topicInfo = (TopicInfo) o;

            if (tid != topicInfo.tid)
                return false;
            return title.equals(topicInfo.title);

        }

        @Override
        public int hashCode() {
            int result = tid;
            result = 31 * result + title.hashCode();
            return result;
        }
    }

    public class UserData implements Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 3809498556270189420L;
        public String avatar;
        public String group_bgcolor;
        public String group_name;
        public int isattention;
        public int isdaren;
        public String userid;
        public String username;

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            UserData userData = (UserData) o;

            if (isattention != userData.isattention)
                return false;
            if (isdaren != userData.isdaren)
                return false;
            if (!avatar.equals(userData.avatar))
                return false;
            if (!group_bgcolor.equals(userData.group_bgcolor))
                return false;
            if (!group_name.equals(userData.group_name))
                return false;
            if (!userid.equals(userData.userid))
                return false;
            return username.equals(userData.username);

        }

        @Override
        public int hashCode() {
            int result = avatar.hashCode();
            result = 31 * result + group_bgcolor.hashCode();
            result = 31 * result + group_name.hashCode();
            result = 31 * result + isattention;
            result = 31 * result + isdaren;
            result = 31 * result + userid.hashCode();
            result = 31 * result + username.hashCode();
            return result;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ItemInfo itemInfo = (ItemInfo) o;

        if (isurl != itemInfo.isurl)
            return false;
        if (pid != itemInfo.pid)
            return false;
        if (!postdata.equals(itemInfo.postdata))
            return false;
        if (!topicdata.equals(itemInfo.topicdata))
            return false;
        if (!url.equals(itemInfo.url))
            return false;
        if (!userdata.equals(itemInfo.userdata))
            return false;
        return zuserdata.equals(itemInfo.zuserdata);

    }

    @Override
    public int hashCode() {
        int result = isurl;
        result = 31 * result + pid;
        result = 31 * result + postdata.hashCode();
        result = 31 * result + topicdata.hashCode();
        result = 31 * result + url.hashCode();
        result = 31 * result + userdata.hashCode();
        result = 31 * result + zuserdata.hashCode();
        return result;
    }
}