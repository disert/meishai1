package com.meishai.entiy;

import java.util.List;

/**
 * 文件名：
 * 描    述：美物,tempid=3获取到的数据
 * 作    者：
 * 时    间：2016年3月17日13:26:06
 * 版    权：
 */
public class StratDetailRespData1 extends BaseRespData {
    public StratDetailRespData.Advertisement advertisement;
    public List<stratDetailItem> list;
    public List<StrategyResqData.StratData> correlation;//	Array;
    public ShareData sharedata;
    public StratDetailRespData.HeadData topicdata;
    public StratDetailRespData.Welfare welfare;
    public String page_title;//	排行详情页

    /**
     * 新的item部分的信息
     */
    public class stratDetailItem {
        public String button_text;//	去购买!
        public String content;//	“以油制油”，通常我们用的粉底液，粉底霜，油性的眼影都含有高量油脂，卸妆油能轻易和脸上的彩妆融合，再以水乳方式彻底溶解彩妆。现在的卸妆油更突破了以往的矿物油成分，用植物油代替更能够清除彩妆，安全无刺激。注意的是，由于它比较油腻，偏油性皮肤的妹纸使用可能会引起闭合性粉刺哦。适用人群：中干性皮肤，每天都有完整上妆习惯的和经常化妆的妹纸。
        public String content_color;
        public int content_is_center;//	0
        public int content_is_strong;//	0
        public int isitem;//	0
        public int ispromise;//	0
        public int istao;//	0
        public String itemurl;
        public int pid;//	110218
        public String price;//	价格:0元
        public String title;//	油状卸妆品
        public String title_color;
        public int title_is_center;//	0
        public int title_is_line;//	0
        public int title_is_strong;//	0
        public int isline;//	0
        public int isfav;
        public List<StratDetailRespData.Picture> pics;
        public String tips_text;//	此商品经过美晒君亲测为品质优良，可以放心购买;
        public int tips_is_center;//	1
        public int tips_is_display;//	1
        public String tips_color;//	33CC99

        //		2016年3月17日13:39:05 美物item tempID=3时 跳转页面中item的数据
//		content	Papa recipe译成中文叫“爸爸的礼物”，很窝心的名字，当家明星春雨系列，内含大量蜂胶和蜂蜜，超级补水补湿，味道也超香的，清爽不粘稠，超薄至极柔软的面膜纸，只要试用一次，就会深深爱上的面膜！
//		pid	125629
//		tips_color	FF0000
//		tips_is_center	0
//		tips_is_display	1
//		tips_text	参考价：11.8元
//		title	春雨papa recipe保湿补水蜂蜜面膜
        public String count_text;//	233人喜欢
        public String rank_bgcolor;//	FF3366
        public String rank_text;//	第1名
        public String thumb;//	http://img.meishai.com/2016/0308/20160308045054215.jpg_300x300.jpg
        public String web_name;//	淘宝精选
    }
}
