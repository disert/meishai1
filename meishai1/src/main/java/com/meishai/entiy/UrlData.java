package com.meishai.entiy;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 文件名：
 * 描    述：
 * 作    者：
 * 时    间：2015/12/7
 * 版    权：
 */
public class UrlData implements Parcelable {
    public String controller;

    public String action;

    public int tid;

    public int userid;

    protected UrlData(Parcel in) {
        controller = in.readString();
        action = in.readString();
        tid = in.readInt();
        userid = in.readInt();
    }

    public static final Creator<UrlData> CREATOR = new Creator<UrlData>() {
        @Override
        public UrlData createFromParcel(Parcel in) {
            return new UrlData(in);
        }

        @Override
        public UrlData[] newArray(int size) {
            return new UrlData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(controller);
        dest.writeString(action);
        dest.writeInt(tid);
        dest.writeInt(userid);
    }
}
