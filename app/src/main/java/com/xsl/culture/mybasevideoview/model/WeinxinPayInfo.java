package com.xsl.culture.mybasevideoview.model;

import com.google.gson.annotations.SerializedName;

public class WeinxinPayInfo {
    /**
     * status : 0
     * msg : 成功
     * data : {"package":"Sign=WXPay","appid":"wx786ad5555f23f74d","sign":"1DD67F6F1230E115D8F3D23CB02D5373","partnerid":"1573861261","prepayid":"wx111430415520184c3f244e401782003700","noncestr":"91156738b1a7416e88507ccb20a66eff","timestamp":"1578724241"}
     */

    private int status;
    private String msg;
    private DataBean data;

//    {
//        "status": 0,
//            "msg": "成功",
//            "data": {
//        "package": "Sign=WXPay",
//                "appid": "wx786ad5555f23f74d",
//                "sign": "1DD67F6F1230E115D8F3D23CB02D5373",
//                "partnerid": "1573861261",
//                "prepayid": "wx111430415520184c3f244e401782003700",
//                "noncestr": "91156738b1a7416e88507ccb20a66eff",
//                "timestamp": "1578724241"
//    }
//    }

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * package : Sign=WXPay
         * appid : wx786ad5555f23f74d
         * sign : 1DD67F6F1230E115D8F3D23CB02D5373
         * partnerid : 1573861261
         * prepayid : wx111430415520184c3f244e401782003700
         * noncestr : 91156738b1a7416e88507ccb20a66eff
         * timestamp : 1578724241
         */

        @SerializedName("package")
        private String packageX;
        private String appid;
        private String sign;
        private String partnerid;
        private String prepayid;
        private String noncestr;
        private String timestamp;

        public String getPackageX() {
            return packageX;
        }

        public void setPackageX(String packageX) {
            this.packageX = packageX;
        }

        public String getAppid() {
            return appid;
        }

        public void setAppid(String appid) {
            this.appid = appid;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public String getPartnerid() {
            return partnerid;
        }

        public void setPartnerid(String partnerid) {
            this.partnerid = partnerid;
        }

        public String getPrepayid() {
            return prepayid;
        }

        public void setPrepayid(String prepayid) {
            this.prepayid = prepayid;
        }

        public String getNoncestr() {
            return noncestr;
        }

        public void setNoncestr(String noncestr) {
            this.noncestr = noncestr;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }
    }
}
