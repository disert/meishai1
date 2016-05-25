package com.meishai.entiy;

import com.google.gson.annotations.Expose;

/**
 * 会员旺旺数据
 *
 * @author sh
 */
public class UserWang {
    // 旺旺ID
    @Expose
    private long tb_user_id;
    // 旺旺名称
    @Expose
    private String tb_user_name;
    // 该旺旺是否为主帐号，isindex=1 选中，isindex=0 未选中
    @Expose
    private int isindex;

    public long getTb_user_id() {
        return tb_user_id;
    }

    public void setTb_user_id(long tb_user_id) {
        this.tb_user_id = tb_user_id;
    }

    public String getTb_user_name() {
        return tb_user_name;
    }

    public void setTb_user_name(String tb_user_name) {
        this.tb_user_name = tb_user_name;
    }

    public int getIsindex() {
        return isindex;
    }

    public void setIsindex(int isindex) {
        this.isindex = isindex;
    }

}
