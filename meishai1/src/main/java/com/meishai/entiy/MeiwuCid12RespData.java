package com.meishai.entiy;

import com.meishai.app.widget.layout.PicLayout;

import java.util.List;

/**
 * 文件名：MeiwuCid12RespData
 * 描    述：美物cid为12时的数据
 * 作    者：yl
 * 时    间：2016/1/20
 * 版    权：
 */
public class MeiwuCid12RespData extends BaseRespData {

    public List<ColorCate> list;

    public class ColorCate {
        public int follow_num;
        public List<StratDetailRespData.Picture> pics;
        public int tid;
        public String title;
        public int typeid;
        public int view_num;
        public int tempid;//	2
    }

}
