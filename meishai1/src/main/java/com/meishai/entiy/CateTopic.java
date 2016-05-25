package com.meishai.entiy;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

/**
 * 美晒分类->话题右侧展示数据
 *
 * @author sh
 */
public class CateTopic implements Parcelable {

    public static final int HOT_TOPIC = 1;
    public static final int FAV_TOPIC = 2;
    public static final int LETAEST_TOPIC = 3;

    // type=1:热门；type=2:关注；type=3:最新
    @Expose
    private int type;
    // 话题类型图片
    @Expose
    private String image;
    // 话题分类名称
    @Expose
    private String name;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public class Topics {
        // 话题ID
        @Expose
        private long tid;
        // 话题展示图片
        @Expose
        private String image;

        public long getTid() {
            return tid;
        }

        public void setTid(long tid) {
            this.tid = tid;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(type);
        dest.writeString(image);
        dest.writeString(name);
    }

    public final static Parcelable.Creator<CateTopic> CREATOR = new Creator<CateTopic>() {

        @Override
        public CateTopic[] newArray(int size) {
            return new CateTopic[size];
        }

        @Override
        public CateTopic createFromParcel(Parcel source) {
            CateTopic item = new CateTopic();
            item.type = source.readInt();
            item.image = source.readString();
            item.name = source.readString();
            return item;
        }
    };

}
