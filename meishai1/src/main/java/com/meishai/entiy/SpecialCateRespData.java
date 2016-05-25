package com.meishai.entiy;

import java.util.List;


/**
 * 专题列表数据的封装类
 *
 * @author Administrator yl
 */
public class SpecialCateRespData extends BaseRespData {

    public List<TagInfo> cate;

    public class TagInfo {
        public String catname;
        public int cid;
    }


}
