package com.meishai.entiy;

import java.io.Serializable;

/**
 * 联系旺旺
 *
 * @author sh
 */
public class UserTaoBaoInfo implements Serializable {

    private String userID;
    private String taobaoUserID;
    private String taobaoUserName;
    private boolean isIndex;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getTaobaoUserID() {
        return taobaoUserID;
    }

    public void setTaobaoUserID(String taobaoUserID) {
        this.taobaoUserID = taobaoUserID;
    }

    public String getTaobaoUserName() {
        return taobaoUserName;
    }

    public void setTaobaoUserName(String taobaoUserName) {
        this.taobaoUserName = taobaoUserName;
    }

    public boolean isIndex() {
        return isIndex;
    }

    public void setIndex(boolean isIndex) {
        this.isIndex = isIndex;
    }

}
