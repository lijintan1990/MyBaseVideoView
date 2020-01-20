package com.xsl.culture.mybasevideoview;

//import android.support.v4.app.Fragment;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
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
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kk.taurus.playerbase.entity.DataSource;
import com.kk.taurus.playerbase.event.OnPlayerEventListener;
import com.kk.taurus.playerbase.player.IPlayer;
import com.kk.taurus.playerbase.widget.BaseVideoView;
import com.xsl.culture.R;
import com.xsl.culture.mybasevideoview.controller.NetworkReq;
import com.xsl.culture.mybasevideoview.model.HomePageInfo;
import com.xsl.culture.mybasevideoview.model.ObtainNetWorkData;
import com.xsl.culture.mybasevideoview.model.RequestCode;
import com.xsl.culture.mybasevideoview.model.SharedPreferenceUtil;
import com.xsl.culture.mybasevideoview.model.SubtitlesDataCoding;
import com.xsl.culture.mybasevideoview.model.SubtitlesModel;
import com.xsl.culture.mybasevideoview.utils.FileUtils;
import com.xsl.culture.mybasevideoview.utils.NetworkCheck;
import com.xsl.culture.mybasevideoview.utils.XslUtils;
import com.xsl.culture.mybasevideoview.utils.ZipUtils;
import com.xsl.culture.mybasevideoview.view.AboutActivity;
import com.xsl.culture.mybasevideoview.view.DownloadActivity;
import com.xsl.culture.mybasevideoview.view.PayNoticeActiviy;
import com.xsl.culture.mybasevideoview.view.langugueActivity;
import com.xsl.culture.mybasevideoview.view.subTitle.SubtitleView;
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
import java.util.ArrayList;
import java.util.List;

import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.kk.taurus.playerbase.player.IPlayer.STATE_PAUSED;
import static com.kk.taurus.playerbase.player.IPlayer.STATE_STARTED;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    BaseVideoView videoView = null;
    LinearLayout subVideoView = null;
    LinearLayout wholeVideoView = null;
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

    @BindViews({R.id.about, R.id.langugue})
    List<TextView> textViewList;

    private static final int UPDATE_VIDEO_THUMBNAIL = 1;
    private static final int UPDATE_SUB_MOVIE_THUMBNAIL = 2;
    private static final int UPDATE_WHOLE_MOVIE_THUMBNAIL = 3;
    private static final int UPDATE_PLAY_TIME = 4;
    private static final int UPDATE_PLAY_DURATION = 5;

    View wifiNoticeView;

    //请求状态码
    private static int REQUEST_PERMISSION_CODE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        XslUtils.hideStausbar(new WeakReference<>(this), true);
        //SharedPreferenceUtil.getInstance(this).remove(getResources().getString(R.string.need_cache_view));

        Log.d(TAG, "main thread id:"+Thread.currentThread().getId());
        getPreference();
        init();
        getNetworkData();
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
                playCtrView.setSelected(false);
                findViewById(R.id.cover_player_controller_bottom_container).setVisibility(View.VISIBLE);
            }
        });
    }

    private void getNetworkData() {
        NetworkReq.getInstance().getTimeLine();
        NetworkReq.getInstance().getChapter();
        NetworkReq.getInstance().getVideoList();
    }
    //当前选择的语言
    public static int curLangugue = langugueActivity.chinese;
    @OnClick({R.id.about, R.id.langugue})
    void textViewOnClick(View view) {
        switch (view.getId()) {
            case R.id.about:
                createActivity(AboutActivity.class, RequestCode.About_req);
                break;
            case R.id.langugue:
                createActivity(langugueActivity.class, RequestCode.Languge_req, curLangugue);
                break;
        }
    }

    private void createActivity(Class<?> cls, int requestCode) {
        Intent intent = new Intent(MainActivity.this, cls);
        startActivityForResult(intent, requestCode);
    }

    private void createActivity(Class<?> cls, int requestCode, int value) {
        Intent intent = new Intent(MainActivity.this, cls);
        intent.putExtra(String.valueOf(R.string.activity_value), value);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCode.About_req) {
        } else if (requestCode == RequestCode.Languge_req) {
            int langugueSelector = 0;
            Bundle bd = data.getExtras();
            langugueSelector = bd.getInt(langugueActivity.langugue_key);
            if (langugueSelector < langugueActivity.unknow)
                curLangugue = langugueSelector;
            if (curLangugue == langugueActivity.chinese) {
                textViewList.get(1).setText("字幕选择: 简体中文");
            } else if (curLangugue == langugueActivity.cantonese) {
                textViewList.get(1).setText("字幕选择: 正体中文");
            } else {
                textViewList.get(1).setText("字幕选择: ENGLISH");
            }
        } else if (requestCode == RequestCode.Download_req) {
            Bundle bundle = data.getExtras();
            int ret = bundle.getInt(getResources().getString(R.string.download_result));
            if (ret == -1) {
                needCacheVideo = true;
            } else if (ret == RequestCode.MainPlay_req) {
                needCacheVideo = false;
                startMainPlayActivity();
            }
        } else if (requestCode == RequestCode.Pay_req) {
            if (needCacheVideo) {
//                String strFile = getExternalFilesDir("").getAbsolutePath() + "/360/1.mp4";
                String strFile = Environment.getExternalStorageDirectory() + "/360/1.mp4";

                if (FileUtils.isFileExists(strFile)) {
                    startMainPlayActivity();
                    needCacheVideo = false;
                } else {
                    startCacheActivity();
                }
            } else {
                needPay = false;
                startMainPlayActivity();
            }
        }
    }

    private void startMainPlayActivity() {
        Log.d(TAG, "startMainPlayActivity");
        Intent intent = new Intent(MainActivity.this, MainPlayerActivity.class);
        intent.putExtra(getResources().getString(R.string.langugue), curLangugue);
        startActivity(intent);
    }

    private void startCacheActivity() {
        Intent intent = new Intent(MainActivity.this, DownloadActivity.class);
        startActivityForResult(intent, RequestCode.Download_req);
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
                //加载字幕
                loadSubtitles();
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
//                    ImageView subImageView = findViewById(R.id.subImageView);
//                    subImageView.setBackground(new BitmapDrawable((Bitmap)msg.obj));
                    break;
                case UPDATE_WHOLE_MOVIE_THUMBNAIL:
//                    ImageView wholeImageView = findViewById(R.id.wholeImageView);
//                    wholeImageView.setBackground(new BitmapDrawable((Bitmap)msg.obj));
                    break;
                case UPDATE_PLAY_TIME:
                    if (bNativeSeekFinish) {
                        curTimeTextView.setText(XslUtils.convertSecToTimeString(msg.arg1/1000));
                        int pos = msg.arg1 * 1000 / videoDuration;
                        Log.d(TAG, "pts:"+msg.arg1);
                        seekBar.setProgress(pos);
                        updateSubtitle(msg.arg1);
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
            if (videoView.getState() == IPlayer.STATE_PAUSED) {
                videoView.resume();
            } else {
                videoView.setDataSource(new DataSource(pageInfo.getData().getIndex().getVideoUrl()));
                videoView.start();
                ImageView imageView = findViewById(R.id.videoImageView);
                imageView.setVisibility(View.GONE);
                playThread = new PlayThread(new WeakReference<>(videoView), new WeakReference<>(handler));
                playThread.start();
            }
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
        //XslUtils.deleteFile(new File(outDir.getAbsolutePath() + "/360"));
        AssetManager assetManager = getResources().getAssets();
        try {
            InputStream inputStream = assetManager.open("defaultData.zip");
            ZipUtils.UnZipFolder(inputStream, outDir.getAbsolutePath());
//            InputStream inputStream1 = assetManager.open("middle.zip");
//            ZipUtils.UnZipFolder(inputStream1, outDir.getAbsolutePath());
            //assetManager.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

//        for (int i=0; i!= 12; i++) {
//            String strPath = outDir.getAbsolutePath() + "/" + i + ".mp4";
//            if (XslUtils.fileIsExists(strPath)) {
//                continue;
//            } else {
//                InputStream inputStream = null;
//                try {
//                    inputStream = assetManager.open("" + i + ".zip");
//                    if (inputStream == null) {
//                        Log.d(TAG, "open " + i + "file filed");
//                        continue;
//                    }
//                    ZipUtils.UnZipFolder(inputStream, outDir.getAbsolutePath());
//                    Log.d(TAG, "unzip " + i + "file");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
 //       }
    }

    private boolean needCacheVideo;
    private boolean needScreenTips;
    private boolean needPay = true;
    private void getPreference() {
        if (SharedPreferenceUtil.getInstance(this).contains(getResources().getString(R.string.need_cache_view))) {
            needCacheVideo = SharedPreferenceUtil.getInstance(this).getBoolean(getResources().getString(R.string.need_cache_view));
        } else {
            needCacheVideo = true;
        }

        Log.d(TAG, "needCacheVideo: " + needCacheVideo);
        if (SharedPreferenceUtil.getInstance(this).contains(getResources().getString(R.string.need_screen_tips))) {
            needScreenTips = SharedPreferenceUtil.getInstance(this).getBoolean(getResources().getString(R.string.need_screen_tips));
        } else {
            needScreenTips = false;
        }

        if (SharedPreferenceUtil.getInstance(this).contains(getResources().getString(R.string.need_pay))) {
            needPay = SharedPreferenceUtil.getInstance(this).getBoolean(getResources().getString(R.string.need_pay));
        } else {
            needPay = true;
        }
    }

    private void init() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
            }
        }
        //unZipRes();
        videoView = findViewById(R.id.one);
        wholeVideoView = findViewById(R.id.statics);
        subVideoView= findViewById(R.id.sub_video);
        playCtrView = findViewById(R.id.player_controller_image_view_play_state);
        seekBar = findViewById(R.id.player_controller_seek_bar);
        curTimeTextView = findViewById(R.id.player_controller_text_view_curr_time);
        durationTextView = findViewById(R.id.player_controller_text_view_total_time);

        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.pause();
                playBtn.setVisibility(View.VISIBLE);
                playCtrView.setSelected(true);

//                if (videoView.getState() == STATE_PAUSED) {
//                    videoView.resume();
//                    playCtrView.setSelected(false);
//                    playBtn.setVisibility(View.GONE);
//                } else if (videoView.getState() == STATE_STARTED) {
//                    videoView.pause();
//                    playBtn.setVisibility(View.VISIBLE);
//                    playCtrView.setSelected(true);
//                }
            }
        });

        subVideoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url;
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
                if (needPay) {
                    Intent intent = new Intent(MainActivity.this, PayNoticeActiviy.class);
                    startActivityForResult(intent, RequestCode.Pay_req);
                    return;
                }

                if (needCacheVideo) {
//                    startCacheActivity();
//                    String strFile = getExternalFilesDir("").getAbsolutePath() + "/360/1.mp4";
                    String strFile = Environment.getExternalStorageDirectory() + "/360/1.mp4";
                    if (FileUtils.isFileExists(strFile)) {
                        startMainPlayActivity();
                        needCacheVideo = false;
                    } else {
                        startCacheActivity();
                    }
                } else {
                    startMainPlayActivity();
                }
            }
        });

        playCtrView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoView.getState() == STATE_PAUSED) {
                    videoView.resume();
                    playCtrView.setSelected(false);
                    playBtn.setVisibility(View.GONE);
                } else if (videoView.getState() == STATE_STARTED) {
                    videoView.pause();
                    playBtn.setVisibility(View.VISIBLE);
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
                Log.d(TAG, "seek pts:" + sec);
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
                    findViewById(R.id.cover_player_controller_bottom_container).setVisibility(View.GONE);
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
//        if (STATE_PAUSED == videoView.getState()) {
//            playBtn.setVisibility(View.GONE);
//        }
//        videoView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoView.pause();
        playBtn.setVisibility(View.VISIBLE);
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

    SubtitleView subtitleView;
    private ArrayList<SubtitlesModel> subtitleLstCN;
    private ArrayList<SubtitlesModel> subtitleLstCA;
    private ArrayList<SubtitlesModel> subtitleLstEN;
    //字幕加载
    private void loadSubtitles() {
        try {
            AssetManager assetManager = getResources().getAssets();
            InputStream inputStreamCN = assetManager.open("chinese.srt");
            InputStream inputStreamCA = assetManager.open("cantonese.srt");
            InputStream inputStreamEN = assetManager.open("english.srt");

            SubtitlesDataCoding dataCodingCN = new SubtitlesDataCoding();
            SubtitlesDataCoding dataCodingCA = new SubtitlesDataCoding();
            SubtitlesDataCoding dataCodingEN = new SubtitlesDataCoding();
            subtitleLstCN = dataCodingCN.readFileStream(inputStreamCN);
            subtitleLstEN = dataCodingEN.readFileStream(inputStreamEN);
            subtitleLstCA = dataCodingCA.readFileStream(inputStreamCA);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //關閉字幕
//        subtitleView = findViewById(R.id.subtitle);
//        subtitleView.setData(subtitleLstCN, SubtitleView.LANGUAGE_TYPE_CHINA);
//        subtitleView.setData(subtitleLstCA, SubtitleView.LANGUAGE_TYPE_CANTONESE);
//        subtitleView.setData(subtitleLstEN, SubtitleView.LANGUAGE_TYPE_ENGLISH);
//
//        Log.d("Subtitle", "ca size:"+subtitleLstCA.size());
//        subtitleView.setLanguage(SubtitleView.LANGUAGE_TYPE_CHINA);
    }

    private void updateSubtitle(int pts) {
        if (subtitleView != null)
            subtitleView.seekTo(pts);
    }
}
