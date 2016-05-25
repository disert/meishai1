package com.meishai.entiy;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 用于保存发布页面数据的bean
 *
 * @author Administrator yl
 */
public class ReleaseData implements Parcelable {

    private int cid;//分类id
    private int pid;//话题id
    private int tid;//贴纸id
    private int sid;
    private long aid;
    private int ptid;
    private int gid;
    private ArrayList<String> paths;//图片路径的集合
    /***/
    @Deprecated
    private List<Integer> ptids;//图片最后一次选中贴纸的集合
    private List<Integer> picsPosition;//删除的图片的集合
    private String content = "";//内容
    private String location = "";//位置
    private String oper;//发布还是修改
    private int isShowLocation;//是否显示地址
    private String longLatPoint = "";//精度 纬度
    private int isShare2Wechat;//是否分享到微信
    private String tids = "";//所有用过的贴纸
    private String url = "";//添加的链接


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(paths);
        dest.writeList(ptids);
        dest.writeList(picsPosition);
        dest.writeInt(gid);
        dest.writeInt(isShare2Wechat);
        dest.writeInt(cid);
        dest.writeInt(pid);
        dest.writeInt(tid);
        dest.writeInt(sid);
        dest.writeLong(aid);
        dest.writeInt(ptid);
        dest.writeString(tids);
        dest.writeString(content);
        dest.writeString(location);
        dest.writeString(oper);
        dest.writeInt(isShowLocation);
        dest.writeString(longLatPoint);
        dest.writeString(url);
    }

    public final static Parcelable.Creator<ReleaseData> CREATOR = new Creator<ReleaseData>() {

        @Override
        public ReleaseData[] newArray(int size) {
            return new ReleaseData[size];
        }

        @Override
        public ReleaseData createFromParcel(Parcel source) {
            ReleaseData item = new ReleaseData();
            item.paths = new ArrayList<String>();
            source.readList(item.paths, getClass().getClassLoader());
            item.ptids = new ArrayList<Integer>();
            source.readList(item.ptids, getClass().getClassLoader());
            item.picsPosition = new ArrayList<Integer>();
            source.readList(item.picsPosition, getClass().getClassLoader());
            item.gid = source.readInt();
            item.isShare2Wechat = source.readInt();
            item.cid = source.readInt();
            item.pid = source.readInt();
            item.tid = source.readInt();
            item.sid = source.readInt();
            item.aid = source.readLong();
            item.ptid = source.readInt();
            item.tids = source.readString();
            item.content = source.readString();
            item.location = source.readString();
            item.oper = source.readString();
            item.isShowLocation = source.readInt();
            item.longLatPoint = source.readString();
            item.url = source.readString();
            return item;
        }
    };

    public String getLongLatPoint() {
        return longLatPoint;
    }

    public void setLongLatPoint(String longLatPoint) {
        this.longLatPoint = longLatPoint;
    }

    public int getIsShowLocation() {
        return isShowLocation;
    }

    public void setIsShowLocation(int isShowLocation) {
        this.isShowLocation = isShowLocation;
    }

    public String getOper() {
        return oper;
    }

    public void setOper(String oper) {
        this.oper = oper;
    }

    public List<Integer> getPtids() {
        return ptids;
    }

    public void setPtids(List<Integer> ptids) {
        this.ptids = ptids;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public long getAid() {
        return aid;
    }

    public void setAid(long aid) {
        this.aid = aid;
    }

    public int getPtid() {
        return ptid;
    }

    public void setPtid(int ptid) {
        this.ptid = ptid;
    }

    public ArrayList<String> getPaths() {
        return paths;
    }

    public void setPaths(ArrayList<String> paths) {
        this.paths = paths;
    }

    public String getTids() {
        return tids;
    }

    public void setTids(String tids) {
        this.tids = tids;
    }

    public int getIsShare2Wechat() {
        return isShare2Wechat;
    }

    public void setIsShare2Wechat(int isShare2Wechat) {
        this.isShare2Wechat = isShare2Wechat;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getGid() {
        return gid;
    }

    public void setGid(int gid) {
        this.gid = gid;
    }

    public List<Integer> getPicsPosition() {
        return picsPosition;
    }

    public void setPicsPosition(List<Integer> picsPosition) {
        this.picsPosition = picsPosition;
    }
}
