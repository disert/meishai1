package com.meishai.entiy;

import java.util.List;

import com.meishai.entiy.StrategyResqData.StratData;

/**
 * 攻略详情的数据封装类
 *
 * @author Administrator
 */
public class StratDetailRespData extends BaseRespData {
    public Advertisement advertisement;
    public Welfare welfare;
    public List<SpecialItem> list;
    public ShareData sharedata;
    public HeadData topicdata;
    public List<Article> article;

    /**
     * 文章信息
     */
    public class Article {
        public String content;
        public List<Picture> pics;//	Array
        public String title;//	“蛋”生美味 用鸡蛋DIY营养早餐
    }

    /**
     * 广告信息
     */
    public class Advertisement {
        public int is_ad;
        public String ad_image;
        public String ad_url;
        public int istao;//	1
    }

    /**
     * 奖品信息
     */
    public class Welfare {
        public int is_welfare;
        public String welfare_image;
        public String welfare_url;
    }

    /**
     * item部分的信息
     */
    public class SpecialItem {
        public String button_text;
        public String itemurl;
        public int isfav;
        public String content;
        public int ispromise;
        public int iszan;
        public int istao;
        public int number;
        public List<Picture> pics;
        public int pid;
        public String price;
        public String promise_text;
        public String promise_url;
        public String title;
        public int zan_num;
    }

    /**
     * 广告的图片信息
     */
    public class Picture {
        public int pic_height;
        public String pic_path;
        public int pic_width;
    }

    /**
     * 头部的说明性信息
     */
    public class HeadData extends StratData {
        public String content;
    }


}
