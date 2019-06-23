package com.example.mybasevideoview.controller;

import android.graphics.Color;
import android.util.Log;
import android.widget.VideoView;

import com.example.mybasevideoview.MainPlayerActivity;
import com.example.mybasevideoview.model.DataType;
import com.example.mybasevideoview.model.TimeLineInfo;
import com.kk.taurus.playerbase.entity.DataSource;
import com.kk.taurus.playerbase.player.IPlayer;
import com.kk.taurus.playerbase.render.AspectRatio;
import com.kk.taurus.playerbase.widget.BaseVideoView;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.ListIterator;

/**
 * 1.轮询小窗口播放，到时间就播放，20ms轮询一次。
 * 2.轮询大窗口播放的逻辑，具体逻辑如下：
 *     从第三个小窗口开始，如果小窗口有视频播放，
 *     则大窗口播放这个窗口的视频，如果其有关联视频，
 *     需要同时播放关联视频，如果关联视频被关闭，
 *     那么中间大屏幕现实没有被关闭的视频
 */
public class PlayersController extends Thread implements IPlayerCtrl{
    static private final String TAG = "PlayersController";
    //当前正在播放DataBean列表的位置，以Video类型为准
    private int playIdInDataBeanLst;
    //开始播放时间
    private int startTime;
    //
    private int pauseTime;
    //整个播放的总时长
    private int totalDuration = 0;
    //中间窗口播放的是第几个窗口的视频
    int centerPlayIndex = -1;
    //当前播放时间，这时间是当前播放时间相对于第一个视频起始播放的时间
    private int currentPlayTime = 0;
    private boolean running = false;
    private WeakReference<List<BaseVideoView>> videoViewList;
    OnPlayCtrlEventListener playCtrlEventListener;

    //操作所有view的锁
    Object lockObj;

    public PlayersController(List<BaseVideoView> videoViewList) {
        this.videoViewList = new WeakReference<>(videoViewList) ;
        lockObj = new Object();
    }

    public void setCtrlEventListener(OnPlayCtrlEventListener onPlayCtrlEventListener) {
        this.playCtrlEventListener = onPlayCtrlEventListener;
    }

    @Override
    public void setDataSource(DataSource dataSource) {

    }

    @Override
    public void setRenderType(int renderType) {

    }

    @Override
    public void setAspectRatio(AspectRatio aspectRatio) {

    }

    @Override
    public boolean switchDecoder(int decoderPlanId) {
        return false;
    }

    @Override
    public void setVolume(float left, float right) {

    }

    @Override
    public void setSpeed(float speed) {

    }

    @Override
    public boolean isInPlaybackState() {
        return false;
    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    public int getCurrentPosition() {
        return currentPlayTime;
    }

    @Override
    public int getDuration() {
        if (MainPlayerActivity.getTimelineInfo() == null)
            return 0;
        if (totalDuration != 0)
            return totalDuration;

        List<TimeLineInfo.DataBean> dataBeanList = MainPlayerActivity.getTimelineInfo().getData();
        for (int i = dataBeanList.size()-1; i >= 0; i--) {
            if (dataBeanList.get(i).getType() == DataType.XSL_VIDEO
                && dataBeanList.get(i).getVideo() != null
                && dataBeanList.get(i).getVideo().getIndex() != -1) {
                totalDuration = dataBeanList.get(i).getStartTime() + dataBeanList.get(i).getVideo().getDuration();
            }
        }
        return totalDuration;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public int getPlayerState() {
        return 0;
    }

    @Override
    public void startPlay_() {
        synchronized (lockObj) {
            if (running) return;
            running = true;
            start();
        }
    }

    @Override
    public void pause_() {
        synchronized (lockObj) {
            if (videoViewList.get() != null) {
                List<BaseVideoView> lst = videoViewList.get();
                for (BaseVideoView videoView : lst) {
                    videoView.pause();
                }
            }
        }
    }

    @Override
    public void resume_() {
        synchronized (lockObj) {
            if (videoViewList.get() != null) {
                List<BaseVideoView> lst = videoViewList.get();

                for (BaseVideoView videoView : lst) {
                    videoView.resume();
                }
            }
        }
    }

    @Override
    public void seekTo_(int msc) {

    }

    @Override
    public void stop_() {
        synchronized (lockObj) {
            running = false;
            List<BaseVideoView> lst = videoViewList.get();
            for (BaseVideoView videoView : lst) {
                videoView.stop();
            }
        }

        try {
            join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopPlayback_() {

    }

//    /**
//     * 判断videoView是不是已经在播放了
//     * @return
//     */
//    private boolean isVideoViewRunning(VideoView videoView) {
//        if (videoViewList.get(videoBean.getIndex()).getState() >= IPlayer.STATE_INITIALIZED) {
//
//        }
//    }

    /**
     * 只需要通过判断上面还右面的视频即可
     * @param id
     * @return
     */
    int getRelativeVideoView(int id) {
        switch (id) {
            case 0:
                return 8;
            case 1:
                return 7;
            case 2:
                return 6;
            case 3:
                return 11;
            case 4:
                return 10;
            case 5:
                return 9;
        }
        return -1;
    }

    /**
     * 获取关联视频的DataBean数据
     * @param currentPlayTime
     * @param relateId
     * @return
     */
    TimeLineInfo.DataBean getRelativeDataBean(int currentPlayTime, int relateId) {
        TimeLineInfo timeLineInfo = MainPlayerActivity.getTimelineInfo();
        TimeLineInfo.DataBean retData = null;
        for (TimeLineInfo.DataBean dataBean : timeLineInfo.getData()) {
            if (dataBean.getStartTime() > currentPlayTime)
                break;
            if (dataBean.getVideo() != null
            && dataBean.getVideo().getIndex() == relateId
            && dataBean.getStartTime()<= currentPlayTime
            && dataBean.getStartTime() + dataBean.getVideo().getDuration() > currentPlayTime) {
                retData = dataBean;
            }
        }
        return retData;
    }

    @Override
    public void run() {
        //中间视频起始播放时间在时间线上的时间
        int centerStartTime = 0;

        while (running) {
            TimeLineInfo timeLineInfo = MainPlayerActivity.getTimelineInfo();
            if (timeLineInfo == null) {
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }

            List<BaseVideoView> lst = videoViewList.get();

            //中间窗口有视频播放，那么开始计时
            if (centerPlayIndex != -1) {

                currentPlayTime = centerStartTime + lst.get(12).getCurrentPosition() / 1000;
                //通知更新进度条
                playCtrlEventListener.onPlayTimeCallback(OnPlayCtrlEventListener.PLAY_TIME_SET_CTRL, totalDuration, currentPlayTime);
            }

            int type = -1;
            for (TimeLineInfo.DataBean dataBean : timeLineInfo.getData()) {
                type = dataBean.getType();
                if (type == DataType.XSL_VIDEO) {
                    //1.轮询小窗口播放
                    TimeLineInfo.DataBean.VideoBean videoBean = dataBean.getVideo();
                    int index = videoBean.getIndex();
                    if (index == -1) {
                        //非法值，不做任何处理
                        continue;
                    }

                    //判断是否需要播放
                    if (dataBean.getStartTime() <= currentPlayTime
                        && (dataBean.getStartTime() + dataBean.getVideo().getDuration()) > currentPlayTime
                        && (lst.get(index).getState() < IPlayer.STATE_INITIALIZED
                            || lst.get(index).getState() >= IPlayer.STATE_STOPPED)) {
                        //播放该窗口视频
                            playCtrlEventListener.onPlayCtrlCallback(OnPlayCtrlEventListener.PLAY_CTRL,
                                    dataBean, index, OnPlayCtrlEventListener.CENTER_NONE);

                            //是否有关联视频
                        int relateType = dataBean.getVideo().getRelevanceType();
                        if (relateType == OnPlayCtrlEventListener.RELATIVE_VERTICAL) {
                            TimeLineInfo.DataBean.VideoBean.RelevanceVideoBean relevanceVideoBean = dataBean.getVideo().getRelevanceVideo();
                            playCtrlEventListener.onPlayRelateVideos(OnPlayCtrlEventListener.PLAY_RELATE_VERTICAL_CTRL,
                                    index, relevanceVideoBean.getIndex(), dataBean.getVideo().getVideoUrl(), relevanceVideoBean.getVideoUrl());
                        } else if (relateType == OnPlayCtrlEventListener.RELATIVE_HORIZON) {
                            TimeLineInfo.DataBean.VideoBean.RelevanceVideoBean relevanceVideoBean = dataBean.getVideo().getRelevanceVideo();
                            playCtrlEventListener.onPlayRelateVideos(OnPlayCtrlEventListener.PLAY_RELATE_HORIZON_CTRL,
                                    index, relevanceVideoBean.getIndex(), dataBean.getVideo().getVideoUrl(), relevanceVideoBean.getVideoUrl());
                        }

                        //播放中间窗口
                        if (centerPlayIndex == -1) {
                            //如果正在播放，那先结束这个播放，在子线程里面结束，不知道会不会有问题，暂时不加到回调里面去
                            if (lst.get(12).isPlaying()) {
                                lst.get(12).stop();
                            }
                            playCtrlEventListener.onPlayCtrlCallback(OnPlayCtrlEventListener.PLAY_CTRL,
                                    dataBean, 12, OnPlayCtrlEventListener.CENTER_FULL);
                            //这里是有隐藏的bug，如果中间的因为某种原因导致播放失败，那需要继续播放，
                            centerPlayIndex = index;
                            lst.get(index).setBoardColor(Color.RED, true);
                            centerStartTime = dataBean.getStartTime();
                        }
                    }
                } else if (type == DataType.XSL_CHAPTER) {

                } else if (type == DataType.XSL_APPLIANCES) {

                } else if (type == DataType.XSL_TEXT) {

                } else if (type == DataType.XSL_ACTION) {

                }
            }

            //线程睡眠，避免cpu过高
            if (running) {
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.e(TAG, "controller thread exit");
    }
}
