package com.meishai.entiy;

import java.util.List;

public class DiscloseListRespData extends BaseRespData {
    public List<DiscloseInfo> list;

    public class DiscloseInfo {
        public String address;//	日本方便
        public String addtime;//	时间：7小时前
        public String content;//	下班
        public int id;//	27
        public String image;//	http://img.meishai.com/2016/0409/20160409105645998.jpg
        public String status_color;//	FF0000
        public String status_text;//	未审核
        public String title;//	【商品】日本
    }

}
