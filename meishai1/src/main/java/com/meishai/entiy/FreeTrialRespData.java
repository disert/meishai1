package com.meishai.entiy;

import java.util.List;

import com.meishai.entiy.BaseRespData;

/**
 * 免费试用响应数据的封装类
 *
 * @author Administrator yl
 */
public class FreeTrialRespData extends BaseRespData {
    public List<FreeTrailData> list;
    public List<Brack> pcdata;
    public List<HomeInfo.SlideInfo> slide;//	Array

    public class Brack {
        public String action;
        public String controller;
        public double height;
        public String image;
        public int userid;
        public double width;
    }

    public class FreeTrailData {

        public int gid;
        public String image;
        public int isnew;
        public String title;
        public String allnum;
        public String appnum;
        public String endday;
        public H5Data h5data;//	Object
        public String icon;//
        public int icon_height;//	130
        public int icon_width;//	250
        public int iswap;//	0
    }

}
