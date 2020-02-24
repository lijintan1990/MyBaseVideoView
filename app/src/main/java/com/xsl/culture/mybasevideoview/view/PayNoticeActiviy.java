package com.xsl.culture.mybasevideoview.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.xapp.jjh.logtools.config.Constant;
import com.xsl.culture.R;
import com.xsl.culture.mybasevideoview.controller.NetworkReq;
import com.xsl.culture.mybasevideoview.model.PayInfo;
import com.xsl.culture.mybasevideoview.model.SharedPreferenceUtil;
import com.xsl.culture.mybasevideoview.model.WeinxinPayInfo;
import com.xsl.culture.mybasevideoview.utils.CommonDialog;
import com.xsl.culture.mybasevideoview.utils.XslUtils;
import com.xsl.culture.wxapi.WXPayEntryActivity;

import java.lang.ref.WeakReference;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.widget.Toast.LENGTH_LONG;
import static com.xsl.culture.mybasevideoview.MainPlayerActivity.TAG;

public class PayNoticeActiviy extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_pay_notice_activiy);
        XslUtils.hideStausbar(new WeakReference<>(this), true);
        ButterKnife.bind(this);

    }

    @OnClick(R.id.pay_btn)
    void pay(View view) {
        initDialog();
    }

    @OnClick(R.id.close_pay)
    void closePay(View view) {

        Intent intent = new Intent();
        intent.putExtra(getResources().getString(R.string.pay_result), -1);
        setResult(RESULT_OK, intent);
        SharedPreferenceUtil.getInstance(getApplicationContext()).putBoolean(getResources().getString(R.string.need_pay), true);
        finish();
    }

    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    Log.d(TAG, "pay  result from zhifubao result:" + msg.arg1);
                    //这里接收支付宝的回调信息
                    //需要注意的是，支付结果一定要调用自己的服务端来确定，不能通过支付宝的回调结果来判断
                    NetworkReq.getInstance().getPayResult("2019120669763089");
                    break;
                }
                default:
                    break;
            }
        };
    };

    private void callZhifubao(String info) {
        //下面的orderInfo就是咱自己的服务端返回的订单信息，里面除了订单ID等，还有签名等安全信息
        //使用方式基本按照支付宝的DEMO里面就行了
        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                PayTask alipay = new PayTask(PayNoticeActiviy.this);
                Map <String,String> result = alipay.payV2(info,true);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

    // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    private void callWeixin(WeinxinPayInfo payInfo) {
        Log.i(TAG, "callWeixin");

        WXPayEntryActivity.setWeixinPayResult(new WXPayEntryActivity.WeixinPayResult() {
            @Override
            public void onWeixinPayResult(int ret) {
                Log.d(TAG, "pay result from weixin " + ret);
                if (ret == 0) {
                    dialog.dismiss();
                    Intent intent = new Intent();
                    intent.putExtra(getResources().getString(R.string.pay_result), 0);
                    setResult(RESULT_OK, intent);
                    SharedPreferenceUtil.getInstance(getApplicationContext()).putBoolean(getResources().getString(R.string.need_pay), false);
                    finish();
                } else {
                    Toast.makeText(PayNoticeActiviy.this, "支付失败", LENGTH_LONG ).show();
                }
            }
        });

        String appId = payInfo.getData().getAppid();
        IWXAPI api = WXAPIFactory.createWXAPI(this, appId);
        boolean ret = api.registerApp(appId);
        Log.i(TAG, "registerApp ret:" + ret);
        //这里的bean，是服务器返回的json生成的bean
        PayReq payRequest = new PayReq();
        payRequest.appId = appId;
        payRequest.partnerId = payInfo.getData().getPartnerid();
        payRequest.prepayId = payInfo.getData().getPrepayid();
        payRequest.packageValue = payInfo.getData().getPackageX();//"Sign=WXPay";//固定值
        payRequest.nonceStr = payInfo.getData().getNoncestr();
        payRequest.timeStamp = payInfo.getData().getTimestamp();
        payRequest.sign = payInfo.getData().getSign();


        Log.i(TAG, "payRequest:" + payInfo.getData().getNoncestr() + " " + payInfo.getData().getSign() + " " + payInfo.getData().getPartnerid() +
                " " + payInfo.getData().getAppid() + " " + payInfo.getData().getPackageX());
        Log.i(TAG, "check arg:" + payRequest.checkArgs());
        //发起请求，调起微信前去支付
        ret = api.sendReq(payRequest);
        Log.d(TAG, "ret :" + ret);
    }

    CommonDialog dialog;
    private void initDialog() {
        dialog = new CommonDialog(PayNoticeActiviy.this);
        dialog.setMessage("￥18.00")
                .setTitle("乡射礼付费缓存")
                .setSingle(false).setOnClickBottomListener(new CommonDialog.OnClickBottomListener() {
            @Override
            public void onZhifubaoClick() {
                dialog.dismiss();
                Toast.makeText(PayNoticeActiviy.this,"支付宝",Toast.LENGTH_SHORT).show();

                NetworkReq.getInstance().setPayInterface(new NetworkReq.PayInterface() {
                    @Override
                    public void zhifubaoInfo(PayInfo payInfo) {
                        callZhifubao(payInfo.getData().getApp());
                    }

                    @Override
                    public void weixinInfo(WeinxinPayInfo payInfo) {

                    }

                    @Override
                    public void payResult(boolean result) {
                        Log.d(TAG, "pay result from service " + result);
                        if (result == true) {
                            dialog.dismiss();
                            Intent intent = new Intent();
                            intent.putExtra(getResources().getString(R.string.pay_result), result);
                            setResult(RESULT_OK, intent);
                            SharedPreferenceUtil.getInstance(getApplicationContext()).putBoolean(getResources().getString(R.string.need_pay), false);
                            finish();
                        } else {
                            Toast.makeText(PayNoticeActiviy.this, "支付失败", LENGTH_LONG ).show();
                        }
                    }
                });

                NetworkReq.getInstance().getPayInfo("2019120669763089", 2);
            }

            @Override
            public void onWeixinClick() {
                dialog.dismiss();
                Toast.makeText(PayNoticeActiviy.this,"微信", Toast.LENGTH_SHORT).show();

                NetworkReq.getInstance().setPayInterface(new NetworkReq.PayInterface() {
                    @Override
                    public void zhifubaoInfo(PayInfo payInfo) {
                        callZhifubao(payInfo.getData().getApp());
                    }

                    @Override
                    public void weixinInfo(WeinxinPayInfo payInfo) {
                        callWeixin(payInfo);
                    }

                    @Override
                    public void payResult(boolean result) {
                        Log.d(TAG, "pay result from service " + result);
                        if (result == true) {
                            dialog.dismiss();
                            Intent intent = new Intent();
                            intent.putExtra(getResources().getString(R.string.pay_result), result);
                            setResult(RESULT_OK, intent);
                            SharedPreferenceUtil.getInstance(getApplicationContext()).putBoolean(getResources().getString(R.string.need_pay), false);
                            finish();
                        } else {
                            Toast.makeText(PayNoticeActiviy.this, "支付失败", LENGTH_LONG ).show();
                        }
                    }
                });



                NetworkReq.getInstance().getWeixinPayInfo("2019120669763089", 1);
            }

            @Override
            public void onNegtiveClick() {
                dialog.dismiss();
                Toast.makeText(PayNoticeActiviy.this,"close",Toast.LENGTH_SHORT).show();
            }
        }).show();
    }
}
