package com.meishai.entiy;

import java.util.List;

/**
 * 文 件 名：
 * 描    述：消息 - 收藏 返回的数据的封装类
 * 作    者：yangling
 * 时    间：2016-06-14
 * 版    权：
 */

public class MessageFavRespBean extends BaseRespData {

    /**
     * userid : 82208
     * avatar : http://img.meishai.com/avatar/9/3/82208/90x90.jpg
     * username : 遥远的未来
     * title : 收藏了你的笔记
     * addtime : 2016-06-04 18:00:25
     * value : 123148
     * image : http://img.meishai.com/2016/0218/20160218120729675.jpg_300x300.jpg
     */

    private List<MessageZanRespBean.ListBean> list;

    public List<MessageZanRespBean.ListBean> getList() {
        return list;
    }

    public void setList(List<MessageZanRespBean.ListBean> list) {
        this.list = list;
    }


}
