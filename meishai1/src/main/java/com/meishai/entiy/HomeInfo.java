package com.meishai.entiy;

import java.util.List;

/**
 * 主页数据的封装类 2.0
 *
 * @author Administrator yl
 */
public class HomeInfo {
    //分类
    public List<CateInfo> application;

    public class CateInfo {
        public H5Data h5data;//	Object
        public int image_height;//	150
        public int image_width;//	150
        public int tempid;//	0
        public String image;
        public String text;
        public String type;
        public String value;
        public int islogin;
        public String action;
        public String controller;
    }

    //分享数据
    public ShareData sharedata;

    //item
    public List<ItemInfo> list;
    public int page;
    public int pages;
    public int pagesize;

    //幻灯片
    public List<SlideInfo> slide;

    public class SlideInfo extends SlideItem {
    }

    public int success;
    public String tips;
    public int total;
    //专题
    public Subject topic;

    public static class Subject {
        public String more;
        public String text;
        public List<SubjectInfo> topicdata;

        public class SubjectInfo {
            public String image;
            public int tid;
            public String title;

            public String type;//	h5
            public int typeid;//	0
            public String value;//	http://www.meishai.com
            public int islogin;//	1
            public int tempid;//	0
            public H5Data h5data;//	Object
            public int image_height;//	380
            public int image_width;//	750

        }
    }

    //达人推荐
    public DaRen daren;

    public class DaRen {
        public List<DaRenData> darendata;

        public class DaRenData {
            public String avatar;
            public String group_bgcolor;
            public String group_name;
            public String intro;
            public int isattention;
            public int isdaren;
            public int userid;
            public String username;
        }

        public String more;
        public String text;
        public String type;
        public String value;

    }

    //美物推荐
    public MeiWu item;

    public class MeiWu {
        public List<ItemData> itemdata;

        public class ItemData {
            public String image;
            public String itemurl;
            public String price;
        }

        public String more;
        public String text;

    }

    public static class ZanUserData {
        public String avatar;
        public String userid;
    }

    public List<MeiWuItem> meiwu;

    public class MeiWuItem {
        public String image;//	http://img.meishai.com/2015/1113/20151113025324994.jpg
        public int tid;//	0
        public String title;//	舌尖上的川味
        public int typeid;//	0
        public String url;//	http://www.taobao.com
        public int width;//	750
    }
}
