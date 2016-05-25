package com.meishai.entiy;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 启动广告图的数据
 * <p/>
 * Created by Administrator on 2015/12/3.
 */
public class SplashData implements Parcelable {
    public String action;//	daren
    public String controller;//	user
    public String image;//	http://img.meishai.com/2015/1202/20151202083649707.jpg
    public String type;//	g_show
    public int typeid;//
    public int tempid;//
    public int userid;//	77557
    public String value;//	444

    protected SplashData(Parcel in) {
        action = in.readString();
        controller = in.readString();
        image = in.readString();
        type = in.readString();
        userid = in.readInt();
        typeid = in.readInt();
        tempid = in.readInt();
        value = in.readString();
    }

    public static final Creator<SplashData> CREATOR = new Creator<SplashData>() {
        @Override
        public SplashData createFromParcel(Parcel in) {
            return new SplashData(in);
        }

        @Override
        public SplashData[] newArray(int size) {
            return new SplashData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(action);
        dest.writeString(controller);
        dest.writeString(image);
        dest.writeString(type);
        dest.writeInt(userid);
        dest.writeInt(typeid);
        dest.writeInt(tempid);
        dest.writeString(value);
    }
}
