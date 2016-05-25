package com.meishai.entiy;

import java.util.List;

import com.google.gson.annotations.Expose;

/**
 * 确认兑换
 *
 * @author sh
 */
public class ConfirmExchange {
    // 成功
    public static final int RESP_SUCCESS = 1;


    // 返回状态
    @Expose
    private Integer success;
    // 提示消息 
    @Expose
    private String tips;

    // 会员地址列表
    @Expose
    private List<ExchangeAddress> addressdata;
    // 商品详情数据
    @Expose
    private List<PointExchange> itemdata;
    // 尺寸数据，如果为空则不显示，点参与按钮的时候kid传入0
    @Expose
    private List<ExchangeSize> sizedata;
    @Expose
    private ButtonData bottom;//	按钮信息
    @Expose
    private List<FuLiGoodsInfo> goodsdata;//	新的商品详情数据,itemdata过时了
    @Expose
    private String user_point;//	您有：13525积分

    //商品颜色,
    @Expose
    private List<ColorInfo> colordata;//	Array

    // userpoint:当前登陆会员所拥有的积分
    // point:会员出的积分，只有当type=3的时候才需要显示加价
    // addpoint:会员点击加减价的时候增加的积分数量
    // buttontext:按钮显示的文字

    @Expose
    private long userpoint;
    @Expose
    private long point;
    @Expose
    private long addpoint;
    @Expose
    private String buttontext;

    //新的地址信息
    @Expose
    private ExchangeAddress useraddress;

    public ExchangeAddress getUseraddress() {
        return useraddress;
    }

    public void setUseraddress(ExchangeAddress useraddress) {
        this.useraddress = useraddress;
    }

    public List<ColorInfo> getColordata() {
        return colordata;
    }

    public static int getRespSuccess() {
        return RESP_SUCCESS;
    }

    public ButtonData getBottom() {
        return bottom;
    }

    public void setBottom(ButtonData bottom) {
        this.bottom = bottom;
    }

    public List<FuLiGoodsInfo> getGoodsdata() {
        return goodsdata;
    }

    public void setGoodsdata(List<FuLiGoodsInfo> goodsdata) {
        this.goodsdata = goodsdata;
    }

    public String getUser_point() {
        return user_point;
    }

    public void setUser_point(String user_point) {
        this.user_point = user_point;
    }

    public void setColordata(List<ColorInfo> colordata) {
        this.colordata = colordata;
    }

    public boolean isSuccess() {
        return this.success.intValue() == RESP_SUCCESS;
    }

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public List<ExchangeAddress> getAddressdata() {
        return addressdata;
    }

    public void setAddressdata(List<ExchangeAddress> addressdata) {
        this.addressdata = addressdata;
    }

    public List<PointExchange> getItemdata() {
        return itemdata;
    }

    public void setItemdata(List<PointExchange> itemdata) {
        this.itemdata = itemdata;
    }

    public List<ExchangeSize> getSizedata() {
        return sizedata;
    }

    public void setSizedata(List<ExchangeSize> sizedata) {
        this.sizedata = sizedata;
    }

    public long getUserpoint() {
        return userpoint;
    }

    public void setUserpoint(long userpoint) {
        this.userpoint = userpoint;
    }

    public long getPoint() {
        return point;
    }

    public void setPoint(long point) {
        this.point = point;
    }

    public long getAddpoint() {
        return addpoint;
    }

    public void setAddpoint(long addpoint) {
        this.addpoint = addpoint;
    }

    public String getButtontext() {
        return buttontext;
    }

    public void setButtontext(String buttontext) {
        this.buttontext = buttontext;
    }

}
