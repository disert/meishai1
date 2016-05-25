package com.meishai.entiy;

import java.io.Serializable;

import com.google.gson.annotations.Expose;

/*
 * 晒晒首页-》分类
 */
public class CateInfo implements Serializable {
    private static final long serialVersionUID = 3570740349833172366L;

    @Expose
    public int cid;
    @Expose
    public String image;
    @Expose
    public String name;

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
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

    public final static int CID_ADD = -1;
}
