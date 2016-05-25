package com.meishai.entiy;

import java.util.List;

/**
 * 文件名：
 * 描    述：晒晒 - 发现 - 数据
 * 作    者：yl
 * 时    间：2016/2/16
 * 版    权：
 */
public class HomeFindRespData extends BaseRespData {
    /**
     * 发现全球
     */
    public DataInfo<DatasItem> area;//	Object
    /**
     * 热门品牌
     */
    public DataInfo<DatasItem> brand;//	Object
    /**
     * 品质生活
     */
    public DataInfo<DatasItem> catalog;//	Object
    /**
     * 专题活动
     */
    public DataInfo<DatasItem> topic;//	Object
    /**
     * 达人之星
     */
    public DataInfo<DarenItem> daren;//	Object
    /**
     * 编辑精选
     */
    public DataInfo<DatasItem> tag;//	Object
    /**
     * 晒晒列表
     */
    public List<HomePageDatas.PostInfo> list;//	Array


    /**
     * 相当于是个基类,封装除晒晒列表以外的数据的
     */
    public class DataInfo<T> {
        public List<T> datas;//	Array
        public String more_text;//	查看更多
        public String title;//	品质生活
        public int cid;//	7
        public int tempid;//	2
    }

    /**
     * 除达人之星以外的数据
     */
    public class DatasItem {
        public String image;//	http://img.meishai.com/2016/0128/20160128102737604.jpg
        public String name;//	我要上头条
        public String text;//	共8987876人关注
        public int tid;//	827

    }

    /**
     * 达人之星的数据
     */
    public class DarenItem {
        public String avatar;//	http://img.meishai.com/avatar/8/10/79738/90x90.jpg
        public int userid;//	79738
        public String username;//	IVY

    }

}
