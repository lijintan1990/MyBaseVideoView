package com.example.mybasevideoview.controller;

import android.graphics.Color;
import android.util.Log;
import android.widget.VideoView;

import com.example.mybasevideoview.MainPlayerActivity;
import com.example.mybasevideoview.model.DataType;
import com.example.mybasevideoview.model.TimeLineInfo;
import com.example.mybasevideoview.utils.XslUtils;
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
    //整个播放的总时长,单位秒
    private int totalDuration = 0;

    //当前播放时间，这时间是当前播放时间相对于第一个视频起始播放的时间
    private int currentPlayTime = 0;
    private boolean running = false;
    private WeakReference<List<BaseVideoView>> videoViewList;
    OnPlayCtrlEventListener playCtrlEventListener;
    OnBtnStateListener btnStateListener;

    //操作所有view的锁
    Object lockObj;

    public PlayersController(List<BaseVideoView> videoViewList) {
        this.videoViewList = new WeakReference<>(videoViewList) ;
        lockObj = new Object();
    }

    public void setCtrlEventListener(OnPlayCtrlEventListener onPlayCtrlEventListener) {
        this.playCtrlEventListener = onPlayCtrlEventListener;
    }

    public void setBtnStateListener(OnBtnStateListener listener) {
        btnStateListener = listener;
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
            if (dataBeanList.get(i).getType() == DataType.XSL_VIDEO) {
                totalDuration = dataBeanList.get(i).getStartTime() + dataBeanList.get(i).getDuration();
                break;
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
            if (lst == null)
                return;
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
            case 6:
                return 2;
            case 7:
                return 1;
            case 8:
                return 0;
            case 9:
                return 5;
            case 10:
                return 4;
            case 11:
                return 3;
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

    void videoProc(TimeLineInfo.DataBean dataBean) {
        List<BaseVideoView> lst = videoViewList.get();
        if (lst == null)
            return;
        int windowIndex = dataBean.getObjId() - 1;
        //中间播放窗口和当前轮询到的id不一致，那么通知界面播放这个id
        if (centerVideoViewIndex != windowIndex) {
            playCtrlEventListener.onPlayCtrlCallback(OnPlayCtrlEventListener.PLAY_CTRL, OnPlayCtrlEventListener.CENTER_FULL, windowIndex);
            //这里是有隐藏的bug，如果中间的因为某种原因导致播放失败，那需要继续播放，
            centerVideoViewIndex = windowIndex;
            lst.get(windowIndex).setBoardColor(Color.RED, true);
            centerStartTime = dataBean.getStartTime();
        }

        int relateId = -1;
        int relateType = dataBean.getRelevanceType();

        //判断是否有关联视频
        if (relateType != 0) {
            relateId = getRelativeVideoView(windowIndex);
            if (relateType == OnPlayCtrlEventListener.RELATIVE_VERTICAL) {
                playCtrlEventListener.onPlayRelateVideos(OnPlayCtrlEventListener.PLAY_RELATE_VERTICAL_CTRL, windowIndex, relateId);
            } else if (relateType == OnPlayCtrlEventListener.RELATIVE_HORIZON) {
                playCtrlEventListener.onPlayRelateVideos(OnPlayCtrlEventListener.PLAY_RELATE_HORIZON_CTRL, windowIndex, relateId);
            }
        }
    }

    private void chapterProc(TimeLineInfo.DataBean dataBean, boolean enable) {

    }

    private void applienceProc(TimeLineInfo.DataBean dataBean, boolean enable) {
        if (btnStateListener == null)
            return;

        if (dataBean == null || !enable) {
            btnStateListener.onStateChange(OnBtnStateListener.XSL_APPLIANCE_BTN_STATE, false, null);
            return;
        }

        if (dataBean.getVideo().getVideoUrl360() != null && !dataBean.getVideo().getVideoUrl360().isEmpty())
            btnStateListener.onStateChange(OnBtnStateListener.XSL_APPLIANCE_BTN_STATE , enable, dataBean.getVideo().getVideoUrl360());
        else if (dataBean.getVideo().getVideoUrl720() != null && !dataBean.getVideo().getVideoUrl720().isEmpty()) {
            btnStateListener.onStateChange(OnBtnStateListener.XSL_APPLIANCE_BTN_STATE, enable, dataBean.getVideo().getVideoUrl720());
        } else if (dataBean.getVideo().getVideoUrl1080() != null && !dataBean.getVideo().getVideoUrl1080().isEmpty()) {
            btnStateListener.onStateChange(OnBtnStateListener.XSL_APPLIANCE_BTN_STATE, enable, dataBean.getVideo().getVideoUrl1080());
        }
    }

    private void actionProc(TimeLineInfo.DataBean dataBean, boolean enable) {
        if (btnStateListener != null) {
            //关闭动作按钮
            if (dataBean == null || !enable) {
                btnStateListener.onStateChange(OnBtnStateListener.XSL_ACTION_BTN_STATE, enable,null);
                return;
            }
            //打开动作按钮
            if (dataBean.getVideo().getVideoUrl360() != null && !dataBean.getVideo().getVideoUrl360().isEmpty())
                btnStateListener.onStateChange(OnBtnStateListener.XSL_ACTION_BTN_STATE, enable,
                    dataBean.getVideo().getVideoUrl360());
            else if (dataBean.getVideo().getVideoUrl720() != null && !dataBean.getVideo().getVideoUrl720().isEmpty()) {
                btnStateListener.onStateChange(OnBtnStateListener.XSL_ACTION_BTN_STATE, enable,
                        dataBean.getVideo().getVideoUrl720());
            } else if (dataBean.getVideo().getVideoUrl1080() != null && !dataBean.getVideo().getVideoUrl1080().isEmpty()) {
                btnStateListener.onStateChange(OnBtnStateListener.XSL_ACTION_BTN_STATE, enable,
                        dataBean.getVideo().getVideoUrl1080());
            }
        }
    }

    private void wordProc(TimeLineInfo.DataBean dataBean, boolean enable) {
        if (btnStateListener != null) {
            //关闭文本按钮
            if (dataBean == null || !enable) {
                btnStateListener.onStateChange(OnBtnStateListener.XSL_WORD_BTN_STATE, enable,null);
                return;
            }
            //打开文本按钮
            if (dataBean.getText() != null &&
                    dataBean.getText().getImgUrl() != null &&
                    !dataBean.getText().getImgUrl().isEmpty())
                btnStateListener.onStateChange(OnBtnStateListener.XSL_WORD_BTN_STATE,
                                                enable, dataBean.getText().getImgUrl());
        }
    }

    //当前中间播放的是第几个小窗口的视频
    int centerVideoViewIndex = -1;

    //中间视频起始播放时间在时间线上的时间
    int centerStartTime = 0;

    @Override
    public void run() {
        playCtrlEventListener.onPlayCtrlCallback(OnPlayCtrlEventListener.PLAY_ALL_CTRL, 0, 0);
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<BaseVideoView> lst = videoViewList.get();

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

            //中间窗口有视频播放，那么开始计时
            if (centerVideoViewIndex != -1) {
                currentPlayTime = centerStartTime + lst.get(12).getCurrentPosition() / 1000;
                //通知更新进度条
                playCtrlEventListener.onPlayTimeCallback(OnPlayCtrlEventListener.PLAY_TIME_SET_CTRL, totalDuration, currentPlayTime);
            }

            int type;
            boolean enableApplience = false;
            boolean enableAction = false;
            boolean enableWord = false;
            boolean enableChapter = false;
            for (TimeLineInfo.DataBean dataBean : timeLineInfo.getData()) {
                if (dataBean.getStartTime() <= currentPlayTime
                        && dataBean.getStartTime() + dataBean.getDuration() > currentPlayTime) {
                    type = dataBean.getType();
                    //这个循环里面有任何一个需要激活的按钮我们都把他激活，
                    //就算激活多次也无所谓，如果轮询完成，有的按钮没有被激活
                    //那么enableXXX变量肯定是false,在for循环之后关闭即可
                    if (type == DataType.XSL_VIDEO) {
                        //关联视频以及中间视频播放处理
                        videoProc(dataBean);
                    } else if (type == DataType.XSL_CHAPTER) {
                        chapterProc(dataBean, true);
                        enableChapter = true;
                    } else if (type == DataType.XSL_APPLIANCES) {
                        applienceProc(dataBean, true);
                        enableApplience = true;
                    } else if (type == DataType.XSL_WORD) {
                        enableWord = true;
                        wordProc(dataBean, true);
                    } else if (type == DataType.XSL_ACTION) {
                        enableAction = true;
                        chapterProc(dataBean, true);
                    }
                }
            }

            if (!enableAction) {
                actionProc(null, false);
            }

            if (!enableApplience) {
                applienceProc(null, false);
            }

            if (!enableWord) {
                wordProc(null, false);
            }

            if (!enableChapter) {
                chapterProc(null, false);
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

        Log.w(TAG,"exit controller thread");
    }
}
