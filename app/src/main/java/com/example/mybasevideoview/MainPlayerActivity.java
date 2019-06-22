package com.example.mybasevideoview;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.mybasevideoview.controller.OnPlayCtrlEventListener;
import com.example.mybasevideoview.controller.PlayersController;
import com.example.mybasevideoview.cover.CompleteCover;
import com.example.mybasevideoview.cover.ControllerCover;
import com.example.mybasevideoview.cover.ErrorCover;
import com.example.mybasevideoview.cover.GestureCover;
import com.example.mybasevideoview.cover.LoadingCover;
import com.example.mybasevideoview.model.HomePageInfo;
import com.example.mybasevideoview.model.ObtainNetWorkData;
import com.example.mybasevideoview.model.PlayData;
import com.example.mybasevideoview.model.TimeLineInfo;
import com.example.mybasevideoview.play.DataInter;

import com.example.mybasevideoview.utils.XslUtils;
import com.example.mybasevideoview.view.ChapterActivity;
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

import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.mybasevideoview.play.DataInter.ReceiverKey.KEY_COMPLETE_COVER;
import static com.example.mybasevideoview.play.DataInter.ReceiverKey.KEY_CONTROLLER_COVER;
import static com.example.mybasevideoview.play.DataInter.ReceiverKey.KEY_ERROR_COVER;
import static com.example.mybasevideoview.play.DataInter.ReceiverKey.KEY_GESTURE_COVER;

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
    boolean mNeedStartTransactAty = true;
    private ReceiverGroup mReceiverGroup;

    //activity 相关返回值
    public static final int ACT_TRANSACT = 1; //主面板引导页
    public static final int ACT_VIDEO_RELATE = 2; // 关联视频

    public static final String sRelateInfo = "relateInfo";

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

        if (mNeedStartTransactAty) {
            Log.d(TAG, "start transactActivity");
            createActivity(TransactActivity.class, 1);
        }
    }

    @BindViews({R.id.about_btn, R.id.langugue_btn, R.id.appliances_btn, R.id.action_btn, R.id.chapter_btn, R.id.word_btn})
    List<ImageButton> buttonList;

    @OnClick({R.id.about_btn, R.id.langugue_btn, R.id.appliances_btn, R.id.action_btn, R.id.chapter_btn, R.id.word_btn})
    void buttonClick(View view) {
        switch (view.getId()) {
            case R.id.about_btn:
                buttonList.get(0);
                break;
            case R.id.action_btn:
                break;
            case R.id.appliances_btn:
                if (buttonList.get(2).isSelected()) {
                    buttonList.get(2).setSelected(false);
                } else {
                    buttonList.get(2).setSelected(true);
                }
                break;
            case R.id.chapter_btn:
                if (buttonList.get(4).isSelected()) {
                    buttonList.get(4).setSelected(false);
                } else {
                    buttonList.get(4).setSelected(true);
                }
                createActivity(ChapterActivity.class, 4);
                break;
            case R.id.word_btn:
                break;
            case R.id.langugue_btn:
                if (buttonList.get(1).isSelected()) {
                    buttonList.get(1).setSelected(false);
                } else {
                    buttonList.get(1).setSelected(true);
                }
                createActivity(langugueActivity.class, 1);
                break;
        }
    }

    public void createActivity(Class<?> cls, int requestCode) {
        Intent intent = new Intent(MainPlayerActivity.this, cls);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            mNeedStartTransactAty = false;
        } else if (requestCode == 2) {

        }
    }

    @Override
    protected void onResume() {
        /**
         * 设置为横屏
         */
        if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            reLayout();
            init();
        }

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

    void init() {
        getTimeLine();
        videoViewArrayList = new ArrayList<BaseVideoView>();
        videoViewArrayList.add((BaseVideoView)findViewById(R.id.p1));
        videoViewArrayList.add((BaseVideoView)findViewById(R.id.p2));
        videoViewArrayList.add((BaseVideoView)findViewById(R.id.p3));
        videoViewArrayList.add((BaseVideoView)findViewById(R.id.p4));
        videoViewArrayList.add((BaseVideoView)findViewById(R.id.p5));
        videoViewArrayList.add((BaseVideoView)findViewById(R.id.p6));
        videoViewArrayList.add((BaseVideoView)findViewById(R.id.p7));
        videoViewArrayList.add((BaseVideoView)findViewById(R.id.p8));
        videoViewArrayList.add((BaseVideoView)findViewById(R.id.p9));
        videoViewArrayList.add((BaseVideoView)findViewById(R.id.p10));
        videoViewArrayList.add((BaseVideoView)findViewById(R.id.p11));
        videoViewArrayList.add((BaseVideoView)findViewById(R.id.p12));
        videoViewArrayList.add((BaseVideoView)findViewById(R.id.p13));

        //测试边框
        videoViewArrayList.get(0).setBoardColor(Color.RED, true);

//        ReceiverGroup receiverGroup = new ReceiverGroup();
//        receiverGroup.addReceiver(DataInter.ReceiverKey.KEY_LOADING_COVER, new LoadingCover(this));
//        receiverGroup.addReceiver(DataInter.ReceiverKey.KEY_CONTROLLER_COVER, new ControllerCover(this));
//        receiverGroup.addReceiver(DataInter.ReceiverKey.KEY_COMPLETE_COVER, new CompleteCover(this));
//        receiverGroup.addReceiver(DataInter.ReceiverKey.KEY_ERROR_COVER, new ErrorCover(this));
//        videoViewArrayList.get(17).setReceiverGroup(receiverGroup);
//        videoViewArrayList.get(17).setOnPlayerEventListener(this);
//        videoViewArrayList.get(17).setDataSource(new DataSource("https://mov.bn.netease.com/open-movie/nos/mp4/2016/01/11/SBC46Q9DV_hd.mp4"));
//        videoViewArrayList.get(17).start();
//        setListenVideoView(videoViewArrayList.get(17));


        //给第一个视频添加进度条
//        ReceiverGroup receiverGroup = new ReceiverGroup(null);
//        receiverGroup.addReceiver(KEY_CONTROLLER_COVER, new ControllerCover(this));
//        receiverGroup.addReceiver(KEY_ERROR_COVER, new ErrorCover(this));
//        receiverGroup.addReceiver(KEY_COMPLETE_COVER, new CompleteCover(this));
//        receiverGroup.addReceiver(KEY_GESTURE_COVER, new GestureCover(this));
//        receiverGroup.addReceiver(KEY_ERROR_COVER, new ErrorCover(this));
//        videoViewArrayList.get(12).setReceiverGroup(receiverGroup);


        playControlHandler = new PlayControlHandler(videoViewArrayList, this);
        playersController = new PlayersController(videoViewArrayList);
        playersController.setCtrlEventListener(new OnPlayCtrlEventListener() {
            @Override
            public void onPlayCtrlCallback(int action, TimeLineInfo.DataBean dataBean, int videoViewIndex, int centerType) {
                Message msg = playControlHandler.obtainMessage(action, centerType, videoViewIndex, dataBean);
                playControlHandler.sendMessage(msg);
            }

            @Override
            public void onPlayRelateVideos(int action, int id1, int id2, String uri_1, String uri_2) {
                RelateVideoInfo info = new RelateVideoInfo(id1, id2, uri_1, uri_2);
                Message msg = playControlHandler.obtainMessage(action, info);
                playControlHandler.sendMessage(msg);
            }
        });
        playersController.startPlay();
    }

    public static class RelateVideoInfo implements Serializable {
        private int id1;
        private int id2;
        private String uri_1;
        private String uri_2;

        public RelateVideoInfo(int id1, int id2, String uri_1, String uri_2) {
            this.id1 = id1;
            this.id2 = id2;
            this.uri_1 = uri_1;
            this.uri_2 = uri_2;
        }

        public int getId1() {
            return id1;
        }

        public void setId1(int id1) {
            this.id1 = id1;
        }

        public int getId2() {
            return id2;
        }

        public void setId2(int id2) {
            this.id2 = id2;
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
    }

    static TimeLineInfo mTimelineInfo = null;
    private void getTimeLine() {
        ObtainNetWorkData.getTimelineData(new Callback<TimeLineInfo>() {
            @Override
            public void onResponse(Call<TimeLineInfo> call, Response<TimeLineInfo> response) {
                Log.d(TAG, "get homepage data success");
                mTimelineInfo = response.body();
                Log.d(TAG, "onResponse thread id:"+Thread.currentThread().getId());
            }

            @Override
            public void onFailure(Call<TimeLineInfo> call, Throwable t) {
                Log.w(TAG, "get homepage failed, "+t.toString());
            }
        });
    }

    public static TimeLineInfo getTimelineInfo() {
        return mTimelineInfo;
    }

    void closeAllPlayers() {
        playersController.stopController();

        for (BaseVideoView videoView : videoViewArrayList) {
            videoView.stop();
            videoView.stopPlayback();
        }
    }

    void reLayout() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        System.out.println("width-display :" + dm.widthPixels);
        System.out.println("heigth-display :" + dm.heightPixels);
        //设置顶层和底层的播放空间的高度
        BaseVideoView p1 = findViewById(R.id.p1);
        BaseVideoView p8 = findViewById(R.id.p8);

        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams)p1.getLayoutParams();
        linearParams.height = (dm.heightPixels - 20)/5;
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
            int cameraId;
            switch (msg.what) {
                //action, centerType, videoViewIndex, playDataList.get(playDataIndex)
                case OnPlayCtrlEventListener.PLAY_CTRL:
                    TimeLineInfo.DataBean dataBean = (TimeLineInfo.DataBean) msg.obj;

                    if (videoViewLst.get() != null) {
                        cameraId = msg.arg2;
                        Log.d(TAG, "play cameraId:"+ cameraId);
                        //videoViewLst.get().get(cameraId).setmIndexInDataBeanLst(dataBean.getIndex());
                        videoViewLst.get().get(cameraId).setDataSource(new DataSource(dataBean.getVideo().getVideoUrl()));
                        videoViewLst.get().get(cameraId).start();
                    }
                    break;
                case OnPlayCtrlEventListener.STOP_CTRL:
                    break;
                case OnPlayCtrlEventListener.PLAY_RELATE_VERTICAL_CTRL:
                    Intent intent = new Intent(mainPlayerActivityWeakReference.get(), RelateVerticalActivity.class);
                    intent.putExtra(sRelateInfo, (RelateVideoInfo)msg.obj);
                    mainPlayerActivityWeakReference.get().startActivityForResult(intent, 2);
                    break;
                case OnPlayCtrlEventListener.PLAY_RELATE_HORIZON_CTRL:
                    Intent intent1 = new Intent(mainPlayerActivityWeakReference.get(), RelateHorizonActivity.class);
                    intent1.putExtra(sRelateInfo, (RelateVideoInfo)msg.obj);
                    mainPlayerActivityWeakReference.get().startActivityForResult(intent1, 2);
                    //mainPlayerActivityWeakReference.get().createActivity(RelateHorizonActivity.class, 2);
                    break;
            }
        }
    }
}
