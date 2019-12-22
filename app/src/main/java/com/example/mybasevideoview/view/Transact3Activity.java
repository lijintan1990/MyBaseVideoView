package com.example.mybasevideoview.view;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.mybasevideoview.R;
import com.example.mybasevideoview.model.RequestCode;
import com.example.mybasevideoview.utils.XslUtils;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Transact3Activity extends Activity {
    private int x, y, width, height;
    @BindView(R.id.center_view)
    RelativeLayout centerView;
    @BindView(R.id.next_3)
    Button nextBtn;
    @BindView(R.id.back_btn)
    Button backBtn;
    @BindView(R.id.controller)
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_transact3);
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
    private MySeekBar mySeekBar;

    void initUI() {
        RelativeLayout.LayoutParams linearLayoutLayoutParams = (RelativeLayout.LayoutParams) linearLayout.getLayoutParams();
        linearLayoutLayoutParams.topMargin = 0;
        linearLayoutLayoutParams.leftMargin = x - width / 3;
        linearLayoutLayoutParams.width = width / 3 * 5;
        linearLayout.setLayoutParams(linearLayoutLayoutParams);

        mySeekBar = findViewById(R.id.main_controller_seek_bar);
        mySeekBar.setMax(1000);


        //添加中间框
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) centerView.getLayoutParams();
        params.leftMargin = x;
        params.topMargin = y;
        params.width = width;
        params.height = height;
        centerView.setLayoutParams(params);


        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) nextBtn.getLayoutParams();
        layoutParams.topMargin = convertDpToPixel(15) + height + params.topMargin;
        layoutParams.leftMargin = x + (width - layoutParams.width) / 2;
        nextBtn.setLayoutParams(layoutParams);
    }

    @OnClick(R.id.next_3)
    void nextClick(View view) {
        finish();
        setResult(RequestCode.Transact3_req);
    }

    @OnClick(R.id.back_btn)
    void backClick(View view) {
        finish();
        setResult(RequestCode.Transact3_req);
    }
    private int convertDpToPixel(int dp) {
        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        return (int)(dp * displayMetrics.density);
    }
}
