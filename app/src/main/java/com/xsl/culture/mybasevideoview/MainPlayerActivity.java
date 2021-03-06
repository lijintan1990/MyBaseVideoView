package com.xsl.culture.mybasevideoview;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mybasevideoview.view.dummy.RelateButton;
import com.kk.taurus.playerbase.entity.DataSource;
import com.kk.taurus.playerbase.event.OnErrorEventListener;
import com.kk.taurus.playerbase.event.OnPlayerEventListener;
import com.kk.taurus.playerbase.player.IPlayer;
import com.kk.taurus.playerbase.widget.BaseVideoView;
import com.xsl.culture.R;
import com.xsl.culture.mybasevideoview.controller.NetworkReq;
import com.xsl.culture.mybasevideoview.controller.OnBtnStateListener;
import com.xsl.culture.mybasevideoview.controller.OnMaskViewListener;
import com.xsl.culture.mybasevideoview.controller.OnPlayCtrlEventListener;
import com.xsl.culture.mybasevideoview.controller.PlayersController;
import com.xsl.culture.mybasevideoview.dialog.ConfirmDialog;
import com.xsl.culture.mybasevideoview.model.ChapterListInfo;
import com.xsl.culture.mybasevideoview.model.RequestCode;
import com.xsl.culture.mybasevideoview.model.SharedPreferenceUtil;
import com.xsl.culture.mybasevideoview.model.SubtitlesDataCoding;
import com.xsl.culture.mybasevideoview.model.SubtitlesModel;
import com.xsl.culture.mybasevideoview.utils.XslUtils;
import com.xsl.culture.mybasevideoview.view.ActionActivity;
import com.xsl.culture.mybasevideoview.view.AppliancesActivity;
import com.xsl.culture.mybasevideoview.view.ChapterActivity;
import com.xsl.culture.mybasevideoview.view.MySeekBar;
import com.xsl.culture.mybasevideoview.view.PlayControlMaskView;
import com.xsl.culture.mybasevideoview.view.Transact2Activity;
import com.xsl.culture.mybasevideoview.view.Transact3Activity;
import com.xsl.culture.mybasevideoview.view.WordActivity;
import com.xsl.culture.mybasevideoview.view.maskActivity;
import com.xsl.culture.mybasevideoview.view.subTitle.SubtitleView;

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

public class MainPlayerActivity extends Activity {
    public static final String TAG = "AIVideo";
    ArrayList<RelateButton> relateBtns = null;
    //ArrayList<View> maskViews = null;
    PlayersController playersController = null;
    PlayControlHandler playControlHandler = null;

    static boolean hadShowPager = false;
    public static final String RELATE_INFO = "RELATE_IHFO";

    @BindViews({R.id.p1, R.id.p2, R.id.p3, R.id.p4, R.id.p5, R.id.p6, R.id.p7, R.id.p8, R.id.p9, R.id.p10, R.id.p11, R.id.p12, R.id.p13})
    List<BaseVideoView> videoViewArrayList;

    @BindViews({R.id.chapter_btn, R.id.appliances_btn, R.id.action_btn, R.id.word_btn, R.id.back_btn})
    List<Button> buttonList;
    @BindViews({R.id.main_controller_text_view_curr_time, R.id.main_controller_text_view_total_time})
    List<TextView> textViews;
    @BindViews({R.id.chapter_title, R.id.chapter_title_content, R.id.chapter_content, R.id.chapter_num, R.id.chapter_name})
    List<TextView> chapterTextViewList;

    @BindViews({R.id.arrow1, R.id.arrow2,R.id.arrow3,R.id.arrow4,R.id.arrow5,R.id.arrow6,R.id.arrow7,R.id.arrow8,R.id.arrow9,R.id.arrow10,R.id.arrow11,R.id.arrow12})
    List<ImageView> maskArroy;
    @BindView(R.id.resume_btn)
    ImageButton resumeBtn;

    /**
     * 大窗口显示的时间
     */
    @BindView(R.id.play_time_info)
    TextView timeView;

    @BindViews({R.id.mask1, R.id.mask2,R.id.mask3,R.id.mask4,R.id.mask5,R.id.mask6,R.id.mask7,R.id.mask8,R.id.mask9,R.id.mask10,R.id.mask11,R.id.mask12})
    List<View> maskViews;
    private MySeekBar mySeekBar;

    // 名物視頻的地址和名称
    String mApplienceUrl = null;
    String applienceName = null;
    // 動作視頻的地址
    String mActionUrl = null;
    String actionName = null;

    //当前文本对应的ID
    int mWordId = -1;

    //当前播放的章节
    int curChapter = 1;
    // 是否是点击了切换章节，如果是切换章节，onResume里面就不能立马恢复播放，
    // 而是要进行seek操作，否则，会出现声音
    boolean chapterChange;

    //中间窗口的字幕
    SubtitleView normalSubtitleView;

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
        curLangugue = MainActivity.curLangugue;
        //这个一定要在createPlayCtrl之前调用，不然重复进入主面板之后就会内存泄漏或者崩溃，
        // 因为设置的videolist为空，_stop实际上无法调用videoView的stop函数
        init();
        //buttonList.get(2).getBackground().setAlpha(50);
        hadShowPager = SharedPreferenceUtil.getInstance(getApplicationContext()).getBoolean(getResources().getString(R.string.had_show_boot_paper));
        if (!hadShowPager) {
            Log.d(TAG, "start transactActivity");
            //createActivity(TransactActivity.class, RequestCode.Transact1_req);
            createActivity(maskActivity.class, RequestCode.Mask_req);
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

    @OnClick(R.id.resume_btn)
    void resumPlay(View view) {
        playersController.resume_();
        resumeBtn.setVisibility(View.GONE);
    }

    boolean centerPlayerPlaying = false;

    @OnClick({R.id.chapter_btn, R.id.appliances_btn, R.id.action_btn, R.id.word_btn, R.id.back_btn})
    void buttonClick(View view) {
        if (videoViewArrayList.get(12).getState() == IPlayer.STATE_PREPARED ||
                videoViewArrayList.get(12).getState() == IPlayer.STATE_STARTED ) {
            centerPlayerPlaying = true;
        } else {
            centerPlayerPlaying = false;
        }

        Log.d(TAG, "centerPlayer state " + videoViewArrayList.get(12).getState());
        switch (view.getId()) {
            case R.id.action_btn:
                if (buttonList.get(2).isSelected()) {
                    buttonList.get(2).setSelected(false);
                } else {
                    buttonList.get(2).setSelected(true);
                }

                createActionActivity(ActionActivity.class, RequestCode.Action_req, mActionUrl, actionName);
                break;
            case R.id.appliances_btn:
                if (buttonList.get(1).isSelected()) {
                    buttonList.get(1).setSelected(false);
                } else {
                    buttonList.get(1).setSelected(true);
                }

//                playersController.pause_();
//                resumeBtn.setVisibility(View.VISIBLE);
                createActivity(AppliancesActivity.class, RequestCode.Appliance_req, mApplienceUrl, applienceName);
                break;
            case R.id.word_btn:
                if (buttonList.get(3).isSelected()) {
                    buttonList.get(3).setSelected(false);
                } else {
                    buttonList.get(3).setSelected(true);
                }
//                playersController.pause_();
//                resumeBtn.setVisibility(View.VISIBLE);
                createActivity(WordActivity.class, RequestCode.Word_req, mWordId);
                break;
            case R.id.chapter_btn:
                if (buttonList.get(0).isSelected()) {
                    buttonList.get(0).setSelected(false);
                } else {
                    buttonList.get(0).setSelected(true);
                }

                List<ChapterListInfo.DataBean> data  = NetworkReq.getInstance().getChapterListInfo().getData();
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
            case R.id.back_btn:
                if (playersController != null)
                    playersController.stop_();
                finish();
                break;
        }
    }

    /**
     * 使所有按钮变灰并且不可点击
     */
    private void disableAllBtn() {
        for (int i=0; i!= 3; i++) {
            buttonList.get(i).getBackground().setAlpha(50);
            buttonList.get(i).setClickable(false);
            setBtnState(true, i);
        }
    }

    private void initSeekBar() {
        mySeekBar = findViewById(R.id.main_controller_seek_bar);
        mySeekBar.setMax(1000);
        if (playersController != null) {
            int duration = playersController.getDuration();
            if (duration != 0) {
                mySeekBar.setChapterListInfo(NetworkReq.getInstance().getChapterListInfo(), duration);
            }
        }

        mySeekBar.setSeekChapterListener(new MySeekBar.SeekChapterListener() {
            @Override
            public void seekToChapter(int time, String chapterId, String chapterTitle) {
                seekChapter(time, chapterId, chapterTitle);
                String text;
                int chapterIndex = Integer.parseInt(chapterId);
                if (chapterIndex < 10) {
                    text = "0" + chapterId;
                } else {
                    text = "" + chapterIndex;
                }
                buttonList.get(0).setText(text);
            }
        });
    }

    private void seekChapter(int time, String chapterId, String chapterTitle) {
        setCenterPlayerBlack(true);
//        setLastCenterPlayerMaskTransact();
        setSmallPlayerMaskBlack();

        time = time * 1000 + 1000;
        Log.d(TAG, "stop center player, seekChapter time" + time + "code:" + chapterId + " name:" + chapterTitle);

        curChapter = Integer.parseInt(chapterId);
        if (curChapter == 10  || curChapter == 8) {
            time += 500;
        } else if (curChapter == 6) {
            time -= 500;
        } else if (curChapter == 11) {
            time += 1000;
        }
        playersController.seekChapter(time);

        updateChapterTxt(curChapter, chapterTitle);
        resumeBtn.setVisibility(View.GONE);
        normalSubtitleView.hide();
        showArroy(-1);
        if (playersController.getDuration() > 0) {
            int progress = time / (playersController.getDuration() / 1000);
            Log.d(TAG, "time: " + time + " seek to progress: " + progress + " duration:" + playersController.getDuration());
            mySeekBar.setProgress(progress);
        }
    }

    public static ArrayList<String> smallVideoUrls = null;
    private void initLocal90Videos() {
//        File outDir = getExternalFilesDir("");
//        String videoDir = outDir.getAbsolutePath() + "/defaultData";
//        if (XslUtils.fileIsExists(videoDir + "/1.mp4")) {
//            smallVideoUrls = new ArrayList<>();
//            for (int i=0; i!=12; i++) {
//                smallVideoUrls.add(videoDir+"/" + i+".mp4");
//            }
//        }
        smallVideoUrls = middleVideoUrls;
    }

    public static ArrayList<String> middleVideoUrls = null;
    private void init360Videos() {
        File outDir = getExternalFilesDir("");
        //String videoDir = outDir.getAbsolutePath() + "/360/";
        //String videoDir = getExternalFilesDir("") + "/360/";
        String videoDir =  Environment.getExternalStorageDirectory().getPath() + "/360/";

        middleVideoUrls = new ArrayList<>();
        for (int i=0; i!=12; i++) {
            middleVideoUrls.add(videoDir + i + ".mp4");
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
        if (useLocalVideo)
            init360Videos();
        initLocal90Videos();
        Log.d(TAG, "init thread id:%d" + Thread.currentThread().getId());
    }

    public void showArroy(int id) {
        for (int i=0; i!=12; i++) {
            if (i == id) {
                maskArroy.get(i).setVisibility(View.VISIBLE);
                Log.w(TAG, "show arroy index:" + i);
            } else {
                maskArroy.get(i).setVisibility(View.INVISIBLE);
            }
        }
    }

    @OnClick({R.id.click_view1, R.id.click_view2, R.id.click_view3, R.id.click_view4, R.id.click_view5, R.id.click_view6, R.id.click_view7, R.id.click_view8, R.id.click_view9, R.id.click_view10, R.id.click_view11, R.id.click_view12})
    void maskClick(View view) {
        Log.w(TAG, "click");
        int index = 0;
        boolean clickAble = false;

        Drawable drawable;
        ColorDrawable dra;
        switch (view.getId()) {
            case R.id.click_view1:
                index =0;
//                if (maskViews.get(0).getVisibility() == View.INVISIBLE)
                drawable = maskViews.get(index).getBackground();
                dra = (ColorDrawable) drawable;
                if (dra.getColor() == getResources().getColor(R.color.translucent))
                    clickAble = true;
                break;
            case R.id.click_view2:
                index =1;
                drawable = maskViews.get(index).getBackground();
                dra = (ColorDrawable) drawable;
                //if (maskViews.get(1).getVisibility() == View.INVISIBLE)
                if (dra.getColor() == getResources().getColor(R.color.translucent))
                    clickAble = true;
                break;
            case R.id.click_view3:
                index =2;
                drawable = maskViews.get(index).getBackground();
                dra = (ColorDrawable) drawable;
//                if (maskViews.get(2).getVisibility() == View.INVISIBLE)
                if (dra.getColor() == getResources().getColor(R.color.translucent))
                    clickAble = true;
                break;
            case R.id.click_view4:
                index = 3;
                drawable = maskViews.get(index).getBackground();
                dra = (ColorDrawable) drawable;
//                if (maskViews.get(3).getVisibility() == View.INVISIBLE)
                if (dra.getColor() == getResources().getColor(R.color.translucent))
                    clickAble = true;
                break;
            case R.id.click_view5:
                index = 4;
                drawable = maskViews.get(index).getBackground();
                dra = (ColorDrawable) drawable;
//                if (maskViews.get(4).getVisibility() == View.INVISIBLE)
                if (dra.getColor() == getResources().getColor(R.color.translucent))
                    clickAble = true;
                break;
            case R.id.click_view6:
                index = 5;
                drawable = maskViews.get(index).getBackground();
                dra = (ColorDrawable) drawable;
//                if (maskViews.get(5).getVisibility() == View.INVISIBLE)
                if (dra.getColor() == getResources().getColor(R.color.translucent))
                    clickAble = true;
                break;
            case R.id.click_view7:
                index = 6;
                drawable = maskViews.get(index).getBackground();
                dra = (ColorDrawable) drawable;
//                if (maskViews.get(6).getVisibility() == View.INVISIBLE)
                if (dra.getColor() == getResources().getColor(R.color.translucent))
                    clickAble = true;
                break;
            case R.id.click_view8:
                index = 7;
                drawable = maskViews.get(index).getBackground();
                dra = (ColorDrawable) drawable;
//                if (maskViews.get(7).getVisibility() == View.INVISIBLE)
                if (dra.getColor() == getResources().getColor(R.color.translucent))
                    clickAble = true;
                break;
            case R.id.click_view9:
                index = 8;
                drawable = maskViews.get(index).getBackground();
                dra = (ColorDrawable) drawable;
//                if (maskViews.get(8).getVisibility() == View.INVISIBLE)
                if (dra.getColor() == getResources().getColor(R.color.translucent))
                    clickAble = true;
                break;
            case R.id.click_view10:
                index = 9;
                drawable = maskViews.get(index).getBackground();
                dra = (ColorDrawable) drawable;
//                if (maskViews.get(9).getVisibility() == View.INVISIBLE)
                if (dra.getColor() == getResources().getColor(R.color.translucent))
                    clickAble = true;
                break;
            case R.id.click_view11:
                index = 10;
                drawable = maskViews.get(index).getBackground();
                dra = (ColorDrawable) drawable;
//                if (maskViews.get(10).getVisibility() == View.INVISIBLE)
                if (dra.getColor() == getResources().getColor(R.color.translucent))
                    clickAble = true;
                break;
            case R.id.click_view12:
                index = 11;
                drawable = maskViews.get(index).getBackground();
                dra = (ColorDrawable) drawable;
//                if (maskViews.get(11).getVisibility() == View.INVISIBLE)
                if (dra.getColor() == getResources().getColor(R.color.translucent))
                    clickAble = true;
                break;
        }

        timeView.setVisibility(View.INVISIBLE);

        if (!clickAble) {
            return;
        }

        for (int i=0; i!=12; i++) {
            if (i == index) {
                int time = videoViewArrayList.get(i).getCurrentPosition() + 1000;
                videoViewArrayList.get(12).stop();
                setCenterPlayerBlack(false);
                Log.i(TAG, "play index " + i+ " in center URL：%s" + smallVideoUrls.get(i));
                videoViewArrayList.get(12).setDataSource(new DataSource(smallVideoUrls.get(i)));
                videoViewArrayList.get(12).start(time);
                videoViewArrayList.get(12).setVolume(0, 0);
                //把之前播放的窗口设置成透明
                setLastCenterPlayerMaskTransact();
                //当前点击的窗口设置成遮罩
                maskViews.get(index).setBackgroundColor(getResources().getColor(R.color.mask_view_play));
                playersController.updateCenterPlayerInfo(i, time);
                resumeBtn.setVisibility(View.GONE);
                Log.d(TAG, "resumeBtn setVisibility(View.GONE);");
                //隐藏章节标题信息
                chapterTextViewList.get(3).setText("");
                chapterTextViewList.get(4).setText("");
                showArroy(i);
            }
            Log.d(TAG, "set index " + index + " red");
        }

        playersController.resume_();
        videoViewArrayList.get(12).setBackgroundResource(R.drawable.xsl_video_shape_white);
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
                        duration, NetworkReq.getInstance().getVideoLstInfo().getData().get(id1).getVideoUrl360(),
                        NetworkReq.getInstance().getVideoLstInfo().getData().get(id2).getVideoUrl360(), id1, id2);
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
                //Log.d(TAG, "onSubtitleUpdate pts: " + pts);
                Message msg = playControlHandler.obtainMessage(SUBTITLE_UPDATE, pts, 0);
                playControlHandler.sendMessage(msg);
            }
        });

        playersController.setBtnStateListener(new OnBtnStateListener() {
            @Override
            public void onStateChange(int action, boolean enable, String url, String name) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (action) {
                            case OnBtnStateListener.XSL_ACTION_BTN_STATE:
                                setBtnState(enable, 2);
                                mActionUrl = url;
                                actionName = name;
                                break;
                            case OnBtnStateListener.XSL_CHAPTER_BTN_STATE:
                                setBtnState(enable, 0);
                                break;
                            case OnBtnStateListener.XSL_WORD_BTN_STATE:
                                setBtnState(enable, 3);
                        }
                    }
                });
            }

            @Override
            public void onWordStateChange(int action, boolean enable, int objId) {
                setBtnState(enable, 3);
                mWordId = objId;
                //Log.i(TAG, "Word id:" + mWordId);
            }

            @Override
            public void onChapterBtnTextUpdate(int startTime, String text, String chapterName) {
                int chapter = Integer.parseInt(text);
                if (chapter == curChapter)
                    return;

                Log.w(TAG, "onChapterBtnTextUpdate startTime:" + startTime + " text:" + text + " name:" + chapterName);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, "update chapter startTime:" + startTime +" text:" + text);
                        buttonList.get(0).setText(text);
                        curChapter = chapter;
                        seekChapter(startTime, text, chapterName);
                    }
                });
            }

            @Override
            public void onApplience(int action, boolean enable, String url, String name) {
                setBtnState(enable, 1);
                mApplienceUrl = url;
                applienceName = name;
            }
        });

        playersController.setMaskViewListener(new OnMaskViewListener() {
            @Override
            public void setMaskViewStatus(int action, ArrayList<Integer> idLst) {

                for (int i=0; i!=12; i++) {
                    boolean isIdFounded = false;
                    for (Integer id : idLst) {
                        if (i == id) {
                            isIdFounded = true;
//                    Log.d(TAG, "i:" + i +" id:" + id + " true");
                            break;
                        } else {
                            isIdFounded = false;
//                    Log.d(TAG, "i:" + i +" id:" + id + " false");
                        }
                    }

                    int state = 0;
                    if (isIdFounded) {
                        if (playersController.getCenterVideoViewIndex() == i) {

                            Drawable drawable = centerBlackLayout.getBackground();
                            ColorDrawable dra = (ColorDrawable) drawable;
                            if (dra.getColor() == Color.TRANSPARENT) {
//                                maskViews.get(index).setBackgroundColor(getResources().getColor(R.color.translucent));
                            } else {
                                playersController.updateCenterPlayerInfo(-1, videoViewArrayList.get(i).getCurrentPosition());
                                normalSubtitleView.hide();
                            }

                            state = 1;
                        } else {
                            state = -1;
                        }
                    } else {
                        state = 0;
//                        if (i == 6) {
//                            Log.d(TAG, "currentPlayTime" + playersController.getCurrentPosition() + " index 6 time:" + videoViewArrayList.get(6).getCurrentPosition());
//                        }
                    }
                    //-1 透明 0 纯黑 1遮罩 70%透明度
                    Message msg = playControlHandler.obtainMessage(action, i, state);
                    playControlHandler.sendMessage(msg);
                }
            }
        });

        int duration = playersController.getDuration();
        if (duration != 0 && NetworkReq.getInstance().getChapterListInfo() != null) {
            mySeekBar.setChapterListInfo(NetworkReq.getInstance().getChapterListInfo(), duration);
        }
        startPlay();
    }

    public void updateChapterTxt(int chapter, String content) {
        String title = "第";

        switch (chapter) {
            case 1:
                title += "一";
                break;
            case 2:
                title += "二";
                break;
            case 3:
                title += "三";
                break;
            case 4:
                title += "四";
                break;
            case 5:
                title += "五";
                break;
            case 6:
                title += "六";
                break;
            case 7:
                title += "七";
                break;
            case 8:
                title += "八";
                break;
            case 9:
                title += "九";
                break;
            case 10:
                title += "十";
                break;
            case 11:
                title += "十一";
                break;
            case 12:
                title += "十二";
                break;
            case 13:
                title += "十三";
                break;
            case 14:
                title += "十四";
                break;
            case 15:
                title += "十五";
                break;
            case 16:
                title += "十六";
                break;
            case 17:
                title += "十七";
                break;
        }
        title += "章";
        chapterTextViewList.get(0).setText(title);
        chapterTextViewList.get(3).setText(title);
        chapterTextViewList.get(4).setText(content);
        //出现章节的时候要隐藏字幕
        normalSubtitleView.hide();
        int index = content.indexOf(" – ");
        String titleContent = content.substring(0, index);
        content = content.substring(index + 3);
        chapterTextViewList.get(1).setText(titleContent);
        chapterTextViewList.get(2).setText(content);
        curChapter = chapter;
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

    private void createActivity(Class<?> cls, int requestCode, String url, String applienceName) {
        Intent intent = new Intent(MainPlayerActivity.this, cls);
        Bundle bundle = new Bundle();
        bundle.putSerializable(String.valueOf(R.string.applience_url), url);
        bundle.putSerializable(String.valueOf(R.string.applience_name), applienceName);

        intent.putExtras(bundle);
        startActivityForResult(intent, requestCode);
    }

    private void createActionActivity(Class<?> cls, int requestCode, String url, String name) {
        Intent intent = new Intent(MainPlayerActivity.this, cls);
        Bundle bundle = new Bundle();
        bundle.putSerializable(String.valueOf(R.string.action_url), url);
        bundle.putSerializable(String.valueOf(R.string.action_name), name);

        intent.putExtras(bundle);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCode.Mask_req) {
            hadShowPager = true;
            createPlayCtrl();
            SharedPreferenceUtil.getInstance(getApplicationContext()).putBoolean(getResources().getString(R.string.had_show_boot_paper), true);
//            Intent intent = new Intent(MainPlayerActivity.this, DownloadActivity.class);
//            startActivityForResult(intent, RequestCode.Download_req);
        } if (requestCode == RequestCode.Transact1_req) {
            Intent intent = new Intent(MainPlayerActivity.this, Transact2Activity.class);
            intent.putExtra(String.valueOf(R.string.center_play_x), centerX);
            intent.putExtra(String.valueOf(R.string.center_play_y), centerY);
            intent.putExtra(String.valueOf(R.string.center_play_width), centerWidth);
            intent.putExtra(String.valueOf(R.string.center_play_height), centerHeight);
            Log.d(TAG, "centerX:" + centerX + " centerY:" + centerY + " width:" + centerWidth);
            startActivityForResult(intent, RequestCode.Transact2_req);
        } if (requestCode == RequestCode.Transact2_req) {
            Intent intent = new Intent(MainPlayerActivity.this, Transact3Activity.class);
            intent.putExtra(String.valueOf(R.string.center_play_x), centerX);
            intent.putExtra(String.valueOf(R.string.center_play_y), centerY);
            intent.putExtra(String.valueOf(R.string.center_play_width), centerWidth);
            intent.putExtra(String.valueOf(R.string.center_play_height), centerHeight);
            Log.d(TAG, "centerX:" + centerX + " centerY:" + centerY + " width:" + centerWidth);
            startActivityForResult(intent, RequestCode.Transact3_req);
        } if (requestCode == RequestCode.Transact3_req) {
            hadShowPager = true;
            createPlayCtrl();
        } else if (requestCode == RequestCode.Appliance_req) {
            buttonList.get(1).setSelected(false);
//            playersController.resume_();
//            videoViewArrayList.get(12).resume();
        } else if (requestCode == RequestCode.Word_req) {
            buttonList.get(3).setSelected(false);
//            playersController.resume_();
//            videoViewArrayList.get(12).resume();
        } else if (requestCode == RequestCode.Chapter_req) {
            Bundle bd = data.getExtras();
            int chapterIndex = bd.getInt(ChapterActivity.chapter_key);
            Log.d(TAG, "chapter_req return chapterIndex:" + chapterIndex);
            if (chapterIndex < 0) {
                buttonList.get(0).setSelected(false);
                return;
            }
            chapterChange = true;
            ChapterListInfo.DataBean dataBean = NetworkReq.getInstance().getChapterListInfo().getData().get(chapterIndex - 1);

            String text;
            if (chapterIndex < 10) {
                text = "0" + chapterIndex;
            } else {
                text = "" + chapterIndex;
            }
            buttonList.get(0).setText(text);
            buttonList.get(0).setSelected(false);
            bNativeSeekFinish = false;
            seekChapter(dataBean.getStartTime(), dataBean.getCode(), dataBean.getName());
            //下下策，防止底层seek不回调
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    // do something
                    bNativeSeekFinish = true;
                }

            },  500);
        }
    }

    private int curLangugue = SubtitleView.LANGUAGE_TYPE_CHINA;
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

        normalSubtitleView = findViewById(R.id.normal_subtitle);
        normalSubtitleView.setData(subtitleLstCN, SubtitleView.LANGUAGE_TYPE_CHINA);
        normalSubtitleView.setData(subtitleLstCA, SubtitleView.LANGUAGE_TYPE_CANTONESE);
        normalSubtitleView.setData(subtitleLstEN, SubtitleView.LANGUAGE_TYPE_ENGLISH);

        Log.d("Subtitle", "ca size:"+subtitleLstCA.size());
        normalSubtitleView.setLanguage(curLangugue);
    }

    @BindView(R.id.play_touch_mask)
    PlayControlMaskView maskView;

    private void updateSubtitle(int pts) {
        //反正有标题的时候还出现字幕
        if (chapterTextViewList.get(3).getText().length() > 0) {
            normalSubtitleView.hide();
            return;
        }

        normalSubtitleView.seekTo(pts);
    }

    public void setCenterPlayerBlack(boolean yes) {
        if (yes) {
            Log.d(TAG, "setCenterPlayerBlack black");
            centerBlackLayout.setBackgroundColor(Color.BLACK);
            normalSubtitleView.hide();
        } else {
            Log.d(TAG, "setCenterPlayerBlack transparent");
            centerBlackLayout.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    long lastTouchTime = 0;
    /**
     * 滑动seek,中间有视频的时候，是在当前timeLine时间范围内seek,
     * 当前没视频的时候是在当前章节范围内seek
     * @param jumpTime
     */
    public void touchMoveSeek(int jumpTime) {
        if (bNativeSeekFinish == false)
            return;
        if (lastTouchTime == 0) {
            lastTouchTime = System.currentTimeMillis();
        } else {
            if (System.currentTimeMillis() - lastTouchTime < 200) {
                return;
            }

            lastTouchTime = System.currentTimeMillis();
        }

        int curPlayTime = videoViewArrayList.get(0).getCurrentPosition() + jumpTime * 1000;
        int startTime = 0, endTime = 0;
        if (videoViewArrayList.get(12).getState() == IPlayer.STATE_STOPPED ||
                videoViewArrayList.get(12).getState() == IPlayer.STATE_IDLE) {
            ChapterListInfo infos = NetworkReq.getInstance().getChapterListInfo();
            for (ChapterListInfo.DataBean dataBean : infos.getData()) {
                if (Integer.parseInt(dataBean.getCode()) == curChapter) {
                    startTime = dataBean.getStartTime() * 1000;
                    endTime = (dataBean.getStartTime() + dataBean.getDuration()) * 1000;

                    if (Math.abs(startTime - curPlayTime) < 800) {
                        break;
                    }
                    if (startTime > curPlayTime) {
                        //第十二章节倒退的时候会倒回到前一个章节视频，多加一点
                        if (curChapter == 12) {
                            curPlayTime = startTime + 800;
                        } else if (curChapter == 9) {
                            curPlayTime = startTime + 1000;
                        } else {
                            curPlayTime = startTime + 500;
                        }
                        Log.i(TAG, "centerPlayer stoped. seekTo chapter " + curChapter + " chapterStartTime " + startTime);
                        setCenterPlayerBlack(true);
                    } else if (curPlayTime >= endTime) {
                        curPlayTime = endTime - 400;
                        Log.i(TAG, "centerPlayer stoped. seekTo chapter " + curChapter + " endTime " + curPlayTime);
                        setCenterPlayerBlack(true);
                    }
                    playersController.seekTo_(curPlayTime);
                    break;
                }
            }
        } else {
            startTime = playersController.getCurTimeLineStartTime();
            endTime = startTime + playersController.getCurTimeLineEndTime();
            if (startTime > curPlayTime) {
                curPlayTime = startTime + 500;

                Log.i(TAG, "centerPlayer seekTo timeLine startTime" + curPlayTime);
            } else if (curPlayTime > endTime) {
                curPlayTime = endTime - 500;
                Log.i(TAG, "centerPlayer seekTo timeLine endTime " + curPlayTime);
            }

            playersController.seekNotify(curPlayTime);
            bNativeSeekFinish = false;
            //下下策，防止底层seek不回调
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    // do something
                    bNativeSeekFinish = true;
                }

            },  1000);
        }

//        int duration = playersController.getDuration();
        int duration = endTime;
        String strCurPlayTime = XslUtils.convertSecToTimeString(curPlayTime / 1000);
        String strDuration = XslUtils.convertSecToTimeString(duration / 1000);
        timeView.setText(strCurPlayTime + "/" + strDuration);
        timeView.setVisibility(View.VISIBLE);

        resumeBtn.setVisibility(View.GONE);
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                //要执行的操作
//                timeView.setVisibility(View.GONE);
//            }
//        }, 1000);
    }

    public void touchUp() {
        timeView.setVisibility(View.INVISIBLE);
        if (videoViewArrayList.get(12).getState() == IPlayer.STATE_PAUSED) {
            resumeBtn.setVisibility(View.VISIBLE);
        }
    }

    public void touchMaskView() {
        int state = videoViewArrayList.get(12).getState();
        if (state == IPlayer.STATE_STARTED) {
            playersController.pause_();
            resumeBtn.setVisibility(View.VISIBLE);
        } else if (state == IPlayer.STATE_PAUSED) {
            playersController.resume_();
            resumeBtn.setVisibility(View.GONE);
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
//        if (videoViewArrayList.get(12).getState() == IPlayer.STATE_PAUSED) {
//            playersController.resume_();
//            videoViewArrayList.get(12).resume();
//        }
        super.onResume();

        if (centerPlayerPlaying && !chapterChange) {
            playersController.resume_();
            resumeBtn.setVisibility(View.GONE);
        }

        chapterChange = false;

        Log.d(TAG, "xx onResume");
    }

    @Override
    protected void onDestroy() {
        closeAllPlayers();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.d(TAG, "xx onStop");
    }

    @Override
    protected void onPause() {

        Log.d(TAG, "videoViewArrayList.get(12).getState(): " + videoViewArrayList.get(12).getState());
        if (videoViewArrayList.get(0).getState() == IPlayer.STATE_STARTED) {
            resumeBtn.setVisibility(View.VISIBLE);
            playersController.pause_();
        }
        super.onPause();
        Log.d(TAG, "xx onPause");
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

        //设置右侧按钮
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
        int smallHeight = linearParams.height;

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
        int smallWidth = linearParams.width;

        p11.setLayoutParams(linearParams);

        linearParams = (LinearLayout.LayoutParams)p5.getLayoutParams();
        //linearParams.width = (dm.widthPixels - convertDpToPixel(60))/5;
        linearParams.width = needWidth / 5 - 12;
        Log.d(TAG, "width:"+ linearParams.width);

        //linearParams.height = linearParams.width * 9 / 16;
        p5.setLayoutParams(linearParams);

//        centerX = findViewById(R.id.p13).getLeft();
//        centerY = findViewById(R.id.p13).getRight();
//        centerWidth = findViewById(R.id.p13).getWidth();
//        centerHeight= findViewById(R.id.p13).getHeight();

        //添加遮罩
        //addMaskView();

        centerX = convertDpToPixel(58) + smallWidth;
        centerY = cotrollerHeight + smallHeight + 16;
        centerWidth = smallWidth * 3 + 6;
        centerHeight = smallHeight * 3 + 3;
        Log.d(TAG, "centerX: " + centerX + " y:" + centerY + " width:" + centerWidth + " centerHeight:" + centerHeight);
    }

    int centerX, centerY, centerWidth, centerHeight;


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

    int k = 0;
    void updatePlayCtroller(int duration, int curTime) {
        //Log.d(TAG, "main player statues:"+videoViewArrayList.get(12).getState());
        if (duration <= 0)
            return;
        int progress = curTime  / (duration / 1000);
//        if (++k % 30 == 0) {
//            Log.d(TAG, "progress: " + progress + " curTime:" + curTime);
//        }
        mySeekBar.setProgress(progress);
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

    public static boolean bNativeSeekFinish = true;

    void setListenVideoView(BaseVideoView videoView) {
        //Log.d(TAG, "setListenVideoView thread id:%d" + Thread.currentThread().getId());
        videoView.setOnPlayerEventListener(new OnPlayerEventListener() {
            @Override
            public void onPlayerEvent(int eventCode, Bundle bundle) {
                //Log.d(TAG, "onPlayerEvent thread id:%d" + Thread.currentThread().getId());
                if (eventCode == OnPlayerEventListener.PLAYER_EVENT_ON_TIMER_UPDATE) {

                } else if (eventCode == OnPlayerEventListener.PLAYER_EVENT_ON_SEEK_COMPLETE) {
                    Log.d(TAG, "mySeek finish, get pos:" + videoView.getCurrentPosition());
                    new Handler().postDelayed(new Runnable() {
                        //等待小视频seek完，反正controller在小视频没有seek完情况下轮训
                        public void run() {
                            // do something
                            bNativeSeekFinish = true;
                        }

                    },  500);
                    playersController.seekTo_(videoViewArrayList.get(12).getCurrentPosition());
                } else if (eventCode == OnPlayerEventListener.PLAYER_EVENT_ON_PLAY_COMPLETE) {
                    Log.d(TAG, "PLAYER_EVENT_ON_PLAY_COMPLETE");
                } else if (eventCode == OnPlayerEventListener.PLAYER_EVENT_ON_START) {
                    Log.d(TAG, "main player started");

//                    TimerTask task = new TimerTask() {
//                        @Override
//                        public void run() {
//                            //要执行的操作
//                            videoView.pause();
//
//                        }
//                    };
//                    Timer timer = new Timer();
//                    timer.schedule(task, 500);//2秒后执行TimeTask的run方法
                }
            }
        });

        videoView.setOnErrorEventListener(new OnErrorEventListener() {
            @Override
            public void onErrorEvent(int eventCode, Bundle bundle) {

            }
        });
    }

    private void startPlay() {
        if (videoViewArrayList != null) {
            playersController.startPlay_();
            playersController.start_();
            setListenVideoView(videoViewArrayList.get(12));
            showArroy(-1);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    /**
                     *要执行的操作
                     */
                    for (BaseVideoView videoView : videoViewArrayList) {
                        videoView.invalidate();
                    }
                }
            }, 1000);//3秒后执行Runnable中的run方法

            //如果没有播放，说明第一次播放，那么延时1s暂停，
            playControlHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    playersController.pause_();
                    ChapterListInfo infos = NetworkReq.getInstance().getChapterListInfo();
                    for (ChapterListInfo.DataBean dataBean : infos.getData()) {
                        if (Integer.parseInt(dataBean.getCode()) == 1) {
                            updateChapterTxt(Integer.parseInt(dataBean.getCode()), dataBean.getName());
                            break;
                        }
                    }
                    //mainPlayerActivityWeakReference.get().resumeBtn.setVisibility(View.VISIBLE);
                }
            }, 1000);
        }
    }

    @BindView(R.id.center_black)
    RelativeLayout centerBlackLayout;


    /**
     * @param index
     * @param state -1 透明 0 纯黑 1遮罩 70%透明度
     */
    private void updateSmallPlayerMaskViewBkg(int index, int state) {
        Drawable drawable = maskViews.get(index).getBackground();
        ColorDrawable dra = (ColorDrawable) drawable;
        if (state == -1) {
            if (dra.getColor() != getResources().getColor(R.color.translucent)) {
                Log.w(TAG, "set index:" + index + " translucent");
                maskViews.get(index).setBackgroundColor(getResources().getColor(R.color.translucent));
            }
        } else if (state == 0) {
            if (dra.getColor() != getResources().getColor(R.color.mask_view_color)) {
                Log.i(TAG, "set index:" + index + " black");
                //showArroy(index);
                maskViews.get(index).setBackgroundColor(getResources().getColor(R.color.mask_view_color));
            }
        } else {
            if (dra.getColor() != getResources().getColor(R.color.mask_view_play)) {
                Log.d(TAG, "set index:" + index + " alpha");
                maskViews.get(index).setBackgroundColor(getResources().getColor(R.color.mask_view_play));
            }
        }
    }

    private void setLastCenterPlayerMaskTransact() {
        //把之前播放的窗口设置成透明
        int curIndex = playersController.getCenterVideoViewIndex();
        if (curIndex != -1) {
            Log.i(TAG, "set play index " + curIndex + " translucent");
            maskViews.get(curIndex).setBackgroundColor(getResources().getColor(R.color.translucent));
        }
    }

    private void setSmallPlayerMaskBlack() {
        //把之前播放的窗口设置成透明
        int curIndex = playersController.getCenterVideoViewIndex();
        for (int i=0; i!=12; i++) {
            Log.i(TAG, "set play index " + curIndex + " black");
            maskViews.get(i).setBackgroundColor(getResources().getColor(R.color.mask_view_color));
        }
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
                case OnPlayCtrlEventListener.PLAY_TIMELINE_CHANGE:
                    videoViewLst.get().get(12).stop();
                    Log.d(TAG, "timeline change");
                    mainPlayerActivityWeakReference.get().setCenterPlayerBlack(true);
                    mainPlayerActivityWeakReference.get().showArroy(-1);
//                    videoViewLst.get().get(msg.arg2).setBackgroundResource(R.drawable.xsl_video_shape);
//                    videoViewLst.get().get(12).setBackgroundResource(R.drawable.xsl_video_shape);

                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mainPlayerActivityWeakReference.get().playersController.pause_();
                        }
                    }, 300);
                    break;
                case OnPlayCtrlEventListener.PLAY_CHAPTER_CHANGE:
                    mainPlayerActivityWeakReference.get().setCenterPlayerBlack(true);
                    //mainPlayerActivityWeakReference.get().resumeBtn.setVisibility(View.VISIBLE);
                    break;
                case OnPlayCtrlEventListener.STOP_CTRL:
                    videoViewLst.get().get(12).stop();
                    break;
                case OnPlayCtrlEventListener.PLAY_TIME_SET_CTRL:
                    mainPlayerActivityWeakReference.get().updatePlayCtroller(msg.arg1, msg.arg2);
                    break;
                case OnMaskViewListener.ACTION_PLAY_MASK:
                    mainPlayerActivityWeakReference.get().updateSmallPlayerMaskViewBkg(msg.arg1, msg.arg2);
                    break;
                case OnPlayCtrlEventListener.SUBTITLE_UPDATE:
                    mainPlayerActivityWeakReference.get().updateSubtitle(msg.arg1);
                    break;
            }
        }
    }
}
