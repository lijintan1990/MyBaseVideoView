package com.example.mybasevideoview;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Build;
import android.os.Bundle;
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

import com.example.mybasevideoview.controller.OnBtnStateListener;
import com.example.mybasevideoview.controller.OnPlayCtrlEventListener;
import com.example.mybasevideoview.controller.PlayersController;
import com.example.mybasevideoview.model.ChapterListInfo;
import com.example.mybasevideoview.model.DataType;
import com.example.mybasevideoview.model.HomePageInfo;
import com.example.mybasevideoview.model.ObtainNetWorkData;
import com.example.mybasevideoview.model.PlayData;
import com.example.mybasevideoview.model.RequestCode;
import com.example.mybasevideoview.model.TimeLineInfo;
import com.example.mybasevideoview.model.VideoListInfo;
import com.example.mybasevideoview.play.DataInter;

import com.example.mybasevideoview.utils.XslUtils;
import com.example.mybasevideoview.view.AboutActivity;
import com.example.mybasevideoview.view.AppliancesActivity;
import com.example.mybasevideoview.view.ChapterActivity;
import com.example.mybasevideoview.view.MySeekBar;
import com.example.mybasevideoview.view.RelateHorizonActivity;
import com.example.mybasevideoview.view.RelateVerticalActivity;
import com.example.mybasevideoview.view.TransactActivity;
import com.example.mybasevideoview.view.langugueActivity;
import com.kk.taurus.playerbase.assist.InterEvent;
import com.kk.taurus.playerbase.assist.OnVideoViewEventHandler;
import com.kk.taurus.playerbase.entity.DataSource;
import com.kk.taurus.playerbase.event.OnErrorEventListener;
import com.kk.taurus.playerbase.event.OnPlayerEventListener;
import com.kk.taurus.playerbase.receiver.OnReceiverEventListener;
import com.kk.taurus.playerbase.receiver.ReceiverGroup;
import com.kk.taurus.playerbase.widget.BaseVideoView;

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
    private static final String TAG = "MainPlayerActivity";
    BaseVideoView videoView[];
    ArrayList<BaseVideoView> videoViewArrayList;
    private boolean userPause;
    private boolean isLandscape;
    PlayersController playersController = null;
    PlayControlHandler playControlHandler = null;

    ImageButton appliances = null;
    ImageButton langugueBtn = null;
    static boolean mNeedStartTransactAty = true;
    private ReceiverGroup mReceiverGroup;
    //public static final String sRelateInfo = "relateInfo";
    public static final String RELATE_ID_ONE = "RELATE_ID_ONE";
    public static final String RELATE_ID_TWO = "RELATE_ID_TWO";

    @BindViews({R.id.about_btn, R.id.langugue_btn, R.id.appliances_btn, R.id.action_btn, R.id.chapter_btn, R.id.word_btn, R.id.back_btn})
    List<ImageButton> buttonList;
    @BindViews({R.id.main_controller_text_view_curr_time, R.id.main_controller_text_view_total_time})
    List<TextView> textViews;
    @BindView(R.id.main_controller_image_view_play_state)
    ImageView ctrlImageView;

    private MySeekBar mySeekBar;

    // 名物視頻的地址
    String mApplienceUrl = null;
    // 動作視頻的地址
    String mActionUrl = null;

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

        //buttonList.get(2).getBackground().setAlpha(50);

        init();
        if (mNeedStartTransactAty) {
            Log.d(TAG, "start transactActivity");
            createActivity(TransactActivity.class, RequestCode.Transact_req);
            mNeedStartTransactAty = false;
        } else {
            createPlayCtrl();
        }
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
        } else {
            playersController.resume_();
            ctrlImageView.setSelected(false);
        }
    }

    /**
     * 使所有按钮变灰并且不可点击
     */
    private void disableAllBtn() {
        for (int i=0; i!= 6; i++) {
            buttonList.get(i).getBackground().setAlpha(50);
            buttonList.get(i).setClickable(false);
        }
    }

    void init() {
        mySeekBar = findViewById(R.id.main_controller_seek_bar);
        mySeekBar.setMax(1000);
        reLayout();
        disableAllBtn();
        videoViewArrayList = new ArrayList<>();
        videoViewArrayList.add(findViewById(R.id.p1));
        videoViewArrayList.add(findViewById(R.id.p2));
        videoViewArrayList.add(findViewById(R.id.p3));
        videoViewArrayList.add(findViewById(R.id.p4));
        videoViewArrayList.add(findViewById(R.id.p5));
        videoViewArrayList.add(findViewById(R.id.p6));
        videoViewArrayList.add(findViewById(R.id.p7));
        videoViewArrayList.add(findViewById(R.id.p8));
        videoViewArrayList.add(findViewById(R.id.p9));
        videoViewArrayList.add(findViewById(R.id.p10));
        videoViewArrayList.add(findViewById(R.id.p11));
        videoViewArrayList.add(findViewById(R.id.p12));
        videoViewArrayList.add(findViewById(R.id.p13));

        getVideoList();
        getTimeLine();
        getChapter();
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
            public void onPlayRelateVideos(int action, int id1, int id2) {
                Message msg = playControlHandler.obtainMessage(action, id1, id2);
                playControlHandler.sendMessage(msg);
            }

            @Override
            public void onPlayTimeCallback(int action, int duration, int curTime) {
                Message msg = playControlHandler.obtainMessage(action, duration, curTime);
                playControlHandler.sendMessage(msg);
            }
        });

        playersController.setBtnStateListener(new OnBtnStateListener() {
            @Override
            public void onStateChange(int action, String url) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (action) {
                            case OnBtnStateListener.XSL_APPLIANCE_ENABLE:
                                buttonList.get(2).setClickable(true);
                                buttonList.get(2).getBackground().setAlpha(255);
                                mApplienceUrl = url;
                                break;
                            case OnBtnStateListener.XSL_ACTION_ENABLE:
                                buttonList.get(3).setClickable(true);
                                buttonList.get(3).getBackground().setAlpha(255);
                                mActionUrl = url;
                        }
                    }
                });
            }
        });

        playersController.startPlay_();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCode.Transact_req) {
            mNeedStartTransactAty = false;
            createPlayCtrl();
        } else if (requestCode == RequestCode.About_req) {
            buttonList.get(0).setSelected(false);
        } else if (requestCode == RequestCode.Relate_req) {
            for (BaseVideoView videoView : videoViewArrayList) {
                videoView.resume();
            }
        } else if (requestCode == RequestCode.Languge_req) {
            int langugueSelector = 0;
            Bundle bd = data.getExtras();
            langugueSelector = bd.getInt(langugueActivity.langugue_key);

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
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        closeAllPlayers();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


//    public static class RelateVideoInfo implements Serializable {
//        private int id1;
//        private int id2;
//        private String uri_1;
//        private String uri_2;
//
//        public RelateVideoInfo(int id1, int id2, String uri_1, String uri_2) {
//            this.id1 = id1;
//            this.id2 = id2;
//            this.uri_1 = uri_1;
//            this.uri_2 = uri_2;
//        }
//
//        public int getId1() {
//            return id1;
//        }
//
//        public void setId1(int id1) {
//            this.id1 = id1;
//        }
//
//        public int getId2() {
//            return id2;
//        }
//
//        public void setId2(int id2) {
//            this.id2 = id2;
//        }
//
//        public String getUri_1() {
//            return uri_1;
//        }
//
//        public void setUri_1(String uri_1) {
//            this.uri_1 = uri_1;
//        }
//
//        public String getUri_2() {
//            return uri_2;
//        }
//
//        public void setUri_2(String uri_2) {
//            this.uri_2 = uri_2;
//        }
//    }

    private void playAll() {
        for (int i=0; i!=12; i++) {
            videoViewArrayList.get(i).setDataSource(new DataSource(mVideolst.getData().get(i).getVideoUrl90()));
            videoViewArrayList.get(i).start();
        }
    }

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

                if (playersController != null)
                {
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

    ChapterListInfo chapterListInfo = null;
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

                if (playersController != null)
                {
                    int duration = playersController.getDuration();
                    if (duration != 0) {
                        mySeekBar.setChapterListInfo(chapterListInfo, duration);
                    }
                }
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
        //设置顶层和底层的播放空间的高度
        BaseVideoView p1 = findViewById(R.id.p1);
        BaseVideoView p8 = findViewById(R.id.p8);

        int seekbarHeight = 24;
        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams)p1.getLayoutParams();
        linearParams.height = (dm.heightPixels - seekbarHeight)/5;
        p1.setLayoutParams(linearParams);
        p8.setLayoutParams(linearParams);

        //设置左边和右边播放控件的宽度
        BaseVideoView p11 = findViewById(R.id.p11);
        BaseVideoView p5 = findViewById(R.id.p5);
        linearParams = (LinearLayout.LayoutParams)p11.getLayoutParams();
        //28+20+10是左边返回控件64dp和右边介绍按钮30dp + 20dp
        linearParams.width = (dm.widthPixels - convertDpToPixel(108))/5 - convertDpToPixel(1);
        p11.setLayoutParams(linearParams);

        linearParams = (LinearLayout.LayoutParams)p5.getLayoutParams();
        linearParams.width = (dm.widthPixels - convertDpToPixel(60))/5;
        p5.setLayoutParams(linearParams);
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

    void updataPlayCtroller(int duration, int curTime) {
        if (duration <= 0)
            return;
        mySeekBar.setProgress(curTime * 1000 / duration);
        //当前时间设置
        textViews.get(0).setText(XslUtils.convertSecToTimeString(curTime));
        //总时间设置
        textViews.get(1).setText(XslUtils.convertSecToTimeString(duration));
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
                switch (eventCode){
                    case InterEvent.CODE_REQUEST_PAUSE:
                        userPause = true;
                        break;
                    case DataInter.Event.EVENT_CODE_REQUEST_BACK:
                        if(isLandscape){
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        }else{
                            finish();
                        }
                        break;
                    case DataInter.Event.EVENT_CODE_REQUEST_TOGGLE_SCREEN:
                        setRequestedOrientation(isLandscape ?
                                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        break;
                    case DataInter.Event.EVENT_CODE_ERROR_SHOW:
                        //videoViewArrayList.get(17).stop();
                        break;
                }
            }

        });
    }

    public static class PlayControlHandler extends Handler {
        WeakReference<ArrayList<BaseVideoView>> videoViewLst;
        WeakReference<MainPlayerActivity> mainPlayerActivityWeakReference;
        public PlayControlHandler(ArrayList<BaseVideoView> lst, MainPlayerActivity mainPlayerActivity) {
            videoViewLst = new WeakReference<ArrayList<BaseVideoView>>(lst);
            mainPlayerActivityWeakReference = new WeakReference<MainPlayerActivity>(mainPlayerActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //action, centerType, videoViewIndex, playDataList.get(playDataIndex)
                case OnPlayCtrlEventListener.PLAY_CTRL:
                    if (videoViewLst.get() != null) {
                        if (msg.arg1 == OnPlayCtrlEventListener.CENTER_FULL) {
                            videoViewLst.get().get(12).setDataSource(new DataSource(mVideolst.getData().get(msg.arg2).getVideoUrl360()));
                            videoViewLst.get().get(12).start();
                        }
                    }
                    break;
                case OnPlayCtrlEventListener.PLAY_ALL_CTRL:
                    mainPlayerActivityWeakReference.get().playAll();
                    break;
                case OnPlayCtrlEventListener.STOP_CTRL:
                    break;
                case OnPlayCtrlEventListener.PLAY_RELATE_VERTICAL_CTRL:
                    mainPlayerActivityWeakReference.get().playersController.pause_();
                    Intent intent = new Intent(mainPlayerActivityWeakReference.get(), RelateVerticalActivity.class);
                    intent.putExtra(RELATE_ID_ONE, msg.arg1);
                    intent.putExtra(RELATE_ID_TWO, msg.arg2);
                    mainPlayerActivityWeakReference.get().startActivityForResult(intent, RequestCode.Relate_req);
                    break;
                case OnPlayCtrlEventListener.PLAY_RELATE_HORIZON_CTRL:
                    mainPlayerActivityWeakReference.get().playersController.pause_();
                    Intent intent1 = new Intent(mainPlayerActivityWeakReference.get(), RelateHorizonActivity.class);
                    intent1.putExtra(RELATE_ID_ONE, msg.arg1);
                    intent1.putExtra(RELATE_ID_TWO, msg.arg2);
                    //intent1.putExtra(RELATE_ID_ONE, videoViewLst.get());
                    mainPlayerActivityWeakReference.get().startActivityForResult(intent1, RequestCode.Relate_req);
                    //mainPlayerActivityWeakReference.get().createActivity(RelateHorizonActivity.class, 2);
                    break;
                case OnPlayCtrlEventListener.PLAY_TIME_SET_CTRL:
                    mainPlayerActivityWeakReference.get().updataPlayCtroller(msg.arg1, msg.arg2);
            }
        }
    }
}
