package com.meishai.entiy;

import java.util.List;

/**
 * Created by Administrator on 2016/6/9.
 *
 * 消息页面接口返回数据的封装类
 */
public class MessageRespBean extends BaseRespData {

    /**
     * com_num : 5
     * fans_num : 2
     * fav_num : 3
     * list : [{"follow_num":96,"icon":"http://img.meishai.com/2016/0323/20160323124823318.png","icon_height":130,"icon_width":250,"image":"http://img.meishai.com/2016/0602/20160602020652648.jpg","tempid":1,"tid":1327,"title":"端午假期 敏感泛红通通不要","view_num":1062},{"follow_num":524,"icon":"http://img.meishai.com/2016/0322/20160322093242287.png","icon_height":130,"icon_width":250,"image":"http://img.meishai.com/2016/0602/20160602054214552.jpg","tempid":1,"tid":1328,"title":"专为出色工作者准备的时尚文具","view_num":5619},{"follow_num":565,"icon_height":130,"icon_width":250,"image":"http://img.meishai.com/2016/0602/20160602100115600.jpg","tempid":1,"tid":1324,"title":"夏日用蜜粉还是粉饼控油定妆？","view_num":6141},{"follow_num":1188,"icon":"http://img.meishai.com/2016/0322/20160322093242287.png","icon_height":130,"icon_width":250,"image":"http://img.meishai.com/2016/0601/20160601040100540.jpg","tempid":1,"tid":1326,"title":"陈罐西式茶货铺","view_num":13159},{"follow_num":1088,"icon_height":130,"icon_width":250,"image":"http://img.meishai.com/2016/0601/20160601032251644.jpg","tempid":1,"tid":1321,"title":"你用的洗面奶是性价比之王吗？","view_num":12409},{"follow_num":2101,"icon":"http://img.meishai.com/2016/0322/20160322093242287.png","icon_height":130,"icon_width":250,"image":"http://img.meishai.com/2016/0601/20160601112321304.jpg","tempid":1,"tid":1323,"title":"征服外貌协会的曲奇 你吃了吗？","view_num":22226},{"follow_num":2241,"icon_height":130,"icon_width":250,"image":"http://img.meishai.com/2016/0531/20160531055237894.jpg","tempid":1,"tid":1322,"title":"天天涂 还不如吃这些小药丸","view_num":23718},{"follow_num":2919,"icon_height":130,"icon_width":250,"image":"http://img.meishai.com/2016/0531/20160531045754313.jpg","tempid":1,"tid":1319,"title":"最受年轻人欢迎的精华液","view_num":30296},{"follow_num":3142,"icon_height":130,"icon_width":250,"image":"http://img.meishai.com/2016/0531/20160531035202789.jpg","tempid":1,"tid":1318,"title":"柠檬其它的功效你知道吗？","view_num":30926},{"follow_num":3922,"icon_height":130,"icon_width":250,"image":"http://img.meishai.com/2016/0604/20160604100506354.jpg","tempid":1,"tid":1320,"title":"Pony推荐的彩妆你种草吗？","view_num":41354}]
     * notice_num : 6
     * zan_num : 24
     */

    private int com_num;
    private int fans_num;
    private int fav_num;
    private int notice_num;
    private int zan_num;
    /**
     * follow_num : 96
     * icon : http://img.meishai.com/2016/0323/20160323124823318.png
     * icon_height : 130
     * icon_width : 250
     * image : http://img.meishai.com/2016/0602/20160602020652648.jpg
     * tempid : 1
     * tid : 1327
     * title : 端午假期 敏感泛红通通不要
     * view_num : 1062
     */

    private List<ListBean> list;

    public int getCom_num() {
        return com_num;
    }

    public void setCom_num(int com_num) {
        this.com_num = com_num;
    }

    public int getFans_num() {
        return fans_num;
    }

    public void setFans_num(int fans_num) {
        this.fans_num = fans_num;
    }

    public int getFav_num() {
        return fav_num;
    }

    public void setFav_num(int fav_num) {
        this.fav_num = fav_num;
    }

    public int getNotice_num() {
        return notice_num;
    }

    public void setNotice_num(int notice_num) {
        this.notice_num = notice_num;
    }

    public int getZan_num() {
        return zan_num;
    }

    public void setZan_num(int zan_num) {
        this.zan_num = zan_num;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        private int follow_num;
        private String icon;
        private int icon_height;
        private int icon_width;
        private String image;
        private int tempid;
        private int tid;
        private String title;
        private int view_num;

        public int getFollow_num() {
            return follow_num;
        }

        public void setFollow_num(int follow_num) {
            this.follow_num = follow_num;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public int getIcon_height() {
            return icon_height;
        }

        public void setIcon_height(int icon_height) {
            this.icon_height = icon_height;
        }

        public int getIcon_width() {
            return icon_width;
        }

        public void setIcon_width(int icon_width) {
            this.icon_width = icon_width;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public int getTempid() {
            return tempid;
        }

        public void setTempid(int tempid) {
            this.tempid = tempid;
        }

        public int getTid() {
            return tid;
        }

        public void setTid(int tid) {
            this.tid = tid;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getView_num() {
            return view_num;
        }

        public void setView_num(int view_num) {
            this.view_num = view_num;
        }
    }
}
