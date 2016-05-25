package com.meishai.entiy;

import java.util.List;

import com.meishai.entiy.HomePageDatas.PostInfo;
import com.meishai.entiy.PostItem.ZanUserInfo;

public class TopicRespData extends BaseRespData {
    public List<PostInfo> list;
    public ShareData sharedata;
    public TopicData1 topicdata1;
    public TopicData topicdata;
    public TopicData2 topicdata2;

    public class TopicData {
        public String description;//	晒服装
        public String image;//	http://img.meishai.com/2015/0421/20150421065158496.png
        public int isattention;//	1
        public String text;//	144晒晒，76关注
        public String title;//	晒服装
    }

    public class TopicData1 {
        public String description;
        public int isattention;
        public int ismore;
        public String more_color;
        public String more_text;
        public String more_url;
        public String pic;
        public String title;
    }

    public class TopicData2 {

        public int fans_num;
        public String image;
        public List<ZanUserInfo> userdata;
    }
}
