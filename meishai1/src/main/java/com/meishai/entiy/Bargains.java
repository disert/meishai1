package com.meishai.entiy;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

/**
 *  特价
 *
 * @author sh
 */
public class Bargains implements Parcelable {

    public enum BargainsType {
        TYPE_ALL("0", "全部"), TYPE_9k9("1", "9块9包邮"), TYPE_20("2", "20元封顶"), TYPE_DIS(
                "3", "精品折扣");
        private String type;
        private String remark;

        private BargainsType(String type, String remark) {
            this.type = type;
            this.remark = remark;
        }

        public String getRemark() {
            return remark;
        }

        public String getType() {
            return type;
        }

        public static String[] getAllTypeRemark() {
            String[] results = new String[BargainsType.values().length];
            int i = 0;
            for (BargainsType bargainsType : BargainsType.values()) {
                results[i] = bargainsType.getRemark();
                i++;
            }
            return results;
        }

    }

    // 商品ID
    @Expose
    private long id;
    // 是否新品
    @Expose
    private int isnew;
    // 商品图片
    @Expose
    private String thumb;
    // 标题
    @Expose
    private String title;
    // 折扣价格
    @Expose
    private double discount;
    // 商品原价
    @Expose
    private double price;
    // 连接地址
    @Expose
    private String itemurl;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getIsnew() {
        return isnew;
    }

    public void setIsnew(int isnew) {
        this.isnew = isnew;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getItemurl() {
        return itemurl;
    }

    public void setItemurl(String itemurl) {
        this.itemurl = itemurl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeInt(isnew);
        dest.writeString(thumb);
        dest.writeString(title);
        dest.writeDouble(discount);
        dest.writeDouble(price);
        dest.writeString(itemurl);
    }

    public final static Parcelable.Creator<Bargains> CREATOR = new Creator<Bargains>() {

        @Override
        public Bargains[] newArray(int size) {
            return new Bargains[size];
        }

        @Override
        public Bargains createFromParcel(Parcel source) {
            Bargains item = new Bargains();
            item.id = source.readLong();
            item.isnew = source.readInt();
            item.thumb = source.readString();
            item.title = source.readString();
            item.discount = source.readDouble();
            item.price = source.readDouble();
            item.itemurl = source.readString();
            return item;
        }
    };

}
