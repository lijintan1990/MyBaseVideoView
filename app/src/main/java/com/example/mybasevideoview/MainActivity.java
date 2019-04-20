package com.example.mybasevideoview;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.kk.taurus.playerbase.assist.OnVideoViewEventHandler;
import com.kk.taurus.playerbase.config.PlayerConfig;
import com.kk.taurus.playerbase.entity.DataSource;
import com.kk.taurus.playerbase.event.OnErrorEventListener;
import com.kk.taurus.playerbase.event.OnPlayerEventListener;
import com.kk.taurus.playerbase.receiver.OnReceiverEventListener;
import com.kk.taurus.playerbase.widget.BaseVideoView;
import com.squareup.haha.perflib.Main;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    BaseVideoView videoView1;
    BaseVideoView videoView2;
    BaseVideoView videoView3;
    BaseVideoView videoView4;
    Button mStartBtn;
    Button mStopBtn;
    Button mStartNewAtyBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        setListenVideoView(videoView1);
        setListenVideoView(videoView2);
        setListenVideoView(videoView3);
        setListenVideoView(videoView4);

        PlayerConfig.setDefaultPlanId(CustomApp.PLAN_ID_EXO);
    }

    void initUI() {
        videoView1 = (BaseVideoView)findViewById(R.id.videoView1);
        videoView2 = (BaseVideoView)findViewById(R.id.videoView2);
        videoView3 = (BaseVideoView)findViewById(R.id.videoView3);
        videoView4 = (BaseVideoView)findViewById(R.id.videoView4);
        mStartBtn = (Button)findViewById(R.id.start_btn);
        mStopBtn = (Button)findViewById(R.id.stop_btn);
        mStartNewAtyBtn = (Button)findViewById(R.id.newActivity);
        mStopBtn.setOnClickListener(this);
        mStartBtn.setOnClickListener(this);
        mStartNewAtyBtn.setOnClickListener(this);
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
                //...
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_btn:
                startPlay();
                break;
            case R.id.stop_btn:
                stopPlay();
                break;
            case R.id.newActivity:
                Intent intent = new Intent(MainActivity.this, MainPlayerActivity.class);
                startActivity(intent);
                break;
        }
    }

    void startPlay() {
        videoView1.setDataSource(new DataSource("https://mov.bn.netease.com/open-movie/nos/mp4/2016/01/11/SBC46Q9DV_hd.mp4"));
        videoView2.setDataSource(new DataSource("https://mov.bn.netease.com/open-movie/nos/mp4/2016/01/11/SBC46Q9DV_hd.mp4"));
        videoView3.setDataSource(new DataSource("https://mov.bn.netease.com/open-movie/nos/mp4/2016/01/11/SBC46Q9DV_hd.mp4"));
        videoView4.setDataSource(new DataSource("https://mov.bn.netease.com/open-movie/nos/mp4/2016/01/11/SBC46Q9DV_hd.mp4"));
        videoView1.start();
        videoView2.start();
        videoView3.start();
        videoView4.start();
    }

    void stopPlay() {
        videoView1.stop();
        videoView2.stop();
        videoView3.stop();
        videoView4.stop();
    }
}
