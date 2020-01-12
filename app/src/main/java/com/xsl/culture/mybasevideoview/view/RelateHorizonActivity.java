package com.xsl.culture.mybasevideoview.view;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import static com.xsl.culture.mybasevideoview.view.RelateVerticalActivity.Relate_key_play_id;
import static com.xsl.culture.mybasevideoview.view.RelateVerticalActivity.Relate_key_relate_id;
import static com.xsl.culture.mybasevideoview.view.RelateVerticalActivity.Relate_key_time;


public class RelateHorizonActivity extends Activity {
    private BaseVideoView leftVideoView = null;
    private BaseVideoView rightVideoView = null;
    MainPlayerActivity.RelateVideoInfo relateVideoInfo = null;
    @BindViews({R.id.back_btn, R.id.left_close, R.id.right_close})
    List<Button> buttonList;
    boolean hadSendResult = false;

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
                    int time = bundle.getInt(EventKey.INT_ARG1) ;
                    if (time >= (relateVideoInfo.getDuration() + relateVideoInfo.getStartTime()) * 1000) {
                        leftVideoView.stopPlayback();
                        rightVideoView.stopPlayback();
                        intent = new Intent();
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
            int time = leftVideoView.getCurrentPosition();
            intent = new Intent();
            intent.putExtra(Relate_key_time, time);
            intent.putExtra(Relate_key_relate_id, relateVideoInfo.getId_2());
            intent.putExtra(Relate_key_play_id, relateVideoInfo.getId_1());
        } else if (view.getId() == R.id.left_close) {
            int time = leftVideoView.getCurrentPosition();
            intent = new Intent();
            intent.putExtra(Relate_key_time, time);
            intent.putExtra(Relate_key_relate_id, relateVideoInfo.getId_1());
            intent.putExtra(Relate_key_play_id, relateVideoInfo.getId_2());
        } else if (view.getId() == R.id.back_btn) {
            int time = leftVideoView.getCurrentPosition();
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
