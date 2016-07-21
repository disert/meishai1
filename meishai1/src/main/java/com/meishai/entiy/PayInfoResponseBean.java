package com.meishai.entiy;

/**
 * 文 件 名：
 * 描    述：支付信息接口返回的数据的封装类
 * 作    者：yangling
 * 时    间：2016-07-13
 * 版    权：
 */

public class PayInfoResponseBean {

    /**
     * success : 1
     * tips : 成功获取数据
     * data : {"payid":2,"payname":"支付宝支付","body":"【新西兰】海丝蓓康 维C杏仁滋润抗氧进口沐浴露/乳 1L等1件商品","out_trade_no":"916152241468309816","total_fee":"233.00"}
     */

    private int success;
    private String tips;
    /**
     * payid : 2
     * payname : 支付宝支付
     * body : 【新西兰】海丝蓓康 维C杏仁滋润抗氧进口沐浴露/乳 1L等1件商品
     * out_trade_no : 916152241468309816
     * total_fee : 233.00
     */

    private DataBean data;

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private int payid;
        private String payname;
        private String body;
        private String out_trade_no;
        private String total_fee;

        public int getPayid() {
            return payid;
        }

        public void setPayid(int payid) {
            this.payid = payid;
        }

        public String getPayname() {
            return payname;
        }

        public void setPayname(String payname) {
            this.payname = payname;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public String getOut_trade_no() {
            return out_trade_no;
        }

        public void setOut_trade_no(String out_trade_no) {
            this.out_trade_no = out_trade_no;
        }

        public String getTotal_fee() {
            return total_fee;
        }

        public void setTotal_fee(String total_fee) {
            this.total_fee = total_fee;
        }
    }
}
