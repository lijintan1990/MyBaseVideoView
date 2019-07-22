package com.example.mybasevideoview;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
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
import com.example.mybasevideoview.controller.OnPlayCtrlEventListener;
import com.example.mybasevideoview.controller.PlayersController;
import com.example.mybasevideoview.model.ChapterListInfo;
import com.example.mybasevideoview.model.ObtainNetWorkData;
import com.example.mybasevideoview.model.RequestCode;
import com.example.mybasevideoview.model.TimeLineInfo;
import com.example.mybasevideoview.model.VideoListInfo;
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
import com.kk.taurus.playerbase.entity.DataSource;
import com.kk.taurus.playerbase.event.OnErrorEventListener;
import com.kk.taurus.playerbase.event.OnPlayerEventListener;
import com.kk.taurus.playerbase.player.IPlayer;
import com.kk.taurus.playerbase.receiver.ReceiverGroup;
import com.kk.taurus.playerbase.widget.BaseVideoView;

import java.io.File;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    // 名物視頻的地址
    String mApplienceUrl = null;
    // 動作視頻的地址
    String mActionUrl = null;

    String mWordTitle = null;
    String mWordImageUrl = null;
    String mWordContent = null;

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
                createActivity(AppliancesActivity.class, RequestCode.Appliance_req, mApplienceUrl);
                break;
            case R.id.chapter_btn:
                if (buttonList.get(4).isSelected()) {
                    buttonList.get(4).setSelected(false);
                } else {
                    buttonList.get(4).setSelected(true);
                }
                createActivity(ChapterActivity.class, RequestCode.Chapter_req);
                break;
            case R.id.word_btn:
                createWordActivity(WordActivity.class, RequestCode.Word_req);
                break;
            case R.id.langugue_btn:
                if (buttonList.get(1).isSelected()) {
                    buttonList.get(1).setSelected(false);
                } else {
                    buttonList.get(1).setSelected(true);
                }
                playersController.pause_();
                createActivity(langugueActivity.class, RequestCode.Languge_req);
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
        } else {
            playersController.resume_();
            videoViewArrayList.get(12).resume();
            ctrlImageView.setSelected(false);
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

    void init() {
        initSeekBar();
        reLayout();
        disableAllBtn();
        initLocal90Videos();
        Log.d(TAG, "init thread id:%d" + Thread.currentThread().getId());

        getVideoList();
        getTimeLine();
        getChapter();
    }

    @OnClick({R.id.p1, R.id.p2, R.id.p3, R.id.p4, R.id.p5, R.id.p6, R.id.p7, R.id.p8, R.id.p9, R.id.p10, R.id.p11, R.id.p12})
    void videoViewOnClick(View view) {
        for (BaseVideoView v : videoViewArrayList) {
            v.setBackgroundResource(R.drawable.xsl_video_shape);
        }

        BaseVideoView videoView = (BaseVideoView) view;
        videoView.setBackgroundResource(R.drawable.xsl_video_shape_white);
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
            public void onWordStateChange(int action, boolean enable, String name, String imageUri, String content) {
                setBtnState(enable, 5);
                mWordTitle = name;
                mWordImageUrl = imageUri;
                mWordContent = content;
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

    public void createActivity(Class<?> cls, int requestCode) {
        Intent intent = new Intent(MainPlayerActivity.this, cls);
        startActivityForResult(intent, requestCode);
    }

    public void createActivity(Class<?> cls, int requestCode, String url) {
        Intent intent = new Intent(MainPlayerActivity.this, cls);
        Bundle bundle = new Bundle();
        bundle.putSerializable(String.valueOf(R.string.applience_url), url);
        intent.putExtras(bundle);
        startActivityForResult(intent, requestCode);
    }

    private void createWordActivity(Class<?> cls, int requestCode) {
        Intent intent = new Intent(MainPlayerActivity.this, cls);
        Bundle bundle = new Bundle();
        bundle.putSerializable(String.valueOf(R.string.word_name), mWordTitle);
        bundle.putSerializable(String.valueOf(R.string.word_image_url), mWordImageUrl);
        bundle.putSerializable(String.valueOf(R.string.word_content), mWordContent);
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
        } else if (requestCode == RequestCode.Appliance_req) {
            buttonList.get(2).setSelected(false);
        } else if (requestCode == RequestCode.Chapter_req) {
            Bundle bd = data.getExtras();
            int chapterIndex = bd.getInt(ChapterActivity.chapter_key);
            int seekTime = chapterListInfo.getData().get(chapterIndex).getStartTime();

            String text;
            if (chapterIndex < 10) {
                text = "0" + chapterIndex;
            } else {
                text = "" + chapterIndex;
            }
            buttonList.get(4).setText(text);
            bNativeSeekFinish = false;
            playersController.seekNotify(seekTime * 1000);
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

            changeRelateBtnStatus(willPlayInCenterId, R.mipmap.relate_playing, true);
            changeRelateBtnStatus(relateId, R.mipmap.relate_video, true);

//            relateBtns.get(willPlayInCenterId).setImageResource(R.mipmap.relate_playing);
//            relateBtns.get(relateId).setImageResource(R.mipmap.relate_video);
//            relateBtns.get(willPlayInCenterId).setVisibility(View.VISIBLE);
//            relateBtns.get(relateId).setVisibility(View.VISIBLE);
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

//    private void seekAll(int sec) {
//        playersController.seekTo_(sec);
//    }

//    private void pauseAll() {
//        for (int i=0; i!=12; i++) {
//            videoViewArrayList.get(i).pause();
//        }
//    }
//
//    private void resumeAll() {
//        for (int i=0; i!=12; i++) {
//            videoViewArrayList.get(i).resume();
//        }
//    }
//
//    private void stopAll() {
//        for (int i=0; i!=12; i++) {
//            videoViewArrayList.get(i).stop();
//        }
//    }

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
        addRelateBtns();
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
                        videoViewOnClick(button.getmFatherView().get());
                        Log.d(TAG, "click window: " + button.getWindowIndex());
                        //取消对关联视频的禁闭
                        playersController.removeRelateDataInLst();
                    }
                }
            });
        }
    }

    /**
     * 改变关联按钮的状态
     * @param id
     * @param resId
     * @param visible false情况下，resId忽略
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

//        videoView.setOnReceiverEventListener(new OnReceiverEventListener() {
//            @Override
//            public void onReceiverEvent(int eventCode, Bundle bundle) {
//                //...
//            }
//        });
        videoView.setOnErrorEventListener(new OnErrorEventListener() {
            @Override
            public void onErrorEvent(int eventCode, Bundle bundle) {

            }
        });

//        videoView.setEventHandler(new OnVideoViewEventHandler(){
//            @Override
//            public void onAssistHandle(BaseVideoView assist, int eventCode, Bundle bundle) {
//                super.onAssistHandle(assist, eventCode, bundle);
//                switch (eventCode){
//                    case InterEvent.CODE_REQUEST_PAUSE:
//                        userPause = true;
//                        break;
//                    case DataInter.Event.EVENT_CODE_REQUEST_BACK:
//                        if(isLandscape){
//                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                        }else{
//                            finish();
//                        }
//                        break;
//                    case DataInter.Event.EVENT_CODE_REQUEST_TOGGLE_SCREEN:
//                        setRequestedOrientation(isLandscape ?
//                                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
//                                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//                        break;
//                    case DataInter.Event.EVENT_CODE_ERROR_SHOW:
//                        //videoViewArrayList.get(17).stop();
//                        break;
//                }
//            }
//
//        });
    }

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
                        videoViewLst.get().get(12).setDataSource(new DataSource(mVideolst.getData().get(msg.arg2).getVideoUrl360()));
                        videoViewLst.get().get(12).start(msg.arg1);
                        mainPlayerActivityWeakReference.get().setListenVideoView(videoViewLst.get().get(12));
                        //videoViewLst.get().get(msg.arg2).setBackgroundResource(R.drawable.xsl_video_shape_white);
                        mainPlayerActivityWeakReference.get().videoViewOnClick(videoViewLst.get().get(msg.arg2));
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
                    mainPlayerActivityWeakReference.get().playersController.pauseNoLock();
                    mainPlayerActivityWeakReference.get().videoViewArrayList.get(12).pause();
                    Intent intent = new Intent(mainPlayerActivityWeakReference.get(), RelateVerticalActivity.class);
                    intent.putExtra(RELATE_INFO, (Serializable) msg.obj);
                    mainPlayerActivityWeakReference.get().startActivityForResult(intent, RequestCode.Relate_req);

                    //修改关联视频UI
                    RelateVideoInfo info = (RelateVideoInfo)msg.obj;
                    mainPlayerActivityWeakReference.get().changeRelateBtnStatus(info.id_1, R.mipmap.relate_playing, true);
                    mainPlayerActivityWeakReference.get().changeRelateBtnStatus(info.id_2, R.mipmap.relate_video, true);
                    break;
                case OnPlayCtrlEventListener.PLAY_RELATE_HORIZON_CTRL:
                    mainPlayerActivityWeakReference.get().playersController.pauseNoLock();
                    mainPlayerActivityWeakReference.get().videoViewArrayList.get(12).pause();
                    Intent intent1 = new Intent(mainPlayerActivityWeakReference.get(), RelateHorizonActivity.class);
                    intent1.putExtra(RELATE_INFO, (Serializable) msg.obj);
                    mainPlayerActivityWeakReference.get().startActivityForResult(intent1, RequestCode.Relate_req);
                    //修改关联视频UI
                    RelateVideoInfo videoInfo = (RelateVideoInfo)msg.obj;
                    mainPlayerActivityWeakReference.get().changeRelateBtnStatus(videoInfo.id_1, R.mipmap.relate_playing, true);
                    mainPlayerActivityWeakReference.get().changeRelateBtnStatus(videoInfo.id_2, R.mipmap.relate_video, true);
                    break;
                case OnPlayCtrlEventListener.PLAY_TELATE_CLOSE_UI:
                    Log.d(TAG, "close relate UI. id1:" + msg.arg1 + " id2:" + msg.arg2);
                    mainPlayerActivityWeakReference.get().changeRelateBtnStatus(msg.arg1, 0, false);
                    mainPlayerActivityWeakReference.get().changeRelateBtnStatus(msg.arg2, 0, false);
                    break;
                case OnPlayCtrlEventListener.PLAY_TIME_SET_CTRL:
                    mainPlayerActivityWeakReference.get().updatePlayCtroller(msg.arg1, msg.arg2);
            }
        }
    }
}
