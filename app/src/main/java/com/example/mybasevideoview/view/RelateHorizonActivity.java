package com.example.mybasevideoview.view;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.example.mybasevideoview.MainPlayerActivity;
import com.example.mybasevideoview.R;
import com.example.mybasevideoview.utils.XslUtils;
import com.kk.taurus.playerbase.entity.DataSource;
import com.kk.taurus.playerbase.widget.BaseVideoView;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindViews;
import butterknife.OnClick;

public class RelateHorizonActivity extends Activity {
    private BaseVideoView leftVideoView = null;
    private BaseVideoView rightVideoView = null;
    MainPlayerActivity.RelateVideoInfo relateVideoInfo = null;
    @BindViews({R.id.back_btn, R.id.left_close, R.id.right_close})
    List<Button> buttonList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_relate_horizon);
        XslUtils.hideStausbar(new WeakReference<>(this), true);
        Intent intent = getIntent();
        relateVideoInfo = (MainPlayerActivity.RelateVideoInfo)intent.getSerializableExtra(MainPlayerActivity.sRelateInfo);
        playVideos();
    }

    void playVideos() {
        leftVideoView = findViewById(R.id.leftVideoView);
        rightVideoView = findViewById(R.id.rightVideoView);
        leftVideoView.setDataSource(new DataSource(relateVideoInfo.getUri_1()));
        rightVideoView.setDataSource(new DataSource(relateVideoInfo.getUri_2()));
        leftVideoView.start();
        rightVideoView.start();
    }

    void closeVideo() {
        if (leftVideoView != null)
            leftVideoView.stopPlayback();
        if (rightVideoView != null)
            rightVideoView.stopPlayback();
    }

    @OnClick({R.id.right_close, R.id.left_close, R.id.back_btn})
    public void onClick(View view) {
        if (view.getId() == R.id.right_close) {
            closeVideo();
            setResult(MainPlayerActivity.ACT_VIDEO_RELATE);
        } else if (view.getId() == R.id.left_close) {
            closeVideo();
            setResult(MainPlayerActivity.ACT_VIDEO_RELATE);
        } else if (view.getId() == R.id.back_btn) {
            closeVideo();
        }
        finish();
    }
}
