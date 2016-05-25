package com.meishai.entiy;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import com.meishai.app.util.StringUtil;
import com.meishai.util.GsonHelper;

/**
 * 试用详情
 *
 * @author sh
 */
public class TryuseDetail {

    // 商品ID
    @Expose
    private long sid;
    // 商品图片
    @Expose
    private String thumb;
    // 商品标题
    @Expose
    private String title;
    // 申请人数
    @Expose
    private int appnum;
    // 通过人数
    @Expose
    private int checknum;
    // 领取人数
    @Expose
    private int ordernum;
    // 结束时间
    @Expose
    private String endtime;
    // 限量数量
    @Expose
    private int snum;
    // 商品价值
    @Expose
    private String price;
    // 担保金
    @Expose
    private String gprice;
    // 免责声明
    @Expose
    private String text1;
    // 温馨提醒
    @Expose
    private String text2;
    // 商品图片列表 [JSON数据格式有误] 例如: "pics": 1
    @Expose
    private Object pics;
    private List<Pic> picList;
    // isapp=1，申请按钮变灰色，颜色代码：#999999
    private int isapp = 0;
    // 分享用到的数据
    @Expose
    private ShareData sharedata;

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

    public int getAppnum() {
        return appnum;
    }

    public void setAppnum(int appnum) {
        this.appnum = appnum;
    }

    public int getChecknum() {
        return checknum;
    }

    public void setChecknum(int checknum) {
        this.checknum = checknum;
    }

    public int getOrdernum() {
        return ordernum;
    }

    public void setOrdernum(int ordernum) {
        this.ordernum = ordernum;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public int getSnum() {
        return snum;
    }

    public void setSnum(int snum) {
        this.snum = snum;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getGprice() {
        return gprice;
    }

    public void setGprice(String gprice) {
        this.gprice = gprice;
    }

    public String getText1() {
        return text1;
    }

    public void setText1(String text1) {
        this.text1 = text1;
    }

    public String getText2() {
        return text2;
    }

    public void setText2(String text2) {
        this.text2 = text2;
    }

    public Object getPics() {
        return pics;
    }

    public void setPics(Object pics) {
        this.pics = pics;
    }

    public List<Pic> getPicList() {
        Type type = new TypeToken<List<Pic>>() {
        }.getType();
        picList = GsonHelper.parseObject(GsonHelper.toJson(this.pics), type);
        return picList;
    }

    public int getIsapp() {
        return isapp;
    }

    public void setIsapp(int isapp) {
        this.isapp = isapp;
    }

    public void setPicList(List<Pic> picList) {
        this.picList = picList;
    }

    public ShareData getSharedata() {
        return sharedata;
    }

    public void setSharedata(ShareData sharedata) {
        this.sharedata = sharedata;
    }

    public class Pic {
        @Expose
        private String url;
        @Expose
        private String width;
        @Expose
        private String height;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getWidth() {
            return width;
        }

        public void setWidth(String width) {
            this.width = width;
        }

        public String getHeight() {
            if (StringUtil.isBlank(height)) {
                height = "800";
            }
            return height;
        }

        public void setHeight(String height) {
            this.height = height;
        }

    }

}
