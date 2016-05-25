package com.meishai.entiy;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import android.os.Parcel;
import android.os.Parcelable;

public class PostItem implements Parcelable {

    public int pid;
    public int cid;//0
    public int tid;
    public int isadmin;//0
    public int isanonymous;//0
    public String userid;
    public int isattention;
    public String username;
    public String avatar;
    public int groupid;
    public int isdaren;
    public String addtime;
    public int iswon;//0
    public String title;//0
    public String description;//0
    public ArrayList<PictureInfo> pics = new ArrayList<PostItem.PictureInfo>();
    public int isarea;
    public int areaid;//0
    public String areaname;
    public int view_num;
    public int zan_num;
    public int com_num;
    //iszan=1：已赞过，iszan=0：未赞过
    public int iszan = 0;
    public ArrayList<ZanUserInfo> zuser = new ArrayList<PostItem.ZanUserInfo>();

    /**
     * 修改晒晒使用
     **/
    //帖子内容
    public String content;
    //分类名称
    public String catename;//0
    //分享数据
    private ShareData sharedata;
    //新增
    public int fav_num;//	0
    public int isfav;//	0
    public int islink;//	1
    public int istao;//	1
    public int istips;//	1
    public String group_bgcolor;//	4BD7FF
    public String group_name;//	V2会员
    public String link;//	https://item.taobao.com/item.htm?id=45922772787
    public String tips;//	本活动由美晒发起，与Apple公司无关
    public List<ItemInfo.TopicInfo> topicdata;//	Array


    public ShareData getSharedata() {
        return sharedata;
    }


    public void setSharedata(ShareData sharedata) {
        this.sharedata = sharedata;
    }


    public PostItem() {
    }

    public void setContent(String content) {
        description = content;
    }


    public String getPictureUrl(int index) {
        if (index >= 0 && index < pics.size()) {
            //DebugLog.d(pics.get(index).url);
            return pics.get(index).url;
        } else {
            return "";
        }

    }


    public static class PictureInfo implements Parcelable {
        public String url;
        public int width;
        public int height;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(url);
            dest.writeInt(width);
            dest.writeInt(height);
        }

        public final static Parcelable.Creator<PictureInfo> CREATOR = new Creator<PostItem.PictureInfo>() {

            @Override
            public PictureInfo[] newArray(int size) {
                return new PictureInfo[size];
            }

            @Override
            public PictureInfo createFromParcel(Parcel source) {
                PictureInfo item = new PictureInfo();
                item.url = source.readString();
                item.width = source.readInt();
                item.height = source.readInt();
                return item;
            }
        };
    }

    public static class ZanUserInfo implements Parcelable {
        public String userid = "";
        public String avatar = "";

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(userid);
            dest.writeString(avatar);
        }

        public final static Parcelable.Creator<ZanUserInfo> CREATOR = new Creator<ZanUserInfo>() {

            @Override
            public ZanUserInfo[] newArray(int size) {
                return new ZanUserInfo[size];
            }

            @Override
            public ZanUserInfo createFromParcel(Parcel source) {
                ZanUserInfo item = new ZanUserInfo();
                item.userid = source.readString();
                item.avatar = source.readString();
                return item;
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(pid);
        dest.writeInt(cid);
        dest.writeInt(tid);
        dest.writeInt(isadmin);
        dest.writeInt(isanonymous);
        dest.writeString(userid);
        dest.writeInt(isattention);
        dest.writeString(username);
        dest.writeString(avatar);
        dest.writeInt(groupid);
        dest.writeInt(isdaren);
        dest.writeString(addtime);
        dest.writeInt(iswon);
        dest.writeString(title);
        dest.writeString(description);

        dest.writeTypedList(pics);

        dest.writeInt(isarea);
        dest.writeInt(areaid);
        dest.writeString(areaname);
        dest.writeInt(view_num);
        dest.writeInt(zan_num);
        dest.writeInt(com_num);
        dest.writeInt(iszan);
        dest.writeTypedList(zuser);
    }

    public final static Parcelable.Creator<PostItem> CREATOR = new Creator<PostItem>() {

        @Override
        public PostItem[] newArray(int size) {
            return new PostItem[size];
        }

        @Override
        public PostItem createFromParcel(Parcel source) {
            PostItem item = new PostItem();
            item.pid = source.readInt();
            item.cid = source.readInt();
            item.tid = source.readInt();
            item.isadmin = source.readInt();
            item.isanonymous = source.readInt();
            item.userid = source.readString();
            item.isattention = source.readInt();
            item.username = source.readString();
            item.avatar = source.readString();
            item.groupid = source.readInt();
            item.isdaren = source.readInt();
            item.addtime = source.readString();
            item.iswon = source.readInt();
            item.title = source.readString();
            item.description = source.readString();

            item.pics = new ArrayList<PostItem.PictureInfo>();
            source.readTypedList(item.pics, PictureInfo.CREATOR);

            item.isarea = source.readInt();
            item.areaid = source.readInt();
            item.areaname = source.readString();
            item.view_num = source.readInt();
            item.zan_num = source.readInt();
            item.com_num = source.readInt();
            item.iszan = source.readInt();

            item.zuser = new ArrayList<PostItem.ZanUserInfo>();
            source.readTypedList(item.zuser, ZanUserInfo.CREATOR);

            return item;
        }
    };
}
