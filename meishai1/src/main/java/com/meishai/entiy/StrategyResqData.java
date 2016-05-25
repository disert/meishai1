package com.meishai.entiy;

import android.os.Parcel;

import java.util.Comparator;
import java.util.List;

import com.meishai.entiy.CateResqData.CateData;
import com.meishai.entiy.HomeInfo.SlideInfo;
import com.meishai.entiy.HomeInfo.Subject;

/**
 * 美物-攻略 的数据存储类
 *
 * @author Administrator
 */
public class StrategyResqData extends BaseRespData {
    // StrategyResqData
    public List<StratData> list;
    public List<SlideInfo> slide;
    public Subject topic;
    public List<CateData> cate;


    public class CateData {
        public String catname;
        public int cid;
        public String desc;
        public String image;
    }

    public class PostPic {
        public String pic_path;//	http://img.meishai.com/2016/0316/20160316035855151.jpg_300x300.jpg
        public int pid;//	125872
    }

    public static class StratData {
        public H5Data h5data;//	Object;
        public int follow_num;
        public String image;
        public int isfollow;
        public int isnew;
        public int tid;
        public int typeid;
        public String title;
        public String type_text;//新增的字段 2015年12月7日15:58:13
        public String subtitle;
        //新增的字段 2016年3月17日13:10:24
        public List<CateData> catlist;//	Array
        public String sub_title;//	美晒大赏正在进行中
        public String sub_title_color;//	FF0000
        public int tempid;//	3
        public String icon;//	http://img.meishai.com/2016/0319/20160319095024313.png
        public int icon_height;//	141
        public int icon_width;//	257
        public int image_height;//	380
        public int image_width;//750
        public List<PostPic> postlist;//	Array
        public int view_num;//	336
        public int islogin;//	1
        public String url;//	http://www.meishai.com/index.php?m=app&c=user&a=daren&code=c0c0c30a9cdccf5040f20bb6abf2bfdd


//		image	http://img.meishai.com/2016/0307/20160307103617284.jpg
//		tid	1052
//		title	达人分享的一大波萌物正在靠近
//		type_text	美物攻略
//		typeid	5


        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            StratData stratData = (StratData) o;

            if (follow_num != stratData.follow_num)
                return false;
            if (isfollow != stratData.isfollow)
                return false;
            if (isnew != stratData.isnew)
                return false;
            if (tid != stratData.tid)
                return false;
            if (typeid != stratData.typeid)
                return false;
            if (image != null ? !image.equals(stratData.image) : stratData.image != null)
                return false;
            if (type_text != null ? !type_text.equals(stratData.type_text) : stratData.type_text != null)
                return false;
            if (title != null ? !title.equals(stratData.title) : stratData.title != null)
                return false;
            return !(subtitle != null ? !subtitle.equals(stratData.subtitle) : stratData.subtitle != null);

        }

        @Override
        public int hashCode() {
            int result = follow_num;
            result = 31 * result + (image != null ? image.hashCode() : 0);
            result = 31 * result + isfollow;
            result = 31 * result + isnew;
            result = 31 * result + tid;
            result = 31 * result + typeid;
            result = 31 * result + (type_text != null ? type_text.hashCode() : 0);
            result = 31 * result + (title != null ? title.hashCode() : 0);
            result = 31 * result + (subtitle != null ? subtitle.hashCode() : 0);
            return result;
        }
    }
}
