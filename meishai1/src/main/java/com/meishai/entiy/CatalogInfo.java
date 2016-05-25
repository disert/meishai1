package com.meishai.entiy;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

/**
 * 分类
 *
 * @author sh
 */
public class CatalogInfo implements Parcelable {

    // 1 表示已经添加过该分类
    public static final int HAS_ADD = 1;
    public static final int NO_ADD = 0;
    // 分类ID
    @Expose
    public int cid;
    // 分类图片
    @Expose
    public String image;
    // 分类名称
    @Expose
    public String name;
    // 分类描述
    @Expose
    public String desc;
    // 当前登陆会员是否添加过该分类
    @Expose
    public int isadd = HAS_ADD;

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getIsadd() {
        return isadd;
    }

    public void setIsadd(int isadd) {
        this.isadd = isadd;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(cid);
        dest.writeString(image);
        dest.writeString(name);
        dest.writeString(desc);
        dest.writeInt(isadd);
    }

    public final static Parcelable.Creator<CatalogInfo> CREATOR = new Creator<CatalogInfo>() {

        @Override
        public CatalogInfo[] newArray(int size) {
            return new CatalogInfo[size];
        }

        @Override
        public CatalogInfo createFromParcel(Parcel source) {
            CatalogInfo item = new CatalogInfo();
            item.cid = source.readInt();
            item.image = source.readString();
            item.name = source.readString();
            item.desc = source.readString();
            item.isadd = source.readInt();
            return item;
        }
    };

}
