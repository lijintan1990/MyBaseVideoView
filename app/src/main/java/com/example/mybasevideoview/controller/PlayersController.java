package com.example.mybasevideoview.controller;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.VideoView;

import com.example.mybasevideoview.MainPlayerActivity;
import com.example.mybasevideoview.model.DataType;
import com.example.mybasevideoview.model.TimeLineInfo;
import com.example.mybasevideoview.utils.XslUtils;
import com.google.android.exoplayer2.Timeline;
import com.kk.taurus.playerbase.entity.DataSource;
import com.kk.taurus.playerbase.event.OnErrorEventListener;
import com.kk.taurus.playerbase.event.OnPlayerEventListener;
import com.kk.taurus.playerbase.player.IPlayer;
import com.kk.taurus.playerbase.render.AspectRatio;
import com.kk.taurus.playerbase.widget.BaseVideoView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Math.abs;

/**
 * 1.轮询小窗口播放，到时间就播放，20ms轮询一次。
 * 2.轮询大窗口播放的逻辑，具体逻辑如下：
 *     从第三个小窗口开始，如果小窗口有视频播放，
 *     则大窗口播放这个窗口的视频，如果其有关联视频，
 *     需要同时播放关联视频，如果关联视频被关闭，
 *     那么中间大屏幕现实没有被关闭的视频
 */
public class PlayersController extends Thread implements IPlayerCtrl{
    static private final String TAG = "AIVideo";
    //当前正在播放DataBean列表的位置，以Video类型为准
    private int playIdInDataBeanLst;
    //开始播放时间
    private int startTime;
    //
    private int pauseTime;
    //整个播放的总时长,单位毫秒
    private int totalDuration = 0;

    private boolean bSeeking = false;

    //当前播放时间，这时间是当前播放时间相对于第一个视频起始播放的时间,单位是毫秒
    private int currentPlayTime = 0;

    private boolean running = false;
    private WeakReference<List<BaseVideoView>> videoViewList;
    //当前正在播放的窗口
    private ArrayList<Integer> videoTimeLinePlaying;
    OnPlayCtrlEventListener playCtrlEventListener;
    OnBtnStateListener btnStateListener;
    OnMaskViewListener maskViewListener;

    //操作所有view的锁
    private Object lockObj;

    public PlayersController(List<BaseVideoView> videoViewList) {
        videoTimeLinePlaying = new ArrayList<>();
        this.videoViewList = new WeakReference<>(videoViewList);
        lockObj = new Object();
    }

    public void setCtrlEventListener(OnPlayCtrlEventListener onPlayCtrlEventListener) {
        this.playCtrlEventListener = onPlayCtrlEventListener;
    }

    public void setBtnStateListener(OnBtnStateListener listener) {
        btnStateListener = listener;
    }

    public void setMaskViewListener(OnMaskViewListener listener) {
        maskViewListener = listener;
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
        if (NetworkReq.getInstance().getTimelineInfo() == null)
            return 0;
        if (totalDuration != 0)
            return totalDuration;

        List<TimeLineInfo.DataBean> dataBeanList = NetworkReq.getInstance().getTimelineInfo().getData();
        for (int i = dataBeanList.size()-1; i >= 0; i--) {
            if (dataBeanList.get(i).getType() == DataType.XSL_VIDEO) {
                totalDuration = (dataBeanList.get(i).getStartTime() + dataBeanList.get(i).getDuration()) * 1000;
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

    public void startPlay_() {
        synchronized (lockObj) {
            if (running) return;
            running = true;
            start();
        }
    }

    @Override
    public void start_() {
        List<BaseVideoView> lst = videoViewList.get();
        if (lst == null)
            return;

        if (lst == null || MainPlayerActivity.smallVideoUrls == null)
            return;
        for (int i=0; i!=12; i++) {
            lst.get(i).setDataSource(new DataSource(MainPlayerActivity.smallVideoUrls.get(i)));
            lst.get(i).start();
        }

//        lst.get(12).setDataSource(new DataSource(MainPlayerActivity.middleVideoUrls.get(centerVideoViewIndex)));
//        lst.get(12).start();
    }

    @Override
    public void pause_() {
        synchronized (lockObj) {
            if (videoViewList.get() != null) {
                List<BaseVideoView> lst = videoViewList.get();
                for (int i=0; i!=12; i++) {
                    lst.get(i).pause();
                    //Log.d(TAG, "xx pause " + i);
                }

                lst.get(12).pause();
            }
        }
    }

    @Override
    public void resume_() {
        synchronized (lockObj) {
            if (videoViewList.get() != null) {
                List<BaseVideoView> lst = videoViewList.get();

                for (int i=0; i!=12; i++) {
                    lst.get(i).resume();
                    //Log.d(TAG, "xx onResume " + i);
                }

                lst.get(12).resume();
            }
        }
    }

    @Override
    public void seekTo_(int msc) {
        synchronized (lockObj) {
            if (videoViewList.get() != null) {
                List<BaseVideoView> lst = videoViewList.get();
                Log.d(TAG, "mySeek to:" + msc);

                for (int i=0; i!=12; i++) {
                    Log.d(TAG, "player status:"+lst.get(i).getState());
                    lst.get(i).seekTo(msc);
                }
            }

            bSeeking = false;
        }
    }

    public void seekChapter(int msc) {
        synchronized (lockObj) {
            bSeeking = true;
            Log.d(TAG, "seekNotify after lock. bSeeking = true");
            List<BaseVideoView> lst = videoViewList.get();
            if (lst == null)
                return;

            //说明中间视频不用切换，也就不用去stop
            if (msc >= centerStartTime && msc <= centerStartTime+centerDuration) {
                lst.get(12).seekTo(msc);
                currentPlayTime = msc;
                Log.d(TAG, "seekNotify currentPlayTime: "+currentPlayTime);
            } else {
                Log.d(TAG, "stop window index:" + centerVideoViewIndex);
                //结束中间视频播放
                lst.get(12).stop();
                playCtrlEventListener.onPlayCtrlCallback(OnPlayCtrlEventListener.PLAY_CHAPTER_CHANGE, msc, -1);
                //查找合适的视频进行播放
                //重新设置
                centerVideoViewIndex = -1;
                seekTo_(msc);
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        //要执行的操作
                        pause_();
                    }
                };
                Timer timer = new Timer();
                timer.schedule(task, 1500);//2秒后执行TimeTask的run方法
                //老的播放已经结束
                bSeeking = false;
                Log.d(TAG,  "set bSeeking false");
            }
        }
    }
    /**
     * 几种情况下会被调用：
     * 1.拖动进度条
     * 2.弹层返回之后会调用，章节弹层和关联视频弹层
     * @param msc
     */
    @Override
    public void seekNotify(int msc) {
        Log.d(TAG, "seekNotify lock");
        synchronized (lockObj) {
            bSeeking = true;
            Log.d(TAG, "seekNotify after lock. bSeeking = true");
            List<BaseVideoView> lst = videoViewList.get();
            if (lst == null)
                return;

            //说明中间视频不用切换，也就不用去stop
            if (msc >= centerStartTime && msc <= centerStartTime+centerDuration) {
                lst.get(12).seekTo(msc);
                currentPlayTime = msc;
                Log.d(TAG, "seekNotify currentPlayTime: "+currentPlayTime);
            } else {
//                Log.d(TAG, "stop window index:" + centerVideoViewIndex);
//                //结束中间视频播放
//                lst.get(12).stop();
//                int state = lst.get(12).getState();
//                //查找合适的视频进行播放
//                //重新设置
//                centerVideoViewIndex = -1;
//                //老的播放已经结束
//                bSeeking = false;
//                Log.d(TAG,  "set bSeeking false");
            }
        }
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

    /**
     * 获取当前播放的timeLine的开始时间
     * @return
     */
    public int getCurTimeLineStartTime() {
        return centerStartTime;
    }

    public int getCenterVideoViewIndex() {
        return centerVideoViewIndex;
    }
    /**
     * 获取当前播放的timeLine的结束时间
     * @return
     */
    public int getCurTimeLineEndTime() {
        return centerDuration;
    }
    public void updateCenterPlayerInfo(int centerPlayId, int playTime) {
        synchronized (lockObj) {
            this.centerVideoViewIndex = centerPlayId;
            TimeLineInfo timeLineInfo = NetworkReq.getInstance().getTimelineInfo();

            for (TimeLineInfo.DataBean dataBean : timeLineInfo.getData()) {
                //查找这个时间段能够播放的视频timeline信息
                if (dataBean.getStartTime() * 1000 <= playTime
                        && (dataBean.getStartTime() + dataBean.getDuration()) * 1000 > playTime
                        && (dataBean.getType() == DataType.XSL_VIDEO
                        && dataBean.getObjId() - 1 == centerPlayId)) {
                    centerStartTime = dataBean.getStartTime() * 1000;
                    centerDuration = dataBean.getDuration() * 1000;
                    Log.d(TAG, "update centerStarTime:"+ centerStartTime + " centerDuration: " + centerDuration);
                }
            }
        }
    }
    /**
     {
     "id": 56,
     "objId": 2,
     "type": 1,
     "startTime": 734,
     "duration": 245,
     "scale": 2, 大格子播放
     "relevanceType": null,
     "relevanceVideoId": null,
     "video": null,
     "chapter": null,
     "text": null
     },
     {
     "id": 84,
     "objId": 11,
     "type": 1,
     "startTime": 743,
     "duration": 23,
     "scale": 1,            //小格子播放
     "relevanceType": 2,    关联关系
     "relevanceVideoId": 5,
     "video": null,
     "chapter": null,
     "text": null
     },
     {
     "id": 82,
     "objId": 5,
     "type": 1,
     "startTime": 743,
     "duration": 23,
     "scale": 1,
     "relevanceType": 2,
     "relevanceVideoId": 11,
     "video": null,
     "chapter": null,
     "text": null
     },
     */
    void videoProc(TimeLineInfo.DataBean dataBean) {
        List<BaseVideoView> lst = videoViewList.get();
        if (lst == null)
            return;

        if (centerVideoViewIndex >= 0 &&
                currentPlayTime > centerStartTime + centerDuration &&
                videoViewList.get().get(12).getState() == IPlayer.STATE_STARTED) {

//            playCtrlEventListener.onPlayCtrlCallback(OnPlayCtrlEventListener.STOP_CTRL, 0, centerVideoViewIndex);
            // 当前timeline结束，需要关闭中间播放，然后暂停所有小窗口播放，并且去掉选中框
            playCtrlEventListener.onPlayCtrlCallback(OnPlayCtrlEventListener.PLAY_TIMELINE_CHANGE, 0, centerVideoViewIndex);
            //centerVideoViewIndex = -1;
        }
    }

    private void applienceProc(TimeLineInfo.DataBean dataBean, boolean enable) {
        if (btnStateListener == null)
            return;

        if (dataBean == null || !enable) {
            btnStateListener.onApplience(OnBtnStateListener.XSL_APPLIANCE_BTN_STATE, false, null, null);
            return;
        }

        if (dataBean.getVideo().getVideoUrl360() != null && !dataBean.getVideo().getVideoUrl360().isEmpty())
            btnStateListener.onApplience(OnBtnStateListener.XSL_APPLIANCE_BTN_STATE , enable, dataBean.getVideo().getVideoUrl360(), dataBean.getVideo().getName());
        else if (dataBean.getVideo().getVideoUrl720() != null && !dataBean.getVideo().getVideoUrl720().isEmpty()) {
            btnStateListener.onApplience(OnBtnStateListener.XSL_APPLIANCE_BTN_STATE, enable, dataBean.getVideo().getVideoUrl720(), dataBean.getVideo().getName());
        } else if (dataBean.getVideo().getVideoUrl1080() != null && !dataBean.getVideo().getVideoUrl1080().isEmpty()) {
            btnStateListener.onApplience(OnBtnStateListener.XSL_APPLIANCE_BTN_STATE, enable, dataBean.getVideo().getVideoUrl1080(), dataBean.getVideo().getName());
        }
    }

    private void chapterProc(TimeLineInfo.DataBean dataBean) {
        if (btnStateListener == null || dataBean == null)
            return;

        btnStateListener.onChapterBtnTextUpdate(dataBean.getStartTime(),
                dataBean.getChapter().getCode(),
                dataBean.getChapter().getName());
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
            if (dataBean.getObjId() >= 0)
                btnStateListener.onWordStateChange(OnBtnStateListener.XSL_WORD_BTN_STATE,
                                                enable, dataBean.getObjId());
        }
    }

    //上一次通知更新字幕时间，每次间隔1s更新一次
    private int lastSubtitleUpdateTime = 0;
    //字幕通知
    private void subTitleNotify() {
        if (abs(currentPlayTime - lastSubtitleUpdateTime) > 300 &&
                videoViewList.get().get(12).getState() == IPlayer.STATE_STARTED &&
                videoViewList.get().get(12).getState() != IPlayer.STATE_PAUSED) {
            //Log.d(TAG, "center player status:" + videoViewList.get().get(12).getState());
            playCtrlEventListener.onSubtitleUpdate(currentPlayTime);
            lastSubtitleUpdateTime = currentPlayTime;
        }
    }

    //当前中间播放的是第几个小窗口的视频
    int centerVideoViewIndex = -1;

    //中间视频起始播放时间在时间线上的时间，单位毫秒
    int centerStartTime = 0;
    int centerDuration = 0;

    int timesCount = 0;
    /**
     * 开始的时候小窗口全部播放并暂停，大窗口只显示章节标题
     * timeline到期之后全部暂停，点击小窗口才会继续播放
     * 章节播放也会暂停，点击小窗口之后才会播放
     */
    private void itemProc() {
        int type;
        boolean enableApplience = false;
        boolean enableAction = false;
        boolean enableWord = false;
        boolean enableChapter = false;
        TimeLineInfo timeLineInfo = NetworkReq.getInstance().getTimelineInfo();
        //每次循环一次清除之前的数据
        videoTimeLinePlaying.clear();

        for (TimeLineInfo.DataBean dataBean : timeLineInfo.getData()) {
            if (dataBean.getStartTime() * 1000 <= currentPlayTime
                    && (dataBean.getStartTime() + dataBean.getDuration()) * 1000 > currentPlayTime) {
                type = dataBean.getType();

                if (type == DataType.XSL_VIDEO) {
                    //关联视频以及中间视频播放处理
                    if (!bSeeking) {
                        videoProc(dataBean);
                        //Log.d(TAG, "currentPlayTime:" + currentPlayTime + " startTime:" + dataBean.getStartTime() + " duration:" + dataBean.getDuration() + " id:" + dataBean.getObjId());
                        videoTimeLinePlaying.add(dataBean.getObjId() - 1);
                    }
                } else if (type == DataType.XSL_CHAPTER) {
                    chapterProc(dataBean);
                } else if (type == DataType.XSL_APPLIANCES) {
                    applienceProc(dataBean, true);
                    enableApplience = true;
                } else if (type == DataType.XSL_WORD) {
                    enableWord = true;
                    wordProc(dataBean, true);
                } else if (type == DataType.XSL_ACTION) {
                    enableAction = true;
                }
            }

            //当前时间还没轮询到开始，直接跳出循环
            if (dataBean.getStartTime() * 1000 > currentPlayTime)
                break;
        }


            if (!bSeeking) {
                maskViewListener.setMaskViewStatus(OnMaskViewListener.ACTION_PLAY_MASK, videoTimeLinePlaying);
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

        //字幕更新
        subTitleNotify();
    }

    @Override
    public void run() {
        Log.d(TAG, "PlayersController start");
        int syncVideoTime = 0;
        //记录上一次的seek的时间，用于计算避免重复seek
        long lastSeekTime = 0;
        List<BaseVideoView> lst = videoViewList.get();

        while (running) {
            TimeLineInfo timeLineInfo = NetworkReq.getInstance().getTimelineInfo();
            if (timeLineInfo == null) {
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }

            // bSeeking 和currentPlayTime必须加锁
            synchronized (lockObj) {
                if (bSeeking) {
                    Log.d(TAG, "seeking now, not cycle time");
                    continue;
                } else {
                    //中间窗口有视频播放，那么开始计时
                    if (centerVideoViewIndex != -1 && lst.get(12).getState() == IPlayer.STATE_STARTED) {
                        currentPlayTime = lst.get(12).getCurrentPosition();
                        //Log.d(TAG, "小视频 play currentPlayTime: " + currentPlayTime + "use view index playTime:"+ lst.get(centerVideoViewIndex).getCurrentPosition());
                        //通知更新进度条
                        playCtrlEventListener.onPlayTimeCallback(OnPlayCtrlEventListener.PLAY_TIME_SET_CTRL, totalDuration, currentPlayTime);
                        int subTime = lst.get(centerVideoViewIndex).getCurrentPosition() - lst.get(12).getCurrentPosition();
                        //简单的同步，快速seek多次，这里存在bug
                        if (subTime > 1000) {
                            for (int i = 0; i != 12; i++) {
                                lst.get(i).pause();
                                Log.d(TAG, "小视频快了，暂停");
                            }
                        } else if (subTime < -300) {
                            //seekTo_(currentPlayTime);
                            long currentTime = System.currentTimeMillis();
                            if (currentTime - 1000 > lastSeekTime) {
                                Log.d(TAG, "小视频慢了，seek " + (currentPlayTime + 500));
                                List<BaseVideoView> videoViews = videoViewList.get();
                                videoViews.get(centerVideoViewIndex).seekTo(currentPlayTime + 500);
                                videoViews.get(centerVideoViewIndex).resume();
                            }
                            lastSeekTime = System.currentTimeMillis();
                        } else {
                            //Log.d(TAG, "小视频恢复播放");
                            for (int i = 0; i != 12; i++) {
                                lst.get(i).resume();
                            }
                        }
                    } else {
                        currentPlayTime = lst.get(0).getCurrentPosition();
                        //Log.d(TAG, "小视频 play currentPlayTime: " + currentPlayTime + "use view index playTime:"+ lst.get(centerVideoViewIndex).getCurrentPosition());
                        //通知更新进度条
                        playCtrlEventListener.onPlayTimeCallback(OnPlayCtrlEventListener.PLAY_TIME_SET_CTRL, totalDuration, currentPlayTime);
                    }
                }

                itemProc();
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
