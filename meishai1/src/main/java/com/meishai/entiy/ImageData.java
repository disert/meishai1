package com.meishai.entiy;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 图片
 *
 * @author sh
 */
public class ImageData implements Parcelable {
    public static int IMAGE_DEF = 1;
    public static int IMAGE_LOCAL = 2;
    public static int IMAGE_NETWORK = 3;

    private ImageData() {

    }

    // 图片地址
    private String path;
    // 图片类型 1:表示默认图片,2:表示本地图片，3:表示网络图片
    private int type;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    /**
     * 默认图片
     *
     * @return
     */
    public static ImageData getDefImage() {
        ImageData data = new ImageData();
        data.setType(IMAGE_DEF);
        return data;
    }

    /**
     * 本地图片
     *
     * @param path 图片路径
     * @return
     */
    public static ImageData getLocalImage(String path) {
        ImageData data = new ImageData();
        data.setType(IMAGE_LOCAL);
        data.setPath(path);
        return data;
    }

    /**
     * 网络图片
     *
     * @param path
     * @return
     */
    public static ImageData getNetworkImage(String path) {
        ImageData data = new ImageData();
        data.setType(IMAGE_NETWORK);
        data.setPath(path);
        return data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeInt(type);
    }

    public final static Parcelable.Creator<ImageData> CREATOR = new Creator<ImageData>() {

        @Override
        public ImageData[] newArray(int size) {
            return new ImageData[size];
        }

        @Override
        public ImageData createFromParcel(Parcel source) {
            ImageData item = new ImageData();
            item.path = source.readString();
            item.type = source.readInt();
            return item;
        }
    };

}
