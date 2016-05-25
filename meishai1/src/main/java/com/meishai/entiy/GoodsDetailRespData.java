package com.meishai.entiy;

import java.util.List;

/**
 * 文件名：GoodsDetailRespData
 * 描    述：商品详情数据的实体类
 * 作    者：yl
 * 时    间：2015/12/28
 * 版    权：
 */
public class GoodsDetailRespData extends BaseRespData {

    /**
     * 商品信息,里面只有一条数据
     */
    public List<GoodsDetail> data;
    /**
     * 分享数据
     */
    public ShareData sharedata;
    /**
     * 相关商品信息
     */
    public List<SKUResqData.Blurb> item_list;
    /**
     * 猜你喜欢信息
     */
    public List<StrategyResqData.StratData> topic_list;
    /**
     * 底部的按钮信息
     */
    public Bottom bottom;

    /**
     * 商品信息数据的实体
     */
    public class GoodsDetail {
        public String image;
        public String price_props;
        public String price_text;
        public String price;
        public String price_unit;
        public String content;
        public int pid;
        public String title;
        public List<StratDetailRespData.Picture> pics;
    }

    public class Bottom {
        public String button_text;//	去淘宝购买
        public int istao;//	1
        public int isfav;//	1
        public String itemurl;//	https://detail.tmall.com/item.htm?id=35865469573
    }
}
