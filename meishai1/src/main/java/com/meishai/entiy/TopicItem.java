package com.meishai.entiy;

import android.os.Parcel;
import android.os.Parcelable;

public class TopicItem implements Parcelable {

    public int tid;
    public String thumb;
    public String title;
    public String userid;
    public String username;
    public String addtime;
    public String description;
    public int isattention;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(tid);
        dest.writeString(thumb);
        dest.writeString(title);
        dest.writeString(userid);
        dest.writeString(username);
        dest.writeString(addtime);
        dest.writeString(description);
        dest.writeInt(isattention);

    }


    public final static Parcelable.Creator<TopicItem> CREATOR = new Creator<TopicItem>() {

        @Override
        public TopicItem[] newArray(int size) {
            return new TopicItem[size];
        }

        @Override
        public TopicItem createFromParcel(Parcel source) {
            TopicItem item = new TopicItem();
            item.tid = source.readInt();
            item.thumb = source.readString();
            item.title = source.readString();
            item.userid = source.readString();
            item.username = source.readString();
            item.addtime = source.readString();
            item.description = source.readString();
            item.isattention = source.readInt();


            return item;
        }
    };
}
