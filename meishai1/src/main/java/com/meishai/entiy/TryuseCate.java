package com.meishai.entiy;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

public class TryuseCate implements Parcelable {
    //  分类id
    @Expose
    private long cid;
    // 分类图片
    @Expose
    private String image;
    // 分类名字
    @Expose
    private String catname;

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

    public String getCatname() {
        return catname;
    }

    public void setCatname(String catname) {
        this.catname = catname;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(cid);
        dest.writeString(image);
        dest.writeString(catname);
    }

    public final static Parcelable.Creator<TryuseCate> CREATOR = new Creator<TryuseCate>() {

        @Override
        public TryuseCate[] newArray(int size) {
            return new TryuseCate[size];
        }

        @Override
        public TryuseCate createFromParcel(Parcel source) {
            TryuseCate item = new TryuseCate();
            item.cid = source.readLong();
            item.image = source.readString();
            item.catname = source.readString();
            return item;
        }
    };
}
