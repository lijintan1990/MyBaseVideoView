package com.example.mybasevideoview.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.mybasevideoview.MainActivity;
import com.example.mybasevideoview.MainPlayerActivity;
import com.example.mybasevideoview.R;
import com.example.mybasevideoview.SubFilmActivity;
import com.example.mybasevideoview.model.HomePageInfo;
import com.example.mybasevideoview.utils.XslUtils;
import com.kk.taurus.playerbase.entity.DataSource;
import com.kk.taurus.playerbase.event.EventKey;
import com.kk.taurus.playerbase.event.OnPlayerEventListener;
import com.kk.taurus.playerbase.widget.BaseVideoView;

import java.lang.ref.WeakReference;

import static com.kk.taurus.playerbase.player.IPlayer.STATE_PAUSED;
import static com.kk.taurus.playerbase.player.IPlayer.STATE_STARTED;

public class AppliancesActivity extends Activity {
    private static final String TAG = "AppliancesActivity";
    //进度条相关参数
    BaseVideoView videoView = null;
    ImageView playCtrView = null;
    SeekBar seekBar = null;
    TextView curTimeTextView = null;
    TextView durationTextView = null;

//    private PlayThread playThread = null;

    //底层seek结束,没有seek的情况下是true.一旦检测到seek,
    // 就变成false,false状态下，禁止更新进度条，直到收到底层
    // seek finish回调在设置成true，继续更新进度条
    boolean bNativeSeekFinish = true;

    //视频时间总长度
    int videoDuration;

    private String playUrl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_appliances);

        XslUtils.hideStausbar(new WeakReference<>(this), true);
        Intent intent = getIntent();
        playUrl = (String) intent.getSerializableExtra(String.valueOf(R.string.applience_url));
        if (playUrl != null) {
            init();
            videoPlay();
        }

        findViewById(R.id.close_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoView != null) {
                    videoView.stopPlayback();
                    finish();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
//        if (playThread != null) {
//            playThread.stopRun();
//        }
        super.onDestroy();
    }

    void init() {
        videoView = findViewById(R.id.one);
        playCtrView = findViewById(R.id.player_controller_image_view_play_state);
        seekBar = findViewById(R.id.player_controller_seek_bar);
        curTimeTextView = findViewById(R.id.player_controller_text_view_curr_time);
        durationTextView = findViewById(R.id.player_controller_text_view_total_time);

        playCtrView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoView.getState() == STATE_PAUSED) {
                    videoView.resume();
                    playCtrView.setSelected(false);
                } else if (videoView.getState() == STATE_STARTED) {
                    videoView.pause();
                    playCtrView.setSelected(true);
                }
            }
        });

        seekBar.setMax(1000);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "seekBar start touch");
                bNativeSeekFinish = false;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "seekBar stop touch:"+seekBar.getProgress());
                int sec = seekBar.getProgress() * videoDuration / 1000;
                Log.d(TAG, "realSeek:" + seekBar.getProgress());
                videoView.seekTo(sec);
            }
        });

        OnPlayerEventListener playerEventListener = new OnPlayerEventListener() {
            @Override
            public void onPlayerEvent(int eventCode, Bundle bundle) {
                if (eventCode == PLAYER_EVENT_ON_TIMER_UPDATE)
                    if (bNativeSeekFinish)
                        updateUI(bundle.getInt(EventKey.INT_ARG1), bundle.getInt(EventKey.INT_ARG2));
                if (eventCode == PLAYER_EVENT_ON_SEEK_COMPLETE) {
                    bNativeSeekFinish = true;
                }
            }
        };

        videoView.setOnPlayerEventListener(playerEventListener);
    }

    void updateUI(int curTime, int duration) {
        curTimeTextView.setText(XslUtils.convertSecToTimeString(curTime/1000));
        durationTextView.setText(XslUtils.convertSecToTimeString(duration/1000));
        seekBar.setProgress(curTime * 1000 / duration);
    }

//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case UPDATE_PLAY_TIME:
//                    if (bNativeSeekFinish) {
//                        curTimeTextView.setText(XslUtils.convertSecToTimeString(msg.arg1/1000));
//                        int pos = msg.arg1 * 1000 / videoDuration;
//                        //Log.d(TAG, "pos:"+pos);
//                        seekBar.setProgress(pos);
//                    }
//                    break;
//                case UPDATE_PLAY_DURATION:
//                    videoDuration = msg.arg1;
//                    durationTextView.setText(XslUtils.convertSecToTimeString(msg.arg1/1000));
//                    break;
//            }
//        }
//    };


    void videoPlay() {
        videoView.setDataSource(new DataSource(playUrl));
        videoView.start();

//        playThread = new PlayThread(new WeakReference<>(videoView), new WeakReference<>(handler));
//        playThread.start();
    }

//    private static class PlayThread extends Thread{
//        boolean bExit = false;
//        WeakReference<BaseVideoView> mVideoViewRef;
//        WeakReference<Handler> mHandler;
//        PlayThread(WeakReference<BaseVideoView> videoViewWeakReference, WeakReference<Handler> handlerWeakReference) {
//            mVideoViewRef = videoViewWeakReference;
//            mHandler = handlerWeakReference;
//        }
//
//        @Override
//        public void run() {
//            super.run();
//            int duration = 0;
//            while (!bExit) {
//                BaseVideoView baseVideoView = mVideoViewRef.get();
//                Handler handler = mHandler.get();
//                if (baseVideoView != null
//                        && handler != null
//                        && baseVideoView.getState() == STATE_STARTED) {
//
//                    if (duration == 0) {
//                        duration = baseVideoView.getDuration();
//                        Message msg = handler.obtainMessage();
//                        msg.what = UPDATE_PLAY_DURATION;
//                        msg.arg1 = duration;
//                        handler.sendMessage(msg);
//                    }
//                    int pos = baseVideoView.getCurrentPosition();
//                    Message msg = handler.obtainMessage();
//                    msg.what = UPDATE_PLAY_TIME;
//                    msg.arg1 = pos;
//                    handler.sendMessage(msg);
//                }
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        public void stopRun() {
//            bExit = true;
//        }
//    }
}
