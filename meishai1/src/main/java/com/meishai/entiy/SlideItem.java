package com.meishai.entiy;

/**
 * 精选页顶部滚动图片
 *
 * @author li
 */
public class SlideItem {
//	--type=post_list：晒晒列表
//	--type=post_view：晒晒详情
//	--type=topic_view：话题讨论
//	--type=user_index：会员首页
//	--type=daren_index：达人首页
//	--type=tejia_index：特价首页
//	--type=tejia_list：特价列表
//	--type=point_index：积分首页
//	--type=point_view：积分详情
//	--type=point_earn：赚积分页
//	--type=try_index：试用首页
//	--type=try_list：试用列表
//	--type=try_view：试用详情
//	--type=pc：pc端页面

    public String type;
    public String image;
    public String value;
    public int typeid;
    public int islogin;//	1
    public int tempid;//	0
    public H5Data h5data;//	Object
    public int image_height;//	380
    public int image_width;//	750
}
