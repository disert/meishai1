package com.meishai.entiy;

/**
 * Created by Administrator on 2016/6/9.
 */
public class DoubleSellRespBean extends BaseRespData {

    /**
     * gid : 5557
     * type : 2
     * groupid : 22
     * text : 我也要参团
     * text_color : 971E02
     * bgcolor : FFCC00
     */

    private SellGoodsRespBean.Button1Bean button;
    /**
     * title : 1.00元 【直降78元】海南三亚贵妃新鲜芒果5斤 果园直摘 限量100份
     * pic : http://img.meishai.com/2016/0607/20160607042506923.jpg
     * content : 【还差一人】美晒美拼，优质商品新鲜特供，一起实惠一起拼
     * url : http://www.meishai.com/mall/group?groupid=22
     */

    private ShareData sharedata;

    public SellGoodsRespBean.Button1Bean getButton() {
        return button;
    }

    public void setButton(SellGoodsRespBean.Button1Bean button) {
        this.button = button;
    }

    public ShareData getSharedata() {
        return sharedata;
    }

    public void setSharedata(ShareData sharedata) {
        this.sharedata = sharedata;
    }


}
