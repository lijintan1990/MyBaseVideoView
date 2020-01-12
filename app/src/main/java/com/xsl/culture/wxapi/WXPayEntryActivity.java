package com.xsl.culture.wxapi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {
    private static final String TAG = "WXPayEntryActivity";

    private IWXAPI api;
    private String app_id = "wx786ad5555f23f74d";//微信开发后台申请的app_id

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //这里可以不填写
        //setContentView(R.layout.activity_langugue);
        Log.d(TAG, "WXPayEntryActivity");
        api = WXAPIFactory.createWXAPI(this, app_id, false);
//        api.registerApp(app_id);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
        Log.d(TAG, "onReq");
    }


    /**
     * 处理结果回调
     *
     * @param resp
     */
    @Override
    public void onResp(BaseResp resp) {
        Log.d(TAG, "onPayFinish, errCode = " + resp.errCode);
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            switch (resp.errCode) {
                case 0://支付成功
                    Toast.makeText(this, "支付成功", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onResp: resp.errCode = 0   支付成功");
                    break;
                case -1://错误，可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等
                    Toast.makeText(this, "支付错误" + resp.errCode, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onResp: resp.errCode = -1  支付错误");
                    break;
                case -2://用户取消，无需处理。发生场景：用户不支付了，点击取消，返回APP。
                    Log.d(TAG, "onResp: resp.errCode = -2  用户取消");
                    Toast.makeText(this, "用户取消" + resp.errCode, Toast.LENGTH_SHORT).show();
                    break;
            }

            if (weixinPayResult != null) {
                weixinPayResult.onWeixinPayResult(resp.errCode);
            }

            finish();//这里需要关闭该页面
        }
    }

    private static WeixinPayResult weixinPayResult;

    public static WeixinPayResult getWeixinPayResult() {
        return weixinPayResult;
    }

    public static void setWeixinPayResult(WeixinPayResult weixinPayResult) {
        WXPayEntryActivity.weixinPayResult = weixinPayResult;
    }

    public interface WeixinPayResult {
        void onWeixinPayResult(int ret);
    }
}


//public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler{
//
//    private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";
//
//    private IWXAPI api;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_langugue);
//
//        api = WXAPIFactory.createWXAPI(this, "wx786ad5555f23f74d");
//        api.handleIntent(getIntent(), this);
//    }
//
//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        setIntent(intent);
//        api.handleIntent(intent, this);
//    }
//
//    @Override
//    public void onReq(BaseReq req) {
//    }
//
//    @Override
//    public void onResp(BaseResp resp) {
//        Log.d("weixin", "onPayFinish, errCode = " + resp.errCode);
//
//        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle("微信支付");
//            builder.setMessage("支付下hi新");
//            builder.show();
//        }
//    }
//}