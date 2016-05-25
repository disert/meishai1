package com.meishai.entiy;

import java.util.List;

public class Upgrade {

    private String success;
    // 新版本号
    private String version;
    // 显示时间，单位：秒，比如显示10秒自动关闭
    private int time;
    // 显示标题
    private String title;
    // 显示文字内容
    private String content;
    // 取消数据
    private List<Cancel> cancel;
    // 确定数据
    private List<Confirm> confirm;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<Cancel> getCancel() {
        return cancel;
    }

    public void setCancel(List<Cancel> cancel) {
        this.cancel = cancel;
    }

    public List<Confirm> getConfirm() {
        return confirm;
    }

    public void setConfirm(List<Confirm> confirm) {
        this.confirm = confirm;
    }

    public class Cancel {
        // 取消显示的文字
        private String text;
        // 取消显示的文字颜色
        private String color;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

    }

    public class Confirm {
        // 确定显示的文字
        private String text;
        // 确定显示的文字颜色
        private String color;
        // 确定跳转的链接
        private String url;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

    }
}
