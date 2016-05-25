package com.meishai.entiy;

import android.os.Parcel;

import java.util.List;

import com.meishai.entiy.HomeInfo.Subject;

/**
 * 美物-单品 的数据存储类
 *
 * @author Administrator
 */
public class SKUResqData extends BaseRespData {
    public List<Blurb> list;


    public class Blurb {
        public String props;
        public String image;
        public int isfav;
        public int ispromise;
        public int istao;
        public int isurl;
        public int iszan;
        public int isnew;
        public String itemurl;
        public int pid;
        public String price;
        public String price_text;
        public String title;
        public int zan_num;
        public int islink;//	1

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Blurb blurb = (Blurb) o;

            if (isfav != blurb.isfav) return false;
            if (ispromise != blurb.ispromise) return false;
            if (istao != blurb.istao) return false;
            if (isurl != blurb.isurl) return false;
            if (iszan != blurb.iszan) return false;
            if (isnew != blurb.isnew) return false;
            if (pid != blurb.pid) return false;
            if (zan_num != blurb.zan_num) return false;
            if (islink != blurb.islink) return false;
            if (!props.equals(blurb.props)) return false;
            if (!image.equals(blurb.image)) return false;
            if (!itemurl.equals(blurb.itemurl)) return false;
            if (!price.equals(blurb.price)) return false;
            if (!price_text.equals(blurb.price_text)) return false;
            return title.equals(blurb.title);

        }

        @Override
        public int hashCode() {
            int result = props.hashCode();
            result = 31 * result + image.hashCode();
            result = 31 * result + isfav;
            result = 31 * result + ispromise;
            result = 31 * result + istao;
            result = 31 * result + isurl;
            result = 31 * result + iszan;
            result = 31 * result + isnew;
            result = 31 * result + itemurl.hashCode();
            result = 31 * result + pid;
            result = 31 * result + price.hashCode();
            result = 31 * result + price_text.hashCode();
            result = 31 * result + title.hashCode();
            result = 31 * result + zan_num;
            result = 31 * result + islink;
            return result;
        }
    }
}
