package com.meishai.entiy;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 品质专场item的数据
 */
public class SpecialInfo implements Parcelable {
    public int cid;
    public String image;
    public int tid;
    public String subtitle;
    public String title;

    protected SpecialInfo(Parcel in) {
        cid = in.readInt();
        image = in.readString();
        tid = in.readInt();
        subtitle = in.readString();
        title = in.readString();
    }

    public static final Creator<SpecialInfo> CREATOR = new Creator<SpecialInfo>() {
        @Override
        public SpecialInfo createFromParcel(Parcel in) {
            return new SpecialInfo(in);
        }

        @Override
        public SpecialInfo[] newArray(int size) {
            return new SpecialInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(cid);
        dest.writeString(image);
        dest.writeInt(tid);
        dest.writeString(subtitle);
        dest.writeString(title);
    }
}
