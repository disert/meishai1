package com.meishai.entiy;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

public class SaleCate implements Parcelable {
    //  分类id
    @Expose
    private long cid;
    // 分类图片
    @Expose
    private String image;
    // 分类名字
    @Expose
    private String name;

    public long getCid() {
        return cid;
    }

    public void setCid(long cid) {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(cid);
        dest.writeString(image);
        dest.writeString(name);
    }

    public final static Parcelable.Creator<SaleCate> CREATOR = new Creator<SaleCate>() {

        @Override
        public SaleCate[] newArray(int size) {
            return new SaleCate[size];
        }

        @Override
        public SaleCate createFromParcel(Parcel source) {
            SaleCate item = new SaleCate();
            item.cid = source.readLong();
            item.image = source.readString();
            item.name = source.readString();
            return item;
        }
    };
}
