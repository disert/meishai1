package com.meishai.entiy;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.meishai.util.StringUtils;

/**
 * @author sh
 */
public class ExchangeAddress implements Serializable {
    private static final long serialVersionUID = 1136583831157992263L;
    // 默认为1
    public final static int IS_DEFAULT = 1;
    public final static int NO_DEFAULT = 0;
    // aid:地址ID
    // isdefault:是否默认
    // realname:真实姓名
    // phone:手机号码
    // address:地址
    @Expose
    private int aid;
    @Expose
    private int isdefault;
    @Expose
    private String realname;
    @Expose
    private String phone;
    @Expose
    private String address;

    // 地区ID
    @Expose
    private int areaid;
    // 政编码
    @Expose
    private String zipcode;

    // 省份名称
    @Expose
    private String province;
    // 市名称
    @Expose
    private String city;

    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }

    public int getIsdefault() {
        return isdefault;
    }

    public void setIsdefault(int isdefault) {
        this.isdefault = isdefault;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        if (StringUtils.isBlank(address)) {
            address = "";
        }
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getAreaid() {
        return areaid;
    }

    public void setAreaid(int areaid) {
        this.areaid = areaid;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getProvince() {
        if (StringUtils.isBlank(province)) {
            province = "";
        }
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        if (StringUtils.isBlank(city)) {
            city = "";
        }
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    // 是否默认 true:默认 ，false:非默认
    public boolean isDef() {
        return isdefault == IS_DEFAULT;
    }

}
