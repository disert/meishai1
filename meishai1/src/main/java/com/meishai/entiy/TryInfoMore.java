package com.meishai.entiy;

import com.google.gson.annotations.Expose;

public class TryInfoMore {
    // ismore=1显示右上角的文字，more=0不显示右上角文字
    @Expose
    private int ismore = 0;
    // 右上角显示的文字
    @Expose
    private String text;
    @Expose
    // iswap=1跳转到wap页,iswap=0跳转到其他试用APP页
    private int iswap = 0;
    @Expose
    private String url;

    public int getIsmore() {
        return ismore;
    }

    public void setIsmore(int ismore) {
        this.ismore = ismore;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getIswap() {
        return iswap;
    }

    public void setIswap(int iswap) {
        this.iswap = iswap;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
