package com.example.mybasevideoview;

import android.app.ActionBar;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.kk.taurus.playerbase.widget.BaseVideoView;

public class MainPlayerActivity extends Activity {
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

    void reLayout() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        System.out.println("width-display :" + dm.widthPixels);
        System.out.println("heigth-display :" + dm.heightPixels);
//        int dpW = convertPixelToDp(dm.widthPixels);
//        int dpH = convertPixelToDp(dm.heightPixels);

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

    //pixel = dip*density;
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

    @Override
    protected void onResume() {
        /**
         * 设置为横屏
         */
        if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        reLayout();
        super.onResume();
    }
}
