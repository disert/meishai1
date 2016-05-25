package com.meishai.entiy;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 试用信息
 *
 * @author sh
 */
public class TrialInfo {
    // 试用未通过
    public static final String TRIAL_NO_PASS = "0";
    // 试用进行中
    public static final String TRIAL_ING = "1";
    // 试用已完成
    public static final String TRIAL_FINISH = "2";

    // 状态
    public enum TrialStatus {
        // 0：未提交[文字颜色：#FF0000]；ostatus=1：未审核[文字颜色：#FF0000]；ostatus=2：拒绝[文字颜色：#FF0000]；ostatus=99：通过[文字颜色：#009900]
        S0(0, "未提交", "#FF0000"), S1(1, "未审核", "#FF0000"), S2(2, "拒绝", "#FF0000"), S3(
                3, "已修改", "#FF6600"), S99(99, "通过", "#009900");
        private int status;
        private String text;
        private String color;

        private TrialStatus(int status, String text, String color) {
            this.status = status;
            this.text = text;
            this.color = color;
        }

        public int getStatus() {
            return status;
        }

        public static TrialStatus getTrialStatus(int status) {
            for (TrialStatus s : TrialStatus.values()) {
                if (s.status == status) {
                    return s;
                }
            }
            return S2;
        }

        public String getText() {
            return text;
        }

        public String getColor() {
            return color;
        }

    }

    // 申请ID
    @Expose
    private int appid;
    // 试用编号
    @Expose
    private int sid;
    //等价于sid,但是更通用,新增
    @Expose
    private int gid;
    // 试用标题
    @Expose
    private String title;
    // 试用图片
    @Expose
    private String thumb;
    // 试用类型 stype=0:拍A送A，stype=1:拍A送B，stype=2:纯红包
    @Expose
    private int stype;
    @Expose
    @SerializedName("stype_text")
    // 试用类型对应文字，直接显示
    private String stypeText;
    // 试用类型文字对应的颜色代码
    @Expose
    @SerializedName("stype_color")
    private String stypeColor;
    // 下单价格
    @Expose
    private double price;
    // 邮费状态
    @Expose
    private String fee;
    // 下单类型 otype=0:直接下单；otype=1:搜索下单；otype=2:无须下单；otype=3二微码下单
    @Expose
    private int otype;
    // 下单类型文字
    @Expose
    private String otype_text;
    // 返还金额
    @Expose
    private double fprice;
    // 红包 hprice>0 才显示
    @Expose
    private double hprice;
    // 定单号状态
    // ostatus=0：未提交[文字颜色：#FF0000]；ostatus=1：未审核[文字颜色：#FF0000]；ostatus=2：拒绝[文字颜色：#FF0000]；ostatus=99：通过[文字颜色：#009900]
    @Expose
    private int ostatus;
    // rstatus=0：未提交[文字颜色：#FF0000]；rstatus=1：未审核[文字颜色：#FF0000]；rstatus=2：拒绝[文字颜色：#FF0000]；rstatus=99：通过[文字颜色：#009900]
    @Expose
    private int rstatus;
    // fstatus=0：担保金未申请；fstatus=1：担保金已返；fstatus=2：担保金已申请
    @Expose
    private int fstatus;
    // 订单编号
    @Expose
    private String orderno;
    // reporttext:评价截图/试用报告文字
    @Expose
    private String reporttext;
    // ordertime:提交订单结束时间
    @Expose
    private long ordertime;
    // reporttime:提交报告结束时间
    @Expose
    private long reporttime;
    // checktime:商家审核结束时间
    @Expose
    private long checktime;
    // systemtime:系统审核结束时间
    @Expose
    private long systemtime;
    // 当前时间
    @Expose
    private long nowtime;

    @Expose
    private String wap_url;

    public int getAppid() {
        return appid;
    }

    public void setAppid(int appid) {
        this.appid = appid;
    }

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public int getStype() {
        return stype;
    }

    public void setStype(int stype) {
        this.stype = stype;
    }

    public String getStypeText() {
        return stypeText;
    }

    public void setStypeText(String stypeText) {
        this.stypeText = stypeText;
    }

    public String getStypeColor() {
        return stypeColor;
    }

    public void setStypeColor(String stypeColor) {
        this.stypeColor = stypeColor;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public int getOtype() {
        return otype;
    }

    public void setOtype(int otype) {
        this.otype = otype;
    }

    public String getOtype_text() {
        return otype_text;
    }

    public void setOtype_text(String otype_text) {
        this.otype_text = otype_text;
    }

    public double getFprice() {
        return fprice;
    }

    public void setFprice(double fprice) {
        this.fprice = fprice;
    }

    public double getHprice() {
        return hprice;
    }

    public void setHprice(double hprice) {
        this.hprice = hprice;
    }

    public int getOstatus() {
        return ostatus;
    }

    public void setOstatus(int ostatus) {
        this.ostatus = ostatus;
    }

    public int getRstatus() {
        return rstatus;
    }

    public void setRstatus(int rstatus) {
        this.rstatus = rstatus;
    }

    public int getFstatus() {
        return fstatus;
    }

    public void setFstatus(int fstatus) {
        this.fstatus = fstatus;
    }

    public String getOrderno() {
        return orderno;
    }

    public void setOrderno(String orderno) {
        this.orderno = orderno;
    }

    public String getReporttext() {
        return reporttext;
    }

    public void setReporttext(String reporttext) {
        this.reporttext = reporttext;
    }

    public long getOrdertime() {
        return ordertime;
    }

    public void setOrdertime(long ordertime) {
        this.ordertime = ordertime;
    }

    public long getReporttime() {
        return reporttime;
    }

    public void setReporttime(long reporttime) {
        this.reporttime = reporttime;
    }

    public long getChecktime() {
        return checktime;
    }

    public void setChecktime(long checktime) {
        this.checktime = checktime;
    }

    public long getSystemtime() {
        return systemtime;
    }

    public void setSystemtime(long systemtime) {
        this.systemtime = systemtime;
    }

    public String getWap_url() {
        return wap_url;
    }

    public void setWap_url(String wap_url) {
        this.wap_url = wap_url;
    }

    public long getNowtime() {
        return nowtime;
    }

    public void setNowtime(long nowtime) {
        this.nowtime = nowtime;
    }

    public int getGid() {
        return gid;
    }

    public void setGid(int gid) {
        this.gid = gid;
    }
}
