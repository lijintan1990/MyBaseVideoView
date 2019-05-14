package com.example.mybasevideoview.view;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.example.mybasevideoview.R;

public class GuideViewUtil {

    private Activity mActivity;

    public GuideViewUtil(Activity activity, int layoutBottom) {
        this.mActivity = activity;
    }


    public void showGuide() {
        if (mActivity == null) {
            return;
        }
        //获取Activity的decorView
        final FrameLayout flRoot = (FrameLayout) mActivity.getWindow().getDecorView();
        final RelativeLayout guideView = (RelativeLayout) LayoutInflater.from(mActivity).inflate(R.layout.layout_decor_view, flRoot, false);
        guideView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击蒙板后去掉该View
                flRoot.removeView(guideView);
            }
        });
        flRoot.addView(guideView);
    }
}