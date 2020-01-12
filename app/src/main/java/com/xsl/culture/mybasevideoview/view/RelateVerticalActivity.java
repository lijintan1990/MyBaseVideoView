package com.xsl.culture.mybasevideoview.view;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.kk.taurus.playerbase.entity.DataSource;
import com.kk.taurus.playerbase.event.EventKey;
import com.kk.taurus.playerbase.event.OnPlayerEventListener;
import com.kk.taurus.playerbase.widget.BaseVideoView;
import com.xsl.culture.mybasevideoview.MainPlayerActivity;
import com.xsl.culture.R;
import com.xsl.culture.mybasevideoview.model.RequestCode;
import com.xsl.culture.mybasevideoview.utils.XslUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RelateVerticalActivity extends Activity {
    private BaseVideoView topVideoView = null;
    private BaseVideoView bottomVideoView = null;
    MainPlayerActivity.RelateVideoInfo relateVideoInfo = null;
    public static final String Relate_key_time = "Relate_ret";
    public static final String Relate_key_play_id = "Relate_play_id";//返回需要在中间播放的id
    public static final String Relate_key_relate_id = "Relate_id";//返回的视频id
    //MainPlayerActivity.RelateVideoInfo relateVideoInfo = null;
    //@BindViews({R.id.back_btn, R.id.top_close, R.id.bottom_close})
    List<Button> buttonList;
    boolean hadSendResult = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_relate_vertical);
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
        topVideoView = findViewById(R.id.topVideoView);
        bottomVideoView = findViewById(R.id.bottomVideoView);
        topVideoView.setDataSource(new DataSource(relateVideoInfo.getUri_1()));
        bottomVideoView.setDataSource(new DataSource(relateVideoInfo.getUri_2()));
        topVideoView.start(relateVideoInfo.getStartTime() * 1000);
        bottomVideoView.start(relateVideoInfo.getStartTime() * 1000);

        OnPlayerEventListener playerEventListener = new OnPlayerEventListener() {
            @Override
            public void onPlayerEvent(int eventCode, Bundle bundle) {
                if (eventCode == PLAYER_EVENT_ON_TIMER_UPDATE) {
                    int time = bundle.getInt(EventKey.INT_ARG2) / 1000 + relateVideoInfo.getStartTime();
                    if (time >= relateVideoInfo.getDuration()) {
                        topVideoView.stopPlayback();
                        bottomVideoView.stopPlayback();
                        Intent intent = new Intent();
                        intent.putExtra(Relate_key_time, time);
                        intent.putExtra(Relate_key_relate_id, relateVideoInfo.getId_2());
                        intent.putExtra(Relate_key_play_id, relateVideoInfo.getId_1());
                        setResult(RequestCode.Relate_req, intent);
                        hadSendResult = true;
                        finish();
                    }
                } else if (eventCode == PLAYER_EVENT_ON_PLAY_COMPLETE) {

                }
            }
        };

        topVideoView.setOnPlayerEventListener(playerEventListener);
    }

    void closeVideo() {
        if (topVideoView != null)
            topVideoView.stopPlayback();
        if (bottomVideoView != null)
            bottomVideoView.stopPlayback();
    }

    Intent intent;
    @OnClick({R.id.bottom_close, R.id.top_close, R.id.back_btn})
    public void onClick(View view) {
        if (view.getId() == R.id.bottom_close) {
            int time = topVideoView.getCurrentPosition();
            intent = new Intent();
            intent.putExtra(Relate_key_time, time);
            intent.putExtra(Relate_key_relate_id, relateVideoInfo.getId_2());
            intent.putExtra(Relate_key_play_id, relateVideoInfo.getId_1());
        } else if (view.getId() == R.id.top_close) {
            int time = bottomVideoView.getCurrentPosition();
            intent = new Intent();
            intent.putExtra(Relate_key_time, time);
            intent.putExtra(Relate_key_relate_id, relateVideoInfo.getId_1());
            intent.putExtra(Relate_key_play_id, relateVideoInfo.getId_2());
        } else if (view.getId() == R.id.back_btn) {
            int time = topVideoView.getCurrentPosition();
            intent = new Intent();
            intent.putExtra(Relate_key_time, time);
            intent.putExtra(Relate_key_relate_id, relateVideoInfo.getId_2());
            intent.putExtra(Relate_key_play_id, relateVideoInfo.getId_1());
        }
        closeVideo();
        hadSendResult = true;
        setResult(RequestCode.Relate_req, intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        if (!hadSendResult)
            setResult(RequestCode.Relate_req, intent);
        super.onDestroy();
    }
}


