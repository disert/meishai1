package com.meishai.entiy;

import android.os.Parcel;

import java.util.List;

import com.meishai.entiy.BaseRespData;

/**
 * 积分商城响应数据的封装类
 *
 * @author Administrator yl
 */
public class PointRewardRespData extends BaseRespData {

    public List<TypeData> typedata;
    public List<PointData> list;


    public class PointData {
        public String allnum;
        public int gid;
        public String lowpoint;
        public String thumb;
        public String title;
        public String type_bgcolor;
        public String type_text;
        public int typeid;
    }

    public class TypeData {
        public String image;
        public int typeid;
        public String typename;
    }

}
