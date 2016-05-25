package com.meishai.entiy;

import java.util.List;

/**
 * 文件名：
 * 描    述：个人主页关注的数据
 * 作    者：
 * 时    间：2016/1/27
 * 版    权：
 */
public class HomePageFollowRespData extends BaseRespData {
    public List<FollowInfo> list;

    public class FollowInfo {
        public String avatar;//	http://img.meishai.com/avatar/8/10/79815/180x180.jpg
        public int isattention;//	0
        public int isdaren;//	1
        public String text;//	20个晒晒 445个粉丝
        public int userid;//	79815
        public String username;//	我才不怕跑步
    }
}
