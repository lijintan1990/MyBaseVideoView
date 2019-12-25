package com.example.mybasevideoview.view;

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
import com.example.mybasevideoview.MainActivity;
import com.example.mybasevideoview.R;
import com.example.mybasevideoview.controller.NetworkReq;
import com.example.mybasevideoview.model.PayInfo;
import com.example.mybasevideoview.model.RequestCode;
import com.example.mybasevideoview.utils.CommonDialog;
import com.example.mybasevideoview.utils.XslUtils;

import java.lang.ref.WeakReference;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.widget.Toast.LENGTH_LONG;
import static com.example.mybasevideoview.MainPlayerActivity.TAG;

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
    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    Log.d(TAG, "pay  result from zhifubao success");
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

    private void initDialog() {
        final CommonDialog dialog = new CommonDialog(PayNoticeActiviy.this);
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
                    public void weixinInfo(PayInfo payInfo) {

                    }

                    @Override
                    public void payResult(boolean result) {
                        Log.d(TAG, "pay result from service " + result);
                        if (result == true) {
                            dialog.dismiss();
                            Intent intent = new Intent();
                            intent.putExtra(getResources().getString(R.string.pay_result), result);
                            setResult(RESULT_OK, intent);
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

            }

            @Override
            public void onNegtiveClick() {
                dialog.dismiss();
                Toast.makeText(PayNoticeActiviy.this,"close",Toast.LENGTH_SHORT).show();
            }
        }).show();
    }
}
