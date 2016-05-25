package com.meishai.entiy;

import com.google.gson.annotations.Expose;

/**
 * 试用
 *
 * @author sh
 */
public class TryInfo {

    public enum TryInfoType {
        // TRY_ALL("0", "全部试用"),
        // TRY_JP("1", "精品试用"),
        TRY_TASK("2", "进行中"), TRY_HB("3", "已结束");
        private String type;
        private String remark;

        private TryInfoType(String type, String remark) {
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
            String[] results = new String[TryInfoType.values().length];
            int i = 0;
            for (TryInfoType infoType : TryInfoType.values()) {
                results[i] = infoType.getRemark();
                i++;
            }
            return results;
        }
    }

    // 试用商品ID
    @Expose
    private long sid;
    // 商品图片
    @Expose
    private String thumb;
    // 商品标题
    @Expose
    private String title;
    // 限量数量
    @Expose
    private int snum;
    // 价值价格
    @Expose
    private double price;
    // 结束时间
    @Expose
    private long endtime;
    // 申请人数
    @Expose
    private int appnum;

    // 试用新首页试用
    @Expose
    private String pic_phone;
    // 是否达人
    @Expose
    private int isdaren;
    @Expose
    private int allnum;
    // 结束天数
    @Expose
    private int endday;

    public long getSid() {
        return sid;
    }

    public void setSid(long sid) {
        this.sid = sid;
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

    public int getSnum() {
        return snum;
    }

    public void setSnum(int snum) {
        this.snum = snum;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public long getEndtime() {
        return endtime;
    }

    public void setEndtime(long endtime) {
        this.endtime = endtime;
    }

    public int getAppnum() {
        return appnum;
    }

    public void setAppnum(int appnum) {
        this.appnum = appnum;
    }

    public String getPic_phone() {
        return pic_phone;
    }

    public void setPic_phone(String pic_phone) {
        this.pic_phone = pic_phone;
    }

    public int getIsdaren() {
        return isdaren;
    }

    public void setIsdaren(int isdaren) {
        this.isdaren = isdaren;
    }

    public int getAllnum() {
        return allnum;
    }

    public void setAllnum(int allnum) {
        this.allnum = allnum;
    }

    public int getEndday() {
        return endday;
    }

    public void setEndday(int endday) {
        this.endday = endday;
    }

}
