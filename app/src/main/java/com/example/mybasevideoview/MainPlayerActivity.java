package com.example.mybasevideoview;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.example.mybasevideoview.cover.CompleteCover;
import com.example.mybasevideoview.cover.ControllerCover;
import com.example.mybasevideoview.cover.ErrorCover;
import com.example.mybasevideoview.cover.LoadingCover;
import com.example.mybasevideoview.play.DataInter;

import com.kk.taurus.playerbase.assist.InterEvent;
import com.kk.taurus.playerbase.assist.OnVideoViewEventHandler;
import com.kk.taurus.playerbase.entity.DataSource;
import com.kk.taurus.playerbase.event.OnErrorEventListener;
import com.kk.taurus.playerbase.event.OnPlayerEventListener;
import com.kk.taurus.playerbase.receiver.OnReceiverEventListener;
import com.kk.taurus.playerbase.receiver.ReceiverGroup;
import com.kk.taurus.playerbase.widget.BaseVideoView;

public class MainPlayerActivity extends Activity implements OnPlayerEventListener {
    BaseVideoView mP17 = null;
    BaseVideoView mP1;
    BaseVideoView mP2;
    BaseVideoView mP3;
    BaseVideoView mP4;
    BaseVideoView mP5;
    BaseVideoView mP6;
    BaseVideoView mP7;
    BaseVideoView mP8;
    BaseVideoView mP9;
    BaseVideoView mP10;
    BaseVideoView mP11;
    BaseVideoView mP12;
    BaseVideoView mP13;
    BaseVideoView mP14;
    BaseVideoView mP15;
    BaseVideoView mP16;

    private boolean userPause;
    private boolean isLandscape;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_player);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);//设置固定状态栏常驻，不覆盖app布局
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);////设置固定状态栏常驻，覆盖app布局
            getWindow().setStatusBarColor(Color.parseColor("#000000"));//设置状态栏颜色
            hideStausbar(true);
        }
    }

    void init() {
        mP1 = (BaseVideoView)findViewById(R.id.p1);
        mP2 = (BaseVideoView)findViewById(R.id.p2);
        mP3 = (BaseVideoView)findViewById(R.id.p3);
        mP4 = (BaseVideoView)findViewById(R.id.p4);
        mP5 = (BaseVideoView)findViewById(R.id.p5);
        mP6 = (BaseVideoView)findViewById(R.id.p6);
        mP7 = (BaseVideoView)findViewById(R.id.p7);
        mP8 = (BaseVideoView)findViewById(R.id.p8);
        mP9 = (BaseVideoView)findViewById(R.id.p9);
        mP10 = (BaseVideoView)findViewById(R.id.p10);
        mP11 = (BaseVideoView)findViewById(R.id.p11);
        mP12 = (BaseVideoView)findViewById(R.id.p12);
        mP13 = (BaseVideoView)findViewById(R.id.p13);
        mP14 = (BaseVideoView)findViewById(R.id.p14);
        mP15 = (BaseVideoView)findViewById(R.id.p15);
        mP16 = (BaseVideoView)findViewById(R.id.p16);
        mP17 = (BaseVideoView)findViewById(R.id.p17);

        ReceiverGroup receiverGroup = new ReceiverGroup();
        receiverGroup.addReceiver(DataInter.ReceiverKey.KEY_LOADING_COVER, new LoadingCover(this));
        receiverGroup.addReceiver(DataInter.ReceiverKey.KEY_CONTROLLER_COVER, new ControllerCover(this));
        receiverGroup.addReceiver(DataInter.ReceiverKey.KEY_COMPLETE_COVER, new CompleteCover(this));
        receiverGroup.addReceiver(DataInter.ReceiverKey.KEY_ERROR_COVER, new ErrorCover(this));
        mP17.setReceiverGroup(receiverGroup);
        mP17.setOnPlayerEventListener(this);
        mP17.setDataSource(new DataSource("https://mov.bn.netease.com/open-movie/nos/mp4/2016/01/11/SBC46Q9DV_hd.mp4"));
        mP17.start();
        setListenVideoView(mP17);
    }

    void reLayout() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        System.out.println("width-display :" + dm.widthPixels);
        System.out.println("heigth-display :" + dm.heightPixels);

        BaseVideoView topLinearLayout = (BaseVideoView) findViewById(R.id.p1);
        BaseVideoView bottomLinearLayout = (BaseVideoView)findViewById(R.id.p6);
        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams)topLinearLayout.getLayoutParams();
        linearParams.height = (dm.heightPixels )/5;
        topLinearLayout.setLayoutParams(linearParams);
        bottomLinearLayout.setLayoutParams(linearParams);

        BaseVideoView leftLinearLayout = (BaseVideoView) findViewById(R.id.p11);
        BaseVideoView rightLinearLayout = (BaseVideoView)findViewById(R.id.p15);
        linearParams = (LinearLayout.LayoutParams)leftLinearLayout.getLayoutParams();

        linearParams.width = (dm.widthPixels - convertDpToPixel(10))/5;
        leftLinearLayout.setLayoutParams(linearParams);
        rightLinearLayout.setLayoutParams(linearParams);
    }

    private int convertDpToPixel(int dp) {
        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        return (int)(dp*displayMetrics.density);
    }

    private int convertPixelToDp(int pixel) {
        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        return (int)(pixel/displayMetrics.density);
    }

    int getStatusHeight() {
        /**
         * 获取状态栏高度——方法1
         * */
        int statusBarHeight1 = -1;
        //获取status_bar_height资源的ID
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight1 = getResources().getDimensionPixelSize(resourceId);
        }
        Log.e("-------", "状态栏-方法1:" + statusBarHeight1);
        return statusBarHeight1;
    }

    //true隐藏，false显示
    private void hideStausbar(boolean enable) {
        if (enable) {
            WindowManager.LayoutParams attrs = getWindow().getAttributes();
            attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(attrs);
        } else {
            WindowManager.LayoutParams attrs = getWindow().getAttributes();
            attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(attrs);
        }
    }

    void setListenVideoView(BaseVideoView videoView) {

        videoView.setOnPlayerEventListener(new OnPlayerEventListener() {
            @Override
            public void onPlayerEvent(int eventCode, Bundle bundle) {
                //...
            }
        });
        videoView.setOnReceiverEventListener(new OnReceiverEventListener() {
            @Override
            public void onReceiverEvent(int eventCode, Bundle bundle) {
                //...
            }
        });
        videoView.setOnErrorEventListener(new OnErrorEventListener() {
            @Override
            public void onErrorEvent(int eventCode, Bundle bundle) {
                //...
            }
        });
        videoView.setEventHandler(new OnVideoViewEventHandler(){
            @Override
            public void onAssistHandle(BaseVideoView assist, int eventCode, Bundle bundle) {
                super.onAssistHandle(assist, eventCode, bundle);
                switch (eventCode){
                    case InterEvent.CODE_REQUEST_PAUSE:
                        userPause = true;
                        break;
                    case DataInter.Event.EVENT_CODE_REQUEST_BACK:
                        if(isLandscape){
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        }else{
                            finish();
                        }
                        break;
                    case DataInter.Event.EVENT_CODE_REQUEST_TOGGLE_SCREEN:
                        setRequestedOrientation(isLandscape ?
                                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        break;
                    case DataInter.Event.EVENT_CODE_ERROR_SHOW:
                        mP17.stop();
                        break;
                }
            }

        });
    }

    @Override
    protected void onResume() {
        /**
         * 设置为横屏
         */
        if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            init();
            reLayout();
        }

        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mP17 != null)
            mP17.stopPlayback();
    }

    @Override
    public void onPlayerEvent(int eventCode, Bundle bundle) {

    }
}
