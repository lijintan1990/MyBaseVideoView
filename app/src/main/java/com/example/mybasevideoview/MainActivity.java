package com.example.mybasevideoview;

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



public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    BaseVideoView videoView1;
    Button mStartBtn;
    Button mStopBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        setListenVideoView();

        PlayerConfig.setDefaultPlanId(CustomApp.PLAN_ID_IJK);
    }

    void initUI() {
        videoView1 = (BaseVideoView)findViewById(R.id.videoView1);
        mStartBtn = (Button)findViewById(R.id.start_btn);
        mStopBtn = (Button)findViewById(R.id.stop_btn);
        mStopBtn.setOnClickListener(this);
        mStartBtn.setOnClickListener(this);
    }

    void setListenVideoView() {

        videoView1.setOnPlayerEventListener(new OnPlayerEventListener() {
            @Override
            public void onPlayerEvent(int eventCode, Bundle bundle) {
                //...
            }
        });
        videoView1.setOnReceiverEventListener(new OnReceiverEventListener() {
            @Override
            public void onReceiverEvent(int eventCode, Bundle bundle) {
                //...
            }
        });
        videoView1.setOnErrorEventListener(new OnErrorEventListener() {
            @Override
            public void onErrorEvent(int eventCode, Bundle bundle) {
                //...
            }
        });
        videoView1.setEventHandler(new OnVideoViewEventHandler(){
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

        }
    }

    void startPlay() {
        videoView1.setDataSource(new DataSource("https://mov.bn.netease.com/open-movie/nos/mp4/2016/01/11/SBC46Q9DV_hd.mp4"));
        videoView1.start();
    }

    void stopPlay() {
        videoView1.stop();
    }
}
