package com.meishai.entiy;

import java.util.List;

/**
 * Created by Administrator on 2016/5/21.
 */
public class DaysTopicResponseBean extends BaseRespData {
    /**
     * pic_height : 380
     * pic_url : http://img.meishai.com/2016/0518/20160518082439246.png
     * pic_width : 750
     * pid : 133387
     */

    private List<ListBean> list;

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        private int pic_height;
        private String pic_url;
        private int pic_width;
        private int pid;

        public int getPic_height() {
            return pic_height;
        }

        public void setPic_height(int pic_height) {
            this.pic_height = pic_height;
        }

        public String getPic_url() {
            return pic_url;
        }

        public void setPic_url(String pic_url) {
            this.pic_url = pic_url;
        }

        public int getPic_width() {
            return pic_width;
        }

        public void setPic_width(int pic_width) {
            this.pic_width = pic_width;
        }

        public int getPid() {
            return pid;
        }

        public void setPid(int pid) {
            this.pid = pid;
        }
    }
//    public List<DaysTopicItem> list;//	Array
//
//    public static class DaysTopicItem{
//        public int pic_height;//	380
//        public String pic_url;//	http://img.meishai.com/2016/0518/20160518082439246.png
//        public int pic_width;//	750
//        public String pid;//	133387
//    }


}
