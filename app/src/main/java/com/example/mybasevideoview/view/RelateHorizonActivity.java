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
import com.example.mybasevideoview.model.RequestCode;
import com.example.mybasevideoview.model.VideoListInfo;
import com.example.mybasevideoview.utils.XslUtils;
import com.kk.taurus.playerbase.entity.DataSource;
import com.kk.taurus.playerbase.widget.BaseVideoView;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RelateHorizonActivity extends Activity {
    private BaseVideoView leftVideoView = null;
    private BaseVideoView rightVideoView = null;
//    MainPlayerActivity.RelateVideoInfo relateVideoInfo = null;
    @BindViews({R.id.back_btn, R.id.left_close, R.id.right_close})
    List<Button> buttonList;

    int videoIndex1;
    int videoIndex2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_relate_horizon);
        ButterKnife.bind(this);
        XslUtils.hideStausbar(new WeakReference<>(this), true);
        Intent intent = getIntent();
        videoIndex1 = intent.getIntExtra(MainPlayerActivity.RELATE_ID_ONE, 0);
        videoIndex2 = intent.getIntExtra(MainPlayerActivity.RELATE_ID_TWO, 0);
//        relateVideoInfo = (MainPlayerActivity.RelateVideoInfo)intent.getSerializableExtra(MainPlayerActivity.sRelateInfo);
        playVideos();
    }

    void playVideos() {
        VideoListInfo videoListInfo = MainPlayerActivity.getVideoLstInfo();
        leftVideoView = findViewById(R.id.leftVideoView);
        rightVideoView = findViewById(R.id.rightVideoView);
        leftVideoView.setDataSource(new DataSource(videoListInfo.getData().get(videoIndex1).getVideoUrl360()));
        rightVideoView.setDataSource(new DataSource(videoListInfo.getData().get(videoIndex2).getVideoUrl360()));
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
            setResult(RequestCode.Relate_req);
        } else if (view.getId() == R.id.left_close) {
            closeVideo();
            setResult(RequestCode.Relate_req);
        } else if (view.getId() == R.id.back_btn) {
            closeVideo();
        }
        finish();
    }
}
