package com.meishai.entiy;

import com.google.gson.annotations.Expose;

public class ColorInfo {
    @Expose
    private int colorid;
    @Expose
    private String name;

    public int getColorid() {
        return colorid;
    }

    public void setColorid(int colorid) {
        this.colorid = colorid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
