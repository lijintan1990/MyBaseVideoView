package com.example.mybasevideoview.controller;

import android.graphics.Color;
import android.util.Log;
import android.widget.VideoView;

import com.example.mybasevideoview.MainPlayerActivity;
import com.example.mybasevideoview.model.DataType;
import com.example.mybasevideoview.model.TimeLineInfo;
import com.example.mybasevideoview.utils.XslUtils;
import com.google.android.exoplayer2.Timeline;
import com.kk.taurus.playerbase.entity.DataSource;
import com.kk.taurus.playerbase.player.IPlayer;
import com.kk.taurus.playerbase.render.AspectRatio;
import com.kk.taurus.playerbase.widget.BaseVideoView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

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
    OnPlayCtrlEventListener playCtrlEventListener;
    OnBtnStateListener btnStateListener;
    OnMaskViewListener maskViewListener;

    //操作所有view的锁
    private Object lockObj;
    //给关联视频的锁，有关联视频的时候主面板需要暂停播放
    private Object relateLockObj;

    //标记已经播放过的关联视频
    ArrayList<TimeLineInfo.DataBean> relatePlayedVideoLst;

    public PlayersController(List<BaseVideoView> videoViewList) {
        this.videoViewList = new WeakReference<>(videoViewList) ;
        relatePlayedVideoLst = new ArrayList<>();
        lockObj = new Object();
        relateLockObj = new Object();
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
        if (MainPlayerActivity.getTimelineInfo() == null)
            return 0;
        if (totalDuration != 0)
            return totalDuration;

        List<TimeLineInfo.DataBean> dataBeanList = MainPlayerActivity.getTimelineInfo().getData();
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
    }

    @Override
    public void pause_() {
        synchronized (lockObj) {
            if (videoViewList.get() != null) {
                List<BaseVideoView> lst = videoViewList.get();
                for (int i=0; i!=12; i++) {
                    lst.get(i).pause();
                }
            }
        }
    }

    public void pauseNoLock() {
        if (videoViewList.get() != null) {
            List<BaseVideoView> lst = videoViewList.get();
            for (int i=0; i!=12; i++) {
                lst.get(i).pause();
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
                }
            }
        }
    }

    @Override
    public void seekTo_(int msc) {
        synchronized (lockObj) {
            msc = msc + 3000;
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

    /**
     * 几种情况下会被调用：
     * 1.拖动进度条
     * 2.弹层返回之后会调用，章节弹层和关联视频弹层
     * @param msc
     */
    @Override
    public void seekNotify(int msc) {
        synchronized (lockObj) {
            bSeeking = true;
            List<BaseVideoView> lst = videoViewList.get();
            if (lst == null)
                return;

            //说明中间视频不用切换，也就不用去stop
            if (msc >= centerStartTime && msc <= centerStartTime+centerDuration) {
                lst.get(12).seekTo(msc);
            } else {
                Log.d(TAG, "stop window index:" + centerVideoViewIndex);
                //结束中间视频播放
                lst.get(12).stop();
                //查找合适的视频进行播放
                //重新设置
                centerVideoViewIndex = -1;
                //老的播放已经结束
                bSeeking = false;
            }

            currentPlayTime = msc;
            Log.d(TAG, "seekNotify currentPlayTime: "+currentPlayTime);
        }
    }

    public void seekFinish() {
        synchronized (lockObj) {
            bSeeking = false;
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
     * 一旦有关联视频，我们需要暂停controller线程
     * 等关联视频关闭在回复controller线程
     */
    public void relateVideoWait() {
        synchronized (relateLockObj) {
            try {
                Log.d(TAG, "before relate wait");
                relateLockObj.wait();
                Log.d(TAG, "after relate wait");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关联视频窗口关闭，需要通知controller线程继续运行
     */
    public void relateViewNotify() {
        synchronized (relateLockObj) {
            Log.d(TAG, "relate notify");
            relateLockObj.notify();
            Log.d(TAG, "after notify");
        }
    }

    private boolean isRelateDataInLst(TimeLineInfo.DataBean dataBean) {
        //TODO: 感觉这个函数可以不用加锁
        Log.d(TAG, "before isRelate");
        synchronized (relateLockObj) {
            for (TimeLineInfo.DataBean data : relatePlayedVideoLst) {
                if (data == dataBean || dataBean.getRelevanceVideoId() == data.getObjId()) {
                    Log.d(TAG, "relate data is in list");
                    return true;
                }
            }
        }
        Log.d(TAG, "after isRelate");
        return false;
    }

    /**
     * 把已经做过关联的视频从链表里面移除，在一下几种情况下移除
     * 1. 当前播放时间已经大于关联视频的结尾时间加3s
     * 2. 用户点击手动关联的时候，需要移除，然后等待controller的再次播放管理视频
     */
    public void removeRelateDataInLst() {
        Log.d(TAG, "before remove relate data form list");
        synchronized (relateLockObj) {
            Log.d(TAG, "取消禁闭，可以继续关联");
            relatePlayedVideoLst.clear();
        }

        Log.d(TAG, "after remove relate data form list");
    }

    /**
     * 加入到已经播放的关联视频列表当中去，避免关联视频activity返回后，
     * 由于一些时间误差导致再次启动关联视频。
     * @param dataBean
     */
    public void addRelateDataToLst(TimeLineInfo.DataBean dataBean) {
        Log.d(TAG, "add relate data to list");
        synchronized (relateLockObj) {
            relatePlayedVideoLst.add(dataBean);
        }
        Log.d(TAG, "after add relate data");
    }

    //被点击的小窗口的id, -1是默认值，表示不需要做任何处理
    private int clickedWindowIdex = -1;

    public void setClickedWindowIndex(int windowIndex) {
        synchronized (lockObj) {
            clickedWindowIdex = windowIndex;
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
        //首先要判断是否需要关闭当前播放的中间窗口
        if (centerVideoViewIndex >= 0 && currentPlayTime > centerStartTime + centerDuration) {
            playCtrlEventListener.onPlayCtrlCallback(OnPlayCtrlEventListener.STOP_CTRL, 0, centerVideoViewIndex);
            centerVideoViewIndex = -1;
        }

        //通知去掉遮罩
        maskViewListener.setMaskViewStatus(OnMaskViewListener.ACTION_MASK_GONE, dataBean.getObjId() - 1, dataBean.getId());


        int windowIndex = dataBean.getObjId() - 1;
        //中间播放窗口和当前轮询到的id不一致，那么通知界面播放这个id
        if (centerVideoViewIndex == -1 && dataBean.getScale() == 2) {
            //这里是有隐藏的bug，如果中间的因为某种原因导致播放失败，那需要继续播放，
            centerVideoViewIndex = windowIndex;
            centerStartTime = dataBean.getStartTime() * 1000;
            centerDuration = dataBean.getDuration() * 1000;
            Log.d(TAG, "change play id:"+windowIndex + " startTime:"+centerStartTime+" duration:"+centerDuration + " currentPlayTime:"+currentPlayTime);
            playCtrlEventListener.onPlayCtrlCallback(OnPlayCtrlEventListener.PLAY_CTRL, currentPlayTime+3000, windowIndex);
        }

        int relateId;
        int relateType = dataBean.getRelevanceType();

        //判断是否有关联视频
        if (relateType != 0) {
            if (isRelateDataInLst(dataBean)) {
                return;
            }
            relateId = dataBean.getRelevanceVideoId() - 1;
            addRelateDataToLst(dataBean);
            if (relateType == OnPlayCtrlEventListener.RELATIVE_VERTICAL) {
                playCtrlEventListener.onPlayRelateVideos(OnPlayCtrlEventListener.PLAY_RELATE_VERTICAL_CTRL,
                        windowIndex, relateId, dataBean.getStartTime(), dataBean.getDuration());
                Log.d(TAG, "will play relate video. playId: "+windowIndex + " relateId:"+relateId);
                Log.d(TAG, "0 before wait");
                relateVideoWait();
                Log.d(TAG, "0 after wait");
            } else if (relateType == OnPlayCtrlEventListener.RELATIVE_HORIZON) {
                addRelateDataToLst(dataBean);
                playCtrlEventListener.onPlayRelateVideos(OnPlayCtrlEventListener.PLAY_RELATE_HORIZON_CTRL,
                        windowIndex, relateId, dataBean.getStartTime(), dataBean.getDuration());
                Log.d(TAG, "will play relate video. playId: "+windowIndex + " relateId:"+relateId);
//                Log.d(TAG, "1 before wait");
//                relateVideoWait();
//                Log.d(TAG, "1 after wait");
            }
        }
    }

    private void chapterProc(TimeLineInfo.DataBean dataBean) {
        if (btnStateListener == null || dataBean == null)
            return;

        btnStateListener.onChapterBtnTextUpdate(dataBean.getChapter().getCode());
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
            if (dataBean.getObjId() >= 0)
                btnStateListener.onWordStateChange(OnBtnStateListener.XSL_WORD_BTN_STATE,
                                                enable, dataBean.getObjId());
        }
    }

    //上一次通知更新字幕时间，每次间隔1s更新一次
    private int lastSubtitleUpdateTime = 0;
    //字幕通知
    private void subTitleNotify() {
        if (abs(currentPlayTime - lastSubtitleUpdateTime) > 1000) {
            playCtrlEventListener.onSubtitleUpdate(currentPlayTime);
            lastSubtitleUpdateTime = currentPlayTime;
        }
    }

    //当前中间播放的是第几个小窗口的视频
    int centerVideoViewIndex = -1;

    //中间视频起始播放时间在时间线上的时间，单位毫秒
    int centerStartTime = 0;
    int centerDuration = 0;

    private void itemProc() {
        int type;
        boolean enableApplience = false;
        boolean enableAction = false;
        boolean enableWord = false;
        boolean enableChapter = false;
        TimeLineInfo timeLineInfo = MainPlayerActivity.getTimelineInfo();

        for (TimeLineInfo.DataBean dataBean : timeLineInfo.getData()) {
            if (dataBean.getStartTime() * 1000 <= currentPlayTime
                    && (dataBean.getStartTime() + dataBean.getDuration()) * 1000 > currentPlayTime) {
                type = dataBean.getType();
                //这个循环里面有任何一个需要激活的按钮我们都把他激活，
                //就算激活多次也无所谓(除了关联视频），如果轮询完成，有的按钮没有被激活
                //那么enableXXX变量肯定是false,在for循环之后关闭即可
                if (type == DataType.XSL_VIDEO) {
                    //关联视频以及中间视频播放处理
                    if (!bSeeking) {
                        videoProc(dataBean);
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
            } else if (dataBean.getType() == DataType.XSL_VIDEO &&
                    (dataBean.getStartTime() + dataBean.getDuration() + 3) * 1000 < currentPlayTime &&
                    (dataBean.getStartTime() + dataBean.getDuration() + 4) * 1000 > currentPlayTime &&
                    dataBean.getRelevanceVideoId() > 0) {
                    //处理被关禁闭的关联视频，在关联视频结束时间之后3-4s之间，将关联视频取消禁闭
                    //还有就是可以通过UI，用户手动取消禁闭
                    removeRelateDataInLst();
                    Log.d(TAG, "will close id:"+dataBean.getId() + " ");
                    //通知关闭UI上面的关联按钮
                    playCtrlEventListener.onRelateUIClose(OnPlayCtrlEventListener.PLAY_RELATE_CLOSE_UI,
                            dataBean.getObjId() - 1, dataBean.getRelevanceVideoId() - 1);
            } else if(dataBean.getType() == DataType.XSL_VIDEO && currentPlayTime > (dataBean.getStartTime() + dataBean.getDuration()) * 1000 &&
                        currentPlayTime < (dataBean.getStartTime() + dataBean.getDuration() + 1) * 1000) {
                //播放完一秒内加上遮罩
                maskViewListener.setMaskViewStatus(OnMaskViewListener.ACTION_MASK_VISIABLE, dataBean.getObjId() - 1, dataBean.getId());
            }

            //当前时间还没轮询到开始，直接跳出循环
            if (dataBean.getStartTime() * 1000 > currentPlayTime)
                break;
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
        int syncVideoTime = 0;
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

            // bSeeking 和currentPlayTime必须加锁
            synchronized (lockObj) {
                if (bSeeking) {
                    Log.d(TAG, "seeking now, not cycle time");
                    continue;
                } else {
                    //中间窗口有视频播放，那么开始计时
                    if (centerVideoViewIndex != -1 && lst.get(12).getState() == IPlayer.STATE_STARTED) {
                        currentPlayTime = lst.get(12).getCurrentPosition();
                        //Log.d(TAG, "play currentPlayTime: " + currentPlayTime);
                        //通知更新进度条
                        playCtrlEventListener.onPlayTimeCallback(OnPlayCtrlEventListener.PLAY_TIME_SET_CTRL, totalDuration, currentPlayTime);
                        int subTime = lst.get(centerVideoViewIndex).getCurrentPosition() - lst.get(12).getCurrentPosition();
                        //简单的同步，快速seek多次，这里存在bug
                        if (subTime > 1000) {
                            for (int i = 0; i != 12; i++) {
                                lst.get(i).pause();
                            }
                        } else if (subTime >= -1000 && subTime <= 1000){
                            for (int i = 0; i != 12; i++) {
                                lst.get(i).resume();
                            }
                        } else if (subTime < -1000) {
                            seekTo_(currentPlayTime);
                        }
                    } else {
                        Log.d(TAG, "-1");
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
