package com.example.mybasevideoview;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.mybasevideoview.controller.OnBtnStateListener;
import com.example.mybasevideoview.controller.OnMaskViewListener;
import com.example.mybasevideoview.controller.OnPlayCtrlEventListener;
import com.example.mybasevideoview.controller.PlayersController;
import com.example.mybasevideoview.model.ChapterListInfo;
import com.example.mybasevideoview.model.ObtainNetWorkData;
import com.example.mybasevideoview.model.RequestCode;
import com.example.mybasevideoview.model.SubtitlesDataCoding;
import com.example.mybasevideoview.model.SubtitlesModel;
import com.example.mybasevideoview.model.TimeLineInfo;
import com.example.mybasevideoview.model.VideoListInfo;
import com.example.mybasevideoview.model.WordMsgs;
import com.example.mybasevideoview.utils.XslUtils;
import com.example.mybasevideoview.view.AboutActivity;
import com.example.mybasevideoview.view.AppliancesActivity;
import com.example.mybasevideoview.view.ChapterActivity;
import com.example.mybasevideoview.view.MySeekBar;
import com.example.mybasevideoview.view.RelateHorizonActivity;
import com.example.mybasevideoview.view.RelateVerticalActivity;
import com.example.mybasevideoview.view.TransactActivity;
import com.example.mybasevideoview.view.WordActivity;
import com.example.mybasevideoview.view.dummy.RelateButton;
import com.example.mybasevideoview.view.langugueActivity;
import com.example.mybasevideoview.view.subTitle.SubtitleTextView;
import com.example.mybasevideoview.view.subTitle.SubtitleView;
import com.kk.taurus.playerbase.entity.DataSource;
import com.kk.taurus.playerbase.event.OnErrorEventListener;
import com.kk.taurus.playerbase.event.OnPlayerEventListener;
import com.kk.taurus.playerbase.player.IPlayer;
import com.kk.taurus.playerbase.widget.BaseVideoView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainPlayerActivity extends Activity {
    public static final String TAG = "AIVideo";
    ArrayList<RelateButton> relateBtns = null;
    ArrayList<View> maskViews = null;
    PlayersController playersController = null;
    PlayControlHandler playControlHandler = null;

    static boolean mNeedStartTransactAty = true;
    public static final String RELATE_INFO = "RELATE_IHFO";

    @BindViews({R.id.p1, R.id.p2, R.id.p3, R.id.p4, R.id.p5, R.id.p6, R.id.p7, R.id.p8, R.id.p9, R.id.p10, R.id.p11, R.id.p12, R.id.p13})
    List<BaseVideoView> videoViewArrayList;

    @BindViews({R.id.about_btn, R.id.langugue_btn, R.id.appliances_btn, R.id.action_btn, R.id.chapter_btn, R.id.word_btn, R.id.back_btn})
    List<Button> buttonList;
    @BindViews({R.id.main_controller_text_view_curr_time, R.id.main_controller_text_view_total_time})
    List<TextView> textViews;
    @BindView(R.id.main_controller_image_view_play_state)
    ImageView ctrlImageView;

    private MySeekBar mySeekBar;

    private BaseVideoView leftVideoView = null;
    private BaseVideoView rightVideoView = null;
    private BaseVideoView topVideoView = null;
    private BaseVideoView bottomVideoView = null;

    // 名物視頻的地址
    String mApplienceUrl = null;
    // 動作視頻的地址
    String mActionUrl = null;

    //当前文本对应的ID
    int mWordId = -1;

    //当前播放的章节
    int curChapter = 1;
    //当前选择的语言
    int curLangugue = langugueActivity.chinese;

    //中间窗口的字幕
    SubtitleView normalSubtitleView;
    SubtitleView horizonRelateSubtitleView;
    SubtitleView verticalRelateSubtitleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_player);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);//设置固定状态栏常驻，不覆盖app布局
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);////设置固定状态栏常驻，覆盖app布局
            getWindow().setStatusBarColor(Color.parseColor("#000000"));//设置状态栏颜色
            XslUtils.hideStausbar(new WeakReference<Activity>(this), true);
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //这个一定要在createPlayCtrl之前调用，不然重复进入主面板之后就会内存泄漏或者崩溃，
        // 因为设置的videolist为空，_stop实际上无法调用videoView的stop函数
        init();
        //buttonList.get(2).getBackground().setAlpha(50);
        if (mNeedStartTransactAty) {
            Log.d(TAG, "start transactActivity");
            createActivity(TransactActivity.class, RequestCode.Transact_req);
            mNeedStartTransactAty = false;
        } else {
            createPlayCtrl();
        }
    }

    @Override
    public void setRequestedOrientation(int requestedOrientation) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O)
            return;
        super.setRequestedOrientation(requestedOrientation);
    }

    @OnClick({R.id.about_btn, R.id.langugue_btn, R.id.appliances_btn, R.id.action_btn, R.id.chapter_btn, R.id.word_btn, R.id.back_btn})
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
            case R.id.action_btn:
                break;
            case R.id.appliances_btn:
                if (buttonList.get(2).isSelected()) {
                    buttonList.get(2).setSelected(false);
                } else {
                    buttonList.get(2).setSelected(true);
                }

                playersController.pause_();
                videoViewArrayList.get(12).pause();
                createActivity(AppliancesActivity.class, RequestCode.Appliance_req, mApplienceUrl);
                break;
            case R.id.chapter_btn:
                if (buttonList.get(4).isSelected()) {
                    buttonList.get(4).setSelected(false);
                } else {
                    buttonList.get(4).setSelected(true);
                }

                List<ChapterListInfo.DataBean> data  = MainPlayerActivity.chapterListInfo.getData();
                int size = data.size();
                int chapterInRecycleIndex = curChapter - 1;
                if (chapterInRecycleIndex < (size+1) / 2) {
                    chapterInRecycleIndex = chapterInRecycleIndex * 2;
                } else {
                    //计算右边的
                    chapterInRecycleIndex = (chapterInRecycleIndex - (size + 1) / 2) * 2 + 1;
                }

//                playersController.pause_();
//                videoViewArrayList.get(12).pause();
                Log.d(TAG, "curChapter:"+curChapter + " index:"+chapterInRecycleIndex);
                createActivity(ChapterActivity.class, RequestCode.Chapter_req, chapterInRecycleIndex);
                break;
            case R.id.word_btn:
                if (buttonList.get(5).isSelected()) {
                    buttonList.get(5).setSelected(false);
                } else {
                    buttonList.get(5).setSelected(true);
                }
                playersController.pause_();
                videoViewArrayList.get(12).pause();
                createActivity(WordActivity.class, RequestCode.Word_req, mWordId);
                break;
            case R.id.langugue_btn:
                if (buttonList.get(1).isSelected()) {
                    buttonList.get(1).setSelected(false);
                } else {
                    buttonList.get(1).setSelected(true);
                }
                playersController.pause_();
                videoViewArrayList.get(12).pause();
                createActivity(langugueActivity.class, RequestCode.Languge_req, curLangugue);
                break;
            case R.id.back_btn:
                if (playersController != null)
                    playersController.stop_();
                finish();
                break;
        }
    }

    /**
     * 暂停 继续按钮
     */
    @OnClick(R.id.main_controller_image_view_play_state)
    void playCtrlClick() {
        if (videoViewArrayList.get(12).isPlaying()) {
            ctrlImageView.setSelected(true);
            playersController.pause_();
            videoViewArrayList.get(12).pause();

            if (leftVideoView.isPlaying()) {
                leftVideoView.pause();
                rightVideoView.pause();
            } else if(rightVideoView.isPlaying()) {
                topVideoView.pause();
                bottomVideoView.pause();
            }
        } else {
            playersController.resume_();
            videoViewArrayList.get(12).resume();
            ctrlImageView.setSelected(false);

            if (leftVideoView.getState() == IPlayer.STATE_PAUSED) {
                leftVideoView.resume();
                rightVideoView.resume();
            } else if(rightVideoView.getState() == IPlayer.STATE_PAUSED) {
                topVideoView.resume();
                bottomVideoView.resume();
            }
        }
    }

    /**
     * 使所有按钮变灰并且不可点击
     */
    private void disableAllBtn() {
        for (int i=2; i!= 6; i++) {
            buttonList.get(i).getBackground().setAlpha(50);
            buttonList.get(i).setClickable(false);
        }

        setBtnState(true, 4);
    }

    private void initSeekBar() {
        mySeekBar = findViewById(R.id.main_controller_seek_bar);
        mySeekBar.setMax(1000);

        mySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                bNativeSeekFinish = false;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "seekBar stop touch:"+seekBar.getProgress());
                if (playersController == null)
                    return;

                //1000是进度条总共1000等份
                 int sec = seekBar.getProgress() * playersController.getDuration() / 1000;
                 Log.d(TAG, "mySeek time:" + sec);
                 playersController.seekNotify(sec);

                 if (leftVideoView.getState() == IPlayer.STATE_STARTED) {
                     leftVideoView.seekTo(sec);
                     rightVideoView.seekTo(sec);
                 } else if(rightVideoView.getState() == IPlayer.STATE_STARTED) {
                     topVideoView.seekTo(sec);
                     bottomVideoView.seekTo(sec);
                 }
            }
        });
    }

    public static ArrayList<String> smallVideoUrls = null;
    private void initLocal90Videos() {
        File outDir = getExternalFilesDir("");
        String videoDir = outDir.getAbsolutePath() + "/defaultData";
        if (XslUtils.fileIsExists(videoDir + "/1.mp4")) {
            smallVideoUrls = new ArrayList<>();
            for (int i=0; i!=12; i++) {
                smallVideoUrls.add(videoDir+"/" + i+".mp4");
            }
        }
    }

    public static ArrayList<String> middleVideoUrls = null;
    private void init360Videos() {
//        File outDir = getExternalFilesDir("");
//        String videoDir = outDir.getAbsolutePath();
        //Uri.parse("android.resource://"+getPackageName()+"/raw/video");
        String sdcard = Environment.getExternalStoragePublicDirectory("").getAbsolutePath();

        middleVideoUrls = new ArrayList<>();
        for (int i=0; i!=12; i++) {
            middleVideoUrls.add("/sdcard/raw/" + i + ".mp4");
        }
//            String strNum = "";
//            for (int i=0; i!=12; i++) {
//                switch (i) {
//                    case 0:
//                        strNum = "zero";
//                        middleVideoUrls.add("android.resource://" + getPackageName() + "/"+ R.raw.zero);
//                        break;
//                    case 1:
//                        strNum = "one";
//                        middleVideoUrls.add("android.resource://" + getPackageName() + "/"+ R.raw.one);
//                        break;
//                    case 2:
//                        strNum = "two";
//                        middleVideoUrls.add("android.resource://" + getPackageName() + "/"+ R.raw.two);
//                        break;
//                    case 3:
//                        strNum = "three";
//                        middleVideoUrls.add("android.resource://" + getPackageName() + "/"+ R.raw.three);
//                        break;
//                    case 4:
//                        strNum = "four";
//                        middleVideoUrls.add("android.resource://" + getPackageName() + "/"+ R.raw.four);
//                        break;
//                    case 5:
//                        strNum = "five";
//                        middleVideoUrls.add("android.resource://" + getPackageName() + "/"+ R.raw.five);
//                        break;
//                    case 6:
//                        strNum = "six";
//                        middleVideoUrls.add("android.resource://" + getPackageName() + "/"+ R.raw.six);
//                        break;
//                    case 7:
//                        strNum = "seven";
//                        middleVideoUrls.add("android.resource://" + getPackageName() + "/"+ R.raw.seven);
//                        break;
//                    case 8:
//                        strNum = "eight";
//                        middleVideoUrls.add("android.resource://" + getPackageName() + "/"+ R.raw.eight);
//                        break;
//                    case 9:
//                        strNum = "nigh";
//                        middleVideoUrls.add("android.resource://" + getPackageName() + "/"+ R.raw.nigh);
//                        break;
//                    case 10:
//                        strNum = "ten";
//                        middleVideoUrls.add("android.resource://" + getPackageName() + "/"+ R.raw.ten);
//                        break;
//                    case 11:
//                        strNum = "eleven";
//                        middleVideoUrls.add("android.resource://" + getPackageName() + "/"+ R.raw.eleven);
//                        break;
//                }
//            }
    }

    void init() {
        initSeekBar();
        reLayout();
        loadSubtitles();
        disableAllBtn();
        initLocal90Videos();
        if (useLocalVideo)
            init360Videos();
        Log.d(TAG, "init thread id:%d" + Thread.currentThread().getId());

        getVideoList();
        getTimeLine();
        getChapter();
    }

    /**
     * 如果是点击事件，我们就更加严格一些，有遮罩的就不能让他点击
     * @param view
     */
    @OnClick({R.id.p1, R.id.p2, R.id.p3, R.id.p4, R.id.p5, R.id.p6, R.id.p7, R.id.p8, R.id.p9, R.id.p10, R.id.p11, R.id.p12})
    void videoViewOnClick(View view) {
        BaseVideoView videoView = (BaseVideoView) view;
        boolean clickAble = false;
        switch (view.getId()) {
            case R.id.p1:
                if (maskViews.get(0).getVisibility() == View.GONE)
                    clickAble = true;
                break;
            case R.id.p2:
                if (maskViews.get(1).getVisibility() == View.GONE)
                    clickAble = true;
                break;
            case R.id.p3:
                if (maskViews.get(2).getVisibility() == View.GONE)
                    clickAble = true;
                break;
            case R.id.p4:
                if (maskViews.get(3).getVisibility() == View.GONE)
                    clickAble = true;
                break;
            case R.id.p5:
                if (maskViews.get(4).getVisibility() == View.GONE)
                    clickAble = true;
                break;
            case R.id.p6:
                if (maskViews.get(5).getVisibility() == View.GONE)
                    clickAble = true;
                break;
            case R.id.p7:
                if (maskViews.get(6).getVisibility() == View.GONE)
                    clickAble = true;
                break;
            case R.id.p8:
                if (maskViews.get(7).getVisibility() == View.GONE)
                    clickAble = true;
                break;
            case R.id.p9:
                if (maskViews.get(8).getVisibility() == View.GONE)
                    clickAble = true;
                break;
            case R.id.p10:
                if (maskViews.get(9).getVisibility() == View.GONE)
                    clickAble = true;
                break;
            case R.id.p11:
                if (maskViews.get(10).getVisibility() == View.GONE)
                    clickAble = true;
                break;
            case R.id.p12:
                if (maskViews.get(11).getVisibility() == View.GONE)
                    clickAble = true;
                break;
        }

        if (!clickAble) {
            return;
        }

        int i = 0;
        for (BaseVideoView v : videoViewArrayList) {
            if (videoView == v) {
                Log.d(TAG, "set index " + i + " white");
                videoView.setBackgroundResource(R.drawable.xsl_video_shape_white);
                int time = videoView.getCurrentPosition();
                Message msg = playControlHandler.obtainMessage(OnPlayCtrlEventListener.PLAY_CTRL, time, i);
                playControlHandler.sendMessage(msg);
            } else {
                Log.d(TAG, "set index " + i + " black");
                v.setBackgroundResource(R.drawable.xsl_video_shape);
            }
            i++;
            if (i == 12) {
                break;
            }
        }
        videoViewArrayList.get(12).setBackgroundResource(R.drawable.xsl_video_shape_white);
    }

    //这个是程序自动触发的，因为seek导致时间不准，不得不把这个拆开写
    void videoViewOnClick_1(BaseVideoView videoView) {
        int i = 0;
        for (BaseVideoView v : videoViewArrayList) {
            i++;
            if (videoView == v) {
                videoView.setBackgroundResource(R.drawable.xsl_video_shape_white);
            } else {
                v.setBackgroundResource(R.drawable.xsl_video_shape);
            }
            if (i == 11)
                break;
        }
    }

    private void createPlayCtrl() {
        playControlHandler = new PlayControlHandler(videoViewArrayList, this);
        playersController = new PlayersController(videoViewArrayList);
        playersController.setCtrlEventListener(new OnPlayCtrlEventListener() {
            @Override
            public void onPlayCtrlCallback(int action, int centerType, int index) {
                Message msg = playControlHandler.obtainMessage(action, centerType, index);
                playControlHandler.sendMessage(msg);
            }

            @Override
            public void onPlayRelateVideos(int action, int id1, int id2, int startTime, int duration) {
                RelateVideoInfo info = new RelateVideoInfo(startTime,
                        duration, mVideolst.getData().get(id1).getVideoUrl360(),
                        mVideolst.getData().get(id2).getVideoUrl360(), id1, id2);
                Message msg = playControlHandler.obtainMessage(action, info);
                playControlHandler.sendMessage(msg);
            }

            @Override
            public void onPlayTimeCallback(int action, int duration, int curTime) {
                Message msg = playControlHandler.obtainMessage(action, duration, curTime);
                playControlHandler.sendMessage(msg);
            }

            @Override
            public void onRelateUIClose(int action, int id1, int id2) {
                Message msg = playControlHandler.obtainMessage(action, id1, id2);
                playControlHandler.sendMessage(msg);
            }

            @Override
            public void onSubtitleUpdate(int pts) {
                Message msg = playControlHandler.obtainMessage(SUBTITLE_UPDATE, pts, 0);
                playControlHandler.sendMessage(msg);
            }
        });

        playersController.setBtnStateListener(new OnBtnStateListener() {
            @Override
            public void onStateChange(int action, boolean enable, String url) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (action) {
                            case OnBtnStateListener.XSL_APPLIANCE_BTN_STATE:
                                setBtnState(enable, 2);
                                mApplienceUrl = url;
                                break;
                            case OnBtnStateListener.XSL_ACTION_BTN_STATE:
                                setBtnState(enable, 3);
                                mActionUrl = url;
                                break;
                            case OnBtnStateListener.XSL_CHAPTER_BTN_STATE:
                                setBtnState(enable, 4);
                                break;
                        }
                    }
                });
            }

            @Override
            public void onWordStateChange(int action, boolean enable, int objId) {
                setBtnState(enable, 5);
                mWordId = objId;
            }

            @Override
            public void onChapterBtnTextUpdate(String text) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int chapter = Integer.parseInt(text);
                        if (chapter != curChapter) {
                            buttonList.get(4).setText(text);
                            curChapter = chapter;
                        }
                    }
                });
            }
        });

        playersController.setMaskViewListener(new OnMaskViewListener() {
            @Override
            public void setMaskViewStatus(int action, int index, int id) {
                Message msg = playControlHandler.obtainMessage(action, index, id);
                playControlHandler.sendMessage(msg);
            }
        });

        playersController.startPlay_();
        int duration = playersController.getDuration();
        if (duration != 0 && chapterListInfo != null) {
            mySeekBar.setChapterListInfo(chapterListInfo, duration);
        }
    }

    /**
     * 设置单个按钮状态
     * @param state
     * @param index 按钮在butterKnife buttonList中的位置
     */
    private void setBtnState(boolean state, int index)  {
        buttonList.get(index).setClickable(state);
        if (state) {
            buttonList.get(index).getBackground().setAlpha(255);
        } else {
            buttonList.get(index).getBackground().setAlpha(50);
        }
    }

    private void createActivity(Class<?> cls, int requestCode) {
        Intent intent = new Intent(MainPlayerActivity.this, cls);
        startActivityForResult(intent, requestCode);
    }

    private void createActivity(Class<?> cls, int requestCode, int value) {
        Intent intent = new Intent(MainPlayerActivity.this, cls);
        intent.putExtra(String.valueOf(R.string.activity_value), value);
        startActivityForResult(intent, requestCode);
    }

    private void createActivity(Class<?> cls, int requestCode, String url) {
        Intent intent = new Intent(MainPlayerActivity.this, cls);
        Bundle bundle = new Bundle();
        bundle.putSerializable(String.valueOf(R.string.applience_url), url);
        intent.putExtras(bundle);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCode.Transact_req) {
            mNeedStartTransactAty = false;
            createPlayCtrl();
        } else if (requestCode == RequestCode.About_req) {
            buttonList.get(0).setSelected(false);
        } else if (requestCode == RequestCode.Languge_req) {
            playersController.resume_();
            videoViewArrayList.get(12).resume();
            int langugueSelector = 0;
            Bundle bd = data.getExtras();
            langugueSelector = bd.getInt(langugueActivity.langugue_key);
            if (langugueSelector < langugueActivity.unknow)
                curLangugue = langugueSelector;
            buttonList.get(1).setSelected(false);

            switch (langugueSelector) {
                case langugueActivity.chinese:
                    normalSubtitleView.setLanguage(SubtitleView.LANGUAGE_TYPE_CHINA);
                    verticalRelateSubtitleView.setLanguage(SubtitleView.LANGUAGE_TYPE_CHINA);
                    horizonRelateSubtitleView.setLanguage(SubtitleView.LANGUAGE_TYPE_CHINA);
                    buttonList.get(1).setBackgroundResource(R.mipmap.xsl_langugue_simple);
                    break;
                case langugueActivity.cantonese:
                    normalSubtitleView.setLanguage(SubtitleView.LANGUAGE_TYPE_CANTONESE);
                    verticalRelateSubtitleView.setLanguage(SubtitleView.LANGUAGE_TYPE_CANTONESE);
                    horizonRelateSubtitleView.setLanguage(SubtitleView.LANGUAGE_TYPE_CANTONESE);
                    buttonList.get(1).setBackgroundResource(R.mipmap.xsl_langugue_complex);
                    break;
                case langugueActivity.english:
                    normalSubtitleView.setLanguage(SubtitleView.LANGUAGE_TYPE_ENGLISH);
                    verticalRelateSubtitleView.setLanguage(SubtitleView.LANGUAGE_TYPE_ENGLISH);
                    horizonRelateSubtitleView.setLanguage(SubtitleView.LANGUAGE_TYPE_ENGLISH);
                    buttonList.get(1).setBackgroundResource(R.mipmap.xsl_langugue_english);
                    break;
            }
        } else if (requestCode == RequestCode.Appliance_req) {
            buttonList.get(2).setSelected(false);
            playersController.resume_();
            videoViewArrayList.get(12).resume();
        } else if (requestCode == RequestCode.Chapter_req) {
            Bundle bd = data.getExtras();
            int chapterIndex = bd.getInt(ChapterActivity.chapter_key);
            int seekTime = chapterListInfo.getData().get(chapterIndex - 1).getStartTime();

            String text;
            if (chapterIndex < 10) {
                text = "0" + chapterIndex;
            } else {
                text = "" + chapterIndex;
            }
            buttonList.get(4).setText(text);
            buttonList.get(4).setSelected(false);
            bNativeSeekFinish = false;
            playersController.seekNotify(seekTime * 1000);
        } else if (requestCode == RequestCode.Word_req) {
            buttonList.get(5).setSelected(false);
            playersController.resume_();
            videoViewArrayList.get(12).resume();
        } else if (requestCode == RequestCode.Relate_req) {
            if (data == null)
                return;
            Bundle bd = data.getExtras();
            int time = bd.getInt(RelateVerticalActivity.Relate_key_time);
            int willPlayInCenterId = bd.getInt(RelateVerticalActivity.Relate_key_play_id);
            int relateId = bd.getInt(RelateVerticalActivity.Relate_key_relate_id);
            Log.d(TAG, "relateid:" + relateId + " willPlayId:"+willPlayInCenterId);
            playersController.relateViewNotify();
            playersController.resume_();

            Message msg = playControlHandler.obtainMessage(OnPlayCtrlEventListener.PLAY_CTRL, time, willPlayInCenterId);
            playControlHandler.sendMessage(msg);

            changeRelateBtnStatus(willPlayInCenterId, R.mipmap.relate_video, true);
            changeRelateBtnStatus(relateId, R.mipmap.relate_video, true);
        }
    }

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

        horizonRelateSubtitleView = findViewById(R.id.horizon_subtitle);
        verticalRelateSubtitleView = findViewById(R.id.vertical_subtitle);
        normalSubtitleView = findViewById(R.id.normal_subtitle);

        horizonRelateSubtitleView.setData(subtitleLstCN, SubtitleView.LANGUAGE_TYPE_CHINA);
        horizonRelateSubtitleView.setData(subtitleLstCA, SubtitleView.LANGUAGE_TYPE_CANTONESE);
        horizonRelateSubtitleView.setData(subtitleLstEN, SubtitleView.LANGUAGE_TYPE_ENGLISH);

        verticalRelateSubtitleView.setData(subtitleLstCN, SubtitleView.LANGUAGE_TYPE_CHINA);
        verticalRelateSubtitleView.setData(subtitleLstCA, SubtitleView.LANGUAGE_TYPE_CANTONESE);
        verticalRelateSubtitleView.setData(subtitleLstEN, SubtitleView.LANGUAGE_TYPE_ENGLISH);

        normalSubtitleView.setData(subtitleLstCN, SubtitleView.LANGUAGE_TYPE_CHINA);
        normalSubtitleView.setData(subtitleLstCA, SubtitleView.LANGUAGE_TYPE_CANTONESE);
        normalSubtitleView.setData(subtitleLstEN, SubtitleView.LANGUAGE_TYPE_ENGLISH);

        Log.d("Subtitle", "ca size:"+subtitleLstCA.size());

        normalSubtitleView.setLanguage(SubtitleView.LANGUAGE_TYPE_CHINA);
        verticalRelateSubtitleView.setLanguage(SubtitleView.LANGUAGE_TYPE_CHINA);
        horizonRelateSubtitleView.setLanguage(SubtitleView.LANGUAGE_TYPE_CHINA);
    }

    private void updateSubtitle(int pts) {
        if (rightVideoView.isPlaying()) {
            horizonRelateSubtitleView.seekTo(pts);
        } else if (topVideoView.isPlaying()) {
            verticalRelateSubtitleView.seekTo(pts);
        } else {
            normalSubtitleView.seekTo(pts);
        }
    }

    @Override
    protected void onResume() {
//        /**
//         * 设置为横屏
//         */
//        if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//
//        }
        if (videoViewArrayList.get(12).getState() == IPlayer.STATE_PAUSED) {
            playersController.resume_();
            videoViewArrayList.get(12).resume();
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        closeAllPlayers();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        if (videoViewArrayList.get(12).isPlaying()) {
            playersController.pause_();
            videoViewArrayList.get(12).pause();
        }
        super.onPause();
    }



    public static class RelateVideoInfo implements Serializable {
        private int startTime;
        private int duration;
        private String uri_1;
        private String uri_2;
        private int id_1;
        private int id_2;

        public RelateVideoInfo(int startTime, int duration, String uri_1, String uri_2, int id1, int id2) {
            this.startTime = startTime;
            this.duration = duration;
            this.uri_1 = uri_1;
            this.uri_2 = uri_2;
            id_1 = id1;
            id_2 = id2;
        }

        public String getUri_1() {
            return uri_1;
        }

        public void setUri_1(String uri_1) {
            this.uri_1 = uri_1;
        }

        public String getUri_2() {
            return uri_2;
        }

        public void setUri_2(String uri_2) {
            this.uri_2 = uri_2;
        }

        public int getStartTime() {
            return startTime;
        }

        public void setStartTime(int startTime) {
            this.startTime = startTime;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public int getId_1() {
            return id_1;
        }

        public void setId_1(int id_1) {
            this.id_1 = id_1;
        }

        public int getId_2() {
            return id_2;
        }

        public void setId_2(int id_2) {
            this.id_2 = id_2;
        }
    }


    static TimeLineInfo mTimelineInfo = null;
    private void getTimeLine() {
        ObtainNetWorkData.getTimelineData(new Callback<TimeLineInfo>() {
            @Override
            public void onResponse(Call<TimeLineInfo> call, Response<TimeLineInfo> response) {
                Log.d(TAG, "get homepage data success");
                mTimelineInfo = response.body();
                Log.d(TAG, "onResponse thread id:"+Thread.currentThread().getId());
                if (mTimelineInfo.getData() == null) {
                    Log.e(TAG, "Service Error. "+ mTimelineInfo.getMsg());
                    mTimelineInfo = null;
                }
                if (playersController != null) {
                    int duration = playersController.getDuration();
                    if (duration != 0) {
                        mySeekBar.setChapterListInfo(chapterListInfo, duration);
                    }
                }
            }

            @Override
            public void onFailure(Call<TimeLineInfo> call, Throwable t) {
                Log.w(TAG, "get timeline failed, "+t.toString());
            }
        });
    }
    public static TimeLineInfo getTimelineInfo() {
        return mTimelineInfo;
    }

    public static WordMsgs wordMsgs = null;
    private void getWordMsgs(int index) {
        ObtainNetWorkData.getWordListData(new Callback<WordMsgs>() {
            @Override
            public void onResponse(Call<WordMsgs> call, Response<WordMsgs> response) {
                wordMsgs = response.body();
                Log.d(TAG, "get word list ok. info:" + response.toString());
            }

            @Override
            public void onFailure(Call<WordMsgs> call, Throwable t) {
                Log.w(TAG, "get word list failed, "+t.toString());
            }
        }, index);
    }

    public static ChapterListInfo chapterListInfo = null;
    private void getChapter() {
        ObtainNetWorkData.getChapterListData(new Callback<ChapterListInfo>() {
            @Override
            public void onResponse(Call<ChapterListInfo> call, Response<ChapterListInfo> response) {
                chapterListInfo = response.body();
                if (playersController != null) {
                    int duration = playersController.getDuration();
                    if (duration != 0) {
                        mySeekBar.setChapterListInfo(chapterListInfo, duration);
                    }
                }
            }

            @Override
            public void onFailure(Call<ChapterListInfo> call, Throwable t) {
                Log.w(TAG, "get chapter info failed, "+t.toString());
            }
        });
    }

    static VideoListInfo mVideolst = null;
    private void getVideoList() {
        ObtainNetWorkData.getVideoListData(new Callback<VideoListInfo>() {
            @Override
            public void onResponse(Call<VideoListInfo> call, Response<VideoListInfo> response) {
                Log.d(TAG, "get homepage data success");
                mVideolst = response.body();
                Log.d(TAG, "onResponse thread id:"+Thread.currentThread().getId());
            }

            @Override
            public void onFailure(Call<VideoListInfo> call, Throwable t) {
                Log.w(TAG, "get timeline failed, "+t.toString());
            }
        });
    }
    public static VideoListInfo getVideoLstInfo() {
        return mVideolst;
    }

    void closeAllPlayers() {
        if (playersController != null)
            playersController.stop_();

        for (BaseVideoView videoView : videoViewArrayList) {
            videoView.stop();
            videoView.stopPlayback();
        }
    }

    //主播放页面重新布局
    void reLayout() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        System.out.println("width-display :" + dm.widthPixels);
        System.out.println("heigth-display :" + dm.heightPixels);
        int backBtnWidth = convertDpToPixel(28+10);
        int cotrollerHeight = convertDpToPixel(24);
        //30是width 20是marginRight
        int defaultRightBtnWidth = convertDpToPixel(30+20);
        int w, h;
        w = dm.widthPixels - backBtnWidth - defaultRightBtnWidth;
        h = dm.heightPixels - cotrollerHeight;
        int needWidth = w;
        int needHeight = h;
        //和16/9进行比较
        if (w * 9 > h * 16) {
            //比例比16/9还大，说明太宽了，则增加右侧按钮的marginLeft
            needWidth = h * 16 / 9;
            int tempWidth = needWidth / 5 >> 1 << 1;
            tempWidth = tempWidth / 16 * 16;
            needWidth = tempWidth * 5;
            needHeight = needWidth * 9 / 16;
        } else if (w * 9 < h * 16) {
            //宽度不够，只能缩小高度，计算进度条的marginTop
            needHeight = w * 9 /16;
            //避免奇数，把needHeight换算成偶数,重新计算宽高
            int tempHeight = needHeight / 5 >> 1 << 1;
            //保证高度是9的倍数
            tempHeight = tempHeight / 9 * 9;
            needHeight = tempHeight * 5;
            needWidth = needHeight * 16 / 9;
        }

        int rightBtnMarginLeft = w - needWidth;
        LinearLayout ln = findViewById(R.id.rightButtons);
        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams)ln.getLayoutParams();
        linearParams.leftMargin = rightBtnMarginLeft;
        ln.setLayoutParams(linearParams);

        int controllerMarginTop = h - needHeight + 8;
        LinearLayout ln1 = findViewById(R.id.controller);
        RelativeLayout.LayoutParams relativeParams = (RelativeLayout.LayoutParams)ln1.getLayoutParams();
        relativeParams.topMargin = controllerMarginTop;
        ln1.setLayoutParams(relativeParams);

        //设置顶层和底层的播放空间的高度
        BaseVideoView p1 = findViewById(R.id.p1);
        BaseVideoView p8 = findViewById(R.id.p8);

        linearParams = (LinearLayout.LayoutParams)p1.getLayoutParams();
        linearParams.height = needHeight / 5;
        Log.d(TAG, "height:"+ linearParams.height);
        //linearParams.width = linearParams.height * 16 / 9;

        p1.setLayoutParams(linearParams);
        p8.setLayoutParams(linearParams);

        //设置左边和右边播放控件的宽度
        BaseVideoView p11 = findViewById(R.id.p11);
        BaseVideoView p5 = findViewById(R.id.p5);
        linearParams = (LinearLayout.LayoutParams)p11.getLayoutParams();
        //28+28+10是左边返回控件64dp和右边介绍按钮30dp + 20dp
        //linearParams.width = (dm.widthPixels - convertDpToPixel(116))/5 - convertDpToPixel(1);
        linearParams.width = needWidth / 5 - 12;
        Log.d(TAG, "width:"+ linearParams.width);

        p11.setLayoutParams(linearParams);

        linearParams = (LinearLayout.LayoutParams)p5.getLayoutParams();
        //linearParams.width = (dm.widthPixels - convertDpToPixel(60))/5;
        linearParams.width = needWidth / 5 - 12;
        Log.d(TAG, "width:"+ linearParams.width);

        //linearParams.height = linearParams.width * 9 / 16;
        p5.setLayoutParams(linearParams);

        //添加按钮
        addRelateBtns();
        //添加遮罩
        addMaskView();

        addVideoLayer();
    }

    View horizonView;
    View verticalView;
    int relateId1;//主播放的id
    int relateId2;//关联视频的id
    /**
     * 添加视频弹层
     */
    private void addVideoLayer() {
        FrameLayout frameLayout = findViewById(R.id.frame_layout);
        //左右关联界面添加
        horizonView = View.inflate(this, R.layout.horizon_video_layer, null);
        frameLayout.addView(horizonView);
        leftVideoView = findViewById(R.id.leftVideoView);
        rightVideoView = findViewById(R.id.rightVideoView);
        findViewById(R.id.left_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leftVideoView.stop();
                rightVideoView.stop();
                horizonView.setVisibility(View.GONE);
                //关闭的是主播放的视频，那么就要播放关联的视频
                int time = videoViewArrayList.get(12).getCurrentPosition();
                Message msg = playControlHandler.obtainMessage(OnPlayCtrlEventListener.PLAY_CTRL, time, relateId2);
                playControlHandler.sendMessage(msg);
                playersController.updateCenterPlayerInfo(relateId2, time);
                changeRelateBtnStatus(relateId2, R.mipmap.relate_video, true);
                changeRelateBtnStatus(relateId1, R.mipmap.relate_video, true);
            }
        });
        findViewById(R.id.right_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leftVideoView.stop();
                rightVideoView.stop();
                horizonView.setVisibility(View.GONE);
                int time = videoViewArrayList.get(12).getCurrentPosition();
                Message msg = playControlHandler.obtainMessage(OnPlayCtrlEventListener.PLAY_CTRL, time, relateId1);
                playControlHandler.sendMessage(msg);
                playersController.updateCenterPlayerInfo(relateId2, time);
                changeRelateBtnStatus(relateId1, R.mipmap.relate_video, true);
                changeRelateBtnStatus(relateId2, R.mipmap.relate_video, true);
            }
        });

        //上下关联界面添加
        verticalView = View.inflate(this, R.layout.vertical_video_layer, null);
        frameLayout.addView(verticalView);
        topVideoView = findViewById(R.id.topVideoView);
        bottomVideoView = findViewById(R.id.bottomVideoView);

        findViewById(R.id.top_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                topVideoView.stop();
                bottomVideoView.stop();
                verticalView.setVisibility(View.GONE);
                changeRelateBtnStatus(relateId2, R.mipmap.relate_video, true);
                changeRelateBtnStatus(relateId1, R.mipmap.relate_video, true);
            }
        });

        findViewById(R.id.bottom_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                topVideoView.stop();
                bottomVideoView.stop();
                verticalView.setVisibility(View.GONE);
                changeRelateBtnStatus(relateId1, R.mipmap.relate_video, true);
                changeRelateBtnStatus(relateId2, R.mipmap.relate_video, true);
            }
        });

        horizonView.setVisibility(View.GONE);
        verticalView.setVisibility(View.GONE);
    }

    /**
     * 自动播放结束，没有去点击关闭按钮
     */
    private void verticalRelateVideosFinish() {
        topVideoView.stop();
        bottomVideoView.stop();
        verticalView.setVisibility(View.GONE);
        changeRelateBtnStatus(relateId1, R.mipmap.relate_video, false);
        changeRelateBtnStatus(relateId2, R.mipmap.relate_video, false);
    }
    /**
     * 自动播放结束，没有去点击关闭按钮
     */
    private void horizonRelateVideosFinish() {
        leftVideoView.stop();
        rightVideoView.stop();
        horizonView.setVisibility(View.GONE);

        changeRelateBtnStatus(relateId2, R.mipmap.relate_video, false);
        changeRelateBtnStatus(relateId1, R.mipmap.relate_video, false);
    }

    private void addRelateBtns() {
        if (relateBtns == null)
            relateBtns = new ArrayList<>();
        for (int i=0; i!=12; i++) {
            RelateButton button = new RelateButton(videoViewArrayList.get(i).getContext());
            relateBtns.add(button);
            //布局
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(200, 100, 0, 0);
            button.setLayoutParams(layoutParams);
            button.setVisibility(View.GONE);//隐藏
            button.getBackground().setAlpha(0);
            button.setWindowIndex(i);

            //和videoView关联
            button.setmFatherView(new WeakReference<>(videoViewArrayList.get(i)));
            videoViewArrayList.get(i).addView(button);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //在可见的情况下才存在需要取消关联禁闭
                    if (button.getmResId() == R.mipmap.relate_video && button.getVisibility() == View.VISIBLE) {
                        videoViewOnClick_1(button.getmFatherView().get());
                        Log.d(TAG, "click window: " + button.getWindowIndex());
                        //取消对关联视频的禁闭
                        playersController.removeRelateDataInLst();
                    }
                }
            });
        }
    }

    /**
     * 给小窗口添加遮罩
     */
    private void addMaskView() {
        if (maskViews == null) {
            maskViews = new ArrayList<>();
        }

        for (int i = 0; i != 12; i++) {
            View view = new View(videoViewArrayList.get(i).getContext());
            maskViews.add(view);
            //布局
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT);
            view.setLayoutParams(layoutParams);
            view.setVisibility(View.VISIBLE);

            view.setBackgroundColor(Color.BLACK);
            view.getBackground().setAlpha(180);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            //和videoView关联
            videoViewArrayList.get(i).addView(view);
        }
    }

    /**
     * 改变关联按钮的状态
     * @param id
     * @param resId
     * @param visible false情况下忽略resId
     */
    void changeRelateBtnStatus(int id, int resId, boolean visible) {
        if (visible) {
            relateBtns.get(id).setVisibility(View.VISIBLE);
        } else {
            relateBtns.get(id).setVisibility(View.GONE);
            return;
        }
        relateBtns.get(id).setImageResource(resId);
        relateBtns.get(id).setmResId(resId);
    }

    private int convertDpToPixel(int dp) {
        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        return (int)(dp*displayMetrics.density);
    }

    private int convertPixelToDp(int pixel) {
        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        return (int)(pixel/displayMetrics.density);
    }

    int getStatusHeight() {
        /**
         * 获取状态栏高度——方法1
         * */
        int statusBarHeight1 = -1;
        //获取status_bar_height资源的ID
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight1 = getResources().getDimensionPixelSize(resourceId);
        }
        Log.e("-------", "状态栏-方法1:" + statusBarHeight1);
        return statusBarHeight1;
    }

    void updatePlayCtroller(int duration, int curTime) {
        //Log.d(TAG, "main player statues:"+videoViewArrayList.get(12).getState());
        if (duration <= 0)
            return;
        mySeekBar.setProgress(curTime * 1000 / duration);
        //当前时间设置
        textViews.get(0).setText(XslUtils.convertSecToTimeString(curTime / 1000));
        //总时间设置
        textViews.get(1).setText(XslUtils.convertSecToTimeString(duration / 1000));
    }

    void setVideoViewBoard(int id) {
        for (int i=0; i!=12; i++) {
            videoViewArrayList.get(i).setBoardColor(false);
        }

        videoViewArrayList.get(id).setBoardColor(true);
        Log.d(TAG, "set videoView red board id: "+ id);
    }

    boolean bNativeSeekFinish = true;
    //第一次播放的时候需要把所有下窗口播放出来
    private boolean bFirstTimePlay = true;

    void setListenVideoView(BaseVideoView videoView) {
        //Log.d(TAG, "setListenVideoView thread id:%d" + Thread.currentThread().getId());

        videoView.setOnPlayerEventListener(new OnPlayerEventListener() {
            @Override
            public void onPlayerEvent(int eventCode, Bundle bundle) {
                //Log.d(TAG, "onPlayerEvent thread id:%d" + Thread.currentThread().getId());
                if (eventCode == OnPlayerEventListener.PLAYER_EVENT_ON_TIMER_UPDATE) {
                    if (bNativeSeekFinish) {
                        //updateUI(bundle.getInt(EventKey.INT_ARG1), bundle.getInt(EventKey.INT_ARG2));
                        long pos = videoView.getCurrentPosition();
                        //Log.d(TAG, "get pos:" + pos);
                    }
                } else if (eventCode == OnPlayerEventListener.PLAYER_EVENT_ON_SEEK_COMPLETE) {
                    Log.d(TAG, "mySeek finish, get pos:" + videoView.getCurrentPosition());
                    if (!bNativeSeekFinish) {
                        //playersController.seekFinish();
                        //这里进行小窗口的seek，也就是说等大窗口seek成功之后再进行小窗口的seek
                        //参数直接忽略,所有小窗口seek到當前的播放時間
                        playersController.seekTo_(playersController.getCurrentPosition());
                    }
                    bNativeSeekFinish = true;
                } else if (eventCode == OnPlayerEventListener.PLAYER_EVENT_ON_PLAY_COMPLETE) {
                    Log.d(TAG, "PLAYER_EVENT_ON_PLAY_COMPLETE");
                } else if (eventCode == OnPlayerEventListener.PLAYER_EVENT_ON_START) {
                    Log.d(TAG, "main player started");
                    if (bFirstTimePlay)
                        playersController.start_();
                    bFirstTimePlay = false;
                }
            }
        });

        videoView.setOnErrorEventListener(new OnErrorEventListener() {
            @Override
            public void onErrorEvent(int eventCode, Bundle bundle) {

            }
        });
    }

    public boolean useLocalVideo = true;
    public static class PlayControlHandler extends Handler {
        WeakReference<List<BaseVideoView>> videoViewLst;
        WeakReference<MainPlayerActivity> mainPlayerActivityWeakReference;

        public PlayControlHandler(List<BaseVideoView> lst, MainPlayerActivity mainPlayerActivity) {
            videoViewLst = new WeakReference<>(lst);
            mainPlayerActivityWeakReference = new WeakReference<>(mainPlayerActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //action, centerType, videoViewIndex, playDataList.get(playDataIndex)
                case OnPlayCtrlEventListener.PLAY_CTRL:
                    if (videoViewLst.get() != null) {
                        if (videoViewLst.get().get(12).getState() == IPlayer.STATE_STARTED)
                            videoViewLst.get().get(12).stop();

                        String uri;
                        if (mainPlayerActivityWeakReference.get().useLocalVideo) {
                            uri = middleVideoUrls.get(msg.arg2);
                        } else {
                            uri = mVideolst.getData().get(msg.arg2).getVideoUrl360();
                        }

                        videoViewLst.get().get(12).setDataSource(new DataSource(uri));
                        videoViewLst.get().get(12).start(msg.arg1);
                        videoViewLst.get().get(12).setVolume(0, 0);
                        mainPlayerActivityWeakReference.get().setListenVideoView(videoViewLst.get().get(12));
                        videoViewLst.get().get(12).setBackgroundResource(R.drawable.xsl_video_shape_white);
                        mainPlayerActivityWeakReference.get().videoViewOnClick_1(videoViewLst.get().get(msg.arg2));
                        Log.d(TAG, "play main url: "+mVideolst.getData().get(msg.arg2).getVideoUrl360());
                    }
                    break;
                case OnPlayCtrlEventListener.STOP_CTRL:
                    videoViewLst.get().get(12).stop();
                    break;
//                case OnPlayCtrlEventListener.SEEK_CTRL:
//                    mainPlayerActivityWeakReference.get().seekAll(msg.arg2);
//                    break;
                case OnPlayCtrlEventListener.PLAY_RELATE_VERTICAL_CTRL:
//                    mainPlayerActivityWeakReference.get().playersController.pauseNoLock();
//                    mainPlayerActivityWeakReference.get().videoViewArrayList.get(12).pause();
//                    Intent intent = new Intent(mainPlayerActivityWeakReference.get(), RelateVerticalActivity.class);
//                    intent.putExtra(RELATE_INFO, (Serializable) msg.obj);
//                    mainPlayerActivityWeakReference.get().startActivityForResult(intent, RequestCode.Relate_req);
                    //播放关联视频
                    mainPlayerActivityWeakReference.get().verticalView.setVisibility(View.VISIBLE);
                    RelateVideoInfo videoInfo = (RelateVideoInfo)msg.obj;
                    if (mainPlayerActivityWeakReference.get().useLocalVideo) {
                        mainPlayerActivityWeakReference.get().topVideoView.setDataSource(new DataSource(middleVideoUrls.get(videoInfo.id_1)));
                        mainPlayerActivityWeakReference.get().topVideoView.start(videoInfo.getStartTime() * 1000);
                        mainPlayerActivityWeakReference.get().bottomVideoView.setDataSource(new DataSource(middleVideoUrls.get(videoInfo.id_2)));
                        mainPlayerActivityWeakReference.get().bottomVideoView.start(videoInfo.getStartTime() * 1000);
                    } else {
                        mainPlayerActivityWeakReference.get().topVideoView.setDataSource(new DataSource(videoInfo.getUri_1()));
                        mainPlayerActivityWeakReference.get().topVideoView.start(videoInfo.getStartTime() * 1000);
                        mainPlayerActivityWeakReference.get().bottomVideoView.setDataSource(new DataSource(videoInfo.getUri_2()));
                        mainPlayerActivityWeakReference.get().bottomVideoView.start(videoInfo.getStartTime() * 1000);
                    }

                    mainPlayerActivityWeakReference.get().relateId1 = videoInfo.id_1;
                    mainPlayerActivityWeakReference.get().relateId2 = videoInfo.id_2;
                    Log.d(TAG, "relate id: " + videoInfo.id_1 + " relate id: "+ videoInfo.id_2);

                    //修改关联视频UI
                    RelateVideoInfo info = (RelateVideoInfo)msg.obj;
                    mainPlayerActivityWeakReference.get().changeRelateBtnStatus(info.id_1, R.mipmap.relate_video, true);
                    mainPlayerActivityWeakReference.get().changeRelateBtnStatus(info.id_2, R.mipmap.relate_video, true);
                    break;
                case OnPlayCtrlEventListener.PLAY_RELATE_HORIZON_CTRL:
                    //播放关联视频
                    mainPlayerActivityWeakReference.get().horizonView.setVisibility(View.VISIBLE);
                    RelateVideoInfo videoInfo1 = (RelateVideoInfo)msg.obj;
                    if (mainPlayerActivityWeakReference.get().useLocalVideo) {
                        mainPlayerActivityWeakReference.get().topVideoView.setDataSource(new DataSource(middleVideoUrls.get(videoInfo1.id_1)));
                        mainPlayerActivityWeakReference.get().topVideoView.start(videoInfo1.getStartTime() * 1000);
                        mainPlayerActivityWeakReference.get().bottomVideoView.setDataSource(new DataSource(middleVideoUrls.get(videoInfo1.id_2)));
                        mainPlayerActivityWeakReference.get().bottomVideoView.start(videoInfo1.getStartTime() * 1000);
                    } else {
                        mainPlayerActivityWeakReference.get().leftVideoView.setDataSource(new DataSource(videoInfo1.getUri_1()));
                        mainPlayerActivityWeakReference.get().leftVideoView.start(videoInfo1.getStartTime() * 1000);
                        mainPlayerActivityWeakReference.get().rightVideoView.setDataSource(new DataSource(videoInfo1.getUri_2()));
                        mainPlayerActivityWeakReference.get().rightVideoView.start(videoInfo1.getStartTime() * 1000);
                    }

                    mainPlayerActivityWeakReference.get().relateId1 = videoInfo1.id_1;
                    mainPlayerActivityWeakReference.get().relateId2 = videoInfo1.id_2;

                    //修改关联视频UI
                    mainPlayerActivityWeakReference.get().changeRelateBtnStatus(videoInfo1.id_1, R.mipmap.relate_video, true);
                    mainPlayerActivityWeakReference.get().changeRelateBtnStatus(videoInfo1.id_2, R.mipmap.relate_video, true);
                    break;
                case OnPlayCtrlEventListener.PLAY_RELATE_CLOSE_UI:
                    mainPlayerActivityWeakReference.get().verticalRelateVideosFinish();
                    mainPlayerActivityWeakReference.get().horizonRelateVideosFinish();
//                    Log.d(TAG, "close relate UI. id1:" + msg.arg1 + " id2:" + msg.arg2);
//                    mainPlayerActivityWeakReference.get().changeRelateBtnStatus(msg.arg1, 0, false);
//                    mainPlayerActivityWeakReference.get().changeRelateBtnStatus(msg.arg2, 0, false);
                    break;
                case OnPlayCtrlEventListener.PLAY_TIME_SET_CTRL:
                    mainPlayerActivityWeakReference.get().updatePlayCtroller(msg.arg1, msg.arg2);
                    break;
                case OnMaskViewListener.ACTION_MASK_GONE:
                    if (mainPlayerActivityWeakReference.get().maskViews.get(msg.arg1).getVisibility() == View.VISIBLE) {
                        mainPlayerActivityWeakReference.get().maskViews.get(msg.arg1).setVisibility(View.GONE);
                        Log.d(TAG, "gone index:"+msg.arg1 + "id:"+msg.arg2);
                    }
                    break;
                case OnMaskViewListener.ACTION_MASK_VISIABLE:
                    if (mainPlayerActivityWeakReference.get().maskViews.get(msg.arg1).getVisibility() == View.GONE) {
                        mainPlayerActivityWeakReference.get().maskViews.get(msg.arg1).setVisibility(View.VISIBLE);
                        Log.d(TAG, "visible index:"+msg.arg1 + "id:"+msg.arg2);
                    }
                    break;
                case OnPlayCtrlEventListener.SUBTITLE_UPDATE:
                    mainPlayerActivityWeakReference.get().updateSubtitle(msg.arg1);
                    break;
            }
        }
    }
}
