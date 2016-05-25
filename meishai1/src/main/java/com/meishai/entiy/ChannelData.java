package com.meishai.entiy;

import com.google.gson.annotations.Expose;

/**
 * 美晒分类左侧数据
 *
 * @author sh
 */
public class ChannelData {

    public enum ChannelType {
        catalog, topic;
    }

    // 进入下一层的类型，type=catalog为分类，type=topic为话题
    @Expose
    private String type;
    // 频道ID
    @Expose
    private long chid;
    // 频道名称
    @Expose
    private String name;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getChid() {
        return chid;
    }

    public void setChid(long chid) {
        this.chid = chid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
