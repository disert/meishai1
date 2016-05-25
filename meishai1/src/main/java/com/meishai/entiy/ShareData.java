package com.meishai.entiy;

import com.google.gson.annotations.Expose;

/**
 * 分享用到的数据
 *
 * @author sh
 */
public class ShareData {
    // 分享标题
    @Expose
    private String title;
    // 分享图片
    @Expose
    private String pic;
    // 分享文字内容
    @Expose
    private String content;
    // 分享用到的链接
    @Expose
    private String url;
    @Expose
    private String share_title;
    @Expose
    private String share_tips;
    // 是否显示积分
    @Expose
    private int isPoint;

    public String getShare_title() {
        return share_title;
    }

    public void setShare_title(String share_title) {
        this.share_title = share_title;
    }

    public String getShare_tips() {
        return share_tips;
    }

    public void setShare_tips(String share_tips) {
        this.share_tips = share_tips;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getIsPoint() {
        return isPoint;
    }

    public void setIsPoint(int isPoint) {
        this.isPoint = isPoint;
    }
}
