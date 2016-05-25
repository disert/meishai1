package com.meishai.entiy;

import android.os.Parcel;

import java.util.List;

/**
 * 美物-分类 的数据存储类
 *
 * @author Administrator
 */
public class CateResqData extends BaseRespData {

    public List<CateData> list;
    public List<Key> keys;

    public class Key {
        public String key;
    }


    public class CateData {
        public String catname;
        public int cid;
        public String desc;
        public String image;
    }
}
