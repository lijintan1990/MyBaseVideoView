package com.example.mybasevideoview.model;

public class PayResult {
    /**
     * status : 0
     * msg : 成功
     * data : false
     */

    private int status;
    private String msg;
    private boolean data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isData() {
        return data;
    }

    public void setData(boolean data) {
        this.data = data;
    }
}
