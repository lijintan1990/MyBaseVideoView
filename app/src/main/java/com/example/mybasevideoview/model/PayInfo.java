package com.example.mybasevideoview.model;

public class PayInfo {

    /**
     * status : 0
     * msg : 成功
     * data : {"app":"alipay_sdk=alipay-sdk-java-4.8.73.ALL&app_id=2019120669763089&biz_content=%7B%22body%22%3A%22%E4%B9%A1%E5%B0%84%E7%A4%BC%E8%A7%86%E9%A2%91%22%2C%22out_trade_no%22%3A%22659162853378490368%22%2C%22product_code%22%3A%22QUICK_MSECURITY_PAY%22%2C%22subject%22%3A%22%E4%B9%A1%E5%B0%84%E7%A4%BC%E8%A7%86%E9%A2%91%22%2C%22timeout_express%22%3A%2230m%22%2C%22total_amount%22%3A%220.01%22%7D&charset=UTF-8&format=json&method=alipay.trade.app.pay&notify_url=https%3A%2F%2Fwww.hongmingyuan.net%2Fapi%2Fpay%2FalipayNotify&sign=mbjz8pkH3TQdF7HYUxhs%2FJPwrM5QYenhxL1pGaU1NRX5HaKc6Vmp15lwht2MPlG9KzVwXF%2F8GkosikEGxITWl2dfDf%2FwL0jVLETD3T4PzRXwwauYUNXj%2B5jcCh0WTDAGfBAqDQK7eb%2Bt9nZsJb2CF%2BxRpoOOvl3DZxmrFdbuf%2B88bxQTFbckcdl6WrcP4DB9V0ZAUnQ1WDaJidDxuzFTVeKWGgMnwHLAysfA89JHzPii9M%2FNKAIsBYQx3O34%2BP01xumnb%2BaVsK6sZaLWI%2FoIxNIzX31iqDYTcm2QzNGQ2G3egWzxemA%2FLyICAySqefdKF7ZFsbtjldicCu%2BptAuisA%3D%3D&sign_type=RSA2&timestamp=2019-12-24+22%3A37%3A50&version=1.0"}
     */

    private int status;
    private String msg;
    private DataBean data;

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
         * app : alipay_sdk=alipay-sdk-java-4.8.73.ALL&app_id=2019120669763089&biz_content=%7B%22body%22%3A%22%E4%B9%A1%E5%B0%84%E7%A4%BC%E8%A7%86%E9%A2%91%22%2C%22out_trade_no%22%3A%22659162853378490368%22%2C%22product_code%22%3A%22QUICK_MSECURITY_PAY%22%2C%22subject%22%3A%22%E4%B9%A1%E5%B0%84%E7%A4%BC%E8%A7%86%E9%A2%91%22%2C%22timeout_express%22%3A%2230m%22%2C%22total_amount%22%3A%220.01%22%7D&charset=UTF-8&format=json&method=alipay.trade.app.pay&notify_url=https%3A%2F%2Fwww.hongmingyuan.net%2Fapi%2Fpay%2FalipayNotify&sign=mbjz8pkH3TQdF7HYUxhs%2FJPwrM5QYenhxL1pGaU1NRX5HaKc6Vmp15lwht2MPlG9KzVwXF%2F8GkosikEGxITWl2dfDf%2FwL0jVLETD3T4PzRXwwauYUNXj%2B5jcCh0WTDAGfBAqDQK7eb%2Bt9nZsJb2CF%2BxRpoOOvl3DZxmrFdbuf%2B88bxQTFbckcdl6WrcP4DB9V0ZAUnQ1WDaJidDxuzFTVeKWGgMnwHLAysfA89JHzPii9M%2FNKAIsBYQx3O34%2BP01xumnb%2BaVsK6sZaLWI%2FoIxNIzX31iqDYTcm2QzNGQ2G3egWzxemA%2FLyICAySqefdKF7ZFsbtjldicCu%2BptAuisA%3D%3D&sign_type=RSA2&timestamp=2019-12-24+22%3A37%3A50&version=1.0
         */

        private String app;

        public String getApp() {
            return app;
        }

        public void setApp(String app) {
            this.app = app;
        }
    }
}
