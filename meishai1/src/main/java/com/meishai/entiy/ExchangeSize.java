package com.meishai.entiy;

import com.google.gson.annotations.Expose;

public class ExchangeSize {
    // kid:尺寸ID
    // name:尺寸文字
    @Expose
    private int kid;
    @Expose
    private String name;

    public int getKid() {
        return kid;
    }

    public void setKid(int kid) {
        this.kid = kid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
