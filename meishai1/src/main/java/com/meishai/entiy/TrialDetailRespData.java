package com.meishai.entiy;

import java.util.List;

import com.meishai.entiy.BaseRespData;
import com.meishai.entiy.ShareData;

/**
 * 试用详情相应的数据封装类
 *
 * @author Administrator yl
 */
public class TrialDetailRespData extends BaseRespData {

    public TrialDetailData data;
    public Notice notice;
    public List<Picture> pics;
    public ShareData sharedata;
    public List<UserInfo> users;


    public class TrialDetailData {
        public int gid;// 5080
        public int isapp;//当前登录用户是否以申请,
        public int isshare;
        public String page_title;// 试用详情

        public String image;// http://img.meishai.com/2015/0930/20150930033134243.jpg
        public String title;// 秋冬新品打底裤裙子

        public String snum;// 限量:5份
        public String price;// 价值:45元
        public String gprice;// 已缴纳225元保证金
        public String lowpoint;// "积分：7000分",
        public String record_text;// "查看兑换记录",
        public String record_url;// "http://www.meishai.com/index.php?m=mobile&c=goods&c=point_user&gid=4395",


        public String shop_logo;// 赞助商家LOGO,
        public String shop_name;// 赞助商家名称,
        public String shop_url;// 赞助商家链接,
        public String shop_text;// 赞助商：

        public String user_more;// http://www.meishai.com/index.php?m=app&c=goods&a=userlist&gid=5080
        public String user_text;// 申请会员
        public String lastnum;// "剩余：0份",;
        public String appnum;//限量:2件
        public String endday;// 距离结束：7天
        public String button_text;// 申请试用

        public int isorder;//
        public String content;//
    }

    public class Notice {
        public String note_content;
        public String note_title;
    }

    public class Picture {
        public int pic_height;
        public String pic_url;
        public int pic_width;
    }

    public class UserInfo {
        public String avatar;
        public int userid;
    }
}
