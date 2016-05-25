package com.meishai.entiy;


public class CommentInfo {

    public int dataid;        // :评论ID
    public String userid;    //:会员ID
    public int isdaren;    //:是否达人
    public String username; // :会员名称
    public String avatar;    // :会员头像
    public String addtime;    // :评论时间
    public String content;    // :评论内容
    //public ArrayList<PictureInfo> pics; // 评论图片
    public int reid;        //:被回复评论ID
    public ReData redata;    // :[userid:被评论的会员ID；username:被评论的会员名称；content:被评论的文字内容]

    //新增,替代groupid的
    public String group_bgcolor;//	71D14B
    public String group_name;//	V1会员

    public int groupid;    //0会员等级
    public int status;        // :0帖子状态//status=1的时候正常显示，status=0的时候隐藏帖子内容、图片，用"该帖子被删除"代替
    public int louid;        // :0楼层ID
    public int isattention; // :0是否关注
    public int isarea;        // :0//isarea=1：显示地理位置，isarea=0：不显示地理位置
    public int areaid;        // :0地区ID
    public String areaname; // :0地区名称
    public int isanonymous; // :0是否匿名//isanonymous=1：匿名，匿名的话不显示关注、头像、会员名称、会员等级、达人标志、地区信息


    public static class ReData {
        public String userid;
        public String username;
        public String content;
    }
}
