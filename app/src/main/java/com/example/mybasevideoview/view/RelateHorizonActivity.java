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
import com.kk.taurus.playerbase.event.EventKey;
import com.kk.taurus.playerbase.event.OnPlayerEventListener;
import com.kk.taurus.playerbase.widget.BaseVideoView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.mybasevideoview.view.RelateVerticalActivity.Relate_key_time;
import static com.example.mybasevideoview.view.RelateVerticalActivity.Relate_key_ret_id;

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
        ButterKnife.bind(this);
        XslUtils.hideStausbar(new WeakReference<>(this), true);
        Intent intent = getIntent();
        relateVideoInfo = (MainPlayerActivity.RelateVideoInfo)intent.getSerializableExtra(MainPlayerActivity.RELATE_INFO);
        playVideos();
    }

    void playVideos() {
        ArrayList<String> videoUrls = MainPlayerActivity.smallVideoUrls;
        if (videoUrls == null)
            return;
        leftVideoView = findViewById(R.id.leftVideoView);
        rightVideoView = findViewById(R.id.rightVideoView);
        leftVideoView.setDataSource(new DataSource(relateVideoInfo.getUri_1()));
        rightVideoView.setDataSource(new DataSource(relateVideoInfo.getUri_2()));
        leftVideoView.start(relateVideoInfo.getStartTime() * 1000);
        rightVideoView.start(relateVideoInfo.getStartTime() * 1000);

        OnPlayerEventListener playerEventListener = new OnPlayerEventListener() {
            @Override
            public void onPlayerEvent(int eventCode, Bundle bundle) {
                if (eventCode == PLAYER_EVENT_ON_TIMER_UPDATE) {
                    int time = bundle.getInt(EventKey.INT_ARG1) / 1000 ;
                    if (time >= (relateVideoInfo.getDuration() + relateVideoInfo.getStartTime())) {
                        leftVideoView.stopPlayback();
                        rightVideoView.stopPlayback();
                        Intent intent = new Intent();
                        intent.putExtra(Relate_key_time, time);
                        setResult(RequestCode.Relate_req, intent);
                        finish();
                    }
                } else if (eventCode == PLAYER_EVENT_ON_PLAY_COMPLETE) {

                }
            }
        };

        leftVideoView.setOnPlayerEventListener(playerEventListener);
    }

    void closeVideo() {
        if (leftVideoView != null)
            leftVideoView.stopPlayback();
        if (rightVideoView != null)
            rightVideoView.stopPlayback();
    }

    Intent intent;
    @OnClick({R.id.right_close, R.id.left_close, R.id.back_btn})
    public void onClick(View view) {
        if (view.getId() == R.id.right_close) {
            int time = leftVideoView.getCurrentPosition() / 1000;
            intent = new Intent();
            intent.putExtra(Relate_key_time, time);
            intent.putExtra(Relate_key_ret_id, -1);
        } else if (view.getId() == R.id.left_close) {
            int time = leftVideoView.getCurrentPosition() / 1000;
            intent = new Intent();
            intent.putExtra(Relate_key_time, time);
            intent.putExtra(Relate_key_ret_id, relateVideoInfo.getId_2());
        } else if (view.getId() == R.id.back_btn) {
            int time = leftVideoView.getCurrentPosition() / 1000;
            intent = new Intent();
            intent.putExtra(Relate_key_time, time);
            intent.putExtra(Relate_key_ret_id, -1);
        }

        closeVideo();
        finish();
    }

    @Override
    protected void onDestroy() {
        setResult(RequestCode.Relate_req, intent);
        super.onDestroy();
    }
}
