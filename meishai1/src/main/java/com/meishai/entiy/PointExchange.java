package com.meishai.entiy;

import com.google.gson.annotations.Expose;

/**
 * @author sh
 */
public class PointExchange {

    public enum PointExchangeType {
        POINT_ALL("0", "全部福利"), POINT_LUCK("2", "幸运抽奖"), POINT_CRAZY("3",
                "疯狂竞拍"), POINT_EXCHANGE("1", "积分兑换");
        private String type;
        private String remark;

        private PointExchangeType(String type, String remark) {
            this.type = type;
            this.remark = remark;
        }

        public String getRemark() {
            return remark;
        }

        public String getType() {
            return type;
        }

        public static String[] getAllTypeRemark() {
            String[] results = new String[PointExchangeType.values().length];
            int i = 0;
            for (PointExchangeType exchangeType : PointExchangeType.values()) {
                results[i] = exchangeType.getRemark();
                i++;
            }
            return results;
        }
    }

    // 商品ID
    @Expose
    private long id;

    // isdaren=1达人专享
    @Expose
    private int isdaren;

    // 商品图片
    @Expose
    private String thumb;

    // 标题
    @Expose
    private String title;

    // 商品原价
    @Expose
    private String price;
    // 名额
    @Expose
    private String allnum;
    // 对象
    @Expose
    private String groupid;
    // 所需积分
    @Expose
    private String lowpoint;
    // type=1：积分兑换；type=2：幸运抽奖；type=3：疯狂竞拍；
    @Expose
    private int type;
    // 小编提醒 如果为空则不显示 确认订单时使用
    @Expose
    private String description;
    // 类型图片
    @Expose
    private String icon;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getIsdaren() {
        return isdaren;
    }

    public void setIsdaren(int isdaren) {
        this.isdaren = isdaren;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAllnum() {
        return allnum;
    }

    public void setAllnum(String allnum) {
        this.allnum = allnum;
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public String getLowpoint() {
        return lowpoint;
    }

    public void setLowpoint(String lowpoint) {
        this.lowpoint = lowpoint;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

}
