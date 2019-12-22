package com.example.mybasevideoview.view;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.mybasevideoview.R;
import com.example.mybasevideoview.model.RequestCode;
import com.example.mybasevideoview.utils.XslUtils;

import java.lang.ref.WeakReference;
import java.util.zip.Inflater;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Transact2Activity extends Activity {

    private int x, y, width, height;
    @BindView(R.id.father)
    RelativeLayout fatherLayout;
    @BindView(R.id.center_view)
    View centerView;
    @BindView(R.id.small_view)
    View smallView;
    @BindView(R.id.next_2)
    Button nextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_transact2);
        XslUtils.hideStausbar(new WeakReference<>(this), true);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        //横屏需要反过来
        x = intent.getIntExtra(String.valueOf(R.string.center_play_x), 0);
        y = intent.getIntExtra(String.valueOf(R.string.center_play_y), 0);
        height = intent.getIntExtra(String.valueOf(R.string.center_play_height), 0);
        width = intent.getIntExtra(String.valueOf(R.string.center_play_width), 0);
        Log.d("tips", "centerX:" + x + " centerY:" + y + " width:" + width + "  height:" + height);

        initUI();
    }

    void initUI() {
        //添加中间框
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) centerView.getLayoutParams();
        params.leftMargin = x;
        params.topMargin = y;
        params.width = width;
        params.height = height;
        centerView.setLayoutParams(params);

        //进度条24dp
        int smallPlayY = convertDpToPixel(24);
        int smallPlayX = x + width / 3;
        int smallPlayWidth = width / 3;
        int smallPlayHeight = height / 3;
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) smallView.getLayoutParams();
        lp.leftMargin = smallPlayX;
        lp.topMargin = smallPlayY;
        lp.width = smallPlayWidth;
        lp.height = smallPlayHeight;
        Log.d("xxx", "position info" + lp.leftMargin + " " + lp.topMargin + " " + lp.width + " "+ lp.height);
        smallView.setLayoutParams(lp);

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) nextBtn.getLayoutParams();
        layoutParams.topMargin = convertDpToPixel(15) + height + params.topMargin;
        layoutParams.leftMargin = lp.leftMargin + (lp.width - layoutParams.width) / 2;
        nextBtn.setLayoutParams(layoutParams);
    }

    @OnClick(R.id.next_2)
    void nextClick(View view) {
        setResult(RequestCode.Transact2_req);
        finish();
    }
    private int convertDpToPixel(int dp) {
        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        return (int)(dp * displayMetrics.density);
    }
}


