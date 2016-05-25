package com.emoji;

import com.google.gson.annotations.Expose;

public class MsgEmojiModle {
    /**
     * 表情资源图片对应的ID
     */
    private int id;

    // 输入框显示的文字字符
    @Expose
    private String text;
    // 表情图片地址
    @Expose
    private String face;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFace() {
        return face;
    }

    public void setFace(String face) {
        this.face = face;
    }

}
