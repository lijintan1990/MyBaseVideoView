package com.example.mybasevideoview.controller;
import android.util.Log;

import com.example.mybasevideoview.model.PlayData;
import com.kk.taurus.playerbase.widget.BaseVideoView;

import java.lang.ref.WeakReference;
import java.util.List;
/**
 * 1.轮询小窗口播放，到时间就播放，20ms轮询一次。
 * 2.轮询大窗口播放的逻辑，具体逻辑如下：
 *     从第三个小窗口开始，如果小窗口有视频播放，
 *     则大窗口播放这个窗口的视频，如果其有关联视频，
 *     需要同时播放关联视频，如果关联视频被关闭，
 *     那么中间大屏幕现实没有被关闭的视频
 */
public class PlayersController extends Thread{
    static private final String TAG = "PlayersController";
    //开始播放时间
    private long startTime;
    //
    private long pauseTime;
    //整个播放的总时长
    private long totalDuration;
    //当前播放时间，这时间是当前播放时间相对于第一个视频起始播放的时间
    private long currentPlayTime;
    //当前正在中间播放的主播放器
    private PlayData curMainPlayer = null;
    private boolean running = false;
    /**
     * 已经按照播放时间排序好了
     */
    private WeakReference<List<PlayData>> listWeakReference;
    private List<BaseVideoView>videoViewList;
    OnPlayCtrlEventListener playCtrlEventListener;

    public PlayersController(List<PlayData> lst, List<BaseVideoView> videoViewList) {
        listWeakReference = new WeakReference<List<PlayData>>(lst);
        this.videoViewList = videoViewList;
    }

    public void setCtrlEventListener(OnPlayCtrlEventListener onPlayCtrlEventListener) {
        this.playCtrlEventListener = onPlayCtrlEventListener;
    }

    @Override
    public synchronized void start() {
        super.start();
        running = true;
    }

    public void stopController() {
        running = false;
    }

    @Override
    public void run() {
        long currentPlayTime;
        while (running) {
            if (curMainPlayer == null) {
                currentPlayTime = 0;
            } else {
                currentPlayTime = videoViewList.get(curMainPlayer.getCameraId()).getCurrentPosition();
            }
            //轮询小窗口播放
            for (PlayData playData : listWeakReference.get()) {
                if (playData.isPlaying()) {
                    if (playData.getEndTime() <= currentPlayTime) {
                        //还有个stopPlayback,后面看看这个有没有问题
                        //playData.getVideoView().stop();
//                        videoViewList.get(playData.getCameraId()).stop();
//                        playData.setPlaying(false);
//                        Log.d(TAG, "stop "+ playData.getCameraId());
                        playCtrlEventListener.onPlayCtrlCallback(OnPlayCtrlEventListener.STOP_CTRL, playData.getIndex(), playData.getCameraId(), OnPlayCtrlEventListener.CENTER_NONE);

                    }
                    continue;
                }

                //getCurrentPosition获取的是微妙
                if (playData.getStartTime() >  currentPlayTime) {
                    continue;
                } else if (playData.getStartTime() <= currentPlayTime && playData.getEndTime() > currentPlayTime) {
                    //需要播放这个视频
                    playData.setPlaying(true);
                    Log.d(TAG, "play "+playData.getCameraId());
                    playCtrlEventListener.onPlayCtrlCallback(OnPlayCtrlEventListener.PLAY_CTRL, playData.getIndex(), playData.getCameraId(), OnPlayCtrlEventListener.CENTER_NONE);
                }
            }
//测试代码
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
continue;
//结束测试的代码
            // 轮询中间窗口播放逻辑
//            if (curMainPlayer != null) {
//                continue;
//            }
//            //中间没有播放的，优先选择第三个进行播放
//            for (int index=2; index < 16; index++) {
//                if (videoViewList.get(index).isPlaying()) {
//                    playCtrlEventListener.onPlayCtrlCallback(OnPlayCtrlEventListener.PLAY_CTRL,
//                            videoViewList.get(index).getmIndexInPlayDataLst(),
//                            index, OnPlayCtrlEventListener.CENTER_FULL);
//                    curMainPlayer = listWeakReference.get().get(videoViewList.get(index).getmIndexInPlayDataLst());
//                    break;
//                }
//            }
//
//            //继续轮询
//            if (curMainPlayer == null) {
//                for (int index=0; index!=2; index++) {
//                    if (videoViewList.get(index).isPlaying()) {
//                        playCtrlEventListener.onPlayCtrlCallback(OnPlayCtrlEventListener.PLAY_CTRL,
//                                videoViewList.get(index).getmIndexInPlayDataLst(),
//                                index, OnPlayCtrlEventListener.CENTER_FULL);
//                        curMainPlayer = listWeakReference.get().get(videoViewList.get(index).getmIndexInPlayDataLst());
//                        break;
//                    }
//                }
//            }
//
//            try {
//                Thread.sleep(20);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
    }
}


//    public static void main(String[] args) {
//        List<Human> humans = Human.getAInitHumanList();
//        //lamdba 表达式 ->
//        Collections.sort(humans, (Human h1, Human h2) -> h1.getAge() - h2.getAge());
//        System.out.println(humans);
//    }