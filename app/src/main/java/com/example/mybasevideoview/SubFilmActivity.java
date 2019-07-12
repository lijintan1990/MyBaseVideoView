package com.example.mybasevideoview;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.mybasevideoview.model.RequestCode;
import com.example.mybasevideoview.utils.XslUtils;
import com.example.mybasevideoview.view.AboutActivity;
import com.example.mybasevideoview.view.langugueActivity;
import com.kk.taurus.playerbase.entity.DataSource;
import com.kk.taurus.playerbase.event.EventKey;
import com.kk.taurus.playerbase.event.OnPlayerEventListener;
import com.kk.taurus.playerbase.widget.BaseVideoView;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.kk.taurus.playerbase.player.IPlayer.STATE_PAUSED;
import static com.kk.taurus.playerbase.player.IPlayer.STATE_PLAYBACK_COMPLETE;
import static com.kk.taurus.playerbase.player.IPlayer.STATE_STARTED;

public class SubFilmActivity extends AppCompatActivity {
    private static final String TAG = "SubFileActivity";
    //进度条相关参数
    BaseVideoView videoView = null;
    ImageView playCtrView = null;
    SeekBar seekBar = null;
    TextView curTimeTextView = null;
    TextView durationTextView = null;

    //底层seek结束,没有seek的情况下是true.一旦检测到seek,
    // 就变成false,false状态下，禁止更新进度条，直到收到底层
    // seek finish回调在设置成true，继续更新进度条
    boolean bNativeSeekFinish = true;
    private String playUrl = null;

    @BindViews({R.id.about_btn, R.id.langugue_btn})
    List<Button> buttonList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_sub_film);
        ButterKnife.bind(this);
        XslUtils.hideStausbar(new WeakReference<>(this), true);
        Intent intent = getIntent();
        playUrl = (String) intent.getSerializableExtra(String.valueOf(R.string.subFile_url));
        if (playUrl != null) {
            init();
            videoPlay();
        }

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoView != null) {
                    videoView.stopPlayback();
                }
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        Intent intent = new Intent();
        intent.putExtra(RequestCode.Key_ret_code, RequestCode.Appliance_req);
        setResult(RESULT_OK, intent);
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
                } else if (videoView.getState() == STATE_PLAYBACK_COMPLETE) {
                    videoView.start();
                    playCtrView.setSelected(false);
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
                int sec = seekBar.getProgress() * videoView.getDuration() / 1000;
                Log.d(TAG, "seek time:" + sec);
                videoView.seekTo(sec);
            }
        });

        OnPlayerEventListener playerEventListener = new OnPlayerEventListener() {
            @Override
            public void onPlayerEvent(int eventCode, Bundle bundle) {
                if (eventCode == PLAYER_EVENT_ON_TIMER_UPDATE) {
                    if (bNativeSeekFinish) {
                        updateUI(bundle.getInt(EventKey.INT_ARG1), bundle.getInt(EventKey.INT_ARG2));
                        long pos = videoView.getCurrentPosition();
                        Log.d(TAG, "get pos:" + pos);
                    }
                } else if (eventCode == PLAYER_EVENT_ON_SEEK_COMPLETE) {
                    Log.d(TAG, "after seek, get pos:" + videoView.getCurrentPosition());
                    bNativeSeekFinish = true;
                } else if (eventCode == PLAYER_EVENT_ON_PLAY_COMPLETE) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            playCtrView.setSelected(false);
                            seekBar.setProgress(0);
                            curTimeTextView.setText("00:00:00");
                        }
                    });
                }
            }
        };

        videoView.setOnPlayerEventListener(playerEventListener);
    }

    @OnClick({R.id.about_btn, R.id.langugue_btn})
    void buttonClick(View view) {
        switch (view.getId()) {
            case R.id.about_btn:
                if (buttonList.get(0).isSelected()) {
                    buttonList.get(0).setSelected(false);
                } else {
                    buttonList.get(0).setSelected(true);
                }
                createActivity(AboutActivity.class, RequestCode.About_req);
                break;
            case R.id.langugue_btn:
                if (buttonList.get(1).isSelected()) {
                    buttonList.get(1).setSelected(false);
                } else {
                    buttonList.get(1).setSelected(true);
                }
                createActivity(langugueActivity.class, RequestCode.Languge_req);
                break;
        }
    }

    public void createActivity(Class<?> cls, int requestCode) {
        Intent intent = new Intent(SubFilmActivity.this, cls);
        startActivityForResult(intent, requestCode);
    }

    void updateUI(int curTime, int duration) {
        curTimeTextView.setText(XslUtils.convertSecToTimeString(curTime/1000));
        durationTextView.setText(XslUtils.convertSecToTimeString(duration/1000));
        seekBar.setProgress(curTime * 1000 / duration);
    }

    void videoPlay() {
        videoView.setDataSource(new DataSource(playUrl));
        videoView.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
         if (requestCode == RequestCode.About_req) {
            buttonList.get(0).setSelected(false);
        }  else if (requestCode == RequestCode.Languge_req) {
            int langugueSelector = 0;
            Bundle bd = data.getExtras();
            langugueSelector = bd.getInt(langugueActivity.langugue_key);
            buttonList.get(1).setSelected(false);
            switch (langugueSelector) {
                case langugueActivity.chinese:
                    break;
                case langugueActivity.cantonese:
                    break;
                case langugueActivity.english:
                    break;
            }
        }
    }
}
