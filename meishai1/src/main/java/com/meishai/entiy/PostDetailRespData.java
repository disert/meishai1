package com.meishai.entiy;

import java.util.List;

/**
 * 文件名：
 * 描    述： 晒晒详情的实体bean
 * 作    者： yl
 * 时    间：2016/1/28
 * 版    权：
 */
public class PostDetailRespData extends BaseRespData {
    public List<CommentInfo> comdata;//	Array
    public List<PostItem> data;//	Array
    public List<HomePageDatas.PostInfo> list;//	Array
}
