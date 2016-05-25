package com.meishai.entiy;

import java.util.List;

/**
 * 文件名：FuLiSheDetailResq
 * 描    述：福利社 - 福利详情 数据的实体类
 * 作    者：yl
 * 时    间：2015/12/16
 * 版    权：
 */
public class FuLiSheDetailResq extends BaseRespData {

    public ButtonData bottom;//	按钮信息
    public DetailData data;//	具体数据
    public GoodsInfo details;//	商品信息及图片
    public List<SkipInfo> h5data;//	链接数组
    public Describe notice;//	提示栏信息
    public ProcessInfo process;//	流程栏图片信息
    public ShareData sharedata;//	分享信息


    public class ButtonData {
        public int app_button;//	1
        public String app_button_color;
        ;//	F15B82
        public String app_button_text;
        ;//
        public String endday;//	距离结束：-16786天
        public int item_button;//	1
        public String item_button_color;//	FFBA00
        public int item_button_istao;//	1
        public String item_button_text;//	直接购买
        public String item_button_url;//	https://item.taobao.com/item.htm?id=521264804148
    }

    public class Describe {
        public String content;
        public String title;//	注意事项
    }

    public class GoodsInfo extends Describe {
        public List<TrialDetailRespData.Picture> pics;
    }

    public class ProcessInfo {
        public int height;//	150
        public String image;//	http://img.meishai.com/2015/1214/20151214102024810.png
        public String title;//	体验流程
        public int width;//	750
    }

    public class SkipInfo {
        public String color;//	006699
        public String title;//	什么是品质体验？
        public String url;//	http://www.meishai.com/topic/514
    }

    public class DetailData {
        public String appnum;//: "3118人",
        public String appnum_color;//: "FF6600",
        public int appnum_line;//: 0,
        public String appnum_text;//: "申请",

        public int gid;//: 5325,
        public String image;//: "http://img.meishai.com/2015/1214/20151214050024567.jpg",
        public String title;//: "时尚翅膀编织单肩斜跨手提包",

        public int isapp;//: 1,
        public int isorder;//: 1,//是否需要跳转“提交订单”页面，isorder=1跳转“提交订单”页面，isorder=0不需要跳转“提交订单”页面
        public int isshare;//: 1,
        public String number;//: "5份",
        public String number_color;//: "FF6600",
        public int number_line;//: 0,
        public String number_text;//: "限量",

        public String price;//: "￥299元",
        public String price_color;//: "FF6600",
        public int price_line;//: 1,
        public String price_text;//: "价值",

        public String page_title;//: "品质体验",


    }

}
