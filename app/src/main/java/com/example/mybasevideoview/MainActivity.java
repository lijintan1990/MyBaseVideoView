package com.example.mybasevideoview;

//import android.support.v4.app.Fragment;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mybasevideoview.model.HomePageInfo;
import com.example.mybasevideoview.model.ObtainNetWorkData;
import com.example.mybasevideoview.utils.NetworkCheck;
import com.example.mybasevideoview.utils.XslUtils;
import com.example.mybasevideoview.utils.ZipUtils;
import com.kk.taurus.playerbase.entity.DataSource;
import com.kk.taurus.playerbase.event.OnPlayerEventListener;
import com.kk.taurus.playerbase.widget.BaseVideoView;
//import android.widget.Button;
//
//import com.example.mybasevideoview.view.FirstPaperFragment;
//import com.example.mybasevideoview.view.XslAboutFragmet;
//import com.example.mybasevideoview.view.XslMainFragment;
//import com.next.easynavigation.view.EasyNavigationBar;
//
//import java.util.ArrayList;
//import java.util.List;
//
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.security.Permission;

import butterknife.ButterKnife;
import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.kk.taurus.playerbase.player.IPlayer.STATE_PAUSED;
import static com.kk.taurus.playerbase.player.IPlayer.STATE_STARTED;


public class MainActivity extends AppCompatActivity {
//    private String[] tabText = {"首页", "乡射礼", "关于"};
//    //未选中icon
//    private int[] normalIcon = {R.mipmap.index, R.mipmap.find, R.mipmap.message};
//    //选中时icon
//    private int[] selectIcon = {R.mipmap.index1, R.mipmap.find1, R.mipmap.message1};
//
//    private List<Fragment> fragments = new ArrayList<>();
    private static final String TAG = "MainActivity";
    BaseVideoView videoView = null;
    FrameLayout subVideoView = null;
    FrameLayout wholeVideoView = null;
    HomePageInfo pageInfo = null;
    ImageView playCtrView = null;
    SeekBar seekBar = null;
    TextView curTimeTextView = null;
    TextView durationTextView = null;

    ImageButton playBtn = null;

    //更新seekBar时间进度线程
    PlayThread playThread = null;
    //wifi是否打开
    boolean bWifiOpen = false;
    //能否使用流量播放
    boolean bCanUseNetflow = false;
    boolean bPermissionCheck = true;
    //首页视频时间总长度
    int videoDuration;

    //底层seek结束,没有seek的情况下是true.一旦检测到seek,
    // 就变成false,false状态下，禁止更新进度条，直到收到底层
    // seek finish回调在设置成true，继续更新进度条
    boolean bNativeSeekFinish = true;

    private static final int UPDATE_VIDEO_THUMBNAIL = 1;
    private static final int UPDATE_SUB_MOVIE_THUMBNAIL = 2;
    private static final int UPDATE_WHOLE_MOVIE_THUMBNAIL = 3;
    private static final int UPDATE_PLAY_TIME = 4;
    private static final int UPDATE_PLAY_DURATION = 5;

    View wifiNoticeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        XslUtils.hideStausbar(new WeakReference<>(this), true);

        Log.d(TAG, "main thread id:"+Thread.currentThread().getId());
        init();

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            permissionCheck();
//            //6.0以上手机权限给予之后在初始化这些图片
//        } else {
            setImageViewBmp();
        //}

        playBtn = findViewById(R.id.play_btn);
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //播放视频
                videoPlay();
                playBtn.setVisibility(View.INVISIBLE);
            }
        });
    }

    void setImageViewBmp() {
        ObtainNetWorkData.getHomeData(new Callback<HomePageInfo>() {
            @Override
            public void onResponse(Call<HomePageInfo> call, Response<HomePageInfo> response) {
                Log.d(TAG, "get homepage data success");
                pageInfo = response.body();
                Log.d(TAG, "onResponse thread id:"+Thread.currentThread().getId());

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //这里放到线程里面做是因为updateImageView里面还有一个网络请求，防止阻塞
                        updateImageView(UPDATE_VIDEO_THUMBNAIL, pageInfo.getData().getIndex().getThumbnailUrl());
                        updateImageView(UPDATE_SUB_MOVIE_THUMBNAIL, pageInfo.getData().getExcerpts().getThumbnailUrl());
                        updateImageView(UPDATE_WHOLE_MOVIE_THUMBNAIL, pageInfo.getData().getFull().getThumbnailUrl());
                    }
                });
                thread.start();
                //播放视频
                openVideo();
            }

            @Override
            public void onFailure(Call<HomePageInfo> call, Throwable t) {
                Log.w(TAG, "get homepage failed, "+t.toString());
            }
        });
    }

    void updateImageView(int id, String uri) {
        Message message = handler.obtainMessage();
        message.what = id;

        try {
            message.obj = ObtainNetWorkData.getBitmap(uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        handler.sendMessage(message);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_VIDEO_THUMBNAIL:
                    Log.d(TAG, "set ImageView background");
                    ImageView imageView = findViewById(R.id.videoImageView);
                    //imageView.setImageBitmap((Bitmap)msg.obj);
                    imageView.setBackground(new BitmapDrawable((Bitmap)msg.obj));
                    break;
                case UPDATE_SUB_MOVIE_THUMBNAIL:
                    ImageView subImageView = findViewById(R.id.subImageView);
                    subImageView.setBackground(new BitmapDrawable((Bitmap)msg.obj));
                    break;
                case UPDATE_WHOLE_MOVIE_THUMBNAIL:
                    ImageView wholeImageView = findViewById(R.id.wholeImageView);
                    wholeImageView.setBackground(new BitmapDrawable((Bitmap)msg.obj));
                    break;
                case UPDATE_PLAY_TIME:
                    if (bNativeSeekFinish) {
                        curTimeTextView.setText(XslUtils.convertSecToTimeString(msg.arg1/1000));
                        int pos = msg.arg1 * 1000 / videoDuration;
                        //Log.d(TAG, "pos:"+pos);
                        seekBar.setProgress(pos);
                    }
                    //18098953191
                    break;
                case UPDATE_PLAY_DURATION:
                    videoDuration = msg.arg1;
                    durationTextView.setText(XslUtils.convertSecToTimeString(msg.arg1/1000));
                    break;
            }
        }
    };

    void openVideo() {
        if (!bPermissionCheck) {
            return;
        }

        if (!NetworkCheck.isWiFiActive(MainActivity.this)) {
//            new  AlertDialog.Builder(MainActivity.this)
//                    .setTitle("网络提示" )
//                    .setMessage("Wifi无链接，使用流量进行播放？" )
//                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            Toast.makeText(MainActivity.this, "使用流量进行播放", Toast.LENGTH_LONG).show();
//                        }
//                    })
//                    .setNegativeButton("否", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            Toast.makeText(MainActivity.this, "拒绝流量进行播放", Toast.LENGTH_LONG).show();
//                        }
//                    })
//                    .show();
            wifiNoticeView = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_wifi_notice, videoView, false);
            videoView.addView(wifiNoticeView);
            Button playContinueBtn = findViewById(R.id.play_continue_btn);
            playContinueBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    videoView.removeView(wifiNoticeView);
                }
            });
        }
    }

    void videoPlay() {
        if (pageInfo != null) {
            videoView.setDataSource(new DataSource(pageInfo.getData().getIndex().getVideoUrl()));
            videoView.start();
            ImageView imageView = findViewById(R.id.videoImageView);
            imageView.setVisibility(View.GONE);
            playThread = new PlayThread(new WeakReference<>(videoView), new WeakReference<>(handler));
            playThread.start();
        }
    }

    private static class PlayThread extends Thread{
        boolean bExit = false;
        WeakReference<BaseVideoView> mVideoViewRef;
        WeakReference<Handler> mHandler;
        PlayThread(WeakReference<BaseVideoView> videoViewWeakReference, WeakReference<Handler> handlerWeakReference) {
            mVideoViewRef = videoViewWeakReference;
            mHandler = handlerWeakReference;
        }

        @Override
        public void run() {
            super.run();
            int duration = 0;
            while (!bExit) {
                BaseVideoView baseVideoView = mVideoViewRef.get();
                Handler handler = mHandler.get();
                if (baseVideoView != null
                        && handler != null
                        && baseVideoView.getState() == STATE_STARTED) {

                    if (duration == 0) {
                        duration = baseVideoView.getDuration();
                        Message msg = handler.obtainMessage();
                        msg.what = UPDATE_PLAY_DURATION;
                        msg.arg1 = duration;
                        handler.sendMessage(msg);
                    }
                    int pos = baseVideoView.getCurrentPosition();
                    Message msg = handler.obtainMessage();
                    msg.what = UPDATE_PLAY_TIME;
                    msg.arg1 = pos;
                    handler.sendMessage(msg);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void stopRun() {
            bExit = true;
        }
    }

    //在荣耀手机上面似乎默认就有这些权限，都不用申请样的
    void permissionCheck() {
//        new Handler(Looper.getMainLooper())
//                .postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        PermissionGen.with(MainActivity.this)
//                                .addRequestCode(100)
//                                .permissions(
//                                        Manifest.permission.ACCESS_WIFI_STATE,
//                                        Manifest.permission.ACCESS_NETWORK_STATE,
//                                        Manifest.permission.READ_EXTERNAL_STORAGE,
//                                        Manifest.permission.WRITE_EXTERNAL_STORAGE
//                                );
//                    }
//                }, 300);
        PermissionGen.with(MainActivity.this)
                .addRequestCode(100)
                .permissions(
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).request();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "request permission");
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @PermissionSuccess(requestCode = 100)
    public void permissionSuccess() {
        Toast.makeText(this, "权限申请成功", Toast.LENGTH_LONG).show();
        Log.d(TAG, "0000000000000");
        bPermissionCheck = true;
        setImageViewBmp();
    }

    @PermissionFail(requestCode = 100)
    public void permissionFailure() {
        Toast.makeText(this, "权限拒绝,无法正常使用", Toast.LENGTH_LONG).show();
        Log.d(TAG, "xxxxxxxxxxxx");
        bPermissionCheck = false;
    }

    void unZipRes() {
        File outDir = getExternalFilesDir("");
        XslUtils.deleteFile(new File(outDir.getAbsolutePath() + "/defaultData"));

        AssetManager assetManager = getResources().getAssets();
        try {
            InputStream inputStream = assetManager.open("defaultData.zip");
            ZipUtils.UnZipFolder(inputStream, outDir.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void init() {
        unZipRes();
        videoView = findViewById(R.id.one);
        subVideoView = findViewById(R.id.subMovie);
        wholeVideoView = findViewById(R.id.wholeMovie);
        playCtrView = findViewById(R.id.player_controller_image_view_play_state);
        seekBar = findViewById(R.id.player_controller_seek_bar);
        curTimeTextView = findViewById(R.id.player_controller_text_view_curr_time);
        durationTextView = findViewById(R.id.player_controller_text_view_total_time);

        subVideoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = null;
                if (pageInfo != null && pageInfo.getStatus() == 0) {
                    url = pageInfo.getData().getExcerpts().getVideoUrl();
                    if (url != null && url != "") {
                        Intent intent = new Intent(MainActivity.this, SubFilmActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(String.valueOf(R.string.subFile_url), url);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }
            }
        });

        wholeVideoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainPlayerActivity.class);
                startActivity(intent);
            }
        });

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
                    return;
                if (eventCode == PLAYER_EVENT_ON_SEEK_COMPLETE) {
                    bNativeSeekFinish = true;
                } else if (eventCode == PLAYER_EVENT_ON_PLAY_COMPLETE) {
                    playBtn.setVisibility(View.VISIBLE);
                }
//                else if (eventCode == PLAYER_EVENT_ON_SEEK_TO) {
//                    bNativeSeekFinish = false;
//                }
            }
        };

        videoView.setOnPlayerEventListener(playerEventListener);
    }


    @Override
    protected void onResume() {
        super.onResume();
        videoView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoView.pause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //videoView.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoView.stopPlayback();

        try {
            if (playThread != null)
                playThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
