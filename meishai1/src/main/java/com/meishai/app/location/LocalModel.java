package com.meishai.app.location;

import com.baidu.location.LocationClientOption;
import com.meishai.app.util.StringUtil;

/**
 * 定位model
 *
 * @author sh
 */
public class LocalModel {

    public final static int MSG_WHAT = 1;
    public static int priority = LocationClientOption.NetWorkFirst;// 默认网路优先
    public static boolean isShowAddr = true;// 是否显示详细地址
    public static String localClass = "bd09ll";// 请求类型
    public static int interval = 3000;// 请求时间间隔
    private String addr = "";// 详细地址
    private String latitude = "";// 纬度
    private String longitude = "";// 经度
    private String radius = "";// 定位半径
    private String province = "";// 省份
    private String city = "";// 城市
    private String district = "";// 县
    private String street = "";// 街道
    private String streCode = "";

    public String getStreet() {
        if (StringUtil.isBlank(street)) {
            street = "";
        }
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreCode() {
        return streCode;
    }

    public void setStreCode(String streCode) {
        this.streCode = streCode;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getLatitude() {
        if (StringUtil.isBlank(latitude)) {
            latitude = "";
        }
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        if (StringUtil.isBlank(longitude)) {
            longitude = "";
        }
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getRadius() {
        return radius;
    }

    public void setRadius(String radius) {
        this.radius = radius;
    }

    public String getProvince() {
        if (StringUtil.isBlank(province)) {
            province = "";
        }
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        if (StringUtil.isBlank(city)) {
            city = "";
        }
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        if (StringUtil.isBlank(province)) {
            district = "";
        }
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    /**
     * 坐标 纬度,经度
     *
     * @return
     */
    public String toPoint() {
        if (latitude.length() > 0 && longitude.length() > 0) {
            return latitude + "," + longitude;
        }
        return "";
    }

    /**
     * 坐标 经度,纬度
     *
     * @return
     */
    public String toLongLatPoint() {
        if (latitude.length() > 0 && longitude.length() > 0) {
            return longitude + "," + latitude;
        }
        return "";
    }
}
