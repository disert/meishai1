package com.meishai.entiy;

/**
 * Created by Administrator on 2016/6/9.
 */
public class SellGoodsRespBean extends BaseRespData {


    /**
     * bgcolor : FF9999
     * gid : 1
     * groupid : 0
     * price : ￥126.00
     * text : 单独购买
     * type : 0
     */

    private Button1Bean button1;
    /**
     * bgcolor : FF0000
     * gid : 1
     * groupid : 0
     * price : ￥0.00
     * text : 1人团购
     * type : 1
     */

    private Button1Bean button2;
    /**
     * page_title : 福利社
     * page_url : http://www.meishai.com/goods/1
     */

    private DataBean data;
    /**
     * isfav : 0
     * text : 收藏
     * url :
     */

    private FavBean fav;
    /**
     * text : 首页
     */

    private HomeBean home;
    /**
     * text : 订单
     * url :
     */

    private OrderBean order;
    /**
     * content : 【还差一人】美晒美拼，优质商品新鲜特供，一起实惠一起拼
     * pic : http://img.meishai.com/2013/0113/20130113035124211.jpg
     * title : 0.00元 2012新款韩版 修身弹力打底裤 女装大码秋冬长裤 小脚靴裤铅笔裤
     * url : http://www.meishai.com/goods/1
     */

    private ShareData sharedata;

    public Button1Bean getButton1() {
        return button1;
    }

    public void setButton1(Button1Bean button1) {
        this.button1 = button1;
    }

    public Button1Bean getButton2() {
        return button2;
    }

    public void setButton2(Button1Bean button2) {
        this.button2 = button2;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public FavBean getFav() {
        return fav;
    }

    public void setFav(FavBean fav) {
        this.fav = fav;
    }

    public HomeBean getHome() {
        return home;
    }

    public void setHome(HomeBean home) {
        this.home = home;
    }

    public OrderBean getOrder() {
        return order;
    }

    public void setOrder(OrderBean order) {
        this.order = order;
    }

    public ShareData getSharedata() {
        return sharedata;
    }

    public void setSharedata(ShareData sharedata) {
        this.sharedata = sharedata;
    }

    public static class Button1Bean {
        private String bgcolor;
        private String gid;
        private String groupid;
        private String price;
        private String text;
        private String type;
        private String text_color;

        public String getText_color() {
            return text_color;
        }

        public void setText_color(String text_color) {
            this.text_color = text_color;
        }

        public String getBgcolor() {
            return bgcolor;
        }

        public void setBgcolor(String bgcolor) {
            this.bgcolor = bgcolor;
        }

        public String getGid() {
            return gid;
        }

        public void setGid(String gid) {
            this.gid = gid;
        }

        public String getGroupid() {
            return groupid;
        }

        public void setGroupid(String groupid) {
            this.groupid = groupid;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }


    public static class DataBean {
        private String page_title;
        private String page_url;

        public String getPage_title() {
            return page_title;
        }

        public void setPage_title(String page_title) {
            this.page_title = page_title;
        }

        public String getPage_url() {
            return page_url;
        }

        public void setPage_url(String page_url) {
            this.page_url = page_url;
        }
    }

    public static class FavBean {
        private int isfav;
        private String text;
        private String url;

        public int getIsfav() {
            return isfav;
        }

        public void setIsfav(int isfav) {
            this.isfav = isfav;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class HomeBean {
        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    public static class OrderBean {
        private String text;
        private String url;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

}
