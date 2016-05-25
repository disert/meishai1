package com.meishai.entiy;

import java.util.List;

/**
 * 文件名：ReleaseRespData
 * 描    述：发布界面需要请求的数据(包含修改帖子时的请求)
 * 作    者：yl
 * 时    间：2016/1/8
 * 版    权：
 */
public class ReleaseRespData extends BaseRespData {

    public SimpleGoddsData goodsdata;//	Object
    public LinkedData linkdata;//Object;//	Object
    public List<TagInfo> tagsdata;//	Array这里面没有图片的信息
    public List<TagInfo> topicdata;//	Array
    public List<PostData> data;//	修改时获取的数据

    public class SimpleGoddsData {
        public int gid;//	5372
        public String text1;//	您正在提交#小编亲测 珊瑚绒日式拖鞋#的报告
        public String text2;//	报告截止日期：剩余3天时间
        public String thumb;//	http://img.meishai.com/2016/0104/20160104090610551.jpg_300x300.jpg
    }

    public class LinkedData {
        public int islink;//	1
        public String tips;//	1
    }

}
